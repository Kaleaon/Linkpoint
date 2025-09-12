// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.GroupRoleMemberList;
import com.lumiyaviewer.lumiya.dao.GroupRoleMemberListDao;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            GroupManager

class this._cls0 extends RequestProcessor
{

    final GroupManager this$0;

    protected volatile boolean isRequestComplete(Object obj, Object obj1)
    {
        return isRequestComplete((UUID)obj, (UUID)obj1);
    }

    protected boolean isRequestComplete(UUID uuid, UUID uuid1)
    {
        uuid = (GroupRoleMemberList)GroupManager._2D_get6(GroupManager.this).load(uuid);
        if (uuid != null)
        {
            return uuid.getMustRevalidate() ^ true;
        } else
        {
            return false;
        }
    }

    protected volatile Object processRequest(Object obj)
    {
        return processRequest((UUID)obj);
    }

    protected UUID processRequest(UUID uuid)
    {
        uuid = (GroupRoleMemberList)GroupManager._2D_get6(GroupManager.this).load(uuid);
        if (uuid != null)
        {
            return uuid.getRequestID();
        } else
        {
            return null;
        }
    }

    protected volatile Object processResult(Object obj, Object obj1)
    {
        return processResult((UUID)obj, (UUID)obj1);
    }

    protected UUID processResult(UUID uuid, UUID uuid1)
    {
        uuid = new GroupRoleMemberList(uuid, uuid1, false);
        GroupManager._2D_get6(GroupManager.this).insertOrReplace(uuid);
        return uuid1;
    }

    (RequestSource requestsource, Executor executor)
    {
        this$0 = GroupManager.this;
        super(requestsource, executor);
    }
}
