package com.lumiyaviewer.lumiya.ui.settings;

import com.google.common.collect.ImmutableList;

public enum NotificationType {
    LocalChat(0, "notify_local_chat", "enableNotifyLocalChat", "soundOnLocalChat", "notifySoundLocalChat", "notifyLEDchatIMs", "notifyLEDColorChatIMs"),
    Private(2, "notify_private_im", "enableNotifyPrivateIM", "soundOnPrivateIM", "notifySoundPrivateIM", "notifyLEDprivateIMs", "notifyLEDColorPrivateIMs"),
    Group(1, "notify_group_messages", "enableNotifyGroupMessage", "soundOnGroupMessage", "notifySoundGroupMessage", "notifyLEDgroupMessages", "notifyLEDColorGroupMessages");
    
    public static ImmutableList<NotificationType> VALUES;
    public static ImmutableList<NotificationType> VALUES_BY_DESCENDING_PRIORITY;
    private String blinkColorKey;
    private String blinkKey;
    private String enableKey;
    private String playSoundKey;
    private String prefScreenKey;
    private int priority;
    private String ringtoneKey;

    static {
        VALUES = ImmutableList.copyOf((E[]) values());
        VALUES_BY_DESCENDING_PRIORITY = ImmutableList.of(Private, Group, LocalChat);
    }

    private NotificationType(int i, String str, String str2, String str3, String str4, String str5, String str6) {
        this.priority = i;
        this.prefScreenKey = str;
        this.enableKey = str2;
        this.playSoundKey = str3;
        this.ringtoneKey = str4;
        this.blinkKey = str5;
        this.blinkColorKey = str6;
    }

    public String getBlinkColorKey() {
        return this.blinkColorKey;
    }

    public String getBlinkKey() {
        return this.blinkKey;
    }

    public String getEnableKey() {
        return this.enableKey;
    }

    public String getPlaySoundKey() {
        return this.playSoundKey;
    }

    public String getPrefScreenKey() {
        return this.prefScreenKey;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getRingtoneKey() {
        return this.ringtoneKey;
    }
}
