package com.lumiyaviewer.lumiya.ui.settings

import com.google.common.collect.ImmutableList

enum class NotificationType(
    val priority: Int,
    val prefScreenKey: String,
    val enableKey: String,
    val playSoundKey: String,
    private val ringtoneKeyValue: String,
    val blinkKey: String,
    val blinkColorKey: String,
) {
    LocalChat(0, "notify_local_chat", "enableNotifyLocalChat", "soundOnLocalChat", "notifySoundLocalChat", "notifyLEDchatIMs", "notifyLEDColorChatIMs"),
    Private(2, "notify_private_im", "enableNotifyPrivateIM", "soundOnPrivateIM", "notifySoundPrivateIM", "notifyLEDprivateIMs", "notifyLEDColorPrivateIMs"),
    Group(1, "notify_group_messages", "enableNotifyGroupMessage", "soundOnGroupMessage", "notifySoundGroupMessage", "notifyLEDgroupMessages", "notifyLEDColorGroupMessages");

    fun getRingtoneKey(): String = ringtoneKeyValue

    companion object {
        val VALUES: ImmutableList<NotificationType> = ImmutableList.copyOf(values())
        val VALUES_BY_DESCENDING_PRIORITY: ImmutableList<NotificationType> =
            ImmutableList.copyOf(listOf(Private, Group, LocalChat))
    }
}
