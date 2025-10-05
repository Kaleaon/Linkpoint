package com.linkpoint.slproto.users.manager

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.settings.NotificationType
import java.util.List
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class UnreadNotificationInfo {

    @JvmStatic
    abstract class ObjectPopupMessage {
        @JvmStatic
    ObjectPopupMessage create(String str, String str2) {
            return AutoValue_UnreadNotificationInfo_ObjectPopupMessage(Strings.nullToEmpty(str), Strings.nullToEmpty(str2))
        }

        public abstract String message()

        public abstract String objectName()
    }

    @JvmStatic
    abstract class ObjectPopupNotification {
        @JvmStatic
private ObjectPopupNotification empty = AutoValue_UnreadNotificationInfo_ObjectPopupNotification(0, 0, Optional.absent())

        @JvmStatic
    ObjectPopupNotification create(Int i, Int i2, ObjectPopupMessage objectPopupMessage) {
            return (i == 0 && i2 == 0 && objectPopupMessage == null) ? empty : AutoValue_UnreadNotificationInfo_ObjectPopupNotification(i, i2, Optional.fromNullable(objectPopupMessage))
        }

        public abstract Int freshObjectPopupsCount()

        public Boolean isEmpty() {
            return equals(empty)
        }

        public abstract Optional<ObjectPopupMessage> lastObjectPopup()

        public abstract Int objectPopupsCount()
    }

    @JvmStatic
    abstract class UnreadMessageSource {
        @JvmStatic
    UnreadMessageSource create(ChatterID chatterID, String str, List<SLChatEvent> list, Int i) {
            return AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID, Optional.fromNullable(str), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), i)
        }

        public abstract ChatterID chatterID()

        public abstract Optional<String> chatterName()

        public abstract ImmutableList<SLChatEvent> unreadMessages()

        public abstract Int unreadMessagesCount()

        public UnreadMessageSource withMessages(List<SLChatEvent> list) {
            return AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID(), chatterName(), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), unreadMessagesCount())
        }
    }

    @JvmStatic
    UnreadNotificationInfo create(UUID uuid, Int i, List<UnreadMessageSource> list, NotificationType notificationType, Int i2, NotificationType notificationType2, UnreadMessageSource unreadMessageSource, ObjectPopupNotification objectPopupNotification) {
        return AutoValue_UnreadNotificationInfo(uuid, i, list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), Optional.fromNullable(notificationType), i2, Optional.fromNullable(notificationType2), Optional.fromNullable(unreadMessageSource), objectPopupNotification)
    }

    public abstract UUID agentUUID()

    public abstract Int freshMessagesCount()

    public abstract Optional<NotificationType> mostImportantFreshType()

    public abstract Optional<NotificationType> mostImportantType()

    public abstract ObjectPopupNotification objectPopupInfo()

    public abstract Optional<UnreadMessageSource> singleFreshSource()

    public abstract Int totalUnreadCount()

    public abstract ImmutableList<UnreadMessageSource> unreadSources()
}
