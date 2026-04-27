// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

final class AutoValue_GroupManager_GroupMemberRolesQuery extends GroupManager.GroupMemberRolesQuery
{

    private final UUID groupID;
    private final UUID memberID;
    private final UUID requestID;

    AutoValue_GroupManager_GroupMemberRolesQuery(UUID uuid, UUID uuid1, UUID uuid2)
    {
        if (uuid == null)
        {
            throw new NullPointerException("Null groupID");
        }
        groupID = uuid;
        if (uuid1 == null)
        {
            throw new NullPointerException("Null memberID");
        }
        memberID = uuid1;
        if (uuid2 == null)
        {
            throw new NullPointerException("Null requestID");
        } else
        {
            requestID = uuid2;
            return;
        }
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof GroupManager.GroupMemberRolesQuery)
        {
            obj = (GroupManager.GroupMemberRolesQuery)obj;
            boolean flag = flag1;
            if (groupID.equals(((GroupManager.GroupMemberRolesQuery) (obj)).groupID()))
            {
                flag = flag1;
                if (memberID.equals(((GroupManager.GroupMemberRolesQuery) (obj)).memberID()))
                {
                    flag = requestID.equals(((GroupManager.GroupMemberRolesQuery) (obj)).requestID());
                }
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
        return ((groupID.hashCode() ^ 0xf4243) * 0xf4243 ^ memberID.hashCode()) * 0xf4243 ^ requestID.hashCode();
    }

    public UUID memberID()
    {
        return memberID;
    }

    public UUID requestID()
    {
        return requestID;
    }

    public String toString()
    {
        return (new StringBuilder()).append("GroupMemberRolesQuery{groupID=").append(groupID).append(", ").append("memberID=").append(memberID).append(", ").append("requestID=").append(requestID).append("}").toString();
    }
}
