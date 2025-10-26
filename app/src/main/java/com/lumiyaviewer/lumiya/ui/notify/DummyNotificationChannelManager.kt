package com.lumiyaviewer.lumiya.ui.notify

import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.google.common.collect.ImmutableSet
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class DummyNotificationChannelManager : NotificationChannelManager {
    String DEFAULT_NOTIFICATION_CHANNEL = "miscellaneous"
    private ImmutableSet<NotificationType> allChannels = ImmutableSet.of(NotificationType.LocalChat, NotificationType.Group, NotificationType.Private)

    Boolean areNotificationsSystemControlled() {
        return false
    }

    @NonNull
    ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        return allChannels
    }

    @NonNull
    String getNotificationChannelName(@NonNull NotificationChannels.Channel channel) {
        return DEFAULT_NOTIFICATION_CHANNEL
    }

    @Nullable
    String getNotificationSummary(Context context, @NonNull NotificationChannels.Channel channel) {
        return null
    }

    Boolean showSystemNotificationSettings(Context context, @Nullable Fragment fragment, @NonNull NotificationChannels.Channel channel) {
        return false
    }

    Boolean useNotificationGroups() {
        return Build.VERSION.SDK_INT >= 24
    }
}
