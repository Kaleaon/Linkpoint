#include <jni.h>
#include <android/log.h>

#define TAG "VivoxStubs"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" {

/**
 * Stub implementations for Vivox native libraries
 * Provides graceful degradation when voice chat functionality is not available
 * Based on Vivox documentation at https://docs.vivox.com/v5/general/core/5_21_0/en-us/Core/Core.htm
 */

// Core Vivox SDK initialization stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1initialize(JNIEnv *env, jclass clazz, jstring config) {
    LOGW("Vivox vx_initialize called - returning compatibility mode (stub)");
    return -1; // Return error code to indicate Vivox not available
}

JNIEXPORT void JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1uninitialize(JNIEnv *env, jclass clazz) {
    LOGI("Vivox vx_uninitialize called (stub - no action needed)");
}

// Connector management stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1connector_1create(JNIEnv *env, jclass clazz, jobject request) {
    LOGW("Vivox connector create requested - voice chat not available (stub)");
    return -1; // Indicate failure
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1connector_1initiate_1shutdown(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox connector shutdown requested (stub)");
    return 0; // Success for shutdown
}

// Account management stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1account_1login(JNIEnv *env, jclass clazz, jobject request) {
    LOGW("Vivox account login requested - voice authentication not available (stub)");
    return -1; // Indicate login failure
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1account_1logout(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox account logout requested (stub)");
    return 0; // Success for logout
}

// Session management stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1session_1create(JNIEnv *env, jclass clazz, jobject request) {
    LOGW("Vivox session create requested - voice sessions not available (stub)");
    return -1; // Indicate session creation failure
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1session_1media_1connect(JNIEnv *env, jclass clazz, jobject request) {
    LOGW("Vivox session media connect requested - voice media not available (stub)");
    return -1; // Indicate media connection failure
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1session_1terminate(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox session terminate requested (stub)");
    return 0; // Success for termination
}

// Audio device management stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1aux_1get_1render_1devices(JNIEnv *env, jclass clazz, jobject request) {
    LOGW("Vivox render devices query requested - audio devices not available (stub)");
    return -1; // Indicate no devices available
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1aux_1get_1capture_1devices(JNIEnv *env, jclass clazz, jobject request) {
    LOGW("Vivox capture devices query requested - audio devices not available (stub)");
    return -1; // Indicate no devices available
}

// Event polling stubs
JNIEXPORT jobject JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1get_1message(JNIEnv *env, jclass clazz) {
    // Return null to indicate no messages available
    return nullptr;
}

JNIEXPORT jboolean JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1is_1initialized(JNIEnv *env, jclass clazz) {
    return JNI_FALSE; // Always return false in stub mode
}

// Version information stubs
JNIEXPORT jstring JNICALL
Java_com_vivox_service_VxClientProxyJNI_BUILD_1VERSION_1get(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("Stub-1.0 (Compatibility Mode)");
}

JNIEXPORT jstring JNICALL
Java_com_vivox_service_VxClientProxyJNI_BUILD_1DATE_1get(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("2024-09-13");
}

JNIEXPORT jstring JNICALL
Java_com_vivox_service_VxClientProxyJNI_BUILD_1HOST_1get(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("Linkpoint-Stub");
}

JNIEXPORT jstring JNICALL
Java_com_vivox_service_VxClientProxyJNI_BUILD_1PERSON_1get(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("ModernLinkpoint");
}

// Volume control stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1connector_1set_1local_1speaker_1volume(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox speaker volume control requested (stub - ignored)");
    return 0; // Return success but do nothing
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1connector_1set_1local_1mic_1volume(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox microphone volume control requested (stub - ignored)");
    return 0; // Return success but do nothing
}

// Mute control stubs
JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1connector_1mute_1local_1speaker(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox speaker mute control requested (stub - ignored)");
    return 0; // Return success but do nothing
}

JNIEXPORT jint JNICALL
Java_com_vivox_service_VxClientProxyJNI_vx_1req_1connector_1mute_1local_1mic(JNIEnv *env, jclass clazz, jobject request) {
    LOGI("Vivox microphone mute control requested (stub - ignored)");
    return 0; // Return success but do nothing
}

} // extern "C"