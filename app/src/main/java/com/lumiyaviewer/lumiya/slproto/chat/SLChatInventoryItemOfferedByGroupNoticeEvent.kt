package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatInventoryItemOfferedByGroupNoticeEvent : SLChatInventoryItemOfferedEvent {
    SLChatInventoryItemOfferedByGroupNoticeEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
    }

    SLChatInventoryItemOfferedByGroupNoticeEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str, SLAssetType sLAssetType) {
        super(chatMessageSource, uuid, improvedInstantMessage, str, extractItemID(improvedInstantMessage), sLAssetType)
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByGroupNotice
    }

    String getText(Context context, @NonNull UserManager userManager) {
        return context.getString(R.string.group_notice_attachment_format, Object[]{getItemName()})
    }
}
