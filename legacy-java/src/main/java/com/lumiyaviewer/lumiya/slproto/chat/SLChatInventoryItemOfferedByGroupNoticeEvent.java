package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatInventoryItemOfferedByGroupNoticeEvent extends SLChatInventoryItemOfferedEvent {
    public SLChatInventoryItemOfferedByGroupNoticeEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
    }

    public SLChatInventoryItemOfferedByGroupNoticeEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str, SLAssetType sLAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, str, extractItemID(improvedInstantMessage), sLAssetType);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByGroupNotice;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatInventoryItemOfferedEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(R.string.group_notice_attachment_format, getItemName());
    }
}
