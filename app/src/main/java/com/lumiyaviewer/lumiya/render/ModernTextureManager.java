package com.lumiyaviewer.lumiya.render;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Modern texture manager that uses Basis Universal transcoder for efficient
 * texture compression and GPU format optimization.
 * 
 * This replaces the legacy JPEG2000-based texture system with a modern
 * GPU-native approach using KTX2 container format and runtime transcoding.
 */
public class ModernTextureManager {
    private static final String TAG = "ModernTextureManager";
    
    // Texture format constants matching JNI implementation
    public static final int FORMAT_ASTC_4x4_RGBA = 0;
    public static final int FORMAT_ETC2_RGBA = 1;
    public static final int FORMAT_BC7_RGBA = 2;
    public static final int FORMAT_RGBA32 = 3;
    
    // GPU capability flags
    private boolean supportsASTC = false;
    private boolean supportsETC2 = false;
    private boolean supportsBC7 = false;
    
    // Native library loading
    static {
        try {
            System.loadLibrary("basis_transcoder");
            Log.i(TAG, "Basis transcoder native library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load basis transcoder native library", e);
            throw new RuntimeException("Critical: Native library not available", e);
        }
    }
    
    // Instance state
    private boolean initialized = false;
    
    // Native method declarations
    private static native boolean nativeInit();
    private static native long nativeCreateTranscoder();
    private static native boolean nativeInitTranscoder(long handle, byte[] ktx2Data);
    private static native int[] nativeGetTextureDimensions(long handle);
    private static native byte[] nativeTranscodeTexture(long handle, int targetFormat, int level);
    private static native void nativeDestroyTranscoder(long handle);
    
    public ModernTextureManager(Context context) {
        // Initialize the transcoder
        try {
            if (!nativeInit()) {
                Log.e(TAG, "Failed to initialize native transcoder");
                throw new RuntimeException("Native transcoder initialization failed");
            }
            
            // Detect GPU capabilities
            detectGPUCapabilities();
            
            Log.i(TAG, "ModernTextureManager initialized with GPU capabilities:");
            Log.i(TAG, "  ASTC support: " + supportsASTC);
            Log.i(TAG, "  ETC2 support: " + supportsETC2);
            Log.i(TAG, "  BC7 support: " + supportsBC7);
            
            initialized = true;
            
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Native library not available", e);
            throw new RuntimeException("Native library loading failed", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during initialization", e);
            throw new RuntimeException("ModernTextureManager initialization failed", e);
        }
    }
    
    /**
     * Detect GPU texture format capabilities
     */
    private void detectGPUCapabilities() {
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);
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
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Get optimal texture format for this device
     */
    public int getOptimalTextureFormat() {
        if (supportsASTC) {
            return FORMAT_ASTC_4x4_RGBA;
        } else if (supportsETC2) {
            return FORMAT_ETC2_RGBA;
        } else if (supportsBC7) {
            return FORMAT_BC7_RGBA;
        } else {
            return FORMAT_RGBA32;
        }
    }
    
    /**
     * Get format name for debugging
     */
    public static String getFormatName(int format) {
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
    public int getOptimalTextureFormat() {
        if (supportsASTC) {
            return FORMAT_ASTC_4x4_RGBA;
        } else if (supportsETC2) {
            return FORMAT_ETC2_RGBA;
        } else if (supportsBC7) {
            return FORMAT_BC7_RGBA;
        } else {
            return FORMAT_RGBA32; // Fallback to uncompressed
        }
    }
    
    /**
     * Load and transcode a KTX2 texture from input stream
     */
    public TextureData loadKTX2Texture(InputStream inputStream) throws IOException {
        if (!initialized) {
            throw new IllegalStateException("ModernTextureManager not properly initialized");
        }
        return loadKTX2Texture(inputStream, getOptimalTextureFormat());
    }
    
    /**
     * Load and transcode a KTX2 texture with specific format
     */
    public TextureData loadKTX2Texture(InputStream inputStream, int targetFormat) throws IOException {
        if (!initialized) {
            throw new IllegalStateException("ModernTextureManager not properly initialized");
        }
        // Read KTX2 data from input stream
        byte[] ktx2Data = readInputStreamToByteArray(inputStream);
        
        // Create transcoder instance
        long transcoderHandle = nativeCreateTranscoder();
        if (transcoderHandle == 0) {
            throw new IOException("Failed to create transcoder instance");
        }
        
        try {
            // Initialize transcoder with KTX2 data
            if (!nativeInitTranscoder(transcoderHandle, ktx2Data)) {
                throw new IOException("Failed to initialize transcoder with KTX2 data");
            }
            
            // Get texture dimensions
            int[] dimensions = nativeGetTextureDimensions(transcoderHandle);
            if (dimensions == null || dimensions.length != 3) {
                throw new IOException("Failed to get texture dimensions");
            }
            
            int width = dimensions[0];
            int height = dimensions[1];
            int levels = dimensions[2];
            
            Log.i(TAG, "Loading KTX2 texture: " + width + "x" + height + " with " + levels + " mip levels");
            
            // Transcode base level (level 0)
            byte[] transcodedData = nativeTranscodeTexture(transcoderHandle, targetFormat, 0);
            if (transcodedData == null) {
                throw new IOException("Failed to transcode texture data");
            }
            
            Log.i(TAG, "Successfully transcoded texture: " + transcodedData.length + " bytes");
            
            return new TextureData(width, height, levels, targetFormat, transcodedData);
            
        } finally {
            // Always clean up transcoder instance
            nativeDestroyTranscoder(transcoderHandle);
        }
    }
    
    /**
     * Read input stream into byte array
     */
    private byte[] readInputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Get OpenGL texture format constant for upload
     */
    public static int getOpenGLFormat(int textureFormat) {
        switch (textureFormat) {
            case FORMAT_ASTC_4x4_RGBA:
                return 0x93B0; // GL_COMPRESSED_RGBA_ASTC_4x4_KHR
            case FORMAT_ETC2_RGBA:
                return 0x9278; // GL_COMPRESSED_RGBA8_ETC2_EAC
            case FORMAT_BC7_RGBA:
                return 0x8E8C; // GL_COMPRESSED_RGBA_BPTC_UNORM
            case FORMAT_RGBA32:
                return GLES20.GL_RGBA;
            default:
                return GLES20.GL_RGBA;
        }
    }
    
    /**
     * Get format name for logging
     */
    public static String getFormatName(int textureFormat) {
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
    public static class TextureData {
        public final int width;
        public final int height;
        public final int levels;
        public final int format;
        public final byte[] data;
        
        public TextureData(int width, int height, int levels, int format, byte[] data) {
            this.width = width;
            this.height = height;
            this.levels = levels;
            this.format = format;
            this.data = data;
        }
        
        public int getOpenGLFormat() {
            return ModernTextureManager.getOpenGLFormat(format);
        }
        
        public String getFormatName() {
            return ModernTextureManager.getFormatName(format);
        }
        
        public boolean isCompressed() {
            return format != FORMAT_RGBA32;
        }
        
        @Override
        public String toString() {
            return String.format("TextureData[%dx%d, %d levels, %s, %d bytes]",
                    width, height, levels, getFormatName(), data.length);
        }
    }
}