// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            FriendManager

class this._cls0 extends RequestFinalProcessor
{

    final FriendManager this$0;

    protected Boolean processRequest(UUID uuid)
    {
        uuid = (Friend)FriendManager._2D_get1(FriendManager.this).load(uuid);
        if (uuid != null)
        {
            return Boolean.valueOf(uuid.getIsOnline());
        } else
        {
            return Boolean.valueOf(false);
        }
    }

    protected volatile Object processRequest(Object obj)
        throws Throwable
    {
        return processRequest((UUID)obj);
    }

    (RequestSource requestsource, Executor executor)
    {
        this$0 = FriendManager.this;
        super(requestsource, executor);
    }
}
