// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatOnlineOfflineEvent extends SLChatEvent
{

    private final boolean wentOnline;

    public SLChatOnlineOfflineEvent(ChatMessage chatmessage, UUID uuid, boolean flag)
    {
        super(chatmessage, uuid);
        wentOnline = flag;
    }

    public SLChatOnlineOfflineEvent(ChatMessageSource chatmessagesource, UUID uuid, boolean flag)
    {
        super(chatmessagesource, uuid);
        wentOnline = flag;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        if (wentOnline)
        {
            return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.WentOnline;
        } else
        {
            return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.WentOffline;
        }
    }

    protected String getText(Context context, UserManager usermanager)
    {
        int i;
        if (wentOnline)
        {
            i = 0x7f090389;
        } else
        {
            i = 0x7f090388;
        }
        return context.getString(i);
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return true;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
    }
}
