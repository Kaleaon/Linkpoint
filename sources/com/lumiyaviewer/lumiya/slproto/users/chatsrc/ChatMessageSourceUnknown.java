// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.chatsrc:
//            ChatMessageSource

public class ChatMessageSourceUnknown extends ChatMessageSource
{

    private static final ChatMessageSourceUnknown Instance = new ChatMessageSourceUnknown();

    private ChatMessageSourceUnknown()
    {
    }

    public static ChatMessageSourceUnknown getInstance()
    {
        return Instance;
    }

    public ChatterID getDefaultChatter(UUID uuid)
    {
        return ChatterID.getLocalChatterID(uuid);
    }

    public String getSourceName(UserManager usermanager)
    {
        return null;
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType()
    {
        return ChatMessageSource.ChatMessageSourceType.Unknown;
    }

    public UUID getSourceUUID()
    {
        return null;
    }

}
