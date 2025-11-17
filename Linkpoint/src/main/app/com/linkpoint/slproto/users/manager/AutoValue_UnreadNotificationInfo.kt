package com.linkpoint.slproto.users.manager

import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.ui.settings.NotificationType
import java.util.UUID
import androidx.annotation.NonNull

class AutoValue_UnreadNotificationInfo : UnreadNotificationInfo {
    private UUID agentUUID
    private Int freshMessagesCount
    private Optional<NotificationType> mostImportantFreshType
    private Optional<NotificationType> mostImportantType
    private UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo
    private Optional<UnreadNotificationInfo.UnreadMessageSource> singleFreshSource
    private Int totalUnreadCount
    private ImmutableList<UnreadNotificationInfo.UnreadMessageSource> unreadSources

    AutoValue_UnreadNotificationInfo(UUID uuid, Int i, ImmutableList<UnreadNotificationInfo.UnreadMessageSource> immutableList, Optional<NotificationType> optional, Int i2, Optional<NotificationType> optional2, Optional<UnreadNotificationInfo.UnreadMessageSource> optional3, UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification) {
        if (uuid == null) {
            throw NullPointerException("Null agentUUID")
        }
        this.agentUUID = uuid
        this.totalUnreadCount = i
        if (immutableList == null) {
            throw NullPointerException("Null unreadSources")
        }
        this.unreadSources = immutableList
        if (optional == null) {
            throw NullPointerException("Null mostImportantType")
        }
        this.mostImportantType = optional
        this.freshMessagesCount = i2
        if (optional2 == null) {
            throw NullPointerException("Null mostImportantFreshType")
        }
        this.mostImportantFreshType = optional2
        if (optional3 == null) {
            throw NullPointerException("Null singleFreshSource")
        }
        this.singleFreshSource = optional3
        if (objectPopupNotification == null) {
            throw NullPointerException("Null objectPopupInfo")
        }
        this.objectPopupInfo = objectPopupNotification
    }

    UUID agentUUID() {
        return this.agentUUID
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotificationInfo)) {
            return false
        }
        UnreadNotificationInfo unreadNotificationInfo = (UnreadNotificationInfo) obj
        if (!this.agentUUID.equals(unreadNotificationInfo.agentUUID()) || this.totalUnreadCount != unreadNotificationInfo.totalUnreadCount() || !this.unreadSources.equals(unreadNotificationInfo.unreadSources()) || !this.mostImportantType.equals(unreadNotificationInfo.mostImportantType()) || this.freshMessagesCount != unreadNotificationInfo.freshMessagesCount() || !this.mostImportantFreshType.equals(unreadNotificationInfo.mostImportantFreshType()) || !this.singleFreshSource.equals(unreadNotificationInfo.singleFreshSource())) {
            return false
        }
        return this.objectPopupInfo.equals(unreadNotificationInfo.objectPopupInfo())
    }

    Int freshMessagesCount() {
        return this.freshMessagesCount
    }

    Int hashCode() {
        return ((((((((((((((this.agentUUID.hashCode() ^ 1000003) * 1000003) ^ this.totalUnreadCount) * 1000003) ^ this.unreadSources.hashCode()) * 1000003) ^ this.mostImportantType.hashCode()) * 1000003) ^ this.freshMessagesCount) * 1000003) ^ this.mostImportantFreshType.hashCode()) * 1000003) ^ this.singleFreshSource.hashCode()) * 1000003) ^ this.objectPopupInfo.hashCode()
    }

    @NonNull
    Optional<NotificationType> mostImportantFreshType() {
        return this.mostImportantFreshType
    }

    @NonNull
    Optional<NotificationType> mostImportantType() {
        return this.mostImportantType
    }

    @NonNull
    UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo() {
        return this.objectPopupInfo
    }

    @NonNull
    Optional<UnreadNotificationInfo.UnreadMessageSource> singleFreshSource() {
        return this.singleFreshSource
    }

    String toString() {
        return "UnreadNotificationInfo{agentUUID=" + this.agentUUID + ", " + "totalUnreadCount=" + this.totalUnreadCount + ", " + "unreadSources=" + this.unreadSources + ", " + "mostImportantType=" + this.mostImportantType + ", " + "freshMessagesCount=" + this.freshMessagesCount + ", " + "mostImportantFreshType=" + this.mostImportantFreshType + ", " + "singleFreshSource=" + this.singleFreshSource + ", " + "objectPopupInfo=" + this.objectPopupInfo + "}"
    }

    Int totalUnreadCount() {
        return this.totalUnreadCount
    }

    @NonNull
    ImmutableList<UnreadNotificationInfo.UnreadMessageSource> unreadSources() {
        return this.unreadSources
    }
}
