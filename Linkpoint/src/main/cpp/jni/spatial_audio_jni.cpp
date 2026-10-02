#include <jni.h>
#include <android/log.h>
#include "../audio/spatial/SpatialAudioEngine.h"

#define LOG_TAG "SpatialAudioJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using linkpoint::audio::SpatialAudioEngine;

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_linkpoint_voice_SpatialAudioBridge_nativeInit(JNIEnv* env, jobject thiz, jint sampleRate, jint maxRingBufferFrames) {
    auto* engine = new SpatialAudioEngine(sampleRate, maxRingBufferFrames);
    LOGI("Native SpatialAudioEngine created via JNI handle=0x%llx", static_cast<unsigned long long>(reinterpret_cast<uintptr_t>(engine)));
    return reinterpret_cast<jlong>(engine);
}

JNIEXPORT jint JNICALL
Java_com_linkpoint_voice_SpatialAudioBridge_nativeProcessPcmBuffer(
        JNIEnv* env, jobject thiz, jlong handle,
        jshortArray inputBuffer, jint inputSamples, jint channels,
        jshortArray outputBuffer, jint maxOutputSamples) {

    auto* engine = reinterpret_cast<SpatialAudioEngine*>(handle);
    if (!engine || !inputBuffer || !outputBuffer) {
        return 0;
    }

    jshort* inPcm = env->GetShortArrayElements(inputBuffer, nullptr);
    jshort* outPcm = env->GetShortArrayElements(outputBuffer, nullptr);

    int processed = 0;
    if (inPcm && outPcm) {
        processed = engine->processAudioPcmBuffer(
            reinterpret_cast<const int16_t*>(inPcm),
            inputSamples,
            channels,
            reinterpret_cast<int16_t*>(outPcm),
            maxOutputSamples
        );
    }

    if (inPcm) {
        env->ReleaseShortArrayElements(inputBuffer, inPcm, JNI_ABORT);
    }
    if (outPcm) {
        env->ReleaseShortArrayElements(outputBuffer, outPcm, 0); // Copy back output
    }

    return processed;
}

JNIEXPORT void JNICALL
Java_com_linkpoint_voice_SpatialAudioBridge_nativeUpdateListenerPosition(
        JNIEnv* env, jobject thiz, jlong handle,
        jfloat x, jfloat y, jfloat z,
        jfloat lookX, jfloat lookY, jfloat lookZ,
        jfloat upX, jfloat upY, jfloat upZ) {

    auto* engine = reinterpret_cast<SpatialAudioEngine*>(handle);
    if (engine) {
        engine->setListenerPosition(x, y, z, lookX, lookY, lookZ, upX, upY, upZ);
    }
}

JNIEXPORT void JNICALL
Java_com_linkpoint_voice_SpatialAudioBridge_nativeUpdateSourcePosition(
        JNIEnv* env, jobject thiz, jlong handle,
        jfloat x, jfloat y, jfloat z) {

    auto* engine = reinterpret_cast<SpatialAudioEngine*>(handle);
    if (engine) {
        engine->setSourcePosition(x, y, z);
    }
}

JNIEXPORT jfloat JNICALL
Java_com_linkpoint_voice_SpatialAudioBridge_nativeGetLatencyMs(
        JNIEnv* env, jobject thiz, jlong handle) {

    auto* engine = reinterpret_cast<SpatialAudioEngine*>(handle);
    if (engine) {
        return engine->getCurrentLatencyMs();
    }
    return 0.0f;
}

JNIEXPORT void JNICALL
Java_com_linkpoint_voice_SpatialAudioBridge_nativeRelease(
        JNIEnv* env, jobject thiz, jlong handle) {

    auto* engine = reinterpret_cast<SpatialAudioEngine*>(handle);
    if (engine) {
        delete engine;
        LOGI("Native SpatialAudioEngine released");
    }
}

} // extern "C"
