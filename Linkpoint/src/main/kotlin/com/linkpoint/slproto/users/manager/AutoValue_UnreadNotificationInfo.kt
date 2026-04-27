package com.linkpoint.slproto.users.manager

import com.google.common.base.Optional
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.ui.settings.NotificationType
import java.util.UUID
import javax.annotation.Nonnull

final class AutoValue_UnreadNotificationInfo : UnreadNotificationInfo() {
    private val UUID agentUUID
    private val Int freshMessagesCount
    private val Optional<NotificationType> mostImportantFreshType
    private val Optional<NotificationType> mostImportantType
    private val UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo
    private val Optional<UnreadNotificationInfo.UnreadMessageSource> singleFreshSource
    private val Int totalUnreadCount
    private val ImmutableList<UnreadNotificationInfo.UnreadMessageSource> unreadSources

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

     public fun agentUUID(): UUID {
        return this.agentUUID
    }

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotificationInfo)) {
            return false
        }
        val unreadNotificationInfo: UnreadNotificationInfo = (UnreadNotificationInfo) obj
        if (!this.agentUUID.equals(unreadNotificationInfo.agentUUID()) || this.totalUnreadCount != unreadNotificationInfo.totalUnreadCount() || !this.unreadSources.equals(unreadNotificationInfo.unreadSources()) || !this.mostImportantType.equals(unreadNotificationInfo.mostImportantType()) || this.freshMessagesCount != unreadNotificationInfo.freshMessagesCount() || !this.mostImportantFreshType.equals(unreadNotificationInfo.mostImportantFreshType()) || !this.singleFreshSource.equals(unreadNotificationInfo.singleFreshSource())) {
            return false
        }
        return this.objectPopupInfo.equals(unreadNotificationInfo.objectPopupInfo())
    }

     public fun freshMessagesCount(): Int {
        return this.freshMessagesCount
    }

     public override fun hashCode(): Int {
        return ((((((((((((((this.agentUUID.hashCode() ^ 1000003) * 1000003) ^ this.totalUnreadCount) * 1000003) ^ this.unreadSources.hashCode()) * 1000003) ^ this.mostImportantType.hashCode()) * 1000003) ^ this.freshMessagesCount) * 1000003) ^ this.mostImportantFreshType.hashCode()) * 1000003) ^ this.singleFreshSource.hashCode()) * 1000003) ^ this.objectPopupInfo.hashCode()
    }

    public Optional<NotificationType> mostImportantFreshType() {
        return this.mostImportantFreshType
    }

    public Optional<NotificationType> mostImportantType() {
        return this.mostImportantType
    }

    public UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo() {
        return this.objectPopupInfo
    }

    public Optional<UnreadNotificationInfo.UnreadMessageSource> singleFreshSource() {
        return this.singleFreshSource
    }

     public override fun toString(): String {
        return "UnreadNotificationInfo{agentUUID=" + this.agentUUID + ", " + "totalUnreadCount=" + this.totalUnreadCount + ", " + "unreadSources=" + this.unreadSources + ", " + "mostImportantType=" + this.mostImportantType + ", " + "freshMessagesCount=" + this.freshMessagesCount + ", " + "mostImportantFreshType=" + this.mostImportantFreshType + ", " + "singleFreshSource=" + this.singleFreshSource + ", " + "objectPopupInfo=" + this.objectPopupInfo + "}"
    }

     public fun totalUnreadCount(): Int {
        return this.totalUnreadCount
    }

    public ImmutableList<UnreadNotificationInfo.UnreadMessageSource> unreadSources() {
        return this.unreadSources
    }
}
