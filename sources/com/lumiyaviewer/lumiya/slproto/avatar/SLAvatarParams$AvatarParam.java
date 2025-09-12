// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLAvatarParams, MeshIndex, SLAvatarParamAlpha, SLAvatarParamColor

public static class skeletonParams
{

    public final float defValue;
    public final ImmutableList drivenParams;
    public final float maxValue;
    public final MeshIndex meshIndex;
    public final float minValue;
    public final boolean morph;
    public final SLAvatarParamAlpha paramAlpha;
    public final SLAvatarParamColor paramColor;
    public final ImmutableMap skeletonParams;

    (MeshIndex meshindex, float f, float f1, float f2, boolean flag, SLAvatarParamColor slavatarparamcolor, SLAvatarParamAlpha slavatarparamalpha, 
            ImmutableList immutablelist, ImmutableMap immutablemap)
    {
        meshIndex = meshindex;
        minValue = f;
        maxValue = f1;
        defValue = f2;
        morph = flag;
        paramColor = slavatarparamcolor;
        paramAlpha = slavatarparamalpha;
        drivenParams = immutablelist;
        skeletonParams = immutablemap;
    }
}
