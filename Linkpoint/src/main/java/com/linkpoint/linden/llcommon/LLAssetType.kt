package com.linkpoint.linden.llcommon

enum class AssetType(
    val value: Int,
    val typeName: String,
    val humanName: String?,
    val canLink: Boolean,
    val canFetch: Boolean,
    val canKnow: Boolean,
) {
    TEXTURE       (  0, "texture",  "texture",          true,  false, true),
    SOUND         (  1, "sound",    "sound",             true,  true,  true),
    CALLINGCARD   (  2, "callcard", "calling card",      true,  false, false),
    LANDMARK      (  3, "landmark", "landmark",          true,  true,  true),
    SCRIPT        (  4, "script",   "legacy script",     true,  false, false),
    CLOTHING      (  5, "clothing", "clothing",          true,  true,  true),
    OBJECT        (  6, "object",   "object",            true,  false, false),
    NOTECARD      (  7, "notecard", "note card",         true,  false, true),
    CATEGORY      (  8, "category", "folder",            true,  false, false),
    LSL_TEXT      ( 10, "lsltext",  "lsl2 script",       true,  false, false),
    LSL_BYTECODE  ( 11, "lslbyte",  "lsl bytecode",      true,  false, false),
    TEXTURE_TGA   ( 12, "txtr_tga", "tga texture",       true,  false, false),
    BODYPART      ( 13, "bodypart", "body part",         true,  true,  true),
    SOUND_WAV     ( 17, "snd_wav",  "sound",             true,  false, false),
    IMAGE_TGA     ( 18, "img_tga",  "targa image",       true,  false, false),
    IMAGE_JPEG    ( 19, "jpeg",     "jpeg image",        true,  false, false),
    ANIMATION     ( 20, "animatn",  "animation",         true,  true,  true),
    GESTURE       ( 21, "gesture",  "gesture",           true,  true,  true),
    SIMSTATE      ( 22, "simstate", "simstate",          false, false, false),
    LINK          ( 24, "link",     "sym link",          false, false, true),
    LINK_FOLDER   ( 25, "link_f",   "sym folder link",   false, false, true),
    MARKETPLACE_FOLDER(26, "marketplace_folder", "marketplace folder", false, false, false),
    WIDGET        ( 40, "widget",   "widget",            false, false, false),
    PERSON        ( 45, "person",   "person",            false, false, false),
    MESH          ( 49, "mesh",     "mesh",              false, false, false),
    RESERVED_1    ( 50, "reserved1",null,                false, false, false),
    RESERVED_2    ( 51, "reserved2",null,                false, false, false),
    RESERVED_3    ( 52, "reserved3",null,                false, false, false),
    RESERVED_4    ( 53, "reserved4",null,                false, false, false),
    RESERVED_5    ( 54, "reserved5",null,                false, false, false),
    RESERVED_6    ( 55, "reserved6",null,                false, false, false),
    SETTINGS      ( 56, "settings", "settings blob",     true,  true,  true),
    MATERIAL      ( 57, "material", "render material",   true,  true,  true),
    GLTF          ( 58, "gltf",     "GLTF",              true,  true,  true),
    GLTF_BIN      ( 59, "glbin",    "GLTF binary",       true,  true,  true),
    UNKNOWN       (255, "invalid",  null,                false, false, false),
    NONE          ( -1, "-1",       null,                false, false, false);

    val isLinkType: Boolean get() = this == LINK || this == LINK_FOLDER

    companion object {
        const val BADLOOKUP = "llassettype_bad_lookup"

        private val byValue: Map<Int, AssetType> = entries.associateBy { it.value }
        private val byTypeName: Map<String, AssetType> = entries.associateBy { it.typeName }
        private val byHumanName: Map<String, AssetType> =
            entries.filter { it.humanName != null }.associateBy { it.humanName!! }

        fun fromInt(value: Int): AssetType = byValue[value] ?: UNKNOWN

        fun lookup(typeName: String): AssetType = byTypeName[typeName] ?: UNKNOWN

        fun lookupHumanReadable(humanName: String): AssetType = byHumanName[humanName] ?: NONE

        fun lookupHumanReadable(type: AssetType): String? = type.humanName

        fun lookupCanLink(type: AssetType): Boolean = type.canLink

        fun lookupIsLinkType(type: AssetType): Boolean = type.isLinkType

        fun lookupIsAssetFetchByIDAllowed(type: AssetType): Boolean = type.canFetch

        fun lookupIsAssetIDKnowable(type: AssetType): Boolean = type.canKnow

        fun getTypeNames(): List<String> =
            entries.filter { it.value in 0 until 60 && it.typeName != "invalid" }
                .map { it.typeName }
    }
}
