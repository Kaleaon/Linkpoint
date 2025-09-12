package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.UUID;
import javax.annotation.Nonnull;

final class AutoValue_UnreadNotificationInfo extends UnreadNotificationInfo {
    private final UUID agentUUID;
    private final int freshMessagesCount;
    private final Optional<NotificationType> mostImportantFreshType;
    private final Optional<NotificationType> mostImportantType;
    private final UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo;
    private final Optional<UnreadNotificationInfo.UnreadMessageSource> singleFreshSource;
    private final int totalUnreadCount;
    private final ImmutableList<UnreadNotificationInfo.UnreadMessageSource> unreadSources;

    AutoValue_UnreadNotificationInfo(UUID uuid, int i, ImmutableList<UnreadNotificationInfo.UnreadMessageSource> immutableList, Optional<NotificationType> optional, int i2, Optional<NotificationType> optional2, Optional<UnreadNotificationInfo.UnreadMessageSource> optional3, UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification) {
        if (uuid == null) {
            throw new NullPointerException("Null agentUUID");
        }
        this.agentUUID = uuid;
        this.totalUnreadCount = i;
        if (immutableList == null) {
            throw new NullPointerException("Null unreadSources");
        }
        this.unreadSources = immutableList;
        if (optional == null) {
            throw new NullPointerException("Null mostImportantType");
        }
        this.mostImportantType = optional;
        this.freshMessagesCount = i2;
        if (optional2 == null) {
            throw new NullPointerException("Null mostImportantFreshType");
        }
        this.mostImportantFreshType = optional2;
        if (optional3 == null) {
            throw new NullPointerException("Null singleFreshSource");
        }
        this.singleFreshSource = optional3;
        if (objectPopupNotification == null) {
            throw new NullPointerException("Null objectPopupInfo");
        }
        this.objectPopupInfo = objectPopupNotification;
    }

    public UUID agentUUID() {
        return this.agentUUID;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UnreadNotificationInfo)) {
            return false;
        }
        UnreadNotificationInfo unreadNotificationInfo = (UnreadNotificationInfo) obj;
        if (!this.agentUUID.equals(unreadNotificationInfo.agentUUID()) || this.totalUnreadCount != unreadNotificationInfo.totalUnreadCount() || !this.unreadSources.equals(unreadNotificationInfo.unreadSources()) || !this.mostImportantType.equals(unreadNotificationInfo.mostImportantType()) || this.freshMessagesCount != unreadNotificationInfo.freshMessagesCount() || !this.mostImportantFreshType.equals(unreadNotificationInfo.mostImportantFreshType()) || !this.singleFreshSource.equals(unreadNotificationInfo.singleFreshSource())) {
            return false;
        }
        return this.objectPopupInfo.equals(unreadNotificationInfo.objectPopupInfo());
    }

    public int freshMessagesCount() {
        return this.freshMessagesCount;
    }

    public int hashCode() {
        return ((((((((((((((this.agentUUID.hashCode() ^ 1000003) * 1000003) ^ this.totalUnreadCount) * 1000003) ^ this.unreadSources.hashCode()) * 1000003) ^ this.mostImportantType.hashCode()) * 1000003) ^ this.freshMessagesCount) * 1000003) ^ this.mostImportantFreshType.hashCode()) * 1000003) ^ this.singleFreshSource.hashCode()) * 1000003) ^ this.objectPopupInfo.hashCode();
    }

    @Nonnull
    public Optional<NotificationType> mostImportantFreshType() {
        return this.mostImportantFreshType;
    }

    @Nonnull
    public Optional<NotificationType> mostImportantType() {
        return this.mostImportantType;
    }

    @Nonnull
    public UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo() {
        return this.objectPopupInfo;
    }

    @Nonnull
    public Optional<UnreadNotificationInfo.UnreadMessageSource> singleFreshSource() {
        return this.singleFreshSource;
    }

    public String toString() {
        return "UnreadNotificationInfo{agentUUID=" + this.agentUUID + ", " + "totalUnreadCount=" + this.totalUnreadCount + ", " + "unreadSources=" + this.unreadSources + ", " + "mostImportantType=" + this.mostImportantType + ", " + "freshMessagesCount=" + this.freshMessagesCount + ", " + "mostImportantFreshType=" + this.mostImportantFreshType + ", " + "singleFreshSource=" + this.singleFreshSource + ", " + "objectPopupInfo=" + this.objectPopupInfo + "}";
    }

    public int totalUnreadCount() {
        return this.totalUnreadCount;
    }

    @Nonnull
    public ImmutableList<UnreadNotificationInfo.UnreadMessageSource> unreadSources() {
        return this.unreadSources;
    }
}
