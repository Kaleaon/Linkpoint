package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.linkpoint.slproto.users.manager.UnreadNotificationInfo
import com.linkpoint.ui.settings.NotificationType
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.UUID
import javax.annotation.Nonnull

abstract class UnreadNotifications {
    @JvmStatic
     fun create(uuid: UUID, immutableMap: ImmutableMap<NotificationType, UnreadNotificationInfo>): UnreadNotifications {
        return AutoValue_UnreadNotifications(uuid, immutableMap)
    }

    public abstract UUID agentUUID()

     public fun filter(immutableSet: ImmutableSet<NotificationType>): UnreadNotifications {
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

     public fun merge(): UnreadNotificationInfo {
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification
        ImmutableList.Builder builder
        val notificationGroups: ImmutableMap<NotificationType, UnreadNotificationInfo> = notificationGroups()
        if (notificationGroups.isEmpty()) {
            return UnreadNotificationInfo.create(agentUUID(), 0, (List<UnreadNotificationInfo.UnreadMessageSource>) null, (NotificationType) null, 0, (NotificationType) null, (UnreadNotificationInfo.UnreadMessageSource) null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, (UnreadNotificationInfo.ObjectPopupMessage) null))
        }
        if (notificationGroups.size() == 1) {
            return (UnreadNotificationInfo) ((Map.Entry) notificationGroups.entrySet().iterator().next()).getValue()
        }
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification2 = null
        val notificationType: NotificationType = null
        val notificationType2: NotificationType = null
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource = null
        val i2: Int = 0
        val i3: Int = 0
        ImmutableList.Builder builder2 = null
        val z: Boolean = false
        val it: Iterator<T> = NotificationType.VALUES_BY_DESCENDING_PRIORITY.iterator()
        while (true) {
            objectPopupNotification = objectPopupNotification2
            builder = builder2
            i = i2
            val z2: Boolean = z
            if (!it.hasNext()) {
                break
            }
            val unreadNotificationInfo: UnreadNotificationInfo = notificationGroups.get((NotificationType) it.next())
            if (unreadNotificationInfo != null) {
                val i4: Int = unreadNotificationInfo.totalUnreadCount() + i
                if (!unreadNotificationInfo.unreadSources().isEmpty()) {
                    if (builder == null) {
                        builder = ImmutableList.builder()
                    }
                    builder.addAll((Iterable) unreadNotificationInfo.unreadSources())
                }
                val orNull: NotificationType = unreadNotificationInfo.mostImportantType().orNull()
                if (orNull != null && (notificationType == null || orNull.compareTo(notificationType) > 0)) {
                    notificationType = orNull
                }
                val orNull2: NotificationType = unreadNotificationInfo.mostImportantFreshType().orNull()
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

    public abstract ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups()
}
