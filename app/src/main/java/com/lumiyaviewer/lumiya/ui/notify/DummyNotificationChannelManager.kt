package com.lumiyaviewer.lumiya.ui.notify

import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import com.google.common.collect.ImmutableSet
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import javax.annotation.Nonnull
import javax.annotation.Nullable

class DummyNotificationChannelManager : NotificationChannelManager {
    val DEFAULT_NOTIFICATION_CHANNEL: String = "miscellaneous"
    private ImmutableSet<NotificationType> allChannels = ImmutableSet.of(NotificationType.LocalChat, NotificationType.Group, NotificationType.Private)

    Boolean areNotificationsSystemControlled() {
        return false
    }

    @Nonnull
    ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        return allChannels
    }

    @Nonnull
    String getNotificationChannelName(@Nonnull NotificationChannels.Channel channel) {
        return DEFAULT_NOTIFICATION_CHANNEL
    }

    @Nullable
    String getNotificationSummary(Context context, @Nonnull NotificationChannels.Channel channel) {
        return null
    }

    Boolean showSystemNotificationSettings(Context context, @Nullable Fragment fragment, @Nonnull NotificationChannels.Channel channel) {
        return false
    }

    Boolean useNotificationGroups() {
        return Build.VERSION.SDK_INT >= 24
    }
}
