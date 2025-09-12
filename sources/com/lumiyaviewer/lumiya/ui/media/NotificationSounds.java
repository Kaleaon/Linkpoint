// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.media;

import android.net.Uri;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;

public final class NotificationSounds extends Enum
{

    private static final NotificationSounds $VALUES[];
    public static final NotificationSounds Group;
    public static final NotificationSounds IM;
    public static final NotificationSounds LocalChat;
    public static final ImmutableMap defaultSounds;
    private final int resourceId;

    private NotificationSounds(String s, int i, int j)
    {
        super(s, i);
        resourceId = j;
    }

    public static Uri getResourceUri(int i)
    {
        return Uri.parse((new StringBuilder()).append("android.resource://com.lumiyaviewer.lumiya/").append(i).toString());
    }

    public static NotificationSounds valueOf(String s)
    {
        return (NotificationSounds)Enum.valueOf(com/lumiyaviewer/lumiya/ui/media/NotificationSounds, s);
    }

    public static NotificationSounds[] values()
    {
        return $VALUES;
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public Uri getUri()
    {
        return getResourceUri(resourceId);
    }

    static 
    {
        LocalChat = new NotificationSounds("LocalChat", 0, 0x7f080001);
        IM = new NotificationSounds("IM", 1, 0x7f080002);
        Group = new NotificationSounds("Group", 2, 0x7f080000);
        $VALUES = (new NotificationSounds[] {
            LocalChat, IM, Group
        });
        defaultSounds = ImmutableMap.of(NotificationType.LocalChat, LocalChat, NotificationType.Private, IM, NotificationType.Group, Group);
    }
}
