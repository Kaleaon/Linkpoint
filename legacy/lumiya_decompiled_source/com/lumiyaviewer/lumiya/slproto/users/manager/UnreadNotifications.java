package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public abstract class UnreadNotifications {
    public static UnreadNotifications create(@Nonnull UUID uuid, @Nonnull ImmutableMap<NotificationType, UnreadNotificationInfo> immutableMap) {
        return new AutoValue_UnreadNotifications(uuid, immutableMap);
    }

    @Nonnull
    public abstract UUID agentUUID();

    public UnreadNotifications filter(@Nonnull ImmutableSet<NotificationType> immutableSet) {
        if (immutableSet.containsAll(notificationGroups().keySet())) {
            return this;
        }
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<NotificationType, UnreadNotificationInfo> entry : notificationGroups().entrySet()) {
            if (immutableSet.contains(entry.getKey())) {
                builder.put(entry);
            }
        }
        return create(agentUUID(), builder.build());
    }

    public UnreadNotificationInfo merge() {
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification;
        ImmutableList.Builder builder;
        int i;
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource;
        ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups = notificationGroups();
        if (notificationGroups.isEmpty()) {
            return UnreadNotificationInfo.create(agentUUID(), 0, null, null, 0, null, null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
        }
        if (notificationGroups.size() == 1) {
            return notificationGroups.entrySet().iterator().next().getValue();
        }
        UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification2 = null;
        NotificationType notificationType = null;
        NotificationType notificationType2 = null;
        UnreadNotificationInfo.UnreadMessageSource unreadMessageSource2 = null;
        int i2 = 0;
        int i3 = 0;
        ImmutableList.Builder builder2 = null;
        boolean z = false;
        Iterator<NotificationType> it = NotificationType.VALUES_BY_DESCENDING_PRIORITY.iterator();
        while (true) {
            objectPopupNotification = objectPopupNotification2;
            builder = builder2;
            i = i2;
            boolean z2 = z;
            if (!it.hasNext()) {
                break;
            }
            UnreadNotificationInfo unreadNotificationInfo = notificationGroups.get(it.next());
            if (unreadNotificationInfo != null) {
                int i4 = unreadNotificationInfo.totalUnreadCount() + i;
                if (!unreadNotificationInfo.unreadSources().isEmpty()) {
                    if (builder == null) {
                        builder = ImmutableList.builder();
                    }
                    builder.addAll((Iterable) unreadNotificationInfo.unreadSources());
                }
                NotificationType orNull = unreadNotificationInfo.mostImportantType().orNull();
                if (orNull != null && (notificationType == null || orNull.compareTo(notificationType) > 0)) {
                    notificationType = orNull;
                }
                NotificationType orNull2 = unreadNotificationInfo.mostImportantFreshType().orNull();
                if (orNull2 != null && (notificationType2 == null || orNull2.compareTo(notificationType2) > 0)) {
                    notificationType2 = orNull2;
                }
                i3 += unreadNotificationInfo.freshMessagesCount();
                if (unreadNotificationInfo.singleFreshSource().orNull() != null) {
                    unreadMessageSource = (unreadMessageSource2 == null && (z2 ^ true)) ? null : null;
                    z2 = true;
                } else {
                    unreadMessageSource = unreadMessageSource2;
                }
                UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo = unreadNotificationInfo.objectPopupInfo();
                if (objectPopupInfo.isEmpty()) {
                    z = z2;
                    unreadMessageSource2 = unreadMessageSource;
                    i2 = i4;
                    builder2 = builder;
                    objectPopupNotification2 = objectPopupNotification;
                } else {
                    unreadMessageSource2 = unreadMessageSource;
                    builder2 = builder;
                    objectPopupNotification2 = objectPopupInfo;
                    z = z2;
                    i2 = i4;
                }
            } else {
                z = z2;
                i2 = i;
                builder2 = builder;
                objectPopupNotification2 = objectPopupNotification;
            }
        }
        return UnreadNotificationInfo.create(agentUUID(), i, builder != null ? builder.build() : null, notificationType, i3, notificationType2, unreadMessageSource2, objectPopupNotification != null ? objectPopupNotification : UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
    }

    @Nonnull
    public abstract ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups();
}
