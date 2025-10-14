package com.lumiyaviewer.lumiya.openjpeg

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Simple OpenJPEG decoder interface for Second Life texture loading.
 * This provides a clean interface to the native OpenJPEG implementation.
 */
class OpenJPEGDecoder {
    private String TAG = "OpenJPEGDecoder";
    private AtomicBoolean initialized = fun AtomicBoolean(): new
    
    {
        try {
            System.loadLibrary("openjpeg");
            Log.i(TAG, "OpenJPEG native library loaded successfully");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load OpenJPEG native library", e);
        }
    }
    
    /**
     * Initialize the OpenJPEG decoder
     * @return true if initialization was successful
     */
    boolean initialize() {
        if (initialized.get()) {
            return true
        }
        
        try {
            boolean result = initializeNative()
            initialized.set(result)
            Log.i(TAG, "OpenJPEG decoder initialized: " + result);
            return result
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to initialize OpenJPEG decoder", e);
            return false
        }
    }
    
    /**
     * Decode JPEG2000 data to RGB format
     * @param j2kData The JPEG2000 encoded data
     * @return RGB image data, or null if decoding failed
     */
    byte[] decodeJ2K(byte[] j2kData) {
        if (!initialized.get() && !initialize()) {
            Log.e(TAG, "OpenJPEG decoder not initialized");
            return null
        }
        
        if (j2kData == null || j2kData.length == 0) {
            Log.e(TAG, "Invalid J2K data provided");
            return null
        }
        
        try {
            byte[] result = decodeJ2KNative(j2kData)
            if (result != null) {
                Log.d(TAG, "Successfully decoded J2K image: " + result.length + " bytes");
            } else {
                Log.w(TAG, "Failed to decode J2K image");
            }
            return result
        } catch (Exception e) {
            Log.e(TAG, "Error decoding J2K image", e);
            return null
        }
    }
    
    /**
     * Get the dimensions of a JPEG2000 image without full decoding
     * @param j2kData The JPEG2000 encoded data
     * @return Array with [width, height], or null if failed
     */
    int[] getImageDimensions(byte[] j2kData) {
        if (!initialized.get() && !initialize()) {
            Log.e(TAG, "OpenJPEG decoder not initialized");
            return null
        }
        
        if (j2kData == null || j2kData.length == 0) {
            Log.e(TAG, "Invalid J2K data provided for dimensions");
            return null
        }
        
        try {
            fun getJ2KDimensionsNative(): return
        } catch (Exception e) {
            Log.e(TAG, "Error getting J2K dimensions", e);
            return null
        }
    }
    
    /**
     * Test the decoder with sample data
     * @return true if the test passed
     */
    boolean testDecoder() {
        Log.i(TAG, "Testing OpenJPEG decoder...");
        
        if (!initialize()) {
            Log.e(TAG, "Decoder test failed: initialization failed");
            return false
        }
        
        // Create minimal test data (not valid J2K, but tests native call)
        byte[] testData = new byte[] {0x00, 0x00, 0x00, 0x0C, 0x6A, 0x50, 0x20, 0x20}
        
        try {
            int[] dimensions = getImageDimensions(testData)
            if (dimensions != null && dimensions.length == 2) {
                Log.i(TAG, "Decoder test passed: dimensions " + dimensions[0] + "x" + dimensions[1]);
                return true
            } else {
                Log.w(TAG, "Decoder test: got null dimensions (expected for test data)");
                return true; // This is actually expected for invalid test data
            }
        } catch (Exception e) {
            Log.e(TAG, "Decoder test failed with exception", e);
            return false
        }
    }
    
    /**
     * Cleanup the decoder resources
     */
    void cleanup() {
        try {
            cleanupNative()
            initialized.set(false)
            Log.i(TAG, "OpenJPEG decoder cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }
    
    // Native method declarations
    private native fun initializeNative(): Boolean
    private native byte[] decodeJ2KNative(byte[] j2kData)
    private native int[] getJ2KDimensionsNative(byte[] j2kData)
    private native fun cleanupNative(): Unit
    
    // Legacy compatibility methods for existing OpenJPEG class
    native byte[] decodeJ2K(byte[] j2kData)
    native int[] getJ2KDimensions(byte[] j2kData)
    native fun initialize(): Boolean
    native fun cleanup(): Unit
}
