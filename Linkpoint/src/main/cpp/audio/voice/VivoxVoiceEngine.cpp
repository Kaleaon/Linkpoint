/**
 * VivoxVoiceEngine.cpp
 *
 * Shutdown legacy 32-bit Vivox native components and delegate voice state
 * management to Java (com.linkpoint.voice.VoiceManager / WebRtcVoiceSession).
 *
 * Legacy Vivox C++ SDK binaries fail on 64-bit Android runtimes (ARM64 / x86_64).
 * Native Vivox binary imports (libvivoxsdk.so / vx_initialize) have been removed.
 */

#include <android/log.h>
#include <jni.h>
#include <cstdint>

#define LOG_TAG "VivoxVoiceEngine"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)

namespace linkpoint {
namespace voice {

class VivoxVoiceEngine {
public:
    static void shutdownLegacyVivoxEngine() {
        LOGI("Legacy 32-bit Vivox native engine shut down completely; voice state management delegated to Java WebRTC pipeline.");
    }

    static bool isVivoxEnabled() {
        return false; // Legacy Vivox is permanently disabled in favor of Java WebRTC
    }
};

} // namespace voice
} // namespace linkpoint

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_voice_VivoxVoiceEngine_nativeIsVivoxSupported(JNIEnv* env, jclass clazz) {
    LOGW("nativeIsVivoxSupported called: returning false (legacy 32-bit Vivox shut down)");
    return JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_linkpoint_voice_VivoxVoiceEngine_nativeShutdownVivox(JNIEnv* env, jclass clazz) {
    linkpoint::voice::VivoxVoiceEngine::shutdownLegacyVivoxEngine();
}

} // extern "C"
