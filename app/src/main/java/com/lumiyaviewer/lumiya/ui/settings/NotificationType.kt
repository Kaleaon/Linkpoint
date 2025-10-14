package com.lumiyaviewer.lumiya.ui.settings
import java.util.*

import com.google.common.collect.ImmutableList

enum NotificationType {
    LocalChat(0, "notify_local_chat", "enableNotifyLocalChat", "soundOnLocalChat", "notifySoundLocalChat", "notifyLEDchatIMs", "notifyLEDColorChatIMs"),
    Private(2, "notify_private_im", "enableNotifyPrivateIM", "soundOnPrivateIM", "notifySoundPrivateIM", "notifyLEDprivateIMs", "notifyLEDColorPrivateIMs"),
    Group(1, "notify_group_messages", "enableNotifyGroupMessage", "soundOnGroupMessage", "notifySoundGroupMessage", "notifyLEDgroupMessages", "notifyLEDColorGroupMessages")
    
    ImmutableList<NotificationType> VALUES
    ImmutableList<NotificationType> VALUES_BY_DESCENDING_PRIORITY
    private String blinkColorKey
    private String blinkKey
    private String enableKey
    private String playSoundKey
    private String prefScreenKey
    private Int priority
    private String ringtoneKey

    {
        VALUES = ImmutableList.copyOf((E[]) values())
        VALUES_BY_DESCENDING_PRIORITY = ImmutableList.of(Private, Group, LocalChat)
    }

    private NotificationType(Int i, String str, String str2, String str3, String str4, String str5, String str6) {
        this.priority = i
        this.prefScreenKey = str
        this.enableKey = str2
        this.playSoundKey = str3
        this.ringtoneKey = str4
        this.blinkKey = str5
        this.blinkColorKey = str6
    }

    String getBlinkColorKey() {
        return this.blinkColorKey
    }

    String getBlinkKey() {
        return this.blinkKey
    }

    String getEnableKey() {
        return this.enableKey
    }

    String getPlaySoundKey() {
        return this.playSoundKey
    }

    String getPrefScreenKey() {
        return this.prefScreenKey
    }

    Int getPriority() {
        return this.priority
    }

    String getRingtoneKey() {
        return this.ringtoneKey
    }
}
