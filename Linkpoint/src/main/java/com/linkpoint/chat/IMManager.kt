package com.linkpoint.chat

import android.util.Log
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
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

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
    
    private val scope = CoroutineScope(MessagingDispatcher.dispatcher + SupervisorJob())
    
    // Active IM sessions
    private val sessions = ConcurrentHashMap<UUID, IMSession>()
    
    // Session messages
    private val sessionMessages = ConcurrentHashMap<UUID, MutableList<IMMessage>>()

    // Last message per session — ConcurrentHashMap provides thread-safe O(1)
    // reads for composable callers on the Main thread while addMessage writes
    // on MessagingDispatcher, avoiding the data race that MutableList.lastOrNull()
    // would introduce (ArrayList.size + elementData access are not atomic).
    private val lastMessageBySession = ConcurrentHashMap<UUID, IMMessage>()
    
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

    private val processedPushIds = ConcurrentHashMap.newKeySet<String>()
    private val pendingSyncSessions = MutableStateFlow<Set<UUID>>(emptySet())
    val syncNeededSessions: StateFlow<Set<UUID>> = pendingSyncSessions

    /**
     * Groups for which we have already received `ChatterBoxSessionStartReply`
     * (or for which we sent the first `ImprovedInstantMessage(Dialog=15)` and
     * are willing to send subsequent Dialog=17 messages without re-bootstrap).
     *
     * Mirrors Lumiya's `SLAgentCircuit.startedGroupSessions`
     * (`lumiya_decompiled_source/.../slproto/SLAgentCircuit.java:1671-1696`).
     */
    private val startedGroupSessions = ConcurrentHashMap.newKeySet<UUID>()

    /**
     * Group chat lines queued while we wait for the simulator to ack the
     * Dialog=15 session-start. Drained from [handleSessionStart] when the
     * `ChatterBoxSessionStartReply` event arrives.
     *
     * Mirrors Lumiya's `pendingGroupMessages`. Without this queue the very
     * first message to a fresh group is dropped server-side because the
     * chatterbox session isn't established yet.
     */
    private val pendingGroupMessages = ConcurrentHashMap<UUID, MutableList<String>>()

    private fun outboundIdentity(): AgentIdentity = AgentIdentity(
        agentId = agentId,
        sessionId = udpConnection.getSessionId(),
        circuitCode = udpConnection.getCircuitCode()
    ).requireValid("IMManager outbound packet")
    
    init {
        // Register for event queue events
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
                pendingSyncSessions.value = pendingSyncSessions.value + sessionId
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
        pendingSyncSessions.value = pendingSyncSessions.value - sessionId
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
                    session.participants.add(agentId)
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
                    session.participants.remove(agentId)
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
            // For groups, mark the chatterbox as live and drain any messages
            // that were queued while we waited for the Dialog=15 reply.
            // Mirrors `SLAgentCircuit.HandleChatterBoxSessionStartReply:368-392`.
            if (startedGroupSessions.add(sessionId)) {
                val queued = pendingGroupMessages.remove(sessionId)
                if (queued != null && queued.isNotEmpty()) {
                    Log.d(TAG, "Draining ${queued.size} pending group messages for $sessionId")
                    queued.forEach { sendGroupChatDialog17(sessionId, it) }
                }
            }
            updateSessionList()
        } else {
            val error = body.getString("error") ?: "Unknown error"
            Log.e(TAG, "Failed to start session $sessionId: $error")
            // Drop the queued group lines too — the session never came up,
            // and resending them would loop on the Dialog=15 path.
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
        scope.launch {
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
                    pendingSyncSessions.value = pendingSyncSessions.value - sessionId
                }
            }
            
            updateSessionList()
        }
    }
    
    /**
     * Send an IM in the given session.
     *
     * All three SL chat dispatch types (P2P, GROUP, CONFERENCE) ride on
     * the **UDP `ImprovedInstantMessage`** message — there is no
     * sendchat-via-cap path in real Second Life. (Lumiya enumerates the
     * `ChatSessionRequest` cap but only its voice module reads the URL;
     * `lumiya_decompiled_source/.../slproto/caps/SLCaps.java:45`,
     * `slproto/modules/voice/SLVoice.java:102` — no chat POSTs go to it.)
     *
     * - P2P: `Dialog=0 (IM_NOTHING_SPECIAL)`, `ID = self ⊕ other`,
     *   `BinaryBucket` empty.
     * - GROUP: first call to a fresh group sends `Dialog=15` and queues
     *   the line in [pendingGroupMessages]; on `ChatterBoxSessionStartReply`
     *   the queue drains as `Dialog=17` messages. Subsequent calls send
     *   `Dialog=17` directly. `ID = ToAgentID = groupId`,
     *   `BinaryBucket = byteArrayOf(0)`.
     * - CONFERENCE: cap brings up the session via [startConferenceSession]
     *   (the only path that legitimately uses the cap), then chat lines
     *   ride `Dialog=17` UDP IM the same as groups.
     *
     * The local-history entry is appended before the network call so the
     * UI shows the user's own message immediately; on send failure we
     * append a system-style error row so the user knows it didn't go.
     */
    fun sendIM(sessionId: UUID, message: String) {
        val session = sessions[sessionId]
        if (session == null) {
            Log.w(TAG, "sendIM: no session $sessionId — message dropped")
            return
        }

        // Show in local history immediately for responsive UX.
        val outgoing = IMMessage(
            id = UUID.randomUUID(),
            sessionId = sessionId,
            fromAgentId = agentId,
            fromName = "You",
            message = message,
            dialogType = IM_SESSION_SEND,
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
                Log.e(TAG, "sendIM failed for session $sessionId", e)
                false
            }
            if (!ok) {
                addMessage(
                    sessionId,
                    IMMessage(
                        id = UUID.randomUUID(),
                        sessionId = sessionId,
                        fromAgentId = UUID(0, 0),
                        fromName = "System",
                        message = "Failed to send message — check connection",
                        dialogType = IM_NOTHING_SPECIAL,
                        timestamp = System.currentTimeMillis(),
                        isOutgoing = false
                    )
                )
            }
        }
    }

    /**
     * Build and send a 1:1 IM as an `ImprovedInstantMessage` UDP packet.
     *
     * Wire format is produced by [SLMessagePackers.packImprovedInstantMessage]
     * — the canonical builder also used by typing indicators and group chat,
     * so a wire-format regression in any one of those three call sites is
     * impossible. Reliability flag mirrors Lumiya
     * (`SLAgentCircuit.SendInstantMessage:737`: `isReliable = true`).
     */
    private fun sendP2PInstantMessage(session: IMSession, message: String): Boolean {
        val targetId = session.participants.firstOrNull()
        if (targetId == null) {
            Log.w(TAG, "sendP2PInstantMessage: session ${session.sessionId} has no participants")
            return false
        }
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = targetId,
            dialog = IM_NOTHING_SPECIAL,
            id = session.sessionId,
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",  // server replaces with our resolved name
            message = message
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        Log.d(TAG, "P2P IM sent to $targetId (session=${session.sessionId})")
        return true
    }

    /**
     * Send a group chat line. Group chat in SL is **UDP** — Lumiya does not
     * use the `ChatSessionRequest` HTTP cap for chat at all (see
     * `lumiya_decompiled_source/.../slproto/caps/SLCaps.java:45` — the cap
     * URL is read but only by the voice module).
     *
     * Two-stage flow, copied from
     * `SLAgentCircuit.SendGroupInstantMessage:1671-1696`:
     *
     * 1. **First message to a fresh group**: enqueue the line in
     *    [pendingGroupMessages], send `ImprovedInstantMessage(Dialog=15)`
     *    with `BinaryBucket = byteArrayOf(0)` to bring up the chatterbox
     *    session. The simulator replies on EventQueue with
     *    `ChatterBoxSessionStartReply`, which [handleSessionStart] processes
     *    by adding the group to [startedGroupSessions] and draining the
     *    pending queue.
     * 2. **Subsequent messages**: send `ImprovedInstantMessage(Dialog=17)`
     *    with `ID = ToAgentID = groupId` and `BinaryBucket = byteArrayOf(0)`
     *    directly.
     *
     * Conferences (ad-hoc multi-user) use a different bring-up path
     * (`startConferenceSession` POSTs to the cap with `method=start
     * conference`) but once started, chat lines also go via Dialog=17 UDP IM.
     */
    private fun sendSessionChat(session: IMSession, message: String): Boolean {
        val sessionId = session.sessionId
        if (session.type == SessionType.GROUP && sessionId !in startedGroupSessions) {
            // Queue the line first so handleSessionStart can flush it on
            // ChatterBoxSessionStartReply, then trigger Dialog=15 bring-up.
            pendingGroupMessages
                .computeIfAbsent(sessionId) { mutableListOf() }
                .add(message)
            sendGroupSessionStart(sessionId)
            Log.d(TAG, "Group chat queued for $sessionId (waiting for session-start reply)")
            return true
        }
        return sendGroupChatDialog17(sessionId, message)
    }

    /**
     * Send `ImprovedInstantMessage(Dialog=15)` to bring up a group chatterbox
     * session. Called from [sendSessionChat] when the group has not yet been
     * acknowledged by the simulator. Reliable (Lumiya
     * `SLAgentCircuit.SendGroupSessionStart:736-737`).
     */
    private fun sendGroupSessionStart(groupId: UUID) {
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = groupId,        // group UUID, per Lumiya
            dialog = IM_SESSION_GROUP_START,
            id = groupId,                // session UUID == group UUID
            timestamp = (System.currentTimeMillis() / 1000).toInt(),
            fromAgentName = "You",
            message = "",
            // SL rejects empty buckets on Dialog=15; Lumiya sends new byte[1]
            binaryBucket = byteArrayOf(0)
        )
        udpConnection.sendPacket(
            MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
            payload,
            reliable = true
        )
        Log.d(TAG, "Group session-start (Dialog=15) sent for $groupId")
    }

    /**
     * Send a `ImprovedInstantMessage(Dialog=17)` group chat line to a
     * group/conference session that's already established server-side.
     */
    private fun sendGroupChatDialog17(sessionId: UUID, message: String): Boolean {
        val payload = SLMessagePackers.packImprovedInstantMessage(
            identity = outboundIdentity(),
            fromGroup = false,
            toAgentId = sessionId,       // for groups, ToAgentID = groupId
            dialog = IM_SESSION_SEND,    // Dialog=17
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
        Log.d(TAG, "Group chat (Dialog=17) sent to session $sessionId")
        return true
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
     * Create or look up the local group-chat session record.
     *
     * Group chat in SL doesn't require a cap call to bring up the
     * chatterbox session — the first `ImprovedInstantMessage(Dialog=15)`
     * does that, and the simulator confirms via the EventQueue
     * `ChatterBoxSessionStartReply`. So this function is purely local:
     * it ensures `sessions[groupId]` exists with `type=GROUP` so that
     * subsequent calls to [sendIM] dispatch to the group path.
     *
     * Idempotent. Safe to call from any thread.
     */
    fun startGroupSessionLocal(groupId: UUID, groupName: String): UUID {
        sessions.getOrPut(groupId) {
            IMSession(
                sessionId = groupId,
                type = SessionType.GROUP,
                name = groupName,
                participants = mutableListOf(),
                isActive = false  // Activated when ChatterBoxSessionStartReply arrives.
            )
        }
        updateSessionList()
        return groupId
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
        scope.launch { sendTypingPacket(sessionId, IM_TYPING_START) }
    }

    /**
     * Stop typing indicator
     */
    fun sendTypingStop(sessionId: UUID) {
        scope.launch { sendTypingPacket(sessionId, IM_TYPING_STOP) }
    }

    private fun sendTypingPacket(sessionId: UUID, dialog: Int) {
        try {
            val session = sessions[sessionId] ?: return
            // For P2P use the first participant; for groups/conferences the
            // session UUID is the target (and the ID).
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
                // Lumiya sends an empty FromAgentName here; SL accepts a
                // single-NUL Variable 1 (the packer enforces the NUL).
                fromAgentName = "",
                message = ""
            )
            udpConnection.sendPacket(
                MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
                payload,
                reliable = false
            )
            Log.d(TAG, "Sent typing dialog=$dialog to session $sessionId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send typing packet (dialog=$dialog)", e)
        }
    }
    
    /**
     * Get session messages
     */
    fun getSessionMessages(sessionId: UUID): List<IMMessage> {
        return sessionMessages[sessionId]?.toList() ?: emptyList()
    }

    /**
     * Returns the last message in [sessionId] without copying the full history list.
     * Thread-safe: backed by a [ConcurrentHashMap] updated in [addMessage] on
     * MessagingDispatcher; safe to call from the Main/Compose thread.
     */
    fun getLastSessionMessage(sessionId: UUID): IMMessage? {
        return lastMessageBySession[sessionId]
    }
    
    /**
     * Mark session as read
     */
    fun markAsRead(sessionId: UUID) {
        _unreadCounts.value = _unreadCounts.value - sessionId
        pendingSyncSessions.value = pendingSyncSessions.value - sessionId
    }
    
    private fun addMessage(sessionId: UUID, message: IMMessage) {
        val messages = sessionMessages.getOrPut(sessionId) { mutableListOf() }
        messages.add(message)
        lastMessageBySession[sessionId] = message
        
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
