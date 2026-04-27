package com.linkpoint.assets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.linkpoint.network.NetworkLogger
import java.lang.reflect.Method
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * JPEG2000 (J2K/JP2) decoder using OpenJPEG native library.
 *
 * Includes runtime diagnostics and an optional JP2ForAndroid fallback (reflection based)
 * to avoid silent null texture degradation when native JNI isn't available.
 */
object JPEG2000Decoder {

    private const val TAG = "JPEG2000Decoder"

    @Volatile private var nativeLoaded = false
    @Volatile private var nativeHealthCheckPassed = false
    @Volatile private var nativeError: String? = null
    @Volatile private var nativeHealthError: String? = null
    private var startupStatus: DecoderStartupStatus? = null

    private var jp2DecoderCtor: java.lang.reflect.Constructor<*>? = null
    private var jp2DecodeMethod: Method? = null

    init {
        initializeNativeDecoder()
        initializeOptionalJp2ForAndroid()
    }

    private fun initializeNativeDecoder() {
        try {
            System.loadLibrary("linkpoint-j2k")
            nativeLoaded = true
            nativeHealthCheckPassed = runNativeHealthCheck()
            if (nativeHealthCheckPassed) {
                Log.i(TAG, "JPEG2000 native decoder loaded and healthy")
            } else {
                Log.w(TAG, "JPEG2000 native decoder loaded but failed health-check")
            }
        } catch (e: UnsatisfiedLinkError) {
            nativeError = e.message
            Log.w(TAG, "Native JPEG2000 decoder unavailable: ${e.message}")
        }
    }

    private fun runNativeHealthCheck(): Boolean {
        return try {
            val healthy = nativeHealthCheck()
            if (!healthy) {
                nativeHealthError = "nativeHealthCheck returned false"
            }
            healthy
        } catch (e: Throwable) {
            nativeHealthError = "${e.javaClass.simpleName}: ${e.message}"
            false
        }
    }

    private fun initializeOptionalJp2ForAndroid() {
        try {
            val decoderClass = Class.forName("com.gemalto.jp2.JP2Decoder")
            jp2DecoderCtor = decoderClass.getConstructor(ByteArray::class.java)
            jp2DecodeMethod = decoderClass.getMethod("decode")
            Log.i(TAG, "JP2ForAndroid fallback available")
        } catch (_: Exception) {
            // Optional dependency, ignore when absent.
        }
    }

    fun isNativeAvailable(): Boolean = nativeLoaded && nativeHealthCheckPassed

    fun getStartupStatus(): DecoderStartupStatus = startupStatus ?: runStartupSelfTest()

    fun runStartupSelfTest(): DecoderStartupStatus {
        val hasReflectionFallback = jp2DecoderCtor != null && jp2DecodeMethod != null
        val availability = when {
            nativeLoaded && nativeHealthCheckPassed -> "native"
            hasReflectionFallback -> "jp2forandroid"
            else -> "none"
        }
        val warning = if (availability == "none") {
            "JPEG2000 decoding unavailable. Second Life textures may use placeholders."
        } else null

        val status = DecoderStartupStatus(
            available = availability != "none",
            activeBackend = availability,
            nativeLoaded = nativeLoaded,
            nativeHealthy = nativeHealthCheckPassed,
            jp2ForAndroidAvailable = hasReflectionFallback,
            nativeError = nativeError,
            nativeHealthError = nativeHealthError,
            warningMessage = warning
        )
        startupStatus = status

        val level = if (status.available) NetworkLogger.Level.INFO else NetworkLogger.Level.WARN
        NetworkLogger.log(level, NetworkLogger.Category.TEXTURE, "JPEG2000 startup self-test: $status")
        warning?.let { Log.w(TAG, it) }

        return status
    }

    fun decode(data: ByteArray): Bitmap? {
        if (data.isEmpty()) return null

        val discardPlan = buildDiscardPlan(0)
        if (isNativeAvailable()) {
            for (discard in discardPlan) {
                decodeNativeWithDiscard(data, discard)?.let { return it }
            }
            Log.w(TAG, "Native decode failed for discard plan=$discardPlan, trying JP2ForAndroid fallback")
        }

        decodeViaJp2ForAndroid(data)?.let { return it }
        return decodeFallback(data)
    }

    fun decode(data: ByteArray, discardLevel: Int): Bitmap? {
        if (data.isEmpty()) return null

        val discardPlan = buildDiscardPlan(discardLevel)
        if (isNativeAvailable()) {
            for (discard in discardPlan) {
                decodeNativeWithDiscard(data, discard)?.let { return it }
            }
            Log.w(TAG, "Native decode failed for discard plan=$discardPlan, trying JP2ForAndroid fallback")
        }

        decodeViaJp2ForAndroid(data)?.let { return it }
        return decodeFallback(data)
    }

    fun getImageSize(data: ByteArray): Pair<Int, Int>? {
        if (data.isEmpty()) return null

        return if (isNativeAvailable()) {
            nativeGetImageSize(data) ?: parseJ2KHeader(data)
        } else {
            parseJ2KHeader(data)
        }
    }

    internal fun buildDiscardPlan(requestedDiscardLevel: Int, maxAdditionalFallbackDiscards: Int = 2): List<Int> {
        val base = requestedDiscardLevel.coerceIn(0, 5)
        return (0..maxAdditionalFallbackDiscards)
            .map { (base + it).coerceIn(0, 5) }
            .distinct()
    }

    private fun decodeNative(data: ByteArray): Bitmap? {
        return try {
            val result = nativeDecode(data, 0)
            if (result != null) createBitmapFromRGBA(result.pixels, result.width, result.height) else null
        } catch (e: Exception) {
            Log.e(TAG, "Native decode failed", e)
            null
        }
    }

    private fun decodeNativeWithDiscard(data: ByteArray, discardLevel: Int): Bitmap? {
        return try {
            val result = nativeDecode(data, discardLevel)
            if (result != null) createBitmapFromRGBA(result.pixels, result.width, result.height) else null
        } catch (e: Exception) {
            Log.e(TAG, "Native decode failed", e)
            null
        }
    }

    private fun decodeViaJp2ForAndroid(data: ByteArray): Bitmap? {
        val ctor = jp2DecoderCtor ?: return null
        val decodeMethod = jp2DecodeMethod ?: return null
        return try {
            val decoder = ctor.newInstance(data)
            decodeMethod.invoke(decoder) as? Bitmap
        } catch (e: Exception) {
            Log.w(TAG, "JP2ForAndroid fallback decode failed: ${e.message}")
            null
        }
    }

    private fun decodeFallback(data: ByteArray): Bitmap? {
        if (data.size >= 2 && data[0] == 0xFF.toByte() && data[1] == 0xD8.toByte()) {
            return try {
                BitmapFactory.decodeByteArray(data, 0, data.size)
            } catch (_: Exception) {
                null
            }
        }
        Log.w(TAG, "Cannot decode JPEG2000 with current runtime setup")
        return null
    }

    private fun parseJ2KHeader(data: ByteArray): Pair<Int, Int>? {
        if (data.size < 50) return null

        try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
            if (data[0] == 0x00.toByte() && data[1] == 0x00.toByte() &&
                data[2] == 0x00.toByte() && data[3] == 0x0C.toByte()) {
                var pos = 0
                while (pos < data.size - 8) {
                    buffer.position(pos)
                    val boxLen = buffer.int
                    val boxType = buffer.int
                    if (boxType == 0x69686472) {
                        val height = buffer.int
                        val width = buffer.int
                        if (width > 0 && height > 0) {
                            return Pair(width, height)
                        }
                    }

                    // Some malformed/partial JP2 payloads have zero box length;
                    // fall back to a byte-wise scan so we can still recover IHDR dimensions.
                    pos += if (boxLen >= 8) boxLen else 1
                }
            }

            if (data[0] == 0xFF.toByte() && data[1] == 0x4F.toByte()) {
                var pos = 2
                while (pos < data.size - 4) {
                    if (data[pos] == 0xFF.toByte() && data[pos + 1] == 0x51.toByte()) {
                        buffer.position(pos + 4)
                        val xsiz = buffer.int
                        val ysiz = buffer.int
                        val xosiz = buffer.int
                        val yosiz = buffer.int
                        return Pair(xsiz - xosiz, ysiz - yosiz)
                    }
                    pos++
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse J2K header", e)
        }

        return null
    }

    private fun createBitmapFromRGBA(pixels: ByteArray, width: Int, height: Int): Bitmap? {
        if (width <= 0 || height <= 0) return null
        val expected = width * height * 4
        if (pixels.size != expected) {
            Log.e(TAG, "Pixel data size mismatch: ${pixels.size} vs expected $expected")
            return null
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(pixels))
        return bitmap
    }

    private external fun nativeDecode(data: ByteArray, discardLevel: Int): DecodeResult?
    private external fun nativeGetImageSize(data: ByteArray): Pair<Int, Int>?
    private external fun nativeHealthCheck(): Boolean

    data class DecoderStartupStatus(
        val available: Boolean,
        val activeBackend: String,
        val nativeLoaded: Boolean,
        val nativeHealthy: Boolean,
        val jp2ForAndroidAvailable: Boolean,
        val nativeError: String?,
        val nativeHealthError: String?,
        val warningMessage: String?
    )

    data class DecodeResult(
        val width: Int,
        val height: Int,
        val components: Int,
        val pixels: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is DecodeResult) return false
            return width == other.width && height == other.height &&
                components == other.components && pixels.contentEquals(other.pixels)
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + components
            result = 31 * result + pixels.contentHashCode()
            return result
        }
    }
}
