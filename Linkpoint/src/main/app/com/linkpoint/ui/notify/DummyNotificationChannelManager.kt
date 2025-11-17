package com.linkpoint.ui.notify

import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import com.google.common.collect.ImmutableSet
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class DummyNotificationChannelManager : NotificationChannelManager {
    val DEFAULT_NOTIFICATION_CHANNEL: String = "miscellaneous"
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
