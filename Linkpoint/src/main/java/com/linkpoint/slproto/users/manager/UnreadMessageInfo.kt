package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.chat.generic.SLChatEvent

abstract class UnreadMessageInfo {
    abstract fun unreadCount(): Int
    abstract fun lastMessage(): SLChatEvent?

    companion object {
        @JvmStatic
        fun create(unreadCount: Int, lastMessage: SLChatEvent?): UnreadMessageInfo {
            return AutoValue_UnreadMessageInfo(unreadCount, lastMessage)
        }
    }
}