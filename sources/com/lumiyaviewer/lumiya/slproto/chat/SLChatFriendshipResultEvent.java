// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatFriendshipResultEvent extends SLChatEvent
{

    private final boolean accepted;

    public SLChatFriendshipResultEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        accepted = chatmessage.getAccepted().booleanValue();
    }

    public SLChatFriendshipResultEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage)
    {
        super(improvedinstantmessage, uuid, chatmessagesource);
        boolean flag;
        if (improvedinstantmessage.MessageBlock_Field.Dialog == 39)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        accepted = flag;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.FriendshipResult;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        int i;
        if (accepted)
        {
            i = 0x7f090126;
        } else
        {
            i = 0x7f090127;
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
        chatmessage.setAccepted(Boolean.valueOf(accepted));
    }
}
