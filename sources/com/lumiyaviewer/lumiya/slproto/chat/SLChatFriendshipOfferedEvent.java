// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatFriendshipOfferedEvent extends SLChatYesNoEvent
{

    public final UUID sessionID;

    public SLChatFriendshipOfferedEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        sessionID = chatmessage.getSessionID();
    }

    public SLChatFriendshipOfferedEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, null);
        sessionID = improvedinstantmessage.MessageBlock_Field.ID;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.FriendshipOffered;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f09012a);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f090129);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f09012b);
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f09012c);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f090128);
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        context = source.getSourceUUID();
        usermanager = usermanager.getActiveAgentCircuit();
        if (context != null && usermanager != null)
        {
            usermanager.AcceptFriendship(context, sessionID);
        }
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setSessionID(sessionID);
    }
}
