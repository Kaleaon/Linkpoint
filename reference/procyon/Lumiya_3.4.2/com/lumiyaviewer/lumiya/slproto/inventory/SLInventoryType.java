// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.HashMap;
import java.util.Map;

public enum SLInventoryType
{
    IT_ANIMATION("IT_ANIMATION", 13, 19, "animation", "Animation"), 
    IT_ATTACHMENT("IT_ATTACHMENT", 11, 17, "attach", "Attachment"), 
    IT_CALLINGCARD("IT_CALLINGCARD", 2, 2, "callcard", "Calling card"), 
    IT_CATEGORY("IT_CATEGORY", 6, 8, "category", "Folder"), 
    IT_GESTURE("IT_GESTURE", 14, 20, "gesture", "Gesture"), 
    IT_LANDMARK("IT_LANDMARK", 3, 3, "landmark", "Landmark"), 
    IT_LSL("IT_LSL", 8, 10, "script", "Script"), 
    IT_MESH("IT_MESH", 15, 22, "mesh", "Mesh"), 
    IT_NOTECARD("IT_NOTECARD", 5, 7, "notecard", "Note card"), 
    IT_OBJECT("IT_OBJECT", 4, 6, "object", "Object"), 
    IT_ROOT_CATEGORY("IT_ROOT_CATEGORY", 7, 9, "root", "Root folder"), 
    IT_SNAPSHOT("IT_SNAPSHOT", 10, 15, "snapshot", "Snapshot"), 
    IT_SOUND("IT_SOUND", 1, 1, "sound", "Sound"), 
    IT_TEXTURE("IT_TEXTURE", 0, 0, "texture", "Texture"), 
    IT_TRASH("IT_TRASH", 9, 14, "trash", "Trash"), 
    IT_UNKNOWN("IT_UNKNOWN", 17, -1, "unknown", "Unknown"), 
    IT_WEARABLE("IT_WEARABLE", 12, 18, "wearable", "Wearable"), 
    IT_WIDGET("IT_WIDGET", 16, 23, "widget", "Widget");
    
    private static final Map<String, SLInventoryType> tagMap;
    private String readableName;
    private String stringCode;
    private int typeCode;
    
    static {
        int i = 0;
        tagMap = new HashMap<String, SLInventoryType>(values().length * 2);
        for (SLInventoryType[] values = values(); i < values.length; ++i) {
            final SLInventoryType slInventoryType = values[i];
            SLInventoryType.tagMap.put(slInventoryType.stringCode, slInventoryType);
        }
    }
    
    private SLInventoryType(final String name, final int ordinal, final int typeCode, final String stringCode, final String readableName) {
        this.typeCode = typeCode;
        this.stringCode = stringCode;
        this.readableName = readableName;
    }
    
    public static SLInventoryType getByString(final String s) {
        SLInventoryType it_UNKNOWN;
        if ((it_UNKNOWN = SLInventoryType.tagMap.get(s)) == null) {
            it_UNKNOWN = SLInventoryType.IT_UNKNOWN;
        }
        return it_UNKNOWN;
    }
    
    public static SLInventoryType getByType(final int n) {
        final SLInventoryType[] values = values();
        for (int i = 0; i < values.length; ++i) {
            final SLInventoryType slInventoryType = values[i];
            if (slInventoryType.typeCode == n) {
                return slInventoryType;
            }
        }
        return SLInventoryType.IT_UNKNOWN;
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
