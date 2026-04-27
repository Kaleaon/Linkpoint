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
     fun create(str: String, str2: String): ObjectPopupMessage {
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
     fun create(i: Int, i2: Int, objectPopupMessage: ObjectPopupMessage): ObjectPopupNotification {
            return (i == 0 && i2 == 0 && objectPopupMessage == null) ? empty : AutoValue_UnreadNotificationInfo_ObjectPopupNotification(i, i2, Optional.fromNullable(objectPopupMessage))
        }

        public abstract Int freshObjectPopupsCount()

         public fun isEmpty(): Boolean {
            return equals(empty)
        }

        public abstract Optional<ObjectPopupMessage> lastObjectPopup()

        public abstract Int objectPopupsCount()
    }

    @JvmStatic
    abstract class UnreadMessageSource {
        @JvmStatic
     fun create(chatterID: ChatterID, str: String, list: List<SLChatEvent>, i: Int): UnreadMessageSource {
            return AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID, Optional.fromNullable(str), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), i)
        }

        public abstract ChatterID chatterID()

        public abstract Optional<String> chatterName()

        public abstract ImmutableList<SLChatEvent> unreadMessages()

        public abstract Int unreadMessagesCount()

         public fun withMessages(list: List<SLChatEvent>): UnreadMessageSource {
            return AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterID(), chatterName(), list != null ? ImmutableList.copyOf(list) : ImmutableList.of(), unreadMessagesCount())
        }
    }

    @JvmStatic
     fun create(uuid: UUID, i: Int, list: List<UnreadMessageSource>, notificationType: NotificationType, i2: Int, notificationType2: NotificationType, unreadMessageSource: UnreadMessageSource, objectPopupNotification: ObjectPopupNotification): UnreadNotificationInfo {
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
