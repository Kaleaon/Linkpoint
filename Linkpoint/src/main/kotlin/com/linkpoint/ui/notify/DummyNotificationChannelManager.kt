package com.linkpoint.ui.notify

import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import com.google.common.collect.ImmutableSet
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import javax.annotation.Nonnull
import javax.annotation.Nullable

class DummyNotificationChannelManager : NotificationChannelManager {
    const val DEFAULT_NOTIFICATION_CHANNEL: String = "miscellaneous"
    private const val ImmutableSet<NotificationType> allChannels = ImmutableSet.of(NotificationType.LocalChat, NotificationType.Group, NotificationType.Private)

     public fun areNotificationsSystemControlled(): Boolean {
        return false
    }

    public ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        return allChannels
    }

     public fun getNotificationChannelName(NotificationChannels.Channel channel): String {
        return DEFAULT_NOTIFICATION_CHANNEL
    }

     public fun getNotificationSummary(context: Context, NotificationChannels.Channel channel): String {
        return null
    }

     public fun showSystemNotificationSettings(context: Context, fragment: Fragment, NotificationChannels.Channel channel): Boolean {
        return false
    }

     public fun useNotificationGroups(): Boolean {
        return Build.VERSION.SDK_INT >= 24
    }
}
