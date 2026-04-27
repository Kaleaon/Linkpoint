// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            WeakExecutor

public class PrimComputeExecutor extends WeakExecutor
{
    private static class InstanceHolder
    {

        private static final PrimComputeExecutor Instance = new PrimComputeExecutor(null);

        static PrimComputeExecutor _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private boolean isPaused;
    private ReentrantLock pauseLock;
    private Condition unpaused;

    private PrimComputeExecutor()
    {
        super("PrimCompute", 1);
        pauseLock = new ReentrantLock();
        unpaused = pauseLock.newCondition();
    }

    PrimComputeExecutor(PrimComputeExecutor primcomputeexecutor)
    {
        this();
    }

    public static PrimComputeExecutor getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    protected void beforeExecute(Thread thread, Runnable runnable)
    {
        super.beforeExecute(thread, runnable);
        pauseLock.lock();
        try
        {
            while (isPaused) 
            {
                unpaused.await();
            }
            break MISSING_BLOCK_LABEL_45;
        }
        // Misplaced declaration of an exception variable
        catch (Runnable runnable) { }
        finally
        {
            pauseLock.unlock();
            throw thread;
        }
        thread.interrupt();
        pauseLock.unlock();
        return;
        pauseLock.unlock();
        return;
    }

    public void pause()
    {
        pauseLock.lock();
        isPaused = true;
        pauseLock.unlock();
        return;
        Exception exception;
        exception;
        pauseLock.unlock();
        throw exception;
    }

    public void resume()
    {
        pauseLock.lock();
        isPaused = false;
        unpaused.signalAll();
        pauseLock.unlock();
        return;
        Exception exception;
        exception;
        pauseLock.unlock();
        throw exception;
    }
}
