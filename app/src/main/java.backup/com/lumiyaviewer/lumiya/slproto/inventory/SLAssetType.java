package com.lumiyaviewer.lumiya.slproto.inventory;

import android.support.v4.os.EnvironmentCompat;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.R;

public enum SLAssetType {
    AT_TEXTURE(0, "texture", SLInventoryType.IT_TEXTURE, 0, R.drawable.inv_image, R.string.asset_type_texture, R.string.asset_action_view),
    AT_SOUND(1, "sound", SLInventoryType.IT_SOUND, 1, R.drawable.inv_sound, R.string.asset_type_sound, -1),
    AT_CALLINGCARD(2, "callcard", SLInventoryType.IT_CALLINGCARD, 2, R.drawable.inv_vcard, R.string.asset_type_calling_card, -1),
    AT_LANDMARK(3, "landmark", SLInventoryType.IT_LANDMARK, 3, R.drawable.inv_landmark, R.string.asset_type_landmark, R.string.asset_action_teleport),
    AT_SCRIPT(4, "script", SLInventoryType.IT_LSL, 10, R.drawable.inv_script, R.string.asset_type_script, R.string.asset_action_edit),
    AT_CLOTHING(5, "clothing", SLInventoryType.IT_WEARABLE, 5, R.drawable.inv_clothes, R.string.asset_type_clothing, -1),
    AT_OBJECT(6, "object", SLInventoryType.IT_OBJECT, 6, R.drawable.inv_object, R.string.asset_type_object, R.string.asset_action_rez),
    AT_NOTECARD(7, "notecard", SLInventoryType.IT_NOTECARD, 7, R.drawable.inv_notecard, R.string.asset_type_notecard, R.string.asset_action_edit),
    AT_CATEGORY(8, "category", SLInventoryType.IT_CATEGORY, -1, R.drawable.inv_folder, R.string.asset_type_folder, -1),
    AT_LSL_TEXT(10, "lsltext", SLInventoryType.IT_LSL, 10, R.drawable.inv_script, R.string.asset_type_lsl, R.string.asset_action_edit),
    AT_LSL_BYTECODE(11, "lslbyte", SLInventoryType.IT_LSL, 10, R.drawable.inv_script, R.string.asset_type_lsl_bytecode, -1),
    AT_TEXTURE_TGA(12, "txtr_tga", SLInventoryType.IT_TEXTURE, 0, R.drawable.inv_image, R.string.asset_type_texture_tga, -1),
    AT_BODYPART(13, "bodypart", SLInventoryType.IT_WEARABLE, 13, R.drawable.inv_human, R.string.asset_type_bodypart, -1),
    AT_SOUND_WAV(17, "snd_wav", SLInventoryType.IT_SOUND, 1, R.drawable.inv_sound, R.string.asset_type_sound, -1),
    AT_IMAGE_TGA(18, "img_tga", SLInventoryType.IT_TEXTURE, 0, R.drawable.inv_image, R.string.asset_type_texture_tga, -1),
    AT_IMAGE_JPEG(19, "jpeg", SLInventoryType.IT_TEXTURE, 0, R.drawable.inv_image, R.string.asset_type_texture, -1),
    AT_ANIMATION(20, "animatn", SLInventoryType.IT_ANIMATION, 20, R.drawable.inv_animation, R.string.asset_type_animation, -1),
    AT_GESTURE(21, "gesture", SLInventoryType.IT_GESTURE, 21, R.drawable.inv_smile, R.string.asset_type_gesture, -1),
    AT_SIMSTATE(22, "simstate", SLInventoryType.IT_UNKNOWN, 6, R.drawable.inv_script, R.string.asset_type_sim_state, -1),
    AT_LINK(24, "link", SLInventoryType.IT_UNKNOWN, 6, R.drawable.inv_link, R.string.asset_type_link, -1),
    AT_LINK_FOLDER(25, "link_f", SLInventoryType.IT_UNKNOWN, 6, R.drawable.inv_link, R.string.asset_type_link, -1),
    AT_MESH(49, "mesh", SLInventoryType.IT_MESH, 6, R.drawable.inv_object, R.string.asset_type_object, -1),
    AT_WIDGET(40, "widget", SLInventoryType.IT_WIDGET, 6, R.drawable.inv_object, R.string.asset_type_object, -1),
    AT_UNKNOWN(-1, EnvironmentCompat.MEDIA_UNKNOWN, SLInventoryType.IT_UNKNOWN, -1, -1, -1, -1);
    
    private static final ImmutableMap<String, SLAssetType> tagMap = null;
    private final int actionDescription;
    private final int drawableResource;
    private final SLInventoryType invType;
    private final int specialFolderType;
    private final String stringCode;
    private final int typeCode;
    private final int typeDescription;

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (SLAssetType sLAssetType : values()) {
            builder.put(sLAssetType.stringCode, sLAssetType);
        }
        tagMap = builder.build();
    }

    private SLAssetType(int i, String str, SLInventoryType sLInventoryType, int i2, int i3, int i4, int i5) {
        this.typeCode = i;
        this.stringCode = str;
        this.invType = sLInventoryType;
        this.specialFolderType = i2;
        this.drawableResource = i3;
        this.typeDescription = i4;
        this.actionDescription = i5;
    }

    public static SLAssetType getByString(String str) {
        SLAssetType sLAssetType = tagMap.get(str);
        return sLAssetType == null ? AT_UNKNOWN : sLAssetType;
    }

    public static SLAssetType getByType(int i) {
        for (SLAssetType sLAssetType : values()) {
            if (sLAssetType.typeCode == i) {
                return sLAssetType;
            }
        }
        return AT_UNKNOWN;
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
