// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import android.os.Handler;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            UIThreadExecutor

class this._cls0
    implements Executor
{

    final UIThreadExecutor this$0;

    public void execute(Runnable runnable)
    {
        UIThreadExecutor._2D_get1(UIThreadExecutor.this).lock();
        UIThreadExecutor._2D_get2(UIThreadExecutor.this).add(runnable);
        if (!UIThreadExecutor._2D_get4(UIThreadExecutor.this).getAndSet(true))
        {
            UIThreadExecutor._2D_get0(UIThreadExecutor.this).post(UIThreadExecutor._2D_get3(UIThreadExecutor.this));
        }
        UIThreadExecutor._2D_get1(UIThreadExecutor.this).unlock();
        return;
        runnable;
        UIThreadExecutor._2D_get1(UIThreadExecutor.this).unlock();
        throw runnable;
    }

    ()
    {
        this$0 = UIThreadExecutor.this;
        super();
    }
}
