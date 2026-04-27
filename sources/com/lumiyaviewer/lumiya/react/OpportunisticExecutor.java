// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OpportunisticExecutor
    implements Executor
{
    private class RunOnceExecutor
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

        private RunOnceExecutor()
        {
            this$0 = OpportunisticExecutor.this;
            super();
        }

        RunOnceExecutor(RunOnceExecutor runonceexecutor)
        {
            this();
        }
    }


    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty;
    private final Queue queue = new LinkedList();
    private final Set runOnceRunnables = new HashSet();
    private final Thread thread;
    private final Runnable worker = new Runnable() {

        final OpportunisticExecutor this$0;

        public void run()
        {
_L5:
            OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
_L7:
            Object obj = (Runnable)OpportunisticExecutor._2D_get2(OpportunisticExecutor.this).poll();
            boolean flag;
            if (obj != null)
            {
                break MISSING_BLOCK_LABEL_189;
            }
            flag = false;
_L3:
            if (OpportunisticExecutor._2D_get3(OpportunisticExecutor.this).isEmpty()) goto _L2; else goto _L1
_L1:
            Runnable runnable;
            obj = OpportunisticExecutor._2D_get3(OpportunisticExecutor.this).iterator();
            if (!((Iterator) (obj)).hasNext())
            {
                break MISSING_BLOCK_LABEL_168;
            }
            runnable = (Runnable)((Iterator) (obj)).next();
            ((Iterator) (obj)).remove();
            OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
            runnable.run();
_L4:
            OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
            flag = true;
              goto _L3
            obj;
            Debug.Warning(((Throwable) (obj)));
              goto _L4
            obj;
            OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).lock();
            throw obj;
            obj;
            try
            {
                OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
                throw obj;
            }
            // Misplaced declaration of an exception variable
            catch (Object obj)
            {
                Debug.Warning(((Throwable) (obj)));
            }
              goto _L5
            flag = true;
_L2:
            if (flag) goto _L7; else goto _L6
_L6:
            OpportunisticExecutor._2D_get1(OpportunisticExecutor.this).await();
              goto _L7
            OpportunisticExecutor._2D_get0(OpportunisticExecutor.this).unlock();
            ((Runnable) (obj)).run();
              goto _L5
        }

            
            {
                this$0 = OpportunisticExecutor.this;
                super();
            }
    };

    static Lock _2D_get0(OpportunisticExecutor opportunisticexecutor)
    {
        return opportunisticexecutor.lock;
    }

    static Condition _2D_get1(OpportunisticExecutor opportunisticexecutor)
    {
        return opportunisticexecutor.notEmpty;
    }

    static Queue _2D_get2(OpportunisticExecutor opportunisticexecutor)
    {
        return opportunisticexecutor.queue;
    }

    static Set _2D_get3(OpportunisticExecutor opportunisticexecutor)
    {
        return opportunisticexecutor.runOnceRunnables;
    }

    public OpportunisticExecutor(String s)
    {
        notEmpty = lock.newCondition();
        thread = new Thread(worker, s);
        thread.start();
    }

    public void execute(Runnable runnable)
    {
        lock.lock();
        if (Thread.currentThread().getId() != thread.getId() || !queue.isEmpty())
        {
            break MISSING_BLOCK_LABEL_92;
        }
        lock.unlock();
        runnable.run();
_L1:
        lock.lock();
_L2:
        lock.unlock();
        return;
        runnable;
        Debug.Warning(runnable);
          goto _L1
        runnable;
        lock.unlock();
        throw runnable;
        queue.offer(runnable);
        notEmpty.signalAll();
          goto _L2
    }

    public RunOnceExecutor getRunOnceExecutor()
    {
        return new RunOnceExecutor(null);
    }
}
