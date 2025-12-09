package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableMap
import com.linkpoint.ui.settings.NotificationType
import java.util.UUID
import androidx.annotation.NonNull

class AutoValue_UnreadNotifications : UnreadNotifications {
    private UUID agentUUID
    private ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups

    AutoValue_UnreadNotifications(UUID uuid, ImmutableMap<NotificationType, UnreadNotificationInfo> immutableMap) {
        if (uuid == null) {
            throw NullPointerException("Null agentUUID")
        }
        this.agentUUID = uuid
        if (immutableMap == null) {
            throw NullPointerException("Null notificationGroups")
        }
        this.notificationGroups = immutableMap
    }

    @NonNull
    fun agentUUID(): UUID {
        return this.agentUUID
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotifications)) {
            return false
        }
        UnreadNotifications unreadNotifications = (UnreadNotifications) obj
        if (this.agentUUID.equals(unreadNotifications.agentUUID())) {
            return this.notificationGroups.equals(unreadNotifications.notificationGroups())
        }
        return false
    }

    fun hashCode(): Int {
        return ((this.agentUUID.hashCode() ^ 1000003) * 1000003) ^ this.notificationGroups.hashCode()
    }

    @NonNull
    fun notificationGroups(): ImmutableMap<NotificationType, UnreadNotificationInfo> {
        return this.notificationGroups
    }

    fun toString(): String {
        return "UnreadNotifications{agentUUID=" + this.agentUUID + ", " + "notificationGroups=" + this.notificationGroups + "}"
    }
}
