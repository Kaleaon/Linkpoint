#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <cstdlib>
#include <cstring>
#include "basis_universal/basisu_transcoder.h"

#define LOG_TAG "OpenJPEGBasis"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Initialize transcoder on library load
static bool transcoder_initialized = false;

// Initialize the Basis Universal transcoder
JNIEXPORT jboolean JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEG_initBasisTranscoder(JNIEnv *env, jobject thiz) {
    if (!transcoder_initialized) {
        basist::basisu_transcoder_init();
        transcoder_initialized = true;
        LOGI("Basis Universal transcoder initialized for OpenJPEG integration");
    }
    return JNI_TRUE;
}

// Check if data is KTX2 format
JNIEXPORT jboolean JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEG_isKTX2Format(JNIEnv *env, jobject thiz, jbyteArray data) {
    if (!data) return JNI_FALSE;
    
    jsize dataLength = env->GetArrayLength(data);
    if (dataLength < 12) return JNI_FALSE;
    
    jbyte* dataPtr = env->GetByteArrayElements(data, nullptr);
    if (!dataPtr) return JNI_FALSE;
    
    // Check KTX2 magic bytes: 0xAB, 0x4B, 0x54, 0x58, 0x20, 0x32, 0x30, 0xBB, 0x0D, 0x0A, 0x1A, 0x0A
    bool isKTX2 = (dataPtr[0] == (jbyte)0xAB && dataPtr[1] == 0x4B && dataPtr[2] == 0x54 && dataPtr[3] == 0x58 &&
                   dataPtr[4] == 0x20 && dataPtr[5] == 0x32 && dataPtr[6] == 0x30 && dataPtr[7] == (jbyte)0xBB &&
                   dataPtr[8] == 0x0D && dataPtr[9] == 0x0A && dataPtr[10] == 0x1A && dataPtr[11] == 0x0A);
    
    env->ReleaseByteArrayElements(data, dataPtr, JNI_ABORT);
    return isKTX2 ? JNI_TRUE : JNI_FALSE;
}

// Get KTX2 texture dimensions
JNIEXPORT jintArray JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEG_getKTX2Dimensions(JNIEnv *env, jobject thiz, jbyteArray ktx2Data) {
    if (!transcoder_initialized) {
        LOGE("Basis transcoder not initialized");
        return nullptr;
    }
    
    jsize dataLength = env->GetArrayLength(ktx2Data);
    jbyte* dataPtr = env->GetByteArrayElements(ktx2Data, nullptr);
    
    if (!dataPtr) {
        LOGE("Failed to get KTX2 data");
        return nullptr;
    }
    
    // Initialize transcoder
    basist::ktx2_transcoder transcoder;
    bool initialized = transcoder.init(dataPtr, static_cast<uint32_t>(dataLength));
    
    env->ReleaseByteArrayElements(ktx2Data, dataPtr, JNI_ABORT);
    
    if (!initialized) {
        LOGE("Failed to initialize KTX2 transcoder");
        return nullptr;
    }
    
    uint32_t width = transcoder.get_width();
    uint32_t height = transcoder.get_height();
    uint32_t levels = transcoder.get_levels();
    
    jintArray result = env->NewIntArray(3);
    if (result) {
        jint dimensions[3] = {static_cast<jint>(width), static_cast<jint>(height), static_cast<jint>(levels)};
        env->SetIntArrayRegion(result, 0, 3, dimensions);
    }
    
    return result;
}

// Map target format from Java constants to Basis Universal format
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

// Decompress KTX2 data 
JNIEXPORT jobject JNICALL
Java_com_lumiyaviewer_lumiya_openjpeg_OpenJPEG_decompressKTX2(JNIEnv *env, jobject thiz, jbyteArray ktx2Data, jint targetFormat) {
    if (!transcoder_initialized) {
        LOGE("Basis transcoder not initialized");
        return nullptr;
    }
    
    jsize dataLength = env->GetArrayLength(ktx2Data);
    jbyte* dataPtr = env->GetByteArrayElements(ktx2Data, nullptr);
    
    if (!dataPtr) {
        LOGE("Failed to get KTX2 data");
        return nullptr;
    }
    
    // Initialize transcoder
    basist::ktx2_transcoder transcoder;
    bool initialized = transcoder.init(dataPtr, static_cast<uint32_t>(dataLength));
    
    if (!initialized) {
        env->ReleaseByteArrayElements(ktx2Data, dataPtr, JNI_ABORT);
        LOGE("Failed to initialize KTX2 transcoder");
        return nullptr;
    }
    
    // Start transcoding
    if (!transcoder.start_transcoding()) {
        env->ReleaseByteArrayElements(ktx2Data, dataPtr, JNI_ABORT);
        LOGE("Failed to start transcoding");
        return nullptr;
    }
    
    // Get level info
    basist::ktx2_image_level_info level_info;
    if (!transcoder.get_image_level_info(level_info, 0, 0, 0)) {
        env->ReleaseByteArrayElements(ktx2Data, dataPtr, JNI_ABORT);
        LOGE("Failed to get image level info");
        return nullptr;
    }
    
    basist::transcoder_texture_format format = getTranscoderFormat(targetFormat);
    
    // Calculate buffer size
    uint32_t output_size_in_bytes;
    uint32_t blocks_or_pixels_buf_size;
    
    if (format == basist::transcoder_texture_format::cTFRGBA32) {
        // Uncompressed format: 4 bytes per pixel
        blocks_or_pixels_buf_size = level_info.m_orig_width * level_info.m_orig_height;
        output_size_in_bytes = blocks_or_pixels_buf_size * 4;
    } else {
        // Compressed formats: size in blocks
        blocks_or_pixels_buf_size = level_info.m_num_blocks_x * level_info.m_num_blocks_y;
        output_size_in_bytes = blocks_or_pixels_buf_size * 16; // 16 bytes per block for most formats
    }
    
    // Allocate buffer for transcoded data
    std::vector<uint8_t> transcoded_data(output_size_in_bytes);
    
    // Perform transcoding
    bool success = transcoder.transcode_image_level(
        0, 0, 0, // level_index, layer_index, face_index
        transcoded_data.data(), 
        blocks_or_pixels_buf_size,
        format,
        0 // decode_flags
    );
    
    env->ReleaseByteArrayElements(ktx2Data, dataPtr, JNI_ABORT);
    
    if (!success) {
        LOGE("Failed to transcode texture");
        return nullptr;
    }
    
    // Allocate persistent memory for the texture data
    void* persistentBuffer = malloc(output_size_in_bytes);
    if (!persistentBuffer) {
        LOGE("Failed to allocate memory for transcoded texture");
        return nullptr;
    }
    
    // Copy transcoded data to persistent buffer
    memcpy(persistentBuffer, transcoded_data.data(), output_size_in_bytes);
    
    // Create DirectByteBuffer that points to our persistent memory
    jobject result = env->NewDirectByteBuffer(persistentBuffer, output_size_in_bytes);
    if (result) {
        LOGI("Successfully transcoded KTX2 texture: %d bytes", output_size_in_bytes);
    } else {
        // Clean up if ByteBuffer creation failed
        free(persistentBuffer);
    }
    
    return result;
}