// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

final class AutoValue_UnreadMessageInfo extends UnreadMessageInfo
{
    private final SLChatEvent lastMessage;
    private final int unreadCount;
    
    AutoValue_UnreadMessageInfo(final int unreadCount, @Nullable final SLChatEvent lastMessage) {
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (o == this) {
            return true;
        }
        if (o instanceof UnreadMessageInfo) {
            final UnreadMessageInfo unreadMessageInfo = (UnreadMessageInfo)o;
            if (this.unreadCount == unreadMessageInfo.unreadCount()) {
                if (this.lastMessage == null) {
                    if (unreadMessageInfo.lastMessage() != null) {
                        equals = false;
                    }
                }
                else {
                    equals = this.lastMessage.equals(unreadMessageInfo.lastMessage());
                }
            }
            else {
                equals = false;
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        final int unreadCount = this.unreadCount;
        int hashCode;
        if (this.lastMessage == null) {
            hashCode = 0;
        }
        else {
            hashCode = this.lastMessage.hashCode();
        }
        return hashCode ^ 1000003 * (unreadCount ^ 0xF4243);
    }
    
    @Nullable
    @Override
    public SLChatEvent lastMessage() {
        return this.lastMessage;
    }
    
    @Override
    public String toString() {
        return "UnreadMessageInfo{unreadCount=" + this.unreadCount + ", " + "lastMessage=" + this.lastMessage + "}";
    }
    
    @Override
    public int unreadCount() {
        return this.unreadCount;
    }
}
