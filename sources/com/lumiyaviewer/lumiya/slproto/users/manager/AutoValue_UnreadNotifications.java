// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableMap;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotifications

final class AutoValue_UnreadNotifications extends UnreadNotifications
{

    private final UUID agentUUID;
    private final ImmutableMap notificationGroups;

    AutoValue_UnreadNotifications(UUID uuid, ImmutableMap immutablemap)
    {
        if (uuid == null)
        {
            throw new NullPointerException("Null agentUUID");
        }
        agentUUID = uuid;
        if (immutablemap == null)
        {
            throw new NullPointerException("Null notificationGroups");
        } else
        {
            notificationGroups = immutablemap;
            return;
        }
    }

    public UUID agentUUID()
    {
        return agentUUID;
    }

    public boolean equals(Object obj)
    {
        boolean flag = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof UnreadNotifications)
        {
            obj = (UnreadNotifications)obj;
            if (agentUUID.equals(((UnreadNotifications) (obj)).agentUUID()))
            {
                flag = notificationGroups.equals(((UnreadNotifications) (obj)).notificationGroups());
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (agentUUID.hashCode() ^ 0xf4243) * 0xf4243 ^ notificationGroups.hashCode();
    }

    public ImmutableMap notificationGroups()
    {
        return notificationGroups;
    }

    public String toString()
    {
        return (new StringBuilder()).append("UnreadNotifications{agentUUID=").append(agentUUID).append(", ").append("notificationGroups=").append(notificationGroups).append("}").toString();
    }
}
