package com.linkpoint.render

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
    private const val String TAG = "ModernTextureManager"
    
    // Texture format constants matching JNI implementation
    const val Int FORMAT_ASTC_4x4_RGBA = 0
    const val Int FORMAT_ETC2_RGBA = 1
    const val Int FORMAT_BC7_RGBA = 2
    const val Int FORMAT_RGBA32 = 3
    
    // GPU capability flags
    private Boolean supportsASTC = false
    private Boolean supportsETC2 = false
    private Boolean supportsBC7 = false
    
    // Native library loading
    static {
        try {
            System.loadLibrary("basis_transcoder")
            Log.i(TAG, "Basis transcoder native library loaded successfully")
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load basis transcoder native library", e)
            throw RuntimeException("Critical: Native library not available", e)
        }
    }
    
    // Instance state
    private Boolean initialized = false
    
    // Native method declarations
    @JvmStatic
private native Boolean nativeInit()
    @JvmStatic
private native Long nativeCreateTranscoder()
    @JvmStatic
private native Boolean nativeInitTranscoder(Long handle, Byte[] ktx2Data)
    @JvmStatic
private native Int[] nativeGetTextureDimensions(Long handle)
    @JvmStatic
private native Byte[] nativeTranscodeTexture(Long handle, Int targetFormat, Int level)
    @JvmStatic
private native Unit nativeDestroyTranscoder(Long handle)
    
    public ModernTextureManager(Context context) {
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
            
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native library not available", e)
            throw RuntimeException("Native library loading failed", e)
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during initialization", e)
            throw RuntimeException("ModernTextureManager initialization failed", e)
        }
    }
    
    /**
     * Detect GPU texture format capabilities
     */
    private Unit detectGPUCapabilities() {
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS)
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
    public Boolean isInitialized() {
        return initialized
    }
    
    /**
     * Get optimal texture format for this device
     */
    public Int getOptimalTextureFormat() {
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
    @JvmStatic
    String getFormatName(Int format) {
        switch (format) {
            case FORMAT_ASTC_4x4_RGBA: return "ASTC 4x4 RGBA"
            case FORMAT_ETC2_RGBA: return "ETC2 RGBA"
            case FORMAT_BC7_RGBA: return "BC7 RGBA"
            case FORMAT_RGBA32: return "RGBA32"
            default: return "Unknown"
        }
    }
    
    /**
     * Get the optimal texture format for this GPU
     */
    public Int getOptimalTextureFormat() {
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
    public TextureData loadKTX2Texture(InputStream inputStream) throws IOException {
        if (!initialized) {
            throw IllegalStateException("ModernTextureManager not properly initialized")
        }
        return loadKTX2Texture(inputStream, getOptimalTextureFormat())
    }
    
    /**
     * Load and transcode a KTX2 texture with specific format
     */
    public TextureData loadKTX2Texture(InputStream inputStream, Int targetFormat) throws IOException {
        if (!initialized) {
            throw IllegalStateException("ModernTextureManager not properly initialized")
        }
        // Read KTX2 data from input stream
        Byte[] ktx2Data = readInputStreamToByteArray(inputStream)
        
        // Create transcoder instance
        Long transcoderHandle = nativeCreateTranscoder()
        if (transcoderHandle == 0) {
            throw IOException("Failed to create transcoder instance")
        }
        
        try {
            // Initialize transcoder with KTX2 data
            if (!nativeInitTranscoder(transcoderHandle, ktx2Data)) {
                throw IOException("Failed to initialize transcoder with KTX2 data")
            }
            
            // Get texture dimensions
            Int[] dimensions = nativeGetTextureDimensions(transcoderHandle)
            if (dimensions == null || dimensions.length != 3) {
                throw IOException("Failed to get texture dimensions")
            }
            
            Int width = dimensions[0]
            Int height = dimensions[1]
            Int levels = dimensions[2]
            
            Log.i(TAG, "Loading KTX2 texture: " + width + "x" + height + " with " + levels + " mip levels")
            
            // Transcode base level (level 0)
            Byte[] transcodedData = nativeTranscodeTexture(transcoderHandle, targetFormat, 0)
            if (transcodedData == null) {
                throw IOException("Failed to transcode texture data")
            }
            
            Log.i(TAG, "Successfully transcoded texture: " + transcodedData.length + " bytes")
            
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
    @JvmStatic
    Int getOpenGLFormat(Int textureFormat) {
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
    @JvmStatic
    String getFormatName(Int textureFormat) {
        switch (textureFormat) {
            case FORMAT_ASTC_4x4_RGBA: return "ASTC_4x4_RGBA"
            case FORMAT_ETC2_RGBA: return "ETC2_RGBA"
            case FORMAT_BC7_RGBA: return "BC7_RGBA"
            case FORMAT_RGBA32: return "RGBA32"
            default: return "UNKNOWN"
        }
    }
    
    /**
     * Data structure for transcoded texture information
     */
    @JvmStatic
    class TextureData {
        val Int width
        val Int height
        val Int levels
        val Int format
        val Byte[] data
        
        public TextureData(Int width, Int height, Int levels, Int format, Byte[] data) {
            this.width = width
            this.height = height
            this.levels = levels
            this.format = format
            this.data = data
        }
        
        public Int getOpenGLFormat() {
            return ModernTextureManager.getOpenGLFormat(format)
        }
        
        public String getFormatName() {
            return ModernTextureManager.getFormatName(format)
        }
        
        public Boolean isCompressed() {
            return format != FORMAT_RGBA32
        }
        
        override String toString() {
            return String.format("TextureData[%dx%d, %d levels, %s, %d bytes]",
                    width, height, levels, getFormatName(), data.length)
        }
    }
}