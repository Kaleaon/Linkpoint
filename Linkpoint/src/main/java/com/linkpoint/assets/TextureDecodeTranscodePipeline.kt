package com.linkpoint.assets

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.util.UUID

/**
 * Shared decode/transcode pipeline:
 *  - J2K decode via linkpoint-j2k ([JPEG2000Decoder]) with optional etcpak ETC2 path.
 *  - Basis/KTX2 transcode path when a Basis transcoder is available.
 *
 * Both backends should consume [Output] so format, mip policy, and sRGB/linear
 * conventions come from one place.
 */
object TextureDecodeTranscodePipeline {

    data class Request(
        val backend: TextureFormatPolicy.Backend,
        val semantic: TextureFormatPolicy.TextureSemantic,
        val capabilities: TextureFormatPolicy.DeviceCapabilities,
    )

    data class Output(
        val uuid: UUID,
        val width: Int,
        val height: Int,
        val rgba: ByteArray?,
        val compressed: Etc2Compressor.Result?,
        val decision: TextureFormatPolicy.Decision,
    )

    fun fromJ2k(uuid: UUID, j2kBytes: ByteArray, request: Request): Output? {
        val bitmap = JPEG2000Decoder.decode(j2kBytes) ?: return null
        return fromBitmap(uuid, bitmap, request)
    }

    fun fromBasisKtx2(uuid: UUID, basisKtx2Bytes: ByteArray, request: Request): Output? {
        if (!request.capabilities.supportsBasisTranscoding) return null
        val rgba = BasisTranscoder.tryTranscodeToRgba32(basisKtx2Bytes) ?: return null
        val dims = BasisTranscoder.tryReadDimensions(basisKtx2Bytes) ?: return null
        val decision = TextureFormatPolicy.decide(
            backend = request.backend,
            semantic = request.semantic,
            width = dims.first,
            height = dims.second,
            capabilities = request.capabilities
        )
        val compressed = if (decision.targetFormat == TextureFormatPolicy.TargetFormat.ETC2_RGBA) {
            Etc2CompressorFactory.get().compress(rgba, dims.first, dims.second, hasAlpha = true)
        } else {
            null
        }
        return Output(uuid, dims.first, dims.second, rgba, compressed, decision)
    }

    fun fromBitmap(uuid: UUID, bitmap: Bitmap, request: Request): Output {
        val width = bitmap.width
        val height = bitmap.height
        val rgba = ByteArray(width * height * 4)
        bitmap.copyPixelsToBuffer(ByteBuffer.wrap(rgba))
        if (!bitmap.isRecycled) bitmap.recycle()

        val decision = TextureFormatPolicy.decide(
            backend = request.backend,
            semantic = request.semantic,
            width = width,
            height = height,
            capabilities = request.capabilities
        )
        val compressed = if (decision.targetFormat == TextureFormatPolicy.TargetFormat.ETC2_RGBA) {
            Etc2CompressorFactory.get().compress(rgba, width, height, hasAlpha = true)
        } else {
            null
        }
        return Output(uuid, width, height, rgba, compressed, decision)
    }
}

/**
 * Basis transcoder shim. The native Basis Universal bridge has not been
 * vendored or wired into CMake, so callers naturally fall back to the J2K
 * pipeline. To enable real transcoding, vendor basis_universal, restore the
 * basis_transcoder_jni.cpp / openjpeg_basis_integration.cpp sources, list
 * them in src/main/cpp/CMakeLists.txt, and reintroduce the JNI bindings.
 */
internal object BasisTranscoder {
    fun tryReadDimensions(ktx2Data: ByteArray): Pair<Int, Int>? = null
    fun tryTranscodeToRgba32(ktx2Data: ByteArray): ByteArray? = null
}
