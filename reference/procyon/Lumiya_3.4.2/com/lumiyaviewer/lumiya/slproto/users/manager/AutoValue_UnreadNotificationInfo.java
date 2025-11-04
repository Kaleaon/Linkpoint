// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.google.common.base.Optional;
import java.util.UUID;

final class AutoValue_UnreadNotificationInfo extends UnreadNotificationInfo
{
    private final UUID agentUUID;
    private final int freshMessagesCount;
    private final Optional<NotificationType> mostImportantFreshType;
    private final Optional<NotificationType> mostImportantType;
    private final ObjectPopupNotification objectPopupInfo;
    private final Optional<UnreadMessageSource> singleFreshSource;
    private final int totalUnreadCount;
    private final ImmutableList<UnreadMessageSource> unreadSources;
    
    AutoValue_UnreadNotificationInfo(final UUID agentUUID, final int totalUnreadCount, final ImmutableList<UnreadMessageSource> unreadSources, final Optional<NotificationType> mostImportantType, final int freshMessagesCount, final Optional<NotificationType> mostImportantFreshType, final Optional<UnreadMessageSource> singleFreshSource, final ObjectPopupNotification objectPopupInfo) {
        if (agentUUID == null) {
            throw new NullPointerException("Null agentUUID");
        }
        this.agentUUID = agentUUID;
        this.totalUnreadCount = totalUnreadCount;
        if (unreadSources == null) {
            throw new NullPointerException("Null unreadSources");
        }
        this.unreadSources = unreadSources;
        if (mostImportantType == null) {
            throw new NullPointerException("Null mostImportantType");
        }
        this.mostImportantType = mostImportantType;
        this.freshMessagesCount = freshMessagesCount;
        if (mostImportantFreshType == null) {
            throw new NullPointerException("Null mostImportantFreshType");
        }
        this.mostImportantFreshType = mostImportantFreshType;
        if (singleFreshSource == null) {
            throw new NullPointerException("Null singleFreshSource");
        }
        this.singleFreshSource = singleFreshSource;
        if (objectPopupInfo == null) {
            throw new NullPointerException("Null objectPopupInfo");
        }
        this.objectPopupInfo = objectPopupInfo;
    }
    
    @Override
    public UUID agentUUID() {
        return this.agentUUID;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = false;
        if (o == this) {
            return true;
        }
        if (o instanceof UnreadNotificationInfo) {
            final UnreadNotificationInfo unreadNotificationInfo = (UnreadNotificationInfo)o;
            boolean equals = b;
            if (this.agentUUID.equals(unreadNotificationInfo.agentUUID())) {
                equals = b;
                if (this.totalUnreadCount == unreadNotificationInfo.totalUnreadCount()) {
                    equals = b;
                    if (this.unreadSources.equals(unreadNotificationInfo.unreadSources())) {
                        equals = b;
                        if (this.mostImportantType.equals(unreadNotificationInfo.mostImportantType())) {
                            equals = b;
                            if (this.freshMessagesCount == unreadNotificationInfo.freshMessagesCount()) {
                                equals = b;
                                if (this.mostImportantFreshType.equals(unreadNotificationInfo.mostImportantFreshType())) {
                                    equals = b;
                                    if (this.singleFreshSource.equals(unreadNotificationInfo.singleFreshSource())) {
                                        equals = this.objectPopupInfo.equals(unreadNotificationInfo.objectPopupInfo());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return equals;
        }
        return false;
    }
    
    @Override
    public int freshMessagesCount() {
        return this.freshMessagesCount;
    }
    
    @Override
    public int hashCode() {
        return (((((((this.agentUUID.hashCode() ^ 0xF4243) * 1000003 ^ this.totalUnreadCount) * 1000003 ^ this.unreadSources.hashCode()) * 1000003 ^ this.mostImportantType.hashCode()) * 1000003 ^ this.freshMessagesCount) * 1000003 ^ this.mostImportantFreshType.hashCode()) * 1000003 ^ this.singleFreshSource.hashCode()) * 1000003 ^ this.objectPopupInfo.hashCode();
    }
    
    @Nonnull
    @Override
    public Optional<NotificationType> mostImportantFreshType() {
        return this.mostImportantFreshType;
    }
    
    @Nonnull
    @Override
    public Optional<NotificationType> mostImportantType() {
        return this.mostImportantType;
    }
    
    @Nonnull
    @Override
    public ObjectPopupNotification objectPopupInfo() {
        return this.objectPopupInfo;
    }
    
    @Nonnull
    @Override
    public Optional<UnreadMessageSource> singleFreshSource() {
        return this.singleFreshSource;
    }
    
    @Override
    public String toString() {
        return "UnreadNotificationInfo{agentUUID=" + this.agentUUID + ", " + "totalUnreadCount=" + this.totalUnreadCount + ", " + "unreadSources=" + this.unreadSources + ", " + "mostImportantType=" + this.mostImportantType + ", " + "freshMessagesCount=" + this.freshMessagesCount + ", " + "mostImportantFreshType=" + this.mostImportantFreshType + ", " + "singleFreshSource=" + this.singleFreshSource + ", " + "objectPopupInfo=" + this.objectPopupInfo + "}";
    }
    
    @Override
    public int totalUnreadCount() {
        return this.totalUnreadCount;
    }
    
    @Nonnull
    @Override
    public ImmutableList<UnreadMessageSource> unreadSources() {
        return this.unreadSources;
    }
}
