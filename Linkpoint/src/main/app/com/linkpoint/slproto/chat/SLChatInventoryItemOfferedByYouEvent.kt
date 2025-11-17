package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatInventoryItemOfferedByYouEvent : SLChatEvent {
    private String itemName

    SLChatInventoryItemOfferedByYouEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.itemName = chatMessage.getItemName()
    }

    SLChatInventoryItemOfferedByYouEvent(@NonNull UUID uuid, String str) {
        super((ChatMessageSource) ChatMessageSourceUnknown.getInstance(), uuid)
        this.itemName = str
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByYou
    }

    /* access modifiers changed from: protected */
    String getText(Context context, @NonNull UserManager userManager) {
        return context.getString(R.string.chat_inventory_own_offer_format, Object[]{this.itemName})
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    Boolean isActionMessage(@NonNull UserManager userManager) {
        return false
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemName(this.itemName)
    }
}
