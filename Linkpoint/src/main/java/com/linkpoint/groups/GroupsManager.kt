package com.linkpoint.groups

import android.os.Parcelable
import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.MessageIds
import com.linkpoint.protocol.messages.UDPConnection
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
 */
class GroupsManager(
    private val udpConnection: UDPConnection,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    companion object {
        private const val TAG = "GroupsManager"
        
        // Group powers (bit flags)
        const val GP_MEMBER_INVITE = 0x2L
        const val GP_MEMBER_EJECT = 0x4L
        const val GP_ROLE_PROPERTIES = 0x100L
        const val GP_NOTICE_SEND = 0x1000L
        const val GP_NOTICES_RECEIVE = 0x2000L
        const val GP_GROUP_CHANGE_IDENTITY = 0x10000000L
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Groups the agent is a member of
    private val groups = ConcurrentHashMap<UUID, Group>()
    
    // Active group
    private val _activeGroup = MutableStateFlow<UUID?>(null)
    val activeGroup: StateFlow<UUID?> = _activeGroup
    
    // Group events
    private val _groupEvents = MutableSharedFlow<GroupEvent>(replay = 0, extraBufferCapacity = 32)
    val groupEvents: SharedFlow<GroupEvent> = _groupEvents
    
    init {
        capabilityManager.registerEventHandler("AgentGroupDataUpdate", this)
        capabilityManager.registerEventHandler("GroupNotice", this)
        capabilityManager.registerEventHandler("GroupChat", this)
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        when (message) {
            "AgentGroupDataUpdate" -> handleGroupDataUpdate(body)
            "GroupNotice" -> handleGroupNotice(body)
            "GroupChat" -> handleGroupChat(body)
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
}

/**
 * Extension function to write UUID to ByteBuffer in big-endian (SL protocol format).
 * UUIDs in SL are always stored as 16 raw bytes in big-endian order.
 */
private fun ByteBuffer.putUUID(uuid: UUID): ByteBuffer {
    val originalOrder = order()
    order(ByteOrder.BIG_ENDIAN)
    putLong(uuid.mostSignificantBits)
    putLong(uuid.leastSignificantBits)
    order(originalOrder)
    return this
}
