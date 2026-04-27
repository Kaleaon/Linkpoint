// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLAvatarControl

class this._cls0 extends SimpleRequestHandler
{

    final SLAvatarControl this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        if (SLAvatarControl._2D_get1(SLAvatarControl.this) != null)
        {
            SLAvatarControl._2D_get1(SLAvatarControl.this).onResultData(subscriptionsinglekey, SLAvatarControl._2D_wrap0(SLAvatarControl.this));
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    ()
    {
        this$0 = SLAvatarControl.this;
        super();
    }
}
