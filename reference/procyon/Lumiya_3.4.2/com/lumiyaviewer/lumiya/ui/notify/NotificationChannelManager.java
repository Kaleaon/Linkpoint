// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.notify;

import android.support.v4.app.Fragment;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.google.common.collect.ImmutableSet;
import android.content.Context;

public interface NotificationChannelManager
{
    boolean areNotificationsSystemControlled();
    
    @Nonnull
    ImmutableSet<NotificationType> getEnabledTypes(final Context p0);
    
    @Nonnull
    String getNotificationChannelName(@Nonnull final NotificationChannels.Channel p0);
    
    @Nullable
    String getNotificationSummary(final Context p0, @Nonnull final NotificationChannels.Channel p1);
    
    boolean showSystemNotificationSettings(final Context p0, @Nullable final Fragment p1, @Nonnull final NotificationChannels.Channel p2);
    
    boolean useNotificationGroups();
}
