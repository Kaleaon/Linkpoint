// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

final class AutoValue_GroupManager_GroupRoleMembersQuery extends GroupManager.GroupRoleMembersQuery
{

    private final UUID groupID;
    private final UUID requestID;
    private final UUID roleID;

    AutoValue_GroupManager_GroupRoleMembersQuery(UUID uuid, UUID uuid1, UUID uuid2)
    {
        if (uuid == null)
        {
            throw new NullPointerException("Null groupID");
        }
        groupID = uuid;
        if (uuid1 == null)
        {
            throw new NullPointerException("Null roleID");
        }
        roleID = uuid1;
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
        if (obj instanceof GroupManager.GroupRoleMembersQuery)
        {
            obj = (GroupManager.GroupRoleMembersQuery)obj;
            boolean flag = flag1;
            if (groupID.equals(((GroupManager.GroupRoleMembersQuery) (obj)).groupID()))
            {
                flag = flag1;
                if (roleID.equals(((GroupManager.GroupRoleMembersQuery) (obj)).roleID()))
                {
                    flag = requestID.equals(((GroupManager.GroupRoleMembersQuery) (obj)).requestID());
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
        return ((groupID.hashCode() ^ 0xf4243) * 0xf4243 ^ roleID.hashCode()) * 0xf4243 ^ requestID.hashCode();
    }

    public UUID requestID()
    {
        return requestID;
    }

    public UUID roleID()
    {
        return roleID;
    }

    public String toString()
    {
        return (new StringBuilder()).append("GroupRoleMembersQuery{groupID=").append(groupID).append(", ").append("roleID=").append(roleID).append(", ").append("requestID=").append(requestID).append("}").toString();
    }
}
