#include <jni.h>
#include <android/log.h>
#include <cstring>  // for memcpy
#include <stdlib.h>
#include <cmath>    // for cosf, sinf

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

// Additional texture manipulation functions based on reference link
// https://gamedev.stackexchange.com/questions/50623/scaling-and-rotating-texture-onto-another-texture-by-raw-buffer-data

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_scaleTexture(JNIEnv *env, jclass clazz, jobject srcBuffer, jint srcWidth, jint srcHeight, jobject destBuffer, jint destWidth, jint destHeight, jint bytesPerPixel) {
    LOGI("Texture scaling requested: %dx%d -> %dx%d, %d bytes per pixel", srcWidth, srcHeight, destWidth, destHeight, bytesPerPixel);
    
    jbyte* srcPtr = (jbyte*)env->GetDirectBufferAddress(srcBuffer);
    jbyte* destPtr = (jbyte*)env->GetDirectBufferAddress(destBuffer);
    
    if (!srcPtr || !destPtr) {
        LOGW("Could not get direct buffer addresses for texture scaling");
        return;
    }
    
    // Simple nearest-neighbor scaling algorithm
    float scaleX = (float)srcWidth / destWidth;
    float scaleY = (float)srcHeight / destHeight;
    
    for (int destY = 0; destY < destHeight; destY++) {
        for (int destX = 0; destX < destWidth; destX++) {
            // Find corresponding source pixel
            int srcX = (int)(destX * scaleX);
            int srcY = (int)(destY * scaleY);
            
            // Clamp to source bounds
            if (srcX >= srcWidth) srcX = srcWidth - 1;
            if (srcY >= srcHeight) srcY = srcHeight - 1;
            
            // Copy pixel data
            int srcOffset = (srcY * srcWidth + srcX) * bytesPerPixel;
            int destOffset = (destY * destWidth + destX) * bytesPerPixel;
            
            for (int b = 0; b < bytesPerPixel; b++) {
                destPtr[destOffset + b] = srcPtr[srcOffset + b];
            }
        }
    }
    
    LOGI("Texture scaling completed successfully");
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_rotateTexture(JNIEnv *env, jclass clazz, jobject srcBuffer, jint width, jint height, jobject destBuffer, jfloat angle, jint bytesPerPixel) {
    LOGI("Texture rotation requested: %dx%d by %.2f degrees, %d bytes per pixel", width, height, angle, bytesPerPixel);
    
    jbyte* srcPtr = (jbyte*)env->GetDirectBufferAddress(srcBuffer);
    jbyte* destPtr = (jbyte*)env->GetDirectBufferAddress(destBuffer);
    
    if (!srcPtr || !destPtr) {
        LOGW("Could not get direct buffer addresses for texture rotation");
        return;
    }
    
    // Convert angle to radians
    float radians = angle * 3.14159f / 180.0f;
    float cosAngle = cosf(radians);
    float sinAngle = sinf(radians);
    
    int centerX = width / 2;
    int centerY = height / 2;
    
    // Clear destination buffer first
    memset(destPtr, 0, width * height * bytesPerPixel);
    
    // Rotate each pixel
    for (int srcY = 0; srcY < height; srcY++) {
        for (int srcX = 0; srcX < width; srcX++) {
            // Translate to center origin
            int relativeX = srcX - centerX;
            int relativeY = srcY - centerY;
            
            // Apply rotation transformation
            int destX = (int)(relativeX * cosAngle - relativeY * sinAngle) + centerX;
            int destY = (int)(relativeX * sinAngle + relativeY * cosAngle) + centerY;
            
            // Check bounds
            if (destX >= 0 && destX < width && destY >= 0 && destY < height) {
                // Copy pixel data
                int srcOffset = (srcY * width + srcX) * bytesPerPixel;
                int destOffset = (destY * width + destX) * bytesPerPixel;
                
                for (int b = 0; b < bytesPerPixel; b++) {
                    destPtr[destOffset + b] = srcPtr[srcOffset + b];
                }
            }
        }
    }
    
    LOGI("Texture rotation completed successfully");
}

JNIEXPORT void JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_blendTextures(JNIEnv *env, jclass clazz, jobject srcBuffer, jobject destBuffer, jint width, jint height, jfloat alpha, jint bytesPerPixel) {
    LOGI("Texture blending requested: %dx%d with alpha %.2f, %d bytes per pixel", width, height, alpha, bytesPerPixel);
    
    jbyte* srcPtr = (jbyte*)env->GetDirectBufferAddress(srcBuffer);
    jbyte* destPtr = (jbyte*)env->GetDirectBufferAddress(destBuffer);
    
    if (!srcPtr || !destPtr) {
        LOGW("Could not get direct buffer addresses for texture blending");
        return;
    }
    
    // Clamp alpha to valid range
    if (alpha < 0.0f) alpha = 0.0f;
    if (alpha > 1.0f) alpha = 1.0f;
    
    int totalPixels = width * height;
    
    // Blend each pixel using alpha blending: result = src * alpha + dest * (1 - alpha)
    for (int pixel = 0; pixel < totalPixels; pixel++) {
        int offset = pixel * bytesPerPixel;
        
        for (int b = 0; b < bytesPerPixel; b++) {
            float srcValue = (float)((unsigned char)srcPtr[offset + b]);
            float destValue = (float)((unsigned char)destPtr[offset + b]);
            float blendedValue = srcValue * alpha + destValue * (1.0f - alpha);
            
            destPtr[offset + b] = (jbyte)((int)blendedValue);
        }
    }
    
    LOGI("Texture blending completed successfully");
}

} // extern "C"