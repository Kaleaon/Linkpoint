package com.linkpoint.slproto.inventory

import android.support.v4.os.EnvironmentCompat
import com.linkpoint.R

enum class SLAssetType(
    val typeCode: Int,
    val stringCode: String,
    val invType: SLInventoryType,
    val specialFolderType: Int,
    val drawableResource: Int,
    val typeDescription: Int,
    val actionDescription: Int
) {
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

    fun getInventoryType(): SLInventoryType = invType

    companion object {
        private val tagMap: Map<String, SLAssetType> = values().associateBy { it.stringCode }

        @JvmStatic
        fun getByString(str: String): SLAssetType {
            return tagMap[str] ?: AT_UNKNOWN
        }

        @JvmStatic
        fun getByType(typeCode: Int): SLAssetType {
            return values().firstOrNull { it.typeCode == typeCode } ?: AT_UNKNOWN
        }
    }
}