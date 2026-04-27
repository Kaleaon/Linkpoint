package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class UnreadNotificationInfo {

    public static abstract class ObjectPopupMessage {
        public static ObjectPopupMessage create(@Nullable String str, @Nullable String str2) {
            return new AutoValue_UnreadNotificationInfo_ObjectPopupMessage(Strings.nullToEmpty(str), Strings.nullToEmpty(str2));
        }

        public abstract String message();

        public abstract String objectName();
    }

    public static abstract class ObjectPopupNotification {
        private static ObjectPopupNotification empty = new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(0, 0, Optional.absent());

        public static ObjectPopupNotification create(int i, int i2, @Nullable ObjectPopupMessage objectPopupMessage) {
            return (i == 0 && i2 == 0 && objectPopupMessage == null) ? empty : new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(i, i2, Optional.fromNullable(objectPopupMessage));
        }

        public abstract int freshObjectPopupsCount();

        public boolean isEmpty() {
            return equals(empty);
        }

        @Nonnull
        public abstract Optional<ObjectPopupMessage> lastObjectPopup();

        public abstract int objectPopupsCount();
    }

    public static abstract class UnreadMessageSource {
        public static UnreadMessageSource create(@Nonnull ChatterID chatterID, @Nullable String str, @Nullable List<SLChatEvent> list, int i) {
            return new AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID, Optional.fromNullable(str), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), i);
        }

        @Nonnull
        public abstract ChatterID chatterID();

        public abstract Optional<String> chatterName();

        @Nonnull
        public abstract ImmutableList<SLChatEvent> unreadMessages();

        public abstract int unreadMessagesCount();

        public UnreadMessageSource withMessages(@Nullable List<SLChatEvent> list) {
            return new AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID(), chatterName(), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), unreadMessagesCount());
        }
    }

    public static UnreadNotificationInfo create(@Nonnull UUID uuid, int i, @Nullable List<UnreadMessageSource> list, @Nullable NotificationType notificationType, int i2, @Nullable NotificationType notificationType2, @Nullable UnreadMessageSource unreadMessageSource, @Nonnull ObjectPopupNotification objectPopupNotification) {
        return new AutoValue_UnreadNotificationInfo(uuid, i, list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), Optional.fromNullable(notificationType), i2, Optional.fromNullable(notificationType2), Optional.fromNullable(unreadMessageSource), objectPopupNotification);
    }

    public abstract UUID agentUUID();

    public abstract int freshMessagesCount();

    @Nonnull
    public abstract Optional<NotificationType> mostImportantFreshType();

    @Nonnull
    public abstract Optional<NotificationType> mostImportantType();

    @Nonnull
    public abstract ObjectPopupNotification objectPopupInfo();

    @Nonnull
    public abstract Optional<UnreadMessageSource> singleFreshSource();

    public abstract int totalUnreadCount();

    @Nonnull
    public abstract ImmutableList<UnreadMessageSource> unreadSources();
}
