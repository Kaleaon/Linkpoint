package com.linkpoint.assets

import android.opengl.ETC1
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * CPU-side ETC2/EAC compressor for [LinkpointTexture].
 *
 * The target encoder is `etcpak` compiled into `liblinkpoint-j2k.so` (see
 * docs/lumiya-port/README.md item 2 + item 3). Until that lands, the
 * [Etc1Fallback] implementation below uses the AOSP `android.opengl.ETC1`
 * encoder for opaque inputs only — it exists to prove the upload path
 * end-to-end and MUST NOT be enabled as the default once any alpha textures
 * flow through `LinkpointTexture`, because dropping alpha silently is worse
 * than uncompressed RGBA.
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
     *                Implementations MAY refuse to compress alpha inputs (the
     *                ETC1 fallback does); callers must check the returned
     *                [Result.format].
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
        ETC1_RGB,        // android.opengl.ETC1, opaque only — fallback path
        ETC2_RGB,        // not yet wired
        ETC2_EAC_RGBA,   // target — implemented by NativeEtcpak once etcpak is in CMake
    }
}

/**
 * Temporary fallback that lets the upload path be exercised before etcpak
 * lands. Refuses alpha inputs to make the asymmetry loud at the call site.
 */
internal object Etc1Fallback : Etc2Compressor {
    private const val TAG = "Etc1Fallback"

    override fun compress(
        rgba: ByteArray,
        width: Int,
        height: Int,
        hasAlpha: Boolean
    ): Etc2Compressor.Result? {
        if (hasAlpha) {
            Log.w(TAG, "ETC1 fallback cannot compress alpha textures (${width}x$height); skipping")
            return null
        }
        if (width <= 0 || height <= 0 || width % 4 != 0 || height % 4 != 0) return null
        val expected = width * height * 4
        if (rgba.size != expected) {
            Log.w(TAG, "rgba size ${rgba.size} != expected $expected for ${width}x$height")
            return null
        }

        // ETC1.encodeImage takes RGB (3 bytes/px); deinterleave straight into
        // the direct buffer instead of going through a heap intermediate.
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
        dst.position(0).get(out)
        return Etc2Compressor.Result(
            data = out,
            format = Etc2Compressor.GpuFormat.ETC1_RGB,
            width = width,
            height = height,
        )
    }
}

/**
 * Real implementation backed by `etcpak` inside `liblinkpoint-j2k.so`.
 * The native entry point does not exist yet — see CMakeLists.txt TODO.
 * Calling [compress] today will throw [UnsatisfiedLinkError]; callers should
 * gate on availability via [isAvailable].
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
        hasAlpha: Boolean
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
 * Picks the best available compressor at call time. Prefer etcpak when the
 * native entry point is wired up; fall back to ETC1 otherwise.
 */
object Etc2CompressorFactory {
    fun get(): Etc2Compressor =
        if (NativeEtcpak.isAvailable()) NativeEtcpak else Etc1Fallback
}
