package com.lumiyaviewer.lumiya.slproto.assets

import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType

enum class SLWearableType(
    private val typeCode: Int,
    private val assetType: SLAssetType,
    private val isCritical: Boolean,
    private val name: String
) {
    WT_SHAPE(0, SLAssetType.AT_BODYPART, true, "Shape"),
    WT_SKIN(1, SLAssetType.AT_BODYPART, true, "Skin"),
    WT_HAIR(2, SLAssetType.AT_BODYPART, false, "Hair"),
    WT_EYES(3, SLAssetType.AT_BODYPART, false, "Eyes"),
    WT_SHIRT(4, SLAssetType.AT_CLOTHING, false, "Shirt"),
    WT_PANTS(5, SLAssetType.AT_CLOTHING, false, "Pants"),
    WT_SHOES(6, SLAssetType.AT_CLOTHING, false, "Shoes"),
    WT_SOCKS(7, SLAssetType.AT_CLOTHING, false, "Socks"),
    WT_JACKET(8, SLAssetType.AT_CLOTHING, false, "Jacket"),
    WT_GLOVES(9, SLAssetType.AT_CLOTHING, false, "Gloves"),
    WT_UNDERSHIRT(10, SLAssetType.AT_CLOTHING, false, "Undershirt"),
    WT_UNDERPANTS(11, SLAssetType.AT_CLOTHING, false, "Underpants"),
    WT_SKIRT(12, SLAssetType.AT_CLOTHING, false, "Skirt"),
    WT_ALPHA(13, SLAssetType.AT_CLOTHING, false, "Alpha"),
    WT_TATTOO(14, SLAssetType.AT_CLOTHING, false, "Tattoo"),
    WT_PHYSICS(15, SLAssetType.AT_CLOTHING, false, "Physics");

    companion object {
        fun getByCode(i: Int): SLWearableType? {
            for (type in values()) {
                if (type.typeCode == i) return type
            }
            return null
        }
    }

    fun getAssetType(): SLAssetType = assetType
    fun getIsCritical(): Boolean = isCritical
    fun getName(): String = name
    fun getTypeCode(): Int = typeCode
    fun isBodyPart(): Boolean = assetType == SLAssetType.AT_BODYPART
}
