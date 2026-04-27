package com.lumiyaviewer.lumiya.ui.media

import android.net.Uri
import com.google.common.collect.ImmutableMap
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.ui.settings.NotificationType

enum class NotificationSounds(val resourceId: Int) {
    LocalChat(R.raw.lumiya_local_chat_message),
    IM(R.raw.lumiya_private_message),
    Group(R.raw.lumiya_group_message),
    

    fun getUri(): Uri {
        return getResourceUri(resourceId)
    }

    companion object {
        @JvmField
        val defaultSounds: ImmutableMap<NotificationType, NotificationSounds> = ImmutableMap.of(
            NotificationType.LocalChat, LocalChat,
            NotificationType.Private, IM,
            NotificationType.Group, Group,
        )

        fun getResourceUri(i: Int): Uri {
            return Uri.parse("android.resource://com.lumiyaviewer.lumiya/$i")
        }
    }
}