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

    fun areNotificationsSystemControlled(): Boolean {
        return false
    }

    @NonNull
    fun getEnabledTypes(Context context): ImmutableSet<NotificationType> {
        return allChannels
    }

    @NonNull
    fun getNotificationChannelName(@NonNull NotificationChannels.Channel channel): String {
        return DEFAULT_NOTIFICATION_CHANNEL
    }

    @Nullable
    fun getNotificationSummary(Context context, @NonNull NotificationChannels.Channel channel): String {
        return null
    }

    fun showSystemNotificationSettings(Context context, @Nullable Fragment fragment, @NonNull NotificationChannels.Channel channel): Boolean {
        return false
    }

    fun useNotificationGroups(): Boolean {
        return Build.VERSION.SDK_INT >= 24
    }
}
