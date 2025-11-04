// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.notify;

import android.content.Intent;
import android.support.v4.app.Fragment;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import android.media.AudioAttributes$Builder;
import com.lumiyaviewer.lumiya.LumiyaApp;
import javax.annotation.Nonnull;
import java.util.Iterator;
import android.app.NotificationManager;
import com.google.common.collect.ImmutableSet;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.EnumMap;
import android.app.NotificationChannel;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import android.support.annotation.RequiresApi;

@RequiresApi(api = 26)
public class OreoNotificationChannelManager implements NotificationChannelManager
{
    private final ImmutableMap<NotificationChannels.Channel, NotificationChannelSettings> channelSettings;
    private final Map<NotificationChannels.Channel, NotificationChannel> channels;
    private final Object lock;
    
    public OreoNotificationChannelManager() {
        this.lock = new Object();
        this.channels = new EnumMap<NotificationChannels.Channel, NotificationChannel>(NotificationChannels.Channel.class);
        this.channelSettings = ImmutableMap.of(NotificationChannels.Channel.OnlineStatus, new NotificationChannelSettings(2, false, null, null), NotificationChannels.Channel.Local, new NotificationChannelSettings(3, true, NotificationType.LocalChat, null), NotificationChannels.Channel.Group, new NotificationChannelSettings(3, true, NotificationType.Group, null), NotificationChannels.Channel.IM, new NotificationChannelSettings(4, true, NotificationType.Private, null));
    }
    
    @Override
    public boolean areNotificationsSystemControlled() {
        return true;
    }
    
    @Nonnull
    @Override
    public ImmutableSet<NotificationType> getEnabledTypes(final Context context) {
        final NotificationChannels instance = NotificationChannels.getInstance();
        final NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
        final ImmutableSet.Builder<NotificationType> builder = ImmutableSet.builder();
        for (final NotificationType notificationType : NotificationType.VALUES) {
            final NotificationChannel notificationChannel = notificationManager.getNotificationChannel(this.getNotificationChannelName(instance.getChannelByType(notificationType)));
            if (notificationChannel != null && notificationChannel.getImportance() > 0) {
                builder.add(notificationType);
            }
        }
        return builder.build();
    }
    
    @Nonnull
    @Override
    public String getNotificationChannelName(@Nonnull final NotificationChannels.Channel channel) {
        synchronized (this.lock) {
            final NotificationChannel notificationChannel = this.channels.get(channel);
            String s;
            if (notificationChannel != null) {
                s = notificationChannel.getId();
            }
            else {
                final Context context = LumiyaApp.getContext();
                final NotificationManager notificationManager = (NotificationManager)context.getSystemService("notification");
                final NotificationChannelSettings notificationChannelSettings = this.channelSettings.get(channel);
                final NotificationChannel notificationChannel2 = new NotificationChannel(channel.channelId, (CharSequence)context.getString(channel.nameStringId), notificationChannelSettings.importance);
                notificationChannel2.setDescription(context.getString(channel.descriptionStringId));
                if (notificationChannelSettings.notificationType != null) {
                    final AudioAttributes$Builder audioAttributes$Builder = new AudioAttributes$Builder();
                    audioAttributes$Builder.setContentType(4);
                    audioAttributes$Builder.setUsage(5);
                    notificationChannel2.setSound(NotificationSounds.defaultSounds.get(notificationChannelSettings.notificationType).getUri(), audioAttributes$Builder.build());
                }
                notificationChannel2.setShowBadge(notificationChannelSettings.showBadge);
                Debug.Printf("Notifications: Creating new notification channel with id '%s'", channel.channelId);
                notificationManager.createNotificationChannel(notificationChannel2);
                this.channels.put(channel, notificationChannel2);
                s = channel.channelId;
            }
            return s;
        }
    }
    
    @Nullable
    @Override
    public String getNotificationSummary(final Context context, @Nonnull final NotificationChannels.Channel channel) {
        final NotificationChannel notificationChannel = ((NotificationManager)context.getSystemService("notification")).getNotificationChannel(this.getNotificationChannelName(channel));
        if (notificationChannel == null) {
            return null;
        }
        switch (notificationChannel.getImportance()) {
            default: {
                return context.getString(2131296769);
            }
            case 4:
            case 5: {
                return context.getString(2131296771);
            }
            case 2: {
                return context.getString(2131296772);
            }
            case 1: {
                return context.getString(2131296773);
            }
            case 0: {
                return context.getString(2131296770);
            }
        }
    }
    
    @Override
    public boolean showSystemNotificationSettings(final Context context, @Nullable final Fragment fragment, @Nonnull final NotificationChannels.Channel channel) {
        final Intent intent = new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.CHANNEL_ID", this.getNotificationChannelName(channel));
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        if (fragment != null) {
            fragment.startActivityForResult(intent, 11);
        }
        else {
            context.startActivity(intent);
        }
        return true;
    }
    
    @Override
    public boolean useNotificationGroups() {
        return true;
    }
    
    private static class NotificationChannelSettings
    {
        final int importance;
        final NotificationType notificationType;
        final boolean showBadge;
        
        private NotificationChannelSettings(final int importance, final boolean showBadge, final NotificationType notificationType) {
            this.importance = importance;
            this.showBadge = showBadge;
            this.notificationType = notificationType;
        }
    }
}
