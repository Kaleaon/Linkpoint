package com.linkpoint.chat

import com.linkpoint.Debug
import com.linkpoint.slproto.SLCircuitNew
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.caps.CAPSManager
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * ChatType - Types of chat messages
 */
enum class ChatType(val typeId: Int) {
    WHISPER(0),
    NORMAL(1),
    SHOUT(2),
    REGION(3),  // Say to entire region
    OWNER(4),   // Object owner
    DEBUG(5),
    BROADCAST(0xFF);
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * ChatSourceType - Source of chat message
 */
enum class ChatSourceType(val typeId: Int) {
    SYSTEM(0),
    AGENT(1),
    OBJECT(2);
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * ChatMessage - Represents a chat message
 */
data class ChatMessage(
    val messageID: UUID = UUID.randomUUID(),
    val fromName: String,
    val sourceID: UUID,
    val ownerID: UUID,
    val sourceType: ChatSourceType,
    val chatType: ChatType,
    val channel: Int,
    val message: String,
    val position: com.linkpoint.slproto.types.LLVector3,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * IMMessage - Instant message
 */
data class IMMessage(
    val messageID: UUID,
    val fromAgentID: UUID,
    val fromAgentName: String,
    val toAgentID: UUID,
    val message: String,
    val imType: IMType,
    val timestamp: Long = System.currentTimeMillis(),
    val offline: Boolean = false,
    val parentEstateID: Int = 0,
    val regionID: UUID = UUID(0, 0),
    val position: com.linkpoint.slproto.types.LLVector3 = com.linkpoint.slproto.types.LLVector3()
)

/**
 * IMType - Types of instant messages
 */
enum class IMType(val typeId: Int) {
    NOTHING_SPECIAL(0),
    MESSAGE_FROM_AGENT(1),
    MESSAGE_FROM_OBJECT(2),
    GROUP_INVITATION(3),
    INVENTORY_OFFERED(4),
    INVENTORY_ACCEPTED(5),
    INVENTORY_DECLINED(6),
    GROUP_VOTE(7),
    GROUP_MESSAGE(8),
    TASK_INVENTORY_OFFERED(9),
    TASK_INVENTORY_ACCEPTED(10),
    TASK_INVENTORY_DECLINED(11),
    NEW_USER_DEFAULT(12),
    SESSION_ADD(13),
    SESSION_OFFLINE_ADD(14),
    SESSION_GROUP_START(15),
    SESSION_CONFERENCE_START(16),
    SESSION_SEND(17),
    SESSION_DROP(18),
    MESSAGEBOX(19),
    START_TYPING(20),
    STOP_TYPING(21),
    TELEPORT_REQUEST(22),
    TELEPORT_LURE(23),
    GROUP_NOTICE(24),
    GROUP_NOTICE_INVENTORY_ACCEPTED(25),
    GROUP_NOTICE_INVENTORY_DECLINED(26),
    FRIENDSHIP_OFFERED(27),
    FRIENDSHIP_ACCEPTED(28),
    FRIENDSHIP_DECLINED(29),
    TYPING(41),
    BUSY_AUTO_RESPONSE(42);
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * IMSession - Represents an IM conversation session
 */
data class IMSession(
    val sessionID: UUID,
    val name: String,
    val isGroupSession: Boolean,
    val participants: MutableSet<UUID> = mutableSetOf(),
    val messages: MutableList<IMMessage> = mutableListOf()
)

/**
 * ChatManager - Manages chat and instant messaging
 * 
 * Features:
 * - Local chat (whisper, normal, shout)
 * - Instant messaging (1-on-1 and group)
 * - Chat history
 * - Typing indicators
 * - Offline messages
 * 
 * Based on Firestorm's chat and IM system
 */
class ChatManager(
    private val circuit: SLCircuitNew,
    private val capsManager: CAPSManager,
    private val agentID: UUID
) {
    
    companion object {
        const val MAX_CHAT_HISTORY = 1000
        const val MAX_IM_HISTORY = 500
        const val PUBLIC_CHANNEL = 0
        const val DEBUG_CHANNEL = 0x7FFFFFFF
    }
    
    // Chat history
    private val chatHistory = CopyOnWriteArrayList<ChatMessage>()
    
    // IM sessions
    private val imSessions = ConcurrentHashMap<UUID, IMSession>()
    
    // Chat listeners
    private val chatListeners = CopyOnWriteArrayList<ChatListener>()
    private val imListeners = CopyOnWriteArrayList<IMListener>()
    
    /**
     * Send chat message
     */
    fun sendChat(message: String, channel: Int = PUBLIC_CHANNEL, type: ChatType = ChatType.NORMAL) {
        val chatMessage = ChatFromViewer(
            agentID = agentID,
            message = message,
            channel = channel,
            type = type.typeId
        )
        circuit.sendMessage(chatMessage)
    }
    
    /**
     * Send whisper
     */
    fun sendWhisper(message: String) = sendChat(message, type = ChatType.WHISPER)
    
    /**
     * Send shout
     */
    fun sendShout(message: String) = sendChat(message, type = ChatType.SHOUT)
    
    /**
     * Send instant message
     */
    suspend fun sendIM(
        toAgentID: UUID,
        message: String,
        imType: IMType = IMType.MESSAGE_FROM_AGENT
    ) = withContext(Dispatchers.IO) {
        
        // Check if we have a session
        val sessionID = findOrCreateSession(toAgentID)
        
        val im = ImprovedInstantMessage(
            fromAgentID = agentID,
            toAgentID = toAgentID,
            message = message,
            imType = imType.typeId,
            sessionID = sessionID
        )
        
        circuit.sendMessage(im)
        
        // Add to local history
        val imMessage = IMMessage(
            messageID = UUID.randomUUID(),
            fromAgentID = agentID,
            fromAgentName = "You",
            toAgentID = toAgentID,
            message = message,
            imType = imType
        )
        
        addIMToSession(sessionID, imMessage)
    }
    
    /**
     * Send group IM
     */
    suspend fun sendGroupIM(groupID: UUID, message: String) {
        sendIM(groupID, message, IMType.SESSION_SEND)
    }
    
    /**
     * Start typing indicator
     */
    fun startTyping(toAgentID: UUID) {
        val sessionID = findOrCreateSession(toAgentID)
        sendIM(toAgentID, "", IMType.START_TYPING)
    }
    
    /**
     * Stop typing indicator
     */
    fun stopTyping(toAgentID: UUID) {
        val sessionID = findOrCreateSession(toAgentID)
        sendIM(toAgentID, "", IMType.STOP_TYPING)
    }
    
    /**
     * Handle received chat message
     */
    fun handleChatMessage(chatMessage: ChatMessage) {
        // Add to history
        chatHistory.add(chatMessage)
        
        // Trim history if too large
        while (chatHistory.size > MAX_CHAT_HISTORY) {
            chatHistory.removeAt(0)
        }
        
        // Notify listeners
        for (listener in chatListeners) {
            listener.onChatMessage(chatMessage)
        }
    }
    
    /**
     * Handle received IM
     */
    fun handleIM(imMessage: IMMessage) {
        // Find or create session
        val sessionID = findOrCreateSession(imMessage.fromAgentID)
        
        // Add to session
        addIMToSession(sessionID, imMessage)
        
        // Notify listeners
        for (listener in imListeners) {
            listener.onIMMessage(imMessage)
        }
    }
    
    /**
     * Find or create IM session
     */
    private fun findOrCreateSession(agentID: UUID): UUID {
        // Look for existing session
        for ((sessionID, session) in imSessions) {
            if (!session.isGroupSession && session.participants.contains(agentID)) {
                return sessionID
            }
        }
        
        // Create new session
        val sessionID = UUID.randomUUID()
        val session = IMSession(
            sessionID = sessionID,
            name = "IM with $agentID",
            isGroupSession = false
        )
        session.participants.add(agentID)
        imSessions[sessionID] = session
        
        return sessionID
    }
    
    /**
     * Add IM to session
     */
    private fun addIMToSession(sessionID: UUID, imMessage: IMMessage) {
        val session = imSessions[sessionID] ?: return
        
        session.messages.add(imMessage)
        
        // Trim history
        while (session.messages.size > MAX_IM_HISTORY) {
            session.messages.removeAt(0)
        }
    }
    
    /**
     * Get chat history
     */
    fun getChatHistory(): List<ChatMessage> = chatHistory.toList()
    
    /**
     * Get IM session
     */
    fun getIMSession(sessionID: UUID): IMSession? = imSessions[sessionID]
    
    /**
     * Get all IM sessions
     */
    fun getAllIMSessions(): List<IMSession> = imSessions.values.toList()
    
    /**
     * Clear chat history
     */
    fun clearChatHistory() = chatHistory.clear()
    
    /**
     * Close IM session
     */
    fun closeIMSession(sessionID: UUID) {
        imSessions.remove(sessionID)
    }
    
    /**
     * Add chat listener
     */
    fun addChatListener(listener: ChatListener) {
        chatListeners.add(listener)
    }
    
    /**
     * Remove chat listener
     */
    fun removeChatListener(listener: ChatListener) {
        chatListeners.remove(listener)
    }
    
    /**
     * Add IM listener
     */
    fun addIMListener(listener: IMListener) {
        imListeners.add(listener)
    }
    
    /**
     * Remove IM listener
     */
    fun removeIMListener(listener: IMListener) {
        imListeners.remove(listener)
    }
}

/**
 * ChatListener - Interface for chat events
 */
interface ChatListener {
    fun onChatMessage(message: ChatMessage)
}

/**
 * IMListener - Interface for IM events
 */
interface IMListener {
    fun onIMMessage(message: IMMessage)
    fun onTypingStart(agentID: UUID) {}
    fun onTypingStop(agentID: UUID) {}
}

/**
 * ChatFromViewer - Message to send chat
 */
class ChatFromViewer(
    val agentID: UUID,
    val message: String,
    val channel: Int,
    val type: Int
) : SLMessage() {
    
    override fun getMessageName() = "ChatFromViewer"
    
    override fun encode(buffer: ByteBuffer) {
        writeUUID(buffer, agentID)
        writeVariableString(buffer, message)
        buffer.putInt(channel)
        buffer.put(type.toByte())
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Not typically received
    }
}

/**
 * ImprovedInstantMessage - IM message
 */
class ImprovedInstantMessage(
    val fromAgentID: UUID,
    val toAgentID: UUID,
    val message: String,
    val imType: Int,
    val sessionID: UUID
) : SLMessage() {
    
    override fun getMessageName() = "ImprovedInstantMessage"
    
    override fun encode(buffer: ByteBuffer) {
        writeUUID(buffer, fromAgentID)
        writeUUID(buffer, toAgentID)
        writeUUID(buffer, sessionID)
        buffer.put(imType.toByte())
        writeVariableString(buffer, message)
    }
    
    override fun decode(buffer: ByteBuffer) {
        // Decode received IM
    }
}
