package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.inventory.SLAssetType
import java.util.UUID
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SLChatInventoryItemOfferedEvent : SLChatYesNoEvent {
    private var assetType: SLAssetType = SLAssetType.AT_UNKNOWN
    private var itemID: UUID? = null
    private var itemName: String = ""
    private var origIMType: Int = 0
    private var sessionID: UUID? = null

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.origIMType = chatMessage.origIMType ?: 0
        this.sessionID = chatMessage.sessionID
        this.itemID = chatMessage.itemID
        this.itemName = chatMessage.itemName ?: ""
        this.assetType = SLAssetType.getByType(chatMessage.assetType ?: 0)
    }

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage) : 
        super(source, uuid, message, SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)) {
        this.itemName = SLMessage.stringFromVariableUTF(message.MessageBlock_Field.Message)
        this.origIMType = message.MessageBlock_Field.Dialog
        this.sessionID = message.MessageBlock_Field.ID
        this.itemID = extractItemID(message)
        this.assetType = extractAssetType(message)
    }

    private fun extractAssetType(message: ImprovedInstantMessage): SLAssetType {
        return if (message.MessageBlock_Field.BinaryBucket.isNotEmpty()) {
            SLAssetType.getByType(message.MessageBlock_Field.BinaryBucket[0].toInt())
        } else {
            SLAssetType.AT_UNKNOWN
        }
    }

    private fun extractItemID(message: ImprovedInstantMessage): UUID? {
        if (message.MessageBlock_Field.BinaryBucket.size < 17) return null
        val wrap = ByteBuffer.wrap(message.MessageBlock_Field.BinaryBucket)
        wrap.order(ByteOrder.BIG_ENDIAN)
        wrap.get() // Skip byte
        return UUID(wrap.long, wrap.long)
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.InventoryItemOffered
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.inv_offer_no)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.inv_offer_declined)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.inv_offer_question)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.inv_offer_yes)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.inv_offer_accepted)
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.origIMType = origIMType
        chatMessage.sessionID = sessionID
        chatMessage.itemID = itemID
        chatMessage.itemName = itemName
        chatMessage.assetType = assetType.typeCode
    }

    // Accessors
    fun getAssetType() = assetType
    fun getItemID() = itemID
    fun getItemName() = itemName
}
