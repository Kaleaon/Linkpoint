package com.linkpoint.ui.notify

import android.content.Context
import android.support.v4.app.Fragment
import com.google.common.collect.ImmutableSet
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import javax.annotation.Nonnull
import javax.annotation.Nullable

interface NotificationChannelManager {
    Boolean areNotificationsSystemControlled()

    ImmutableSet<NotificationType> getEnabledTypes(Context context)

    String getNotificationChannelName(NotificationChannels.Channel channel)

    String getNotificationSummary(Context context, NotificationChannels.Channel channel)

    Boolean showSystemNotificationSettings(Context context, Fragment fragment, NotificationChannels.Channel channel)

    Boolean useNotificationGroups()
}
