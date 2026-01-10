package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.chat.generic.SLChatEvent
import androidx.annotation.Nullable

class AutoValue_UnreadMessageInfo : UnreadMessageInfo {
    private SLChatEvent lastMessage
    private Int unreadCount

    AutoValue_UnreadMessageInfo(Int i, @Nullable SLChatEvent sLChatEvent) {
        this.unreadCount = i
        this.lastMessage = sLChatEvent
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is UnreadMessageInfo)) {
            return false
        }
        UnreadMessageInfo unreadMessageInfo = (UnreadMessageInfo) obj
        if (this.unreadCount == unreadMessageInfo.unreadCount()) {
            return this.lastMessage == null ? unreadMessageInfo.lastMessage() == null : this.lastMessage.equals(unreadMessageInfo.lastMessage())
        }
        return false
    }

    fun hashCode(): Int {
        return (this.lastMessage == null ? 0 : this.lastMessage.hashCode()) ^ (1000003 * (this.unreadCount ^ 1000003))
    }

    @Nullable
    fun lastMessage(): SLChatEvent {
        return this.lastMessage
    }

    fun toString(): String {
        return "UnreadMessageInfo{unreadCount=" + this.unreadCount + ", " + "lastMessage=" + this.lastMessage + "}"
    }

    fun unreadCount(): Int {
        return this.unreadCount
    }
}
