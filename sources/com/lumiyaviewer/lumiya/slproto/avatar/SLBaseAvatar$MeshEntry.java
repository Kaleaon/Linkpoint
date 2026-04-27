// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLBaseAvatar, SLPolyMesh, AvatarTextureFaceIndex, BakedTextureIndex

public static class polyMesh
{

    public String meshName;
    public SLPolyMesh polyMesh;
    public AvatarTextureFaceIndex textureFaceIndex;
    public BakedTextureIndex textureIndex;

    public (BakedTextureIndex bakedtextureindex, AvatarTextureFaceIndex avatartexturefaceindex, String s, SLPolyMesh slpolymesh)
    {
        textureIndex = bakedtextureindex;
        textureFaceIndex = avatartexturefaceindex;
        meshName = s;
        polyMesh = slpolymesh;
    }
}
