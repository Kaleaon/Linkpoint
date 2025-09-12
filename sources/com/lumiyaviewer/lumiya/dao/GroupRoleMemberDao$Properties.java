// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.dao;

import de.greenrobot.dao.Property;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.dao:
//            GroupRoleMemberDao

public static class 
{

    public static final Property GroupID = new Property(0, java/util/UUID, "groupID", false, "GROUP_ID");
    public static final Property RequestID = new Property(1, java/util/UUID, "requestID", false, "REQUEST_ID");
    public static final Property RoleID = new Property(2, java/util/UUID, "roleID", false, "ROLE_ID");
    public static final Property UserID = new Property(3, java/util/UUID, "userID", false, "USER_ID");


    public ()
    {
    }
}
