package com.linkpoint.world

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.messages.UDPConnection
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages friends list, online status, and friendship operations
 */
class FriendsManager(
    private val udpConnection: UDPConnection,
    private val capabilityManager: CapabilityManager,
    private val agentId: UUID
) : EventHandler {
    
    companion object {
        private const val TAG = "FriendsManager"
        
        // Friendship rights
        const val RIGHTS_ONLINE_STATUS = 0x01
        const val RIGHTS_MAP_LOCATION = 0x02
        const val RIGHTS_MODIFY_OBJECTS = 0x04
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
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
        capabilityManager.registerEventHandler("FriendshipOffered", this)
        capabilityManager.registerEventHandler("FriendshipAccepted", this)
        capabilityManager.registerEventHandler("FriendshipDeclined", this)
        capabilityManager.registerEventHandler("FriendshipTerminated", this)
        capabilityManager.registerEventHandler("OnlineNotification", this)
        capabilityManager.registerEventHandler("OfflineNotification", this)
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
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
        pendingOffers.remove(transactionId)
    }
    
    private fun handleFriendshipTerminated(body: LLSDMap) {
        val otherId = UUID.fromString(body.getString("other_id") ?: return)
        
        friends.remove(otherId)?.let { friend ->
            _onlineFriends.value = _onlineFriends.value - otherId
            
            scope.launch {
                _friendsFlow.emit(FriendEvent.Removed(friend.agentId))
            }
        }
    }
    
    private fun handleOnlineNotification(body: LLSDMap) {
        val agents = body.getArray("AgentOnline")
        agents?.value?.forEach { agent ->
            if (agent is LLSDMap) {
                val agentIdStr = agent.getString("agent_id") ?: return@forEach
                val agentId = UUID.fromString(agentIdStr)
                
                friends[agentId]?.isOnline = true
                _onlineFriends.value = _onlineFriends.value + agentId
                
                scope.launch {
                    _friendsFlow.emit(FriendEvent.OnlineStatusChanged(agentId, true))
                }
            }
        }
    }
    
    private fun handleOfflineNotification(body: LLSDMap) {
        val agents = body.getArray("AgentOffline")
        agents?.value?.forEach { agent ->
            if (agent is LLSDMap) {
                val agentIdStr = agent.getString("agent_id") ?: return@forEach
                val agentId = UUID.fromString(agentIdStr)
                
                friends[agentId]?.isOnline = false
                _onlineFriends.value = _onlineFriends.value - agentId
                
                scope.launch {
                    _friendsFlow.emit(FriendEvent.OnlineStatusChanged(agentId, false))
                }
            }
        }
    }
    
    /**
     * Get all friends
     */
    fun getAllFriends(): List<Friend> = friends.values.toList()
    
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
        return withContext(Dispatchers.IO) {
            try {
                // Would send OfferFriendship message
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to offer friendship", e)
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
                // Would send TerminateFriendship message
                
                friends.remove(friendAgentId)
                _onlineFriends.value = _onlineFriends.value - friendAgentId
                
                _friendsFlow.emit(FriendEvent.Removed(friendAgentId))
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
                // Would send GrantUserRights message
                
                friends[friendAgentId] = friend.copy(rightsGiven = rights)
                
                _friendsFlow.emit(FriendEvent.RightsChanged(friendAgentId))
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
                // Would send FindAgent message
                // Return their location
                null
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
                // Would send LureRequest message
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
                // Would send StartLure message
                true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to offer teleport", e)
                false
            }
        }
    }
    
    fun shutdown() {
        scope.cancel()
    }
}

data class Friend(
    val agentId: UUID,
    val name: String,
    val rightsGiven: Int,
    val rightsHas: Int,
    var isOnline: Boolean = false
) {
    val canSeeOnline: Boolean get() = (rightsHas and FriendsManager.RIGHTS_ONLINE_STATUS) != 0
    val canTrack: Boolean get() = (rightsHas and FriendsManager.RIGHTS_MAP_LOCATION) != 0
    val canModifyObjects: Boolean get() = (rightsHas and FriendsManager.RIGHTS_MODIFY_OBJECTS) != 0
}

data class FriendshipOffer(
    val transactionId: UUID,
    val fromAgentId: UUID,
    val fromName: String,
    val message: String,
    val timestamp: Long
)

sealed class FriendEvent {
    data class Added(val friend: Friend) : FriendEvent()
    data class Removed(val agentId: UUID) : FriendEvent()
    data class OnlineStatusChanged(val agentId: UUID, val isOnline: Boolean) : FriendEvent()
    data class RightsChanged(val agentId: UUID) : FriendEvent()
    data class OfferReceived(val offer: FriendshipOffer) : FriendEvent()
}

sealed class TrackResult {
    data class Located(val region: String, val x: Float, val y: Float, val z: Float) : TrackResult()
    object NotFound : TrackResult()
    object NoPermission : TrackResult()
}
