package com.lumiyaviewer.lumiya.slproto.users.manager;
import java.util.*;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import javax.annotation.Nonnull;

final class AutoValue_UnreadNotificationInfo_UnreadMessageSource extends UnreadNotificationInfo.UnreadMessageSource {
    private final ChatterID chatterID;
    private final Optional<String> chatterName;
    private final ImmutableList<SLChatEvent> unreadMessages;
    private final int unreadMessagesCount;

    AutoValue_UnreadNotificationInfo_UnreadMessageSource(ChatterID chatterID2, Optional<String> optional, ImmutableList<SLChatEvent> immutableList, int i) {
        if (chatterID2 == null) {
            throw new NullPointerException("Null chatterID");
        }
        this.chatterID = chatterID2;
        if (optional == null) {
            throw new NullPointerException("Null chatterName");
        }
        this.chatterName = optional;
        if (immutableList == null) {
            throw new NullPointerException("Null unreadMessages");
        }
        this.unreadMessages = immutableList;
        this.unreadMessagesCount = i;
    }

    @Nonnull
    public ChatterID chatterID() {
        return this.chatterID;
    }

    public Optional<String> chatterName() {
        return this.chatterName;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnreadNotificationInfo.UnreadMessageSource)) {
            return false;
        }
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = (UnreadNotificationInfo.UnreadMessageSource) obj;
        if (!this.chatterID.equals(unreadMessageSource.chatterID()) || !this.chatterName.equals(unreadMessageSource.chatterName()) || !this.unreadMessages.equals(unreadMessageSource.unreadMessages())) {
            return false;
        }
        return this.unreadMessagesCount == unreadMessageSource.unreadMessagesCount();
    }

    public int hashCode() {
        return ((((((this.chatterID.hashCode() ^ 1000003) * 1000003) ^ this.chatterName.hashCode()) * 1000003) ^ this.unreadMessages.hashCode()) * 1000003) ^ this.unreadMessagesCount;
    }

    public String toString() {
        return "UnreadMessageSource{chatterID=" + this.chatterID + ", " + "chatterName=" + this.chatterName + ", " + "unreadMessages=" + this.unreadMessages + ", " + "unreadMessagesCount=" + this.unreadMessagesCount + "}";
    }

    @Nonnull
    public ImmutableList<SLChatEvent> unreadMessages() {
        return this.unreadMessages;
    }

    public int unreadMessagesCount() {
        return this.unreadMessagesCount;
    }
}
