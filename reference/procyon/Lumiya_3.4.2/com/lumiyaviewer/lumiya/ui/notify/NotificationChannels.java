// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.notify;

import android.support.v4.app.Fragment;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableSet;
import android.content.Context;
import javax.annotation.Nonnull;
import android.os.Build$VERSION;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

public class NotificationChannels
{
    public static final String MESSAGE_NOTIFICATION_GROUP = "messageNotifications";
    private final NotificationChannelManager channelManager;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues() {
        if (NotificationChannels.-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues != null) {
            return NotificationChannels.-com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues = new int[NotificationType.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues[NotificationType.Group.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues[NotificationType.LocalChat.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues[NotificationType.Private.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues = -com-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    private NotificationChannels() {
        if (Build$VERSION.SDK_INT < 26) {
            this.channelManager = new DummyNotificationChannelManager();
        }
        else {
            this.channelManager = new OreoNotificationChannelManager();
        }
    }
    
    public static NotificationChannels getInstance() {
        return InstanceHolder.Instance;
    }
    
    public boolean areNotificationsSystemControlled() {
        return this.channelManager.areNotificationsSystemControlled();
    }
    
    public Channel getChannelByType(@Nonnull final NotificationType notificationType) {
        switch (-getcom-lumiyaviewer-lumiya-ui-settings-NotificationTypeSwitchesValues()[notificationType.ordinal()]) {
            default: {
                return null;
            }
            case 2: {
                return Channel.Local;
            }
            case 3: {
                return Channel.IM;
            }
            case 1: {
                return Channel.Group;
            }
        }
    }
    
    public String getChannelName(@Nonnull final Channel channel) {
        return this.channelManager.getNotificationChannelName(channel);
    }
    
    @Nonnull
    public ImmutableSet<NotificationType> getEnabledTypes(final Context context) {
        return this.channelManager.getEnabledTypes(context);
    }
    
    @Nullable
    public String getNotificationSummary(final Context context, @Nonnull final Channel channel) {
        return this.channelManager.getNotificationSummary(context, channel);
    }
    
    public boolean showSystemNotificationSettings(final Context context, @Nullable final Fragment fragment, @Nonnull final Channel channel) {
        return this.channelManager.showSystemNotificationSettings(context, fragment, channel);
    }
    
    public boolean useNotificationGroups() {
        return this.channelManager.useNotificationGroups();
    }
    
    public enum Channel
    {
        Group("Group", 2, "groupChat", 2131296775, 2131296774, NotificationType.Group, 2131755068), 
        IM("IM", 3, "privateIM", 2131296777, 2131296776, NotificationType.Private, 2131755070), 
        Local("Local", 1, "localChat", 2131296779, 2131296778, NotificationType.LocalChat, 2131755071), 
        OnlineStatus("OnlineStatus", 0, "onlineStatus", 2131296781, 2131296780, (NotificationType)null, 2131755042);
        
        @Nonnull
        public final String channelId;
        public final int descriptionStringId;
        public final int nameStringId;
        public final int notificationId;
        @Nullable
        public final NotificationType notificationType;
        
        private Channel(final String name, final int ordinal, @Nonnull final String channelId, final int nameStringId, final int descriptionStringId, @Nullable final NotificationType notificationType, final int notificationId) {
            this.channelId = channelId;
            this.nameStringId = nameStringId;
            this.descriptionStringId = descriptionStringId;
            this.notificationType = notificationType;
            this.notificationId = notificationId;
        }
    }
    
    private static class InstanceHolder
    {
        private static final NotificationChannels Instance;
        
        static {
            Instance = new NotificationChannels(null);
        }
    }
}
