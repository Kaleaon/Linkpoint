// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public class SLChatTextEvent extends SLChatEvent
{

    protected final String text;

    public SLChatTextEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        text = chatmessage.getMessageText();
    }

    public SLChatTextEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage, String s)
    {
        super(improvedinstantmessage, uuid, chatmessagesource);
        if (s != null)
        {
            text = s;
            return;
        }
        if (improvedinstantmessage != null)
        {
            text = SLMessage.stringFromVariableUTF(improvedinstantmessage.MessageBlock_Field.Message);
            return;
        } else
        {
            text = null;
            return;
        }
    }

    public SLChatTextEvent(ChatMessageSource chatmessagesource, UUID uuid, LoadURL loadurl)
    {
        super(chatmessagesource, uuid);
        text = (new StringBuilder()).append(SLMessage.stringFromVariableUTF(loadurl.Data_Field.Message)).append(": ").append(SLMessage.stringFromVariableUTF(loadurl.Data_Field.URL)).toString();
    }

    public SLChatTextEvent(ChatMessageSource chatmessagesource, UUID uuid, String s)
    {
        super(chatmessagesource, uuid);
        text = s;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.Text;
    }

    public String getRawText()
    {
        if (text != null && text.startsWith("/me "))
        {
            return text.substring(4);
        } else
        {
            return text;
        }
    }

    public String getText(Context context, UserManager usermanager)
    {
        if (text != null && text.startsWith("/me "))
        {
            return text.substring(4);
        } else
        {
            return text;
        }
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return text != null && text.startsWith("/me ");
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setMessageText(text);
    }
}
