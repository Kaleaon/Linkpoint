#include "ChatBubbleOverlay.h"

extern "C" {

JNIEXPORT jfloat JNICALL
Java_com_linkpoint_ui_chat_SpatialChatView_nativeComputeOpacity(
    JNIEnv* env,
    jclass clazz,
    jfloat cameraX, jfloat cameraY, jfloat cameraZ,
    jfloat sourceX, jfloat sourceY, jfloat sourceZ) {
    return linkpoint::ChatBubbleOverlay::computeBubbleOpacity(
        cameraX, cameraY, cameraZ,
        sourceX, sourceY, sourceZ
    );
}

}
