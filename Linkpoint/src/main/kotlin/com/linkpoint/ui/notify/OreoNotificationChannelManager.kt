package com.linkpoint.ui.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.linkpoint.Debug
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.ui.media.NotificationSounds
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import java.util.EnumMap
import java.util.Map
import javax.annotation.Nonnull
import javax.annotation.Nullable

@RequiresApi(api = 26)
class OreoNotificationChannelManager : NotificationChannelManager {
    private val ImmutableMap<NotificationChannels.Channel, NotificationChannelSettings> channelSettings = ImmutableMap.of(NotificationChannels.Channel.OnlineStatus, NotificationChannelSettings(2, false, (NotificationType) null, (NotificationChannelSettings) null), NotificationChannels.Channel.Local, NotificationChannelSettings(3, true, NotificationType.LocalChat, (NotificationChannelSettings) null), NotificationChannels.Channel.Group, NotificationChannelSettings(3, true, NotificationType.Group, (NotificationChannelSettings) null), NotificationChannels.Channel.IM, NotificationChannelSettings(4, true, NotificationType.Private, (NotificationChannelSettings) null))
    private val Map<NotificationChannels.Channel, NotificationChannel> channels = EnumMap(NotificationChannels.Channel.class)
    private val Object lock = Object()

    @JvmStatic
private class NotificationChannelSettings {
        final Int importance
        final NotificationType notificationType
        final Boolean showBadge

        private NotificationChannelSettings(Int i, Boolean z, NotificationType notificationType2) {
            this.importance = i
            this.showBadge = z
            this.notificationType = notificationType2
        }

        /* synthetic */ NotificationChannelSettings(Int i, Boolean z, NotificationType notificationType2, NotificationChannelSettings notificationChannelSettings) {
            this(i, z, notificationType2)
        }
    }

     public fun areNotificationsSystemControlled(): Boolean {
        return true
    }

    public ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        val instance: NotificationChannels = NotificationChannels.getInstance()
        val notificationManager: NotificationManager = (NotificationManager) context.getSystemService("notification")
        ImmutableSet.Builder builder = ImmutableSet.builder()
        for (NotificationType notificationType : NotificationType.VALUES) {
            val notificationChannel: NotificationChannel = notificationManager.getNotificationChannel(getNotificationChannelName(instance.getChannelByType(notificationType)))
            if (notificationChannel != null && notificationChannel.getImportance() > 0) {
                builder.add((Object) notificationType)
            }
        }
        return builder.build()
    }

     public fun getNotificationChannelName(NotificationChannels.Channel channel): String {
        String str
        synchronized (this.lock) {
            val notificationChannel: NotificationChannel = this.channels.get(channel)
            if (notificationChannel != null) {
                str = notificationChannel.getId()
            } else {
                val context: Context = LinkpointApp.getContext()
                val notificationManager: NotificationManager = (NotificationManager) context.getSystemService("notification")
                val notificationChannelSettings: NotificationChannelSettings = this.channelSettings.get(channel)
                val notificationChannel2: NotificationChannel = NotificationChannel(channel.channelId, context.getString(channel.nameStringId), notificationChannelSettings.importance)
                notificationChannel2.setDescription(context.getString(channel.descriptionStringId))
                if (notificationChannelSettings.notificationType != null) {
                    AudioAttributes.Builder builder = AudioAttributes.Builder()
                    builder.setContentType(4)
                    builder.setUsage(5)
                    notificationChannel2.setSound(NotificationSounds.defaultSounds.get(notificationChannelSettings.notificationType).getUri(), builder.build())
                }
                notificationChannel2.setShowBadge(notificationChannelSettings.showBadge)
                Debug.Printf("Notifications: Creating notification channel with id '%s'", channel.channelId)
                notificationManager.createNotificationChannel(notificationChannel2)
                this.channels.put(channel, notificationChannel2)
                str = channel.channelId
            }
        }
        return str
    }

     public fun getNotificationSummary(context: Context, NotificationChannels.Channel channel): String {
        val notificationChannel: NotificationChannel = ((NotificationManager) context.getSystemService("notification")).getNotificationChannel(getNotificationChannelName(channel))
        if (notificationChannel == null) {
            return null
        }
        switch (notificationChannel.getImportance()) {
            case 0:
                return context.getString(R.string.notification_summary_importance_disabled)
            case 1:
                return context.getString(R.string.notification_summary_importance_min)
            case 2:
                return context.getString(R.string.notification_summary_importance_low)
            case 4:
            case 5:
                return context.getString(R.string.notification_summary_importance_high)
            default:
                return context.getString(R.string.notification_summary_importance_default)
        }
    }

     public fun showSystemNotificationSettings(context: Context, fragment: Fragment, NotificationChannels.Channel channel): Boolean {
        val intent: Intent = Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS")
        intent.putExtra("android.provider.extra.CHANNEL_ID", getNotificationChannelName(channel))
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName())
        if (fragment != null) {
            fragment.startActivityForResult(intent, 11)
            return true
        }
        context.startActivity(intent)
        return true
    }

     public fun useNotificationGroups(): Boolean {
        return true
    }
}
