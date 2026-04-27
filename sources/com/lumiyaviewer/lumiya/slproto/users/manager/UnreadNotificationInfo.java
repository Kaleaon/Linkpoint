// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.List;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            AutoValue_UnreadNotificationInfo, AutoValue_UnreadNotificationInfo_ObjectPopupMessage, AutoValue_UnreadNotificationInfo_ObjectPopupNotification, AutoValue_UnreadNotificationInfo_UnreadMessageSource

public abstract class UnreadNotificationInfo
{
    public static abstract class ObjectPopupMessage
    {

        public static ObjectPopupMessage create(String s, String s1)
        {
            return new AutoValue_UnreadNotificationInfo_ObjectPopupMessage(Strings.nullToEmpty(s), Strings.nullToEmpty(s1));
        }

        public abstract String message();

        public abstract String objectName();

        public ObjectPopupMessage()
        {
        }
    }

    public static abstract class ObjectPopupNotification
    {

        private static ObjectPopupNotification empty = new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(0, 0, Optional.absent());

        public static ObjectPopupNotification create(int i, int j, ObjectPopupMessage objectpopupmessage)
        {
            if (i == 0 && j == 0 && objectpopupmessage == null)
            {
                return empty;
            } else
            {
                return new AutoValue_UnreadNotificationInfo_ObjectPopupNotification(i, j, Optional.fromNullable(objectpopupmessage));
            }
        }

        public abstract int freshObjectPopupsCount();

        public boolean isEmpty()
        {
            return equals(empty);
        }

        public abstract Optional lastObjectPopup();

        public abstract int objectPopupsCount();


        public ObjectPopupNotification()
        {
        }
    }

    public static abstract class UnreadMessageSource
    {

        public static UnreadMessageSource create(ChatterID chatterid, String s, List list, int i)
        {
            Optional optional = Optional.fromNullable(s);
            if (list != null)
            {
                s = ImmutableList.copyOf(list);
            } else
            {
                s = ImmutableList.of();
            }
            return new AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterid, optional, s, i);
        }

        public abstract ChatterID chatterID();

        public abstract Optional chatterName();

        public abstract ImmutableList unreadMessages();

        public abstract int unreadMessagesCount();

        public UnreadMessageSource withMessages(List list)
        {
            ChatterID chatterid = chatterID();
            Optional optional = chatterName();
            if (list != null)
            {
                list = ImmutableList.copyOf(list);
            } else
            {
                list = ImmutableList.of();
            }
            return new AutoValue_UnreadNotificationInfo_UnreadMessageSource(chatterid, optional, list, unreadMessagesCount());
        }

        public UnreadMessageSource()
        {
        }
    }


    public UnreadNotificationInfo()
    {
    }

    public static UnreadNotificationInfo create(UUID uuid, int i, List list, NotificationType notificationtype, int j, NotificationType notificationtype1, UnreadMessageSource unreadmessagesource, ObjectPopupNotification objectpopupnotification)
    {
        if (list != null)
        {
            list = ImmutableList.copyOf(list);
        } else
        {
            list = ImmutableList.of();
        }
        return new AutoValue_UnreadNotificationInfo(uuid, i, list, Optional.fromNullable(notificationtype), j, Optional.fromNullable(notificationtype1), Optional.fromNullable(unreadmessagesource), objectpopupnotification);
    }

    public abstract UUID agentUUID();

    public abstract int freshMessagesCount();

    public abstract Optional mostImportantFreshType();

    public abstract Optional mostImportantType();

    public abstract ObjectPopupNotification objectPopupInfo();

    public abstract Optional singleFreshSource();

    public abstract int totalUnreadCount();

    public abstract ImmutableList unreadSources();
}
