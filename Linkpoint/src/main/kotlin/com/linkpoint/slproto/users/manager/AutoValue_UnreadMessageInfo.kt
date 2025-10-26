package com.linkpoint.slproto.users.manager

import com.linkpoint.slproto.chat.generic.SLChatEvent
import javax.annotation.Nullable

final class AutoValue_UnreadMessageInfo : UnreadMessageInfo() {
    private val SLChatEvent lastMessage
    private val Int unreadCount

    AutoValue_UnreadMessageInfo(Int i, SLChatEvent sLChatEvent) {
        this.unreadCount = i
        this.lastMessage = sLChatEvent
    }

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadMessageInfo)) {
            return false
        }
        val unreadMessageInfo: UnreadMessageInfo = (UnreadMessageInfo) obj
        if (this.unreadCount == unreadMessageInfo.unreadCount()) {
            return this.lastMessage == null ? unreadMessageInfo.lastMessage() == null : this.lastMessage.equals(unreadMessageInfo.lastMessage())
        }
        return false
    }

     public fun hashCode(): Int {
        return (this.lastMessage == null ? 0 : this.lastMessage.hashCode()) ^ (1000003 * (this.unreadCount ^ 1000003))
    }

     public fun lastMessage(): SLChatEvent {
        return this.lastMessage
    }

     public fun toString(): String {
        return "UnreadMessageInfo{unreadCount=" + this.unreadCount + ", " + "lastMessage=" + this.lastMessage + "}"
    }

     public fun unreadCount(): Int {
        return this.unreadCount
    }
}
