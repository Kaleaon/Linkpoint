// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

public enum BakedTextureIndex
{
    BAKED_EYES("BAKED_EYES", 3, AvatarTextureFaceIndex.TEX_EYES_BAKED), 
    BAKED_HAIR("BAKED_HAIR", 5, AvatarTextureFaceIndex.TEX_HAIR_BAKED), 
    BAKED_HEAD("BAKED_HEAD", 0, AvatarTextureFaceIndex.TEX_HEAD_BAKED), 
    BAKED_LOWER("BAKED_LOWER", 2, AvatarTextureFaceIndex.TEX_LOWER_BAKED), 
    BAKED_SKIRT("BAKED_SKIRT", 4, AvatarTextureFaceIndex.TEX_SKIRT_BAKED), 
    BAKED_UPPER("BAKED_UPPER", 1, AvatarTextureFaceIndex.TEX_UPPER_BAKED);
    
    private AvatarTextureFaceIndex faceIndex;
    
    private BakedTextureIndex(final String name, final int ordinal, final AvatarTextureFaceIndex faceIndex) {
        this.faceIndex = faceIndex;
    }
    
    public AvatarTextureFaceIndex getFaceIndex() {
        return this.faceIndex;
    }
}
