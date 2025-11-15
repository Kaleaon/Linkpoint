#include <jni.h>
#include <android/log.h>

#define TAG "AudioStubs"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)

extern "C" {

/**
 * Audio stub implementations for vxaudio-jni, sndfile, and oRTP libraries
 * Provides graceful fallback when advanced audio processing is not available
 */

// vxaudio-jni stub functions
JNIEXPORT jint JNICALL
Java_com_vivox_audio_VxAudioJNI_initializeAudio(JNIEnv *env, jclass clazz, jint sampleRate, jint bufferSize) {
    LOGW("VxAudio initialization requested - advanced audio processing not available (stub)");
    return -1; // Return error to indicate audio system not available
}

JNIEXPORT void JNICALL
Java_com_vivox_audio_VxAudioJNI_shutdownAudio(JNIEnv *env, jclass clazz) {
    LOGI("VxAudio shutdown requested (stub - no action needed)");
}

JNIEXPORT jint JNICALL
Java_com_vivox_audio_VxAudioJNI_processAudioBuffer(JNIEnv *env, jclass clazz, jbyteArray buffer, jint length) {
    LOGW("VxAudio buffer processing requested - not available in stub mode");
    return 0; // Return 0 processed bytes
}

// sndfile stub functions (audio file I/O)
JNIEXPORT jlong JNICALL
Java_com_vivox_audio_SndFileJNI_sf_1open(JNIEnv *env, jclass clazz, jstring path, jint mode, jobject sfinfo) {
    LOGW("SndFile open requested - audio file I/O not available (stub)");
    return 0; // Return null handle
}

JNIEXPORT jint JNICALL
Java_com_vivox_audio_SndFileJNI_sf_1close(JNIEnv *env, jclass clazz, jlong handle) {
    LOGI("SndFile close requested (stub)");
    return 0; // Return success
}

JNIEXPORT jlong JNICALL
Java_com_vivox_audio_SndFileJNI_sf_1read_1float(JNIEnv *env, jclass clazz, jlong handle, jfloatArray buffer, jlong frames) {
    LOGW("SndFile read requested - returning 0 frames (stub)");
    return 0; // Return 0 frames read
}

JNIEXPORT jlong JNICALL
Java_com_vivox_audio_SndFileJNI_sf_1write_1float(JNIEnv *env, jclass clazz, jlong handle, jfloatArray buffer, jlong frames) {
    LOGW("SndFile write requested - not available in stub mode");
    return 0; // Return 0 frames written
}

// oRTP stub functions (Real-time Transport Protocol for audio/video)
JNIEXPORT jlong JNICALL
Java_com_vivox_rtp_oRTPJNI_rtp_1session_1new(JNIEnv *env, jclass clazz, jint type) {
    LOGW("oRTP session creation requested - RTP streaming not available (stub)");
    return 0; // Return null session handle
}

JNIEXPORT jint JNICALL
Java_com_vivox_rtp_oRTPJNI_rtp_1session_1set_1local_1addr(JNIEnv *env, jclass clazz, jlong session, jstring addr, jint port) {
    LOGW("oRTP set local address requested - RTP not available (stub)");
    return -1; // Return error
}

JNIEXPORT jint JNICALL
Java_com_vivox_rtp_oRTPJNI_rtp_1session_1send_1with_1ts(JNIEnv *env, jclass clazz, jlong session, jbyteArray buffer, jint length, jint timestamp) {
    LOGW("oRTP send packet requested - RTP transmission not available (stub)");
    return -1; // Return error
}

JNIEXPORT jint JNICALL
Java_com_vivox_rtp_oRTPJNI_rtp_1session_1recv_1with_1ts(JNIEnv *env, jclass clazz, jlong session, jbyteArray buffer, jint length, jint timestamp) {
    LOGW("oRTP receive packet requested - RTP reception not available (stub)");
    return -1; // Return error (no data received)
}

JNIEXPORT void JNICALL
Java_com_vivox_rtp_oRTPJNI_rtp_1session_1destroy(JNIEnv *env, jclass clazz, jlong session) {
    LOGI("oRTP session destroy requested (stub - no action needed)");
}

// General audio device enumeration stubs
JNIEXPORT jint JNICALL
Java_com_vivox_audio_AudioDeviceJNI_getInputDeviceCount(JNIEnv *env, jclass clazz) {
    LOGW("Audio input device count requested - returning 0 (stub)");
    return 0; // No input devices available in stub mode
}

JNIEXPORT jint JNICALL
Java_com_vivox_audio_AudioDeviceJNI_getOutputDeviceCount(JNIEnv *env, jclass clazz) {
    LOGW("Audio output device count requested - returning 0 (stub)");
    return 0; // No output devices available in stub mode
}

JNIEXPORT jstring JNICALL
Java_com_vivox_audio_AudioDeviceJNI_getDeviceName(JNIEnv *env, jclass clazz, jint deviceIndex, jboolean isInput) {
    LOGW("Audio device name requested - no devices in stub mode");
    return env->NewStringUTF("No audio devices (Compatibility Mode)");
}

// Version information for audio components
JNIEXPORT jstring JNICALL
Java_com_vivox_audio_VxAudioJNI_getVersion(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("VxAudio-Stub-1.0");
}

JNIEXPORT jstring JNICALL
Java_com_vivox_audio_SndFileJNI_sf_1version_1string(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("libsndfile-Stub-1.0 (No file I/O)");
}

JNIEXPORT jstring JNICALL
Java_com_vivox_rtp_oRTPJNI_ortp_1get_1version(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF("oRTP-Stub-1.0 (No network streaming)");
}

} // extern "C"