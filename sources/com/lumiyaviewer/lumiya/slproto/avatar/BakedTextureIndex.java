// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            AvatarTextureFaceIndex

public final class BakedTextureIndex extends Enum
{

    private static final BakedTextureIndex $VALUES[];
    public static final BakedTextureIndex BAKED_EYES;
    public static final BakedTextureIndex BAKED_HAIR;
    public static final BakedTextureIndex BAKED_HEAD;
    public static final BakedTextureIndex BAKED_LOWER;
    public static final BakedTextureIndex BAKED_SKIRT;
    public static final BakedTextureIndex BAKED_UPPER;
    private AvatarTextureFaceIndex faceIndex;

    private BakedTextureIndex(String s, int i, AvatarTextureFaceIndex avatartexturefaceindex)
    {
        super(s, i);
        faceIndex = avatartexturefaceindex;
    }

    public static BakedTextureIndex valueOf(String s)
    {
        return (BakedTextureIndex)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/avatar/BakedTextureIndex, s);
    }

    public static BakedTextureIndex[] values()
    {
        return $VALUES;
    }

    public AvatarTextureFaceIndex getFaceIndex()
    {
        return faceIndex;
    }

    static 
    {
        BAKED_HEAD = new BakedTextureIndex("BAKED_HEAD", 0, AvatarTextureFaceIndex.TEX_HEAD_BAKED);
        BAKED_UPPER = new BakedTextureIndex("BAKED_UPPER", 1, AvatarTextureFaceIndex.TEX_UPPER_BAKED);
        BAKED_LOWER = new BakedTextureIndex("BAKED_LOWER", 2, AvatarTextureFaceIndex.TEX_LOWER_BAKED);
        BAKED_EYES = new BakedTextureIndex("BAKED_EYES", 3, AvatarTextureFaceIndex.TEX_EYES_BAKED);
        BAKED_SKIRT = new BakedTextureIndex("BAKED_SKIRT", 4, AvatarTextureFaceIndex.TEX_SKIRT_BAKED);
        BAKED_HAIR = new BakedTextureIndex("BAKED_HAIR", 5, AvatarTextureFaceIndex.TEX_HAIR_BAKED);
        $VALUES = (new BakedTextureIndex[] {
            BAKED_HEAD, BAKED_UPPER, BAKED_LOWER, BAKED_EYES, BAKED_SKIRT, BAKED_HAIR
        });
    }
}
