package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;

public enum SLWearableType {
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
    
    private SLAssetType assetType;
    private boolean isCritical;
    private String name;
    private int typeCode;

    private SLWearableType(int i, SLAssetType sLAssetType, boolean z, String str) {
        this.typeCode = i;
        this.assetType = sLAssetType;
        this.isCritical = z;
        this.name = str;
    }

    public static SLWearableType getByCode(int i) {
        for (SLWearableType sLWearableType : values()) {
            if (sLWearableType.typeCode == i) {
                return sLWearableType;
            }
        }
        return null;
    }

    public SLAssetType getAssetType() {
        return this.assetType;
    }

    public boolean getIsCritical() {
        return this.isCritical;
    }

    public String getName() {
        return this.name;
    }

    public int getTypeCode() {
        return this.typeCode;
    }

    public boolean isBodyPart() {
        return this.assetType == SLAssetType.AT_BODYPART;
    }
}
