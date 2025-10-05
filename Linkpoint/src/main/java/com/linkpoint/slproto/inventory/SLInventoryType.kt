package com.linkpoint.slproto.inventory

import android.support.v4.os.EnvironmentCompat

enum class SLInventoryType(val typeCode: Int, val stringCode: String, val readableName: String) {
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
        private val tagMap: Map<String, SLInventoryType> = values().associateBy { it.stringCode }

        @JvmStatic
        fun getByString(str: String): SLInventoryType {
            return tagMap[str] ?: IT_UNKNOWN
        }

        @JvmStatic
        fun getByType(typeCode: Int): SLInventoryType {
            return values().firstOrNull { it.typeCode == typeCode } ?: IT_UNKNOWN
        }
    }
}