package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
final class AutoValue_UnreadNotificationInfo_UnreadMessageSource extends UnreadNotificationInfo.UnreadMessageSource {
    private final ChatterID chatterID;
    private final Optional<String> chatterName;
    private final ImmutableList<SLChatEvent> unreadMessages;
    private final int unreadMessagesCount;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AutoValue_UnreadNotificationInfo_UnreadMessageSource(ChatterID chatterID, Optional<String> optional, ImmutableList<SLChatEvent> immutableList, int i) {
        if (chatterID == null) {
            throw new NullPointerException("Null chatterID");
        }
        this.chatterID = chatterID;
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

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource
    @Nonnull
    public ChatterID chatterID() {
        return this.chatterID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource
    public Optional<String> chatterName() {
        return this.chatterName;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof UnreadNotificationInfo.UnreadMessageSource) {
            UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = (UnreadNotificationInfo.UnreadMessageSource) obj;
            if (this.chatterID.equals(unreadMessageSource.chatterID()) && this.chatterName.equals(unreadMessageSource.chatterName()) && this.unreadMessages.equals(unreadMessageSource.unreadMessages())) {
                return this.unreadMessagesCount == unreadMessageSource.unreadMessagesCount();
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        return ((((((this.chatterID.hashCode() ^ 1000003) * 1000003) ^ this.chatterName.hashCode()) * 1000003) ^ this.unreadMessages.hashCode()) * 1000003) ^ this.unreadMessagesCount;
    }

    public String toString() {
        return "UnreadMessageSource{chatterID=" + this.chatterID + ", chatterName=" + this.chatterName + ", unreadMessages=" + this.unreadMessages + ", unreadMessagesCount=" + this.unreadMessagesCount + "}";
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource
    @Nonnull
    public ImmutableList<SLChatEvent> unreadMessages() {
        return this.unreadMessages;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo.UnreadMessageSource
    public int unreadMessagesCount() {
        return this.unreadMessagesCount;
    }
}
