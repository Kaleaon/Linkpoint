package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatInventoryItemOfferedByYouEvent : SLChatEvent() {
    private val String itemName

    public SLChatInventoryItemOfferedByYouEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.itemName = chatMessage.getItemName()
    }

    public SLChatInventoryItemOfferedByYouEvent(UUID uuid, String str) {
        super((ChatMessageSource) ChatMessageSourceUnknown.getInstance(), uuid)
        this.itemName = str
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.InventoryItemOfferedByYou
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, UserManager userManager) {
        return context.getString(R.string.chat_inventory_own_offer_format, Object[]{this.itemName})
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    public Boolean isActionMessage(UserManager userManager) {
        return false
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemName(this.itemName)
    }
}
