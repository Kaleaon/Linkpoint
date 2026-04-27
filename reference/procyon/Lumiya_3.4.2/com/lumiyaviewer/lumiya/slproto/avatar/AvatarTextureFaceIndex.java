// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

public enum AvatarTextureFaceIndex
{
    TEX_EYES_ALPHA("TEX_EYES_ALPHA", 24, "eyes"), 
    TEX_EYES_BAKED("TEX_EYES_BAKED", 11, "eyes"), 
    TEX_EYES_IRIS("TEX_EYES_IRIS", 3, "eyes"), 
    TEX_HAIR("TEX_HAIR", 4, "hair"), 
    TEX_HAIR_ALPHA("TEX_HAIR_ALPHA", 25, "hair"), 
    TEX_HAIR_BAKED("TEX_HAIR_BAKED", 20, "hair"), 
    TEX_HEAD_ALPHA("TEX_HEAD_ALPHA", 23, "head"), 
    TEX_HEAD_BAKED("TEX_HEAD_BAKED", 8, "head"), 
    TEX_HEAD_BODYPAINT("TEX_HEAD_BODYPAINT", 0, "head"), 
    TEX_HEAD_TATTOO("TEX_HEAD_TATTOO", 26, "head"), 
    TEX_LOWER_ALPHA("TEX_LOWER_ALPHA", 21, "lower"), 
    TEX_LOWER_BAKED("TEX_LOWER_BAKED", 10, "lower"), 
    TEX_LOWER_BODYPAINT("TEX_LOWER_BODYPAINT", 6, "lower"), 
    TEX_LOWER_JACKET("TEX_LOWER_JACKET", 14, "lower"), 
    TEX_LOWER_PANTS("TEX_LOWER_PANTS", 2, "lower"), 
    TEX_LOWER_SHOES("TEX_LOWER_SHOES", 7, "lower"), 
    TEX_LOWER_SOCKS("TEX_LOWER_SOCKS", 12, "lower"), 
    TEX_LOWER_TATTOO("TEX_LOWER_TATTOO", 28, "lower"), 
    TEX_LOWER_UNDERPANTS("TEX_LOWER_UNDERPANTS", 17, "lower"), 
    TEX_SKIRT("TEX_SKIRT", 18, "skirt"), 
    TEX_SKIRT_BAKED("TEX_SKIRT_BAKED", 19, "skirt"), 
    TEX_UPPER_ALPHA("TEX_UPPER_ALPHA", 22, "upper"), 
    TEX_UPPER_BAKED("TEX_UPPER_BAKED", 9, "upper"), 
    TEX_UPPER_BODYPAINT("TEX_UPPER_BODYPAINT", 5, "upper"), 
    TEX_UPPER_GLOVES("TEX_UPPER_GLOVES", 15, "upper"), 
    TEX_UPPER_JACKET("TEX_UPPER_JACKET", 13, "upper"), 
    TEX_UPPER_SHIRT("TEX_UPPER_SHIRT", 1, "upper"), 
    TEX_UPPER_TATTOO("TEX_UPPER_TATTOO", 27, "upper"), 
    TEX_UPPER_UNDERSHIRT("TEX_UPPER_UNDERSHIRT", 16, "upper");
    
    private String bakedTextureName;
    
    private AvatarTextureFaceIndex(final String name, final int ordinal, final String bakedTextureName) {
        this.bakedTextureName = bakedTextureName;
    }
    
    public String getBakedTextureName() {
        return this.bakedTextureName;
    }
}
