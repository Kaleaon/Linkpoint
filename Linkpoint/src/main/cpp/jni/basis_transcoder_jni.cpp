#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <vector>
#include "basis_universal/basisu_transcoder.h"

#define LOG_TAG "BasisTranscoder"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static bool g_transcoder_initialized = false;

static void ensure_transcoder_init() {
    if (!g_transcoder_initialized) {
        basist::basisu_transcoder_init();
        g_transcoder_initialized = true;
        LOGI("Basis Universal transcoder initialized successfully");
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_linkpoint_assets_BasisTranscoder_nativeInit(JNIEnv* env, jobject thiz) {
    ensure_transcoder_init();
    return JNI_TRUE;
}

extern "C" JNIEXPORT jobject JNICALL
Java_com_linkpoint_assets_BasisTranscoder_nativeGetDimensions(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata
) {
    if (!jdata) return nullptr;
    ensure_transcoder_init();

    jsize dataSize = env->GetArrayLength(jdata);
    if (dataSize <= 0) return nullptr;

    jbyte* dataPtr = env->GetByteArrayElements(jdata, nullptr);
    if (!dataPtr) return nullptr;

    basist::ktx2_transcoder transcoder;
    bool ok = transcoder.init(dataPtr, static_cast<uint32_t>(dataSize));
    env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);

    if (!ok) {
        LOGE("Failed to init KTX2 transcoder for dimension check");
        return nullptr;
    }

    uint32_t w = transcoder.get_width();
    uint32_t h = transcoder.get_height();

    jclass pairClass = env->FindClass("kotlin/Pair");
    if (!pairClass) return nullptr;

    jmethodID pairConstructor = env->GetMethodID(pairClass, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");
    jclass intClass = env->FindClass("java/lang/Integer");
    jmethodID intConstructor = env->GetMethodID(intClass, "<init>", "(I)V");

    if (!pairConstructor || !intClass || !intConstructor) return nullptr;

    jobject widthObj = env->NewObject(intClass, intConstructor, static_cast<jint>(w));
    jobject heightObj = env->NewObject(intClass, intConstructor, static_cast<jint>(h));

    return env->NewObject(pairClass, pairConstructor, widthObj, heightObj);
}

static jbyteArray internalTranscodeKTX2(
    JNIEnv* env,
    jbyteArray jdata,
    basist::transcoder_texture_format targetFormat
) {
    if (!jdata) return nullptr;
    ensure_transcoder_init();

    jsize dataSize = env->GetArrayLength(jdata);
    if (dataSize <= 0) return nullptr;

    jbyte* dataPtr = env->GetByteArrayElements(jdata, nullptr);
    if (!dataPtr) return nullptr;

    basist::ktx2_transcoder transcoder;
    bool ok = transcoder.init(dataPtr, static_cast<uint32_t>(dataSize));
    if (!ok) {
        LOGE("Failed to init KTX2 transcoder");
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    if (!transcoder.start_transcoding()) {
        LOGE("Failed to start KTX2 transcoding");
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    uint32_t width = transcoder.get_width();
    uint32_t height = transcoder.get_height();
    if (width == 0 || height == 0) {
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    basist::ktx2_image_level_info level_info;
    if (!transcoder.get_image_level_info(level_info, 0, 0, 0)) {
        LOGE("Failed to get image level info");
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    uint32_t bytesPerUnit = basist::basis_get_bytes_per_block_or_pixel(targetFormat);
    bool isUncompressed = basist::basis_transcoder_format_is_uncompressed(targetFormat);

    uint32_t units = isUncompressed ? (width * height) : level_info.m_total_blocks;
    uint32_t outputSizeBytes = units * bytesPerUnit;

    if (outputSizeBytes == 0) {
        env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);
        return nullptr;
    }

    std::vector<uint8_t> outBuffer(outputSizeBytes);

    bool transcodeOk = transcoder.transcode_image_level(
        0, 0, 0,
        outBuffer.data(),
        units,
        targetFormat,
        0
    );

    env->ReleaseByteArrayElements(jdata, dataPtr, JNI_ABORT);

    if (!transcodeOk) {
        LOGE("transcode_image_level failed for format %d", static_cast<int>(targetFormat));
        return nullptr;
    }

    jbyteArray result = env->NewByteArray(static_cast<jsize>(outputSizeBytes));
    if (result) {
        env->SetByteArrayRegion(result, 0, static_cast<jsize>(outputSizeBytes), reinterpret_cast<const jbyte*>(outBuffer.data()));
    }
    return result;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_linkpoint_assets_BasisTranscoder_nativeTranscodeToRgba32(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata
) {
    return internalTranscodeKTX2(env, jdata, basist::transcoder_texture_format::cTFRGBA32);
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_linkpoint_assets_BasisTranscoder_nativeTranscodeToEtc2(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata
) {
    return internalTranscodeKTX2(env, jdata, basist::transcoder_texture_format::cTFETC2_RGBA);
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_linkpoint_assets_BasisTranscoder_nativeTranscodeToAstc(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata
) {
    return internalTranscodeKTX2(env, jdata, basist::transcoder_texture_format::cTFASTC_4x4_RGBA);
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_linkpoint_assets_BasisTranscoder_nativeTranscode(
    JNIEnv* env,
    jobject thiz,
    jbyteArray jdata,
    jint targetFormat
) {
    basist::transcoder_texture_format fmt;
    switch (targetFormat) {
        case 0:
            fmt = basist::transcoder_texture_format::cTFASTC_4x4_RGBA;
            break;
        case 1:
            fmt = basist::transcoder_texture_format::cTFETC2_RGBA;
            break;
        case 2:
            fmt = basist::transcoder_texture_format::cTFBC7_RGBA;
            break;
        case 3:
        default:
            fmt = basist::transcoder_texture_format::cTFRGBA32;
            break;
    }
    return internalTranscodeKTX2(env, jdata, fmt);
}
