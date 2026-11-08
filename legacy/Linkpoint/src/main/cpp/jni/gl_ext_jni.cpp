#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <android/log.h>

#define LOG_TAG "GlExtJni"
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)

// GL_EXT_buffer_storage entry-point resolved lazily via eglGetProcAddress.
// The Android SDK does not wrap this extension in any GLES3xExt Java class.
typedef void (*PFNGLBUFFERSTORAGEEXTPROC)(GLenum target, GLsizeiptr size,
                                          const void* data, GLbitfield flags);

static PFNGLBUFFERSTORAGEEXTPROC s_glBufferStorageEXT = nullptr;
static bool s_resolved = false;

static void resolveBufferStorageEXT() {
    if (s_resolved) return;
    s_resolved = true;
    s_glBufferStorageEXT = reinterpret_cast<PFNGLBUFFERSTORAGEEXTPROC>(
        eglGetProcAddress("glBufferStorageEXT"));
    if (!s_glBufferStorageEXT) {
        LOGW("glBufferStorageEXT not found via eglGetProcAddress");
    } else {
        LOGI("glBufferStorageEXT resolved");
    }
}

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_render_lumiya_core_GlExtHelper_nativeIsBufferStorageExtAvailable(
        JNIEnv* /*env*/, jclass /*clazz*/) {
    resolveBufferStorageEXT();
    return s_glBufferStorageEXT != nullptr ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_com_linkpoint_render_lumiya_core_GlExtHelper_nativeBufferStorageExt(
        JNIEnv* env, jclass /*clazz*/,
        jint target, jlong size, jobject data, jint flags) {
    if (!s_glBufferStorageEXT) return;

    void* dataPtr = nullptr;
    if (data != nullptr) {
        dataPtr = env->GetDirectBufferAddress(data);
    }
    s_glBufferStorageEXT(static_cast<GLenum>(target),
                         static_cast<GLsizeiptr>(size),
                         dataPtr,
                         static_cast<GLbitfield>(flags));
}

} // extern "C"
