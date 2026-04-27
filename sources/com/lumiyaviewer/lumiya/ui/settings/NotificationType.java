// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import com.google.common.collect.ImmutableList;

public final class NotificationType extends Enum
{

    private static final NotificationType $VALUES[];
    public static final NotificationType Group;
    public static final NotificationType LocalChat;
    public static final NotificationType Private;
    public static ImmutableList VALUES = ImmutableList.copyOf(values());
    public static ImmutableList VALUES_BY_DESCENDING_PRIORITY;
    private String blinkColorKey;
    private String blinkKey;
    private String enableKey;
    private String playSoundKey;
    private String prefScreenKey;
    private int priority;
    private String ringtoneKey;

    private NotificationType(String s, int i, int j, String s1, String s2, String s3, String s4, 
            String s5, String s6)
    {
        super(s, i);
        priority = j;
        prefScreenKey = s1;
        enableKey = s2;
        playSoundKey = s3;
        ringtoneKey = s4;
        blinkKey = s5;
        blinkColorKey = s6;
    }

    public static NotificationType valueOf(String s)
    {
        return (NotificationType)Enum.valueOf(com/lumiyaviewer/lumiya/ui/settings/NotificationType, s);
    }

    public static NotificationType[] values()
    {
        return $VALUES;
    }

    public String getBlinkColorKey()
    {
        return blinkColorKey;
    }

    public String getBlinkKey()
    {
        return blinkKey;
    }

    public String getEnableKey()
    {
        return enableKey;
    }

    public String getPlaySoundKey()
    {
        return playSoundKey;
    }

    public String getPrefScreenKey()
    {
        return prefScreenKey;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getRingtoneKey()
    {
        return ringtoneKey;
    }

    static 
    {
        LocalChat = new NotificationType("LocalChat", 0, 0, "notify_local_chat", "enableNotifyLocalChat", "soundOnLocalChat", "notifySoundLocalChat", "notifyLEDchatIMs", "notifyLEDColorChatIMs");
        Private = new NotificationType("Private", 1, 2, "notify_private_im", "enableNotifyPrivateIM", "soundOnPrivateIM", "notifySoundPrivateIM", "notifyLEDprivateIMs", "notifyLEDColorPrivateIMs");
        Group = new NotificationType("Group", 2, 1, "notify_group_messages", "enableNotifyGroupMessage", "soundOnGroupMessage", "notifySoundGroupMessage", "notifyLEDgroupMessages", "notifyLEDColorGroupMessages");
        $VALUES = (new NotificationType[] {
            LocalChat, Private, Group
        });
        VALUES_BY_DESCENDING_PRIORITY = ImmutableList.of(Private, Group, LocalChat);
    }
}
