// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.voice.SLVoice;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLMissedVoiceCallEvent extends SLChatYesNoEvent
{

    public SLMissedVoiceCallEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
    }

    public SLMissedVoiceCallEvent(ChatMessageSource chatmessagesource, UUID uuid, String s)
    {
        super(chatmessagesource, uuid, s);
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.MissedVoiceCall;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f0901bc);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f0901bb);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f0901bd);
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f0901be);
    }

    public String getYesMessage(Context context)
    {
        return "";
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        context = usermanager.getActiveAgentCircuit();
        if (context != null)
        {
            context = context.getModules();
            if (context != null)
            {
                ((SLModules) (context)).voice.userVoiceChatRequest(source.getSourceUUID());
            }
        }
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
    }
}
