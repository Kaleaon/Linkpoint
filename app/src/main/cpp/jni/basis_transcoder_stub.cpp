#include <jni.h>
#include <android/log.h>

#define TAG "BasisTranscoderStub"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)

extern "C" {

/**
 * Stub implementation of basis_transcoder native library
 * Provides basic texture transcoding fallback functionality  
 * This is used when the full Basis Universal transcoder is not available
 */

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeInit(JNIEnv *env, jclass clazz) {
    LOGI("Basis transcoder native init called - using stub implementation");
    LOGW("Full Basis Universal functionality not available - using fallback");
}

JNIEXPORT jboolean JNICALL  
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeIsFormatSupported(JNIEnv *env, jclass clazz, jint format) {
    LOGI("Checking if texture format %d is supported (stub)", format);
    
    // Return false for all Basis Universal specific formats  
    // The app should fall back to standard texture formats
    LOGW("Basis format %d not supported in stub implementation", format);
    return JNI_FALSE;
}

JNIEXPORT jbyteArray JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeTranscodeTexture(JNIEnv *env, jclass clazz, jbyteArray basisData, jint targetFormat) {
    LOGI("Transcode texture called on stub implementation - returning null");
    LOGW("Cannot transcode Basis texture in stub mode - app should use fallback textures");
    
    // Return null to indicate transcoding failed
    // The calling code should handle this gracefully and use alternative texture formats
    return nullptr;
}

JNIEXPORT jint JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeGetOptimalFormat(JNIEnv *env, jclass clazz) {
    LOGI("Get optimal texture format called - returning standard RGBA8888");
    
    // Return a safe standard format that all devices support
    // This corresponds to a standard uncompressed RGBA format
    return 0x1908; // GL_RGBA
}

JNIEXPORT jstring JNICALL
Java_com_lumiyaviewer_lumiya_render_ModernTextureManager_nativeGetVersion(JNIEnv *env, jclass clazz) {
    LOGI("Get Basis transcoder version called");
    return env->NewStringUTF("Stub-1.0 (Fallback Implementation)");
}

} // extern "C"