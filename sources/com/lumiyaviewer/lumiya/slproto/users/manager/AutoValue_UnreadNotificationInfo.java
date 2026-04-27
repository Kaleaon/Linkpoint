// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotificationInfo

final class AutoValue_UnreadNotificationInfo extends UnreadNotificationInfo
{

    private final UUID agentUUID;
    private final int freshMessagesCount;
    private final Optional mostImportantFreshType;
    private final Optional mostImportantType;
    private final UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo;
    private final Optional singleFreshSource;
    private final int totalUnreadCount;
    private final ImmutableList unreadSources;

    AutoValue_UnreadNotificationInfo(UUID uuid, int i, ImmutableList immutablelist, Optional optional, int j, Optional optional1, Optional optional2, 
            UnreadNotificationInfo.ObjectPopupNotification objectpopupnotification)
    {
        if (uuid == null)
        {
            throw new NullPointerException("Null agentUUID");
        }
        agentUUID = uuid;
        totalUnreadCount = i;
        if (immutablelist == null)
        {
            throw new NullPointerException("Null unreadSources");
        }
        unreadSources = immutablelist;
        if (optional == null)
        {
            throw new NullPointerException("Null mostImportantType");
        }
        mostImportantType = optional;
        freshMessagesCount = j;
        if (optional1 == null)
        {
            throw new NullPointerException("Null mostImportantFreshType");
        }
        mostImportantFreshType = optional1;
        if (optional2 == null)
        {
            throw new NullPointerException("Null singleFreshSource");
        }
        singleFreshSource = optional2;
        if (objectpopupnotification == null)
        {
            throw new NullPointerException("Null objectPopupInfo");
        } else
        {
            objectPopupInfo = objectpopupnotification;
            return;
        }
    }

    public UUID agentUUID()
    {
        return agentUUID;
    }

    public boolean equals(Object obj)
    {
        boolean flag1 = false;
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof UnreadNotificationInfo)
        {
            obj = (UnreadNotificationInfo)obj;
            boolean flag = flag1;
            if (agentUUID.equals(((UnreadNotificationInfo) (obj)).agentUUID()))
            {
                flag = flag1;
                if (totalUnreadCount == ((UnreadNotificationInfo) (obj)).totalUnreadCount())
                {
                    flag = flag1;
                    if (unreadSources.equals(((UnreadNotificationInfo) (obj)).unreadSources()))
                    {
                        flag = flag1;
                        if (mostImportantType.equals(((UnreadNotificationInfo) (obj)).mostImportantType()))
                        {
                            flag = flag1;
                            if (freshMessagesCount == ((UnreadNotificationInfo) (obj)).freshMessagesCount())
                            {
                                flag = flag1;
                                if (mostImportantFreshType.equals(((UnreadNotificationInfo) (obj)).mostImportantFreshType()))
                                {
                                    flag = flag1;
                                    if (singleFreshSource.equals(((UnreadNotificationInfo) (obj)).singleFreshSource()))
                                    {
                                        flag = objectPopupInfo.equals(((UnreadNotificationInfo) (obj)).objectPopupInfo());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return flag;
        } else
        {
            return false;
        }
    }

    public int freshMessagesCount()
    {
        return freshMessagesCount;
    }

    public int hashCode()
    {
        return (((((((agentUUID.hashCode() ^ 0xf4243) * 0xf4243 ^ totalUnreadCount) * 0xf4243 ^ unreadSources.hashCode()) * 0xf4243 ^ mostImportantType.hashCode()) * 0xf4243 ^ freshMessagesCount) * 0xf4243 ^ mostImportantFreshType.hashCode()) * 0xf4243 ^ singleFreshSource.hashCode()) * 0xf4243 ^ objectPopupInfo.hashCode();
    }

    public Optional mostImportantFreshType()
    {
        return mostImportantFreshType;
    }

    public Optional mostImportantType()
    {
        return mostImportantType;
    }

    public UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo()
    {
        return objectPopupInfo;
    }

    public Optional singleFreshSource()
    {
        return singleFreshSource;
    }

    public String toString()
    {
        return (new StringBuilder()).append("UnreadNotificationInfo{agentUUID=").append(agentUUID).append(", ").append("totalUnreadCount=").append(totalUnreadCount).append(", ").append("unreadSources=").append(unreadSources).append(", ").append("mostImportantType=").append(mostImportantType).append(", ").append("freshMessagesCount=").append(freshMessagesCount).append(", ").append("mostImportantFreshType=").append(mostImportantFreshType).append(", ").append("singleFreshSource=").append(singleFreshSource).append(", ").append("objectPopupInfo=").append(objectPopupInfo).append("}").toString();
    }

    public int totalUnreadCount()
    {
        return totalUnreadCount;
    }

    public ImmutableList unreadSources()
    {
        return unreadSources;
    }
}
