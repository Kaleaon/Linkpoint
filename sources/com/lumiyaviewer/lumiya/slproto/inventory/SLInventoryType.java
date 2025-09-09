package com.lumiyaviewer.lumiya.slproto.inventory;

import android.support.v4.os.EnvironmentCompat;
import java.util.HashMap;
import java.util.Map;

public enum SLInventoryType {
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
    
    private static final Map<String, SLInventoryType> tagMap = null;
    private String readableName;
    private String stringCode;
    private int typeCode;

    static {
        int i;
        tagMap = new HashMap(values().length * 2);
        for (SLInventoryType sLInventoryType : values()) {
            tagMap.put(sLInventoryType.stringCode, sLInventoryType);
        }
    }

    private SLInventoryType(int i, String str, String str2) {
        this.typeCode = i;
        this.stringCode = str;
        this.readableName = str2;
    }

    public static SLInventoryType getByString(String str) {
        SLInventoryType sLInventoryType = tagMap.get(str);
        return sLInventoryType == null ? IT_UNKNOWN : sLInventoryType;
    }

    public static SLInventoryType getByType(int i) {
        for (SLInventoryType sLInventoryType : values()) {
            if (sLInventoryType.typeCode == i) {
                return sLInventoryType;
            }
        }
        return IT_UNKNOWN;
    }

    public String getReadableName() {
        return this.readableName;
    }

    public String getStringCode() {
        return this.stringCode;
    }

    public int getTypeCode() {
        return this.typeCode;
    }
}
