// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class UnreadNotifications
{
    public static UnreadNotifications create(@Nonnull final UUID uuid, @Nonnull final ImmutableMap<NotificationType, UnreadNotificationInfo> immutableMap) {
        return new AutoValue_UnreadNotifications(uuid, immutableMap);
    }
    
    @Nonnull
    public abstract UUID agentUUID();
    
    public UnreadNotifications filter(@Nonnull final ImmutableSet<NotificationType> set) {
        if (set.containsAll(this.notificationGroups().keySet())) {
            return this;
        }
        final ImmutableMap.Builder<NotificationType, UnreadNotificationInfo> builder = ImmutableMap.builder();
        for (final Map.Entry<Object, ?> entry : this.notificationGroups().entrySet()) {
            if (set.contains(entry.getKey())) {
                builder.put((Map.Entry<? extends NotificationType, ? extends UnreadNotificationInfo>)entry);
            }
        }
        return create(this.agentUUID(), builder.build());
    }
    
    public UnreadNotificationInfo merge() {
        final ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups = this.notificationGroups();
        if (notificationGroups.isEmpty()) {
            return UnreadNotificationInfo.create(this.agentUUID(), 0, null, null, 0, null, null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
        }
        if (notificationGroups.size() == 1) {
            return (UnreadNotificationInfo)((Map.Entry)notificationGroups.entrySet().iterator().next()).getValue();
        }
        NotificationType o = null;
        NotificationType o2 = null;
        Object o3 = null;
        int n = 0;
        final Iterator<Object> iterator = NotificationType.VALUES_BY_DESCENDING_PRIORITY.iterator();
        UnreadNotificationInfo.ObjectPopupNotification create = null;
        ImmutableList.Builder<Object> builder = null;
        int n2 = 0;
        int n3 = 0;
        while (iterator.hasNext()) {
            final UnreadNotificationInfo unreadNotificationInfo = notificationGroups.get(iterator.next());
            NotificationType notificationType2;
            ImmutableList.Builder<Object> builder3;
            Object o5;
            if (unreadNotificationInfo != null) {
                n2 += unreadNotificationInfo.totalUnreadCount();
                ImmutableList.Builder<Object> builder2 = builder;
                if (!unreadNotificationInfo.unreadSources().isEmpty()) {
                    if ((builder2 = builder) == null) {
                        builder2 = ImmutableList.builder();
                    }
                    builder2.addAll(unreadNotificationInfo.unreadSources());
                }
                final NotificationType notificationType = unreadNotificationInfo.mostImportantType().orNull();
                notificationType2 = o;
                Label_0219: {
                    if (notificationType != null) {
                        if (o != null) {
                            notificationType2 = o;
                            if (notificationType.compareTo(o) <= 0) {
                                break Label_0219;
                            }
                        }
                        notificationType2 = notificationType;
                    }
                }
                final NotificationType notificationType3 = unreadNotificationInfo.mostImportantFreshType().orNull();
                NotificationType notificationType4 = o2;
                Label_0257: {
                    if (notificationType3 != null) {
                        if (o2 != null) {
                            notificationType4 = o2;
                            if (notificationType3.compareTo(o2) <= 0) {
                                break Label_0257;
                            }
                        }
                        notificationType4 = notificationType3;
                    }
                }
                n += unreadNotificationInfo.freshMessagesCount();
                Object o4 = unreadNotificationInfo.singleFreshSource().orNull();
                if (o4 != null) {
                    if (o3 != null || (n3 ^ 0x1) == 0x0) {
                        o4 = null;
                    }
                    n3 = 1;
                }
                else {
                    o4 = o3;
                }
                final UnreadNotificationInfo.ObjectPopupNotification objectPopupInfo = unreadNotificationInfo.objectPopupInfo();
                if (!objectPopupInfo.isEmpty()) {
                    o3 = o4;
                    builder3 = builder2;
                    o5 = objectPopupInfo;
                    o2 = notificationType4;
                }
                else {
                    final ImmutableList.Builder<Object> builder4 = builder2;
                    final UnreadNotificationInfo.ObjectPopupNotification objectPopupNotification = create;
                    builder3 = builder4;
                    o2 = notificationType4;
                    o3 = o4;
                    o5 = objectPopupNotification;
                }
            }
            else {
                final ImmutableList.Builder<Object> builder5 = builder;
                o5 = create;
                builder3 = builder5;
                notificationType2 = o;
            }
            final Object o6 = o5;
            o = notificationType2;
            builder = builder3;
            create = (UnreadNotificationInfo.ObjectPopupNotification)o6;
        }
        final UUID agentUUID = this.agentUUID();
        Object build;
        if (builder != null) {
            build = builder.build();
        }
        else {
            build = null;
        }
        if (create == null) {
            create = UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null);
        }
        return UnreadNotificationInfo.create(agentUUID, n2, (List<UnreadNotificationInfo.UnreadMessageSource>)build, o, n, o2, (UnreadNotificationInfo.UnreadMessageSource)o3, create);
    }
    
    @Nonnull
    public abstract ImmutableMap<NotificationType, UnreadNotificationInfo> notificationGroups();
}
