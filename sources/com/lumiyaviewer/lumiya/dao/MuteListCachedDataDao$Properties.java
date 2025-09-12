// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            MuteListCachedDataDao

public static class 
{

    public static final Property CRC;
    public static final Property Data = new Property(2, [B, "data", false, "DATA");
    public static final Property Id = new Property(0, java/lang/Long, "id", true, "_id");

    static 
    {
        CRC = new Property(1, Integer.TYPE, "CRC", false, "CRC");
    }

    public ()
    {
    }
}
