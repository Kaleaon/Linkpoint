// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupRoleMemberList
{

    private UUID groupID;
    private boolean mustRevalidate;
    private UUID requestID;

    public GroupRoleMemberList()
    {
    }

    public GroupRoleMemberList(UUID uuid)
    {
        groupID = uuid;
    }

    public GroupRoleMemberList(UUID uuid, UUID uuid1, boolean flag)
    {
        groupID = uuid;
        requestID = uuid1;
        mustRevalidate = flag;
    }

    public UUID getGroupID()
    {
        return groupID;
    }

    public boolean getMustRevalidate()
    {
        return mustRevalidate;
    }

    public UUID getRequestID()
    {
        return requestID;
    }

    public void setGroupID(UUID uuid)
    {
        groupID = uuid;
    }

    public void setMustRevalidate(boolean flag)
    {
        mustRevalidate = flag;
    }

    public void setRequestID(UUID uuid)
    {
        requestID = uuid;
    }
}
