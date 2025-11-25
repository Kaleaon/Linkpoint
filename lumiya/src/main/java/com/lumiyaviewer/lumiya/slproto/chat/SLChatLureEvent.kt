package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class SLChatLureEvent : SLChatYesNoEvent {
    private var lureID: UUID? = null
    private var regionID: Long = 0
    private var position: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)
    private var lookup: DoubleArray = doubleArrayOf(0.0, 0.0, 0.0)

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.lureID = chatMessage.lureID
        // Other fields would be loaded from DB if available
    }

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage) : 
        super(source, uuid, message, "Teleport request") {
        this.lureID = message.MessageBlock_Field.ID
        // Parse binary bucket for region/pos if needed
        parseBinaryBucket(message.MessageBlock_Field.BinaryBucket)
    }

    private fun parseBinaryBucket(bucket: ByteArray) {
        if (bucket.isEmpty()) return
        // Simplified parsing
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.Lure
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.lure_decline)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.lure_declined)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.lure_question)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.lure_accept)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.lure_accepted)
    }

    override fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val circuit = userManager.activeAgentCircuit
        if (circuit != null && lureID != null) {
            // Trigger teleport
            // TeleportProgressDialog(context, userManager, circuit, lureID).show()
        }
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.lureID = lureID
    }
}
