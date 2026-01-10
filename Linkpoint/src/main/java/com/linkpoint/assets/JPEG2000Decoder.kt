package com.linkpoint.assets

import android.graphics.Bitmap
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * JPEG2000 (J2K/JP2) decoder using OpenJPEG native library
 * 
 * Second Life textures are encoded in JPEG2000 format which requires
 * specialized decoding. This wrapper uses the OpenJPEG library.
 */
object JPEG2000Decoder {
    
    private const val TAG = "JPEG2000Decoder"
    
    // Native library loading state
    private var nativeLoaded = false
    private var nativeError: String? = null
    
    init {
        try {
            // The openjpeg-ndk library should be loaded automatically
            // But we need our own JNI wrapper library
            System.loadLibrary("linkpoint-j2k")
            nativeLoaded = true
            Log.i(TAG, "JPEG2000 native decoder loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            nativeError = e.message
            Log.w(TAG, "Native JPEG2000 decoder not available, using fallback: ${e.message}")
        }
    }
    
    /**
     * Check if native decoding is available
     */
    fun isNativeAvailable(): Boolean = nativeLoaded
    
    /**
     * Decode JPEG2000 data to Bitmap
     */
    fun decode(data: ByteArray): Bitmap? {
        if (data.isEmpty()) return null
        
        return if (nativeLoaded) {
            decodeNative(data)
        } else {
            decodeFallback(data)
        }
    }
    
    /**
     * Decode with specified discard level (for LOD)
     * Higher discard = lower resolution = faster decode
     */
    fun decode(data: ByteArray, discardLevel: Int): Bitmap? {
        if (data.isEmpty()) return null
        
        return if (nativeLoaded) {
            decodeNativeWithDiscard(data, discardLevel)
        } else {
            decodeFallback(data)
        }
    }
    
    /**
     * Get image dimensions without full decode
     */
    fun getImageSize(data: ByteArray): Pair<Int, Int>? {
        if (data.isEmpty()) return null
        
        return if (nativeLoaded) {
            nativeGetImageSize(data)
        } else {
            // Try to parse header
            parseJ2KHeader(data)
        }
    }
    
    /**
     * Native decode implementation
     */
    private fun decodeNative(data: ByteArray): Bitmap? {
        return try {
            val result = nativeDecode(data, 0)
            if (result != null) {
                createBitmapFromRGBA(result.pixels, result.width, result.height)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Native decode failed", e)
            null
        }
    }
    
    private fun decodeNativeWithDiscard(data: ByteArray, discardLevel: Int): Bitmap? {
        return try {
            val result = nativeDecode(data, discardLevel)
            if (result != null) {
                createBitmapFromRGBA(result.pixels, result.width, result.height)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Native decode failed", e)
            null
        }
    }
    
    /**
     * Fallback decoder (very limited, handles only simple cases)
     */
    private fun decodeFallback(data: ByteArray): Bitmap? {
        // Check if it's actually a standard JPEG (some textures are)
        if (data.size >= 2 && data[0] == 0xFF.toByte() && data[1] == 0xD8.toByte()) {
            // It's a regular JPEG, use Android's decoder
            return try {
                android.graphics.BitmapFactory.decodeByteArray(data, 0, data.size)
            } catch (e: Exception) {
                null
            }
        }
        
        // For actual J2K/JP2, we can't decode without native library
        Log.w(TAG, "Cannot decode JPEG2000 without native library")
        return null
    }
    
    /**
     * Parse J2K header to get dimensions
     */
    private fun parseJ2KHeader(data: ByteArray): Pair<Int, Int>? {
        if (data.size < 50) return null
        
        try {
            val buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN)
            
            // Check for JP2 file format box
            if (data[0] == 0x00.toByte() && data[1] == 0x00.toByte() &&
                data[2] == 0x00.toByte() && data[3] == 0x0C.toByte()) {
                // JP2 format - find ihdr box
                var pos = 0
                while (pos < data.size - 8) {
                    buffer.position(pos)
                    val boxLen = buffer.int
                    val boxType = buffer.int
                    
                    if (boxType == 0x69686472) { // 'ihdr'
                        val height = buffer.int
                        val width = buffer.int
                        return Pair(width, height)
                    }
                    
                    pos += if (boxLen > 0) boxLen else 8
                    if (pos <= 0) break
                }
            }
            
            // Check for J2K codestream
            if (data[0] == 0xFF.toByte() && data[1] == 0x4F.toByte()) {
                // J2K codestream - find SIZ marker
                var pos = 2
                while (pos < data.size - 4) {
                    if (data[pos] == 0xFF.toByte() && data[pos + 1] == 0x51.toByte()) {
                        // SIZ marker found
                        buffer.position(pos + 4)
                        val xsiz = buffer.int // width
                        val ysiz = buffer.int // height
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
        if (pixels.size != width * height * 4) {
            Log.e(TAG, "Pixel data size mismatch: ${pixels.size} vs expected ${width * height * 4}")
            return null
        }
        
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val buffer = ByteBuffer.wrap(pixels)
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }
    
    // Native method declarations
    private external fun nativeDecode(data: ByteArray, discardLevel: Int): DecodeResult?
    private external fun nativeGetImageSize(data: ByteArray): Pair<Int, Int>?
    
    /**
     * Result from native decode
     */
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
