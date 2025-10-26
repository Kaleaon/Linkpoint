package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableMap
import com.linkpoint.ui.settings.NotificationType
import java.util.UUID
import javax.annotation.Nonnull

final class AutoValue_UnreadNotifications : UnreadNotifications() {
    private val UUID agentUUID
    private val ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups

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

     public fun agentUUID(): UUID {
        return this.agentUUID
    }

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof UnreadNotifications)) {
            return false
        }
        val unreadNotifications: UnreadNotifications = (UnreadNotifications) obj
        if (this.agentUUID.equals(unreadNotifications.agentUUID())) {
            return this.notificationGroups.equals(unreadNotifications.notificationGroups())
        }
        return false
    }

     public fun hashCode(): Int {
        return ((this.agentUUID.hashCode() ^ 1000003) * 1000003) ^ this.notificationGroups.hashCode()
    }

    public ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups() {
        return this.notificationGroups
    }

     public fun toString(): String {
        return "UnreadNotifications{agentUUID=" + this.agentUUID + ", " + "notificationGroups=" + this.notificationGroups + "}"
    }
}
