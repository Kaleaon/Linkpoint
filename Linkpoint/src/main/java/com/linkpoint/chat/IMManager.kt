package com.linkpoint.chat

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Instant Message manager
 * Handles private IMs, group chat, and conference chat
 */
class IMManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    companion object {
        private const val TAG = "IMManager"
        private const val MAX_SESSION_HISTORY = 200
        
        // IM dialog types
        const val IM_NOTHING_SPECIAL = 0
        const val IM_MESSAGEBOX = 1
        const val IM_GROUP_INVITATION = 3
        const val IM_INVENTORY_OFFERED = 4
        const val IM_INVENTORY_ACCEPTED = 5
        const val IM_INVENTORY_DECLINED = 6
        const val IM_GROUP_VOTE = 7
        const val IM_GROUP_MESSAGE_DEPRECATED = 8
        const val IM_TASK_INVENTORY_OFFERED = 9
        const val IM_TASK_INVENTORY_ACCEPTED = 10
        const val IM_TASK_INVENTORY_DECLINED = 11
        const val IM_NEW_USER_DEFAULT = 12
        const val IM_SESSION_INVITE = 13
        const val IM_SESSION_P2P_INVITE = 14
        const val IM_SESSION_GROUP_START = 15
        const val IM_SESSION_CONFERENCE_START = 16
        const val IM_SESSION_SEND = 17
        const val IM_SESSION_LEAVE = 18
        const val IM_FROM_TASK = 19
        const val IM_BUSY_AUTO_RESPONSE = 20
        const val IM_CONSOLE_AND_CHAT_HISTORY = 21
        const val IM_LURE_USER = 22
        const val IM_LURE_ACCEPTED = 23
        const val IM_LURE_DECLINED = 24
        const val IM_GODLIKE_LURE_USER = 25
        const val IM_TELEPORT_REQUEST = 26
        const val IM_GROUP_ELECTION_DEPRECATED = 27
        const val IM_GOTO_URL = 28
        const val IM_FROM_TASK_AS_ALERT = 31
        const val IM_GROUP_NOTICE = 32
        const val IM_GROUP_NOTICE_INVENTORY_ACCEPTED = 33
        const val IM_GROUP_NOTICE_INVENTORY_DECLINED = 34
        const val IM_GROUP_INVITATION_ACCEPT = 35
        const val IM_GROUP_INVITATION_DECLINE = 36
        const val IM_GROUP_NOTICE_REQUESTED = 37
        const val IM_FRIENDSHIP_OFFERED = 38
        const val IM_FRIENDSHIP_ACCEPTED = 39
        const val IM_FRIENDSHIP_DECLINED = 40
        const val IM_TYPING_START = 41
        const val IM_TYPING_STOP = 42
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Active IM sessions
    private val sessions = ConcurrentHashMap<UUID, IMSession>()
    
    // Session messages
    private val sessionMessages = ConcurrentHashMap<UUID, MutableList<IMMessage>>()
    
    // Events
    private val _messageFlow = MutableSharedFlow<IMMessage>(replay = 0, extraBufferCapacity = 64)
    val messageFlow: SharedFlow<IMMessage> = _messageFlow
    
    private val _sessionFlow = MutableSharedFlow<IMSessionEvent>(replay = 0, extraBufferCapacity = 16)
    val sessionFlow: SharedFlow<IMSessionEvent> = _sessionFlow
    
    // Active sessions list
    private val _activeSessions = MutableStateFlow<List<IMSession>>(emptyList())
    val activeSessions: StateFlow<List<IMSession>> = _activeSessions
    
    // Unread counts
    private val _unreadCounts = MutableStateFlow<Map<UUID, Int>>(emptyMap())
    val unreadCounts: StateFlow<Map<UUID, Int>> = _unreadCounts
    
    init {
        // Register for event queue events
        capabilityManager.registerEventHandler("ChatterBoxInvitation", this)
        capabilityManager.registerEventHandler("ChatterBoxSessionEventReply", this)
        capabilityManager.registerEventHandler("ChatterBoxSessionStartReply", this)
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        when (message) {
            "ChatterBoxInvitation" -> handleInvitation(body)
            "ChatterBoxSessionEventReply" -> handleSessionEvent(body)
            "ChatterBoxSessionStartReply" -> handleSessionStart(body)
        }
    }
    
    private fun handleInvitation(body: LLSDMap) {
        val inviteInfo = body.getMap("instantmessage")?.getMap("message_params") ?: return
        
        val sessionId = UUID.fromString(inviteInfo.getString("id") ?: return)
        val fromAgentId = UUID.fromString(inviteInfo.getString("from_id") ?: return)
        val fromName = inviteInfo.getString("from_name") ?: "Unknown"
        val message = inviteInfo.getString("message") ?: ""
        val type = inviteInfo.getInt("dialog") ?: 0
        
        // Create session
        val sessionType = when (type) {
            IM_SESSION_GROUP_START -> SessionType.GROUP
            IM_SESSION_CONFERENCE_START -> SessionType.CONFERENCE
            else -> SessionType.P2P
        }
        
        val session = IMSession(
            sessionId = sessionId,
            type = sessionType,
            name = fromName,
            participants = mutableListOf(fromAgentId)
        )
        sessions[sessionId] = session
        updateSessionList()
        
        scope.launch {
            _sessionFlow.emit(IMSessionEvent.Invited(session))
        }
    }
    
    private fun handleSessionEvent(body: LLSDMap) {
        val sessionIdStr = body.getString("session_id") ?: return
        val sessionId = try { UUID.fromString(sessionIdStr) } catch (e: Exception) { return }
        val success = body.getInt("success") == 1
        val eventType = body.getString("event") ?: "unknown"
        
        val session = sessions[sessionId] ?: return
        
        when (eventType) {
            "join" -> {
                // Participant joined
                val agentId = body.getString("agent_id")?.let { 
                    try { UUID.fromString(it) } catch (e: Exception) { null }
                }
                if (agentId != null && agentId !in session.participants) {
                    session.participants = session.participants + agentId
                    scope.launch {
                        _sessionFlow.emit(IMSessionEvent.ParticipantJoined(sessionId, agentId))
                    }
                    Log.d(TAG, "Participant $agentId joined session $sessionId")
                }
            }
            "leave" -> {
                // Participant left
                val agentId = body.getString("agent_id")?.let { 
                    try { UUID.fromString(it) } catch (e: Exception) { null }
                }
                if (agentId != null) {
                    session.participants = session.participants - agentId
                    scope.launch {
                        _sessionFlow.emit(IMSessionEvent.ParticipantLeft(sessionId, agentId))
                    }
                    Log.d(TAG, "Participant $agentId left session $sessionId")
                }
            }
            "typing" -> {
                // Typing indicator
                val agentId = body.getString("agent_id")?.let { 
                    try { UUID.fromString(it) } catch (e: Exception) { null }
                }
                val isTyping = body.getInt("typing") == 1
                if (agentId != null) {
                    if (isTyping) {
                        session.typingParticipants = session.typingParticipants + agentId
                    } else {
                        session.typingParticipants = session.typingParticipants - agentId
                    }
                }
            }
            else -> {
                Log.d(TAG, "Unknown session event: $eventType for session $sessionId")
            }
        }
        
        updateSessionList()
    }
    
    private fun handleSessionStart(body: LLSDMap) {
        val sessionIdStr = body.getString("session_id") ?: return
        val sessionId = try { UUID.fromString(sessionIdStr) } catch (e: Exception) { return }
        val success = body.getInt("success") == 1
        
        if (success) {
            val session = sessions[sessionId]
            if (session != null) {
                session.isActive = true
                scope.launch {
                    _sessionFlow.emit(IMSessionEvent.Joined(session))
                }
                Log.d(TAG, "Session $sessionId started successfully")
            }
            updateSessionList()
        } else {
            val error = body.getString("error") ?: "Unknown error"
            Log.e(TAG, "Failed to start session $sessionId: $error")
            // Remove failed session
            sessions.remove(sessionId)
            scope.launch {
                _sessionFlow.emit(IMSessionEvent.Left(sessionId))
            }
            updateSessionList()
        }
    }
    
    /**
     * Handle incoming IM
     */
    fun handleIncomingIM(
        fromAgentId: UUID,
        fromName: String,
        message: String,
        sessionId: UUID,
        dialogType: Int,
        timestamp: Long
    ) {
        // Get or create session
        val session = sessions.getOrPut(sessionId) {
            IMSession(
                sessionId = sessionId,
                type = if (dialogType == IM_SESSION_GROUP_START) SessionType.GROUP else SessionType.P2P,
                name = fromName,
                participants = mutableListOf(fromAgentId)
            )
        }
        
        when (dialogType) {
            IM_TYPING_START -> {
                session.typingParticipants = session.typingParticipants + fromAgentId
            }
            IM_TYPING_STOP -> {
                session.typingParticipants = session.typingParticipants - fromAgentId
            }
            else -> {
                val imMessage = IMMessage(
                    id = UUID.randomUUID(),
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message,
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = false
                )
                
                addMessage(sessionId, imMessage)
                session.typingParticipants = session.typingParticipants - fromAgentId
            }
        }
        
        updateSessionList()
    }
    
    /**
     * Send IM
     */
    fun sendIM(sessionId: UUID, message: String) {
        val session = sessions[sessionId] ?: return
        
        scope.launch {
            val request = LLSDMap().apply {
                this["session-id"] = LLSDString(sessionId.toString())
                this["message"] = LLSDString(message)
            }
            
            capabilityManager.request(CapabilityManager.CAP_CHAT_PASS, request)
            
            // Add to local history
            val imMessage = IMMessage(
                id = UUID.randomUUID(),
                sessionId = sessionId,
                fromAgentId = agentId,
                fromName = "You",
                message = message,
                dialogType = IM_SESSION_SEND,
                timestamp = System.currentTimeMillis(),
                isOutgoing = true
            )
            
            addMessage(sessionId, imMessage)
        }
    }
    
    /**
     * Start P2P IM session
     */
    fun startP2PSession(targetAgentId: UUID, targetName: String): UUID {
        val sessionId = computeP2PSessionId(agentId, targetAgentId)
        
        val session = sessions.getOrPut(sessionId) {
            IMSession(
                sessionId = sessionId,
                type = SessionType.P2P,
                name = targetName,
                participants = mutableListOf(targetAgentId),
                isActive = true
            )
        }
        
        updateSessionList()
        return sessionId
    }
    
    /**
     * Start group chat session
     */
    suspend fun startGroupSession(groupId: UUID, groupName: String): UUID? {
        val request = LLSDMap().apply {
            this["method"] = LLSDString("start session")
            this["session-id"] = LLSDString(groupId.toString())
            this["params"] = LLSDMap().apply {
                this["type"] = LLSDInteger(0) // Group chat
                this["session-id"] = LLSDString(groupId.toString())
            }
        }
        
        val response = capabilityManager.request(CapabilityManager.CAP_CHAT_PASS, request)
        if (response is LLSDMap && response.getInt("success") == 1) {
            val session = IMSession(
                sessionId = groupId,
                type = SessionType.GROUP,
                name = groupName,
                participants = mutableListOf(),
                isActive = true
            )
            sessions[groupId] = session
            updateSessionList()
            return groupId
        }
        
        return null
    }
    
    /**
     * Start conference (ad-hoc group) session
     */
    suspend fun startConferenceSession(participants: List<UUID>, name: String): UUID? {
        val sessionId = UUID.randomUUID()
        
        val request = LLSDMap().apply {
            this["method"] = LLSDString("start conference")
            this["session-id"] = LLSDString(sessionId.toString())
            this["params"] = LLSDMap().apply {
                this["type"] = LLSDInteger(7) // Ad-hoc
                this["session-id"] = LLSDString(sessionId.toString())
                this["caller-id"] = LLSDString(agentId.toString())
                this["bucket"] = LLSDArray().apply {
                    participants.forEach { add(LLSDString(it.toString())) }
                }
            }
        }
        
        val response = capabilityManager.request(CapabilityManager.CAP_CHAT_PASS, request)
        if (response is LLSDMap && response.getInt("success") == 1) {
            val session = IMSession(
                sessionId = sessionId,
                type = SessionType.CONFERENCE,
                name = name,
                participants = participants.toMutableList(),
                isActive = true
            )
            sessions[sessionId] = session
            updateSessionList()
            return sessionId
        }
        
        return null
    }
    
    /**
     * Leave session
     */
    suspend fun leaveSession(sessionId: UUID) {
        val request = LLSDMap().apply {
            this["method"] = LLSDString("close session")
            this["session-id"] = LLSDString(sessionId.toString())
        }
        
        capabilityManager.request(CapabilityManager.CAP_CHAT_PASS, request)
        
        sessions[sessionId]?.isActive = false
        updateSessionList()
    }
    
    /**
     * Send typing indicator
     */
    fun sendTypingStart(sessionId: UUID) {
        scope.launch {
            try {
                // Send typing indicator via ImprovedInstantMessage with dialog type
                val session = sessions[sessionId] ?: return@launch
                
                // Get the target - for P2P use first participant, for groups use session ID
                val targetId = if (session.type == SessionType.P2P && session.participants.isNotEmpty()) {
                    session.participants.first()
                } else {
                    sessionId
                }
                
                val payload = java.nio.ByteBuffer.allocate(100).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                
                // AgentData block - UUIDs are big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())  // Proper session ID
                
                // MessageBlock
                payload.put(0)  // FromGroup (BOOL)
                payload.putUUID(targetId)  // ToAgentID
                payload.putInt(0)  // ParentEstateID
                payload.putUUID(UUID(0, 0))  // RegionID (empty)
                payload.putFloat(0f)  // Position X
                payload.putFloat(0f)  // Position Y
                payload.putFloat(0f)  // Position Z
                payload.put(0)  // Offline
                payload.put(IM_TYPING_START.toByte())  // Dialog
                payload.putUUID(sessionId)  // ID (session/IM ID)
                payload.putInt((System.currentTimeMillis() / 1000).toInt())  // Timestamp
                
                // FromAgentName (Variable 1)
                payload.put(0)  // Empty name
                // Message (Variable 2)
                payload.putShort(0)
                // BinaryBucket (Variable 2)
                payload.putShort(0)
                
                // EstateBlock
                payload.putInt(0)  // EstateID
                
                // MetaData Variable block - count = 0
                payload.put(0)
                
                udpConnection.sendPacket(
                    com.linkpoint.protocol.messages.MessageIds.IMPROVED_INSTANT_MESSAGE,
                    payload.array().copyOf(payload.position()),
                    reliable = false
                )
                Log.d(TAG, "Sent typing start to session $sessionId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send typing start", e)
            }
        }
    }
    
    /**
     * Stop typing indicator
     */
    fun sendTypingStop(sessionId: UUID) {
        scope.launch {
            try {
                val session = sessions[sessionId] ?: return@launch
                
                // Get the target - for P2P use first participant, for groups use session ID
                val targetId = if (session.type == SessionType.P2P && session.participants.isNotEmpty()) {
                    session.participants.first()
                } else {
                    sessionId
                }
                
                val payload = java.nio.ByteBuffer.allocate(100).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                
                // AgentData block - UUIDs are big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())  // Proper session ID
                
                // MessageBlock
                payload.put(0)  // FromGroup (BOOL)
                payload.putUUID(targetId)  // ToAgentID
                payload.putInt(0)  // ParentEstateID
                payload.putUUID(UUID(0, 0))  // RegionID (empty)
                payload.putFloat(0f)  // Position X
                payload.putFloat(0f)  // Position Y
                payload.putFloat(0f)  // Position Z
                payload.put(0)  // Offline
                payload.put(IM_TYPING_STOP.toByte())  // Dialog
                payload.putUUID(sessionId)  // ID (session/IM ID)
                payload.putInt((System.currentTimeMillis() / 1000).toInt())  // Timestamp
                
                // FromAgentName (Variable 1)
                payload.put(0)  // Empty name
                // Message (Variable 2)
                payload.putShort(0)
                // BinaryBucket (Variable 2)
                payload.putShort(0)
                
                // EstateBlock
                payload.putInt(0)  // EstateID
                
                // MetaData Variable block - count = 0
                payload.put(0)
                
                udpConnection.sendPacket(
                    com.linkpoint.protocol.messages.MessageIds.IMPROVED_INSTANT_MESSAGE,
                    payload.array().copyOf(payload.position()),
                    reliable = false
                )
                Log.d(TAG, "Sent typing stop to session $sessionId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send typing stop", e)
            }
        }
    }
    
    /**
     * Get session messages
     */
    fun getSessionMessages(sessionId: UUID): List<IMMessage> {
        return sessionMessages[sessionId]?.toList() ?: emptyList()
    }
    
    /**
     * Mark session as read
     */
    fun markAsRead(sessionId: UUID) {
        _unreadCounts.value = _unreadCounts.value - sessionId
    }
    
    private fun addMessage(sessionId: UUID, message: IMMessage) {
        val messages = sessionMessages.getOrPut(sessionId) { mutableListOf() }
        messages.add(message)
        
        if (messages.size > MAX_SESSION_HISTORY) {
            messages.removeAt(0)
        }
        
        // Update unread count
        if (!message.isOutgoing) {
            val current = _unreadCounts.value[sessionId] ?: 0
            _unreadCounts.value = _unreadCounts.value + (sessionId to (current + 1))
        }
        
        scope.launch {
            _messageFlow.emit(message)
        }
    }
    
    private fun updateSessionList() {
        _activeSessions.value = sessions.values
            .filter { it.isActive }
            .sortedByDescending { sessionMessages[it.sessionId]?.lastOrNull()?.timestamp ?: 0L }
    }
    
    private fun computeP2PSessionId(agent1: UUID, agent2: UUID): UUID {
        // P2P session ID is XOR of agent IDs
        return UUID(
            agent1.mostSignificantBits xor agent2.mostSignificantBits,
            agent1.leastSignificantBits xor agent2.leastSignificantBits
        )
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

data class IMSession(
    val sessionId: UUID,
    val type: SessionType,
    val name: String,
    val participants: MutableList<UUID>,
    var isActive: Boolean = false,
    var typingParticipants: Set<UUID> = emptySet()
)

enum class SessionType {
    P2P, GROUP, CONFERENCE
}

data class IMMessage(
    val id: UUID,
    val sessionId: UUID,
    val fromAgentId: UUID,
    val fromName: String,
    val message: String,
    val dialogType: Int,
    val timestamp: Long,
    val isOutgoing: Boolean = false
)

sealed class IMSessionEvent {
    data class Invited(val session: IMSession) : IMSessionEvent()
    data class Joined(val session: IMSession) : IMSessionEvent()
    data class Left(val sessionId: UUID) : IMSessionEvent()
    data class ParticipantJoined(val sessionId: UUID, val agentId: UUID) : IMSessionEvent()
    data class ParticipantLeft(val sessionId: UUID, val agentId: UUID) : IMSessionEvent()
}
