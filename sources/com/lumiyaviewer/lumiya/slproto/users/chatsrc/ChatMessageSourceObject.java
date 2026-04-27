// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.chatsrc:
//            ChatMessageSource

public class ChatMessageSourceObject extends ChatMessageSource
{

    public final String name;
    public final UUID uuid;

    ChatMessageSourceObject(ChatMessage chatmessage)
    {
        uuid = chatmessage.getSenderUUID();
        name = chatmessage.getSenderName();
    }

    public ChatMessageSourceObject(UUID uuid1, String s)
    {
        uuid = uuid1;
        name = s;
    }

    public ChatterID getDefaultChatter(UUID uuid1)
    {
        return ChatterID.getLocalChatterID(uuid1);
    }

    public String getSourceName(UserManager usermanager)
    {
        return name;
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType()
    {
        return ChatMessageSource.ChatMessageSourceType.Object;
    }

    public UUID getSourceUUID()
    {
        return uuid;
    }

    public void serializeTo(ChatMessage chatmessage)
    {
        super.serializeTo(chatmessage);
        chatmessage.setSenderUUID(uuid);
        chatmessage.setSenderName(name);
    }
}
