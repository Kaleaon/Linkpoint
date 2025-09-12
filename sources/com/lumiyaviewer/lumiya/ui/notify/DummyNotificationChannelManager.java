// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.notify:
//            NotificationChannelManager

public class DummyNotificationChannelManager
    implements NotificationChannelManager
{

    public static final String DEFAULT_NOTIFICATION_CHANNEL = "miscellaneous";
    private static final ImmutableSet allChannels;

    public DummyNotificationChannelManager()
    {
    }

    public boolean areNotificationsSystemControlled()
    {
        return false;
    }

    public ImmutableSet getEnabledTypes(Context context)
    {
        return allChannels;
    }

    public String getNotificationChannelName(NotificationChannels.Channel channel)
    {
        return "miscellaneous";
    }

    public String getNotificationSummary(Context context, NotificationChannels.Channel channel)
    {
        return null;
    }

    public boolean showSystemNotificationSettings(Context context, Fragment fragment, NotificationChannels.Channel channel)
    {
        return false;
    }

    public boolean useNotificationGroups()
    {
        return android.os.Build.VERSION.SDK_INT >= 24;
    }

    static 
    {
        allChannels = ImmutableSet.of(NotificationType.LocalChat, NotificationType.Group, NotificationType.Private);
    }
}
