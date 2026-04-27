package com.linkpoint.slproto.assets

import com.linkpoint.slproto.inventory.SLAssetType

enum class SLWearableType {
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
    WT_PHYSICS(15, SLAssetType.AT_CLOTHING, false, "Physics")
    
    private SLAssetType assetType
    private Boolean isCritical
    private String name
    private Int typeCode

    private SLWearableType(Int i, SLAssetType sLAssetType, Boolean z, String str) {
        this.typeCode = i
        this.assetType = sLAssetType
        this.isCritical = z
        this.name = str
    }

    @JvmStatic
     fun getByCode(i: Int): SLWearableType {
        for (SLWearableType sLWearableType : values()) {
            if (sLWearableType.typeCode == i) {
                return sLWearableType
            }
        }
        return null
    }

     public fun getAssetType(): SLAssetType {
        return this.assetType
    }

     public fun getIsCritical(): Boolean {
        return this.isCritical
    }

     public fun getName(): String {
        return this.name
    }

     public fun getTypeCode(): Int {
        return this.typeCode
    }

     public fun isBodyPart(): Boolean {
        return this.assetType == SLAssetType.AT_BODYPART
    }
}
