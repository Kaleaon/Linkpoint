package com.linkpoint.groups

import android.os.Parcelable
import android.util.Log
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.chat.IMManager
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.SLMessagePackers
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.types.getUUID
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages Second Life groups - memberships, chat, notices, etc.
 *
 * Inbound group processing and flow emissions are serialized on the MessagingDispatcher
 * "MessageThread" to avoid mixing Default/IO dispatchers with messaging state.
 */
class GroupsManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    private val scope = CoroutineScope(MessagingDispatcher.dispatcher + SupervisorJob())
    
    // Groups the agent is a member of
    private val groups = ConcurrentHashMap<UUID, Group>()
    
    // Active group
    private val _activeGroup = MutableStateFlow<UUID?>(null)
    val activeGroup: StateFlow<UUID?> = _activeGroup
    
    // Group events
    private val _groupEvents = MutableSharedFlow<GroupEvent>(replay = 0, extraBufferCapacity = 32)
    val groupEvents: SharedFlow<GroupEvent> = _groupEvents
    
    init {
        capabilityManager.registerEventHandler(
            "AgentGroupDataUpdate",
            this,
            MessagingDispatcher.dispatcher
        )
        capabilityManager.registerEventHandler(
            "GroupNotice",
            this,
            MessagingDispatcher.dispatcher
        )
        capabilityManager.registerEventHandler(
            "GroupChat",
            this,
            MessagingDispatcher.dispatcher
        )
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        scope.launch {
            when (message) {
                "AgentGroupDataUpdate" -> handleGroupDataUpdate(body)
                "GroupNotice" -> handleGroupNotice(body)
                "GroupChat" -> handleGroupChat(body)
            }
        }
    }
    
    private fun handleGroupDataUpdate(body: LLSDMap) {
        val groupData = body.getArray("GroupData") ?: return
        
        for (item in groupData.value) {
            if (item is LLSDMap) {
                val groupId = UUID.fromString(item.getString("GroupID") ?: continue)
                val groupName = item.getString("GroupName") ?: "Unknown Group"
                val contribution = item.getInt("Contribution") ?: 0
                val insigniaId = item.getString("GroupInsigniaID")?.let { UUID.fromString(it) }
                val acceptNotices = item.getBoolean("AcceptNotices") ?: true
                val powers = item.getLong("GroupPowers") ?: 0L
                val listInProfile = item.getBoolean("ListInProfile") ?: true
                
                val group = Group(
                    groupId = groupId,
                    name = groupName,
                    contribution = contribution,
                    insigniaId = insigniaId,
                    acceptNotices = acceptNotices,
                    powers = powers,
                    listInProfile = listInProfile
                )
                
                groups[groupId] = group
            }
        }
        
        scope.launch {
            _groupEvents.emit(GroupEvent.GroupsUpdated(groups.values.toList()))
        }
    }
    
    private fun handleGroupNotice(body: LLSDMap) {
        val groupId = UUID.fromString(body.getString("group_id") ?: return)
        val senderId = UUID.fromString(body.getString("sender_id") ?: return)
        val senderName = body.getString("sender_name") ?: "Unknown"
        val subject = body.getString("subject") ?: ""
        val message = body.getString("message") ?: ""
        val timestamp = body.getLong("timestamp") ?: System.currentTimeMillis()
        
        val notice = GroupNotice(
            noticeId = UUID.randomUUID(),
            groupId = groupId,
            senderId = senderId,
            senderName = senderName,
            subject = subject,
            message = message,
            timestamp = timestamp
        )
        
        scope.launch {
            _groupEvents.emit(GroupEvent.NoticeReceived(notice))
        }
    }
    
    private fun handleGroupChat(body: LLSDMap) {
        val groupId = UUID.fromString(body.getString("group_id") ?: return)
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Unknown"
        val message = body.getString("message") ?: ""
        val timestamp = body.getLong("timestamp") ?: System.currentTimeMillis()
        
        val chatMessage = GroupChatMessage(
            groupId = groupId,
            fromId = fromId,
            fromName = fromName,
            message = message,
            timestamp = timestamp
        )
        
        scope.launch {
            _groupEvents.emit(GroupEvent.ChatReceived(chatMessage))
        }
    }
    
    /**
     * Get all groups
     */
    fun getAllGroups(): List<Group> = groups.values.toList()
    
    /**
     * Get a specific group
     */
    fun getGroup(groupId: UUID): Group? = groups[groupId]
    
    /**
     * Set active group (for name tag display)
     */
    suspend fun setActiveGroup(groupId: UUID?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // GroupData
                if (groupId != null) {
                    payload.putUUID(groupId)
                } else {
                    payload.putUUID(UUID(0, 0)) // UUID_ZERO
                }
                
                udpConnection.sendPacket(MessageIdRegistry.ACTIVATE_GROUP, payload.array().copyOf(payload.position()), reliable = true)
                _activeGroup.value = groupId
                
                Log.i(TAG, "Active group set to: $groupId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to set active group", e)
                false
            }
        }
    }
    
    /**
     * Optional reference to [IMManager]. When wired (via [setIMManager]),
     * `sendGroupChat` delegates the bring-up + queueing to the IM session
     * state machine in IMManager (single source of truth). When null —
     * e.g. early in initialization, before IMManager exists — we fall
     * back to a self-contained Dialog=15+17 send pair. Both paths produce
     * identical bytes-on-wire via [SLMessagePackers].
     */
    @Volatile
    private var imManager: IMManager? = null

    fun setIMManager(manager: IMManager) {
        imManager = manager
    }

    /**
     * Send a group chat message.
     *
     * Group chat in Second Life is **UDP `ImprovedInstantMessage`**, not
     * the `ChatSessionRequest` HTTP cap. (Lumiya enumerates the cap but
     * only its voice module reads the URL —
     * `lumiya_decompiled_source/.../slproto/caps/SLCaps.java:45`,
     * `slproto/modules/voice/SLVoice.java:102`. There is no Lumiya call
     * that POSTs `method=sendchat` to that cap.)
     *
     * Wire: Dialog=15 (IM_SESSION_GROUP_START) brings up the chatterbox
     * session, then Dialog=17 (IM_SESSION_SEND) carries each line. Both
     * use `ID = ToAgentID = groupId` and `BinaryBucket = byte[1]` (single
     * zero byte; SL rejects empty buckets on Dialog=15). See
     * `SLAgentCircuit.SendGroupSessionStart:721-739` and
     * `SendGroupInstantMessage:1671-1696`.
     *
     * Suspend signature retained for API compatibility with existing
     * callers; the body is now non-blocking.
     */
    @Suppress("RedundantSuspendModifier")
    suspend fun sendGroupChat(groupId: UUID, message: String): Boolean {
        // If IMManager is wired, route through it so the queue + start-reply
        // state machine runs there instead of duplicating it. The local
        // session record will be lazily created on first send.
        imManager?.let { im ->
            return try {
                im.startGroupSessionLocal(groupId, groups[groupId]?.name ?: "Group")
                im.sendIM(groupId, message)
                true
            } catch (e: Exception) {
                Log.e(TAG, "sendGroupChat via IMManager failed", e)
                false
            }
        }
        // Fallback path — self-contained, no shared state. Sends the
        // session-start and the chat line back-to-back. The simulator is
        // idempotent on Dialog=15 (a duplicate session-start is a no-op),
        // so this is safe even when the session is already up.
        return try {
            val identity = AgentIdentity(
                agentId = agentId,
                sessionId = udpConnection.getSessionId(),
                circuitCode = udpConnection.getCircuitCode()
            ).requireValid("GroupsManager.sendGroupChat")
            val ts = (System.currentTimeMillis() / 1000).toInt()

            udpConnection.sendPacket(
                MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
                SLMessagePackers.packImprovedInstantMessage(
                    identity = identity,
                    fromGroup = false,
                    toAgentId = groupId,
                    dialog = IMManager.IM_SESSION_GROUP_START,
                    id = groupId,
                    timestamp = ts,
                    fromAgentName = "You",
                    message = "",
                    binaryBucket = byteArrayOf(0)
                ),
                reliable = true
            )
            udpConnection.sendPacket(
                MessageIdRegistry.IMPROVED_INSTANT_MESSAGE,
                SLMessagePackers.packImprovedInstantMessage(
                    identity = identity,
                    fromGroup = false,
                    toAgentId = groupId,
                    dialog = IMManager.IM_SESSION_SEND,
                    id = groupId,
                    timestamp = ts,
                    fromAgentName = "You",
                    message = message,
                    binaryBucket = byteArrayOf(0)
                ),
                reliable = true
            )
            Log.i(TAG, "Sent group chat (Dialog=15+17) to $groupId via fallback path")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send group chat", e)
            false
        }
    }

    /**
     * Start or join a group chat session (lazy — no server round-trip).
     *
     * In Lumiya, group sessions come up implicitly on the first Dialog=15
     * IM. This function exists only so existing callers that "open" a
     * group chat panel before sending can pre-create the local session
     * record in [IMManager]. Returns the group UUID (which is also the
     * session UUID by convention).
     */
    suspend fun startGroupChatSession(groupId: UUID): UUID? {
        imManager?.startGroupSessionLocal(groupId, groups[groupId]?.name ?: "Group")
        return groupId
    }
    
    /**
     * Leave a group
     */
    suspend fun leaveGroup(groupId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // GroupData
                payload.putUUID(groupId)
                
                udpConnection.sendPacket(MessageIdRegistry.LEAVE_GROUP_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                
                groups.remove(groupId)
                if (_activeGroup.value == groupId) {
                    _activeGroup.value = null
                }
                
                scope.launch {
                    _groupEvents.emit(GroupEvent.LeftGroup(groupId))
                }
                
                Log.i(TAG, "Left group: $groupId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to leave group", e)
                false
            }
        }
    }
    
    /**
     * Request group information
     */
    suspend fun requestGroupInfo(groupId: UUID) {
        withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // GroupData
                payload.putUUID(groupId)
                
                udpConnection.sendPacket(MessageIdRegistry.GROUP_PROFILE_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested group info for: $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request group info", e)
            }
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
    
    // ==================== UDP MESSAGE HANDLERS ====================
    
    /**
     * Handle active group update from AgentDataUpdate message
     */
    fun handleActiveGroupUpdate(groupId: UUID, groupTitle: String, groupPowers: Long) {
        scope.launch {
            Log.i(TAG, "📋 Active group updated: $groupId, title='$groupTitle'")
            
            _activeGroup.value = groupId
            
            // Update group info if we have it
            groups[groupId]?.let { group ->
                groups[groupId] = group.copy(powers = groupPowers)
            }
            
            _groupEvents.emit(GroupEvent.ActiveGroupChanged(groupId, groupTitle))
        }
    }
    
    // ==================== GROUP ACCOUNTING ====================
    
    /**
     * Request group account summary.
     */
    suspend fun requestAccountSummary(groupId: UUID): GroupAccountSummary? {
        return withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(52).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(groupId)
                payload.putUUID(udpConnection.getSessionId())
                
                // MoneyData
                payload.putInt(0) // RequestID - server will echo this back
                payload.putInt(-1) // IntervalDays - current
                payload.putInt(0) // CurrentInterval - start
                
                udpConnection.sendPacket(MSG_GROUP_ACCOUNT_SUMMARY_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested account summary for group: $groupId")
                
                // Note: Response will come via message handler
                null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request account summary", e)
                null
            }
        }
    }
    
    /**
     * Request group account details.
     */
    suspend fun requestAccountDetails(groupId: UUID): GroupAccountDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(52).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(groupId)
                payload.putUUID(udpConnection.getSessionId())
                
                // MoneyData
                payload.putInt(0) // RequestID
                payload.putInt(-1) // IntervalDays
                payload.putInt(0) // CurrentInterval
                
                udpConnection.sendPacket(MSG_GROUP_ACCOUNT_DETAILS_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested account details for group: $groupId")
                
                null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request account details", e)
                null
            }
        }
    }
    
    /**
     * Request group account transactions.
     */
    suspend fun requestAccountTransactions(groupId: UUID): List<GroupTransaction>? {
        return withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(52).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(groupId)
                payload.putUUID(udpConnection.getSessionId())
                
                // MoneyData
                payload.putInt(0) // RequestID
                payload.putInt(-1) // IntervalDays
                payload.putInt(0) // CurrentInterval
                
                udpConnection.sendPacket(MSG_GROUP_ACCOUNT_TRANSACTIONS_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested account transactions for group: $groupId")
                
                null
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request account transactions", e)
                null
            }
        }
    }
    
    // ==================== GROUP PROPOSALS/VOTING ====================
    
    /**
     * Request active proposals for a group.
     */
    suspend fun requestActiveProposals(groupId: UUID) {
        withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(52).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // GroupData
                payload.putUUID(groupId)
                
                // TransactionData
                payload.putUUID(UUID.randomUUID()) // TransactionID
                
                udpConnection.sendPacket(MSG_GROUP_ACTIVE_PROPOSALS_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested active proposals for group: $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request active proposals", e)
            }
        }
    }
    
    /**
     * Request vote history for a group.
     */
    suspend fun requestVoteHistory(groupId: UUID) {
        withContext(Dispatchers.IO) {
            try {
                val payload = ByteBuffer.allocate(52).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // GroupData
                payload.putUUID(groupId)
                
                // TransactionData
                payload.putUUID(UUID.randomUUID()) // TransactionID
                
                udpConnection.sendPacket(MSG_GROUP_VOTE_HISTORY_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested vote history for group: $groupId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request vote history", e)
            }
        }
    }
    
    /**
     * Cast a vote on a proposal.
     */
    suspend fun castVote(groupId: UUID, proposalId: UUID, voteString: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val voteBytes = voteString.toByteArray(Charsets.UTF_8)
                val payload = ByteBuffer.allocate(52 + voteBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // ProposalData
                payload.putUUID(groupId)
                payload.putUUID(proposalId)
                payload.put(voteBytes.size.toByte())
                payload.put(voteBytes)
                
                udpConnection.sendPacket(MSG_GROUP_PROPOSAL_BALLOT, payload.array().copyOf(payload.position()), reliable = true)
                Log.i(TAG, "Cast vote on proposal $proposalId: $voteString")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cast vote", e)
                false
            }
        }
    }
    
    /**
     * Start a new proposal.
     */
    suspend fun startProposal(
        groupId: UUID,
        quorum: Int,
        majority: Float,
        duration: Int, // in seconds
        proposalText: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val textBytes = proposalText.toByteArray(Charsets.UTF_8)
                val payload = ByteBuffer.allocate(60 + textBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // ProposalData
                payload.putUUID(groupId)
                payload.putInt(quorum)
                payload.putFloat(majority)
                payload.putInt(duration)
                payload.putShort(textBytes.size.toShort())
                payload.put(textBytes)
                
                udpConnection.sendPacket(MSG_START_GROUP_PROPOSAL, payload.array().copyOf(payload.position()), reliable = true)
                Log.i(TAG, "Started new proposal in group $groupId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start proposal", e)
                false
            }
        }
    }
    
    companion object {
        private const val TAG = "GroupsManager"
        
        // Group powers (bit flags)
        const val GP_MEMBER_INVITE = 0x2L
        const val GP_MEMBER_EJECT = 0x4L
        const val GP_ROLE_PROPERTIES = 0x100L
        const val GP_NOTICE_SEND = 0x1000L
        const val GP_NOTICES_RECEIVE = 0x2000L
        const val GP_GROUP_CHANGE_IDENTITY = 0x10000000L
        
        // Group accounting message IDs
        const val MSG_GROUP_ACCOUNT_SUMMARY_REQUEST = 0xFF0070
        const val MSG_GROUP_ACCOUNT_DETAILS_REQUEST = 0xFF0071
        const val MSG_GROUP_ACCOUNT_TRANSACTIONS_REQUEST = 0xFF0072
        
        // Group proposals message IDs
        const val MSG_GROUP_ACTIVE_PROPOSALS_REQUEST = 0xFF0073
        const val MSG_GROUP_VOTE_HISTORY_REQUEST = 0xFF0074
        const val MSG_GROUP_PROPOSAL_BALLOT = 0xFF0075
        const val MSG_START_GROUP_PROPOSAL = 0xFF0076
    }
    
    // ==================== UDP MESSAGE HANDLERS ====================
    
    /**
     * Cached group roles keyed by groupId. Updated by [handleGroupRoleData]
     * (UDP `GroupRoleDataReply`); read by UI via [getGroupRoles] and the
     * [GroupEvent.RolesUpdated] event flow.
     */
    private val groupRoles = ConcurrentHashMap<UUID, List<GroupRole>>()

    /**
     * Cached group titles keyed by groupId. Updated by [handleGroupTitles]
     * (UDP `GroupTitlesReply`); read by UI via [getGroupTitles] and the
     * [GroupEvent.TitlesUpdated] event flow.
     */
    private val groupTitles = ConcurrentHashMap<UUID, List<GroupTitle>>()

    fun getGroupRoles(groupId: UUID): List<GroupRole> = groupRoles[groupId].orEmpty()
    fun getGroupTitles(groupId: UUID): List<GroupTitle> = groupTitles[groupId].orEmpty()

    /**
     * Handle GroupRoleDataReply UDP message. Wire format
     * (Lumiya parity, slproto/messages/GroupRoleDataReply.java PackPayload):
     *   AgentData: AgentID(LLUUID)
     *   GroupData: GroupID(LLUUID), RequestID(LLUUID), RoleCount(S32)
     *   RoleData (Variable, U8 count):
     *     RoleID(LLUUID), Name(Variable 1), Title(Variable 1),
     *     Description(Variable 1), Powers(U64), Members(S32)
     */
    fun handleGroupRoleData(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

            // AgentData
            if (buffer.remaining() < 16) return
            buffer.position(buffer.position() + 16) // skip AgentID

            // GroupData
            if (buffer.remaining() < 16 + 16 + 4) return
            val groupId = buffer.getUUID()
            val requestId = buffer.getUUID()
            val totalRoleCount = buffer.int

            // RoleData block (Variable u8 count + entries)
            if (buffer.remaining() < 1) return
            val entryCount = buffer.get().toInt() and 0xFF

            val parsed = ArrayList<GroupRole>(entryCount)
            for (i in 0 until entryCount) {
                if (buffer.remaining() < 16 + 1) break
                val roleId = buffer.getUUID()
                val name = readVariable1String(buffer) ?: break
                val title = readVariable1String(buffer) ?: break
                val description = readVariable1String(buffer) ?: break
                if (buffer.remaining() < 8 + 4) break
                val powers = buffer.long
                val members = buffer.int
                parsed += GroupRole(
                    roleId = roleId,
                    name = name,
                    title = title,
                    description = description,
                    powers = powers,
                    members = members
                )
            }

            // GroupRoleDataReply is paged: each packet carries a slice of
            // the full role list. Merge keyed by RoleID so an in-progress
            // multi-packet reply doesn't overwrite earlier slices, but
            // start fresh when we see a new RequestID for this group.
            val previous = groupRoles[groupId].orEmpty()
            val carried =
                if (previous.isNotEmpty() && lastGroupRoleRequestId[groupId] == requestId) {
                    previous
                } else {
                    emptyList()
                }
            lastGroupRoleRequestId[groupId] = requestId
            val merged = (carried + parsed).distinctBy { it.roleId }
            groupRoles[groupId] = merged

            Log.d(TAG, "📋 GroupRoleDataReply: groupId=$groupId, +${parsed.size} roles " +
                "(cached=${merged.size}, declared=$totalRoleCount)")

            scope.launch {
                _groupEvents.emit(GroupEvent.RolesUpdated(groupId, merged))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing GroupRoleDataReply", e)
        }
    }

    private val lastGroupRoleRequestId = ConcurrentHashMap<UUID, UUID>()

    /**
     * Handle GroupTitlesReply UDP message. Wire format
     * (Lumiya parity, slproto/messages/GroupTitlesReply.java PackPayload):
     *   AgentData: AgentID(LLUUID), GroupID(LLUUID), RequestID(LLUUID)
     *   GroupData (Variable, U8 count):
     *     Title(Variable 1), RoleID(LLUUID), Selected(BOOL)
     */
    fun handleGroupTitles(payload: ByteArray) {
        try {
            val buffer = ByteBuffer.wrap(payload).order(ByteOrder.LITTLE_ENDIAN)

            // AgentData: AgentID, GroupID, RequestID
            if (buffer.remaining() < 16 + 16 + 16) return
            buffer.position(buffer.position() + 16) // skip AgentID
            val groupId = buffer.getUUID()
            buffer.getUUID() // RequestID — unused; titles aren't paged, sim sends one reply

            if (buffer.remaining() < 1) return
            val count = buffer.get().toInt() and 0xFF

            val parsed = ArrayList<GroupTitle>(count)
            for (i in 0 until count) {
                val title = readVariable1String(buffer) ?: break
                if (buffer.remaining() < 16 + 1) break
                val roleId = buffer.getUUID()
                val selected = buffer.get() != 0.toByte()
                parsed += GroupTitle(
                    title = title,
                    roleId = roleId,
                    selected = selected
                )
            }

            groupTitles[groupId] = parsed
            Log.d(TAG, "📋 GroupTitlesReply: groupId=$groupId, ${parsed.size} titles")

            scope.launch {
                _groupEvents.emit(GroupEvent.TitlesUpdated(groupId, parsed))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing GroupTitlesReply", e)
        }
    }

    /**
     * Read a `Variable 1` string field — 1-byte u8 length prefix followed by
     * UTF-8 bytes (with the SL convention that the length includes the
     * trailing NUL). Returns null if the buffer is short. The returned
     * String trims the trailing NUL.
     */
    private fun readVariable1String(buffer: ByteBuffer): String? {
        if (buffer.remaining() < 1) return null
        val len = buffer.get().toInt() and 0xFF
        if (buffer.remaining() < len) return null
        if (len == 0) return ""
        val bytes = ByteArray(len)
        buffer.get(bytes)
        // Strip the NUL terminator that the SL `Variable 1` convention includes.
        val effective = if (bytes[len - 1] == 0.toByte()) len - 1 else len
        return String(bytes, 0, effective, Charsets.UTF_8)
    }
    
    /**
     * Handle GroupNoticeAdd confirmation.
     */
    fun handleGroupNoticeAdd(payload: ByteArray) {
        try {
            Log.d(TAG, "📢 Group notice add confirmed (${payload.size} bytes)")
            // This is typically a confirmation that a notice was added
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing GroupNoticeAdd", e)
        }
    }
    
    /**
     * Handle AgentGroupDataUpdate UDP message.
     * Updates the agent's group memberships from UDP (fallback for capability).
     */
    fun handleAgentGroupDataUpdate(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block - 16 bytes
            buffer.position(buffer.position() + 16) // Skip AgentID
            
            // GroupData block count
            if (buffer.remaining() < 1) return
            val groupCount = buffer.get().toInt() and 0xFF
            
            Log.d(TAG, "📋 AgentGroupDataUpdate: $groupCount groups")
            
            // Parse each group
            for (i in 0 until groupCount) {
                if (buffer.remaining() < 43) break // Minimum group data size
                
                val groupId = buffer.getUUID()
                val groupPowers = buffer.long
                val acceptNotices = buffer.get() != 0.toByte()
                val groupInsigniaId = buffer.getUUID()
                val contribution = buffer.int
                
                // Read variable-length group name
                val nameLen = buffer.get().toInt() and 0xFF
                val nameBytes = ByteArray(nameLen)
                if (buffer.remaining() >= nameLen) {
                    buffer.get(nameBytes)
                }
                val groupName = String(nameBytes, Charsets.UTF_8).trimEnd('\u0000')
                
                // Update or add group
                val group = groups[groupId]?.copy(
                    powers = groupPowers,
                    acceptNotices = acceptNotices,
                    insigniaId = if (groupInsigniaId != UUID(0, 0)) groupInsigniaId else null,
                    contribution = contribution
                ) ?: Group(
                    groupId = groupId,
                    name = groupName,
                    powers = groupPowers,
                    acceptNotices = acceptNotices,
                    insigniaId = if (groupInsigniaId != UUID(0, 0)) groupInsigniaId else null,
                    contribution = contribution
                )
                
                groups[groupId] = group
            }
            
            // Emit update event
            scope.launch {
                _groupEvents.emit(GroupEvent.GroupsUpdated(groups.values.toList()))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing AgentGroupDataUpdate", e)
        }
    }
}

@Parcelize
data class Group(
    val groupId: UUID,
    val name: String,
    val contribution: Int = 0,
    val insigniaId: UUID? = null,
    val acceptNotices: Boolean = true,
    val powers: Long = 0,
    val listInProfile: Boolean = true,
    val charter: String = "",
    val founderName: String = "",
    val memberCount: Int = 0,
    val isOpenEnrollment: Boolean = false,
    val membershipFee: Int = 0
) : Parcelable {
    val canSendNotices: Boolean get() = (powers and GroupsManager.GP_NOTICE_SEND) != 0L
    val canInvite: Boolean get() = (powers and GroupsManager.GP_MEMBER_INVITE) != 0L
    val canEject: Boolean get() = (powers and GroupsManager.GP_MEMBER_EJECT) != 0L
}

@Parcelize
data class GroupNotice(
    val noticeId: UUID,
    val groupId: UUID,
    val senderId: UUID,
    val senderName: String,
    val subject: String,
    val message: String,
    val timestamp: Long,
    val hasAttachment: Boolean = false,
    val attachmentName: String? = null
) : Parcelable

data class GroupChatMessage(
    val groupId: UUID,
    val fromId: UUID,
    val fromName: String,
    val message: String,
    val timestamp: Long
)

sealed class GroupEvent {
    data class GroupsUpdated(val groups: List<Group>) : GroupEvent()
    data class JoinedGroup(val group: Group) : GroupEvent()
    data class LeftGroup(val groupId: UUID) : GroupEvent()
    data class NoticeReceived(val notice: GroupNotice) : GroupEvent()
    data class ChatReceived(val message: GroupChatMessage) : GroupEvent()
    data class ActiveGroupChanged(val groupId: UUID, val groupTitle: String) : GroupEvent()
    data class RolesUpdated(val groupId: UUID, val roles: List<GroupRole>) : GroupEvent()
    data class TitlesUpdated(val groupId: UUID, val titles: List<GroupTitle>) : GroupEvent()
}

/**
 * A single role within a group, parsed from `GroupRoleDataReply`. The
 * `powers` long is a bitmask against the `GP_*` constants on
 * [GroupsManager] (e.g. `GP_NOTICE_SEND`, `GP_MEMBER_INVITE`).
 */
@Parcelize
data class GroupRole(
    val roleId: UUID,
    val name: String,
    val title: String,
    val description: String,
    val powers: Long,
    val members: Int
) : Parcelable

/**
 * A title (display label) the agent can wear within a group, parsed from
 * `GroupTitlesReply`. Exactly one title is `selected` per group at any time.
 */
@Parcelize
data class GroupTitle(
    val title: String,
    val roleId: UUID,
    val selected: Boolean
) : Parcelable

// ==================== GROUP ACCOUNTING DATA CLASSES ====================

/**
 * Group account summary data.
 */
data class GroupAccountSummary(
    val groupId: UUID,
    val balance: Int,
    val totalCredits: Int,
    val totalDebits: Int,
    val objectTaxCurrent: Int,
    val lightTaxCurrent: Int,
    val landTaxCurrent: Int,
    val groupTaxCurrent: Int,
    val parcelDirFeeCurrent: Int,
    val objectTaxEstimate: Int,
    val lightTaxEstimate: Int,
    val landTaxEstimate: Int,
    val groupTaxEstimate: Int,
    val parcelDirFeeEstimate: Int,
    val startDate: String,
    val lastTaxDate: String,
    val taxDate: String
)

/**
 * Group account details data.
 */
data class GroupAccountDetails(
    val groupId: UUID,
    val items: List<AccountDetailItem>
)

/**
 * A single account detail item.
 */
data class AccountDetailItem(
    val description: String,
    val amount: Int,
    val date: String
)

/**
 * Group transaction data.
 */
data class GroupTransaction(
    val groupId: UUID,
    val time: String,
    val type: String,
    val item: String,
    val user: String,
    val amount: Int
)

/**
 * Group proposal data.
 */
@Parcelize
data class GroupProposal(
    val proposalId: UUID,
    val groupId: UUID,
    val proposerName: String,
    val proposalText: String,
    val quorum: Int,
    val majority: Float,
    val startDate: Long,
    val endDate: Long,
    val voteYes: Int = 0,
    val voteNo: Int = 0,
    val voteAbstain: Int = 0
) : Parcelable

/**
 * Group vote history item.
 */
@Parcelize
data class GroupVoteHistoryItem(
    val voteId: UUID,
    val proposalText: String,
    val result: String,
    val voteDate: Long,
    val yesVotes: Int,
    val noVotes: Int,
    val abstainVotes: Int
) : Parcelable
