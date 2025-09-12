// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ObjectsManager, UserManager

class this._cls0 extends SimpleRequestHandler
{

    final ObjectsManager this$0;

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ObjectsManager$2_3438(SLAgentCircuit slagentcircuit, Integer integer)
    {
        slagentcircuit = slagentcircuit.getObjectProfile(integer.intValue());
        if (slagentcircuit != null)
        {
            ObjectsManager._2D_get4(ObjectsManager.this).onResultData(integer, slagentcircuit);
            return;
        } else
        {
            ObjectsManager._2D_get4(ObjectsManager.this).onResultError(integer, new jectDoesNotExistException(integer.intValue(), null));
            return;
        }
    }

    public void onRequest(Integer integer)
    {
        SLAgentCircuit slagentcircuit = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
        if (slagentcircuit != null)
        {
            slagentcircuit.execute(new sYOCADj00lseQFiZ3z4._cls1(this, slagentcircuit, integer));
            return;
        } else
        {
            ObjectsManager._2D_get4(ObjectsManager.this).onResultError(integer, new com.lumiyaviewer.lumiya.slproto.NotConnectedException());
            return;
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((Integer)obj);
    }

    ception()
    {
        this$0 = ObjectsManager.this;
        super();
    }
}
