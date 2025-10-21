package com.linkpoint.slproto.users.manager
import java.util.*

import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import javax.annotation.Nonnull

final class AutoValue_UnreadNotificationInfo_UnreadMessageSource : UnreadNotificationInfo().UnreadMessageSource {
    private val ChatterID chatterID
    private val Optional<String> chatterName
    private val ImmutableList<SLChatEvent> unreadMessages
    private val Int unreadMessagesCount

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

    public ChatterID chatterID() {
        return this.chatterID
    }

    public Optional<String> chatterName() {
        return this.chatterName
    }

    public Boolean equals(Object obj) {
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

    public Int hashCode() {
        return ((((((this.chatterID.hashCode() ^ 1000003) * 1000003) ^ this.chatterName.hashCode()) * 1000003) ^ this.unreadMessages.hashCode()) * 1000003) ^ this.unreadMessagesCount
    }

    public String toString() {
        return "UnreadMessageSource{chatterID=" + this.chatterID + ", " + "chatterName=" + this.chatterName + ", " + "unreadMessages=" + this.unreadMessages + ", " + "unreadMessagesCount=" + this.unreadMessagesCount + "}"
    }

    public ImmutableList<SLChatEvent> unreadMessages() {
        return this.unreadMessages
    }

    public Int unreadMessagesCount() {
        return this.unreadMessagesCount
    }
}
