// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLMinimap

public static class distance
{

    public final ChatterID chatterID;
    public volatile float distance;
    public volatile ImmutableVector location;

    (ChatterID chatterid, ImmutableVector immutablevector)
    {
        chatterID = chatterid;
        location = immutablevector;
        distance = (0.0F / 0.0F);
    }
}
