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

JNIEXPORT void JNICALL
Java_com_linkpoint_linden_llmath_Matrix4_nativeMultiply(
    JNIEnv* env,
    jobject thiz,
    jfloatArray jm1,
    jfloatArray jm2,
    jfloatArray jout
) {
    float m1[16], m2[16], out[16];
    env->GetFloatArrayRegion(jm1, 0, 16, m1);
    env->GetFloatArrayRegion(jm2, 0, 16, m2);

    for (int j = 0; j < 4; j++) {
        for (int i = 0; i < 4; i++) {
            out[j * 4 + i] = m1[j * 4 + 0] * m2[0 * 4 + i] +
                             m1[j * 4 + 1] * m2[1 * 4 + i] +
                             m1[j * 4 + 2] * m2[2 * 4 + i] +
                             m1[j * 4 + 3] * m2[3 * 4 + i];
        }
    }

    env->SetFloatArrayRegion(jout, 0, 16, out);
}

JNIEXPORT jboolean JNICALL
Java_com_linkpoint_linden_llmath_Matrix4_nativeInverse(
    JNIEnv* env,
    jobject thiz,
    jfloatArray jm,
    jfloatArray jout
) {
    float m[16], inv[16];
    env->GetFloatArrayRegion(jm, 0, 16, m);

    // Compute determinant
    float det = m[3] * m[6] * m[9] * m[12] - m[2] * m[7] * m[9] * m[12] - m[3] * m[5] * m[10] * m[12]
              + m[1] * m[7] * m[10] * m[12] + m[2] * m[5] * m[11] * m[12] - m[1] * m[6] * m[11] * m[12]
              - m[3] * m[6] * m[8] * m[13] + m[2] * m[7] * m[8] * m[13] + m[3] * m[4] * m[10] * m[13]
              - m[0] * m[7] * m[10] * m[13] - m[2] * m[4] * m[11] * m[13] + m[0] * m[6] * m[11] * m[13]
              + m[3] * m[5] * m[8] * m[14] - m[1] * m[7] * m[8] * m[14] - m[3] * m[4] * m[9] * m[14]
              + m[0] * m[7] * m[9] * m[14] + m[1] * m[4] * m[11] * m[14] - m[0] * m[5] * m[11] * m[14]
              - m[2] * m[5] * m[8] * m[15] + m[1] * m[6] * m[8] * m[15] + m[2] * m[4] * m[9] * m[15]
              - m[0] * m[6] * m[9] * m[15] - m[1] * m[4] * m[10] * m[15] + m[0] * m[5] * m[10] * m[15];

    if (fabsf(det) < 1e-8f) {
        return JNI_FALSE;
    }

    memcpy(inv, m, sizeof(m));
    float t;
    t = inv[1]; inv[1] = inv[4]; inv[4] = t;
    t = inv[2]; inv[2] = inv[8]; inv[8] = t;
    t = inv[6]; inv[6] = inv[9]; inv[9] = t;

    for (int j = 0; j < 3; j++) {
        inv[j * 4 + 3] = inv[12] * inv[0 * 4 + j] + inv[13] * inv[1 * 4 + j] + inv[14] * inv[2 * 4 + j];
    }
    inv[12] = -inv[0 * 4 + 3];
    inv[13] = -inv[1 * 4 + 3];
    inv[14] = -inv[2 * 4 + 3];
    inv[3] = 0.0f; inv[7] = 0.0f; inv[11] = 0.0f;

    env->SetFloatArrayRegion(jout, 0, 16, inv);
    return JNI_TRUE;
}

} // extern "C"
