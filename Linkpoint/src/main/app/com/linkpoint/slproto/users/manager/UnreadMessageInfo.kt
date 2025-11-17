package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.chat.generic.SLChatEvent

data class UnreadMessageInfo(
    val unreadCount: Int,
    val lastMessage: SLChatEvent?,
) {
    companion object {
        @JvmStatic
        fun create(
            unreadCount: Int,
            lastMessage: SLChatEvent?,
        ): UnreadMessageInfo {
            return UnreadMessageInfo(unreadCount, lastMessage)
        }
    }
}
