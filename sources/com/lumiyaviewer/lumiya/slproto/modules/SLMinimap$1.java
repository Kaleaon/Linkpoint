// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLMinimap

class init> extends SimpleRequestHandler
{

    final SLMinimap this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        if (SLMinimap._2D_get1(SLMinimap.this) != null)
        {
            SLMinimap._2D_get1(SLMinimap.this).onResultData(subscriptionsinglekey, new erLocations(SLMinimap._2D_get0(SLMinimap.this), SLMinimap._2D_wrap0(SLMinimap.this), SLMinimap._2D_get2(SLMinimap.this)));
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    erLocations()
    {
        this$0 = SLMinimap.this;
        super();
    }
}
