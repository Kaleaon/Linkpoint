package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.inventory.SLAssetType
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
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

    fun getText(Context context, @NonNull UserManager userManager): String {
        return context.getString(R.string.group_notice_attachment_format, Object[]{getItemName()})
    }
}
