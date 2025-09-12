// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            SearchGridResultDao

public static class 
{

    public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
    public static final Property ItemName = new Property(4, java/lang/String, "itemName", false, "ITEM_NAME");
    public static final Property ItemType;
    public static final Property ItemUUID = new Property(3, java/util/UUID, "itemUUID", false, "ITEM_UUID");
    public static final Property LevensteinDistance;
    public static final Property MemberCount = new Property(6, java/lang/Integer, "memberCount", false, "MEMBER_COUNT");
    public static final Property SearchUUID = new Property(1, java/util/UUID, "searchUUID", false, "SEARCH_UUID");

    static 
    {
        ItemType = new Property(2, Integer.TYPE, "itemType", false, "ITEM_TYPE");
        LevensteinDistance = new Property(5, Integer.TYPE, "levensteinDistance", false, "LEVENSTEIN_DISTANCE");
    }

    public ()
    {
    }
}
