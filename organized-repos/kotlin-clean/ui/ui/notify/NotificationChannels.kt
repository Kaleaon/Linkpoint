package com.linkpoint.ui.notify

import android.content.Context
import android.os.Build
import android.support.v4.app.Fragment
import com.google.common.collect.ImmutableSet
import com.linkpoint.R
import com.linkpoint.ui.settings.NotificationType
import javax.annotation.Nonnull
import javax.annotation.Nullable

class NotificationChannels {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues = null
    const val MESSAGE_NOTIFICATION_GROUP: String = "messageNotifications"
    private val NotificationChannelManager channelManager

    enum class Channel {
        OnlineStatus("onlineStatus", R.string.notify_online_status_name, R.string.notify_online_status_desc, null.toInt(), R.id.online_notify_id),
        Local("localChat", R.string.notify_local_chat_name, R.string.notify_local_chat_desc, NotificationType.LocalChat, R.id.unread_notify_local_id),
        Group("groupChat", R.string.notify_group_chat_name, R.string.notify_group_chat_desc, NotificationType.Group, R.id.unread_notify_group_id),
        IM("privateIM", R.string.notify_im_name, R.string.notify_im_desc, NotificationType.Private, R.id.unread_notify_im_id)
        
        val String channelId
        val Int descriptionStringId
        val Int nameStringId
        val Int notificationId
        val NotificationType notificationType

        private Channel(String str, Int i, Int i2, NotificationType notificationType2, Int i3) {
            this.channelId = str
            this.nameStringId = i
            this.descriptionStringId = i2
            this.notificationType = notificationType2
            this.notificationId = i3
        }
    }

    @JvmStatic
private class InstanceHolder {
        /* access modifiers changed from: private */
        const val NotificationChannels Instance = NotificationChannels((NotificationChannels) null)

        private InstanceHolder() {
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m666getcomlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues() {
        if (f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues != null) {
            return f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues
        }
        IntArray iArr = Int[NotificationType.values().length]
        try {
            iArr[NotificationType.Group.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[NotificationType.LocalChat.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[NotificationType.Private.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues = iArr
        return iArr
    }

    private NotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            this.channelManager = DummyNotificationChannelManager()
        } else {
            this.channelManager = OreoNotificationChannelManager()
        }
    }

    /* synthetic */ NotificationChannels(NotificationChannels notificationChannels) {
        this()
    }

    @JvmStatic
    NotificationChannels getInstance() {
        return InstanceHolder.Instance
    }

    public Boolean areNotificationsSystemControlled() {
        return this.channelManager.areNotificationsSystemControlled()
    }

    public Channel getChannelByType(NotificationType notificationType) {
        switch (m666getcomlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues()[notificationType.ordinal()]) {
            case 1:
                return Channel.Group
            case 2:
                return Channel.Local
            case 3:
                return Channel.IM
            default:
                return null
        }
    }

    public String getChannelName(Channel channel) {
        return this.channelManager.getNotificationChannelName(channel)
    }

    public ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        return this.channelManager.getEnabledTypes(context)
    }

    public String getNotificationSummary(Context context, Channel channel) {
        return this.channelManager.getNotificationSummary(context, channel)
    }

    public Boolean showSystemNotificationSettings(Context context, Fragment fragment, Channel channel) {
        return this.channelManager.showSystemNotificationSettings(context, fragment, channel)
    }

    public Boolean useNotificationGroups() {
        return this.channelManager.useNotificationGroups()
    }
}
