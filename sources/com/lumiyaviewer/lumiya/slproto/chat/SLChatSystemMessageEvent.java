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

public final class SLChatSystemMessageEvent extends SLChatEvent
{

    private final String text;

    public SLChatSystemMessageEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        text = chatmessage.getMessageText();
    }

    public SLChatSystemMessageEvent(ChatMessageSource chatmessagesource, UUID uuid, String s)
    {
        super(chatmessagesource, uuid);
        text = s;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.SystemMessage;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        return text;
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_PLAIN;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return true;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setMessageText(text);
    }
}
