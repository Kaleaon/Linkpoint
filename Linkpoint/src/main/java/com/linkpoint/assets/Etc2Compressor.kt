package com.linkpoint.assets

import android.opengl.ETC1
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * CPU-side ETC2/EAC compressor for [LinkpointTexture].
 *
 * Uses two implementations under the hood:
 *
 *  - **RGB path** ([Etc1AsEtc2Rgb]): wraps Android's
 *    `android.opengl.ETC1` encoder. ETC2_RGB8 is a strict superset of
 *    ETC1: every valid ETC1 4×4 block is a valid ETC2_RGB8 block (the
 *    GPU decoder selects the matching mode). So we encode with ETC1
 *    and tag the output as `ETC2_RGB` so Filament uploads it via
 *    `Texture.InternalFormat.ETC2_RGB8`. No quality loss vs ETC1; we
 *    just gain access to the modern compressed-texture format.
 *
 *  - **RGBA path** ([Etc1PlusEacRgba]): runs the ETC1 RGB encoder for
 *    the colour channels, runs [EacAlphaEncoder] for the alpha
 *    channel, and interleaves them into ETC2_EAC_RGBA8 layout — 8
 *    bytes alpha + 8 bytes RGB per 4×4 block, alpha first per the
 *    Khronos spec (ARB_ES3_compatibility, "ETC2_EAC_RGBA8" layout).
 *    Standards-compliant; decoded by every GPU that advertises
 *    ETC2_EAC_RGBA8 (every GLES 3.0+ device, which is every Android
 *    device Linkpoint targets).
 *
 * Quality is moderate (per-block exhaustive search inside
 * EacAlphaEncoder; ETC1 inherits Android's encoder quality). When that
 * becomes a bottleneck, swap in `etcpak` via the JNI scaffold below
 * — same calling contract.
 */
interface Etc2Compressor {
    /**
     * Compress an RGBA8 buffer to a GPU-uploadable compressed format.
     *
     * @param rgba    source pixel data, exactly `width * height * 4` bytes,
     *                row-major, no padding.
     * @param width   pixel width, must be a multiple of 4.
     * @param height  pixel height, must be a multiple of 4.
     * @param hasAlpha whether the source uses the alpha channel meaningfully.
     */
    fun compress(rgba: ByteArray, width: Int, height: Int, hasAlpha: Boolean): Result?

    data class Result(
        val data: ByteArray,
        val format: GpuFormat,
        val width: Int,
        val height: Int,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Result) return false
            return format == other.format && width == other.width &&
                height == other.height && data.contentEquals(other.data)
        }
        override fun hashCode(): Int {
            var h = format.hashCode()
            h = 31 * h + width
            h = 31 * h + height
            h = 31 * h + data.contentHashCode()
            return h
        }
    }

    enum class GpuFormat {
        ETC1_RGB,        // raw ETC1 (kept for diagnostic / tests)
        ETC2_RGB,        // bit-identical to ETC1; tag-only difference for Filament upload
        ETC2_EAC_RGBA,   // ETC2 RGB + EAC alpha, 16 bytes/block
    }
}

/**
 * Encode an opaque RGBA buffer as ETC2_RGB8 by deferring to Android's
 * ETC1 encoder and re-tagging. The bytes are bit-identical to ETC1
 * output; the ETC2 GPU decoder accepts them unchanged.
 */
internal object Etc1AsEtc2Rgb : Etc2Compressor {
    private const val TAG = "Etc1AsEtc2Rgb"

    override fun compress(
        rgba: ByteArray,
        width: Int,
        height: Int,
        hasAlpha: Boolean,
    ): Etc2Compressor.Result? {
        val rgbBlocks = encodeEtc1Rgb(rgba, width, height) ?: return null
        return Etc2Compressor.Result(
            data = rgbBlocks,
            format = if (hasAlpha) Etc2Compressor.GpuFormat.ETC2_RGB
                     else Etc2Compressor.GpuFormat.ETC2_RGB,
            width = width,
            height = height,
        )
    }

    /**
     * Run Android's ETC1 encoder over an RGBA buffer (alpha discarded
     * by the deinterleave step). Returns null on validation failure.
     */
    fun encodeEtc1Rgb(rgba: ByteArray, width: Int, height: Int): ByteArray? {
        if (width <= 0 || height <= 0 || width % 4 != 0 || height % 4 != 0) {
            Log.w(TAG, "ETC1 requires multiple-of-4 dimensions; got ${width}x$height")
            return null
        }
        val expected = width * height * 4
        if (rgba.size != expected) {
            Log.w(TAG, "rgba size ${rgba.size} != expected $expected for ${width}x$height")
            return null
        }
        // ETC1.encodeImage takes RGB (3 bytes/px); deinterleave straight
        // into the direct buffer instead of going through a heap copy.
        val rgbBytes = width * height * 3
        val src = ByteBuffer.allocateDirect(rgbBytes).order(ByteOrder.nativeOrder())
        var i = 0
        while (i < rgba.size) {
            src.put(rgba[i]).put(rgba[i + 1]).put(rgba[i + 2])
            i += 4
        }
        src.position(0)

        val compressedSize = ETC1.getEncodedDataSize(width, height)
        val dst = ByteBuffer.allocateDirect(compressedSize).order(ByteOrder.nativeOrder())
        ETC1.encodeImage(src, width, height, 3, width * 3, dst)
        val out = ByteArray(compressedSize)
        // Cast through java.nio.Buffer to dodge the JDK 9 covariant-return
        // mismatch (Android's java.nio.Buffer.position(int) returns
        // Buffer, not the covariant ByteBuffer).
        dst.position(0)
        dst.get(out)
        return out
    }
}

/**
 * Encode an RGBA buffer as ETC2_EAC_RGBA8 — alpha block (8 bytes) +
 * RGB block (8 bytes) per 4×4 pixel tile, alpha-first per the spec.
 * Each component is encoded independently (ETC1 for RGB, EAC for
 * alpha) and then interleaved.
 */
internal object Etc1PlusEacRgba : Etc2Compressor {
    private const val TAG = "Etc1PlusEacRgba"

    override fun compress(
        rgba: ByteArray,
        width: Int,
        height: Int,
        hasAlpha: Boolean,
    ): Etc2Compressor.Result? {
        if (!hasAlpha) {
            // Caller knows the texture is opaque; route to the ETC2_RGB
            // path so the output is half the size.
            return Etc1AsEtc2Rgb.compress(rgba, width, height, hasAlpha = false)
        }
        if (width <= 0 || height <= 0 || width % 4 != 0 || height % 4 != 0) {
            Log.w(TAG, "ETC2_EAC requires multiple-of-4 dimensions; got ${width}x$height")
            return null
        }
        val expected = width * height * 4
        if (rgba.size != expected) {
            Log.w(TAG, "rgba size ${rgba.size} != expected $expected for ${width}x$height")
            return null
        }

        // 1. Encode the RGB channels via ETC1 (same path as Etc1AsEtc2Rgb).
        val rgbBlocks = Etc1AsEtc2Rgb.encodeEtc1Rgb(rgba, width, height) ?: return null

        // 2. Extract the alpha plane and encode it via EAC.
        val alphaPlane = ByteArray(width * height)
        var i = 0
        var j = 0
        while (i < rgba.size) {
            alphaPlane[j++] = rgba[i + 3]
            i += 4
        }
        val alphaBlocks = EacAlphaEncoder.encode(alphaPlane, width, height)

        // 3. Interleave: ETC2_EAC_RGBA8 layout puts alpha (8 bytes) FIRST
        //    in each 16-byte block, then RGB (8 bytes). Both block streams
        //    are in the same raster order so we just zip them.
        val blockCount = (width / 4) * (height / 4)
        val expectedRgbSize = blockCount * 8
        val expectedAlphaSize = blockCount * 8
        if (rgbBlocks.size != expectedRgbSize || alphaBlocks.size != expectedAlphaSize) {
            Log.w(TAG, "block size mismatch: rgb=${rgbBlocks.size}/${expectedRgbSize}, " +
                "alpha=${alphaBlocks.size}/${expectedAlphaSize}")
            return null
        }
        val out = ByteArray(blockCount * 16)
        for (b in 0 until blockCount) {
            System.arraycopy(alphaBlocks, b * 8, out, b * 16, 8)
            System.arraycopy(rgbBlocks, b * 8, out, b * 16 + 8, 8)
        }
        return Etc2Compressor.Result(
            data = out,
            format = Etc2Compressor.GpuFormat.ETC2_EAC_RGBA,
            width = width,
            height = height,
        )
    }
}

/**
 * JNI scaffold for an `etcpak`-backed encoder. Currently always
 * returns "not available" because the native entry point is gated on
 * `LINKPOINT_HAVE_ETCPAK` in `cpp/CMakeLists.txt`, which is only
 * defined when the etcpak source is vendored. The Kotlin
 * [Etc1AsEtc2Rgb] / [Etc1PlusEacRgba] paths above produce
 * standards-compliant output without it; this hook stays so a future
 * commit can drop in the SIMD-accelerated implementation by simply
 * adding the etcpak source files and the CMake define — no Kotlin
 * changes required.
 */
internal object NativeEtcpak : Etc2Compressor {
    @Volatile private var probed = false
    @Volatile private var available = false

    fun isAvailable(): Boolean {
        if (!probed) probe()
        return available
    }

    @Synchronized private fun probe() {
        if (probed) return
        probed = true
        available = try {
            // Same .so as the J2K decoder; loading is idempotent.
            System.loadLibrary("linkpoint-j2k")
            nativeHasEtcpak()
        } catch (_: UnsatisfiedLinkError) {
            false
        } catch (_: Throwable) {
            false
        }
    }

    override fun compress(
        rgba: ByteArray,
        width: Int,
        height: Int,
        hasAlpha: Boolean,
    ): Etc2Compressor.Result? {
        if (!isAvailable()) return null
        if (width <= 0 || height <= 0 || width % 4 != 0 || height % 4 != 0) return null
        val expected = width * height * 4
        if (rgba.size != expected) return null

        val out = nativeCompressEtc2Rgba(rgba, width, height, hasAlpha) ?: return null
        return Etc2Compressor.Result(
            data = out,
            format = if (hasAlpha) Etc2Compressor.GpuFormat.ETC2_EAC_RGBA
                     else Etc2Compressor.GpuFormat.ETC2_RGB,
            width = width,
            height = height,
        )
    }

    @JvmStatic private external fun nativeHasEtcpak(): Boolean
    @JvmStatic private external fun nativeCompressEtc2Rgba(
        rgba: ByteArray,
        width: Int,
        height: Int,
        hasAlpha: Boolean,
    ): ByteArray?
}

/**
 * Picks the best available compressor at call time. Order:
 *   1. `etcpak` JNI bridge, when vendored (best quality, fastest).
 *   2. Pure-Kotlin ETC1+EAC path: standards-compliant and works on
 *      every device. Routes opaque inputs to the half-size ETC2_RGB
 *      path automatically.
 *
 * The previous fallback (`Etc1Fallback`, opaque-only, ETC1-tagged) is
 * gone — it dropped alpha silently, which the [LinkpointTexture]
 * upload path treated as "fall through to RGBA8". That worked but
 * defeated the whole point of compression on alpha textures.
 */
object Etc2CompressorFactory {
    fun get(): Etc2Compressor =
        if (NativeEtcpak.isAvailable()) NativeEtcpak else Etc1PlusEacRgba
}
