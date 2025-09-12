// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventory

class > extends SimpleRequestHandler
{

    final SLInventory this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        SLInventory._2D_wrap4(SLInventory.this);
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    ()
    {
        this$0 = SLInventory.this;
        super();
    }
}
