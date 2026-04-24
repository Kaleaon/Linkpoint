package com.linkpoint.assets

import android.graphics.Bitmap
import android.util.Log
import java.lang.ref.Cleaner
import java.nio.ByteBuffer
import java.util.UUID
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
 *
 * This is a scaffold. It does not yet:
 *   - Fold the J2K decoder body in (still calls [JPEG2000Decoder.decode])
 *   - Wire the [MmappedTextureCache] hit/miss path (the cache exists but
 *     callers are not yet invoking it)
 *   - Implement [uploadToFilament] (TODO — needs Filament `Engine` plumbing
 *     from `render/RenderManager.kt`)
 *   - Hook ETC2/EAC via etcpak (gated on the native entry point; falls back
 *     to the ETC1 path with the documented opaque-only restriction)
 */
class LinkpointTexture private constructor(
    val uuid: UUID,
    val width: Int,
    val height: Int,
    private val source: Source,
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
     * Bytes accounted against [TextureMemoryTracker] on the native-heap line.
     * Held in a separate object so the [Cleaner] callback can read it without
     * capturing a reference to the outer [LinkpointTexture] (which would
     * defeat the GC-driven leak detection).
     */
    private val nativeBytesRef = LongHolder()
    private val cleanerRegistration: Cleaner.Cleanable =
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

    /**
     * Compress the held RGBA buffer to ETC2/EAC (or fall back to ETC1 for
     * opaque inputs if etcpak is not yet linked). Returns the result, also
     * stashed on this object for [uploadToFilament].
     *
     * Caller is responsible for deciding whether the texture has alpha — we
     * do not infer it from the pixel data, because scanning every pixel on
     * every load is exactly the kind of per-frame cost we are trying to cut.
     * SL's `TextureEntry` carries the alpha flag.
     */
    fun compressEtc2(hasAlpha: Boolean): Etc2Compressor.Result? {
        check(!closed.get()) { "LinkpointTexture[$uuid] is closed" }
        val src = rgba ?: return null
        val result = Etc2CompressorFactory.get().compress(src, width, height, hasAlpha)
        compressed = result
        return result
    }

    /**
     * Upload the compressed payload (or RGBA, if no compression succeeded)
     * to Filament. Not yet implemented — see class kdoc.
     */
    fun uploadToFilament(/* engine: com.google.android.filament.Engine */): Any? {
        check(!closed.get()) { "LinkpointTexture[$uuid] is closed" }
        // TODO(item 1): build Texture via Texture.Builder, pick format from
        //   compressed.format if non-null else Texture.InternalFormat.RGBA8,
        //   call setImage with PixelBufferDescriptor. Account against
        //   TextureMemoryTracker.allocGpu / freeGpu.
        Log.w(TAG, "uploadToFilament not yet implemented (uuid=$uuid)")
        return null
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
        // Cancel the Cleaner so its leak-warning path doesn't fire.
        cleanerRegistration.clean()
    }

    /**
     * Cleaner-side cleanup. Runs on a JDK Cleaner thread, NOT the caller's
     * thread, so it intentionally does not touch Filament. It frees the
     * native-heap accounting and logs the leak so the call-site bug gets a
     * real fix.
     */
    private class LongHolder(@Volatile var value: Long = 0)

    private class CleanupAction(
        private val uuid: UUID,
        private val nativeBytesRef: LongHolder,
        private val closedFlag: AtomicBoolean,
    ) : Runnable {
        override fun run() {
            if (closedFlag.get()) return // close() already ran cleanly
            Log.w(TAG, "LinkpointTexture[$uuid] leaked — close() was not called")
            val bytes = nativeBytesRef.value
            if (bytes > 0) TextureMemoryTracker.freeNative(bytes)
            TextureMemoryTracker.textureClosed()
        }
    }

    companion object {
        private const val TAG = "LinkpointTexture"
        private val CLEANER: Cleaner = Cleaner.create()

        /**
         * Decode J2K bytes into a new texture. Calls into [JPEG2000Decoder]
         * for now; will route through `TextureManager.decodeTexture` once the
         * pipeline is wired up so the existing decode-memory budget, retry
         * loop, and per-texture error tracking apply (see
         * docs/lumiya-port/README.md item 1, and `TextureManager.kt`).
         *
         * Until then we apply a coarse pixel-count guard so a malformed or
         * adversarial J2K cannot OOM the process via this path. SL textures
         * are capped at 1024×1024 in practice.
         */
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

        /**
         * Wrap a freshly decoded [Bitmap]. Copies the pixels out to a
         * caller-owned RGBA byte array and recycles the bitmap, because
         * holding a `Bitmap` here would put the texture's lifetime under
         * Android's bitmap pool rather than ours.
         */
        fun fromBitmap(
            uuid: UUID,
            bitmap: Bitmap,
            source: Source = Source.J2K_DECODE
        ): LinkpointTexture {
            val width = bitmap.width
            val height = bitmap.height
            val rgba = ByteArray(width * height * 4)
            val buf = ByteBuffer.wrap(rgba)
            bitmap.copyPixelsToBuffer(buf)
            if (!bitmap.isRecycled) bitmap.recycle()
            val tex = LinkpointTexture(uuid, width, height, source)
            tex.setRgbaAccounted(rgba)
            return tex
        }

        /**
         * Wrap an existing [MmappedTextureCache.CachedTexture]. The texture
         * takes ownership of the cache handle; closing the texture closes
         * the handle.
         */
        fun fromCache(
            uuid: UUID,
            handle: MmappedTextureCache.CachedTexture
        ): LinkpointTexture {
            val source = if (handle.mmapped) Source.CACHE_HIT_MMAP else Source.CACHE_HIT_HEAP
            val tex = LinkpointTexture(uuid, handle.width, handle.height, source)
            tex.cacheHandle = handle
            tex.compressed = Etc2Compressor.Result(
                data = ByteArray(0), // payload lives in handle.buffer
                format = handle.format,
                width = handle.width,
                height = handle.height,
            )
            return tex
        }
    }
}
