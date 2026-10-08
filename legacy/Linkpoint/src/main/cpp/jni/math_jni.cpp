/**
 * JNI bindings for unified 3D math and SL Volume generation
 */

#include <jni.h>
#include <android/log.h>
#include <cmath>
#include <cstring>

#define LOG_TAG "MathJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" {

JNIEXPORT void JNICALL
Java_com_linkpoint_linden_llmath_Quaternion_nativeRotateVector3(
    JNIEnv* env,
    jobject thiz,
    jfloat x, jfloat y, jfloat z,
    jfloat qx, jfloat qy, jfloat qz, jfloat qw,
    jfloatArray jout
) {
    float rw = -qx * x - qy * y - qz * z;
    float rx =  qw * x + qy * z - qz * y;
    float ry =  qw * y + qz * x - qx * z;
    float rz =  qw * z + qx * y - qy * x;

    float outX = -rw * qx + rx * qw - ry * qz + rz * qy;
    float outY = -rw * qy + ry * qw - rz * qx + rx * qz;
    float outZ = -rw * qz + rz * qw - rx * qy + ry * qx;

    float res[3] = { outX, outY, outZ };
    env->SetFloatArrayRegion(jout, 0, 3, res);
}

JNIEXPORT void JNICALL
Java_com_linkpoint_linden_llmath_Quaternion_nativeSlerp(
    JNIEnv* env,
    jobject thiz,
    jfloat t,
    jfloat x1, jfloat y1, jfloat z1, jfloat w1,
    jfloat x2, jfloat y2, jfloat z2, jfloat w2,
    jfloatArray jout
) {
    float cosTheta = x1 * x2 + y1 * y2 + z1 * z2 + w1 * w2;
    bool bFlip = false;
    if (cosTheta < 0.0f) {
        cosTheta = -cosTheta;
        bFlip = true;
    }

    float alpha, beta;
    if (1.0f - cosTheta < 0.00001f) {
        alpha = t;
        beta = 1.0f - t;
    } else {
        float theta = acosf(cosTheta);
        float sinTheta = sinf(theta);
        alpha = sinf(t * theta) / sinTheta;
        beta = sinf((1.0f - t) * theta) / sinTheta;
    }

    float bFactor = bFlip ? -beta : beta;
    float rx = bFactor * x1 + alpha * x2;
    float ry = bFactor * y1 + alpha * y2;
    float rz = bFactor * z1 + alpha * z2;
    float rw = bFactor * w1 + alpha * w2;

    float mag = sqrtf(rx * rx + ry * ry + rz * rz + rw * rw);
    if (mag > 1e-6f) {
        rx /= mag; ry /= mag; rz /= mag; rw /= mag;
    } else {
        rx = 0.0f; ry = 0.0f; rz = 0.0f; rw = 1.0f;
    }

    float res[4] = { rx, ry, rz, rw };
    env->SetFloatArrayRegion(jout, 0, 4, res);
}

} // extern "C"
