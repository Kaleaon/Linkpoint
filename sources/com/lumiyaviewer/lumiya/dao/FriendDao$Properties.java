// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            FriendDao

public static class 
{

    public static final Property IsOnline;
    public static final Property RightsGiven;
    public static final Property RightsHas;
    public static final Property Uuid = new Property(0, java/util/UUID, "uuid", true, "UUID");

    static 
    {
        RightsGiven = new Property(1, Integer.TYPE, "rightsGiven", false, "RIGHTS_GIVEN");
        RightsHas = new Property(2, Integer.TYPE, "rightsHas", false, "RIGHTS_HAS");
        IsOnline = new Property(3, Boolean.TYPE, "isOnline", false, "IS_ONLINE");
    }

    public ()
    {
    }
}
