// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.notify;

import android.os.Build$VERSION;
import android.support.v4.app.Fragment;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.google.common.collect.ImmutableSet;

public class DummyNotificationChannelManager implements NotificationChannelManager
{
    public static final String DEFAULT_NOTIFICATION_CHANNEL = "miscellaneous";
    private static final ImmutableSet<NotificationType> allChannels;
    
    static {
        allChannels = ImmutableSet.of(NotificationType.LocalChat, NotificationType.Group, NotificationType.Private);
    }
    
    @Override
    public boolean areNotificationsSystemControlled() {
        return false;
    }
    
    @Nonnull
    @Override
    public ImmutableSet<NotificationType> getEnabledTypes(final Context context) {
        return DummyNotificationChannelManager.allChannels;
    }
    
    @Nonnull
    @Override
    public String getNotificationChannelName(@Nonnull final NotificationChannels.Channel channel) {
        return "miscellaneous";
    }
    
    @Nullable
    @Override
    public String getNotificationSummary(final Context context, @Nonnull final NotificationChannels.Channel channel) {
        return null;
    }
    
    @Override
    public boolean showSystemNotificationSettings(final Context context, @Nullable final Fragment fragment, @Nonnull final NotificationChannels.Channel channel) {
        return false;
    }
    
    @Override
    public boolean useNotificationGroups() {
        return Build$VERSION.SDK_INT >= 24;
    }
}
