// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.baker:
//            BakeLayer

public class BakeLayerSet
{

    public BakedTextureIndex bakedTextureIndex;
    public boolean clear_alpha;
    public int height;
    public BakeLayer layers[];
    public BakeLayer maskLayers[];
    public int width;

    public BakeLayerSet(BakedTextureIndex bakedtextureindex, int i, int j, boolean flag, BakeLayer abakelayer[], BakeLayer abakelayer1[])
    {
        bakedTextureIndex = bakedtextureindex;
        width = i;
        height = j;
        clear_alpha = flag;
        layers = abakelayer;
        maskLayers = abakelayer1;
    }
}
