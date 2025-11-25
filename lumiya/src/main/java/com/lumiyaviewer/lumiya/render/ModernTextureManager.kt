package com.lumiyaviewer.lumiya.render

import android.content.Context
import android.opengl.GLES20
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Modern texture manager that uses Basis Universal transcoder for efficient
 * texture compression and GPU format optimization.
 * 
 * This replaces the legacy JPEG2000-based texture system with a modern
 * GPU-native approach using KTX2 container format and runtime transcoding.
 */
class ModernTextureManager(private val context: Context) {
    companion object {
        private const val TAG = "ModernTextureManager"
        
        // Texture format constants matching JNI implementation
        const val FORMAT_ASTC_4x4_RGBA = 0
        const val FORMAT_ETC2_RGBA = 1
        const val FORMAT_BC7_RGBA = 2
        const val FORMAT_RGBA32 = 3
    }
    
    // GPU capability flags
    private var supportsASTC: Boolean = false
    private var supportsETC2: Boolean = false
    private var supportsBC7: Boolean = false
    
    // Instance state
    private var initialized: Boolean = false
    
    // Native library loading
    init {
        try {
            System.loadLibrary("basis_transcoder")
            Log.i(TAG, "Basis transcoder native library loaded successfully")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load basis transcoder native library", e)
            throw RuntimeException("Critical: Native library not available", e)
        }
    }
    
    // Native method declarations
    private external fun nativeInit(): Boolean
    private external fun nativeCreateTranscoder(): Long
    private external fun nativeInitTranscoder(handle: Long, ktx2Data: ByteArray): Boolean
    private external fun nativeGetTextureDimensions(handle: Long): IntArray
    private external fun nativeTranscodeTexture(handle: Long, targetFormat: Int, level: Int): ByteArray
    private external fun nativeDestroyTranscoder(handle: Long)
    
    init {
        // Initialize the transcoder
        try {
            if (!nativeInit()) {
                Log.e(TAG, "Failed to initialize native transcoder")
                throw RuntimeException("Native transcoder initialization failed")
            }
            
            // Detect GPU capabilities
            detectGPUCapabilities()
            
            Log.i(TAG, "ModernTextureManager initialized with GPU capabilities:")
            Log.i(TAG, "  ASTC support: " + supportsASTC)
            Log.i(TAG, "  ETC2 support: " + supportsETC2)
            Log.i(TAG, "  BC7 support: " + supportsBC7)
            
            initialized = true
            
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Native library not available", e)
            throw RuntimeException("Native library loading failed", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during initialization", e)
            throw RuntimeException("ModernTextureManager initialization failed", e)
        }
    }
    
    /**
     * Detect GPU texture format capabilities
     */
    private fun detectGPUCapabilities() {
        val extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS)
        if (extensions != null) {
            supportsASTC = extensions.contains("GL_KHR_texture_compression_astc_ldr")
            supportsETC2 = extensions.contains("GL_OES_compressed_ETC2_RGB8_texture") ||
                          extensions.contains("GL_ARB_ES3_compatibility")
            supportsBC7 = extensions.contains("GL_EXT_texture_compression_bptc")
        }
    }
    
    /**
     * Check if the texture manager is properly initialized
     */
    fun isInitialized(): Boolean {
        return initialized
    }
    
    /**
     * Get optimal texture format for this device
     */
    fun getOptimalTextureFormat(): Int {
        if (supportsASTC) {
            return FORMAT_ASTC_4x4_RGBA
        } else if (supportsETC2) {
            return FORMAT_ETC2_RGBA
        } else if (supportsBC7) {
            return FORMAT_BC7_RGBA
        } else {
            return FORMAT_RGBA32
        }
    }
    
    /**
     * Get format name for debugging
     */
    fun getFormatName(format: Int): String {
        return when (format) {
            FORMAT_ASTC_4x4_RGBA -> "ASTC 4x4 RGBA"
            FORMAT_ETC2_RGBA -> "ETC2 RGBA"
            FORMAT_BC7_RGBA -> "BC7 RGBA"
            FORMAT_RGBA32 -> "RGBA32"
            else -> "Unknown"
        }
    }
    
    /**
     * Load and transcode a KTX2 texture from input stream
     */
    fun loadKTX2Texture(inputStream: InputStream): TextureData {
        if (!initialized) {
            throw IllegalStateException("ModernTextureManager not properly initialized")
        }
        return loadKTX2Texture(inputStream, getOptimalTextureFormat())
    }
    
    /**
     * Load and transcode a KTX2 texture with specific format
     */
    fun loadKTX2Texture(inputStream: InputStream, targetFormat: Int): TextureData {
        if (!initialized) {
            throw IllegalStateException("ModernTextureManager not properly initialized")
        }
        // Read KTX2 data from input stream
        val ktx2Data = readInputStreamToByteArray(inputStream)
        
        // Create transcoder instance
        val transcoderHandle = nativeCreateTranscoder()
        if (transcoderHandle == 0L) {
            throw IOException("Failed to create transcoder instance")
        }
        
        try {
            // Initialize transcoder with KTX2 data
            if (!nativeInitTranscoder(transcoderHandle, ktx2Data)) {
                throw IOException("Failed to initialize transcoder with KTX2 data")
            }
            
            // Get texture dimensions
            val dimensions = nativeGetTextureDimensions(transcoderHandle)
            if (dimensions.size != 3) {
                throw IOException("Failed to get texture dimensions")
            }
            
            val width = dimensions[0]
            val height = dimensions[1]
            val levels = dimensions[2]
            
            Log.i(TAG, "Loading KTX2 texture: ${width}x${height} with $levels mip levels")
            
            // Transcode base level (level 0)
            val transcodedData = nativeTranscodeTexture(transcoderHandle, targetFormat, 0)
            
            Log.i(TAG, "Successfully transcoded texture: ${transcodedData.size} bytes")
            
            return TextureData(width, height, levels, targetFormat, transcodedData)
            
        } finally {
            // Always clean up transcoder instance
            nativeDestroyTranscoder(transcoderHandle)
        }
    }
    
    /**
     * Read input stream into Byte array
     */
    private fun readInputStreamToByteArray(inputStream: InputStream): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var bytesRead: Int
        
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        
        return outputStream.toByteArray()
    }
    
    /**
     * Get OpenGL texture format constant for upload
     */
    fun getOpenGLFormat(textureFormat: Int): Int {
        return when (textureFormat) {
            FORMAT_ASTC_4x4_RGBA -> 0x93B0 // GL_COMPRESSED_RGBA_ASTC_4x4_KHR
            FORMAT_ETC2_RGBA -> 0x9278 // GL_COMPRESSED_RGBA8_ETC2_EAC
            FORMAT_BC7_RGBA -> 0x8E8C // GL_COMPRESSED_RGBA_BPTC_UNORM
            FORMAT_RGBA32 -> GLES20.GL_RGBA
            else -> GLES20.GL_RGBA
        }
    }
    
    /**
     * Data structure for transcoded texture information
     */
    data class TextureData(
        val width: Int,
        val height: Int,
        val levels: Int,
        val format: Int,
        val data: ByteArray
    ) {
        fun getOpenGLFormat(): Int {
            return when (format) {
                FORMAT_ASTC_4x4_RGBA -> 0x93B0 // GL_COMPRESSED_RGBA_ASTC_4x4_KHR
                FORMAT_ETC2_RGBA -> 0x9278 // GL_COMPRESSED_RGBA8_ETC2_EAC
                FORMAT_BC7_RGBA -> 0x8E8C // GL_COMPRESSED_RGBA_BPTC_UNORM
                FORMAT_RGBA32 -> GLES20.GL_RGBA
                else -> GLES20.GL_RGBA
            }
        }
        
        fun getFormatName(): String {
            return when (format) {
                FORMAT_ASTC_4x4_RGBA -> "ASTC_4x4_RGBA"
                FORMAT_ETC2_RGBA -> "ETC2_RGBA"
                FORMAT_BC7_RGBA -> "BC7_RGBA"
                FORMAT_RGBA32 -> "RGBA32"
                else -> "UNKNOWN"
            }
        }
        
        fun isCompressed(): Boolean {
            return format != FORMAT_RGBA32
        }
        
        override fun toString(): String {
            return "TextureData[${width}x$height, $levels levels, ${getFormatName()}, ${data.size} bytes]"
        }
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as TextureData
            if (width != other.width) return false
            if (height != other.height) return false
            if (levels != other.levels) return false
            if (format != other.format) return false
            if (!data.contentEquals(other.data)) return false
            return true
        }
        
        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + levels
            result = 31 * result + format
            result = 31 * result + data.contentHashCode()
            return result
        }
    }
}
