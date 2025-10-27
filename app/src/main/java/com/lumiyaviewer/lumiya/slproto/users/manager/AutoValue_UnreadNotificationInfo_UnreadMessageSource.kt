package com.lumiyaviewer.lumiya.slproto.users.manager
import java.util.*

import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo
import androidx.annotation.NonNull

class AutoValue_UnreadNotificationInfo_UnreadMessageSource : UnreadNotificationInfo.UnreadMessageSource {
    private ChatterID chatterID
    private Optional<String> chatterName
    private ImmutableList<SLChatEvent> unreadMessages
    private Int unreadMessagesCount

    AutoValue_UnreadNotificationInfo_UnreadMessageSource(ChatterID chatterID2, Optional<String> optional, ImmutableList<SLChatEvent> immutableList, Int i) {
        if (chatterID2 == null) {
            throw NullPointerException("Null chatterID")
        }
        this.chatterID = chatterID2
        if (optional == null) {
            throw NullPointerException("Null chatterName")
        }
        this.chatterName = optional
        if (immutableList == null) {
            throw NullPointerException("Null unreadMessages")
        }
        this.unreadMessages = immutableList
        this.unreadMessagesCount = i
    }

    @NonNull
    ChatterID chatterID() {
        return this.chatterID
    }

    Optional<String> chatterName() {
        return this.chatterName
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotificationInfo.UnreadMessageSource)) {
            return false
        }
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = (UnreadNotificationInfo.UnreadMessageSource) obj
        if (!this.chatterID.equals(unreadMessageSource.chatterID()) || !this.chatterName.equals(unreadMessageSource.chatterName()) || !this.unreadMessages.equals(unreadMessageSource.unreadMessages())) {
            return false
        }
        return this.unreadMessagesCount == unreadMessageSource.unreadMessagesCount()
    }

    Int hashCode() {
        return ((((((this.chatterID.hashCode() ^ 1000003) * 1000003) ^ this.chatterName.hashCode()) * 1000003) ^ this.unreadMessages.hashCode()) * 1000003) ^ this.unreadMessagesCount
    }

    String toString() {
        return "UnreadMessageSource{chatterID=" + this.chatterID + ", " + "chatterName=" + this.chatterName + ", " + "unreadMessages=" + this.unreadMessages + ", " + "unreadMessagesCount=" + this.unreadMessagesCount + "}"
    }

    @NonNull
    ImmutableList<SLChatEvent> unreadMessages() {
        return this.unreadMessages
    }

    Int unreadMessagesCount() {
        return this.unreadMessagesCount
    }
}
