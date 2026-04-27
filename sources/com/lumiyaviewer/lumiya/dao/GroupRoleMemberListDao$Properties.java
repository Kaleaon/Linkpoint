// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            GroupRoleMemberListDao

public static class 
{

    public static final Property GroupID = new Property(0, java/util/UUID, "groupID", true, "GROUP_ID");
    public static final Property MustRevalidate;
    public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");

    static 
    {
        MustRevalidate = new Property(2, Boolean.TYPE, "mustRevalidate", false, "MUST_REVALIDATE");
    }

    public ()
    {
    }
}
