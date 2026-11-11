package com.linkpoint.linden.llinventory

// LLWearableType::EType from llappearance/llwearabletype.h
// Asset types: clothing = 5, bodypart = 13 (mirrors LLAssetType)
private const val ASSET_CLOTHING  = 5
private const val ASSET_BODYPART  = 13

data class WearableEntry(
    val typeName: String,
    val defaultNewName: String,
    val assetType: Int,
    val allowMultiwear: Boolean = true,
    val disableCameraSwitch: Boolean = false
)

enum class WearableType(val value: Int, val entry: WearableEntry) {
    SHAPE(0,      WearableEntry("shape",      "New Shape",       ASSET_BODYPART,  false, true)),
    SKIN(1,       WearableEntry("skin",       "New Skin",        ASSET_BODYPART,  false, true)),
    HAIR(2,       WearableEntry("hair",       "New Hair",        ASSET_BODYPART,  false, true)),
    EYES(3,       WearableEntry("eyes",       "New Eyes",        ASSET_BODYPART,  false, true)),
    SHIRT(4,      WearableEntry("shirt",      "New Shirt",       ASSET_CLOTHING)),
    PANTS(5,      WearableEntry("pants",      "New Pants",       ASSET_CLOTHING)),
    SHOES(6,      WearableEntry("shoes",      "New Shoes",       ASSET_CLOTHING)),
    SOCKS(7,      WearableEntry("socks",      "New Socks",       ASSET_CLOTHING)),
    JACKET(8,     WearableEntry("jacket",     "New Jacket",      ASSET_CLOTHING)),
    GLOVES(9,     WearableEntry("gloves",     "New Gloves",      ASSET_CLOTHING)),
    UNDERSHIRT(10,WearableEntry("undershirt", "New Undershirt",  ASSET_CLOTHING)),
    UNDERPANTS(11,WearableEntry("underpants", "New Underpants",  ASSET_CLOTHING)),
    SKIRT(12,     WearableEntry("skirt",      "New Skirt",       ASSET_CLOTHING)),
    ALPHA(13,     WearableEntry("alpha",      "New Alpha",       ASSET_CLOTHING)),
    TATTOO(14,    WearableEntry("tattoo",     "New Tattoo",      ASSET_CLOTHING)),
    PHYSICS(15,   WearableEntry("physics",    "New Physics",     ASSET_CLOTHING,  true, true)),
    UNIVERSAL(16, WearableEntry("universal",  "New Universal",   ASSET_CLOTHING)),
    INVALID(255,  WearableEntry("invalid",    "",                -1)),
    NONE(-1,      WearableEntry("none",       "",                -1));

    val typeName: String get() = entry.typeName
    val defaultNewName: String get() = entry.defaultNewName
    val assetType: Int get() = entry.assetType
    val allowMultiwear: Boolean get() = entry.allowMultiwear
    val disableCameraSwitch: Boolean get() = entry.disableCameraSwitch

    companion object {
        private val byValue: Map<Int, WearableType> = entries.associateBy { it.value }
        private val byName: Map<String, WearableType> = entries.associateBy { it.typeName }
        private val byAssetType: Map<Int, List<WearableType>> =
            entries.filter { it != INVALID && it != NONE }
                   .groupBy { it.assetType }

        fun fromValue(value: Int): WearableType = byValue[value] ?: INVALID
        fun fromName(name: String): WearableType = byName[name] ?: INVALID

        fun fromInventoryFlags(flags: UInt): WearableType =
            fromValue((flags and InventoryDefines.II_FLAGS_SUBTYPE_MASK).toInt())

        fun forAssetType(assetType: Int): List<WearableType> =
            byAssetType[assetType] ?: emptyList()
    }
}
