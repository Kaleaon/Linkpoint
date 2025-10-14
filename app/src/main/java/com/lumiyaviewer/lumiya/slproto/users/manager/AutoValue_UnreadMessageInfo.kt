package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import javax.annotation.Nullable

class AutoValue_UnreadMessageInfo : UnreadMessageInfo {
    private SLChatEvent lastMessage
    private Int unreadCount

    AutoValue_UnreadMessageInfo(Int i, @Nullable SLChatEvent sLChatEvent) {
        this.unreadCount = i
        this.lastMessage = sLChatEvent
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadMessageInfo)) {
            return false
        }
        UnreadMessageInfo unreadMessageInfo = (UnreadMessageInfo) obj
        if (this.unreadCount == unreadMessageInfo.unreadCount()) {
            return this.lastMessage == null ? unreadMessageInfo.lastMessage() == null : this.lastMessage.equals(unreadMessageInfo.lastMessage())
        }
        return false
    }

    Int hashCode() {
        return (this.lastMessage == null ? 0 : this.lastMessage.hashCode()) ^ (1000003 * (this.unreadCount ^ 1000003))
    }

    @Nullable
    SLChatEvent lastMessage() {
        return this.lastMessage
    }

    String toString() {
        return "UnreadMessageInfo{unreadCount=" + this.unreadCount + ", " + "lastMessage=" + this.lastMessage + "}"
    }

    Int unreadCount() {
        return this.unreadCount
    }
}
