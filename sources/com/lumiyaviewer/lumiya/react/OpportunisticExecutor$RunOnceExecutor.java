// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            OpportunisticExecutor

private class <init>
    implements Executor
{

    final OpportunisticExecutor this$0;

    public void execute(Runnable runnable)
    {
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
        OpportunisticExecutor._2D_get3(OpportunisticExecutor.this).add(runnable);
        OpportunisticExecutor._2D_get1(OpportunisticExecutor.this).signalAll();
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
        return;
        runnable;
        OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
        throw runnable;
    }

    private I()
    {
        this$0 = OpportunisticExecutor.this;
        super();
    }

    this._cls0(this._cls0 _pcls0)
    {
        this();
    }
}
