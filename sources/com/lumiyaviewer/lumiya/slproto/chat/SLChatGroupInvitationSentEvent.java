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

public final class SLChatGroupInvitationSentEvent extends SLChatEvent
{

    public SLChatGroupInvitationSentEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
    }

    public SLChatGroupInvitationSentEvent(ChatMessageSource chatmessagesource, UUID uuid)
    {
        super(chatmessagesource, uuid);
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.GroupInvitationSent;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        usermanager = source.getSourceName(usermanager);
        if (usermanager == null)
        {
            usermanager = "(unknown)";
        }
        return context.getString(0x7f09017e, new Object[] {
            usermanager
        });
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
    }
}
