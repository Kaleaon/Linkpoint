// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import android.os.Handler;
import android.os.Looper;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UIThreadExecutor
    implements Executor
{
    private static class InstanceHolder
    {

        private static final UIThreadExecutor Instance = new UIThreadExecutor();

        static UIThreadExecutor _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private final Handler handler;
    private final Lock lock = new ReentrantLock();
    private final Looper mainLooper = Looper.getMainLooper();
    private final Queue queue = new ConcurrentLinkedQueue();
    private final Runnable queueRunnable = new _2D_.Lambda.D7hbGKuty0crHscG_2D_TIL_2D_CtFXqo(this);
    private final AtomicBoolean runnablePosted = new AtomicBoolean(false);
    private final Executor serialExecutor = new Executor() {

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

            
            {
                this$0 = UIThreadExecutor.this;
                super();
            }
    };

    static Handler _2D_get0(UIThreadExecutor uithreadexecutor)
    {
        return uithreadexecutor.handler;
    }

    static Lock _2D_get1(UIThreadExecutor uithreadexecutor)
    {
        return uithreadexecutor.lock;
    }

    static Queue _2D_get2(UIThreadExecutor uithreadexecutor)
    {
        return uithreadexecutor.queue;
    }

    static Runnable _2D_get3(UIThreadExecutor uithreadexecutor)
    {
        return uithreadexecutor.queueRunnable;
    }

    static AtomicBoolean _2D_get4(UIThreadExecutor uithreadexecutor)
    {
        return uithreadexecutor.runnablePosted;
    }

    public UIThreadExecutor()
    {
        handler = new Handler(mainLooper);
    }

    public static Executor getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    public static Executor getSerialInstance()
    {
        return InstanceHolder._2D_get0().serialExecutor;
    }

    public void execute(Runnable runnable)
    {
        boolean flag;
        lock.lock();
        if (Looper.myLooper() != mainLooper)
        {
            break MISSING_BLOCK_LABEL_99;
        }
        flag = queue.isEmpty();
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_99;
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
        lock.lock();
        throw runnable;
        runnable;
        lock.unlock();
        throw runnable;
        queue.add(runnable);
        if (!runnablePosted.getAndSet(true))
        {
            handler.post(queueRunnable);
        }
          goto _L2
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_UIThreadExecutor_2348()
    {
        lock.lock();
        runnablePosted.set(false);
_L1:
        Runnable runnable = (Runnable)queue.poll();
        if (runnable == null)
        {
            break MISSING_BLOCK_LABEL_93;
        }
        lock.unlock();
        runnable.run();
_L2:
        lock.lock();
          goto _L1
        Object obj;
        obj;
        lock.unlock();
        throw obj;
        obj;
        Debug.Warning(((Throwable) (obj)));
          goto _L2
        obj;
        lock.lock();
        throw obj;
        lock.unlock();
        return;
    }
}
