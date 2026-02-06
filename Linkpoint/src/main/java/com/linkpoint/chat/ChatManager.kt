package com.linkpoint.chat

import android.util.Log
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.protocol.messages.ChatData
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.protocol.messages.ChatType
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages local and nearby chat.
 *
 * Inbound chat processing and flow emissions are serialized on the MessagingDispatcher
 * "MessageThread" to avoid mixing Default/IO dispatchers with messaging state.
 */
class ChatManager(
    private val udpConnection: UDPConnectionFixed,
    private val agentId: UUID
) {
    companion object {
        private const val TAG = "ChatManager"
        private const val MAX_CHAT_HISTORY = 500
    }
    
    private val scope = CoroutineScope(MessagingDispatcher.dispatcher + SupervisorJob())
    
    // Chat history
    private val chatHistory = mutableListOf<ChatMessage>()
    
    // Chat events
    private val _chatFlow = MutableSharedFlow<ChatMessage>(replay = 0, extraBufferCapacity = 64)
    val chatFlow: SharedFlow<ChatMessage> = _chatFlow
    
    // Typing indicators
    private val _typingAvatars = MutableStateFlow<Set<UUID>>(emptySet())
    val typingAvatars: StateFlow<Set<UUID>> = _typingAvatars
    
    // Nearby avatars speaking
    private val _speakingAvatars = MutableStateFlow<Set<UUID>>(emptySet())
    val speakingAvatars: StateFlow<Set<UUID>> = _speakingAvatars
    
    /**
     * Handle incoming chat from simulator
     */
    fun handleChatFromSimulator(data: ChatData) {
        scope.launch {
            when (data.chatType) {
                ChatType.START_TYPING -> {
                    _typingAvatars.value = _typingAvatars.value + data.sourceId
                }
                ChatType.STOP_TYPING -> {
                    _typingAvatars.value = _typingAvatars.value - data.sourceId
                }
                else -> {
                    val message = ChatMessage(
                        id = UUID.randomUUID(),
                        fromName = data.fromName,
                        sourceId = data.sourceId,
                        ownerId = data.ownerId,
                        sourceType = data.sourceType,
                        chatType = data.chatType,
                        position = data.position,
                        message = data.message,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    addMessage(message)
                    
                    // Remove from typing
                    _typingAvatars.value = _typingAvatars.value - data.sourceId
                }
            }
        }
    }
    
    /**
     * Send chat message
     */
    /**
     * Send chat message
     */
    fun sendChat(message: String, type: ChatType = ChatType.NORMAL, channel: Int = 0) {
        scope.launch {
            val data = buildChatPacket(message, type, channel)
            udpConnection.sendPacket(MessageIds.CHAT_FROM_VIEWER, data)
            
            // Add to local history
            val localMessage = ChatMessage(
                id = UUID.randomUUID(),
                fromName = "You",
                sourceId = agentId,
                ownerId = agentId,
                sourceType = ChatSourceType.AGENT,
                chatType = type,
                position = LLVector3.zero(),
                message = message,
                timestamp = System.currentTimeMillis(),
                isOutgoing = true
            )
            addMessage(localMessage)
        }
    }
    
    /**
     * Whisper
     */
    fun whisper(message: String, channel: Int = 0) {
        sendChat(message, ChatType.WHISPER, channel)
    }
    
    /**
     * Shout
     */
    fun shout(message: String, channel: Int = 0) {
        sendChat(message, ChatType.SHOUT, channel)
    }
    
    /**
     * Start typing indicator
     */
    fun startTyping() {
        scope.launch {
            val data = buildChatPacket("", ChatType.START_TYPING, 0)
            udpConnection.sendPacket(MessageIds.CHAT_FROM_VIEWER, data)
        }
    }
    
    /**
     * Stop typing indicator
     */
    fun stopTyping() {
        scope.launch {
            val data = buildChatPacket("", ChatType.STOP_TYPING, 0)
            udpConnection.sendPacket(MessageIds.CHAT_FROM_VIEWER, data)
        }
    }
    
    private fun buildChatPacket(message: String, type: ChatType, channel: Int): ByteArray {
        val messageBytes = message.toByteArray(Charsets.UTF_8)
        
        // ChatFromViewer message format per SL protocol:
        // AgentData block:
        //   - AgentID (LLUUID, 16 bytes) - big-endian UUID
        //   - SessionID (LLUUID, 16 bytes) - big-endian UUID
        // ChatData block:
        //   - Message (Variable 2) - 2-byte length prefix, then message bytes + null terminator
        //   - Type (U8)
        //   - Channel (S32, little-endian)
        
        // Total: 16 + 16 + 2 + message.length + 1 (null) + 1 (type) + 4 = 40 + message.length
        val buffer = ByteBuffer.allocate(40 + messageBytes.size)
            .order(ByteOrder.LITTLE_ENDIAN)
        
        // AgentData block - UUIDs use big-endian per SL protocol
        buffer.putUUID(agentId)
        
        // SessionID - get from UDPConnection
        val sessionId = udpConnection.getSessionId()
        buffer.putUUID(sessionId)
        
        // ChatData block
        // Message - Variable 2 (2-byte length prefix + string + null terminator)
        buffer.putShort((messageBytes.size + 1).toShort())
        buffer.put(messageBytes)
        buffer.put(0) // Null terminator
        
        // Type (U8)
        buffer.put(type.value.toByte())
        
        // Channel (S32, little-endian)
        buffer.putInt(channel)
        
        return buffer.array()
    }
    
    private fun addMessage(message: ChatMessage) {
        synchronized(chatHistory) {
            chatHistory.add(message)
            if (chatHistory.size > MAX_CHAT_HISTORY) {
                chatHistory.removeAt(0)
            }
        }
        
        scope.launch {
            _chatFlow.emit(message)
        }
    }
    
    /**
     * Get chat history
     */
    fun getHistory(): List<ChatMessage> {
        return synchronized(chatHistory) {
            chatHistory.toList()
        }
    }
    
    /**
     * Clear chat history
     */
    fun clearHistory() {
        synchronized(chatHistory) {
            chatHistory.clear()
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

data class ChatMessage(
    val id: UUID,
    val fromName: String,
    val sourceId: UUID,
    val ownerId: UUID,
    val sourceType: ChatSourceType,
    val chatType: ChatType,
    val position: LLVector3,
    val message: String,
    val timestamp: Long,
    val isOutgoing: Boolean = false
)
