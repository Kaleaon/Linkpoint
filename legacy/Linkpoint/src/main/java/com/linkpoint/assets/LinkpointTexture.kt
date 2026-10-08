package com.linkpoint.assets

import android.graphics.Bitmap
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import java.lang.ref.PhantomReference
import java.lang.ref.ReferenceQueue
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Unified texture lifecycle: J2K bytes (or cache hit) → optional ETC2
 * compression → GPU upload → release.
 *
 * Mirrors the role of Lumiya's `OpenJPEG` class but corrects the lifecycle
 * pattern: this is `AutoCloseable`, NOT finaliser-driven. Lumiya's
 * `finalize()` released the native buffer non-deterministically, so the
 * decompiled tree showed VRAM exhaustion under churn even though the
 * "owning" reference had long since gone out of scope. We do not copy that.
 *
 * Cleanup strategy (in order):
 *   1. The caller calls [close] once it has uploaded the texture and no
 *      longer needs the CPU-side buffer. This is the primary path.
 *   2. A [Cleaner] registration acts purely as a leak detector — if the
 *      handle is GC'd while still open, [Cleaner] logs a warning and frees
 *      the native/mmap accounting. The Filament `Texture` is NOT released
 *      from the Cleaner: Filament resources must be freed on the Filament
 *      thread, and we cannot guarantee that from a Cleaner thread. Leaking
 *      a Filament `Texture` is bad, but crashing the Filament engine is
 *      worse — the warning is what triggers the fix at the call site.
 */
class LinkpointTexture private constructor(
    val uuid: UUID,
    val width: Int,
    val height: Int,
    private val source: Source,
    private val semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO,
) : AutoCloseable {

    enum class Source { J2K_DECODE, CACHE_HIT_MMAP, CACHE_HIT_HEAP }

    private val closed = AtomicBoolean(false)

    /** Decoded RGBA pixels, owned by this texture. Null after [close]. */
    @Volatile private var rgba: ByteArray? = null

    /** Compressed payload + format, set by [compressEtc2]. Null if not compressed. */
    @Volatile private var compressed: Etc2Compressor.Result? = null

    /** Live mmap handle, if this texture was served from the cache. */
    @Volatile private var cacheHandle: MmappedTextureCache.CachedTexture? = null

    /**
     * Filament `Texture` produced by [uploadToFilament]. Held here only so
     * [releaseFilamentTexture] can run on the render thread; [close] does
     * NOT touch this field, because the JDK [Cleaner] thread can invoke
     * close-paths and Filament resources MUST be freed on the engine
     * thread (see class kdoc).
     */
    @Volatile private var filamentTexture: Texture? = null

    /** Bytes accounted against [TextureMemoryTracker.allocGpu]; 0 when not on GPU. */
    @Volatile private var gpuBytes: Long = 0L

    /**
     * Bytes accounted against [TextureMemoryTracker] on the native-heap line.
     * Held in a separate object so the [Cleaner] callback can read it without
     * capturing a reference to the outer [LinkpointTexture] (which would
     * defeat the GC-driven leak detection).
     */
    private val nativeBytesRef = LongHolder()
    private val cleanerRegistration: Cleanable =
        CLEANER.register(this, CleanupAction(uuid, nativeBytesRef, closed))

    init {
        TextureMemoryTracker.textureOpened()
    }

    private fun setRgbaAccounted(bytes: ByteArray) {
        rgba = bytes
        nativeBytesRef.value = bytes.size.toLong()
        TextureMemoryTracker.allocNative(bytes.size.toLong())
    }

    fun rgbaBytes(): ByteArray? = rgba

    fun compressedPayload(): Etc2Compressor.Result? = compressed

    fun compressEtc2(hasAlpha: Boolean): Etc2Compressor.Result? {
        check(!closed.get()) { "LinkpointTexture[$uuid] is closed" }
        val src = rgba ?: return null
        val result = Etc2CompressorFactory.get().compress(src, width, height, hasAlpha)
        compressed = result
        return result
    }

    fun uploadToFilament(engine: Engine): Texture? {
        check(!closed.get()) { "LinkpointTexture[$uuid] is closed" }

        filamentTexture?.let { return it }

        val compressedResult = compressed
        val compressedBlob = compressedResult?.data
        val canUseCompressed = compressedResult != null &&
            compressedBlob != null && compressedBlob.isNotEmpty() &&
            (compressedResult.format == Etc2Compressor.GpuFormat.ETC2_RGB ||
             compressedResult.format == Etc2Compressor.GpuFormat.ETC2_EAC_RGBA)

        return try {
            if (canUseCompressed) {
                uploadCompressed(engine, compressedResult!!, compressedBlob!!)
            } else {
                uploadRgba8(engine)
            }
        } catch (e: Exception) {
            Log.w(TAG, "uploadToFilament[$uuid] failed: ${e.message}", e)
            null
        }
    }

    private fun uploadRgba8(engine: Engine): Texture? {
        val pixels = rgba ?: run {
            Log.w(TAG, "uploadToFilament[$uuid]: no RGBA buffer (already released?)")
            return null
        }

        val direct = ByteBuffer.allocateDirect(pixels.size).order(ByteOrder.nativeOrder())
        direct.put(pixels)
        direct.flip()

        val tex = Texture.Builder()
            .width(width).height(height)
            .levels(TextureFormatPolicy.mipLevelsFor(width, height))
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(
                if (semantic == TextureFormatPolicy.TextureSemantic.ALBEDO) {
                    Texture.InternalFormat.SRGB8_A8
                } else {
                    Texture.InternalFormat.RGBA8
                }
            )
            .build(engine)

        val descriptor = Texture.PixelBufferDescriptor(
            direct, Texture.Format.RGBA, Texture.Type.UBYTE
        )
        tex.setImage(engine, 0, descriptor)
        tex.generateMipmaps(engine)

        filamentTexture = tex
        val approxBytes = (width.toLong() * height.toLong() * 4L * 4L) / 3L
        gpuBytes = approxBytes
        TextureMemoryTracker.allocGpu(approxBytes)
        return tex
    }

    private fun uploadCompressed(
        engine: Engine,
        result: Etc2Compressor.Result,
        blob: ByteArray
    ): Texture? {
        val internalFormat = when (result.format) {
            Etc2Compressor.GpuFormat.ETC2_RGB -> Texture.InternalFormat.ETC2_RGB8
            Etc2Compressor.GpuFormat.ETC2_EAC_RGBA -> Texture.InternalFormat.ETC2_EAC_RGBA8
            else -> return null
        }
        val pixelFormat = when (result.format) {
            Etc2Compressor.GpuFormat.ETC2_RGB -> Texture.CompressedFormat.ETC2_RGB8
            Etc2Compressor.GpuFormat.ETC2_EAC_RGBA -> Texture.CompressedFormat.ETC2_EAC_RGBA8
            else -> return null
        }

        val direct = ByteBuffer.allocateDirect(blob.size).order(ByteOrder.nativeOrder())
        direct.put(blob)
        direct.flip()

        val tex = Texture.Builder()
            .width(result.width).height(result.height)
            .levels(1)
            .sampler(Texture.Sampler.SAMPLER_2D)
            .format(internalFormat)
            .build(engine)

        val descriptor = Texture.PixelBufferDescriptor(direct, pixelFormat, blob.size)
        tex.setImage(engine, 0, descriptor)

        filamentTexture = tex
        gpuBytes = blob.size.toLong()
        TextureMemoryTracker.allocGpu(blob.size.toLong())
        return tex
    }

    fun releaseFilamentTexture(engine: Engine) {
        val tex = filamentTexture ?: return
        try {
            engine.destroyTexture(tex)
        } catch (e: Exception) {
            Log.w(TAG, "destroyTexture[$uuid] threw: ${e.message}")
        }
        filamentTexture = null
        if (gpuBytes > 0) {
            TextureMemoryTracker.freeGpu(gpuBytes)
            gpuBytes = 0L
        }
    }

    override fun close() {
        if (!closed.compareAndSet(false, true)) return
        rgba = null
        compressed = null
        cacheHandle?.close()
        cacheHandle = null
        val bytes = nativeBytesRef.value
        if (bytes > 0) TextureMemoryTracker.freeNative(bytes)
        TextureMemoryTracker.textureClosed()
        cleanerRegistration.clean()
    }

    private class LongHolder(@Volatile var value: Long = 0)

    private class CleanupAction(
        private val uuid: UUID,
        private val nativeBytesRef: LongHolder,
        private val closedFlag: AtomicBoolean,
    ) : Runnable {
        override fun run() {
            if (closedFlag.get()) return
            Log.w(TAG, "LinkpointTexture[$uuid] leaked — close() was not called")
            val bytes = nativeBytesRef.value
            if (bytes > 0) TextureMemoryTracker.freeNative(bytes)
            TextureMemoryTracker.textureClosed()
        }
    }

    interface Cleanable {
        fun clean()
    }

    private class SimpleCleaner {
        private val queue = ReferenceQueue<Any>()
        private val phantomRefs = ConcurrentHashMap.newKeySet<PhantomCleanable>()

        init {
            val thread = Thread {
                while (!Thread.currentThread().isInterrupted) {
                    try {
                        val ref = queue.remove() as? PhantomCleanable
                        if (ref != null) {
                            phantomRefs.remove(ref)
                            ref.action.run()
                        }
                    } catch (_: InterruptedException) {
                        break
                    } catch (e: Exception) {
                        Log.w(TAG, "Cleaner thread error: ${e.message}")
                    }
                }
            }
            thread.isDaemon = true
            thread.name = "LinkpointTexture-Cleaner"
            thread.start()
        }

        fun register(obj: Any, action: Runnable): Cleanable {
            val ref = PhantomCleanable(obj, queue, action)
            phantomRefs.add(ref)
            return ref
        }

        private inner class PhantomCleanable(
            referent: Any,
            q: ReferenceQueue<Any>,
            val action: Runnable
        ) : PhantomReference<Any>(referent, q), Cleanable {
            override fun clean() {
                if (phantomRefs.remove(this)) {
                    clear()
                }
            }
        }
    }

    companion object {
        private const val TAG = "LinkpointTexture"
        private val CLEANER = SimpleCleaner()

        const val MAX_DECODED_PIXELS = 2048 * 2048
        fun fromJ2k(uuid: UUID, j2kBytes: ByteArray): LinkpointTexture? {
            val size = JPEG2000Decoder.getImageSize(j2kBytes)
            if (size != null && size.first.toLong() * size.second > MAX_DECODED_PIXELS) {
                Log.w(TAG, "refusing oversized J2K $uuid: ${size.first}x${size.second}")
                return null
            }
            val bitmap = JPEG2000Decoder.decode(j2kBytes) ?: return null
            return fromBitmap(uuid, bitmap, source = Source.J2K_DECODE)
        }

        fun fromBitmap(
            uuid: UUID,
            bitmap: Bitmap,
            source: Source = Source.J2K_DECODE,
            semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO
        ): LinkpointTexture {
            val width = bitmap.width
            val height = bitmap.height
            val rgba = ByteArray(width * height * 4)
            val buf = ByteBuffer.wrap(rgba)
            bitmap.copyPixelsToBuffer(buf)
            if (!bitmap.isRecycled) bitmap.recycle()
            val tex = LinkpointTexture(uuid, width, height, source, semantic)
            tex.setRgbaAccounted(rgba)
            return tex
        }

        fun fromCache(
            uuid: UUID,
            handle: MmappedTextureCache.CachedTexture,
            semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO
        ): LinkpointTexture {
            val source = if (handle.mmapped) Source.CACHE_HIT_MMAP else Source.CACHE_HIT_HEAP
            val tex = LinkpointTexture(uuid, handle.width, handle.height, source, semantic)
            tex.cacheHandle = handle
            tex.compressed = Etc2Compressor.Result(
                data = ByteArray(0),
                format = handle.format,
                width = handle.width,
                height = handle.height,
            )
            return tex
        }
    }
}
