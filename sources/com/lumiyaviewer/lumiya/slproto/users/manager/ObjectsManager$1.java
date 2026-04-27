// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.MultipleChatterNameRetriever;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ObjectsManager, UserManager

class this._cls0 extends SimpleRequestHandler
{

    final ObjectsManager this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        subscriptionsinglekey = ObjectsManager._2D_get8(ObjectsManager.this).getActiveAgentCircuit();
        if (subscriptionsinglekey != null)
        {
            subscriptionsinglekey.execute(ObjectsManager._2D_get7(ObjectsManager.this));
            return;
        } else
        {
            ObjectsManager._2D_get3(ObjectsManager.this).onResultError(SubscriptionSingleKey.Value, new com.lumiyaviewer.lumiya.slproto.NotConnectedException());
            return;
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    public void onRequestCancelled(SubscriptionSingleKey subscriptionsinglekey)
    {
        ObjectsManager._2D_get2(ObjectsManager.this).clearChatters();
    }

    public volatile void onRequestCancelled(Object obj)
    {
        onRequestCancelled((SubscriptionSingleKey)obj);
    }

    ver()
    {
        this$0 = ObjectsManager.this;
        super();
    }
}
