package com.linkpoint.slproto.users.manager
import java.util.*

import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
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
    fun chatterID(): ChatterID {
        return this.chatterID
    }

    fun chatterName(): Optional<String> {
        return this.chatterName
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is UnreadNotificationInfo.UnreadMessageSource)) {
            return false
        }
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = (UnreadNotificationInfo.UnreadMessageSource) obj
        if (!this.chatterID.equals(unreadMessageSource.chatterID()) || !this.chatterName.equals(unreadMessageSource.chatterName()) || !this.unreadMessages.equals(unreadMessageSource.unreadMessages())) {
            return false
        }
        return this.unreadMessagesCount == unreadMessageSource.unreadMessagesCount()
    }

    fun hashCode(): Int {
        return ((((((this.chatterID.hashCode() ^ 1000003) * 1000003) ^ this.chatterName.hashCode()) * 1000003) ^ this.unreadMessages.hashCode()) * 1000003) ^ this.unreadMessagesCount
    }

    fun toString(): String {
        return "UnreadMessageSource{chatterID=" + this.chatterID + ", " + "chatterName=" + this.chatterName + ", " + "unreadMessages=" + this.unreadMessages + ", " + "unreadMessagesCount=" + this.unreadMessagesCount + "}"
    }

    @NonNull
    fun unreadMessages(): ImmutableList<SLChatEvent> {
        return this.unreadMessages
    }

    fun unreadMessagesCount(): Int {
        return this.unreadMessagesCount
    }
}
