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
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import java.util.UUID;

public final class SLChatLureEvent extends SLChatYesNoEvent
{

    private final UUID lureID;

    public SLChatLureEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        lureID = chatmessage.getSessionID();
    }

    public SLChatLureEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, null);
        lureID = improvedinstantmessage.MessageBlock_Field.ID;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.Lure;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f090341);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f090340);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f090342);
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f090349);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f09033f);
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        SLAgentCircuit slagentcircuit = usermanager.getActiveAgentCircuit();
        if (slagentcircuit != null)
        {
            (new TeleportProgressDialog(context, usermanager, 0x7f090350)).show();
            slagentcircuit.TeleportToLure(lureID);
        }
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setSessionID(lureID);
    }
}
