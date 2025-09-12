package com.lumiyaviewer.lumiya.slproto.avatar;

public enum BakedTextureIndex {
    BAKED_HEAD(AvatarTextureFaceIndex.TEX_HEAD_BAKED),
    BAKED_UPPER(AvatarTextureFaceIndex.TEX_UPPER_BAKED),
    BAKED_LOWER(AvatarTextureFaceIndex.TEX_LOWER_BAKED),
    BAKED_EYES(AvatarTextureFaceIndex.TEX_EYES_BAKED),
    BAKED_SKIRT(AvatarTextureFaceIndex.TEX_SKIRT_BAKED),
    BAKED_HAIR(AvatarTextureFaceIndex.TEX_HAIR_BAKED);
    
    private AvatarTextureFaceIndex faceIndex;

    private BakedTextureIndex(AvatarTextureFaceIndex avatarTextureFaceIndex) {
        this.faceIndex = avatarTextureFaceIndex;
    }

    public AvatarTextureFaceIndex getFaceIndex() {
        return this.faceIndex;
    }
}
