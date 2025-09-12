package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.UUID;
import javax.annotation.Nonnull;

final class AutoValue_UnreadNotifications extends UnreadNotifications {
    private final UUID agentUUID;
    private final ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups;

    AutoValue_UnreadNotifications(UUID uuid, ImmutableMap<NotificationType, UnreadNotificationInfo> immutableMap) {
        if (uuid == null) {
            throw new NullPointerException("Null agentUUID");
        }
        this.agentUUID = uuid;
        if (immutableMap == null) {
            throw new NullPointerException("Null notificationGroups");
        }
        this.notificationGroups = immutableMap;
    }

    @Nonnull
    public UUID agentUUID() {
        return this.agentUUID;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnreadNotifications)) {
            return false;
        }
        UnreadNotifications unreadNotifications = (UnreadNotifications) obj;
        if (this.agentUUID.equals(unreadNotifications.agentUUID())) {
            return this.notificationGroups.equals(unreadNotifications.notificationGroups());
        }
        return false;
    }

    public int hashCode() {
        return ((this.agentUUID.hashCode() ^ 1000003) * 1000003) ^ this.notificationGroups.hashCode();
    }

    @Nonnull
    public ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups() {
        return this.notificationGroups;
    }

    public String toString() {
        return "UnreadNotifications{agentUUID=" + this.agentUUID + ", " + "notificationGroups=" + this.notificationGroups + "}";
    }
}
