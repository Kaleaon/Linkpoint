package com.linkpoint.groups

import android.os.Parcelable
import android.util.Log
import com.linkpoint.messaging.MessagingDispatcher
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.MessageIds
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
                
                udpConnection.sendPacket(MessageIds.ACTIVATE_GROUP, payload.array().copyOf(payload.position()), reliable = true)
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
     * Send group chat message
     */
    suspend fun sendGroupChat(groupId: UUID, message: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                val payload = ByteBuffer.allocate(100 + messageBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // ChatData
                payload.putUUID(groupId)
                
                // Message
                payload.putShort(messageBytes.size.toShort())
                payload.put(messageBytes)
                
                // For group chat, we use the chat session via capabilities
                val sessionId = getOrCreateGroupChatSession(groupId)
                
                Log.i(TAG, "Sent group chat to $groupId: $message")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send group chat", e)
                false
            }
        }
    }
    
    /**
     * Start or join a group chat session
     */
    suspend fun startGroupChatSession(groupId: UUID): UUID? {
        return getOrCreateGroupChatSession(groupId)
    }
    
    private suspend fun getOrCreateGroupChatSession(groupId: UUID): UUID? {
        return withContext(Dispatchers.IO) {
            try {
                val request = LLSDMap().apply {
                    this["method"] = LLSDString("start conference")
                    this["session-id"] = LLSDUUID(groupId)
                }
                
                val response = capabilityManager.request("ChatSessionRequest", request)
                if (response is LLSDMap) {
                    UUID.fromString(response.getString("session_id"))
                } else {
                    Log.w(TAG, "Using groupId as fallback session ID for group $groupId")
                    groupId // Fallback: use group ID as session ID
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create chat session", e)
                Log.w(TAG, "Using groupId as fallback session ID for group $groupId")
                groupId
            }
        }
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
                
                udpConnection.sendPacket(MessageIds.LEAVE_GROUP_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
                
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
                
                udpConnection.sendPacket(MessageIds.GROUP_PROFILE_REQUEST, payload.array().copyOf(payload.position()), reliable = true)
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
     * Handle GroupRoleDataReply UDP message.
     * Parses role data for a group.
     */
    fun handleGroupRoleData(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            buffer.position(buffer.position() + 32) // Skip AgentID and GroupID
            
            // Role count
            if (buffer.remaining() < 4) return
            val roleCount = buffer.int
            
            Log.d(TAG, "📋 Parsing $roleCount group roles")
            
            // Note: Full parsing would extract role names, powers, etc.
            // For now we just acknowledge receipt
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing GroupRoleDataReply", e)
        }
    }
    
    /**
     * Handle GroupTitlesReply UDP message.
     * Parses available titles for a group.
     */
    fun handleGroupTitles(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // AgentData block
            buffer.position(buffer.position() + 48) // Skip AgentID, GroupID, RequestID
            
            // Title count
            if (buffer.remaining() < 1) return
            val titleCount = buffer.get().toInt() and 0xFF
            
            Log.d(TAG, "📋 Parsing $titleCount group titles")
            
            // Note: Full parsing would extract title names and IDs
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing GroupTitlesReply", e)
        }
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
}

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
