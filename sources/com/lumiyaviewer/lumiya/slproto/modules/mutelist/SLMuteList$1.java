// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.mutelist:
//            SLMuteList

class this._cls0 extends SimpleRequestHandler
{

    final SLMuteList this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        if (SLMuteList._2D_get0(SLMuteList.this) != null)
        {
            SLMuteList._2D_get0(SLMuteList.this).onResultData(SubscriptionSingleKey.Value, getMuteList());
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    ()
    {
        this$0 = SLMuteList.this;
        super();
    }
}
