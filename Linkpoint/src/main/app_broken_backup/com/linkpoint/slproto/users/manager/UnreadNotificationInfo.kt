package com.linkpoint.slproto.users.manager

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.google.common.collect.ImmutableList
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.ui.settings.NotificationType
import java.util.List
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

abstract class UnreadNotificationInfo {

    abstract class ObjectPopupMessage {
        fun create(@Nullable String str, @Nullable String str2): ObjectPopupMessage {
            return AutoValue_UnreadNotificationInfo_ObjectPopupMessage(Strings.nullToEmpty(str), Strings.nullToEmpty(str2))
        }

        abstract String message()

        abstract String objectName()
    }

    abstract class ObjectPopupNotification {
        private ObjectPopupNotification empty = AutoValue_UnreadNotificationInfo_ObjectPopupNotification(0, 0, Optional.absent())

        fun create(Int i, Int i2, @Nullable ObjectPopupMessage objectPopupMessage): ObjectPopupNotification {
            return (i == 0 && i2 == 0 && objectPopupMessage == null) ? empty : AutoValue_UnreadNotificationInfo_ObjectPopupNotification(i, i2, Optional.fromNullable(objectPopupMessage))
        }

        abstract Int freshObjectPopupsCount()

        fun isEmpty(): Boolean {
            return equals(empty)
        }

        @NonNull
        abstract Optional<ObjectPopupMessage> lastObjectPopup()

        abstract Int objectPopupsCount()
    }

    abstract class UnreadMessageSource {
        fun create(@NonNull ChatterID chatterID, @Nullable String str, @Nullable List<SLChatEvent> list, Int i): UnreadMessageSource {
            return AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID, Optional.fromNullable(str), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), i)
        }

        @NonNull
        abstract ChatterID chatterID()

        abstract Optional<String> chatterName()

        @NonNull
        abstract ImmutableList<SLChatEvent> unreadMessages()

        abstract Int unreadMessagesCount()

        fun withMessages(@Nullable List<SLChatEvent> list): UnreadMessageSource {
            return AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID(), chatterName(), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), unreadMessagesCount())
        }
    }

    fun create(@NonNull UUID uuid, Int i, @Nullable List<UnreadMessageSource> list, @Nullable NotificationType notificationType, Int i2, @Nullable NotificationType notificationType2, @Nullable UnreadMessageSource unreadMessageSource, @NonNull ObjectPopupNotification objectPopupNotification): UnreadNotificationInfo {
        return AutoValue_UnreadNotificationInfo(uuid, i, list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), Optional.fromNullable(notificationType), i2, Optional.fromNullable(notificationType2), Optional.fromNullable(unreadMessageSource), objectPopupNotification)
    }

    abstract UUID agentUUID()

    abstract Int freshMessagesCount()

    @NonNull
    abstract Optional<NotificationType> mostImportantFreshType()

    @NonNull
    abstract Optional<NotificationType> mostImportantType()

    @NonNull
    abstract ObjectPopupNotification objectPopupInfo()

    @NonNull
    abstract Optional<UnreadMessageSource> singleFreshSource()

    abstract Int totalUnreadCount()

    @NonNull
    abstract ImmutableList<UnreadMessageSource> unreadSources()
}
