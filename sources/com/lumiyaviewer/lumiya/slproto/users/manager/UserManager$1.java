// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.dao.UserNameDao;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

class this._cls0 extends RequestProcessor
{

    final UserManager this$0;

    protected volatile boolean isRequestComplete(Object obj, Object obj1)
    {
        return isRequestComplete((UUID)obj, (UserName)obj1);
    }

    protected boolean isRequestComplete(UUID uuid, UserName username)
    {
        return username != null && (username.getIsBadUUID() || username.getDisplayName() != null && username.getUserName() != null);
    }

    protected UserName processRequest(UUID uuid)
    {
        return (UserName)UserManager._2D_get0(UserManager.this).getUserNameDao().load(uuid);
    }

    protected volatile Object processRequest(Object obj)
    {
        return processRequest((UUID)obj);
    }

    protected UserName processResult(UUID uuid, UserName username)
    {
        uuid = (UserName)UserManager._2D_get0(UserManager.this).getUserNameDao().load(uuid);
        if (uuid != null)
        {
            if (uuid.mergeWith(username))
            {
                UserManager._2D_get0(UserManager.this).getUserNameDao().update(uuid);
            }
            return uuid;
        } else
        {
            UserManager._2D_get0(UserManager.this).getUserNameDao().insertOrReplace(username);
            return username;
        }
    }

    protected volatile Object processResult(Object obj, Object obj1)
    {
        return processResult((UUID)obj, (UserName)obj1);
    }

    A(RequestSource requestsource, Executor executor)
    {
        this$0 = UserManager.this;
        super(requestsource, executor);
    }
}
