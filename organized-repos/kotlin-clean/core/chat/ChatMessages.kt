package com.linkpoint.chat

import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

/**
 * ChatFromSimulator - Received chat message
 */
class ChatFromSimulator(
    val fromName: String,
    val sourceID: UUID,
    val ownerID: UUID,
    val sourceType: Byte,
    val chatType: Byte,
    val channel: Int,
    val message: String,
    val position: LLVector3
) : SLMessage() {
    
    override fun getMessageName() = "ChatFromSimulator"
    
    override fun encode(buffer: ByteBuffer) {
        // Not typically sent from client
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Decoded by MessageDecoder
    }
    
    /**
     * Convert to ChatMessage
     */
    fun toChatMessage(): ChatMessage {
        return ChatMessage(
            fromName = fromName,
            sourceID = sourceID,
            ownerID = ownerID,
            sourceType = ChatSourceType.fromId(sourceType.toInt()) ?: ChatSourceType.SYSTEM,
            chatType = ChatType.fromId(chatType.toInt()) ?: ChatType.NORMAL,
            channel = channel,
            message = message,
            position = position
        )
    }
}

/**
 * ImprovedInstantMessageReceived - Received IM
 */
class ImprovedInstantMessageReceived(
    val fromAgentID: UUID,
    val fromAgentName: String,
    val toAgentID: UUID,
    val message: String,
    val imType: Int,
    val sessionID: UUID,
    val offline: Boolean
) : SLMessage() {
    
    override fun getMessageName() = "ImprovedInstantMessage"
    
    override fun encode(buffer: ByteBuffer) {
        // Not typically sent (use ImprovedInstantMessage for sending)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Decoded by MessageDecoder
    }
    
    /**
     * Convert to IMMessage
     */
    fun toIMMessage(): IMMessage {
        return IMMessage(
            messageID = UUID.randomUUID(),
            fromAgentID = fromAgentID,
            fromAgentName = fromAgentName,
            toAgentID = toAgentID,
            message = message,
            imType = IMType.fromId(imType) ?: IMType.MESSAGE_FROM_AGENT,
            offline = offline
        )
    }
}
