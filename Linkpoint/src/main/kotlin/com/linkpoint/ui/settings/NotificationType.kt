package com.linkpoint.ui.settings
import java.util.*

import com.google.common.collect.ImmutableList

enum class NotificationType {
    LocalChat(0, "notify_local_chat", "enableNotifyLocalChat", "soundOnLocalChat", "notifySoundLocalChat", "notifyLEDchatIMs", "notifyLEDColorChatIMs"),
    Private(2, "notify_private_im", "enableNotifyPrivateIM", "soundOnPrivateIM", "notifySoundPrivateIM", "notifyLEDprivateIMs", "notifyLEDColorPrivateIMs"),
    Group(1, "notify_group_messages", "enableNotifyGroupMessage", "soundOnGroupMessage", "notifySoundGroupMessage", "notifyLEDgroupMessages", "notifyLEDColorGroupMessages")
    
    @JvmStatic
    ImmutableList<NotificationType> VALUES
    @JvmStatic
    ImmutableList<NotificationType> VALUES_BY_DESCENDING_PRIORITY
    private String blinkColorKey
    private String blinkKey
    private String enableKey
    private String playSoundKey
    private String prefScreenKey
    private Int priority
    private String ringtoneKey

    static {
        VALUES = ImmutableList.copyOf((Array<E>) values())
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

     public fun getBlinkColorKey(): String {
        return this.blinkColorKey
    }

     public fun getBlinkKey(): String {
        return this.blinkKey
    }

     public fun getEnableKey(): String {
        return this.enableKey
    }

     public fun getPlaySoundKey(): String {
        return this.playSoundKey
    }

     public fun getPrefScreenKey(): String {
        return this.prefScreenKey
    }

     public fun getPriority(): Int {
        return this.priority
    }

     public fun getRingtoneKey(): String {
        return this.ringtoneKey
    }
}
