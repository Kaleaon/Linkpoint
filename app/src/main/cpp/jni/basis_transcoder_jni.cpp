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
    
    // Get transcoded size
    uint32_t transcoded_size = instance->transcoder.get_transcoded_texture_size(
        0, level, format
    );
    
    if (transcoded_size == 0) {
        LOGE("Failed to get transcoded texture size");
        return nullptr;
    }
    
    // Allocate buffer for transcoded data
    std::vector<uint8_t> transcoded_data(transcoded_size);
    
    // Perform transcoding
    bool success = instance->transcoder.transcode_image(
        0, level, transcoded_data.data(), transcoded_size, format, 0
    );
    
    if (!success) {
        LOGE("Failed to transcode texture");
        return nullptr;
    }
    
    // Create Java byte array and copy data
    jbyteArray result = env->NewByteArray(transcoded_size);
    if (result) {
        env->SetByteArrayRegion(result, 0, transcoded_size, 
                               reinterpret_cast<jbyte*>(transcoded_data.data()));
    }
    
    LOGI("Successfully transcoded texture: %d bytes", transcoded_size);
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