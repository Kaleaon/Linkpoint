// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import java.util.UUID;

public class GroupRoleMember
{

    private UUID groupID;
    private UUID requestID;
    private UUID roleID;
    private UUID userID;

    public GroupRoleMember()
    {
    }

    public GroupRoleMember(UUID uuid, UUID uuid1, UUID uuid2, UUID uuid3)
    {
        groupID = uuid;
        requestID = uuid1;
        roleID = uuid2;
        userID = uuid3;
    }

    public UUID getGroupID()
    {
        return groupID;
    }

    public UUID getRequestID()
    {
        return requestID;
    }

    public UUID getRoleID()
    {
        return roleID;
    }

    public UUID getUserID()
    {
        return userID;
    }

    public void setGroupID(UUID uuid)
    {
        groupID = uuid;
    }

    public void setRequestID(UUID uuid)
    {
        requestID = uuid;
    }

    public void setRoleID(UUID uuid)
    {
        roleID = uuid;
    }

    public void setUserID(UUID uuid)
    {
        userID = uuid;
    }
}
