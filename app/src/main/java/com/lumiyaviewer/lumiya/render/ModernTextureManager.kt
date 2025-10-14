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
class ModernTextureManager {
    private String TAG = "ModernTextureManager";
    
    // Texture format constants matching JNI implementation
    Int FORMAT_ASTC_4x4_RGBA = 0
    Int FORMAT_ETC2_RGBA = 1
    Int FORMAT_BC7_RGBA = 2
    Int FORMAT_RGBA32 = 3
    
    // GPU capability flags
    private var supportsASTC: Boolean = false
    private var supportsETC2: Boolean = false
    private var supportsBC7: Boolean = false
    
    // Native library loading
    {
        try {
            System.loadLibrary("basis_transcoder");
            Log.i(TAG, "Basis transcoder native library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load basis transcoder native library", e);
            throw RuntimeException("Critical: Native library not available", e);
        }
    }
    
    // Instance state
    private var initialized: Boolean = false
    
    // Native method declarations
    private native Boolean nativeInit()
    private native Long nativeCreateTranscoder()
    private native Boolean nativeInitTranscoder(Long handle, Byte[] ktx2Data)
    private native Int[] nativeGetTextureDimensions(Long handle)
    private native Byte[] nativeTranscodeTexture(Long handle, Int targetFormat, Int level)
    private native Unit nativeDestroyTranscoder(Long handle)
    
    constructor(context: Context) {
        // Initialize the transcoder
        try {
            if (!nativeInit()) {
                Log.e(TAG, "Failed to initialize native transcoder");
                throw RuntimeException("Native transcoder initialization failed");
            }
            
            // Detect GPU capabilities
            detectGPUCapabilities()
            
            Log.i(TAG, "ModernTextureManager initialized with GPU capabilities:");
            Log.i(TAG, "  ASTC support: " + supportsASTC);
            Log.i(TAG, "  ETC2 support: " + supportsETC2);
            Log.i(TAG, "  BC7 support: " + supportsBC7);
            
            initialized = true
            
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native library not available", e);
            throw RuntimeException("Native library loading failed", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during initialization", e);
            throw RuntimeException("ModernTextureManager initialization failed", e);
        }
    }
    
    /**
     * Detect GPU texture format capabilities
     */
    private fun detectGPUCapabilities(): Unit {
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS)
        if (extensions != null) {
            supportsASTC = extensions.contains("GL_KHR_texture_compression_astc_ldr");
            supportsETC2 = extensions.contains("GL_OES_compressed_ETC2_RGB8_texture") ||
                          extensions.contains("GL_ARB_ES3_compatibility");
            supportsBC7 = extensions.contains("GL_EXT_texture_compression_bptc");
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
        switch (format) {
            case FORMAT_ASTC_4x4_RGBA: return "ASTC 4x4 RGBA";
            case FORMAT_ETC2_RGBA: return "ETC2 RGBA";
            case FORMAT_BC7_RGBA: return "BC7 RGBA";
            case FORMAT_RGBA32: return "RGBA32";
            default: return "Unknown";
        }
    }
    
    /**
     * Get the optimal texture format for this GPU
     */
    fun getOptimalTextureFormat(): Int {
        if (supportsASTC) {
            return FORMAT_ASTC_4x4_RGBA
        } else if (supportsETC2) {
            return FORMAT_ETC2_RGBA
        } else if (supportsBC7) {
            return FORMAT_BC7_RGBA
        } else {
            return FORMAT_RGBA32; // Fallback to uncompressed
        }
    }
    
    /**
     * Load and transcode a KTX2 texture from input stream
     */
    TextureData loadKTX2Texture(InputStream inputStream) throws IOException {
        if (!initialized) {
            throw IllegalStateException("ModernTextureManager not properly initialized");
        }
        return loadKTX2Texture(inputStream, getOptimalTextureFormat())
    }
    
    /**
     * Load and transcode a KTX2 texture with specific format
     */
    TextureData loadKTX2Texture(InputStream inputStream, Int targetFormat) throws IOException {
        if (!initialized) {
            throw IllegalStateException("ModernTextureManager not properly initialized");
        }
        // Read KTX2 data from input stream
        Byte[] ktx2Data = readInputStreamToByteArray(inputStream)
        
        // Create transcoder instance
        Long transcoderHandle = nativeCreateTranscoder()
        if (transcoderHandle == 0) {
            throw IOException("Failed to create transcoder instance");
        }
        
        try {
            // Initialize transcoder with KTX2 data
            if (!nativeInitTranscoder(transcoderHandle, ktx2Data)) {
                throw IOException("Failed to initialize transcoder with KTX2 data");
            }
            
            // Get texture dimensions
            Int[] dimensions = nativeGetTextureDimensions(transcoderHandle)
            if (dimensions == null || dimensions.length != 3) {
                throw IOException("Failed to get texture dimensions");
            }
            
            Int width = dimensions[0]
            Int height = dimensions[1]
            Int levels = dimensions[2]
            
            Log.i(TAG, "Loading KTX2 texture: " + width + "x" + height + " with " + levels + " mip levels");
            
            // Transcode base level (level 0)
            Byte[] transcodedData = nativeTranscodeTexture(transcoderHandle, targetFormat, 0)
            if (transcodedData == null) {
                throw IOException("Failed to transcode texture data");
            }
            
            Log.i(TAG, "Successfully transcoded texture: " + transcodedData.length + " bytes");
            
            return TextureData(width, height, levels, targetFormat, transcodedData)
            
        } finally {
            // Always clean up transcoder instance
            nativeDestroyTranscoder(transcoderHandle)
        }
    }
    
    /**
     * Read input stream into Byte array
     */
    private Byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = ByteArrayOutputStream()
        Byte[] buffer = Byte[8192]
        Int bytesRead
        
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        
        return outputStream.toByteArray()
    }
    
    /**
     * Get OpenGL texture format constant for upload
     */
    fun getOpenGLFormat(textureFormat: Int): Int {
        switch (textureFormat) {
            case FORMAT_ASTC_4x4_RGBA:
                return 0x93B0; // GL_COMPRESSED_RGBA_ASTC_4x4_KHR
            case FORMAT_ETC2_RGBA:
                return 0x9278; // GL_COMPRESSED_RGBA8_ETC2_EAC
            case FORMAT_BC7_RGBA:
                return 0x8E8C; // GL_COMPRESSED_RGBA_BPTC_UNORM
            case FORMAT_RGBA32:
                return GLES20.GL_RGBA
            default:
                return GLES20.GL_RGBA
        }
    }
    
    /**
     * Get format name for logging
     */
    fun getFormatName(textureFormat: Int): String {
        switch (textureFormat) {
            case FORMAT_ASTC_4x4_RGBA: return "ASTC_4x4_RGBA";
            case FORMAT_ETC2_RGBA: return "ETC2_RGBA";
            case FORMAT_BC7_RGBA: return "BC7_RGBA";
            case FORMAT_RGBA32: return "RGBA32";
            default: return "UNKNOWN";
        }
    }
    
    /**
     * Data structure for transcoded texture information
     */
    class TextureData {
        Int width
        Int height
        Int levels
        Int format
        Byte[] data
        
        TextureData(Int width, Int height, Int levels, Int format, Byte[] data) {
            this.width = width
            this.height = height
            this.levels = levels
            this.format = format
            this.data = data
        }
        
        fun getOpenGLFormat(): Int {
            return ModernTextureManager.getOpenGLFormat(format)
        }
        
        fun getFormatName(): String {
            return ModernTextureManager.getFormatName(format)
        }
        
        fun isCompressed(): Boolean {
            return format != FORMAT_RGBA32
        }
        
        override fun toString(): String {
            return String.format("TextureData[%dx%d, %d levels, %s, %d bytes]",
                    width, height, levels, getFormatName(), data.length)
        }
    }
}
