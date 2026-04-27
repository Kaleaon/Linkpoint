// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            CachedResponseDao

public static class 
{

    public static final Property Data = new Property(1, [B, "data", false, "DATA");
    public static final Property Key = new Property(0, java/lang/String, "key", true, "KEY");
    public static final Property MustRevalidate;

    static 
    {
        MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
    }

    public ()
    {
    }
}
