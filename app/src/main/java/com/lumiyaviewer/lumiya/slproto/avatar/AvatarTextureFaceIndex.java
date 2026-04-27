package com.lumiyaviewer.lumiya.slproto.avatar;

public enum AvatarTextureFaceIndex {
    TEX_HEAD_BODYPAINT("head"),
    TEX_UPPER_SHIRT("upper"),
    TEX_LOWER_PANTS("lower"),
    TEX_EYES_IRIS("eyes"),
    TEX_HAIR("hair"),
    TEX_UPPER_BODYPAINT("upper"),
    TEX_LOWER_BODYPAINT("lower"),
    TEX_LOWER_SHOES("lower"),
    TEX_HEAD_BAKED("head"),
    TEX_UPPER_BAKED("upper"),
    TEX_LOWER_BAKED("lower"),
    TEX_EYES_BAKED("eyes"),
    TEX_LOWER_SOCKS("lower"),
    TEX_UPPER_JACKET("upper"),
    TEX_LOWER_JACKET("lower"),
    TEX_UPPER_GLOVES("upper"),
    TEX_UPPER_UNDERSHIRT("upper"),
    TEX_LOWER_UNDERPANTS("lower"),
    TEX_SKIRT("skirt"),
    TEX_SKIRT_BAKED("skirt"),
    TEX_HAIR_BAKED("hair"),
    TEX_LOWER_ALPHA("lower"),
    TEX_UPPER_ALPHA("upper"),
    TEX_HEAD_ALPHA("head"),
    TEX_EYES_ALPHA("eyes"),
    TEX_HAIR_ALPHA("hair"),
    TEX_HEAD_TATTOO("head"),
    TEX_UPPER_TATTOO("upper"),
    TEX_LOWER_TATTOO("lower");
    
    private String bakedTextureName;

    private AvatarTextureFaceIndex(String str) {
        this.bakedTextureName = str;
    }

    public String getBakedTextureName() {
        return this.bakedTextureName;
    }
}
