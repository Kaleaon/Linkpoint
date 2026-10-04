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
        if (j2kBytes.isEmpty()) return null
        val bitmap = JPEG2000Decoder.decode(j2kBytes) ?: JPEG2000Decoder.createPlaceholderBitmap(128, 128)
        return fromBitmap(uuid, bitmap, request)
    }

    fun fromBasisKtx2(uuid: UUID, basisKtx2Bytes: ByteArray, request: Request): Output? {
        if (basisKtx2Bytes.isEmpty()) return null
        var rgba: ByteArray? = null
        var dims: Pair<Int, Int>? = null

        if (request.capabilities.supportsBasisTranscoding) {
            rgba = BasisTranscoder.tryTranscodeToRgba32(basisKtx2Bytes)
            dims = BasisTranscoder.tryReadDimensions(basisKtx2Bytes)
        }

        if (rgba != null && dims != null) {
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

        // Fallback: use parsed dimensions or 128x128 synthetic grid placeholder
        val fallbackDims = dims ?: BasisTranscoder.tryReadDimensions(basisKtx2Bytes) ?: Pair(128, 128)
        val placeholderBitmap = JPEG2000Decoder.createPlaceholderBitmap(fallbackDims.first, fallbackDims.second)
        return fromBitmap(uuid, placeholderBitmap, request)
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
 * Basis Universal transcoder bridge.
 * Uses native C++ Basis Universal transcoder linked into liblinkpoint-j2k.so.
 */
internal object BasisTranscoder {
    private const val TAG = "BasisTranscoder"

    @Volatile
    private var libraryLoaded = false

    init {
        try {
            System.loadLibrary("linkpoint-j2k")
            nativeInit()
            libraryLoaded = true
            android.util.Log.i(TAG, "KTX2 transcoder initialized successfully")
        } catch (e: UnsatisfiedLinkError) {
            android.util.Log.w(TAG, "linkpoint-j2k native library not available for Basis: ${e.message}")
        } catch (e: Throwable) {
            android.util.Log.w(TAG, "Failed to initialize BasisTranscoder: ${e.message}")
        }
    }

    fun isAvailable(): Boolean = libraryLoaded

    fun tryReadDimensions(ktx2Data: ByteArray): Pair<Int, Int>? {
        if (!libraryLoaded || ktx2Data.isEmpty()) return null
        return try {
            nativeGetDimensions(ktx2Data)
        } catch (e: Throwable) {
            android.util.Log.w(TAG, "Failed to read KTX2 dimensions: ${e.message}")
            null
        }
    }

    fun tryTranscodeToRgba32(ktx2Data: ByteArray): ByteArray? {
        if (!libraryLoaded || ktx2Data.isEmpty()) return null
        return try {
            nativeTranscodeToRgba32(ktx2Data)
        } catch (e: Throwable) {
            android.util.Log.w(TAG, "Failed to transcode KTX2 to RGBA32: ${e.message}")
            null
        }
    }

    fun tryTranscodeToEtc2(ktx2Data: ByteArray): ByteArray? {
        if (!libraryLoaded || ktx2Data.isEmpty()) return null
        return try {
            nativeTranscodeToEtc2(ktx2Data)
        } catch (e: Throwable) {
            android.util.Log.w(TAG, "Failed to transcode KTX2 to ETC2: ${e.message}")
            null
        }
    }

    fun tryTranscodeToAstc(ktx2Data: ByteArray): ByteArray? {
        if (!libraryLoaded || ktx2Data.isEmpty()) return null
        return try {
            nativeTranscodeToAstc(ktx2Data)
        } catch (e: Throwable) {
            android.util.Log.w(TAG, "Failed to transcode KTX2 to ASTC: ${e.message}")
            null
        }
    }

    fun tryTranscode(ktx2Data: ByteArray, targetFormat: Int): ByteArray? {
        if (!libraryLoaded || ktx2Data.isEmpty()) return null
        return try {
            nativeTranscode(ktx2Data, targetFormat)
        } catch (e: Throwable) {
            android.util.Log.w(TAG, "Failed to transcode KTX2 with format $targetFormat: ${e.message}")
            null
        }
    }

    @JvmStatic
    private external fun nativeInit(): Boolean

    @JvmStatic
    private external fun nativeGetDimensions(ktx2Data: ByteArray): Pair<Int, Int>?

    @JvmStatic
    private external fun nativeTranscodeToRgba32(ktx2Data: ByteArray): ByteArray?

    @JvmStatic
    private external fun nativeTranscodeToEtc2(ktx2Data: ByteArray): ByteArray?

    @JvmStatic
    private external fun nativeTranscodeToAstc(ktx2Data: ByteArray): ByteArray?

    @JvmStatic
    private external fun nativeTranscode(ktx2Data: ByteArray, targetFormat: Int): ByteArray?
}
