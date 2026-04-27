// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;

import com.google.common.collect.ImmutableList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.avatar:
//            SLAvatarParams, SLVisualParamID

public static class params
{

    public final int appearanceIndex;
    public final int id;
    public final SLVisualParamID name;
    public final ImmutableList params;

    (int i, int j, SLVisualParamID slvisualparamid, ImmutableList immutablelist)
    {
        id = i;
        appearanceIndex = j;
        name = slvisualparamid;
        params = immutablelist;
    }
}
