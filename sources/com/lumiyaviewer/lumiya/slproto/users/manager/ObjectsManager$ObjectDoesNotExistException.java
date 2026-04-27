// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ObjectsManager

public static class <init> extends Exception
{

    private final int localID;
    private final UUID uuid;

    public int getLocalID()
    {
        return localID;
    }

    private (int i)
    {
        localID = i;
        uuid = null;
    }

    uuid(int i, uuid uuid1)
    {
        this(i);
    }

    private <init>(UUID uuid1)
    {
        localID = 0;
        uuid = uuid1;
    }

    uuid(UUID uuid1, uuid uuid2)
    {
        this(uuid1);
    }
}
