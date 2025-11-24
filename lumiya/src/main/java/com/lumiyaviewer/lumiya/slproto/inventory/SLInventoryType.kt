package com.lumiyaviewer.lumiya.slproto.inventory

import androidx.v4.os.EnvironmentCompat
import com.google.common.collect.ImmutableMap

enum class SLInventoryType(
    val typeCode: Int,
    val stringCode: String,
    val readableName: String
) {
    IT_TEXTURE(0, "texture", "Texture"),
    IT_SOUND(1, "sound", "Sound"),
    IT_CALLINGCARD(2, "callcard", "Calling card"),
    IT_LANDMARK(3, "landmark", "Landmark"),
    IT_OBJECT(6, "object", "Object"),
    IT_NOTECARD(7, "notecard", "Note card"),
    IT_CATEGORY(8, "category", "Folder"),
    IT_ROOT_CATEGORY(9, "root", "Root folder"),
    IT_LSL(10, "script", "Script"),
    IT_TRASH(14, "trash", "Trash"),
    IT_SNAPSHOT(15, "snapshot", "Snapshot"),
    IT_ATTACHMENT(17, "attach", "Attachment"),
    IT_WEARABLE(18, "wearable", "Wearable"),
    IT_ANIMATION(19, "animation", "Animation"),
    IT_GESTURE(20, "gesture", "Gesture"),
    IT_MESH(22, "mesh", "Mesh"),
    IT_WIDGET(23, "widget", "Widget"),
    IT_UNKNOWN(-1, EnvironmentCompat.MEDIA_UNKNOWN, "Unknown");

    companion object {
        private val tagMap = ImmutableMap.builder<String, SLInventoryType>().apply {
            for (type in values()) {
                put(type.stringCode, type)
            }
        }.build()

        fun getByString(str: String): SLInventoryType {
            return tagMap[str] ?: IT_UNKNOWN
        }

        fun getByType(type: Int): SLInventoryType {
            for (inventoryType in values()) {
                if (inventoryType.typeCode == type) {
                    return inventoryType
                }
            }
            return IT_UNKNOWN
        }
    }
}
