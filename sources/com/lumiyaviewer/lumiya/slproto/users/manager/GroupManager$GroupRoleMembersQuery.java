// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            GroupManager, AutoValue_GroupManager_GroupRoleMembersQuery

public static abstract class bersQuery
{

    public static bersQuery create(UUID uuid, UUID uuid1, UUID uuid2)
    {
        return new AutoValue_GroupManager_GroupRoleMembersQuery(uuid, uuid1, uuid2);
    }

    public abstract UUID groupID();

    public abstract UUID requestID();

    public abstract UUID roleID();

    public bersQuery()
    {
    }
}
