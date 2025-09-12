// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.text.DateFormat;
import java.util.UUID;

public class SLChatSessionMarkEvent extends SLChatEvent
{
    public static final class SessionMarkType extends Enum
    {

        private static final SessionMarkType $VALUES[];
        public static final SessionMarkType NewSession;
        public static final SessionMarkType Teleport;

        public static SessionMarkType valueOf(String s)
        {
            return (SessionMarkType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/SLChatSessionMarkEvent$SessionMarkType, s);
        }

        public static SessionMarkType[] values()
        {
            return $VALUES;
        }

        static 
        {
            NewSession = new SessionMarkType("NewSession", 0);
            Teleport = new SessionMarkType("Teleport", 1);
            $VALUES = (new SessionMarkType[] {
                NewSession, Teleport
            });
        }

        private SessionMarkType(String s, int i)
        {
            super(s, i);
        }
    }


    private final String description;
    private final SessionMarkType sessionMarkType;

    public SLChatSessionMarkEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        sessionMarkType = SessionMarkType.values()[chatmessage.getChatChannel().intValue()];
        description = chatmessage.getMessageText();
    }

    public SLChatSessionMarkEvent(ChatMessageSource chatmessagesource, UUID uuid, SessionMarkType sessionmarktype, String s)
    {
        super(chatmessagesource, uuid);
        sessionMarkType = sessionmarktype;
        description = s;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.SessionMark;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        if (sessionMarkType == SessionMarkType.Teleport)
        {
            return context.getString(0x7f09033b, new Object[] {
                description
            });
        } else
        {
            return context.getString(0x7f0901dc, new Object[] {
                DateFormat.getDateTimeInstance(3, 3).format(getTimestamp())
            });
        }
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_SESSION_MARK;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return true;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setMessageText(description);
        chatmessage.setChatChannel(Integer.valueOf(sessionMarkType.ordinal()));
    }
}
