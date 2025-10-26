package com.linkpoint.ui.notify

import android.content.Context
import android.support.v4.app.Fragment
import com.google.common.collect.ImmutableSet
import com.linkpoint.ui.notify.NotificationChannels
import com.linkpoint.ui.settings.NotificationType
import javax.annotation.Nonnull
import javax.annotation.Nullable

interface NotificationChannelManager {
     fun areNotificationsSystemControlled(): Boolean)

    ImmutableSet<NotificationType> getEnabledTypes(Context context)

     fun getNotificationChannelName(NotificationChannels.Channel channel): String)

     fun getNotificationSummary(context: Context, NotificationChannels.Channel channel): String)

     fun showSystemNotificationSettings(context: Context, fragment: Fragment, NotificationChannels.Channel channel): Boolean)

     fun useNotificationGroups(): Boolean)
}
