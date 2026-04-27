// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.google.common.collect.ImmutableSet;

public interface NotificationChannelManager
{

    public abstract boolean areNotificationsSystemControlled();

    public abstract ImmutableSet getEnabledTypes(Context context);

    public abstract String getNotificationChannelName(NotificationChannels.Channel channel);

    public abstract String getNotificationSummary(Context context, NotificationChannels.Channel channel);

    public abstract boolean showSystemNotificationSettings(Context context, Fragment fragment, NotificationChannels.Channel channel);

    public abstract boolean useNotificationGroups();
}
