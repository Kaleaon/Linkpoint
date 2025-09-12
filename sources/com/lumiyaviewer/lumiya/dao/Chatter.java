// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class Chatter
{

    private boolean active;
    private Long id;
    private Long lastMessageID;
    private UUID lastSessionID;
    private boolean muted;
    private int type;
    private int unreadCount;
    private UUID uuid;

    public Chatter()
    {
    }

    public Chatter(Long long1)
    {
        id = long1;
    }

    public Chatter(Long long1, int i, UUID uuid1, boolean flag, boolean flag1, int j, Long long2, 
            UUID uuid2)
    {
        id = long1;
        type = i;
        uuid = uuid1;
        active = flag;
        muted = flag1;
        unreadCount = j;
        lastMessageID = long2;
        lastSessionID = uuid2;
    }

    public boolean getActive()
    {
        return active;
    }

    public Long getId()
    {
        return id;
    }

    public Long getLastMessageID()
    {
        return lastMessageID;
    }

    public UUID getLastSessionID()
    {
        return lastSessionID;
    }

    public boolean getMuted()
    {
        return muted;
    }

    public int getType()
    {
        return type;
    }

    public int getUnreadCount()
    {
        return unreadCount;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public void setActive(boolean flag)
    {
        active = flag;
    }

    public void setId(Long long1)
    {
        id = long1;
    }

    public void setLastMessageID(Long long1)
    {
        lastMessageID = long1;
    }

    public void setLastSessionID(UUID uuid1)
    {
        lastSessionID = uuid1;
    }

    public void setMuted(boolean flag)
    {
        muted = flag;
    }

    public void setType(int i)
    {
        type = i;
    }

    public void setUnreadCount(int i)
    {
        unreadCount = i;
    }

    public void setUuid(UUID uuid1)
    {
        uuid = uuid1;
    }
}
