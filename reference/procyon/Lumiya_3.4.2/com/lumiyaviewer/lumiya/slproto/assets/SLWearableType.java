// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.assets;

import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;

public enum SLWearableType
{
    WT_ALPHA("WT_ALPHA", 13, 13, SLAssetType.AT_CLOTHING, false, "Alpha"), 
    WT_EYES("WT_EYES", 3, 3, SLAssetType.AT_BODYPART, false, "Eyes"), 
    WT_GLOVES("WT_GLOVES", 9, 9, SLAssetType.AT_CLOTHING, false, "Gloves"), 
    WT_HAIR("WT_HAIR", 2, 2, SLAssetType.AT_BODYPART, false, "Hair"), 
    WT_JACKET("WT_JACKET", 8, 8, SLAssetType.AT_CLOTHING, false, "Jacket"), 
    WT_PANTS("WT_PANTS", 5, 5, SLAssetType.AT_CLOTHING, false, "Pants"), 
    WT_PHYSICS("WT_PHYSICS", 15, 15, SLAssetType.AT_CLOTHING, false, "Physics"), 
    WT_SHAPE("WT_SHAPE", 0, 0, SLAssetType.AT_BODYPART, true, "Shape"), 
    WT_SHIRT("WT_SHIRT", 4, 4, SLAssetType.AT_CLOTHING, false, "Shirt"), 
    WT_SHOES("WT_SHOES", 6, 6, SLAssetType.AT_CLOTHING, false, "Shoes"), 
    WT_SKIN("WT_SKIN", 1, 1, SLAssetType.AT_BODYPART, true, "Skin"), 
    WT_SKIRT("WT_SKIRT", 12, 12, SLAssetType.AT_CLOTHING, false, "Skirt"), 
    WT_SOCKS("WT_SOCKS", 7, 7, SLAssetType.AT_CLOTHING, false, "Socks"), 
    WT_TATTOO("WT_TATTOO", 14, 14, SLAssetType.AT_CLOTHING, false, "Tattoo"), 
    WT_UNDERPANTS("WT_UNDERPANTS", 11, 11, SLAssetType.AT_CLOTHING, false, "Underpants"), 
    WT_UNDERSHIRT("WT_UNDERSHIRT", 10, 10, SLAssetType.AT_CLOTHING, false, "Undershirt");
    
    private SLAssetType assetType;
    private boolean isCritical;
    private String name;
    private int typeCode;
    
    private SLWearableType(final String name, final int ordinal, final int typeCode, final SLAssetType assetType, final boolean isCritical, final String name2) {
        this.typeCode = typeCode;
        this.assetType = assetType;
        this.isCritical = isCritical;
        this.name = name2;
    }
    
    public static SLWearableType getByCode(final int n) {
        final SLWearableType[] values = values();
        for (int i = 0; i < values.length; ++i) {
            final SLWearableType slWearableType = values[i];
            if (slWearableType.typeCode == n) {
                return slWearableType;
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
