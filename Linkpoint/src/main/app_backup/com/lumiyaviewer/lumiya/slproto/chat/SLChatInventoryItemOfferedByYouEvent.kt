package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLChatInventoryItemOfferedByYouEvent : SLChatEvent {
    private val itemName: String

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.itemName = chatMessage.itemName ?: ""
    }

    constructor(uuid: UUID, itemName: String) : super(ChatMessageSourceUnknown.getInstance(), uuid) {
        this.itemName = itemName
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.InventoryItemOfferedByYou
    }

    override fun getMessage(): CharSequence {
        return "You offered inventory item: $itemName"
    }

    override fun getSenderDisplayName(): CharSequence? {
        return "You"
    }

    override fun getChatterID(): com.lumiyaviewer.lumiya.slproto.users.ChatterID {
        return com.lumiyaviewer.lumiya.slproto.users.ChatterID(agentUUID, "")
    }

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_NORMAL
    }
    
    // Stub implementation as SLChatEvent might require it
    fun isActionMessage(userManager: UserManager): Boolean {
        return false
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.itemName = itemName
    }
}
