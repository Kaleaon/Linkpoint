// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.google.common.collect.ImmutableMap;
import java.util.UUID;

final class AutoValue_UnreadNotifications extends UnreadNotifications
{
    private final UUID agentUUID;
    private final ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups;
    
    AutoValue_UnreadNotifications(final UUID agentUUID, final ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups) {
        if (agentUUID == null) {
            throw new NullPointerException("Null agentUUID");
        }
        this.agentUUID = agentUUID;
        if (notificationGroups == null) {
            throw new NullPointerException("Null notificationGroups");
        }
        this.notificationGroups = notificationGroups;
    }
    
    @Nonnull
    @Override
    public UUID agentUUID() {
        return this.agentUUID;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = false;
        if (o == this) {
            return true;
        }
        if (o instanceof UnreadNotifications) {
            final UnreadNotifications unreadNotifications = (UnreadNotifications)o;
            if (this.agentUUID.equals(unreadNotifications.agentUUID())) {
                equals = this.notificationGroups.equals(unreadNotifications.notificationGroups());
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return (this.agentUUID.hashCode() ^ 0xF4243) * 1000003 ^ this.notificationGroups.hashCode();
    }
    
    @Nonnull
    @Override
    public ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups() {
        return this.notificationGroups;
    }
    
    @Override
    public String toString() {
        return "UnreadNotifications{agentUUID=" + this.agentUUID + ", " + "notificationGroups=" + this.notificationGroups + "}";
    }
}
