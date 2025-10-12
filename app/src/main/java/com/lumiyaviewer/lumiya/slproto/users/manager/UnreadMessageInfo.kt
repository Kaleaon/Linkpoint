package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent

data class UnreadMessageInfo(
    val unreadCount: Int,
    val lastMessage: SLChatEvent?
) {
    companion object {
        @JvmStatic
        fun create(unreadCount: Int, lastMessage: SLChatEvent?): UnreadMessageInfo {
            return UnreadMessageInfo(unreadCount, lastMessage)
        }
    }
}