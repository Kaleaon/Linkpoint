// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatLureRequestedEvent extends SLChatEvent
{

    private final String message;

    public SLChatLureRequestedEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        message = chatmessage.getMessageText();
    }

    public SLChatLureRequestedEvent(String s, UUID uuid)
    {
        super(ChatMessageSourceUnknown.getInstance(), uuid);
        message = s;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.LureRequested;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        if (Strings.isNullOrEmpty(message))
        {
            return context.getString(0x7f0900b5);
        } else
        {
            return context.getString(0x7f0900b4, new Object[] {
                message
            });
        }
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return false;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setMessageText(message);
    }
}
