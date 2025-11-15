package com.lumiyaviewer.lumiya.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo
import com.lumiyaviewer.lumiya.ui.settings.NotificationType
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.UUID
import androidx.annotation.NonNull

abstract class UnreadNotifications {
    UnreadNotifications create(@NonNull UUID uuid, @NonNull ImmutableMap<NotificationType, UnreadNotificationInfo> immutableMap) {
        return AutoValue_UnreadNotifications(uuid, immutableMap)
    }

    @NonNull
    abstract UUID agentUUID()

    UnreadNotifications filter(@NonNull ImmutableSet<NotificationType> immutableSet) {
        if (immutableSet.containsAll(notificationGroups().keySet())) {
            return this
        }
        ImmutableMap.Builder builder = ImmutableMap.builder()
        for (Map.Entry entry : notificationGroups().entrySet()) {
            if (immutableSet.contains(entry.getKey())) {
                builder.put(entry)
            }
        }
        return create(agentUUID(), builder.build())
    }

    UnreadNotificationInfo merge() {
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification
        ImmutableList.Builder builder
        ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups = notificationGroups()
        if (notificationGroups.isEmpty()) {
            return UnreadNotificationInfo.create(agentUUID(), 0, (List<UnreadNotificationInfo.UnreadMessageSource>) null, (NotificationType) null, 0, (NotificationType) null, (UnreadNotificationInfo.UnreadMessageSource) null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null))
        }
        if (notificationGroups.size() == 1) {
            return (UnreadNotificationInfo) ((Map.Entry) notificationGroups.entrySet().iterator().next()).getValue()
        }
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification2 = null
        NotificationType notificationType = null
        NotificationType notificationType2 = null
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = null
        Int i2 = 0
        Int i3 = 0
        ImmutableList.Builder builder2 = null
        Boolean z = false
        Iterator<T> it = NotificationType.VALUES_BY_DESCENDING_PRIORITY.iterator()
        while (true) {
            objectPopupNotification = objectPopupNotification2
            builder = builder2
            i = i2
            Boolean z2 = z
            if (!it.hasNext()) {
                break
            }
            UnreadNotificationInfo unreadNotificationInfo = notificationGroups.get((NotificationType) it.next())
            if (unreadNotificationInfo != null) {
                Int i4 = unreadNotificationInfo.totalUnreadCount() + i
                if (!unreadNotificationInfo.unreadSources().isEmpty()) {
                    if (builder == null) {
                        builder = ImmutableList.builder()
                    }
                    builder.addAll((Iterable) unreadNotificationInfo.unreadSources())
                }
                NotificationType orNull = unreadNotificationInfo.mostImportantType().orNull()
                if (orNull != null && (notificationType == null || orNull.compareTo(notificationType) > 0)) {
                    notificationType = orNull
                }
                NotificationType orNull2 = unreadNotificationInfo.mostImportantFreshType().orNull()
                if (orNull2 != null && (notificationType2 == null || orNull2.compareTo(notificationType2) > 0)) {
                    notificationType2 = orNull2
                }
                i3 += unreadNotificationInfo.freshMessagesCount()
                UnreadNotificationInfo.UnreadMessageSource orNull3 = unreadNotificationInfo.singleFreshSource().orNull()
                if (orNull3 != null) {
                    if (unreadMessageSource != null || !(!z2)) {
                        orNull3 = null
                    }
                    z2 = true
                } else {
                    orNull3 = unreadMessageSource
                }
                UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo = unreadNotificationInfo.objectPopupInfo()
                if (!objectPopupInfo.isEmpty()) {
                    unreadMessageSource = orNull3
                    builder2 = builder
                    objectPopupNotification2 = objectPopupInfo
                    z = z2
                    i2 = i4
                } else {
                    z = z2
                    unreadMessageSource = orNull3
                    i2 = i4
                    builder2 = builder
                    objectPopupNotification2 = objectPopupNotification
                }
            } else {
                z = z2
                i2 = i
                builder2 = builder
                objectPopupNotification2 = objectPopupNotification
            }
        }
        return UnreadNotificationInfo.create(agentUUID(), i, builder != null ? builder.build() : null, notificationType, i3, notificationType2, unreadMessageSource, objectPopupNotification != null ? objectPopupNotification : UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null))
    }

    @NonNull
    abstract ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups()
}
