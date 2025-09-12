// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.slproto.assets.SLWearableType;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLAvatarAppearance

public static class isTouchable
{

    private final int attachedTo;
    private final boolean isTouchable;
    private final UUID itemID;
    private final String name;
    private final int objectLocalID;
    private final SLWearableType wornOn;

    static int _2D_get0(isTouchable istouchable)
    {
        return istouchable.objectLocalID;
    }

    int getAttachedTo()
    {
        return attachedTo;
    }

    public boolean getIsTouchable()
    {
        return isTouchable;
    }

    public String getName()
    {
        return name;
    }

    public int getObjectLocalID()
    {
        return objectLocalID;
    }

    public SLWearableType getWornOn()
    {
        return wornOn;
    }

    public UUID itemID()
    {
        return itemID;
    }

    I(SLWearableType slwearabletype, int i, UUID uuid, String s, int j, boolean flag)
    {
        wornOn = slwearabletype;
        attachedTo = i;
        itemID = uuid;
        name = s;
        objectLocalID = j;
        isTouchable = flag;
    }
}
