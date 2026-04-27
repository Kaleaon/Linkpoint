package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import javax.annotation.Nullable;

final class AutoValue_UnreadMessageInfo extends UnreadMessageInfo {
    private final SLChatEvent lastMessage;
    private final int unreadCount;

    AutoValue_UnreadMessageInfo(int i, @Nullable SLChatEvent sLChatEvent) {
        this.unreadCount = i;
        this.lastMessage = sLChatEvent;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnreadMessageInfo)) {
            return false;
        }
        UnreadMessageInfo unreadMessageInfo = (UnreadMessageInfo) obj;
        if (this.unreadCount == unreadMessageInfo.unreadCount()) {
            return this.lastMessage == null ? unreadMessageInfo.lastMessage() == null : this.lastMessage.equals(unreadMessageInfo.lastMessage());
        }
        return false;
    }

    public int hashCode() {
        return (this.lastMessage == null ? 0 : this.lastMessage.hashCode()) ^ (1000003 * (this.unreadCount ^ 1000003));
    }

    @Nullable
    public SLChatEvent lastMessage() {
        return this.lastMessage;
    }

    public String toString() {
        return "UnreadMessageInfo{unreadCount=" + this.unreadCount + ", " + "lastMessage=" + this.lastMessage + "}";
    }

    public int unreadCount() {
        return this.unreadCount;
    }
}
