// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatInventoryItemOfferedByYouEvent extends SLChatEvent
{

    private final String itemName;

    public SLChatInventoryItemOfferedByYouEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        itemName = chatmessage.getItemName();
    }

    public SLChatInventoryItemOfferedByYouEvent(UUID uuid, String s)
    {
        super(ChatMessageSourceUnknown.getInstance(), uuid);
        itemName = s;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.InventoryItemOfferedByYou;
    }

    protected String getText(Context context, UserManager usermanager)
    {
        return context.getString(0x7f0900af, new Object[] {
            itemName
        });
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    protected boolean isActionMessage(UserManager usermanager)
    {
        return false;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setItemName(itemName);
    }
}
