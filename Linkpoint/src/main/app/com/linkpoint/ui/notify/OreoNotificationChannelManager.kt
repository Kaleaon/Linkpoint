package com.linkpoint.ui.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.linkpoint.Debug
import com.linkpoint.LumiyaApp
import com.linkpoint.R
import com.linkpoint.ui.media.NotificationSounds
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import java.util.EnumMap
import java.util.Map
import androidx.annotation.NonNull
import androidx.annotation.Nullable

@RequiresApi(api = 26)
class OreoNotificationChannelManager : NotificationChannelManager {
    private ImmutableMap<NotificationChannels.Channel, NotificationChannelSettings> channelSettings = ImmutableMap.of(NotificationChannels.Channel.OnlineStatus, NotificationChannelSettings(2, false, (NotificationType) null, (NotificationChannelSettings) null), NotificationChannels.Channel.Local, NotificationChannelSettings(3, true, NotificationType.LocalChat, (NotificationChannelSettings) null), NotificationChannels.Channel.Group, NotificationChannelSettings(3, true, NotificationType.Group, (NotificationChannelSettings) null), NotificationChannels.Channel.IM, NotificationChannelSettings(4, true, NotificationType.Private, (NotificationChannelSettings) null))
    private Map<NotificationChannels.Channel, NotificationChannel> channels = EnumMap(NotificationChannels.Channel.class)
    private Any lock = Any()

    private class NotificationChannelSettings {
        Int importance
        NotificationType notificationType
        Boolean showBadge

        private NotificationChannelSettings(Int i, Boolean z, NotificationType notificationType2) {
            this.importance = i
            this.showBadge = z
            this.notificationType = notificationType2
        }

        /* synthetic */ NotificationChannelSettings(Int i, Boolean z, NotificationType notificationType2, NotificationChannelSettings notificationChannelSettings) {
            this(i, z, notificationType2)
        }
    }

    Boolean areNotificationsSystemControlled() {
        return true
    }

    @NonNull
    ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        NotificationChannels instance = NotificationChannels.getInstance()
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification")
        ImmutableSet.Builder builder = ImmutableSet.builder()
        for (NotificationType notificationType : NotificationType.VALUES) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(getNotificationChannelName(instance.getChannelByType(notificationType)))
            if (notificationChannel != null && notificationChannel.getImportance() > 0) {
                builder.add((Any) notificationType)
            }
        }
        return builder.build()
    }

    @NonNull
    String getNotificationChannelName(@NonNull NotificationChannels.Channel channel) {
        String str
        synchronized (this.lock) {
            NotificationChannel notificationChannel = this.channels.get(channel)
            if (notificationChannel != null) {
                str = notificationChannel.getId()
            } else {
                Context context = LumiyaApp.getContext()
                NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification")
                NotificationChannelSettings notificationChannelSettings = this.channelSettings.get(channel)
                NotificationChannel notificationChannel2 = NotificationChannel(channel.channelId, context.getString(channel.nameStringId), notificationChannelSettings.importance)
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

    @Nullable
    String getNotificationSummary(Context context, @NonNull NotificationChannels.Channel channel) {
        NotificationChannel notificationChannel = ((NotificationManager) context.getSystemService("notification")).getNotificationChannel(getNotificationChannelName(channel))
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

    Boolean showSystemNotificationSettings(Context context, @Nullable Fragment fragment, @NonNull NotificationChannels.Channel channel) {
        Intent intent = Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS")
        intent.putExtra("android.provider.extra.CHANNEL_ID", getNotificationChannelName(channel))
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName())
        if (fragment != null) {
            fragment.startActivityForResult(intent, 11)
            return true
        }
        context.startActivity(intent)
        return true
    }

    Boolean useNotificationGroups() {
        return true
    }
}
