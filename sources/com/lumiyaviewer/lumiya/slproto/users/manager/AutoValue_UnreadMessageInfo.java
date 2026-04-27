// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadMessageInfo

final class AutoValue_UnreadMessageInfo extends UnreadMessageInfo
{

    private final SLChatEvent lastMessage;
    private final int unreadCount;

    AutoValue_UnreadMessageInfo(int i, SLChatEvent slchatevent)
    {
        unreadCount = i;
        lastMessage = slchatevent;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof UnreadMessageInfo)
        {
            obj = (UnreadMessageInfo)obj;
            if (unreadCount == ((UnreadMessageInfo) (obj)).unreadCount())
            {
                if (lastMessage == null)
                {
                    return ((UnreadMessageInfo) (obj)).lastMessage() == null;
                } else
                {
                    return lastMessage.equals(((UnreadMessageInfo) (obj)).lastMessage());
                }
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
        int j = unreadCount;
        int i;
        if (lastMessage == null)
        {
            i = 0;
        } else
        {
            i = lastMessage.hashCode();
        }
        return i ^ 0xf4243 * (j ^ 0xf4243);
    }

    public SLChatEvent lastMessage()
    {
        return lastMessage;
    }

    public String toString()
    {
        return (new StringBuilder()).append("UnreadMessageInfo{unreadCount=").append(unreadCount).append(", ").append("lastMessage=").append(lastMessage).append("}").toString();
    }

    public int unreadCount()
    {
        return unreadCount;
    }
}
