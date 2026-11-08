package com.linkpoint.linden.llappearance

import com.linkpoint.linden.llcommon.AssetType

enum class LLWearableType(
    val typeName: String,
    val label: String,
    val assetType: AssetType,
    val layerOrder: Int,
    val iconName: String,
) {
    SHAPE       ("shape",       "Body Shape",       AssetType.BODYPART,  0,  "inv_item_shape"),
    SKIN        ("skin",        "Skin",             AssetType.BODYPART,  1,  "inv_item_skin"),
    HAIR        ("hair",        "Hair",             AssetType.BODYPART,  2,  "inv_item_hair"),
    EYES        ("eyes",        "Eyes",             AssetType.BODYPART,  3,  "inv_item_eyes"),
    SHIRT       ("shirt",       "Shirt",            AssetType.CLOTHING,  4,  "inv_item_shirt"),
    PANTS       ("pants",       "Pants",            AssetType.CLOTHING,  5,  "inv_item_pants"),
    SHOES       ("shoes",       "Shoes",            AssetType.CLOTHING,  6,  "inv_item_shoes"),
    SOCKS       ("socks",       "Socks",            AssetType.CLOTHING,  7,  "inv_item_socks"),
    JACKET      ("jacket",      "Jacket",           AssetType.CLOTHING,  8,  "inv_item_jacket"),
    GLOVES      ("gloves",      "Gloves",           AssetType.CLOTHING,  9,  "inv_item_gloves"),
    UNDERSHIRT  ("undershirt",  "Undershirt",       AssetType.CLOTHING,  10, "inv_item_undershirt"),
    UNDERPANTS  ("underpants",  "Underpants",       AssetType.CLOTHING,  11, "inv_item_underpants"),
    SKIRT       ("skirt",       "Skirt",            AssetType.CLOTHING,  12, "inv_item_skirt"),
    ALPHA       ("alpha",       "Alpha Mask",       AssetType.CLOTHING,  13, "inv_item_alpha"),
    TATTOO      ("tattoo",      "Tattoo",           AssetType.CLOTHING,  14, "inv_item_tattoo"),
    PHYSICS     ("physics",     "Physics",          AssetType.CLOTHING,  15, "inv_item_physics"),
    UNIVERSAL   ("universal",   "Universal",        AssetType.CLOTHING,  16, "inv_item_universal"),
    INVALID     ("invalid",     "Invalid",          AssetType.NONE,      -1, "inv_item_invalid"),
    ;

    companion object {
        fun fromString(name: String): LLWearableType =
            entries.firstOrNull { it.typeName == name.lowercase() } ?: INVALID

        fun fromInt(value: Int): LLWearableType =
            entries.firstOrNull { it.layerOrder == value } ?: INVALID

        fun clothingTypes(): List<LLWearableType> =
            entries.filter { it.assetType == AssetType.CLOTHING }

        fun bodypartTypes(): List<LLWearableType> =
            entries.filter { it.assetType == AssetType.BODYPART }
    }
}
