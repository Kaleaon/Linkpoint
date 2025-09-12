// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules:
//            SLUserNameFetcher

class this._cls0
    implements Runnable
{

    final SLUserNameFetcher this$0;

    public void run()
    {
_L2:
        if (SLUserNameFetcher._2D_get2(SLUserNameFetcher.this))
        {
            break; /* Loop/switch isn't completed */
        }
        while (SLUserNameFetcher._2D_wrap0(SLUserNameFetcher.this)) ;
        SLUserNameFetcher._2D_get1(SLUserNameFetcher.this).lock();
        SLUserNameFetcher._2D_get0(SLUserNameFetcher.this).await();
        SLUserNameFetcher._2D_get1(SLUserNameFetcher.this).unlock();
        if (true) goto _L2; else goto _L1
        Exception exception;
        exception;
        try
        {
            SLUserNameFetcher._2D_get1(SLUserNameFetcher.this).unlock();
            throw exception;
        }
        catch (InterruptedException interruptedexception) { }
_L1:
    }

    A()
    {
        this$0 = SLUserNameFetcher.this;
        super();
    }
}
