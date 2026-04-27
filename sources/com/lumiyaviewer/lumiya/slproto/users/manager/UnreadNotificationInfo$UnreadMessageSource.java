// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotificationInfo, AutoValue_UnreadNotificationInfo_UnreadMessageSource

public static abstract class ageSource
{

    public static ageSource create(ChatterID chatterid, String s, List list, int i)
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

    public ageSource withMessages(List list)
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

    public ageSource()
    {
    }
}
