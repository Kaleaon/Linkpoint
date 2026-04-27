// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;

final class AutoValue_UnreadNotificationInfo_UnreadMessageSource extends UnreadMessageSource
{
    private final ChatterID chatterID;
    private final Optional<String> chatterName;
    private final ImmutableList<SLChatEvent> unreadMessages;
    private final int unreadMessagesCount;
    
    AutoValue_UnreadNotificationInfo_UnreadMessageSource(final ChatterID chatterID, final Optional<String> chatterName, final ImmutableList<SLChatEvent> unreadMessages, final int unreadMessagesCount) {
        if (chatterID == null) {
            throw new NullPointerException("Null chatterID");
        }
        this.chatterID = chatterID;
        if (chatterName == null) {
            throw new NullPointerException("Null chatterName");
        }
        this.chatterName = chatterName;
        if (unreadMessages == null) {
            throw new NullPointerException("Null unreadMessages");
        }
        this.unreadMessages = unreadMessages;
        this.unreadMessagesCount = unreadMessagesCount;
    }
    
    @Nonnull
    @Override
    public ChatterID chatterID() {
        return this.chatterID;
    }
    
    @Override
    public Optional<String> chatterName() {
        return this.chatterName;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof UnreadMessageSource) {
            final UnreadMessageSource unreadMessageSource = (UnreadMessageSource)o;
            if (this.chatterID.equals(unreadMessageSource.chatterID()) && this.chatterName.equals(unreadMessageSource.chatterName()) && this.unreadMessages.equals(unreadMessageSource.unreadMessages())) {
                if (this.unreadMessagesCount != unreadMessageSource.unreadMessagesCount()) {
                    b = false;
                }
            }
            else {
                b = false;
            }
            return b;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (((this.chatterID.hashCode() ^ 0xF4243) * 1000003 ^ this.chatterName.hashCode()) * 1000003 ^ this.unreadMessages.hashCode()) * 1000003 ^ this.unreadMessagesCount;
    }
    
    @Override
    public String toString() {
        return "UnreadMessageSource{chatterID=" + this.chatterID + ", " + "chatterName=" + this.chatterName + ", " + "unreadMessages=" + this.unreadMessages + ", " + "unreadMessagesCount=" + this.unreadMessagesCount + "}";
    }
    
    @Nonnull
    @Override
    public ImmutableList<SLChatEvent> unreadMessages() {
        return this.unreadMessages;
    }
    
    @Override
    public int unreadMessagesCount() {
        return this.unreadMessagesCount;
    }
}
