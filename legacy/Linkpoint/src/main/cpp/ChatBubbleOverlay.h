#ifndef CHAT_BUBBLE_OVERLAY_H
#define CHAT_BUBBLE_OVERLAY_H

#include <jni.h>
#include <cmath>

namespace linkpoint {

class ChatBubbleOverlay {
public:
    ChatBubbleOverlay() = default;
    ~ChatBubbleOverlay() = default;

    /**
     * Computes the Euclidean distance between camera position and chat source position.
     */
    static float computeEuclideanDistance(float cameraX, float cameraY, float cameraZ,
                                           float sourceX, float sourceY, float sourceZ) {
        float dx = cameraX - sourceX;
        float dy = cameraY - sourceY;
        float dz = cameraZ - sourceZ;
        return std::sqrt(dx * dx + dy * dy + dz * dz);
    }

    /**
     * Calculates opacity attenuation based on distance:
     * - Distance < 5.0m: 100% opacity (1.0f)
     * - Distance between 5.0m and 20.0m: scales smoothly from 100% (1.0f) down to 30% (0.3f)
     * - Distance > 20.0m: 30% opacity (0.3f)
     */
    static float calculateOpacity(float distance) {
        const float MIN_ATTENUATION_DIST = 5.0f;
        const float MAX_ATTENUATION_DIST = 20.0f;
        const float MIN_OPACITY = 0.3f;
        const float MAX_OPACITY = 1.0f;

        if (distance <= MIN_ATTENUATION_DIST) {
            return MAX_OPACITY;
        }
        if (distance >= MAX_ATTENUATION_DIST) {
            return MIN_OPACITY;
        }

        float fraction = (distance - MIN_ATTENUATION_DIST) / (MAX_ATTENUATION_DIST - MIN_ATTENUATION_DIST);
        return MAX_OPACITY - fraction * (MAX_OPACITY - MIN_OPACITY);
    }

    /**
     * Computes distance and resulting bubble opacity in a single 16ms-optimized call.
     */
    static float computeBubbleOpacity(float cameraX, float cameraY, float cameraZ,
                                       float sourceX, float sourceY, float sourceZ) {
        float distance = computeEuclideanDistance(cameraX, cameraY, cameraZ, sourceX, sourceY, sourceZ);
        return calculateOpacity(distance);
    }
};

} // namespace linkpoint

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jfloat JNICALL
Java_com_linkpoint_ui_chat_SpatialChatView_nativeComputeOpacity(
    JNIEnv* env,
    jclass clazz,
    jfloat cameraX, jfloat cameraY, jfloat cameraZ,
    jfloat sourceX, jfloat sourceY, jfloat sourceZ
);

#ifdef __cplusplus
}
#endif

#endif // CHAT_BUBBLE_OVERLAY_H
