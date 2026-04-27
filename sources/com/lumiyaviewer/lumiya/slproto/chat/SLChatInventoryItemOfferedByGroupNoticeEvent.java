// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat:
//            SLChatInventoryItemOfferedEvent

public final class SLChatInventoryItemOfferedByGroupNoticeEvent extends SLChatInventoryItemOfferedEvent
{

    public SLChatInventoryItemOfferedByGroupNoticeEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
    }

    public SLChatInventoryItemOfferedByGroupNoticeEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage, String s, SLAssetType slassettype)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, s, extractItemID(improvedinstantmessage), slassettype);
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.InventoryItemOfferedByGroupNotice;
    }

    public String getText(Context context, UserManager usermanager)
    {
        return context.getString(0x7f090147, new Object[] {
            getItemName()
        });
    }
}
