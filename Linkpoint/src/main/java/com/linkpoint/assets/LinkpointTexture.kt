package com.linkpoint.assets

import android.graphics.Bitmap
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Texture
import java.lang.ref.Cleaner
import java.nio.ByteBuffer
import java.nio.ByteOrder
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
 *   - Implement [uploadToFilament] (pending — needs Filament `Engine` plumbing
 *     from `render/RenderManager.kt`)
 *   - Hook ETC2/EAC via etcpak (gated on the native entry point; falls back
 *     to the ETC1 path with the documented opaque-only restriction)
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
     * Upload the held RGBA8 buffer to Filament as a 2D texture with
     * mipmaps. Returns the Filament `Texture` (also stashed for
     * [releaseFilamentTexture]) or null on failure (closed handle, no
     * pixels, engine refused).
     *
     * MUST be called on the Filament render thread — Filament `Engine`
     * methods are not threadsafe. The companion call site,
     * `RenderManager.uploadTerrainDetailTexture`, follows the same
     * constraint and is the template for this implementation.
     *
     * Format selection:
     *   - If [compressed] holds an ETC2/EAC payload (etcpak path),
     *     uploads the compressed blob via the matching Filament
     *     `Texture.InternalFormat`. ETC1 fallback is opaque-only and
     *     cannot be consumed by Filament's `Texture.setImage` directly
     *     (Filament only accepts the OpenGL-compatible compressed
     *     formats listed in `Texture.InternalFormat.*COMPRESSED*`), so
     *     for ETC1 we ignore the compressed buffer and fall through to
     *     the RGBA8 path on [rgba].
     *   - Otherwise uploads RGBA8.
     *
     * VRAM accounting estimates the GPU footprint as `width*height*4`
     * for RGBA8 (worst-case for the renderer's working set). For ETC2
     * we use the compressed payload size, since that's what the GPU
     * driver will actually allocate.
     */
    fun uploadToFilament(engine: Engine): Texture? {
        check(!closed.get()) { "LinkpointTexture[$uuid] is closed" }

        // Already uploaded — return the existing handle so callers can
        // call this idempotently from material rebinds.
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
        val approxBytes = (width.toLong() * height.toLong() * 4L * 4L) / 3L // mip pyramid ≈ 1.33×
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
            .levels(1) // ETC2 mipmap chains require pre-built levels; skip until etcpak emits them
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

    /**
     * Release the Filament `Texture` produced by [uploadToFilament]. MUST
     * be called on the render thread. Decoupled from [close] so callers
     * can manage lifetime: the engine resource lives until the renderer
     * unbinds the texture from all materials, while the CPU-side RGBA
     * buffer can be freed earlier.
     */
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

        /**
         * Wrap an existing [MmappedTextureCache.CachedTexture]. The texture
         * takes ownership of the cache handle; closing the texture closes
         * the handle.
         */
        fun fromCache(
            uuid: UUID,
            handle: MmappedTextureCache.CachedTexture,
            semantic: TextureFormatPolicy.TextureSemantic = TextureFormatPolicy.TextureSemantic.ALBEDO
        ): LinkpointTexture {
            val source = if (handle.mmapped) Source.CACHE_HIT_MMAP else Source.CACHE_HIT_HEAP
            val tex = LinkpointTexture(uuid, handle.width, handle.height, source, semantic)
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
