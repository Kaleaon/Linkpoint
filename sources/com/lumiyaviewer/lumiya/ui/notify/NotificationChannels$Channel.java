// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.notify:
//            NotificationChannels

public static final class notificationId extends Enum
{

    private static final IM $VALUES[];
    public static final IM Group;
    public static final IM IM;
    public static final IM Local;
    public static final IM OnlineStatus;
    public final String channelId;
    public final int descriptionStringId;
    public final int nameStringId;
    public final int notificationId;
    public final NotificationType notificationType;

    public static notificationId valueOf(String s)
    {
        return (notificationId)Enum.valueOf(com/lumiyaviewer/lumiya/ui/notify/NotificationChannels$Channel, s);
    }

    public static notificationId[] values()
    {
        return $VALUES;
    }

    static 
    {
        OnlineStatus = new <init>("OnlineStatus", 0, "onlineStatus", 0x7f09020d, 0x7f09020c, null, 0x7f100022);
        Local = new <init>("Local", 1, "localChat", 0x7f09020b, 0x7f09020a, NotificationType.LocalChat, 0x7f10003f);
        Group = new <init>("Group", 2, "groupChat", 0x7f090207, 0x7f090206, NotificationType.Group, 0x7f10003c);
        IM = new <init>("IM", 3, "privateIM", 0x7f090209, 0x7f090208, NotificationType.Private, 0x7f10003e);
        $VALUES = (new .VALUES[] {
            OnlineStatus, Local, Group, IM
        });
    }

    private (String s, int i, String s1, int j, int k, NotificationType notificationtype, int l)
    {
        super(s, i);
        channelId = s1;
        nameStringId = j;
        descriptionStringId = k;
        notificationType = notificationtype;
        notificationId = l;
    }
}
