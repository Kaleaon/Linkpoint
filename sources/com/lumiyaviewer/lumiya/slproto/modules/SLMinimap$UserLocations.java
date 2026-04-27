// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.types.ImmutableVector;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLMinimap

public static class userPositions
{

    public final float myAvatarHeading;
    public final ImmutableVector myAvatarPosition;
    public final Map userPositions;

    (ImmutableVector immutablevector, float f, Map map)
    {
        myAvatarPosition = immutablevector;
        myAvatarHeading = f;
        userPositions = map;
    }
}
