package com.linkpoint.social

import com.linkpoint.Debug
import com.linkpoint.slproto.SLCircuitNew
import com.linkpoint.slproto.caps.CAPSManager
import kotlinx.coroutines.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * FriendStatus - Online status of a friend
 */
enum class FriendStatus {
    OFFLINE,
    ONLINE,
    AWAY,
    BUSY
}

/**
 * FriendRights - Permission bits for friends
 */
object FriendRights {
    const val NONE = 0
    const val CAN_SEE_ONLINE = 1 shl 0      // Can see when I'm online
    const val CAN_SEE_ON_MAP = 1 shl 1      // Can see me on map
    const val CAN_MODIFY_OBJECTS = 1 shl 2  // Can modify my objects
}

/**
 * Friend - Represents a friend entry
 */
data class Friend(
    val friendID: UUID,
    val name: String,
    var status: FriendStatus,
    var rights: Int,
    var myRights: Int,  // Rights I granted to them
    var theirRights: Int,  // Rights they granted to me
    var lastOnline: Long = 0L
)

/**
 * FriendRequest - Pending friend request
 */
data class FriendRequest(
    val requestID: UUID,
    val fromID: UUID,
    val fromName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * FriendsManager - Manages friends list and relationships
 * 
 * Features:
 * - Friends list
 * - Online status tracking
 * - Friend requests
 * - Permission management
 * - Online notifications
 * 
 * Based on Firestorm's LLAvatarTracker
 */
class FriendsManager(
    private val circuit: SLCircuitNew,
    private val capsManager: CAPSManager,
    private val agentID: UUID
) {
    
    companion object {
        const val MAX_FRIENDS = 500
    }
    
    // Friends list
    private val friends = ConcurrentHashMap<UUID, Friend>()
    
    // Pending friend requests
    private val pendingRequests = CopyOnWriteArrayList<FriendRequest>()
    
    // Listeners
    private val statusListeners = CopyOnWriteArrayList<FriendStatusListener>()
    private val requestListeners = CopyOnWriteArrayList<FriendRequestListener>()
    
    /**
     * Fetch friends list
     */
    suspend fun fetchFriendsList() = withContext(Dispatchers.IO) {
        try {
            val capURL = capsManager.getCapability("FetchFriends")
            if (capURL == null) {
                Debug.Log("FetchFriends capability not available")
                return@withContext
            }
            
            // Make CAPS request
            val request = com.linkpoint.slproto.llsd.LLSD.emptyMap()
            request["agent_id"] = com.linkpoint.slproto.llsd.LLSD(agentID)
            
            val response = capsManager.postLLSD(capURL, request)
            
            // Parse friends
            if (response.isMap() && response.has("friends")) {
                val friendsArray = response["friends"]
                if (friendsArray.isArray()) {
                    for (i in 0 until friendsArray.size()) {
                        val friendData = friendsArray[i]
                        parseFriend(friendData)
                    }
                }
            }
            
            Debug.Log("Loaded ${friends.size} friends")
            
        } catch (e: Exception) {
            Debug.Log("Failed to fetch friends: ${e.message}")
        }
    }
    
    /**
     * Parse friend from LLSD
     */
    private fun parseFriend(llsd: com.linkpoint.slproto.llsd.LLSD) {
        val friendID = llsd["friend_id"].asUUID()
        val name = llsd["name"].asString()
        val online = llsd["online"].asBoolean()
        val myRights = llsd["my_rights"].asInteger()
        val theirRights = llsd["their_rights"].asInteger()
        
        val friend = Friend(
            friendID = friendID,
            name = name,
            status = if (online) FriendStatus.ONLINE else FriendStatus.OFFLINE,
            rights = myRights or theirRights,
            myRights = myRights,
            theirRights = theirRights
        )
        
        friends[friendID] = friend
    }
    
    /**
     * Send friend request
     */
    suspend fun sendFriendRequest(targetID: UUID, message: String = "") {
        // TODO: Send friend offer message via circuit
        Debug.Log("Sending friend request to $targetID")
    }
    
    /**
     * Accept friend request
     */
    suspend fun acceptFriendRequest(requestID: UUID) {
        val request = pendingRequests.find { it.requestID == requestID }
        if (request != null) {
            // TODO: Send accept message
            pendingRequests.remove(request)
            
            // Add to friends list
            val friend = Friend(
                friendID = request.fromID,
                name = request.fromName,
                status = FriendStatus.ONLINE,
                rights = FriendRights.CAN_SEE_ONLINE,
                myRights = FriendRights.CAN_SEE_ONLINE,
                theirRights = 0
            )
            friends[friend.friendID] = friend
            
            Debug.Log("Accepted friend request from ${request.fromName}")
        }
    }
    
    /**
     * Decline friend request
     */
    suspend fun declineFriendRequest(requestID: UUID) {
        val request = pendingRequests.find { it.requestID == requestID }
        if (request != null) {
            // TODO: Send decline message
            pendingRequests.remove(request)
            Debug.Log("Declined friend request from ${request.fromName}")
        }
    }
    
    /**
     * Remove friend
     */
    suspend fun removeFriend(friendID: UUID) {
        val friend = friends.remove(friendID)
        if (friend != null) {
            // TODO: Send termination message
            Debug.Log("Removed friend ${friend.name}")
        }
    }
    
    /**
     * Update friend status
     */
    fun updateFriendStatus(friendID: UUID, status: FriendStatus) {
        val friend = friends[friendID]
        if (friend != null) {
            val oldStatus = friend.status
            friend.status = status
            
            if (status == FriendStatus.ONLINE) {
                friend.lastOnline = System.currentTimeMillis()
            }
            
            // Notify listeners
            if (oldStatus != status) {
                for (listener in statusListeners) {
                    listener.onFriendStatusChanged(friend, oldStatus, status)
                }
            }
        }
    }
    
    /**
     * Update friend rights
     */
    fun updateFriendRights(friendID: UUID, myRights: Int, theirRights: Int) {
        val friend = friends[friendID]
        if (friend != null) {
            friend.myRights = myRights
            friend.theirRights = theirRights
            friend.rights = myRights or theirRights
        }
    }
    
    /**
     * Get friend by ID
     */
    fun getFriend(friendID: UUID): Friend? = friends[friendID]
    
    /**
     * Get all friends
     */
    fun getAllFriends(): List<Friend> = friends.values.toList()
    
    /**
     * Get online friends
     */
    fun getOnlineFriends(): List<Friend> {
        return friends.values.filter { it.status == FriendStatus.ONLINE }
    }
    
    /**
     * Get offline friends
     */
    fun getOfflineFriends(): List<Friend> {
        return friends.values.filter { it.status == FriendStatus.OFFLINE }
    }
    
    /**
     * Check if friend is online
     */
    fun isFriendOnline(friendID: UUID): Boolean {
        return friends[friendID]?.status == FriendStatus.ONLINE
    }
    
    /**
     * Check if is friend
     */
    fun isFriend(friendID: UUID): Boolean {
        return friends.containsKey(friendID)
    }
    
    /**
     * Get pending friend requests
     */
    fun getPendingRequests(): List<FriendRequest> = pendingRequests.toList()
    
    /**
     * Handle received friend request
     */
    fun handleFriendRequest(fromID: UUID, fromName: String, message: String) {
        val request = FriendRequest(
            requestID = UUID.randomUUID(),
            fromID = fromID,
            fromName = fromName,
            message = message
        )
        
        pendingRequests.add(request)
        
        // Notify listeners
        for (listener in requestListeners) {
            listener.onFriendRequestReceived(request)
        }
    }
    
    /**
     * Add status listener
     */
    fun addStatusListener(listener: FriendStatusListener) {
        statusListeners.add(listener)
    }
    
    /**
     * Remove status listener
     */
    fun removeStatusListener(listener: FriendStatusListener) {
        statusListeners.remove(listener)
    }
    
    /**
     * Add request listener
     */
    fun addRequestListener(listener: FriendRequestListener) {
        requestListeners.add(listener)
    }
    
    /**
     * Remove request listener
     */
    fun removeRequestListener(listener: FriendRequestListener) {
        requestListeners.remove(listener)
    }
}

/**
 * FriendStatusListener - Interface for friend status changes
 */
interface FriendStatusListener {
    fun onFriendStatusChanged(friend: Friend, oldStatus: FriendStatus, newStatus: FriendStatus)
}

/**
 * FriendRequestListener - Interface for friend request events
 */
interface FriendRequestListener {
    fun onFriendRequestReceived(request: FriendRequest)
}
