package com.lumiyaviewer.lumiya.models

/**
 * Common Data Models for Linkpoint
 * 
 * Defines common data structures used throughout the app.
 */

/**
 * User Model
 */
data class User(
    val id: Long,
    val username: String,
    val displayName: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val joinDate: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false,
    val lastSeen: Long? = null
)

/**
 * Chat Message Model
 */
data class ChatMessage(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: MessageType = MessageType.TEXT,
    val isRead: Boolean = false
) {
    enum class MessageType {
        TEXT, IMAGE, SYSTEM, NOTIFICATION
    }
}

/**
 * Chat Channel Model
 */
data class ChatChannel(
    val id: Long,
    val name: String,
    val type: ChannelType,
    val participantCount: Int = 0,
    val lastMessage: ChatMessage? = null,
    val unreadCount: Int = 0
) {
    enum class ChannelType {
        DIRECT, GROUP, LOCAL, REGION
    }
}

/**
 * Grid Info Model
 */
data class GridInfo(
    val name: String,
    val url: String,
    val description: String,
    val isDefault: Boolean = false,
    val loginUri: String? = null
)

/**
 * Location Model
 */
data class Location(
    val regionName: String,
    val x: Float,
    val y: Float,
    val z: Float,
    val gridUrl: String
) {
    fun toSLURL(): String {
        return "secondlife://$regionName/${x.toInt()}/${y.toInt()}/${z.toInt()}"
    }
    
    override fun toString(): String {
        return "$regionName (${x.toInt()}, ${y.toInt()}, ${z.toInt()})"
    }
}

/**
 * Object Model
 */
data class WorldObject(
    val id: Long,
    val localId: Int,
    val name: String,
    val description: String? = null,
    val ownerId: Long,
    val ownerName: String,
    val position: Position,
    val rotation: Rotation,
    val scale: Scale,
    val isPhysical: Boolean = false,
    val isTemporary: Boolean = false,
    val isPhantom: Boolean = false
)

/**
 * Position Model
 */
data class Position(
    val x: Float,
    val y: Float,
    val z: Float
) {
    fun distanceTo(other: Position): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
    }
    
    override fun toString(): String {
        return "(${x.format(2)}, ${y.format(2)}, ${z.format(2)})"
    }
}

/**
 * Rotation Model
 */
data class Rotation(
    val x: Float,
    val y: Float,
    val z: Float,
    val w: Float
) {
    override fun toString(): String {
        return "(${x.format(2)}, ${y.format(2)}, ${z.format(2)}, ${w.format(2)})"
    }
}

/**
 * Scale Model
 */
data class Scale(
    val x: Float,
    val y: Float,
    val z: Float
) {
    override fun toString(): String {
        return "(${x.format(2)}, ${y.format(2)}, ${z.format(2)})"
    }
}

/**
 * Inventory Item Model
 */
data class InventoryItem(
    val id: Long,
    val uuid: String,
    val name: String,
    val description: String? = null,
    val type: InventoryType,
    val parentId: Long? = null,
    val creatorId: Long,
    val ownerId: Long,
    val creationDate: Long = System.currentTimeMillis(),
    val permissions: Permissions
) {
    enum class InventoryType {
        FOLDER,
        TEXTURE,
        SOUND,
        LANDMARK,
        SCRIPT,
        CLOTHING,
        OBJECT,
        NOTECARD,
        ANIMATION,
        GESTURE,
        BODYPART,
        UNKNOWN
    }
}

/**
 * Permissions Model
 */
data class Permissions(
    val canCopy: Boolean = true,
    val canModify: Boolean = true,
    val canTransfer: Boolean = true,
    val canShare: Boolean = false
)

/**
 * Friend Model
 */
data class Friend(
    val id: Long,
    val username: String,
    val displayName: String,
    val isOnline: Boolean = false,
    val lastSeen: Long? = null,
    val location: Location? = null,
    val status: FriendStatus = FriendStatus.FRIEND
) {
    enum class FriendStatus {
        FRIEND, PENDING, BLOCKED
    }
}

/**
 * Group Model
 */
data class Group(
    val id: Long,
    val uuid: String,
    val name: String,
    val description: String? = null,
    val memberCount: Int = 0,
    val foundedDate: Long,
    val isOpen: Boolean = false,
    val role: GroupRole = GroupRole.MEMBER
) {
    enum class GroupRole {
        OWNER, OFFICER, MEMBER
    }
}

/**
 * Notification Model
 */
data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionUrl: String? = null
) {
    enum class NotificationType {
        MESSAGE, FRIEND_REQUEST, GROUP_INVITE, SYSTEM, PAYMENT, ALERT
    }
}

/**
 * Preference Model
 */
data class UserPreferences(
    val themeMode: String = "SYSTEM",
    val useDynamicColor: Boolean = true,
    val useHighContrast: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val chatNotifications: Boolean = true,
    val groupNotifications: Boolean = true,
    val friendNotifications: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val allowInstantMessages: Boolean = true,
    val highQualityGraphics: Boolean = true,
    val enableShadows: Boolean = true
)

/**
 * Connection Status Model
 */
data class ConnectionStatus(
    val isConnected: Boolean,
    val gridUrl: String?,
    val regionName: String?,
    val ping: Int? = null,
    val bandwidth: Float? = null,
    val lastUpdate: Long = System.currentTimeMillis()
)

/**
 * Statistics Model
 */
data class Statistics(
    val fps: Float = 0f,
    val objectsRendered: Int = 0,
    val avatarsVisible: Int = 0,
    val memoryUsed: Long = 0,
    val networkBytesReceived: Long = 0,
    val networkBytesSent: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Extension functions
 */

/**
 * Format float to specified decimal places
 */
private fun Float.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}

/**
 * Check if user is recently online (within last hour)
 */
fun User.isRecentlyOnline(): Boolean {
    val lastSeen = this.lastSeen ?: return false
    val hourAgo = System.currentTimeMillis() - (60 * 60 * 1000)
    return lastSeen > hourAgo
}

/**
 * Get time ago string
 */
fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> "${diff / 604800_000}w ago"
    }
}

/**
 * Check if message is recent (within last 5 minutes)
 */
fun ChatMessage.isRecent(): Boolean {
    val fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000)
    return timestamp > fiveMinutesAgo
}

/**
 * Get inventory item icon name
 */
fun InventoryItem.InventoryType.getIconName(): String {
    return when (this) {
        InventoryItem.InventoryType.FOLDER -> "folder"
        InventoryItem.InventoryType.TEXTURE -> "image"
        InventoryItem.InventoryType.SOUND -> "music_note"
        InventoryItem.InventoryType.LANDMARK -> "place"
        InventoryItem.InventoryType.SCRIPT -> "code"
        InventoryItem.InventoryType.CLOTHING -> "checkroom"
        InventoryItem.InventoryType.OBJECT -> "category"
        InventoryItem.InventoryType.NOTECARD -> "description"
        InventoryItem.InventoryType.ANIMATION -> "animation"
        InventoryItem.InventoryType.GESTURE -> "gesture"
        InventoryItem.InventoryType.BODYPART -> "accessibility"
        InventoryItem.InventoryType.UNKNOWN -> "help"
    }
}