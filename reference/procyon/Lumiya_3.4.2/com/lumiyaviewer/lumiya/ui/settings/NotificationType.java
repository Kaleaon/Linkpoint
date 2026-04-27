// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import com.google.common.collect.ImmutableList;

public enum NotificationType
{
    Group("Group", 2, 1, "notify_group_messages", "enableNotifyGroupMessage", "soundOnGroupMessage", "notifySoundGroupMessage", "notifyLEDgroupMessages", "notifyLEDColorGroupMessages"), 
    LocalChat("LocalChat", 0, 0, "notify_local_chat", "enableNotifyLocalChat", "soundOnLocalChat", "notifySoundLocalChat", "notifyLEDchatIMs", "notifyLEDColorChatIMs"), 
    Private("Private", 1, 2, "notify_private_im", "enableNotifyPrivateIM", "soundOnPrivateIM", "notifySoundPrivateIM", "notifyLEDprivateIMs", "notifyLEDColorPrivateIMs");
    
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
        NotificationType.VALUES = ImmutableList.copyOf(values());
        NotificationType.VALUES_BY_DESCENDING_PRIORITY = ImmutableList.of(NotificationType.Private, NotificationType.Group, NotificationType.LocalChat);
    }
    
    private NotificationType(final String name, final int ordinal, final int priority, final String prefScreenKey, final String enableKey, final String playSoundKey, final String ringtoneKey, final String blinkKey, final String blinkColorKey) {
        this.priority = priority;
        this.prefScreenKey = prefScreenKey;
        this.enableKey = enableKey;
        this.playSoundKey = playSoundKey;
        this.ringtoneKey = ringtoneKey;
        this.blinkKey = blinkKey;
        this.blinkColorKey = blinkColorKey;
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
