// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            AutoValue_UnreadMessageInfo

public abstract class UnreadMessageInfo
{

    public UnreadMessageInfo()
    {
    }

    public static UnreadMessageInfo create(int i, SLChatEvent slchatevent)
    {
        return new AutoValue_UnreadMessageInfo(i, slchatevent);
    }

    public abstract SLChatEvent lastMessage();

    public abstract int unreadCount();
}
