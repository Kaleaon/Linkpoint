package com.linkpoint.texture

/**
 * OpenJPEG - Native bridge to OpenJPEG library for JPEG2000 decoding
 * 
 * Second Life uses JPEG2000 (J2K/J2C) for texture compression.
 * This provides a native interface to the OpenJPEG library.
 * 
 * Native library must be loaded: libopenjpeg.so
 */
object OpenJPEG {
    
    data class ImageInfo(
        val width: Int,
        val height: Int,
        val components: Int,  // 3 for RGB, 4 for RGBA
        val bitDepth: Int
    )
    
    data class DecodeResult(
        val success: Boolean,
        val rgba: ByteArray?,
        val width: Int,
        val height: Int,
        val error: String = ""
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            
            other as DecodeResult
            
            if (success != other.success) return false
            if (rgba != null) {
                if (other.rgba == null) return false
                if (!rgba.contentEquals(other.rgba)) return false
            } else if (other.rgba != null) return false
            if (width != other.width) return false
            if (height != other.height) return false
            if (error != other.error) return false
            
            return true
        }
        
        override fun hashCode(): Int {
            var result = success.hashCode()
            result = 31 * result + (rgba?.contentHashCode() ?: 0)
            result = 31 * result + width
            result = 31 * result + height
            result = 31 * result + error.hashCode()
            return result
        }
    }
    
    init {
        try {
            System.loadLibrary("openjpeg")
            nativeInit()
        } catch (e: UnsatisfiedLinkError) {
            com.linkpoint.Debug.Log("OpenJPEG native library not found. Using fallback decoder.")
        }
    }
    
    /**
     * Initialize native library
     */
    private external fun nativeInit()
    
    /**
     * Decode JPEG2000 data to RGBA
     * @param j2kData JPEG2000 compressed data
     * @return RGBA pixel data (8-bit per channel)
     */
    external fun decode(j2kData: ByteArray): ByteArray?
    
    /**
     * Decode with detailed result
     */
    external fun decodeWithInfo(j2kData: ByteArray): DecodeResult
    
    /**
     * Get image information without full decode
     */
    external fun getImageInfo(j2kData: ByteArray): ImageInfo?
    
    /**
     * Encode RGBA to JPEG2000
     */
    external fun encode(
        rgba: ByteArray,
        width: Int,
        height: Int,
        quality: Int = 50  // 1-100
    ): ByteArray?
    
    /**
     * Check if native library is available
     */
    fun isNativeAvailable(): Boolean {
        return try {
            System.loadLibrary("openjpeg")
            true
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }
    
    /**
     * Fallback decoder (slow, for when native is not available)
     * Uses Android's built-in image decoder if possible
     */
    fun decodeFallback(j2kData: ByteArray): DecodeResult {
        return try {
            // Try using Android BitmapFactory
            val options = android.graphics.BitmapFactory.Options()
            options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888
            
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(j2kData, 0, j2kData.size, options)
            
            if (bitmap != null) {
                // Convert bitmap to RGBA byte array
                val width = bitmap.width
                val height = bitmap.height
                val rgba = ByteArray(width * height * 4)
                
                val pixels = IntArray(width * height)
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                
                for (i in pixels.indices) {
                    val pixel = pixels[i]
                    rgba[i * 4 + 0] = ((pixel shr 16) and 0xFF).toByte()  // R
                    rgba[i * 4 + 1] = ((pixel shr 8) and 0xFF).toByte()   // G
                    rgba[i * 4 + 2] = (pixel and 0xFF).toByte()           // B
                    rgba[i * 4 + 3] = ((pixel shr 24) and 0xFF).toByte()  // A
                }
                
                bitmap.recycle()
                
                DecodeResult(
                    success = true,
                    rgba = rgba,
                    width = width,
                    height = height
                )
            } else {
                DecodeResult(
                    success = false,
                    rgba = null,
                    width = 0,
                    height = 0,
                    error = "BitmapFactory failed to decode"
                )
            }
        } catch (e: Exception) {
            DecodeResult(
                success = false,
                rgba = null,
                width = 0,
                height = 0,
                error = "Fallback decode failed: ${e.message}"
            )
        }
    }
}
