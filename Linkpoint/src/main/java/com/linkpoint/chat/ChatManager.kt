package com.linkpoint.chat

import android.util.Log
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.ChatData
import com.linkpoint.protocol.messages.ChatSourceType
import com.linkpoint.protocol.messages.ChatType
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.SLMessagePackers
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
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
     * Send a local-chat line.
     *
     * Marked **reliable** to mirror Lumiya
     * (`SLAgentCircuit.SendLocalChatMessage:1724`: `chatFromViewer.isReliable = true`).
     * On lossy networks an unreliable ChatFromViewer is silently dropped
     * with no resend and no failure callback — the user types and sees
     * nothing land in nearby chat.
     */
    fun sendChat(message: String, type: ChatType = ChatType.NORMAL, channel: Int = 0) {
        scope.launch {
            val identity = AgentIdentity(
                agentId = agentId,
                sessionId = udpConnection.getSessionId(),
                circuitCode = udpConnection.getCircuitCode()
            ).requireValid("ChatManager.sendChat")
            val data = SLMessagePackers.packChatFromViewer(
                identity = identity,
                message = message,
                type = type.value,
                channel = channel
            )
            udpConnection.sendPacket(MessageIds.CHAT_FROM_VIEWER, data, reliable = true)

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
     * Typing indicators in nearby chat are deprecated. Lumiya does not
     * route them through `ChatFromViewer` (the legacy `Type=4/5` path is
     * inert in modern SL), and per-conversation typing is delivered via
     * `ImprovedInstantMessage` Dialog=41/42 in [IMManager]. Kept here as a
     * no-op so existing UI bindings compile; remove the call sites once
     * the IM-based path is wired through.
     */
    fun startTyping() {
        Log.v(TAG, "startTyping: nearby-chat typing is deprecated — use IMManager.sendTypingStart")
    }

    fun stopTyping() {
        Log.v(TAG, "stopTyping: nearby-chat typing is deprecated — use IMManager.sendTypingStop")
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
