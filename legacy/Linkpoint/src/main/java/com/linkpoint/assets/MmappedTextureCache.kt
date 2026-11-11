package com.linkpoint.assets

import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * On-disk cache of decoded + GPU-compressed textures, served via mmap.
 *
 * Implements the safety constraints from docs/lumiya-port/README.md item 4:
 *
 *  - Files are immutable once written. Writes go to `<uuid>.etc2.tmp`, are
 *    fsync'd, then atomically renamed. There is no overwrite path.
 *  - Eviction goes through a refcount, not a direct `File.delete()`. The
 *    cache marks a UUID as "evictable"; the actual unlink runs only when the
 *    refcount returned by [acquire]/[release] drops to zero. This avoids the
 *    SIGBUS-on-truncate class of crash that a naive `delete()` would cause
 *    against any in-flight `MappedByteBuffer`.
 *  - Concurrent maps are bounded ([MAX_CONCURRENT_MAPS]) to keep virtual
 *    address space from being exhausted on 32-bit ABIs. Hitting the cap
 *    falls back to a non-mmapped read into a heap [ByteBuffer], which is
 *    slower but does not consume VA space.
 *  - File size is validated against the on-disk header before mapping; a
 *    truncated file is deleted and a cache miss is reported.
 *
 * This class is intentionally a pure cache layer — it does not know about
 * Filament. Callers (typically [LinkpointTexture]) are responsible for
 * uploading the returned buffer to the GPU.
 */
class MmappedTextureCache(private val rootDir: File) {

    companion object {
        private const val TAG = "MmappedTextureCache"

        // Conservative cap — tune per-device once we have telemetry.
        // 256 mapped textures × ~256KiB each ≈ 64MiB of VA, well under the
        // ~1GiB practical ceiling on a 32-bit ABI.
        private const val MAX_CONCURRENT_MAPS = 256

        // Layout: 16-byte little-endian header followed by payload.
        //   bytes  0..3  : magic 'L','P','T','2'
        //   bytes  4..5  : format ordinal ([Etc2Compressor.GpuFormat])
        //   bytes  6..7  : reserved (0)
        //   bytes  8..11 : width
        //   bytes 12..15 : height
        // Then payload.length bytes of compressed pixel data.
        private const val HEADER_SIZE = 16
        private val MAGIC = byteArrayOf('L'.code.toByte(), 'P'.code.toByte(), 'T'.code.toByte(), '2'.code.toByte())
    }

    init {
        if (!rootDir.exists() && !rootDir.mkdirs()) {
            Log.w(TAG, "could not create $rootDir; cache will report misses")
        }
    }

    private val activeMaps = AtomicInteger(0)
    private val handles = ConcurrentHashMap<UUID, Handle>()

    /**
     * Atomic, fsync'd write of a freshly compressed texture into the cache.
     *
     * Returns `false` if the underlying directory is missing/unwritable. Does
     * not throw on transient I/O errors — caching is an optimisation.
     */
    fun put(
        uuid: UUID,
        format: Etc2Compressor.GpuFormat,
        width: Int,
        height: Int,
        payload: ByteArray
    ): Boolean {
        val finalFile = fileFor(uuid)
        val tmpFile = File(finalFile.parentFile, "${finalFile.name}.tmp")
        try {
            RandomAccessFile(tmpFile, "rw").use { raf ->
                val header = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.LITTLE_ENDIAN)
                header.put(MAGIC)
                header.putShort(format.ordinal.toShort())
                header.putShort(0) // reserved
                header.putInt(width)
                header.putInt(height)
                raf.write(header.array())
                raf.write(payload)
                raf.fd.sync()
            }
            // Atomic on POSIX filesystems used by Android internal storage.
            if (!tmpFile.renameTo(finalFile)) {
                Log.w(TAG, "rename failed for $finalFile")
                tmpFile.delete()
                return false
            }
            return true
        } catch (e: Throwable) {
            Log.w(TAG, "put failed for $uuid", e)
            tmpFile.delete()
            return false
        }
    }

    /**
     * Acquire a refcounted handle to a cached texture.
     *
     * The returned [CachedTexture] MUST be closed by the caller — releasing
     * the last reference is what permits eviction to actually unlink the file.
     */
    fun acquire(uuid: UUID): CachedTexture? {
        val handle = handles.computeIfAbsent(uuid) { Handle(uuid) }
        return handle.acquire()
    }

    /**
     * Mark a UUID as evictable. The file is not deleted until the last live
     * handle is released. Returns true if the UUID was tracked.
     */
    fun markEvictable(uuid: UUID): Boolean {
        val handle = handles[uuid] ?: return false
        handle.markEvictable()
        return true
    }

    private fun fileFor(uuid: UUID): File = File(rootDir, "$uuid.etc2")

    internal inner class Handle(val uuid: UUID) {
        private val refCount = AtomicInteger(0)
        @Volatile private var evictable = false

        // The mapped/heap buffer is opened once per [Handle] and shared
        // across concurrent acquires — opening it twice would double-count
        // [activeMaps] and waste virtual address space on the same file.
        @Volatile private var sharedPayload: SharedPayload? = null

        fun acquire(): CachedTexture? {
            val file = fileFor(uuid)
            if (!file.exists()) return null
            refCount.incrementAndGet()
            return try {
                acquireShared(file)?.let { payload ->
                    CachedTexture(this, payload)
                } ?: run {
                    release()
                    null
                }
            } catch (e: Throwable) {
                Log.w(TAG, "acquire failed for $uuid", e)
                release()
                null
            }
        }

        fun markEvictable() {
            evictable = true
            tryUnlink()
        }

        fun release() {
            val after = refCount.decrementAndGet()
            if (after < 0) Log.w(TAG, "refCount went negative for $uuid")
            if (after == 0 && evictable) tryUnlink()
        }

        private fun tryUnlink() {
            if (refCount.get() != 0) return
            // Drop the mmap accounting before deleting the backing file —
            // any future acquire would re-open and re-account.
            sharedPayload?.let { releaseSharedAccounting(it) }
            sharedPayload = null
            val file = fileFor(uuid)
            if (file.exists() && !file.delete()) {
                Log.w(TAG, "delete failed for $file")
            }
            handles.remove(uuid, this)
        }

        @Synchronized
        private fun acquireShared(file: File): SharedPayload? {
            sharedPayload?.let { return it }
            val payload = openPayload(file) ?: return null
            if (payload.mmapped) {
                activeMaps.incrementAndGet()
                TextureMemoryTracker.allocMmapped(payload.bytes)
            }
            sharedPayload = payload
            return payload
        }

        private fun openPayload(file: File): SharedPayload? {
            val length = file.length()
            if (length < HEADER_SIZE) {
                Log.w(TAG, "truncated cache file ${file.name} (size=$length); deleting")
                file.delete()
                return null
            }
            return RandomAccessFile(file, "r").use { raf ->
                val header = parseHeader(raf, file) ?: return@use null
                val payloadSize = (length - HEADER_SIZE).toInt()
                if (payloadSize <= 0) {
                    Log.w(TAG, "empty payload in ${file.name}; deleting")
                    file.delete()
                    return@use null
                }
                val mapped = tryMap(raf.channel, payloadSize)
                val buffer = mapped?.asReadOnlyBuffer()
                    ?: readHeapPayload(raf.channel, payloadSize, file)?.asReadOnlyBuffer()
                    ?: return@use null
                SharedPayload(
                    format = header.format,
                    width = header.width,
                    height = header.height,
                    buffer = buffer,
                    mmapped = mapped != null,
                    bytes = payloadSize.toLong(),
                )
            }
        }

        private fun parseHeader(raf: RandomAccessFile, file: File): Header? {
            val bytes = ByteArray(HEADER_SIZE)
            raf.readFully(bytes)
            if (!bytes.startsWith(MAGIC)) {
                Log.w(TAG, "bad magic in ${file.name}; deleting")
                file.delete()
                return null
            }
            val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            buf.position(4)
            val formatOrd = buf.short.toInt() and 0xFFFF
            buf.short // reserved
            val width = buf.int
            val height = buf.int
            val format = Etc2Compressor.GpuFormat.values().getOrNull(formatOrd) ?: run {
                Log.w(TAG, "unknown format $formatOrd in ${file.name}; deleting")
                file.delete()
                return null
            }
            return Header(format, width, height)
        }

        private fun readHeapPayload(channel: FileChannel, payloadSize: Int, file: File): ByteBuffer? {
            val heap = ByteBuffer.allocate(payloadSize)
            channel.position(HEADER_SIZE.toLong())
            var read = 0
            while (read < payloadSize) {
                val n = channel.read(heap)
                if (n < 0) break
                read += n
            }
            if (read != payloadSize) {
                Log.w(TAG, "short read for ${file.name}: $read of $payloadSize")
                return null
            }
            heap.flip()
            return heap
        }

        private fun tryMap(channel: FileChannel, payloadSize: Int): MappedByteBuffer? {
            if (activeMaps.get() >= MAX_CONCURRENT_MAPS) return null
            return try {
                channel.map(FileChannel.MapMode.READ_ONLY, HEADER_SIZE.toLong(), payloadSize.toLong())
            } catch (e: FileNotFoundException) {
                null
            } catch (e: Throwable) {
                Log.w(TAG, "mmap failed; falling back to heap read", e)
                null
            }
        }

        private fun releaseSharedAccounting(payload: SharedPayload) {
            if (payload.mmapped) {
                activeMaps.decrementAndGet()
                TextureMemoryTracker.freeMmapped(payload.bytes)
            }
        }

        internal fun onClose() {
            release()
        }
    }

    private data class Header(
        val format: Etc2Compressor.GpuFormat,
        val width: Int,
        val height: Int,
    )

    /** Single backing buffer + accounting for one cache-file open. */
    internal data class SharedPayload(
        val format: Etc2Compressor.GpuFormat,
        val width: Int,
        val height: Int,
        val buffer: ByteBuffer,
        val mmapped: Boolean,
        val bytes: Long,
    )

    /**
     * Refcounted handle to a cached texture. Closing this decrements the
     * refcount that gates eviction; the underlying buffer is shared across
     * concurrent acquires and only released when the last handle drops.
     */
    class CachedTexture internal constructor(
        private val handle: Handle,
        private val payload: SharedPayload,
    ) : AutoCloseable {
        val format: Etc2Compressor.GpuFormat get() = payload.format
        val width: Int get() = payload.width
        val height: Int get() = payload.height
        val buffer: ByteBuffer get() = payload.buffer.duplicate()
        val mmapped: Boolean get() = payload.mmapped

        private var closed = false
        override fun close() {
            if (closed) return
            closed = true
            handle.onClose()
        }
    }
}

private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
    if (size < prefix.size) return false
    for (i in prefix.indices) if (this[i] != prefix[i]) return false
    return true
}
