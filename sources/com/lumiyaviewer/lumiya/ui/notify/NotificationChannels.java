// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import android.content.Context;
import android.support.v4.app.Fragment;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.notify:
//            DummyNotificationChannelManager, OreoNotificationChannelManager, NotificationChannelManager

public class NotificationChannels
{
    public static final class Channel extends Enum
    {

        private static final Channel $VALUES[];
        public static final Channel Group;
        public static final Channel IM;
        public static final Channel Local;
        public static final Channel OnlineStatus;
        public final String channelId;
        public final int descriptionStringId;
        public final int nameStringId;
        public final int notificationId;
        public final NotificationType notificationType;

        public static Channel valueOf(String s)
        {
            return (Channel)Enum.valueOf(com/lumiyaviewer/lumiya/ui/notify/NotificationChannels$Channel, s);
        }

        public static Channel[] values()
        {
            return $VALUES;
        }

        static 
        {
            OnlineStatus = new Channel("OnlineStatus", 0, "onlineStatus", 0x7f09020d, 0x7f09020c, null, 0x7f100022);
            Local = new Channel("Local", 1, "localChat", 0x7f09020b, 0x7f09020a, NotificationType.LocalChat, 0x7f10003f);
            Group = new Channel("Group", 2, "groupChat", 0x7f090207, 0x7f090206, NotificationType.Group, 0x7f10003c);
            IM = new Channel("IM", 3, "privateIM", 0x7f090209, 0x7f090208, NotificationType.Private, 0x7f10003e);
            $VALUES = (new Channel[] {
                OnlineStatus, Local, Group, IM
            });
        }

        private Channel(String s, int i, String s1, int j, int k, NotificationType notificationtype, int l)
        {
            super(s, i);
            channelId = s1;
            nameStringId = j;
            descriptionStringId = k;
            notificationType = notificationtype;
            notificationId = l;
        }
    }

    private static class InstanceHolder
    {

        private static final NotificationChannels Instance = new NotificationChannels(null);

        static NotificationChannels _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues[];
    public static final String MESSAGE_NOTIFICATION_GROUP = "messageNotifications";
    private final NotificationChannelManager channelManager;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues;
        }
        int ai[] = new int[NotificationType.values().length];
        try
        {
            ai[NotificationType.Group.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[NotificationType.LocalChat.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[NotificationType.Private.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues = ai;
        return ai;
    }

    private NotificationChannels()
    {
        if (android.os.Build.VERSION.SDK_INT < 26)
        {
            channelManager = new DummyNotificationChannelManager();
            return;
        } else
        {
            channelManager = new OreoNotificationChannelManager();
            return;
        }
    }

    NotificationChannels(NotificationChannels notificationchannels)
    {
        this();
    }

    public static NotificationChannels getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    public boolean areNotificationsSystemControlled()
    {
        return channelManager.areNotificationsSystemControlled();
    }

    public Channel getChannelByType(NotificationType notificationtype)
    {
        switch (_2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_ui_2D_settings_2D_NotificationTypeSwitchesValues()[notificationtype.ordinal()])
        {
        default:
            return null;

        case 2: // '\002'
            return Channel.Local;

        case 3: // '\003'
            return Channel.IM;

        case 1: // '\001'
            return Channel.Group;
        }
    }

    public String getChannelName(Channel channel)
    {
        return channelManager.getNotificationChannelName(channel);
    }

    public ImmutableSet getEnabledTypes(Context context)
    {
        return channelManager.getEnabledTypes(context);
    }

    public String getNotificationSummary(Context context, Channel channel)
    {
        return channelManager.getNotificationSummary(context, channel);
    }

    public boolean showSystemNotificationSettings(Context context, Fragment fragment, Channel channel)
    {
        return channelManager.showSystemNotificationSettings(context, fragment, channel);
    }

    public boolean useNotificationGroups()
    {
        return channelManager.useNotificationGroups();
    }
}
