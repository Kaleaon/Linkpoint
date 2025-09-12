// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            UserDao

public static class 
{

    public static final Property BadUUID;
    public static final Property DisplayName = new Property(3, java/lang/String, "displayName", false, "DISPLAY_NAME");
    public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");
    public static final Property IsFriend;
    public static final Property RightsGiven;
    public static final Property RightsHas;
    public static final Property UserName = new Property(2, java/lang/String, "userName", false, "USER_NAME");
    public static final Property Uuid = new Property(1, java/util/UUID, "uuid", false, "UUID");

    static 
    {
        BadUUID = new Property(4, Boolean.TYPE, "badUUID", false, "BAD_UUID");
        IsFriend = new Property(5, Boolean.TYPE, "isFriend", false, "IS_FRIEND");
        RightsGiven = new Property(6, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
        RightsHas = new Property(7, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
    }

    public ()
    {
    }
}
