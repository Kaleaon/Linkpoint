// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatLureRequestEvent extends SLChatYesNoEvent
{

    public SLChatLureRequestEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
    }

    public SLChatLureRequestEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, null);
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.LureRequest;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f090346);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f090344);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f090347);
    }

    public String getText(Context context, UserManager usermanager)
    {
        context = context.getString(0x7f090345);
        if (!Strings.isNullOrEmpty(text))
        {
            return (new StringBuilder()).append(context).append(": ").append(text).toString();
        } else
        {
            return (new StringBuilder()).append(context).append(".").toString();
        }
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f090348);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f090343);
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return true;
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        UUID uuid = source.getSourceUUID();
        SLAgentCircuit slagentcircuit = usermanager.getActiveAgentCircuit();
        if (uuid != null && slagentcircuit != null)
        {
            String s = slagentcircuit.getRegionName();
            usermanager = s;
            if (Strings.isNullOrEmpty(s))
            {
                usermanager = context.getString(0x7f090362);
            }
            slagentcircuit.OfferTeleport(uuid, context.getString(0x7f09018e, new Object[] {
                usermanager
            }));
        }
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
    }
}
