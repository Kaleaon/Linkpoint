// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLTaskInventories

class this._cls0 extends SimpleRequestHandler
{

    final SLTaskInventories this$0;

    public void onRequest(Integer integer)
    {
        SLTaskInventories._2D_wrap0(SLTaskInventories.this, integer.intValue());
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((Integer)obj);
    }

    A()
    {
        this$0 = SLTaskInventories.this;
        super();
    }
}
