package com.linkpoint.notifications

import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.linkpoint.R
import com.linkpoint.protocol.capabilities.EventHandler
import com.linkpoint.protocol.llsd.LLSDMap
import com.linkpoint.push.PushEvent
import com.linkpoint.push.PushEventBus
import com.linkpoint.push.PushEventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

class NotificationManager(
    private val context: Context
) : EventHandler {

    companion object {
        private const val TAG = "NotificationManager"

        const val CHANNEL_IM = "linkpoint_im"
        const val CHANNEL_GROUP = "linkpoint_group"
        const val CHANNEL_SYSTEM = "linkpoint_system"
        const val CHANNEL_FRIENDSHIP = "linkpoint_friendship"
        const val CHANNEL_TELEPORT = "linkpoint_teleport"

        const val MAX_NOTIFICATIONS = 100
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val notificationIdCounter = AtomicInteger(1000)
    private val notifications = ConcurrentLinkedQueue<SLNotification>()
    private val processedPushIds = ConcurrentHashMap.newKeySet<String>()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _notificationEvents = MutableSharedFlow<NotificationEvent>(replay = 0, extraBufferCapacity = 32)
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents

    private val _syncOnOpenRequested = MutableStateFlow(false)
    val syncOnOpenRequested: StateFlow<Boolean> = _syncOnOpenRequested

    init {
        createNotificationChannels()
        observePushEvents()
    }

    private fun observePushEvents() {
        scope.launch {
            PushEventBus.events.collect { event ->
                onPushEvent(event)
            }
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

    fun onPushEvent(event: PushEvent) {
        if (!processedPushIds.add(event.dedupeId)) {
            Log.d(TAG, "Dropping duplicate push event ${event.dedupeId}")
            return
        }
        trimProcessedIds()

        val type = when (event.type) {
            PushEventType.IM -> NotificationType.INSTANT_MESSAGE
            PushEventType.GROUP_NOTICE -> NotificationType.GROUP_NOTICE
            PushEventType.UNKNOWN -> NotificationType.SYSTEM
        }

        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = type,
                title = event.title,
                message = event.message,
                fromId = event.senderId,
                timestamp = event.timestampMs,
                data = mapOf(
                    "pushDedupeId" to event.dedupeId,
                    "sessionId" to (event.sessionId?.toString() ?: "")
                )
            )
        )

        val channel = if (event.type == PushEventType.IM) CHANNEL_IM else CHANNEL_GROUP
        showSystemNotification(channel, event.title, event.message, NotificationCompat.PRIORITY_HIGH)
        _syncOnOpenRequested.value = true
    }

    fun acknowledgeSyncOnOpen() {
        _syncOnOpenRequested.value = false
    }

    private fun trimProcessedIds() {
        if (processedPushIds.size > MAX_NOTIFICATIONS * 4) {
            processedPushIds.clear()
        }
    }

    private fun createNotificationChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager
            manager.createNotificationChannels(
                listOf(
                    NotificationChannel(CHANNEL_IM, "Instant Messages", AndroidNotificationManager.IMPORTANCE_HIGH),
                    NotificationChannel(CHANNEL_GROUP, "Group Messages", AndroidNotificationManager.IMPORTANCE_DEFAULT),
                    NotificationChannel(CHANNEL_SYSTEM, "System Notifications", AndroidNotificationManager.IMPORTANCE_DEFAULT),
                    NotificationChannel(CHANNEL_FRIENDSHIP, "Friendship", AndroidNotificationManager.IMPORTANCE_HIGH),
                    NotificationChannel(CHANNEL_TELEPORT, "Teleport", AndroidNotificationManager.IMPORTANCE_HIGH)
                )
            )
        }
    }

    private fun handleInstantMessage(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Unknown"
        val message = body.getString("message") ?: ""
        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = NotificationType.INSTANT_MESSAGE,
                title = fromName,
                message = message,
                fromId = fromId,
                timestamp = System.currentTimeMillis()
            )
        )
        showSystemNotification(CHANNEL_IM, fromName, message, NotificationCompat.PRIORITY_HIGH)
    }

    private fun handleGroupNotice(body: LLSDMap) {
        val groupId = UUID.fromString(body.getString("group_id") ?: return)
        val groupName = body.getString("group_name") ?: "Group"
        val subject = body.getString("subject") ?: ""
        val message = body.getString("message") ?: ""

        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = NotificationType.GROUP_NOTICE,
                title = "$groupName: $subject",
                message = message,
                fromId = groupId,
                timestamp = System.currentTimeMillis()
            )
        )

        showSystemNotification(CHANNEL_GROUP, "$groupName: $subject", message, NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun handleScriptDialog(body: LLSDMap) {
        val objectName = body.getString("object_name") ?: "Object"
        val message = body.getString("message") ?: ""
        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = NotificationType.SCRIPT_DIALOG,
                title = objectName,
                message = message,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private fun handleTeleportOffer(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Someone"
        val regionName = body.getString("region_name") ?: "Unknown region"
        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = NotificationType.TELEPORT_OFFER,
                title = "Teleport Offer from $fromName",
                message = "To: $regionName",
                fromId = fromId,
                timestamp = System.currentTimeMillis()
            )
        )
        showSystemNotification(CHANNEL_TELEPORT, "Teleport Offer from $fromName", "To: $regionName", NotificationCompat.PRIORITY_HIGH)
    }

    private fun handleFriendshipOffer(body: LLSDMap) {
        val fromId = UUID.fromString(body.getString("from_id") ?: return)
        val fromName = body.getString("from_name") ?: "Someone"
        val message = body.getString("message") ?: ""
        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = NotificationType.FRIENDSHIP_OFFER,
                title = "Friendship Offer from $fromName",
                message = message.ifEmpty { "Would like to be your friend" },
                fromId = fromId,
                timestamp = System.currentTimeMillis()
            )
        )
        showSystemNotification(
            CHANNEL_FRIENDSHIP,
            "Friendship Offer from $fromName",
            message.ifEmpty { "Would like to be your friend" },
            NotificationCompat.PRIORITY_HIGH
        )
    }

    private fun handleTransaction(body: LLSDMap) {
        val amount = body.getInt("amount") ?: return
        val description = body.getString("description") ?: "Transaction"
        val title = if (amount >= 0) "Received L$$amount" else "Paid L$${-amount}"
        addNotification(
            SLNotification(
                id = UUID.randomUUID(),
                type = NotificationType.TRANSACTION,
                title = title,
                message = description,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun addNotification(notification: SLNotification) {
        notifications.add(notification)
        while (notifications.size > MAX_NOTIFICATIONS) notifications.poll()
        _unreadCount.value = notifications.count { !it.isRead }
        scope.launch { _notificationEvents.emit(NotificationEvent.Added(notification)) }
    }

    fun getAllNotifications(): List<SLNotification> = notifications.toList().sortedByDescending { it.timestamp }

    fun markAsRead(notificationId: UUID) {
        notifications.find { it.id == notificationId }?.isRead = true
        _unreadCount.value = notifications.count { !it.isRead }
    }

    fun markAllAsRead() {
        notifications.forEach { it.isRead = true }
        _unreadCount.value = 0
    }

    fun clearAll() {
        notifications.clear()
        _unreadCount.value = 0
    }

    private fun showSystemNotification(channelId: String, title: String, message: String, priority: Int) {
        try {
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context).notify(notificationIdCounter.getAndIncrement(), notification)
        } catch (e: SecurityException) {
            Log.w(TAG, "Missing notification permission", e)
        }
    }

    fun shutdown() {
        scope.cancel()
        notifications.clear()
    }
}

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

sealed class NotificationEvent {
    data class Added(val notification: SLNotification) : NotificationEvent()
    data class Removed(val notificationId: UUID) : NotificationEvent()
    object AllCleared : NotificationEvent()
}
