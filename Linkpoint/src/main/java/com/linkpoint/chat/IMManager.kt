package com.linkpoint.chat

import android.util.Log
import com.linkpoint.linden.llmessage.IMType
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.SLMessagePackers
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.push.PushEvent
import com.linkpoint.push.PushEventBus
import com.linkpoint.push.PushEventType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Instant Message manager
 * Handles private IMs, group chat, and conference chat.
 *
 * Inbound IM processing and flow emissions are serialized on the MessagingDispatcher
 * "MessageThread" to avoid mixing Default/IO dispatchers with messaging state.
 */
class IMManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    companion object {
        private const val TAG = "IMManager"
        private const val MAX_SESSION_HISTORY = 200
        private fun logDebug(tag: String, msg: String) {
            try { Log.d(tag, msg) } catch (_: Throwable) { println("[$tag] $msg") }
        }

        private fun logWarn(tag: String, msg: String, tr: Throwable? = null) {
            try { if (tr != null) Log.w(tag, msg, tr) else Log.w(tag, msg) } catch (_: Throwable) { println("[$tag] $msg ${tr?.message ?: ""}") }
        }

        private fun logError(tag: String, msg: String, tr: Throwable? = null) {
            try { if (tr != null) Log.e(tag, msg, tr) else Log.e(tag, msg) } catch (_: Throwable) { println("[$tag] $msg ${tr?.message ?: ""}") }
        }
    }

    private val scope = CoroutineScope(MessagingDispatcher.dispatcher + SupervisorJob())

    // Active IM sessions
    private val sessions = ConcurrentHashMap<UUID, IMSession>()

    // Session messages
    private val sessionMessages = ConcurrentHashMap<UUID, MutableList<SLChatEvent>>()

    private val lastMessageBySession = ConcurrentHashMap<UUID, SLChatEvent>()
    
    // Events
    private val _messageFlow = MutableSharedFlow<SLChatEvent>(replay = 0, extraBufferCapacity = 64)
    val messageFlow: SharedFlow<SLChatEvent> = _messageFlow
    private val _sessionFlow = MutableSharedFlow<IMSessionEvent>(replay = 0, extraBufferCapacity = 16)
    val sessionFlow: SharedFlow<IMSessionEvent> = _sessionFlow

    // Active sessions list
    private val _activeSessions = MutableStateFlow<List<IMSession>>(emptyList())
    val activeSessions: StateFlow<List<IMSession>> = _activeSessions

    // Unread counts
    private val _unreadCounts = MutableStateFlow<Map<UUID, Int>>(emptyMap())
    val unreadCounts: StateFlow<Map<UUID, Int>> = _unreadCounts

    private val processedPushIds = ConcurrentHashMap.newKeySet<String>()
    private val pendingSyncSessions = MutableStateFlow<Set<UUID>>(emptySet())
    val syncNeededSessions: StateFlow<Set<UUID>> = pendingSyncSessions

    private val startedGroupSessions = ConcurrentHashMap.newKeySet<UUID>()

    private val pendingGroupMessages = ConcurrentHashMap<UUID, MutableList<String>>()

    private fun outboundIdentity(): AgentIdentity = AgentIdentity(
        agentId = agentId,
        sessionId = udpConnection.getSessionId(),
        circuitCode = udpConnection.getCircuitCode()
    ).requireValid("IMManager outbound packet")

    init {
        capabilityManager.registerEventHandler(
            "ChatterBoxInvitation",
            this,
            MessagingDispatcher.dispatcher
        )
        capabilityManager.registerEventHandler(
            "ChatterBoxSessionEventReply",
            this,
            MessagingDispatcher.dispatcher
        )
        capabilityManager.registerEventHandler(
            "ChatterBoxSessionStartReply",
            this,
            MessagingDispatcher.dispatcher
        )

        observePushEvents()
    }

    private fun observePushEvents() {
        scope.launch {
            PushEventBus.events.collect { event ->
                handlePushWakeEvent(event)
            }
        }
    }

    private fun handlePushWakeEvent(event: PushEvent) {
        if (!processedPushIds.add(event.dedupeId)) {
            return
        }

        val sessionId = event.sessionId ?: return
        when (event.type) {
            PushEventType.IM, PushEventType.GROUP_NOTICE -> {
                pendingSyncSessions.update { it + sessionId }
            }
            PushEventType.UNKNOWN -> Unit
        }

        if (processedPushIds.size > MAX_SESSION_HISTORY * 4) {
            processedPushIds.clear()
        }
    }

    /**
     * Called by UI when opening a session after push wakeup.
     * This allows queue reconciliation with event queue state.
     */
    fun reconcileSessionOnOpen(sessionId: UUID) {
        pendingSyncSessions.update { it - sessionId }
    }

    override fun onEvent(message: String, body: LLSDMap) {
        scope.launch {
            when (message) {
                "ChatterBoxInvitation" -> handleInvitation(body)
                "ChatterBoxSessionEventReply" -> handleSessionEvent(body)
                "ChatterBoxSessionStartReply" -> handleSessionStart(body)
            }
        }
    }

    private fun handleInvitation(body: LLSDMap) {
        val inviteInfo = body.getMap("instantmessage")?.getMap("message_params") ?: return

        val sessionId = UUID.fromString(inviteInfo.getString("id") ?: return)
        val fromAgentId = UUID.fromString(inviteInfo.getString("from_id") ?: return)
        val fromName = inviteInfo.getString("from_name") ?: "Unknown"
        val message = inviteInfo.getString("message") ?: ""
        val typeInt = inviteInfo.getInt("dialog") ?: 0
        val dialogType = IMType.fromValue(typeInt)
        
        val sessionType = when (dialogType) {
            IMType.SESSION_GROUP_START -> SessionType.GROUP
            IMType.SESSION_CONFERENCE_START -> SessionType.CONFERENCE
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
                val agentId = body.getString("agent_id")?.let {
                    try { UUID.fromString(it) } catch (e: Exception) { null }
                }
                if (agentId != null && agentId !in session.participants) {
                    session.participants.add(agentId)
                    scope.launch {
                        _sessionFlow.emit(IMSessionEvent.ParticipantJoined(sessionId, agentId))
                    }
                    logDebug(TAG, "Participant $agentId joined session $sessionId")
                }
            }
            "leave" -> {
                val agentId = body.getString("agent_id")?.let {
                    try { UUID.fromString(it) } catch (e: Exception) { null }
                }
                if (agentId != null) {
                    session.participants.remove(agentId)
                    scope.launch {
                        _sessionFlow.emit(IMSessionEvent.ParticipantLeft(sessionId, agentId))
                    }
                    logDebug(TAG, "Participant $agentId left session $sessionId")
                }
            }
            "typing" -> {
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
                logDebug(TAG, "Unknown session event: $eventType for session $sessionId")
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
                logDebug(TAG, "Session $sessionId started successfully")
            }
            if (startedGroupSessions.add(sessionId)) {
                val queued = pendingGroupMessages.remove(sessionId)
                if (queued != null && queued.isNotEmpty()) {
                    logDebug(TAG, "Draining ${queued.size} pending group messages for $sessionId")
                    queued.forEach { sendGroupChatDialog17(sessionId, it) }
                }
            }
            updateSessionList()
        } else {
            val error = body.getString("error") ?: "Unknown error"
            logError(TAG, "Failed to start session $sessionId: $error")
            pendingGroupMessages.remove(sessionId)
            startedGroupSessions.remove(sessionId)
            sessions.remove(sessionId)
            lastMessageBySession.remove(sessionId)
            scope.launch {
                _sessionFlow.emit(IMSessionEvent.Left(sessionId))
            }
            updateSessionList()
        }
    }

    fun handleIncomingIM(
        fromAgentId: UUID,
        fromName: String,
        message: String,
        sessionId: UUID,
        dialogType: IMType,
        timestamp: Long,
        binaryBucket: ByteArray = byteArrayOf()
    ) {
        scope.launch {
            val session = sessions.getOrPut(sessionId) {
                IMSession(
                    sessionId = sessionId,
                    type = if (dialogType == IMType.SESSION_GROUP_START) SessionType.GROUP else SessionType.P2P,
                    name = fromName,
                    participants = mutableListOf(fromAgentId)
                )
            }

            when (dialogType) {
                IMType.TYPING_START -> {
                    session.typingParticipants = session.typingParticipants + fromAgentId
                }
                IMType.TYPING_STOP -> {
                    session.typingParticipants = session.typingParticipants - fromAgentId
                }
                else -> {
                    val event = decodeToSLChatEvent(
                        fromAgentId = fromAgentId,
                        fromName = fromName,
                        message = message,
                        sessionId = sessionId,
                        dialogType = dialogType,
                        timestamp = timestamp,
                        binaryBucket = binaryBucket,
                        isOutgoing = false
                    )
                    addMessage(sessionId, event)
                    session.typingParticipants = session.typingParticipants - fromAgentId
                    pendingSyncSessions.update { it - sessionId }
                }
            }

            updateSessionList()
        }
    }


    fun decodeToSLChatEvent(
        fromAgentId: UUID,
        fromName: String,
        message: String,
        sessionId: UUID,
        dialogType: IMType,
        timestamp: Long,
        binaryBucket: ByteArray = byteArrayOf(),
        isOutgoing: Boolean = false
    ): SLChatEvent {
        return try {
            when (dialogType) {
                IMType.FRIENDSHIP_OFFERED -> SLChatEvent.FriendshipOffer(
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message.ifBlank { "$fromName has offered you friendship." },
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = isOutgoing
                )
                IMType.FRIENDSHIP_ACCEPTED, IMType.FRIENDSHIP_DECLINED -> SLChatEvent.FriendshipResult(
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message,
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = isOutgoing,
                    isAccepted = dialogType == IMType.FRIENDSHIP_ACCEPTED
                )
                IMType.GROUP_INVITATION -> {
                    val joinFee = if (binaryBucket.size >= 4) {
                        try {
                            ByteBuffer.wrap(binaryBucket).order(ByteOrder.BIG_ENDIAN).int
                        } catch (e: Exception) {
                            0
                        }
                    } else 0
                    SLChatEvent.GroupInvitation(
                        sessionId = sessionId,
                        fromAgentId = fromAgentId,
                        fromName = fromName,
                        message = message.ifBlank { "$fromName invited you to join a group." },
                        dialogType = dialogType,
                        timestamp = timestamp,
                        isOutgoing = isOutgoing,
                        groupId = fromAgentId,
                        joinFee = joinFee
                    )
                }
                IMType.LURE_USER, IMType.GODLIKE_LURE_USER -> SLChatEvent.TeleportLure(
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message.ifBlank { "$fromName has offered to teleport you." },
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = isOutgoing,
                    lureId = sessionId,
                    regionName = message
                )
                IMType.INVENTORY_OFFERED, IMType.TASK_INVENTORY_OFFERED -> SLChatEvent.InventoryOffer(
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message.ifBlank { "$fromName offered you an item." },
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = isOutgoing
                )
                IMType.MESSAGEBOX, IMType.FROM_TASK_AS_ALERT -> SLChatEvent.System(
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message,
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = isOutgoing
                )
                else -> SLChatEvent.Text(
                    sessionId = sessionId,
                    fromAgentId = fromAgentId,
                    fromName = fromName,
                    message = message,
                    dialogType = dialogType,
                    timestamp = timestamp,
                    isOutgoing = isOutgoing
                )
            }
        } catch (e: Exception) {
            logWarn(TAG, "Failed to decode binary bucket or dialog $dialogType, falling back to text event", e)
            SLChatEvent.Text(
                sessionId = sessionId,
                fromAgentId = fromAgentId,
                fromName = fromName,
                message = message,
                dialogType = dialogType,
                timestamp = timestamp,
                isOutgoing = isOutgoing
            )
        }
    }
    
    fun sendIM(sessionId: UUID, message: String) {
        val session = sessions[sessionId]
        if (session == null) {
            logWarn(TAG, "sendIM: no session $sessionId — message dropped")
            return
        }

        val outgoing = SLChatEvent.Text(
            id = UUID.randomUUID(),
            sessionId = sessionId,
            fromAgentId = agentId,
            fromName = "You",
            message = message,
            dialogType = IMType.SESSION_SEND,
            timestamp = System.currentTimeMillis(),
            isOutgoing = true
        )
        addMessage(sessionId, outgoing)

        scope.launch {
            val ok = try {
                when (session.type) {
                    SessionType.P2P -> sendP2PInstantMessage(session, message)
                    SessionType.GROUP, SessionType.CONFERENCE ->
                        sendSessionChat(session, message)
                }
            } catch (e: Exception) {
                logError(TAG, "sendIM failed for session $sessionId", e)
                false
            }
            if (!ok) {
                addMessage(
                    sessionId,
                    SLChatEvent.System(
                        id = UUID.randomUUID(),
                        sessionId = sessionId,
                        fromAgentId = UUID(0, 0),
                        fromName = "System",
                        message = "Failed to send message — check connection",
                        dialogType = IMType.NOTHING_SPECIAL,
                        timestamp = System.currentTimeMillis(),
                        isOutgoing = false
                    )
                )
            }
        }
    }

    fun respondToFriendshipOffer(event: SLChatEvent.FriendshipOffer, accept: Boolean) {
        if (event.actionState != CardActionState.PENDING) {
            logDebug(TAG, "respondToFriendshipOffer already processed (state=${event.actionState})")
            return
        }
        event.actionState = if (accept) CardActionState.ACCEPTED else CardActionState.DECLINED
        val dialog = if (accept) IMType.FRIENDSHIP_ACCEPTED else IMType.FRIENDSHIP_DECLINED
        val responseText = if (accept) "Accepted friendship offer" else "Declined friendship offer"
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = event.fromAgentId,
            dialog = dialog,
            id = event.sessionId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = responseText
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "Friendship response (dialog=$dialog) sent to ${event.fromAgentId}")
    }

    fun respondToGroupInvitation(event: SLChatEvent.GroupInvitation, accept: Boolean) {
        if (event.actionState != CardActionState.PENDING) {
            logDebug(TAG, "respondToGroupInvitation already processed (state=${event.actionState})")
            return
        }
        event.actionState = if (accept) CardActionState.ACCEPTED else CardActionState.DECLINED
        val dialog = if (accept) IMType.GROUP_INVITATION_ACCEPT else IMType.GROUP_INVITATION_DECLINE
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = event.groupId,
            dialog = dialog,
            id = event.sessionId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = "",
            binaryBucket = byteArrayOf()
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "Group invite response (dialog=$dialog) sent to ${event.groupId}")
    }

    fun respondToTeleportLure(event: SLChatEvent.TeleportLure, accept: Boolean) {
        if (event.actionState != CardActionState.PENDING) {
            logDebug(TAG, "respondToTeleportLure already processed (state=${event.actionState})")
            return
        }
        event.actionState = if (accept) CardActionState.ACCEPTED else CardActionState.DECLINED
        val dialog = if (accept) IMType.LURE_ACCEPTED else IMType.LURE_DECLINED
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = event.fromAgentId,
            dialog = dialog,
            id = event.lureId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = ""
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "Teleport lure response (dialog=$dialog) sent to ${event.fromAgentId}")
    }

    fun respondToInventoryOffer(event: SLChatEvent.InventoryOffer, accept: Boolean) {
        if (event.actionState != CardActionState.PENDING) {
            logDebug(TAG, "respondToInventoryOffer already processed (state=${event.actionState})")
            return
        }
        event.actionState = if (accept) CardActionState.ACCEPTED else CardActionState.DECLINED
        val dialog = if (accept) IMType.INVENTORY_ACCEPTED else IMType.INVENTORY_DECLINED
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = event.fromAgentId,
            dialog = dialog,
            id = event.sessionId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = ""
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "Inventory offer response (dialog=$dialog) sent to ${event.fromAgentId}")
    }

    private fun sendP2PInstantMessage(session: IMSession, message: String): Boolean {
        val targetId = session.participants.firstOrNull()
        if (targetId == null) {
            logWarn(TAG, "sendP2PInstantMessage: session ${session.sessionId} has no participants")
            return false
        }
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = targetId,
            dialog = IMType.NOTHING_SPECIAL,
            id = session.sessionId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = message
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "P2P IM sent to $targetId (session=${session.sessionId})")
        return true
    }

    private fun sendSessionChat(session: IMSession, message: String): Boolean {
        val sessionId = session.sessionId
        if (session.type == SessionType.GROUP && sessionId !in startedGroupSessions) {
            pendingGroupMessages
                .computeIfAbsent(sessionId) { mutableListOf() }
                .add(message)
            sendGroupSessionStart(sessionId)
            logDebug(TAG, "Group chat queued for $sessionId (waiting for session-start reply)")
            return true
        }
        return sendGroupChatDialog17(sessionId, message)
    }

    private fun sendGroupSessionStart(groupId: UUID) {
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = groupId,
            dialog = IMType.SESSION_GROUP_START,
            id = groupId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = "",
            binaryBucket = byteArrayOf(0)
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "Group session-start (Dialog=15) sent for $groupId")
    }

    private fun sendGroupChatDialog17(sessionId: UUID, message: String): Boolean {
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = sessionId,
            dialog = IMType.SESSION_SEND,
            id = sessionId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = message,
            binaryBucket = byteArrayOf(0)
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        logDebug(TAG, "Group chat (Dialog=17) sent to session $sessionId")
        return true
    }

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

    fun startGroupSessionLocal(groupId: UUID, groupName: String): UUID {
        sessions.getOrPut(groupId) {
            IMSession(
                sessionId = groupId,
                type = SessionType.GROUP,
                name = groupName,
                participants = mutableListOf(),
                isActive = false
            )
        }
        updateSessionList()
        return groupId
    }

    suspend fun startConferenceSession(participants: List<UUID>, name: String): UUID? {
        val sessionId = UUID.randomUUID()

        val request = LLSDMap().apply {
            this["method"] = LLSDString("start conference")
            this["session-id"] = LLSDString(sessionId.toString())
            this["params"] = LLSDMap().apply {
                this["type"] = LLSDInteger(7)
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

    suspend fun leaveSession(sessionId: UUID) {
        val request = LLSDMap().apply {
            this["method"] = LLSDString("close session")
            this["session-id"] = LLSDString(sessionId.toString())
        }

        capabilityManager.request(CapabilityManager.CAP_CHAT_PASS, request)

        sessions[sessionId]?.isActive = false
        updateSessionList()
    }

    fun sendTypingStart(sessionId: UUID) {
        scope.launch { sendTypingPacket(sessionId, IMType.TYPING_START) }
    }

    fun sendTypingStop(sessionId: UUID) {
        scope.launch { sendTypingPacket(sessionId, IMType.TYPING_STOP) }
    }

    private fun sendTypingPacket(sessionId: UUID, dialog: IMType) {
        try {
            val session = sessions[sessionId] ?: return
            val targetId = if (session.type == SessionType.P2P && session.participants.isNotEmpty()) {
                session.participants.first()
            } else {
                sessionId
            }
            val payload = SLMessagePackers.packImprovedInstantMessage(
                identity = outboundIdentity(),
                fromGroup = false,
                toAgentId = targetId,
                dialog = dialog,
                id = sessionId,
                timestamp = (System.currentTimeMillis() / 1000).toInt(),
                fromAgentName = "",
                message = ""
            )
            udpConnection.sendPacket(
                MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
                payload,
                reliable = false
            )
            logDebug(TAG, "Sent typing dialog=$dialog to session $sessionId")
        } catch (e: Exception) {
            logError(TAG, "Failed to send typing packet (dialog=$dialog)", e)
        }
    }
    fun getSessionMessages(sessionId: UUID): List<SLChatEvent> {
        return sessionMessages[sessionId]?.toList() ?: emptyList()
    }

    fun getLastSessionMessage(sessionId: UUID): SLChatEvent? {
        return lastMessageBySession[sessionId]
    }

    fun markAsRead(sessionId: UUID) {
        _unreadCounts.value = _unreadCounts.value - sessionId
        pendingSyncSessions.update { it - sessionId }
    }
    private fun addMessage(sessionId: UUID, message: SLChatEvent) {
        val messages = sessionMessages.getOrPut(sessionId) { mutableListOf() }
        messages.add(message)
        lastMessageBySession[sessionId] = message

        if (messages.size > MAX_SESSION_HISTORY) {
            messages.removeAt(0)
        }

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

typealias IMMessage = SLChatEvent

sealed class IMSessionEvent {
    data class Invited(val session: IMSession) : IMSessionEvent()
    data class Joined(val session: IMSession) : IMSessionEvent()
    data class Left(val sessionId: UUID) : IMSessionEvent()
    data class ParticipantJoined(val sessionId: UUID, val agentId: UUID) : IMSessionEvent()
    data class ParticipantLeft(val sessionId: UUID, val agentId: UUID) : IMSessionEvent()
}
