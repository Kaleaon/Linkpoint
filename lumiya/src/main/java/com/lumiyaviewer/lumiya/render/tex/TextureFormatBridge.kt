package com.lumiyaviewer.lumiya.render.tex

import android.content.Context
import android.util.Log
import com.lumiyaviewer.lumiya.render.ModernTextureManager
import java.io.IOException
import java.io.InputStream

/**
 * Integration bridge between legacy JPEG2000 texture system and modern KTX2/Basis Universal system
 * This class provides a migration path and fallback mechanisms
 */
class TextureFormatBridge {
    private val TAG = "TextureFormatBridge"
    
    private var modernManager: ModernTextureManager? = null
    private var modernManagerInitialized = false
    
    /**
     * Initialize the texture format bridge
     */
    fun initialize(context: Context) {
        try {
            modernManager = ModernTextureManager(context)
            modernManagerInitialized = true
            Log.i(TAG, "Modern texture manager initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize modern texture manager, falling back to legacy system", e)
            modernManagerInitialized = false
        }
    }
    
    /**
     * Check if modern texture system is available
     */
    fun isModernTextureSystemAvailable(): Boolean {
        return modernManagerInitialized && modernManager != null
    }
    
    /**
     * Detect texture format from stream header
     */
    @Throws(IOException::class)
    fun detectTextureFormat(stream: InputStream): TextureFormat {
        // Read first few bytes to detect format
        if (!stream.markSupported()) {
            return TextureFormat.JPEG2000 // Fallback
        }
        
        stream.mark(16)
        val header = ByteArray(12)
        val bytesRead = stream.read(header)
        stream.reset()
        
        if (bytesRead >= 12) {
            // Check for KTX2 magic bytes
            if (header[0] == 0xAB.toByte() && header[1] == 0x4B.toByte() && header[2] == 0x54.toByte() && header[3] == 0x58.toByte() &&
                header[4] == 0x20.toByte() && header[5] == 0x32.toByte() && header[6] == 0x30.toByte() && header[7] == 0xBB.toByte() &&
                header[8] == 0x0D.toByte() && header[9] == 0x0A.toByte() && header[10] == 0x1A.toByte() && header[11] == 0x0A.toByte()) {
                return TextureFormat.KTX2
            }
            
            // Check for JPEG2000 magic bytes
            if (header[0] == 0x00.toByte() && header[1] == 0x00.toByte() && header[2] == 0x00.toByte() && header[3] == 0x0C.toByte() &&
                header[4] == 0x6A.toByte() && header[5] == 0x50.toByte() && header[6] == 0x20.toByte() && header[7] == 0x20.toByte()) {
                return TextureFormat.JPEG2000
            }
        }
        
        // Default to JPEG2000 for compatibility
        return TextureFormat.JPEG2000
    }
    
    /**
     * Load texture using appropriate system based on format
     */
    @Throws(IOException::class)
    fun loadTexture(stream: InputStream): TextureData {
        val format = detectTextureFormat(stream)
        
        return when (format) {
            TextureFormat.KTX2 -> loadKTX2Texture(stream)
            TextureFormat.JPEG2000 -> loadJPEG2000Texture(stream)
        }
    }
    
    @Throws(IOException::class)
    private fun loadKTX2Texture(stream: InputStream): TextureData {
        if (!isModernTextureSystemAvailable()) {
            throw IOException("Modern texture system not available")
        }
        
        val modernData = modernManager!!.loadKTX2Texture(stream)
        
        // Convert to unified TextureData format
        return TextureData(
            modernData.width,
            modernData.height,
            modernData.data,
            TextureFormat.KTX2,
            modernData.getOpenGLFormat(),
            modernData.isCompressed()
        )
    }
    
    @Throws(IOException::class)
    private fun loadJPEG2000Texture(stream: InputStream): TextureData {
        throw IOException("JPEG2000 texture loading not implemented in bridge")
    }
    
    /**
     * Texture format enumeration
     */
    enum class TextureFormat {
        JPEG2000,
        KTX2
    }
    
    /**
     * Unified texture data structure
     */
    data class TextureData(
        val width: Int,
        val height: Int,
        val data: ByteArray,
        val format: TextureFormat,
        val openGLFormat: Int,
        val compressed: Boolean
    ) {
        override fun toString(): String {
            return String.format("TextureData[%dx%d, %s, %s, %d bytes]",
                    width, height, format, if (compressed) "compressed" else "uncompressed", data.size)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TextureData

            if (width != other.width) return false
            if (height != other.height) return false
            if (!data.contentEquals(other.data)) return false
            if (format != other.format) return false
            if (openGLFormat != other.openGLFormat) return false
            if (compressed != other.compressed) return false

            return true
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + data.contentHashCode()
            result = 31 * result + format.hashCode()
            result = 31 * result + openGLFormat
            result = 31 * result + compressed.hashCode()
            return result
        }
    }
}
