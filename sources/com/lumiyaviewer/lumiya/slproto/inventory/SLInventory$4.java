// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.react.RequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory

class this._cls0
    implements RequestHandler
{

    final SLInventory this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        SLInventory._2D_get2(SLInventory.this).set(true);
        SLInventory._2D_wrap2(SLInventory.this);
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    public void onRequestCancelled(SubscriptionSingleKey subscriptionsinglekey)
    {
        SLInventory._2D_get2(SLInventory.this).set(false);
        SLInventory._2D_wrap4(SLInventory.this);
    }

    public volatile void onRequestCancelled(Object obj)
    {
        onRequestCancelled((SubscriptionSingleKey)obj);
    }

    ()
    {
        this$0 = SLInventory.this;
        super();
    }
}
