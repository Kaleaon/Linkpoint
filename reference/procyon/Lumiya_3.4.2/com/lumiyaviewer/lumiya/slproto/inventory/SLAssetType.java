// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.google.common.collect.ImmutableMap;

public enum SLAssetType
{
    AT_ANIMATION("AT_ANIMATION", 16, 20, "animatn", SLInventoryType.IT_ANIMATION, 20, 2130837693, 2131296354, -1), 
    AT_BODYPART("AT_BODYPART", 12, 13, "bodypart", SLInventoryType.IT_WEARABLE, 13, 2130837697, 2131296356, -1), 
    AT_CALLINGCARD("AT_CALLINGCARD", 2, 2, "callcard", SLInventoryType.IT_CALLINGCARD, 2, 2130837709, 2131296357, -1), 
    AT_CATEGORY("AT_CATEGORY", 8, 8, "category", SLInventoryType.IT_CATEGORY, -1, 2130837696, 2131296359, -1), 
    AT_CLOTHING("AT_CLOTHING", 5, 5, "clothing", SLInventoryType.IT_WEARABLE, 5, 2130837695, 2131296358, -1), 
    AT_GESTURE("AT_GESTURE", 17, 21, "gesture", SLInventoryType.IT_GESTURE, 21, 2130837705, 2131296360, -1), 
    AT_IMAGE_JPEG("AT_IMAGE_JPEG", 15, 19, "jpeg", SLInventoryType.IT_TEXTURE, 0, 2130837698, 2131296373, -1), 
    AT_IMAGE_TGA("AT_IMAGE_TGA", 14, 18, "img_tga", SLInventoryType.IT_TEXTURE, 0, 2130837698, 2131296374, -1), 
    AT_LANDMARK("AT_LANDMARK", 3, 3, "landmark", SLInventoryType.IT_LANDMARK, 3, 2130837699, 2131296361, 2131296335), 
    AT_LINK("AT_LINK", 19, 24, "link", SLInventoryType.IT_UNKNOWN, 6, 2130837700, 2131296362, -1), 
    AT_LINK_FOLDER("AT_LINK_FOLDER", 20, 25, "link_f", SLInventoryType.IT_UNKNOWN, 6, 2130837700, 2131296362, -1), 
    AT_LSL_BYTECODE("AT_LSL_BYTECODE", 10, 11, "lslbyte", SLInventoryType.IT_LSL, 10, 2130837704, 2131296365, -1), 
    AT_LSL_TEXT("AT_LSL_TEXT", 9, 10, "lsltext", SLInventoryType.IT_LSL, 10, 2130837704, 2131296364, 2131296333), 
    AT_MESH("AT_MESH", 21, 49, "mesh", SLInventoryType.IT_MESH, 6, 2130837702, 2131296367, -1), 
    AT_NOTECARD("AT_NOTECARD", 7, 7, "notecard", SLInventoryType.IT_NOTECARD, 7, 2130837701, 2131296366, 2131296333), 
    AT_OBJECT("AT_OBJECT", 6, 6, "object", SLInventoryType.IT_OBJECT, 6, 2130837702, 2131296367, 2131296334), 
    AT_SCRIPT("AT_SCRIPT", 4, 4, "script", SLInventoryType.IT_LSL, 10, 2130837704, 2131296369, 2131296333), 
    AT_SIMSTATE("AT_SIMSTATE", 18, 22, "simstate", SLInventoryType.IT_UNKNOWN, 6, 2130837704, 2131296370, -1), 
    AT_SOUND("AT_SOUND", 1, 1, "sound", SLInventoryType.IT_SOUND, 1, 2130837706, 2131296372, -1), 
    AT_SOUND_WAV("AT_SOUND_WAV", 13, 17, "snd_wav", SLInventoryType.IT_SOUND, 1, 2130837706, 2131296372, -1), 
    AT_TEXTURE("AT_TEXTURE", 0, 0, "texture", SLInventoryType.IT_TEXTURE, 0, 2130837698, 2131296373, 2131296336), 
    AT_TEXTURE_TGA("AT_TEXTURE_TGA", 11, 12, "txtr_tga", SLInventoryType.IT_TEXTURE, 0, 2130837698, 2131296374, -1), 
    AT_UNKNOWN("AT_UNKNOWN", 23, -1, "unknown", SLInventoryType.IT_UNKNOWN, -1, -1, -1, -1), 
    AT_WIDGET("AT_WIDGET", 22, 40, "widget", SLInventoryType.IT_WIDGET, 6, 2130837702, 2131296367, -1);
    
    private static final ImmutableMap<String, SLAssetType> tagMap;
    private final int actionDescription;
    private final int drawableResource;
    private final SLInventoryType invType;
    private final int specialFolderType;
    private final String stringCode;
    private final int typeCode;
    private final int typeDescription;
    
    static {
        final ImmutableMap.Builder<String, SLAssetType> builder = ImmutableMap.builder();
        final SLAssetType[] values = values();
        for (int i = 0; i < values.length; ++i) {
            final SLAssetType slAssetType = values[i];
            builder.put(slAssetType.stringCode, slAssetType);
        }
        tagMap = builder.build();
    }
    
    private SLAssetType(final String name, final int ordinal, final int typeCode, final String stringCode, final SLInventoryType invType, final int specialFolderType, final int drawableResource, final int typeDescription, final int actionDescription) {
        this.typeCode = typeCode;
        this.stringCode = stringCode;
        this.invType = invType;
        this.specialFolderType = specialFolderType;
        this.drawableResource = drawableResource;
        this.typeDescription = typeDescription;
        this.actionDescription = actionDescription;
    }
    
    public static SLAssetType getByString(final String s) {
        SLAssetType at_UNKNOWN;
        if ((at_UNKNOWN = SLAssetType.tagMap.get(s)) == null) {
            at_UNKNOWN = SLAssetType.AT_UNKNOWN;
        }
        return at_UNKNOWN;
    }
    
    public static SLAssetType getByType(final int n) {
        final SLAssetType[] values = values();
        for (int i = 0; i < values.length; ++i) {
            final SLAssetType slAssetType = values[i];
            if (slAssetType.typeCode == n) {
                return slAssetType;
            }
        }
        return SLAssetType.AT_UNKNOWN;
    }
    
    public int getActionDescription() {
        return this.actionDescription;
    }
    
    public int getDrawableResource() {
        return this.drawableResource;
    }
    
    public SLInventoryType getInventoryType() {
        return this.invType;
    }
    
    public int getSpecialFolderType() {
        return this.specialFolderType;
    }
    
    public String getStringCode() {
        return this.stringCode;
    }
    
    public int getTypeCode() {
        return this.typeCode;
    }
    
    public int getTypeDescription() {
        return this.typeDescription;
    }
}
