#include <jni.h>
#include <android/log.h>
#include <cstring>  // for memcpy
#include <stdlib.h>

#define TAG "RawbufStub"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)

extern "C" {

/**
 * Stub implementation of rawbuf native library
 * Provides basic DirectByteBuffer functionality with standard Java ByteBuffer
 * This is a fallback when the full rawbuf library is not available
 */

// Native method implementations for DirectByteBuffer class

JNIEXPORT jobject JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_allocate(JNIEnv *env, jobject thiz, jint size) {
    LOGI("Allocating DirectByteBuffer of size %d using fallback implementation", size);
    
    // Use standard Java DirectByteBuffer allocation
    jclass byteBufferClass = env->FindClass("java/nio/ByteBuffer");
    if (!byteBufferClass) {
        LOGW("Could not find ByteBuffer class");
        return nullptr;
    }
    
    jmethodID allocateDirectMethod = env->GetStaticMethodID(byteBufferClass, "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
    if (!allocateDirectMethod) {
        LOGW("Could not find allocateDirect method");
        return nullptr;
    }
    
    jobject buffer = env->CallStaticObjectMethod(byteBufferClass, allocateDirectMethod, size);
    return buffer;
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_release(JNIEnv *env, jobject thiz, jobject buffer) {
    // For standard Java ByteBuffer, no explicit release needed - handled by GC
    LOGI("Release called on DirectByteBuffer (stub - no action needed)");
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_copyByteArray(JNIEnv *env, jobject thiz, jobject buffer, jint offset, jbyteArray src, jint srcOffset, jint length) {
    LOGI("Copying byte array to DirectByteBuffer using fallback implementation");
    
    // Get buffer direct access
    jbyte* bufferPtr = (jbyte*)env->GetDirectBufferAddress(buffer);
    if (!bufferPtr) {
        LOGW("Could not get direct buffer address");
        return;
    }
    
    // Get array elements
    jbyte* srcPtr = env->GetByteArrayElements(src, nullptr);
    if (!srcPtr) {
        LOGW("Could not get byte array elements");
        return;
    }
    
    // Copy data
    memcpy(bufferPtr + offset, srcPtr + srcOffset, length);
    
    // Release array
    env->ReleaseByteArrayElements(src, srcPtr, 0);
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_copyFloatArray(JNIEnv *env, jobject thiz, jobject buffer, jint offset, jfloatArray src, jint srcOffset, jint length) {
    LOGI("Copying float array to DirectByteBuffer using fallback implementation");
    
    float* bufferPtr = (float*)env->GetDirectBufferAddress(buffer);
    if (!bufferPtr) {
        LOGW("Could not get direct buffer address for floats");
        return;
    }
    
    jfloat* srcPtr = env->GetFloatArrayElements(src, nullptr);
    if (!srcPtr) {
        LOGW("Could not get float array elements");
        return;
    }
    
    // Copy float data (offset is in float units)
    memcpy(bufferPtr + offset, srcPtr + srcOffset, length * sizeof(float));
    
    env->ReleaseFloatArrayElements(src, srcPtr, 0);
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_copyShortArray(JNIEnv *env, jobject thiz, jobject buffer, jint offset, jshortArray src, jint srcOffset, jint length) {
    LOGI("Copying short array to DirectByteBuffer using fallback implementation");
    
    short* bufferPtr = (short*)env->GetDirectBufferAddress(buffer);
    if (!bufferPtr) {
        LOGW("Could not get direct buffer address for shorts");
        return;
    }
    
    jshort* srcPtr = env->GetShortArrayElements(src, nullptr);
    if (!srcPtr) {
        LOGW("Could not get short array elements");
        return;
    }
    
    // Copy short data (offset is in short units)  
    memcpy(bufferPtr + offset, srcPtr + srcOffset, length * sizeof(short));
    
    env->ReleaseShortArrayElements(src, srcPtr, 0);
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_copyShortArrayOffset(JNIEnv *env, jobject thiz, jobject buffer, jint offset, jshortArray src, jint srcOffset, jint length, jshort offsetValue) {
    LOGI("Copying short array with offset to DirectByteBuffer using fallback implementation");
    
    short* bufferPtr = (short*)env->GetDirectBufferAddress(buffer);
    if (!bufferPtr) {
        LOGW("Could not get direct buffer address for shorts with offset");
        return;
    }
    
    jshort* srcPtr = env->GetShortArrayElements(src, nullptr);
    if (!srcPtr) {
        LOGW("Could not get short array elements for offset copy");
        return;
    }
    
    // Copy with offset applied to each element
    for (int i = 0; i < length; i++) {
        bufferPtr[offset + i] = srcPtr[srcOffset + i] + offsetValue;
    }
    
    env->ReleaseShortArrayElements(src, srcPtr, 0);
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_copyPart(JNIEnv *env, jobject thiz, jobject destBuffer, jobject srcBuffer, jint destOffset, jint srcOffset, jint length) {
    LOGI("Copying between DirectByteBuffers using fallback implementation");
    
    jbyte* destPtr = (jbyte*)env->GetDirectBufferAddress(destBuffer);
    jbyte* srcPtr = (jbyte*)env->GetDirectBufferAddress(srcBuffer);
    
    if (!destPtr || !srcPtr) {
        LOGW("Could not get direct buffer addresses for buffer copy");
        return;
    }
    
    memcpy(destPtr + destOffset, srcPtr + srcOffset, length);
}

JNIEXPORT jint JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_zeroDecodeArray(JNIEnv *env, jclass clazz, jbyteArray src, jint srcStart, jint srcLen, jbyteArray dest, jint destStart, jint destMaxLen) {
    LOGI("Zero decode array using fallback implementation (basic copy)");
    
    // This is a simplified implementation - just copy data without zero decoding
    // Real zero decoding would decompress Second Life's zero-encoded data format
    
    jbyte* srcPtr = env->GetByteArrayElements(src, nullptr);
    jbyte* destPtr = env->GetByteArrayElements(dest, nullptr);
    
    if (!srcPtr || !destPtr) {
        LOGW("Could not get array elements for zero decode");
        if (srcPtr) env->ReleaseByteArrayElements(src, srcPtr, 0);
        if (destPtr) env->ReleaseByteArrayElements(dest, destPtr, JNI_ABORT);
        return -1;
    }
    
    // Simple copy (not real zero decode)
    int copyLen = (srcLen < destMaxLen) ? srcLen : destMaxLen;
    memcpy(destPtr + destStart, srcPtr + srcStart, copyLen);
    
    env->ReleaseByteArrayElements(src, srcPtr, 0);
    env->ReleaseByteArrayElements(dest, destPtr, 0);
    
    LOGW("Zero decode fallback: copied %d bytes (not actual decompression)", copyLen);
    return copyLen;
}

} // extern "C"