package com.linkpoint.world

import android.os.Parcelable
import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.capabilities.EventQueueDispatcher
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.ids.MessageIdRegistry
import com.linkpoint.protocol.messages.UDPConnectionFixed
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
 * Manages friends list, online status, and friendship operations.
 * 
 * Enhanced with detailed logging for debugging friend-related issues.
 */
class FriendsManager(
    private val udpConnection: UDPConnectionFixed,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    companion object {
        private const val TAG = "FriendsManager"

        // Friendship rights
        const val RIGHTS_ONLINE_STATUS = 0x01
        const val RIGHTS_MAP_LOCATION = 0x02
        const val RIGHTS_MODIFY_OBJECTS = 0x04

        // Name synthesised by [addFriendFromLogin] when the buddy-list lacks
        // a name (which is always — the login response only carries UUIDs).
        // Used by [getUnresolvedNameAgentIds] to identify friends whose
        // display-name lookup never completed so the UI can retry.
        private const val PLACEHOLDER_NAME_PREFIX = "Resident ("
    }
    
    private val scope = CoroutineScope(EventQueueDispatcher.dispatcher + SupervisorJob())
    
    // Friends list
    private val friends = ConcurrentHashMap<UUID, Friend>()
    
    // Online status
    private val _onlineFriends = MutableStateFlow<Set<UUID>>(emptySet())
    val onlineFriends: StateFlow<Set<UUID>> = _onlineFriends
    
    // Friend list updates
    private val _friendsFlow = MutableSharedFlow<FriendEvent>(replay = 0, extraBufferCapacity = 32)
    val friendsFlow: SharedFlow<FriendEvent> = _friendsFlow
    
    // Pending friendship offers
    private val pendingOffers = ConcurrentHashMap<UUID, FriendshipOffer>()
    
    init {
        capabilityManager.registerEventHandler("FriendshipOffered", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("FriendshipAccepted", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("FriendshipDeclined", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("FriendshipTerminated", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("OnlineNotification", this, EventQueueDispatcher.dispatcher)
        capabilityManager.registerEventHandler("OfflineNotification", this, EventQueueDispatcher.dispatcher)
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        Log.d(TAG, "📬 Friend event received: $message")
        when (message) {
            "FriendshipOffered" -> handleFriendshipOffered(body)
            "FriendshipAccepted" -> handleFriendshipAccepted(body)
            "FriendshipDeclined" -> handleFriendshipDeclined(body)
            "FriendshipTerminated" -> handleFriendshipTerminated(body)
            "OnlineNotification" -> handleOnlineNotification(body)
            "OfflineNotification" -> handleOfflineNotification(body)
        }
    }
    
    private fun handleFriendshipOffered(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Unknown"
        val transactionId = UUID.fromString(body.getString("transaction_id") ?: return)
        val message = body.getString("message") ?: ""
        
        Log.i(TAG, "👋 Friendship offer from: $fromName ($fromId)")
        NetworkLogger.logFriendshipOffer(fromId.toString(), fromName, message)
        
        val offer = FriendshipOffer(
            transactionId = transactionId,
            fromAgentId = fromId,
            fromName = fromName,
            message = message,
            timestamp = System.currentTimeMillis()
        )
        
        pendingOffers[transactionId] = offer
        
        scope.launch {
            _friendsFlow.emit(FriendEvent.OfferReceived(offer))
        }
    }
    
    private fun handleFriendshipAccepted(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Unknown"
        
        Log.i(TAG, "✓ Friendship accepted by: $fromName ($fromId)")
        NetworkLogger.logFriendshipAccepted(fromId.toString(), fromName)
        
        val friend = Friend(
            agentId = fromId,
            name = fromName,
            rightsGiven = RIGHTS_ONLINE_STATUS,
            rightsHas = RIGHTS_ONLINE_STATUS
        )
        
        friends[fromId] = friend
        
        scope.launch {
            _friendsFlow.emit(FriendEvent.Added(friend))
        }
    }
    
    private fun handleFriendshipDeclined(body: LLSDMap) {
        val transactionId = UUID.fromString(body.getString("transaction_id") ?: return)
        val offer = pendingOffers.remove(transactionId)
        
        Log.i(TAG, "✗ Friendship declined for transaction: $transactionId")
        offer?.let { NetworkLogger.logFriendshipDeclined(it.fromAgentId.toString()) }
    }
    
    private fun handleFriendshipTerminated(body: LLSDMap) {
        val otherId = UUID.fromString(body.getString("other_id") ?: return)
        
        friends.remove(otherId)?.let { friend ->
            Log.i(TAG, "👋 Friendship terminated with: ${friend.name} ($otherId)")
            NetworkLogger.logFriendshipTerminated(otherId.toString(), friend.name)
            
            _onlineFriends.value = _onlineFriends.value - otherId
            
            scope.launch {
                _friendsFlow.emit(FriendEvent.Removed(friend.agentId))
            }
        }
    }
    
    /**
     * Handle UDP AcceptFriendship message (UDP fallback for capability)
     */
    fun handleFriendshipAccepted(agentId: UUID, transactionId: UUID) {
        val offer = pendingOffers.remove(transactionId)
        
        Log.i(TAG, "✓ Friendship accepted: agent=$agentId, transaction=$transactionId")
        
        // Add as friend
        addFriendFromLogin(agentId, "", 0, 0)
        
        scope.launch {
            friends[agentId]?.let { friend ->
                _friendsFlow.emit(FriendEvent.Added(friend))
            }
        }
    }
    
    /**
     * Handle UDP DeclineFriendship message (UDP fallback for capability)
     */
    fun handleFriendshipDeclined(agentId: UUID, transactionId: UUID) {
        val offer = pendingOffers.remove(transactionId)
        Log.i(TAG, "✗ Friendship declined: agent=$agentId, transaction=$transactionId")
    }
    
    /**
     * Handle UDP FormFriendship message (confirmation of new friendship)
     */
    fun handleFriendshipFormed(fromAgentId: UUID, toAgentId: UUID) {
        Log.i(TAG, "🤝 Friendship formed: $fromAgentId <-> $toAgentId")
        
        // Add both as friends if they're not us
        if (fromAgentId != agentId) {
            addFriendFromLogin(fromAgentId, "", 0, 0)
        }
        if (toAgentId != agentId) {
            addFriendFromLogin(toAgentId, "", 0, 0)
        }
        
        scope.launch {
            friends[fromAgentId]?.let { friend ->
                _friendsFlow.emit(FriendEvent.Added(friend))
            }
        }
    }
    
    private fun handleOnlineNotification(body: LLSDMap) {
        val agents = body.getArray("AgentOnline")
        val count = agents?.value?.size ?: 0
        Log.d(TAG, "🟢 Online notification received with $count agents")
        
        agents?.value?.forEach { agent ->
            if (agent is LLSDMap) {
                val agentIdStr = agent.getString("agent_id") ?: return@forEach
                val agentUuid = UUID.fromString(agentIdStr)
                
                val friend = friends[agentUuid]
                val friendName = friend?.name ?: "Unknown"
                
                friend?.isOnline = true
                _onlineFriends.value = _onlineFriends.value + agentUuid
                
                Log.i(TAG, "🟢 Friend online: $friendName ($agentUuid)")
                NetworkLogger.logFriendOnlineStatus(agentUuid.toString(), friendName, true)
                
                scope.launch {
                    _friendsFlow.emit(FriendEvent.OnlineStatusChanged(agentUuid, true))
                }
            }
        }
    }
    
    private fun handleOfflineNotification(body: LLSDMap) {
        val agents = body.getArray("AgentOffline")
        val count = agents?.value?.size ?: 0
        Log.d(TAG, "🔴 Offline notification received with $count agents")
        
        agents?.value?.forEach { agent ->
            if (agent is LLSDMap) {
                val agentIdStr = agent.getString("agent_id") ?: return@forEach
                val agentUuid = UUID.fromString(agentIdStr)
                
                val friend = friends[agentUuid]
                val friendName = friend?.name ?: "Unknown"
                
                friend?.isOnline = false
                _onlineFriends.value = _onlineFriends.value - agentUuid
                
                Log.i(TAG, "🔴 Friend offline: $friendName ($agentUuid)")
                NetworkLogger.logFriendOnlineStatus(agentUuid.toString(), friendName, false)
                
                scope.launch {
                    _friendsFlow.emit(FriendEvent.OnlineStatusChanged(agentUuid, false))
                }
            }
        }
    }
    
    /**
     * Get all friends
     */
    fun getAllFriends(): List<Friend> = friends.values.toList()
    
    /**
     * Find and add a friend by name
     */
    suspend fun findAndAddFriend(name: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Use directory lookup to find agent
                val lookupResponse = capabilityManager.request(
                    "AgentDomain",
                    LLSDMap().apply { this["names"] = LLSDString(name) }
                )
                
                if (lookupResponse is LLSDMap) {
                    val agentId = UUID.fromString(lookupResponse.getString("agent_id") ?: return@withContext false)
                    offerFriendship(agentId)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to find friend", e)
                false
            }
        }
    }
    
    /**
     * Send IM to friend via ImprovedInstantMessage protocol.
     * Uses dialog type 0 (IM_NOTHING_SPECIAL) for regular instant messages.
     */
    suspend fun sendIM(friendAgentId: UUID, message: String) {
        withContext(Dispatchers.IO) {
            try {
                // Wire format (LL message_template `ImprovedInstantMessage`,
                // low-freq 254; Lumiya:
                // slproto/messages/ImprovedInstantMessage.java PackPayload):
                //   AgentData:    AgentID(LLUUID), SessionID(LLUUID)
                //   MessageBlock: FromGroup(BOOL), ToAgentID(LLUUID),
                //                 ParentEstateID(U32), RegionID(LLUUID),
                //                 Position(LLVector3), Offline(U8),
                //                 Dialog(U8), ID(LLUUID), Timestamp(U32),
                //                 FromAgentName(Variable 1 NUL-term),
                //                 Message(Variable 2 NUL-term),
                //                 BinaryBucket(Variable 2)
                //
                // Lumiya's stringToVariableUTF (slproto/SLMessage.java line 212)
                // appends a NUL byte and includes it in the length prefix.
                // The previous encoder shipped FromAgentName and Message
                // without the NUL, so the simulator's UTF parser would walk
                // past the field boundary into adjacent fields.

                val nameRaw = "User".toByteArray(Charsets.UTF_8)
                val nameBytes = nameRaw + 0.toByte()

                val messageRaw = message.toByteArray(Charsets.UTF_8)
                val cappedMessage = if (messageRaw.size > 1023) messageRaw.copyOf(1023) else messageRaw
                val messageBytes = cappedMessage + 0.toByte()

                val payload = ByteBuffer
                    .allocate(36 /* AgentData */ + 1 /* FromGroup */ + 16 /* ToAgentID */ +
                              4 /* ParentEstateID */ + 16 /* RegionID */ + 12 /* Position */ +
                              1 /* Offline */ + 1 /* Dialog */ + 16 /* ID */ + 4 /* Timestamp */ +
                              1 + nameBytes.size /* FromAgentName V1 */ +
                              2 + messageBytes.size /* Message V2 */ +
                              2 /* BinaryBucket length only, empty */)
                    .order(ByteOrder.LITTLE_ENDIAN)

                // AgentData block
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())

                // MessageBlock
                payload.put(0)  // FromGroup = false
                payload.putUUID(friendAgentId)  // ToAgentID
                payload.putInt(0)  // ParentEstateID
                payload.putUUID(UUID(0, 0))  // RegionID (empty)
                payload.putFloat(0f)  // Position X
                payload.putFloat(0f)  // Position Y
                payload.putFloat(0f)  // Position Z
                payload.put(0)  // Offline
                payload.put(0)  // Dialog = IM_NOTHING_SPECIAL (regular IM)

                val transactionId = UUID.randomUUID()
                payload.putUUID(transactionId)
                payload.putInt((System.currentTimeMillis() / 1000).toInt())

                // FromAgentName (Variable 1, NUL-terminated)
                payload.put(nameBytes.size.toByte())
                payload.put(nameBytes)

                // Message (Variable 2, NUL-terminated)
                payload.putShort(messageBytes.size.toShort())
                payload.put(messageBytes)

                // BinaryBucket (Variable 2) - empty for regular IM
                payload.putShort(0)

                udpConnection.sendPacket(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE, payload.array().copyOf(payload.position()), reliable = true)
                Log.i(TAG, "Sent IM to friend $friendAgentId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send IM to friend", e)
            }
        }
    }
    
    /**
     * Teleport to a friend's location.
     * Delegates to requestTeleportToFriend which sends IM_LURE_USER request.
     */
    suspend fun teleportTo(friendAgentId: UUID) {
        val success = requestTeleportToFriend(friendAgentId, "Requesting teleport to your location")
        if (success) {
            Log.i(TAG, "Teleport request sent to friend $friendAgentId")
        } else {
            Log.w(TAG, "Failed to send teleport request to friend $friendAgentId")
        }
    }
    
    /**
     * Get friend by ID
     */
    fun getFriend(agentId: UUID): Friend? = friends[agentId]
    
    /**
     * Check if someone is a friend
     */
    fun isFriend(agentId: UUID): Boolean = friends.containsKey(agentId)
    
    /**
     * Check if friend is online
     */
    fun isOnline(agentId: UUID): Boolean = agentId in _onlineFriends.value
    
    /**
     * Accept friendship offer
     */
    suspend fun acceptFriendship(transactionId: UUID): Boolean {
        val offer = pendingOffers.remove(transactionId) ?: return false
        
        return withContext(Dispatchers.IO) {
            try {
                // Would send AcceptFriendship message
                
                val friend = Friend(
                    agentId = offer.fromAgentId,
                    name = offer.fromName,
                    rightsGiven = RIGHTS_ONLINE_STATUS,
                    rightsHas = RIGHTS_ONLINE_STATUS
                )
                
                friends[offer.fromAgentId] = friend
                
                _friendsFlow.emit(FriendEvent.Added(friend))
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to accept friendship", e)
                false
            }
        }
    }
    
    /**
     * Decline friendship offer
     */
    suspend fun declineFriendship(transactionId: UUID): Boolean {
        val offer = pendingOffers.remove(transactionId) ?: return false
        
        return withContext(Dispatchers.IO) {
            try {
                // Would send DeclineFriendship message
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to decline friendship", e)
                false
            }
        }
    }
    
    /**
     * Offer friendship to someone
     */
    suspend fun offerFriendship(targetAgentId: UUID, message: String = ""): Boolean {
        Log.d(TAG, "👋 Sending friendship offer to: $targetAgentId")
        NetworkLogger.logFriendshipOfferSent(targetAgentId.toString(), message)
        
        return withContext(Dispatchers.IO) {
            try {
                // OfferFriendship via ImprovedInstantMessage
                val transactionId = UUID.randomUUID()
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                
                val payload = ByteBuffer.allocate(200 + messageBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData block - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // MessageBlock
                payload.put(0)  // FromGroup = false
                payload.putUUID(targetAgentId)
                payload.putInt(0)  // ParentEstateID
                payload.putUUID(UUID(0, 0))  // RegionID (empty)
                payload.putFloat(0f)  // Position X
                payload.putFloat(0f)  // Position Y
                payload.putFloat(0f)  // Position Z
                payload.put(0)  // Offline
                payload.put(38)  // Dialog = IM_FRIENDSHIP_OFFERED
                payload.putUUID(transactionId)
                payload.putInt((System.currentTimeMillis() / 1000).toInt())  // Timestamp
                
                // FromAgentName
                val nameBytes = "User".toByteArray(Charsets.UTF_8)
                payload.put(nameBytes.size.toByte())
                payload.put(nameBytes)
                
                // Message
                payload.putShort(messageBytes.size.toShort())
                payload.put(messageBytes)
                
                // BinaryBucket - empty for friendship offer
                payload.putShort(0)
                
                udpConnection.sendPacket(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE, payload.array().copyOf(payload.position()), reliable = true)
                Log.i(TAG, "✓ Friendship offer sent to $targetAgentId (transaction: $transactionId)")
                true
            } catch (e: Exception) {
                Log.e(TAG, "✗ Failed to offer friendship to $targetAgentId", e)
                false
            }
        }
    }
    
    /**
     * Remove a friend
     */
    suspend fun removeFriend(friendAgentId: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // TerminateFriendship message
                val payload = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // ExBlock
                payload.putUUID(friendAgentId)
                
                udpConnection.sendPacket(MessageIdRegistry.TERMINATE_FRIENDSHIP, payload.array().copyOf(payload.position()), reliable = true)
                
                friends.remove(friendAgentId)
                _onlineFriends.value = _onlineFriends.value - friendAgentId
                
                _friendsFlow.emit(FriendEvent.Removed(friendAgentId))
                Log.i(TAG, "Removed friend $friendAgentId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove friend", e)
                false
            }
        }
    }
    
    /**
     * Update rights given to friend
     */
    suspend fun updateRightsGiven(friendAgentId: UUID, rights: Int): Boolean {
        val friend = friends[friendAgentId] ?: return false
        
        return withContext(Dispatchers.IO) {
            try {
                // GrantUserRights message
                val payload = ByteBuffer.allocate(56).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // Rights block count
                payload.put(1)
                
                // Rights block
                payload.putUUID(friendAgentId)
                payload.putInt(rights)
                
                udpConnection.sendPacket(MessageIdRegistry.GRANT_USER_RIGHTS, payload.array().copyOf(payload.position()), reliable = true)
                
                friends[friendAgentId] = friend.copy(rightsGiven = rights)
                
                _friendsFlow.emit(FriendEvent.RightsChanged(friendAgentId))
                Log.i(TAG, "Updated rights for friend $friendAgentId: $rights")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update rights", e)
                false
            }
        }
    }
    
    /**
     * Get pending friendship offers
     */
    fun getPendingOffers(): List<FriendshipOffer> = pendingOffers.values.toList()
    
    /**
     * Track friend on map (if they've granted permission)
     */
    suspend fun trackFriend(friendAgentId: UUID): TrackResult? {
        val friend = friends[friendAgentId] ?: return null
        
        if ((friend.rightsHas and RIGHTS_MAP_LOCATION) == 0) {
            return TrackResult.NoPermission
        }
        
        return withContext(Dispatchers.IO) {
            try {
                // FindAgent message
                val payload = ByteBuffer.allocate(50).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData (requesting agent) - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // TargetBlock
                payload.putUUID(friendAgentId)
                
                udpConnection.sendPacket(MessageIdRegistry.FIND_AGENT, payload.array().copyOf(payload.position()), reliable = true)
                Log.d(TAG, "Requested location for friend $friendAgentId")
                
                // Response will come asynchronously via event handler
                // For now return pending status
                TrackResult.Pending
            } catch (e: Exception) {
                Log.e(TAG, "Failed to track friend", e)
                null
            }
        }
    }
    
    /**
     * Request teleport to friend
     */
    suspend fun requestTeleportToFriend(friendAgentId: UUID, message: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // TeleportLureRequest via ImprovedInstantMessage with dialog type 22 (IM_LURE_USER)
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                val payload = ByteBuffer.allocate(200 + messageBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // MessageBlock
                payload.put(0)  // FromGroup = false
                payload.putUUID(friendAgentId)
                payload.putInt(0)  // ParentEstateID
                payload.putUUID(UUID(0, 0))  // RegionID (empty)
                payload.putFloat(0f)  // Position X
                payload.putFloat(0f)  // Position Y
                payload.putFloat(0f)  // Position Z
                payload.put(0)  // Offline
                payload.put(22)  // Dialog = IM_LURE_USER (teleport request)
                
                val transactionId = UUID.randomUUID()
                payload.putUUID(transactionId)
                payload.putInt((System.currentTimeMillis() / 1000).toInt())
                
                // FromAgentName
                val nameBytes = "User".toByteArray(Charsets.UTF_8)
                payload.put(nameBytes.size.toByte())
                payload.put(nameBytes)
                
                // Message
                payload.putShort(messageBytes.size.toShort())
                if (messageBytes.isNotEmpty()) payload.put(messageBytes)
                
                // BinaryBucket - empty
                payload.putShort(0)
                
                udpConnection.sendPacket(MessageIdRegistry.IMPROVED_INSTANT_MESSAGE, payload.array().copyOf(payload.position()), reliable = true)
                Log.i(TAG, "Requested teleport to friend $friendAgentId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request teleport", e)
                false
            }
        }
    }
    
    /**
     * Offer teleport to friend
     */
    suspend fun offerTeleportToFriend(friendAgentId: UUID, message: String = ""): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // StartLure message - offer teleport TO your location
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                val payload = ByteBuffer.allocate(200 + messageBytes.size).order(ByteOrder.LITTLE_ENDIAN)
                
                // AgentData - UUIDs use big-endian per SL protocol
                payload.putUUID(agentId)
                payload.putUUID(udpConnection.getSessionId())
                
                // Info block
                payload.put(0)  // LureType = 0 (normal teleport offer)
                
                // MessageBlock - sending IM with dialog type 15 (IM_TELEPORT_REQUEST)
                payload.put(0)  // FromGroup = false
                payload.putUUID(friendAgentId)
                payload.putInt(0)  // ParentEstateID
                payload.putUUID(UUID(0, 0))  // RegionID (empty)
                payload.putFloat(0f)  // Position X
                payload.putFloat(0f)  // Position Y
                payload.putFloat(0f)  // Position Z
                payload.put(0)  // Offline
                
                val transactionId = UUID.randomUUID()
                payload.putUUID(transactionId)
                
                // Message
                payload.putShort(messageBytes.size.toShort())
                if (messageBytes.isNotEmpty()) payload.put(messageBytes)
                
                // TargetID (friend to send offer to)
                payload.put(1)  // Number of targets
                payload.putUUID(friendAgentId)
                
                udpConnection.sendPacket(MessageIdRegistry.START_LURE, payload.array().copyOf(payload.position()), reliable = true)
                Log.i(TAG, "Offered teleport to friend $friendAgentId")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to offer teleport", e)
                false
            }
        }
    }
    
    fun shutdown() {
        Log.d(TAG, "FriendsManager shutting down - ${friends.size} friends, ${_onlineFriends.value.size} online")
        scope.cancel()
    }
    
    // ==================== UDP MESSAGE HANDLERS ====================
    
    /**
     * Handle OnlineNotification from UDP (fallback when capability events unavailable)
     */
    fun handleUdpOnlineNotification(agentId: UUID) {
        Log.i(TAG, "🟢 UDP OnlineNotification: $agentId came online")
        
        val friend = friends[agentId]
        val friendName = friend?.name ?: "Unknown"
        
        friend?.isOnline = true
        _onlineFriends.value = _onlineFriends.value + agentId
        
        NetworkLogger.logFriendOnlineStatus(agentId.toString(), friendName, true)
        
        scope.launch {
            _friendsFlow.emit(FriendEvent.OnlineStatusChanged(agentId, true))
        }
    }
    
    /**
     * Handle OfflineNotification from UDP (fallback when capability events unavailable)
     */
    fun handleUdpOfflineNotification(agentId: UUID) {
        Log.i(TAG, "🔴 UDP OfflineNotification: $agentId went offline")
        
        val friend = friends[agentId]
        val friendName = friend?.name ?: "Unknown"
        
        friend?.isOnline = false
        _onlineFriends.value = _onlineFriends.value - agentId
        
        NetworkLogger.logFriendOnlineStatus(agentId.toString(), friendName, false)
        
        scope.launch {
            _friendsFlow.emit(FriendEvent.OnlineStatusChanged(agentId, false))
        }
    }
    
    /**
     * Handle ChangeUserRights from UDP
     */
    fun handleUdpRightsChange(friendId: UUID, newRights: Int) {
        Log.i(TAG, "🔐 UDP ChangeUserRights: $friendId rights=$newRights")
        
        val friend = friends[friendId]
        if (friend != null) {
            friends[friendId] = friend.copy(rightsHas = newRights)
            
            scope.launch {
                _friendsFlow.emit(FriendEvent.RightsChanged(friendId))
            }
        }
    }
    
    /**
     * Add or update friend from login response data
     */
    fun addFriendFromLogin(agentId: UUID, name: String, rightsGiven: Int, rightsHas: Int) {
        val resolvedName = name.ifBlank { "Resident (${agentId.toString().take(8)})" }
        Log.d(TAG, "Adding friend from login: $resolvedName ($agentId)")
        val friend = Friend(
            agentId = agentId,
            name = resolvedName,
            rightsGiven = rightsGiven,
            rightsHas = rightsHas
        )
        val isNew = friends.put(agentId, friend) == null
        // Emit so any UI that's already observing the flow refreshes once buddy-list
        // parsing completes after the user has opened the Friends screen.
        if (isNew) {
            scope.launch {
                _friendsFlow.emit(FriendEvent.Added(friend))
            }
        }
    }
    
    /**
     * Return the agent IDs of friends whose display name has not been
     * resolved yet (i.e. still on the synthesised placeholder). Callers
     * can feed this back into [ProfileManager.getDisplayNames] when the
     * Friends screen opens so login-time lookup failures don't leave the
     * list permanently stuck on placeholders.
     */
    fun getUnresolvedNameAgentIds(): List<UUID> =
        friends.values
            .filter { it.name.startsWith(PLACEHOLDER_NAME_PREFIX) || it.name.isBlank() }
            .map { it.agentId }

    /**
     * Update a friend's display name after resolution.
     */
    fun updateFriendName(agentId: UUID, displayName: String) {
        friends[agentId]?.let { friend ->
            if (friend.name != displayName) {
                friends[agentId] = friend.copy(name = displayName)
                Log.d(TAG, "Updated friend name: $agentId -> $displayName")
                scope.launch {
                    _friendsFlow.emit(FriendEvent.NameUpdated(agentId))
                }
            }
        }
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Get diagnostic information for debug reports
     */
    fun getDiagnostics(): FriendsDiagnostics {
        return FriendsDiagnostics(
            totalFriends = friends.size,
            onlineFriends = _onlineFriends.value.size,
            pendingOffers = pendingOffers.size,
            friendsList = friends.values.map { friend ->
                FriendInfo(
                    agentId = friend.agentId.toString(),
                    name = friend.name,
                    isOnline = friend.isOnline,
                    rightsGiven = formatRights(friend.rightsGiven),
                    rightsHas = formatRights(friend.rightsHas)
                )
            }
        )
    }
    
    private fun formatRights(rights: Int): String {
        val rightsList = mutableListOf<String>()
        if ((rights and RIGHTS_ONLINE_STATUS) != 0) rightsList.add("Online")
        if ((rights and RIGHTS_MAP_LOCATION) != 0) rightsList.add("Map")
        if ((rights and RIGHTS_MODIFY_OBJECTS) != 0) rightsList.add("Modify")
        return rightsList.ifEmpty { listOf("None") }.joinToString(", ")
    }
    
    /**
     * Friends diagnostics data class
     */
    data class FriendsDiagnostics(
        val totalFriends: Int,
        val onlineFriends: Int,
        val pendingOffers: Int,
        val friendsList: List<FriendInfo>
    )
    
    /**
     * Individual friend info for diagnostics
     */
    data class FriendInfo(
        val agentId: String,
        val name: String,
        val isOnline: Boolean,
        val rightsGiven: String,
        val rightsHas: String
    )
}

@Parcelize
data class Friend(
    val agentId: UUID,
    val name: String,
    val rightsGiven: Int,
    val rightsHas: Int,
    var isOnline: Boolean = false,
    var lastSeenTime: Long = System.currentTimeMillis()
) : Parcelable {
    val canSeeOnline: Boolean get() = (rightsHas and FriendsManager.RIGHTS_ONLINE_STATUS) != 0
    val canTrack: Boolean get() = (rightsHas and FriendsManager.RIGHTS_MAP_LOCATION) != 0
    val canModifyObjects: Boolean get() = (rightsHas and FriendsManager.RIGHTS_MODIFY_OBJECTS) != 0
}

@Parcelize
data class FriendshipOffer(
    val transactionId: UUID,
    val fromAgentId: UUID,
    val fromName: String,
    val message: String,
    val timestamp: Long
) : Parcelable

sealed class FriendEvent {
    data class Added(val friend: Friend) : FriendEvent()
    data class Removed(val agentId: UUID) : FriendEvent()
    data class OnlineStatusChanged(val agentId: UUID, val isOnline: Boolean) : FriendEvent()
    data class RightsChanged(val agentId: UUID) : FriendEvent()
    data class OfferReceived(val offer: FriendshipOffer) : FriendEvent()
    data class NameUpdated(val agentId: UUID) : FriendEvent()
}

sealed class TrackResult {
    data class Located(val region: String, val x: Float, val y: Float, val z: Float) : TrackResult()
    object NotFound : TrackResult()
    object NoPermission : TrackResult()
    object Pending : TrackResult()
}
