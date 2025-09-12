// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.notify;

import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.notify:
//            OreoNotificationChannelManager

private static class <init>
{

    final int importance;
    final NotificationType notificationType;
    final boolean showBadge;

    private (int i, boolean flag, NotificationType notificationtype)
    {
        importance = i;
        showBadge = flag;
        notificationType = notificationtype;
    }

    notificationType(int i, boolean flag, NotificationType notificationtype, notificationType notificationtype1)
    {
        this(i, flag, notificationtype);
    }
}
