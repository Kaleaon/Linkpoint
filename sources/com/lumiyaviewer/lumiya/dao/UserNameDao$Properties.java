// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            UserNameDao

public static class 
{

    public static final Property DisplayName = new Property(2, java/lang/String, "displayName", false, "DISPLAY_NAME");
    public static final Property IsBadUUID;
    public static final Property UserName = new Property(1, java/lang/String, "userName", false, "USER_NAME");
    public static final Property Uuid = new Property(0, java/util/UUID, "uuid", true, "UUID");

    static 
    {
        IsBadUUID = new Property(3, Boolean.TYPE, "isBadUUID", false, "IS_BAD_UUID");
    }

    public ()
    {
    }
}
