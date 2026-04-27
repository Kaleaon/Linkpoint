// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            CachedAssetDao

public static class 
{

    public static final Property Data = new Property(2, [B, "data", false, "DATA");
    public static final Property Key = new Property(0, java/lang/String, "key", true, "KEY");
    public static final Property MustRevalidate;
    public static final Property Status;

    static 
    {
        Status = new Property(1, Integer.TYPE, "status", false, "STATUS");
        MustRevalidate = new Property(3, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
    }

    public ()
    {
    }
}
