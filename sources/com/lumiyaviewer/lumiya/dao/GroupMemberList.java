// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupMemberList
{

    private UUID groupID;
    private UUID requestID;

    public GroupMemberList()
    {
    }

    public GroupMemberList(UUID uuid)
    {
        groupID = uuid;
    }

    public GroupMemberList(UUID uuid, UUID uuid1)
    {
        groupID = uuid;
        requestID = uuid1;
    }

    public UUID getGroupID()
    {
        return groupID;
    }

    public UUID getRequestID()
    {
        return requestID;
    }

    public void setGroupID(UUID uuid)
    {
        groupID = uuid;
    }

    public void setRequestID(UUID uuid)
    {
        requestID = uuid;
    }
}
