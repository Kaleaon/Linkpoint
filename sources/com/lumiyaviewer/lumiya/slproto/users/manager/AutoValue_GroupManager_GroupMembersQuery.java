// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

final class AutoValue_GroupManager_GroupMembersQuery extends GroupManager.GroupMembersQuery
{

    private final UUID groupID;
    private final UUID requestID;

    AutoValue_GroupManager_GroupMembersQuery(UUID uuid, UUID uuid1)
    {
        if (uuid == null)
        {
            throw new NullPointerException("Null groupID");
        }
        groupID = uuid;
        if (uuid1 == null)
        {
            throw new NullPointerException("Null requestID");
        } else
        {
            requestID = uuid1;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        boolean flag = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof GroupManager.GroupMembersQuery)
        {
            obj = (GroupManager.GroupMembersQuery)obj;
            if (groupID.equals(((GroupManager.GroupMembersQuery) (obj)).groupID()))
            {
                flag = requestID.equals(((GroupManager.GroupMembersQuery) (obj)).requestID());
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public UUID groupID()
    {
        return groupID;
    }

    public int hashCode()
    {
        return (groupID.hashCode() ^ 0xf4243) * 0xf4243 ^ requestID.hashCode();
    }

    public UUID requestID()
    {
        return requestID;
    }

    public String toString()
    {
        return (new StringBuilder()).append("GroupMembersQuery{groupID=").append(groupID).append(", ").append("requestID=").append(requestID).append("}").toString();
    }
}
