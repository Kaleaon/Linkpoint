package com.linkpoint.slproto.inventory

import androidx.v4.os.EnvironmentCompat
import java.util.HashMap
import java.util.Map

enum SLInventoryType {
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
    IT_UNKNOWN(-1, EnvironmentCompat.MEDIA_UNKNOWN, "Unknown")
    
    private Map<String, SLInventoryType> tagMap = null
    private String readableName
    private String stringCode
    private Int typeCode

    {
        tagMap = HashMap(values().length * 2)
        for (SLInventoryType sLInventoryType : values()) {
            tagMap.put(sLInventoryType.stringCode, sLInventoryType)
        }
    }

    private SLInventoryType(Int i, String str, String str2) {
        this.typeCode = i
        this.stringCode = str
        this.readableName = str2
    }

    SLInventoryType getByString(String str) {
        SLInventoryType sLInventoryType = tagMap.get(str)
        return sLInventoryType == null ? IT_UNKNOWN : sLInventoryType
    }

    SLInventoryType getByType(Int i) {
        for (SLInventoryType sLInventoryType : values()) {
            if (sLInventoryType.typeCode == i) {
                return sLInventoryType
            }
        }
        return IT_UNKNOWN
    }

    String getReadableName() {
        return this.readableName
    }

    String getStringCode() {
        return this.stringCode
    }

    Int getTypeCode() {
        return this.typeCode
    }
}
