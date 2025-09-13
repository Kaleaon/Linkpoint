#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <cstdlib>

#define TAG "ZeroDecode"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

extern "C" {

/**
 * Real Zero-Decode Implementation for Second Life Protocol
 * 
 * Second Life uses zero-encoding to compress message data by replacing
 * sequences of zero bytes with a special encoding format:
 * - 0x00 followed by count byte indicates (count) zero bytes
 * - Other bytes are copied as-is
 * 
 * This is critical for proper SL protocol communication.
 */

JNIEXPORT jint JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_zeroDecodeArray(
    JNIEnv *env, jclass clazz, 
    jbyteArray src, jint srcStart, jint srcLen, 
    jbyteArray dest, jint destStart, jint destMaxLen) {
    
    if (!src || !dest) {
        LOGE("Zero decode: null arrays provided");
        return -1;
    }
    
    if (srcLen <= 0 || destMaxLen <= 0) {
        LOGE("Zero decode: invalid lengths - src: %d, dest: %d", srcLen, destMaxLen);
        return -1;
    }
    
    // Get array elements
    jbyte* srcPtr = env->GetByteArrayElements(src, nullptr);
    jbyte* destPtr = env->GetByteArrayElements(dest, nullptr);
    
    if (!srcPtr || !destPtr) {
        LOGE("Zero decode: failed to get array elements");
        if (srcPtr) env->ReleaseByteArrayElements(src, srcPtr, 0);
        if (destPtr) env->ReleaseByteArrayElements(dest, destPtr, JNI_ABORT);
        return -1;
    }
    
    LOGI("Zero decode: processing %d bytes from offset %d", srcLen, srcStart);
    
    int srcPos = srcStart;
    int destPos = destStart;
    int srcEnd = srcStart + srcLen;
    int destEnd = destStart + destMaxLen;
    
    // Process the zero-encoded data
    while (srcPos < srcEnd && destPos < destEnd) {
        unsigned char currentByte = (unsigned char)srcPtr[srcPos++];
        
        if (currentByte == 0x00) {
            // Zero-encoding detected
            if (srcPos >= srcEnd) {
                LOGW("Zero decode: incomplete zero-encoding at end of data");
                break;
            }
            
            unsigned char zeroCount = (unsigned char)srcPtr[srcPos++];
            
            // Sanity check for zero count
            if (zeroCount == 0) {
                LOGW("Zero decode: zero count is 0, treating as literal 0x00");
                if (destPos < destEnd) {
                    destPtr[destPos++] = 0x00;
                }
                srcPos--; // Step back to reprocess the count byte as regular data
                continue;
            }
            
            // Check if we have enough space in destination
            if (destPos + zeroCount > destEnd) {
                LOGW("Zero decode: not enough space for %d zeros, truncating", zeroCount);
                zeroCount = destEnd - destPos;
            }
            
            // Write the zeros
            memset(destPtr + destPos, 0x00, zeroCount);
            destPos += zeroCount;
            
            LOGI("Zero decode: wrote %d zero bytes at position %d", zeroCount, destPos - zeroCount);
            
        } else {
            // Regular byte - copy as-is
            destPtr[destPos++] = (jbyte)currentByte;
        }
    }
    
    int decodedLength = destPos - destStart;
    
    // Release arrays
    env->ReleaseByteArrayElements(src, srcPtr, 0);
    env->ReleaseByteArrayElements(dest, destPtr, 0);
    
    LOGI("Zero decode: completed - input %d bytes, output %d bytes", srcLen, decodedLength);
    
    if (srcPos < srcEnd) {
        LOGW("Zero decode: %d bytes remaining in source (destination full)", srcEnd - srcPos);
    }
    
    return decodedLength;
}

/**
 * Zero-Encode Implementation (opposite of decode)
 * Useful for sending data back to Second Life servers
 */
JNIEXPORT jint JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_zeroEncodeArray(
    JNIEnv *env, jclass clazz,
    jbyteArray src, jint srcStart, jint srcLen,
    jbyteArray dest, jint destStart, jint destMaxLen) {
    
    if (!src || !dest) {
        LOGE("Zero encode: null arrays provided");
        return -1;
    }
    
    jbyte* srcPtr = env->GetByteArrayElements(src, nullptr);
    jbyte* destPtr = env->GetByteArrayElements(dest, nullptr);
    
    if (!srcPtr || !destPtr) {
        LOGE("Zero encode: failed to get array elements");
        if (srcPtr) env->ReleaseByteArrayElements(src, srcPtr, 0);
        if (destPtr) env->ReleaseByteArrayElements(dest, destPtr, JNI_ABORT);
        return -1;
    }
    
    int srcPos = srcStart;
    int destPos = destStart;
    int srcEnd = srcStart + srcLen;
    int destEnd = destStart + destMaxLen;
    
    while (srcPos < srcEnd && destPos < destEnd) {
        unsigned char currentByte = (unsigned char)srcPtr[srcPos];
        
        if (currentByte == 0x00) {
            // Count consecutive zeros
            int zeroCount = 0;
            int tempPos = srcPos;
            
            while (tempPos < srcEnd && srcPtr[tempPos] == 0x00 && zeroCount < 255) {
                zeroCount++;
                tempPos++;
            }
            
            // Write zero-encoding: 0x00 followed by count
            if (destPos + 2 <= destEnd) {
                destPtr[destPos++] = 0x00;      // Zero marker
                destPtr[destPos++] = (jbyte)zeroCount;  // Count
                srcPos += zeroCount;            // Skip the zeros we encoded
                
                LOGI("Zero encode: encoded %d zeros", zeroCount);
            } else {
                LOGW("Zero encode: not enough space for zero encoding");
                break;
            }
        } else {
            // Regular byte - copy as-is
            destPtr[destPos++] = (jbyte)currentByte;
            srcPos++;
        }
    }
    
    int encodedLength = destPos - destStart;
    
    env->ReleaseByteArrayElements(src, srcPtr, 0);
    env->ReleaseByteArrayElements(dest, destPtr, 0);
    
    LOGI("Zero encode: completed - input %d bytes, output %d bytes", srcLen, encodedLength);
    
    return encodedLength;
}

/**
 * Validate zero-encoded data format
 * Returns 1 if valid, 0 if invalid
 */
JNIEXPORT jint JNICALL
Java_com_lumiyaviewer_rawbuffers_DirectByteBuffer_validateZeroEncoding(
    JNIEnv *env, jclass clazz,
    jbyteArray data, jint start, jint length) {
    
    if (!data || length <= 0) {
        return 0;
    }
    
    jbyte* dataPtr = env->GetByteArrayElements(data, nullptr);
    if (!dataPtr) {
        return 0;
    }
    
    int pos = start;
    int end = start + length;
    bool isValid = true;
    
    while (pos < end && isValid) {
        unsigned char currentByte = (unsigned char)dataPtr[pos++];
        
        if (currentByte == 0x00) {
            // Check if we have a count byte
            if (pos >= end) {
                LOGW("Zero validation: incomplete zero-encoding at end");
                isValid = false;
                break;
            }
            
            unsigned char count = (unsigned char)dataPtr[pos++];
            if (count == 0) {
                LOGW("Zero validation: invalid zero count");
                isValid = false;
            }
        }
        // Other bytes are always valid
    }
    
    env->ReleaseByteArrayElements(data, dataPtr, 0);
    
    LOGI("Zero validation: data is %s", isValid ? "VALID" : "INVALID");
    return isValid ? 1 : 0;
}

} // extern "C"