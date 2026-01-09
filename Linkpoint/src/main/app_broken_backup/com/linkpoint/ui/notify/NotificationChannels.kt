package com.linkpoint.ui.notify

import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.google.common.collect.ImmutableSet
import com.linkpoint.R
import com.linkpoint.ui.settings.NotificationType

/**
 * Manages notification channels for different notification types.
 * Supports both legacy (pre-Oreo) and modern (Oreo+) notification systems.
 */
class NotificationChannels private constructor() {
    
    companion object {
        @Volatile
        private var instance: NotificationChannels? = null
        
        fun getInstance(): NotificationChannels {
            return instance ?: synchronized(this) {
                instance ?: NotificationChannels().also { instance = it }
            }
        }
    }
    
    val MESSAGE_NOTIFICATION_GROUP = "messageNotifications"
    
    private val channelManager: NotificationChannelManager = if (Build.VERSION.SDK_INT < 26) {
        DummyNotificationChannelManager()
    } else {
        OreoNotificationChannelManager()
    }
    
    /**
     * Notification channels for different types of messages
     */
    enum class Channel(
        val channelId: String,
        val nameStringId: Int,
        val descriptionStringId: Int,
        val notificationType: NotificationType?,
        val notificationId: Int
    ) {
        OnlineStatus(
            "onlineStatus",
            R.string.notify_online_status_name,
            R.string.notify_online_status_desc,
            null,
            R.id.online_notify_id
        ),
        Local(
            "localChat",
            R.string.notify_local_chat_name,
            R.string.notify_local_chat_desc,
            NotificationType.LocalChat,
            R.id.unread_notify_local_id
        ),
        Group(
            "groupChat",
            R.string.notify_group_chat_name,
            R.string.notify_group_chat_desc,
            NotificationType.Group,
            R.id.unread_notify_group_id
        ),
        IM(
            "privateIM",
            R.string.notify_im_name,
            R.string.notify_im_desc,
            NotificationType.Private,
            R.id.unread_notify_im_id
        )
    }
    
    fun areNotificationsSystemControlled(): Boolean {
        return channelManager.areNotificationsSystemControlled()
    }
    
    fun getChannelByType(notificationType: NotificationType): Channel? {
        return when (notificationType) {
            NotificationType.Group -> Channel.Group
            NotificationType.LocalChat -> Channel.Local
            NotificationType.Private -> Channel.IM
            else -> null
        }
    }
    
    fun getChannelName(channel: Channel): String {
        return channelManager.getNotificationChannelName(channel)
    }
    
    fun getEnabledTypes(context: Context): ImmutableSet<NotificationType> {
        return channelManager.getEnabledTypes(context)
    }
    
    fun getNotificationSummary(context: Context, channel: Channel): String? {
        return channelManager.getNotificationSummary(context, channel)
    }
    
    fun showSystemNotificationSettings(
        context: Context,
        fragment: Fragment?,
        channel: Channel
    ): Boolean {
        return channelManager.showSystemNotificationSettings(context, fragment, channel)
    }
    
    fun useNotificationGroups(): Boolean {
        return channelManager.useNotificationGroups()
    }
}
