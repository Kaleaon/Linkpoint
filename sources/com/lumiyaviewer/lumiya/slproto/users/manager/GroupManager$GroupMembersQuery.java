// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            GroupManager, AutoValue_GroupManager_GroupMembersQuery

public static abstract class bersQuery
{

    public static bersQuery create(UUID uuid, UUID uuid1)
    {
        return new AutoValue_GroupManager_GroupMembersQuery(uuid, uuid1);
    }

    public abstract UUID groupID();

    public abstract UUID requestID();

    public bersQuery()
    {
    }
}
