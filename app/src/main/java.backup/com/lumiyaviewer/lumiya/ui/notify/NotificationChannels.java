package com.lumiyaviewer.lumiya.ui.notify;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NotificationChannels {

    /* renamed from: -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues = null;
    public static final String MESSAGE_NOTIFICATION_GROUP = "messageNotifications";
    private final NotificationChannelManager channelManager;

    public enum Channel {
        OnlineStatus("onlineStatus", R.string.notify_online_status_name, R.string.notify_online_status_desc, (int) null, R.id.online_notify_id),
        Local("localChat", R.string.notify_local_chat_name, R.string.notify_local_chat_desc, NotificationType.LocalChat, R.id.unread_notify_local_id),
        Group("groupChat", R.string.notify_group_chat_name, R.string.notify_group_chat_desc, NotificationType.Group, R.id.unread_notify_group_id),
        IM("privateIM", R.string.notify_im_name, R.string.notify_im_desc, NotificationType.Private, R.id.unread_notify_im_id);
        
        @Nonnull
        public final String channelId;
        public final int descriptionStringId;
        public final int nameStringId;
        public final int notificationId;
        @Nullable
        public final NotificationType notificationType;

        private Channel(String str, int i, @Nonnull int i2, NotificationType notificationType2, int i3) {
            this.channelId = str;
            this.nameStringId = i;
            this.descriptionStringId = i2;
            this.notificationType = notificationType2;
            this.notificationId = i3;
        }
    }

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final NotificationChannels Instance = new NotificationChannels((NotificationChannels) null);

        private InstanceHolder() {
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m666getcomlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues() {
        if (f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues != null) {
            return f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues;
        }
        int[] iArr = new int[NotificationType.values().length];
        try {
            iArr[NotificationType.Group.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[NotificationType.LocalChat.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[NotificationType.Private.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f464comlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues = iArr;
        return iArr;
    }

    private NotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            this.channelManager = new DummyNotificationChannelManager();
        } else {
            this.channelManager = new OreoNotificationChannelManager();
        }
    }

    /* synthetic */ NotificationChannels(NotificationChannels notificationChannels) {
        this();
    }

    public static NotificationChannels getInstance() {
        return InstanceHolder.Instance;
    }

    public boolean areNotificationsSystemControlled() {
        return this.channelManager.areNotificationsSystemControlled();
    }

    public Channel getChannelByType(@Nonnull NotificationType notificationType) {
        switch (m666getcomlumiyaviewerlumiyauisettingsNotificationTypeSwitchesValues()[notificationType.ordinal()]) {
            case 1:
                return Channel.Group;
            case 2:
                return Channel.Local;
            case 3:
                return Channel.IM;
            default:
                return null;
        }
    }

    public String getChannelName(@Nonnull Channel channel) {
        return this.channelManager.getNotificationChannelName(channel);
    }

    @Nonnull
    public ImmutableSet<NotificationType> getEnabledTypes(Context context) {
        return this.channelManager.getEnabledTypes(context);
    }

    @Nullable
    public String getNotificationSummary(Context context, @Nonnull Channel channel) {
        return this.channelManager.getNotificationSummary(context, channel);
    }

    public boolean showSystemNotificationSettings(Context context, @Nullable Fragment fragment, @Nonnull Channel channel) {
        return this.channelManager.showSystemNotificationSettings(context, fragment, channel);
    }

    public boolean useNotificationGroups() {
        return this.channelManager.useNotificationGroups();
    }
}
