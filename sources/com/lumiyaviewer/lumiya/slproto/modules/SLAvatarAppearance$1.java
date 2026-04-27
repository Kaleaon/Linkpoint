// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.react.ResultHandler;
import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLAvatarAppearance

class this._cls0 extends SimpleRequestHandler
{

    final SLAvatarAppearance this$0;

    public void onRequest(SubscriptionSingleKey subscriptionsinglekey)
    {
        if (SLAvatarAppearance._2D_get0(SLAvatarAppearance.this) != null)
        {
            SLAvatarAppearance._2D_get0(SLAvatarAppearance.this).onResultData(subscriptionsinglekey, SLAvatarAppearance._2D_wrap0(SLAvatarAppearance.this));
        }
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((SubscriptionSingleKey)obj);
    }

    ()
    {
        this$0 = SLAvatarAppearance.this;
        super();
    }
}
