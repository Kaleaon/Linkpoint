#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include "basis_universal/basisu_transcoder.h"

#define LOG_TAG "BasisTranscoder"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Initialize transcoder on library load
static bool transcoder_initialized = false;

// Initialize the Basis Universal transcoder
JNIEXPORT jboolean JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeInit(JNIEnv *env, jclass clazz) {
    if (!transcoder_initialized) {
        basist::basisu_transcoder_init();
        transcoder_initialized = true;
        LOGI("Basis Universal transcoder initialized");
    }
    return JNI_TRUE;
}

// Transcoder structure to hold transcoder instance
struct TranscoderInstance {
    basist::ktx2_transcoder transcoder;
    std::vector<uint8_t> file_data;
    bool initialized;
    
    TranscoderInstance() : initialized(false) {}
};

// Create a new transcoder instance
JNIEXPORT jlong JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeCreateTranscoder(JNIEnv *env, jclass clazz) {
    TranscoderInstance* instance = new TranscoderInstance();
    return reinterpret_cast<jlong>(instance);
}

// Initialize transcoder with KTX2 data
JNIEXPORT jboolean JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeInitTranscoder(
    JNIEnv *env, jclass clazz, jlong handle, jbyteArray ktx2Data) {
    
    TranscoderInstance* instance = reinterpret_cast<TranscoderInstance*>(handle);
    if (!instance) {
        LOGE("Invalid transcoder handle");
        return JNI_FALSE;
    }
    
    // Get KTX2 data from Java byte array
    jsize dataLength = env->GetArrayLength(ktx2Data);
    jbyte* dataPtr = env->GetByteArrayElements(ktx2Data, nullptr);
    
    if (!dataPtr) {
        LOGE("Failed to get KTX2 data");
        return JNI_FALSE;
    }
    
    // Copy data to internal buffer
    instance->file_data.resize(dataLength);
    memcpy(instance->file_data.data(), dataPtr, dataLength);
    
    // Release Java byte array
    env->ReleaseByteArrayElements(ktx2Data, dataPtr, JNI_ABORT);
    
    // Initialize transcoder
    instance->initialized = instance->transcoder.init(
        instance->file_data.data(), 
        static_cast<uint32_t>(instance->file_data.size())
    );
    
    if (!instance->initialized) {
        LOGE("Failed to initialize KTX2 transcoder");
        return JNI_FALSE;
    }
    
    LOGI("KTX2 transcoder initialized successfully");
    return JNI_TRUE;
}

// Get texture dimensions
JNIEXPORT jintArray JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeGetTextureDimensions(
    JNIEnv *env, jclass clazz, jlong handle) {
    
    TranscoderInstance* instance = reinterpret_cast<TranscoderInstance*>(handle);
    if (!instance || !instance->initialized) {
        LOGE("Invalid or uninitialized transcoder handle");
        return nullptr;
    }
    
    uint32_t width = instance->transcoder.get_width();
    uint32_t height = instance->transcoder.get_height();
    uint32_t levels = instance->transcoder.get_levels();
    
    jintArray result = env->NewIntArray(3);
    if (result) {
        jint dimensions[3] = {static_cast<jint>(width), static_cast<jint>(height), static_cast<jint>(levels)};
        env->SetIntArrayRegion(result, 0, 3, dimensions);
    }
    
    return result;
}

// Get supported transcoder formats based on target format
basist::transcoder_texture_format getTranscoderFormat(jint targetFormat) {
    switch (targetFormat) {
        case 0: // ASTC_4x4
            return basist::transcoder_texture_format::cTFASTC_4x4_RGBA;
        case 1: // ETC2_RGBA
            return basist::transcoder_texture_format::cTFETC2_RGBA;
        case 2: // BC7_RGBA
            return basist::transcoder_texture_format::cTFBC7_RGBA;
        case 3: // RGBA32
            return basist::transcoder_texture_format::cTFRGBA32;
        default:
            return basist::transcoder_texture_format::cTFRGBA32;
    }
}

// Transcode texture to target format
JNIEXPORT jbyteArray JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeTranscodeTexture(
    JNIEnv *env, jclass clazz, jlong handle, jint targetFormat, jint level) {
    
    TranscoderInstance* instance = reinterpret_cast<TranscoderInstance*>(handle);
    if (!instance || !instance->initialized) {
        LOGE("Invalid or uninitialized transcoder handle");
        return nullptr;
    }
    
    basist::transcoder_texture_format format = getTranscoderFormat(targetFormat);
    
    // Start transcoding first
    if (!instance->transcoder.start_transcoding()) {
        LOGE("Failed to start transcoding");
        return nullptr;
    }
    
    // Get level info to calculate buffer size
    basist::ktx2_image_level_info level_info;
    if (!instance->transcoder.get_image_level_info(level_info, level, 0, 0)) {
        LOGE("Failed to get image level info");
        return nullptr;
    }
    
    // Calculate the required output buffer size based on format
    uint32_t output_size_in_bytes;
    uint32_t blocks_or_pixels_buf_size;
    
    if (format == basist::transcoder_texture_format::cTFRGBA32) {
        // Uncompressed format: 4 bytes per pixel
        blocks_or_pixels_buf_size = level_info.m_orig_width * level_info.m_orig_height;
        output_size_in_bytes = blocks_or_pixels_buf_size * 4;
    } else {
        // Compressed formats: size in blocks
        blocks_or_pixels_buf_size = level_info.m_num_blocks_x * level_info.m_num_blocks_y;
        
        // Calculate bytes per block for different formats
        uint32_t bytes_per_block = 16; // Default for most formats like ASTC, ETC2
        if (format == basist::transcoder_texture_format::cTFBC7_RGBA) {
            bytes_per_block = 16; // BC7 uses 16 bytes per block
        }
        
        output_size_in_bytes = blocks_or_pixels_buf_size * bytes_per_block;
    }
    
    // Allocate buffer for transcoded data
    std::vector<uint8_t> transcoded_data(output_size_in_bytes);
    
    // Perform transcoding
    bool success = instance->transcoder.transcode_image_level(
        level, 0, 0, // level_index, layer_index, face_index
        transcoded_data.data(), 
        blocks_or_pixels_buf_size, // This parameter expects blocks for compressed, pixels for uncompressed
        format,
        0 // decode_flags
    );
    
    if (!success) {
        LOGE("Failed to transcode texture");
        return nullptr;
    }
    
    // Create Java byte array and copy data
    jbyteArray result = env->NewByteArray(output_size_in_bytes);
    if (result) {
        env->SetByteArrayRegion(result, 0, output_size_in_bytes, 
                               reinterpret_cast<jbyte*>(transcoded_data.data()));
    }
    
    LOGI("Successfully transcoded texture: %d bytes", output_size_in_bytes);
    return result;
}

// Clean up transcoder instance
JNIEXPORT void JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeDestroyTranscoder(
    JNIEnv *env, jclass clazz, jlong handle) {
    
    TranscoderInstance* instance = reinterpret_cast<TranscoderInstance*>(handle);
    if (instance) {
        delete instance;
        LOGI("Transcoder instance destroyed");
    }
}