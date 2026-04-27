// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.eventbus;


// Referenced classes of package com.lumiyaviewer.lumiya.eventbus:
//            EventBus

public abstract class EventRateLimiter
{

    private final EventBus bus;
    private volatile boolean isPending;
    private volatile long lastTimeFired;
    private final Object lock = new Object();
    private final long minInterval;

    protected EventRateLimiter(EventBus eventbus, long l)
    {
        lastTimeFired = 0L;
        isPending = false;
        bus = eventbus;
        minInterval = l;
    }

    public void fire()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        isPending = true;
        obj;
        JVM INSTR monitorexit ;
        firePending();
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void firePending()
    {
        boolean flag1 = false;
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag = flag1;
        long l;
        if (!isPending)
        {
            break MISSING_BLOCK_LABEL_56;
        }
        l = System.currentTimeMillis();
        flag = flag1;
        if (l < lastTimeFired + minInterval)
        {
            break MISSING_BLOCK_LABEL_56;
        }
        flag = true;
        isPending = false;
        lastTimeFired = l;
        obj;
        JVM INSTR monitorexit ;
        if (flag)
        {
            onActualFire();
            obj = getEventToFire();
            if (obj != null && bus != null)
            {
                bus.publish(obj);
            }
        }
        return;
        Exception exception;
        exception;
        throw exception;
    }

    protected abstract Object getEventToFire();

    protected void onActualFire()
    {
    }
}
