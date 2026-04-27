// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupMember
{

    private long agentPowers;
    private int contribution;
    private UUID groupID;
    private boolean isOwner;
    private String onlineStatus;
    private UUID requestID;
    private String title;
    private UUID userID;

    public GroupMember()
    {
    }

    public GroupMember(UUID uuid, UUID uuid1, UUID uuid2, int i, String s, long l, 
            String s1, boolean flag)
    {
        groupID = uuid;
        requestID = uuid1;
        userID = uuid2;
        contribution = i;
        onlineStatus = s;
        agentPowers = l;
        title = s1;
        isOwner = flag;
    }

    public long getAgentPowers()
    {
        return agentPowers;
    }

    public int getContribution()
    {
        return contribution;
    }

    public UUID getGroupID()
    {
        return groupID;
    }

    public boolean getIsOwner()
    {
        return isOwner;
    }

    public String getOnlineStatus()
    {
        return onlineStatus;
    }

    public UUID getRequestID()
    {
        return requestID;
    }

    public String getTitle()
    {
        return title;
    }

    public UUID getUserID()
    {
        return userID;
    }

    public void setAgentPowers(long l)
    {
        agentPowers = l;
    }

    public void setContribution(int i)
    {
        contribution = i;
    }

    public void setGroupID(UUID uuid)
    {
        groupID = uuid;
    }

    public void setIsOwner(boolean flag)
    {
        isOwner = flag;
    }

    public void setOnlineStatus(String s)
    {
        onlineStatus = s;
    }

    public void setRequestID(UUID uuid)
    {
        requestID = uuid;
    }

    public void setTitle(String s)
    {
        title = s;
    }

    public void setUserID(UUID uuid)
    {
        userID = uuid;
    }
}
