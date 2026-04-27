// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.lumiyaviewer.lumiya.ui.settings.NotificationType;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            AutoValue_UnreadNotifications, UnreadNotificationInfo

public abstract class UnreadNotifications
{

    public UnreadNotifications()
    {
    }

    public static UnreadNotifications create(UUID uuid, ImmutableMap immutablemap)
    {
        return new AutoValue_UnreadNotifications(uuid, immutablemap);
    }

    public abstract UUID agentUUID();

    public UnreadNotifications filter(ImmutableSet immutableset)
    {
        if (immutableset.containsAll(notificationGroups().keySet()))
        {
            return this;
        }
        com.google.common.collect.ImmutableMap.Builder builder = ImmutableMap.builder();
        Iterator iterator = notificationGroups().entrySet().iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if (immutableset.contains(entry.getKey()))
            {
                builder.put(entry);
            }
        } while (true);
        return create(agentUUID(), builder.build());
    }

    public UnreadNotificationInfo merge()
    {
        Object obj;
        Object obj1;
        Object obj4;
        Object obj5;
        Object obj7;
        ImmutableMap immutablemap;
        Iterator iterator;
        boolean flag;
        int i;
        int j;
        immutablemap = notificationGroups();
        if (immutablemap.isEmpty())
        {
            return UnreadNotificationInfo.create(agentUUID(), 0, null, null, 0, null, null, UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null));
        }
        if (immutablemap.size() == 1)
        {
            return (UnreadNotificationInfo)((java.util.Map.Entry)immutablemap.entrySet().iterator().next()).getValue();
        }
        obj7 = null;
        obj5 = null;
        obj4 = null;
        j = 0;
        iterator = NotificationType.VALUES_BY_DESCENDING_PRIORITY.iterator();
        obj = null;
        obj1 = null;
        i = 0;
        flag = false;
_L2:
        Object obj6;
        Object obj2;
        UnreadNotificationInfo unreadnotificationinfo;
label0:
        {
            if (!iterator.hasNext())
            {
                break; /* Loop/switch isn't completed */
            }
            unreadnotificationinfo = (UnreadNotificationInfo)immutablemap.get((NotificationType)iterator.next());
            if (unreadnotificationinfo == null)
            {
                break MISSING_BLOCK_LABEL_420;
            }
            i = unreadnotificationinfo.totalUnreadCount() + i;
            obj2 = obj1;
            if (!unreadnotificationinfo.unreadSources().isEmpty())
            {
                obj2 = obj1;
                if (obj1 == null)
                {
                    obj2 = ImmutableList.builder();
                }
                ((com.google.common.collect.ImmutableList.Builder) (obj2)).addAll(unreadnotificationinfo.unreadSources());
            }
            obj1 = (NotificationType)unreadnotificationinfo.mostImportantType().orNull();
            obj6 = obj7;
            if (obj1 == null)
            {
                break label0;
            }
            if (obj7 != null)
            {
                obj6 = obj7;
                if (((NotificationType) (obj1)).compareTo(((Enum) (obj7))) <= 0)
                {
                    break label0;
                }
            }
            obj6 = obj1;
        }
label1:
        {
            obj1 = (NotificationType)unreadnotificationinfo.mostImportantFreshType().orNull();
            obj7 = obj5;
            if (obj1 == null)
            {
                break label1;
            }
            if (obj5 != null)
            {
                obj7 = obj5;
                if (((NotificationType) (obj1)).compareTo(((Enum) (obj5))) <= 0)
                {
                    break label1;
                }
            }
            obj7 = obj1;
        }
        j += unreadnotificationinfo.freshMessagesCount();
        obj1 = (UnreadNotificationInfo.UnreadMessageSource)unreadnotificationinfo.singleFreshSource().orNull();
        if (obj1 != null)
        {
            if (obj4 != null || !(flag ^ true))
            {
                obj1 = null;
            }
            flag = true;
        } else
        {
            obj1 = obj4;
        }
        obj5 = unreadnotificationinfo.objectPopupInfo();
        if (!((UnreadNotificationInfo.ObjectPopupNotification) (obj5)).isEmpty())
        {
            obj4 = obj1;
            obj = obj2;
            obj1 = obj5;
            obj5 = obj7;
        } else
        {
            obj4 = obj2;
            obj2 = obj;
            obj = obj4;
            obj5 = obj7;
            obj4 = obj1;
            obj1 = obj2;
        }
_L3:
        obj2 = obj1;
        obj7 = obj6;
        obj1 = obj;
        obj = obj2;
        if (true) goto _L2; else goto _L1
_L1:
        obj2 = agentUUID();
        if (obj1 != null)
        {
            obj1 = ((com.google.common.collect.ImmutableList.Builder) (obj1)).build();
        } else
        {
            obj1 = null;
        }
        if (obj == null)
        {
            obj = UnreadNotificationInfo.ObjectPopupNotification.create(0, 0, null);
        }
        return UnreadNotificationInfo.create(((UUID) (obj2)), i, ((java.util.List) (obj1)), ((NotificationType) (obj7)), j, ((NotificationType) (obj5)), ((UnreadNotificationInfo.UnreadMessageSource) (obj4)), ((UnreadNotificationInfo.ObjectPopupNotification) (obj)));
        Object obj3 = obj1;
        obj1 = obj;
        obj = obj3;
        obj6 = obj7;
          goto _L3
    }

    public abstract ImmutableMap notificationGroups();
}
