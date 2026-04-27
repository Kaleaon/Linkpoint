// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            ChatterDao

public static class 
{

    public static final Property Active;
    public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
    public static final Property LastMessageID = new Property(6, java/lang/Long, "lastMessageID", false, "LAST_MESSAGE_ID");
    public static final Property LastSessionID = new Property(7, java/util/UUID, "lastSessionID", false, "LAST_SESSION_ID");
    public static final Property Muted;
    public static final Property Type;
    public static final Property UnreadCount;
    public static final Property Uuid = new Property(2, java/util/UUID, "uuid", false, "UUID");

    static 
    {
        Type = new Property(1, Integer.TYPE, "type", false, "TYPE");
        Active = new Property(3, Boolean.TYPE, "active", false, "ACTIVE");
        Muted = new Property(4, Boolean.TYPE, "muted", false, "MUTED");
        UnreadCount = new Property(5, Integer.TYPE, "unreadCount", false, "UNREAD_COUNT");
    }

    public ()
    {
    }
}
