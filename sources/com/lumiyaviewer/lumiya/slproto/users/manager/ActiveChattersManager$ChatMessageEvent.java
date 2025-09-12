// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.ChatMessage;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ActiveChattersManager

public static class isPrivate
{

    public final ChatMessage chatMessage;
    public final boolean isNewMessage;
    public final boolean isPrivate;

    (ChatMessage chatmessage, boolean flag, boolean flag1)
    {
        chatMessage = chatmessage;
        isNewMessage = flag;
        isPrivate = flag1;
    }
}
