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

    public Boolean equals(Object obj) {
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

    public Int hashCode() {
        return (this.lastMessage == null ? 0 : this.lastMessage.hashCode()) ^ (1000003 * (this.unreadCount ^ 1000003))
    }

    public SLChatEvent lastMessage() {
        return this.lastMessage
    }

    public String toString() {
        return "UnreadMessageInfo{unreadCount=" + this.unreadCount + ", " + "lastMessage=" + this.lastMessage + "}"
    }

    public Int unreadCount() {
        return this.unreadCount
    }
}
