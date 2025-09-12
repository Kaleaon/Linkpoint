// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.ui.media.NotificationSounds;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.notify:
//            NotificationChannelManager, NotificationChannels

public class OreoNotificationChannelManager
    implements NotificationChannelManager
{
    private static class NotificationChannelSettings
    {

        final int importance;
        final NotificationType notificationType;
        final boolean showBadge;

        private NotificationChannelSettings(int i, boolean flag, NotificationType notificationtype)
        {
            importance = i;
            showBadge = flag;
            notificationType = notificationtype;
        }

        NotificationChannelSettings(int i, boolean flag, NotificationType notificationtype, NotificationChannelSettings notificationchannelsettings)
        {
            this(i, flag, notificationtype);
        }
    }


    private final ImmutableMap channelSettings;
    private final Map channels = new EnumMap(com/lumiyaviewer/lumiya/ui/notify/NotificationChannels$Channel);
    private final Object lock = new Object();

    public OreoNotificationChannelManager()
    {
        channelSettings = ImmutableMap.of(NotificationChannels.Channel.OnlineStatus, new NotificationChannelSettings(2, false, null, null), NotificationChannels.Channel.Local, new NotificationChannelSettings(3, true, NotificationType.LocalChat, null), NotificationChannels.Channel.Group, new NotificationChannelSettings(3, true, NotificationType.Group, null), NotificationChannels.Channel.IM, new NotificationChannelSettings(4, true, NotificationType.Private, null));
    }

    public boolean areNotificationsSystemControlled()
    {
        return true;
    }

    public ImmutableSet getEnabledTypes(Context context)
    {
        NotificationChannels notificationchannels = NotificationChannels.getInstance();
        context = (NotificationManager)context.getSystemService("notification");
        com.google.common.collect.ImmutableSet.Builder builder = ImmutableSet.builder();
        Iterator iterator = NotificationType.VALUES.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            NotificationType notificationtype = (NotificationType)iterator.next();
            NotificationChannel notificationchannel = context.getNotificationChannel(getNotificationChannelName(notificationchannels.getChannelByType(notificationtype)));
            if (notificationchannel != null && notificationchannel.getImportance() > 0)
            {
                builder.add(notificationtype);
            }
        } while (true);
        return builder.build();
    }

    public String getNotificationChannelName(NotificationChannels.Channel channel)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        NotificationChannel notificationchannel = (NotificationChannel)channels.get(channel);
        if (notificationchannel == null) goto _L2; else goto _L1
_L1:
        channel = notificationchannel.getId();
_L4:
        obj;
        JVM INSTR monitorexit ;
        return channel;
_L2:
        Context context = LumiyaApp.getContext();
        NotificationManager notificationmanager = (NotificationManager)context.getSystemService("notification");
        NotificationChannelSettings notificationchannelsettings = (NotificationChannelSettings)channelSettings.get(channel);
        NotificationChannel notificationchannel1 = new NotificationChannel(channel.channelId, context.getString(channel.nameStringId), notificationchannelsettings.importance);
        notificationchannel1.setDescription(context.getString(channel.descriptionStringId));
        if (notificationchannelsettings.notificationType != null)
        {
            android.media.AudioAttributes.Builder builder = new android.media.AudioAttributes.Builder();
            builder.setContentType(4);
            builder.setUsage(5);
            notificationchannel1.setSound(((NotificationSounds)NotificationSounds.defaultSounds.get(notificationchannelsettings.notificationType)).getUri(), builder.build());
        }
        notificationchannel1.setShowBadge(notificationchannelsettings.showBadge);
        Debug.Printf("Notifications: Creating new notification channel with id '%s'", new Object[] {
            channel.channelId
        });
        notificationmanager.createNotificationChannel(notificationchannel1);
        channels.put(channel, notificationchannel1);
        channel = channel.channelId;
        if (true) goto _L4; else goto _L3
_L3:
        channel;
        throw channel;
    }

    public String getNotificationSummary(Context context, NotificationChannels.Channel channel)
    {
        channel = ((NotificationManager)context.getSystemService("notification")).getNotificationChannel(getNotificationChannelName(channel));
        if (channel != null)
        {
            switch (channel.getImportance())
            {
            case 3: // '\003'
            default:
                return context.getString(0x7f090201);

            case 4: // '\004'
            case 5: // '\005'
                return context.getString(0x7f090203);

            case 2: // '\002'
                return context.getString(0x7f090204);

            case 1: // '\001'
                return context.getString(0x7f090205);

            case 0: // '\0'
                return context.getString(0x7f090202);
            }
        } else
        {
            return null;
        }
    }

    public boolean showSystemNotificationSettings(Context context, Fragment fragment, NotificationChannels.Channel channel)
    {
        Intent intent = new Intent("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.CHANNEL_ID", getNotificationChannelName(channel));
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        if (fragment != null)
        {
            fragment.startActivityForResult(intent, 11);
        } else
        {
            context.startActivity(intent);
        }
        return true;
    }

    public boolean useNotificationGroups()
    {
        return true;
    }
}
