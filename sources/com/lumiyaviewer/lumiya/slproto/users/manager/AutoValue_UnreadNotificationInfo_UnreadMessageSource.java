// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

final class AutoValue_UnreadNotificationInfo_UnreadMessageSource extends UnreadNotificationInfo.UnreadMessageSource
{

    private final ChatterID chatterID;
    private final Optional chatterName;
    private final ImmutableList unreadMessages;
    private final int unreadMessagesCount;

    AutoValue_UnreadNotificationInfo_UnreadMessageSource(ChatterID chatterid, Optional optional, ImmutableList immutablelist, int i)
    {
        if (chatterid == null)
        {
            throw new NullPointerException("Null chatterID");
        }
        chatterID = chatterid;
        if (optional == null)
        {
            throw new NullPointerException("Null chatterName");
        }
        chatterName = optional;
        if (immutablelist == null)
        {
            throw new NullPointerException("Null unreadMessages");
        } else
        {
            unreadMessages = immutablelist;
            unreadMessagesCount = i;
            return;
        }
    }

    public ChatterID chatterID()
    {
        return chatterID;
    }

    public Optional chatterName()
    {
        return chatterName;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof UnreadNotificationInfo.UnreadMessageSource)
        {
            obj = (UnreadNotificationInfo.UnreadMessageSource)obj;
            if (chatterID.equals(((UnreadNotificationInfo.UnreadMessageSource) (obj)).chatterID()) && chatterName.equals(((UnreadNotificationInfo.UnreadMessageSource) (obj)).chatterName()) && unreadMessages.equals(((UnreadNotificationInfo.UnreadMessageSource) (obj)).unreadMessages()))
            {
                return unreadMessagesCount == ((UnreadNotificationInfo.UnreadMessageSource) (obj)).unreadMessagesCount();
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (((chatterID.hashCode() ^ 0xf4243) * 0xf4243 ^ chatterName.hashCode()) * 0xf4243 ^ unreadMessages.hashCode()) * 0xf4243 ^ unreadMessagesCount;
    }

    public String toString()
    {
        return (new StringBuilder()).append("UnreadMessageSource{chatterID=").append(chatterID).append(", ").append("chatterName=").append(chatterName).append(", ").append("unreadMessages=").append(unreadMessages).append(", ").append("unreadMessagesCount=").append(unreadMessagesCount).append("}").toString();
    }

    public ImmutableList unreadMessages()
    {
        return unreadMessages;
    }

    public int unreadMessagesCount()
    {
        return unreadMessagesCount;
    }
}
