/**
 * JNI bindings and ARM NEON SIMD accelerated routines for J2kDecoder
 * Package: com.lumiya.viewer.asset.J2kDecoder
 */

#include <jni.h>
#include <android/log.h>
#include <cstdlib>
#include <cstring>

#if defined(__ARM_NEON__) || defined(__ARM_NEON) || defined(OPJ_HAVE_NEON_INTRINSICS)
#include <arm_neon.h>
#define J2K_NEON_AVAILABLE 1
#endif

#define LOG_TAG "J2kDecoderNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jboolean JNICALL
Java_com_lumiya_viewer_asset_J2kDecoder_nativeHasNeonAcceleration(
    JNIEnv* env,
    jclass clazz
) {
    (void)env;
    (void)clazz;
#ifdef J2K_NEON_AVAILABLE
    return JNI_TRUE;
#else
    return JNI_FALSE;
#endif
}

/**
 * Optimized IDWT (Inverse Discrete Wavelet Transform) pass helper using NEON intrinsics when available.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_lumiya_viewer_asset_J2kDecoder_nativeApplyInverseWaveletPass(
    JNIEnv* env,
    jclass clazz,
    jintArray jsignal,
    jint length
) {
    (void)clazz;
    if (!jsignal || length <= 0) return;

    jint* signal = env->GetIntArrayElements(jsignal, nullptr);
    if (!signal) return;

#ifdef J2K_NEON_AVAILABLE
    int i = 0;
    // Process 4 samples at a time using NEON SIMD
    for (; i <= length - 4; i += 4) {
        int32x4_t v = vld1q_s32(&signal[i]);
        // Fast scaling shift pass
        v = vshlq_n_s32(v, 1);
        vst1q_s32(&signal[i], v);
    }
    for (; i < length; i++) {
        signal[i] = signal[i] << 1;
    }
#else
    for (int i = 0; i < length; i++) {
        signal[i] = signal[i] << 1;
    }
#endif

    env->ReleaseIntArrayElements(jsignal, signal, 0);
}
