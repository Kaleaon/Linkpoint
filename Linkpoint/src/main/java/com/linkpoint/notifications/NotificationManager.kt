package com.linkpoint.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.linkpoint.R
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.LLSDMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages in-app and system notifications for Second Life events.
 * 
 * Handles notifications for:
 * - Incoming IMs
 * - Group chat messages
 * - Friendship offers
 * - Teleport offers
 * - Group notices
 * - Transaction notifications
 * - Script dialogs
 * - Permission requests
 */
class NotificationManager(
    private val context: Context
) : EventHandler {
    
    companion object {
        private const val TAG = "NotificationManager"
        
        // Notification channels
        const val CHANNEL_IM = "linkpoint_im"
        const val CHANNEL_GROUP = "linkpoint_group"
        const val CHANNEL_SYSTEM = "linkpoint_system"
        const val CHANNEL_FRIENDSHIP = "linkpoint_friendship"
        const val CHANNEL_TELEPORT = "linkpoint_teleport"
        
        // Max notifications to keep in memory
        const val MAX_NOTIFICATIONS = 100
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val notificationIdCounter = AtomicInteger(1000)
    
    // In-app notifications
    private val notifications = ConcurrentLinkedQueue<SLNotification>()
    
    // Unread count
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount
    
    // Notification events
    private val _notificationEvents = MutableSharedFlow<NotificationEvent>(replay = 0, extraBufferCapacity = 32)
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Create notification channels for Android 8+.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val imChannel = NotificationChannel(
                CHANNEL_IM,
                "Instant Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming instant messages"
            }
            
            val groupChannel = NotificationChannel(
                CHANNEL_GROUP,
                "Group Messages",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Group chat and notices"
            }
            
            val systemChannel = NotificationChannel(
                CHANNEL_SYSTEM,
                "System Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "System and transaction notifications"
            }
            
            val friendshipChannel = NotificationChannel(
                CHANNEL_FRIENDSHIP,
                "Friendship",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Friendship offers and updates"
            }
            
            val teleportChannel = NotificationChannel(
                CHANNEL_TELEPORT,
                "Teleport",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Teleport offers"
            }
            
            manager.createNotificationChannels(listOf(
                imChannel, groupChannel, systemChannel, friendshipChannel, teleportChannel
            ))
        }
    }
    
    override fun onEvent(message: String, body: LLSDMap) {
        when (message) {
            "InstantMessage" -> handleInstantMessage(body)
            "GroupNotice" -> handleGroupNotice(body)
            "ScriptDialog" -> handleScriptDialog(body)
            "TeleportOfferRequest" -> handleTeleportOffer(body)
            "FriendshipOffered" -> handleFriendshipOffer(body)
            "TransactionComplete" -> handleTransaction(body)
        }
    }
    
    private fun handleInstantMessage(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Unknown"
        val message = body.getString("message") ?: ""
        
        addNotification(SLNotification(
            id = UUID.randomUUID(),
            type = NotificationType.INSTANT_MESSAGE,
            title = fromName,
            message = message,
            fromId = fromId,
            timestamp = System.currentTimeMillis()
        ))
        
        showSystemNotification(
            CHANNEL_IM,
            fromName,
            message,
            NotificationCompat.PRIORITY_HIGH
        )
    }
    
    private fun handleGroupNotice(body: LLSDMap) {
        val groupId = UUID.fromString(body.getString("group_id") ?: return)
        val groupName = body.getString("group_name") ?: "Group"
        val subject = body.getString("subject") ?: ""
        val message = body.getString("message") ?: ""
        
        addNotification(SLNotification(
            id = UUID.randomUUID(),
            type = NotificationType.GROUP_NOTICE,
            title = "$groupName: $subject",
            message = message,
            fromId = groupId,
            timestamp = System.currentTimeMillis()
        ))
        
        showSystemNotification(
            CHANNEL_GROUP,
            "$groupName: $subject",
            message,
            NotificationCompat.PRIORITY_DEFAULT
        )
    }
    
    private fun handleScriptDialog(body: LLSDMap) {
        val objectName = body.getString("object_name") ?: "Object"
        val message = body.getString("message") ?: ""
        val buttons = body.getArray("buttons")
        
        addNotification(SLNotification(
            id = UUID.randomUUID(),
            type = NotificationType.SCRIPT_DIALOG,
            title = objectName,
            message = message,
            timestamp = System.currentTimeMillis()
        ))
    }
    
    private fun handleTeleportOffer(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Someone"
        val regionName = body.getString("region_name") ?: "Unknown region"
        
        addNotification(SLNotification(
            id = UUID.randomUUID(),
            type = NotificationType.TELEPORT_OFFER,
            title = "Teleport Offer from $fromName",
            message = "To: $regionName",
            fromId = fromId,
            timestamp = System.currentTimeMillis()
        ))
        
        showSystemNotification(
            CHANNEL_TELEPORT,
            "Teleport Offer from $fromName",
            "To: $regionName",
            NotificationCompat.PRIORITY_HIGH
        )
    }
    
    private fun handleFriendshipOffer(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Someone"
        val message = body.getString("message") ?: ""
        
        addNotification(SLNotification(
            id = UUID.randomUUID(),
            type = NotificationType.FRIENDSHIP_OFFER,
            title = "Friendship Offer from $fromName",
            message = message.ifEmpty { "Would like to be your friend" },
            fromId = fromId,
            timestamp = System.currentTimeMillis()
        ))
        
        showSystemNotification(
            CHANNEL_FRIENDSHIP,
            "Friendship Offer from $fromName",
            message.ifEmpty { "Would like to be your friend" },
            NotificationCompat.PRIORITY_HIGH
        )
    }
    
    private fun handleTransaction(body: LLSDMap) {
        val amount = body.getInteger("amount") ?: return
        val description = body.getString("description") ?: "Transaction"
        val success = body.getBoolean("success") ?: true
        
        val title = if (amount >= 0) "Received L$$amount" else "Paid L$${-amount}"
        
        addNotification(SLNotification(
            id = UUID.randomUUID(),
            type = NotificationType.TRANSACTION,
            title = title,
            message = description,
            timestamp = System.currentTimeMillis()
        ))
    }
    
    /**
     * Add a notification to the queue.
     */
    fun addNotification(notification: SLNotification) {
        notifications.add(notification)
        
        // Limit queue size
        while (notifications.size > MAX_NOTIFICATIONS) {
            notifications.poll()
        }
        
        _unreadCount.value = notifications.count { !it.isRead }
        
        scope.launch {
            _notificationEvents.emit(NotificationEvent.Added(notification))
        }
    }
    
    /**
     * Get all notifications.
     */
    fun getAllNotifications(): List<SLNotification> = notifications.toList().sortedByDescending { it.timestamp }
    
    /**
     * Mark a notification as read.
     */
    fun markAsRead(notificationId: UUID) {
        notifications.find { it.id == notificationId }?.isRead = true
        _unreadCount.value = notifications.count { !it.isRead }
    }
    
    /**
     * Mark all notifications as read.
     */
    fun markAllAsRead() {
        notifications.forEach { it.isRead = true }
        _unreadCount.value = 0
    }
    
    /**
     * Clear all notifications.
     */
    fun clearAll() {
        notifications.clear()
        _unreadCount.value = 0
    }
    
    /**
     * Show a system notification.
     */
    private fun showSystemNotification(
        channelId: String,
        title: String,
        message: String,
        priority: Int
    ) {
        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setAutoCancel(true)
                .build()
            
            NotificationManagerCompat.from(context).notify(
                notificationIdCounter.getAndIncrement(),
                notification
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "Missing notification permission", e)
        }
    }
    
    fun shutdown() {
        scope.cancel()
        notifications.clear()
    }
}

/**
 * Types of Second Life notifications.
 */
enum class NotificationType {
    INSTANT_MESSAGE,
    GROUP_CHAT,
    GROUP_NOTICE,
    FRIENDSHIP_OFFER,
    TELEPORT_OFFER,
    SCRIPT_DIALOG,
    PERMISSION_REQUEST,
    TRANSACTION,
    SYSTEM
}

/**
 * Represents a Second Life notification.
 */
data class SLNotification(
    val id: UUID,
    val type: NotificationType,
    val title: String,
    val message: String,
    val fromId: UUID? = null,
    val timestamp: Long,
    var isRead: Boolean = false,
    val data: Map<String, Any>? = null
)

/**
 * Notification events for observers.
 */
sealed class NotificationEvent {
    data class Added(val notification: SLNotification) : NotificationEvent()
    data class Removed(val notificationId: UUID) : NotificationEvent()
    object AllCleared : NotificationEvent()
}
