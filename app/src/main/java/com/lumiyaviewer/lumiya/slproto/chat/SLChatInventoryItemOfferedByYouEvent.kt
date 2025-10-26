package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

class SLChatInventoryItemOfferedByYouEvent : SLChatEvent {
    private String itemName

    SLChatInventoryItemOfferedByYouEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid)
        this.itemName = chatMessage.getItemName()
    }

    SLChatInventoryItemOfferedByYouEvent(@Nonnull UUID uuid, String str) {
        super((ChatMessageSource) ChatMessageSourceUnknown.getInstance(), uuid)
        this.itemName = str
    }

    /* access modifiers changed from: protected */
    @Nonnull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByYou
    }

    /* access modifiers changed from: protected */
    String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(R.string.chat_inventory_own_offer_format, Array<Any>{this.itemName})
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    Boolean isActionMessage(@Nonnull UserManager userManager) {
        return false
    }

    Unit serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemName(this.itemName)
    }
}
