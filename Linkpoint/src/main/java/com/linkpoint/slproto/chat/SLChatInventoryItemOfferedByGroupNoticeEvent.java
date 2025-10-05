package com.linkpoint.slproto.chat;

import android.content.Context;
import com.linkpoint.R;
import com.linkpoint.dao.ChatMessage;
import com.linkpoint.slproto.chat.generic.SLChatEvent;
import com.linkpoint.slproto.inventory.SLAssetType;
import com.linkpoint.slproto.messages.ImprovedInstantMessage;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource;
import com.linkpoint.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLChatInventoryItemOfferedByGroupNoticeEvent extends SLChatInventoryItemOfferedEvent {
    public SLChatInventoryItemOfferedByGroupNoticeEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
    }

    public SLChatInventoryItemOfferedByGroupNoticeEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str, SLAssetType sLAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, str, extractItemID(improvedInstantMessage), sLAssetType);
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByGroupNotice;
    }

    public String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(R.string.group_notice_attachment_format, new Object[]{getItemName()});
    }
}
