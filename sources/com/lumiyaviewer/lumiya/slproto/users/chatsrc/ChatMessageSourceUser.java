// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.chatsrc:
//            ChatMessageSource

public class ChatMessageSourceUser extends ChatMessageSource
{

    private String displayName;
    private String legacyName;
    public final UUID uuid;

    ChatMessageSourceUser(ChatMessage chatmessage)
    {
        uuid = chatmessage.getSenderUUID();
        displayName = chatmessage.getSenderName();
        legacyName = chatmessage.getSenderLegacyName();
    }

    public ChatMessageSourceUser(UUID uuid1)
    {
        uuid = uuid1;
        displayName = null;
        legacyName = null;
    }

    public ChatterID getDefaultChatter(UUID uuid1)
    {
        return ChatterID.getUserChatterID(uuid1, uuid);
    }

    public String getSourceName(UserManager usermanager)
    {
        if (GlobalOptions.getInstance().isLegacyUserNames())
        {
            return legacyName;
        } else
        {
            return displayName;
        }
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType()
    {
        return ChatMessageSource.ChatMessageSourceType.User;
    }

    public UUID getSourceUUID()
    {
        return uuid;
    }

    public void serializeTo(ChatMessage chatmessage)
    {
        super.serializeTo(chatmessage);
        chatmessage.setSenderUUID(uuid);
        chatmessage.setSenderName(displayName);
        chatmessage.setSenderLegacyName(legacyName);
    }

    public void setDisplayName(String s)
    {
        displayName = s;
    }

    public void setLegacyName(String s)
    {
        legacyName = s;
    }
}
