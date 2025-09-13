package com.lumiyaviewer.lumiya.ui.notify;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.ui.notify.NotificationChannels;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface NotificationChannelManager {
    boolean areNotificationsSystemControlled();

    @Nonnull
    ImmutableSet<NotificationType> getEnabledTypes(Context context);

    @Nonnull
    String getNotificationChannelName(@Nonnull NotificationChannels.Channel channel);

    @Nullable
    String getNotificationSummary(Context context, @Nonnull NotificationChannels.Channel channel);

    boolean showSystemNotificationSettings(Context context, @Nullable Fragment fragment, @Nonnull NotificationChannels.Channel channel);

    boolean useNotificationGroups();
}
