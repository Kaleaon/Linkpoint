// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ChatterID

public static final class notificationType extends Enum
{

    private static final Group $VALUES[];
    public static final Group Group;
    public static final Group Local;
    public static final Group User;
    public static final Group VALUES[] = values();
    private final NotificationType notificationType;

    public static notificationType valueOf(String s)
    {
        return (notificationType)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/users/ChatterID$ChatterType, s);
    }

    public static notificationType[] values()
    {
        return $VALUES;
    }

    public NotificationType getNotificationType()
    {
        return notificationType;
    }

    static 
    {
        Local = new <init>("Local", 0, NotificationType.LocalChat);
        User = new <init>("User", 1, NotificationType.Private);
        Group = new <init>("Group", 2, NotificationType.Group);
        $VALUES = (new .VALUES[] {
            Local, User, Group
        });
    }

    private A(String s, int i, NotificationType notificationtype)
    {
        super(s, i);
        notificationType = notificationtype;
    }
}
