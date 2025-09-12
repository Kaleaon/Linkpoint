// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            Startable

public class StartingExecutor
{

    private final Set activeRequests;
    private final Object lock;
    private int maxConcurrentRequests;
    private volatile boolean paused;
    private final Queue waitingRequests;

    public StartingExecutor()
    {
        waitingRequests = new WeakQueue();
        activeRequests = Collections.newSetFromMap(new IdentityHashMap());
        lock = new Object();
        maxConcurrentRequests = 1;
        paused = false;
        maxConcurrentRequests = 1;
    }

    public StartingExecutor(int i)
    {
        waitingRequests = new WeakQueue();
        activeRequests = Collections.newSetFromMap(new IdentityHashMap());
        lock = new Object();
        maxConcurrentRequests = 1;
        paused = false;
        maxConcurrentRequests = i;
    }

    private void runQueue()
    {
_L7:
        if (paused) goto _L2; else goto _L1
_L1:
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        int i;
        int j;
        i = activeRequests.size();
        j = maxConcurrentRequests;
        if (i < j) goto _L4; else goto _L3
_L3:
        obj;
        JVM INSTR monitorexit ;
_L2:
        Startable startable;
        return;
_L4:
        if ((startable = (Startable)waitingRequests.poll()) == null) goto _L3; else goto _L5
_L5:
        activeRequests.add(startable);
        obj;
        JVM INSTR monitorexit ;
        startable.start();
        if (true) goto _L7; else goto _L6
_L6:
        Exception exception;
        exception;
        throw exception;
    }

    public void cancelRequest(Startable startable)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        waitingRequests.remove(startable);
        activeRequests.remove(startable);
        obj;
        JVM INSTR monitorexit ;
        runQueue();
        return;
        startable;
        throw startable;
    }

    public void completeRequest(Startable startable)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        activeRequests.remove(startable);
        obj;
        JVM INSTR monitorexit ;
        runQueue();
        return;
        startable;
        throw startable;
    }

    public void pause()
    {
        paused = true;
    }

    public void queueRequest(Startable startable)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        waitingRequests.add(startable);
        obj;
        JVM INSTR monitorexit ;
        runQueue();
        return;
        startable;
        throw startable;
    }

    public void setMaxConcurrentTasks(int i)
    {
        maxConcurrentRequests = i;
    }

    public void unpause()
    {
        paused = false;
        runQueue();
    }
}
