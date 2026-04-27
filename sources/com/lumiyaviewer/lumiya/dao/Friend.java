// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class Friend
{

    public static final int GRANT_MAP_LOCATION = 2;
    public static final int GRANT_MODIFY_OBJECTS = 4;
    public static final int GRANT_ONLINE_STATUS = 1;
    private boolean isOnline;
    private int rightsGiven;
    private int rightsHas;
    private UUID uuid;

    public Friend()
    {
    }

    public Friend(UUID uuid1)
    {
        uuid = uuid1;
    }

    public Friend(UUID uuid1, int i, int j, boolean flag)
    {
        uuid = uuid1;
        rightsGiven = i;
        rightsHas = j;
        isOnline = flag;
    }

    public boolean getIsOnline()
    {
        return isOnline;
    }

    public int getRightsGiven()
    {
        return rightsGiven;
    }

    public int getRightsHas()
    {
        return rightsHas;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setIsOnline(boolean flag)
    {
        isOnline = flag;
    }

    public void setRightsGiven(int i)
    {
        rightsGiven = i;
    }

    public void setRightsHas(int i)
    {
        rightsHas = i;
    }

    public void setUuid(UUID uuid1)
    {
        uuid = uuid1;
    }
}
