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
    const val String DEFAULT_NOTIFICATION_CHANNEL = "miscellaneous"
    private const val ImmutableSet<NotificationType> allChannels = ImmutableSet.of(NotificationType.LocalChat, NotificationType.Group, NotificationType.Private)

    public Boolean areNotificationsSystemControlled() {
        return false
    }

    public ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        return allChannels
    }

    public String getNotificationChannelName(NotificationChannels.Channel channel) {
        return DEFAULT_NOTIFICATION_CHANNEL
    }

    public String getNotificationSummary(Context context, NotificationChannels.Channel channel) {
        return null
    }

    public Boolean showSystemNotificationSettings(Context context, Fragment fragment, NotificationChannels.Channel channel) {
        return false
    }

    public Boolean useNotificationGroups() {
        return Build.VERSION.SDK_INT >= 24
    }
}
