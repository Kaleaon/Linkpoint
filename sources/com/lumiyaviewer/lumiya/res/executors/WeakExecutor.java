// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WeakExecutor extends ThreadPoolExecutor
{
    private static class ComparableFutureTask extends FutureTask
        implements Comparable
    {

        private final int priority;

        public int compareTo(ComparableFutureTask comparablefuturetask)
        {
            if (comparablefuturetask == this)
            {
                return 0;
            } else
            {
                return priority - comparablefuturetask.priority;
            }
        }

        public volatile int compareTo(Object obj)
        {
            return compareTo((ComparableFutureTask)obj);
        }

        ComparableFutureTask(Runnable runnable, Object obj)
        {
            super(new WeakRunnable(runnable, null), obj);
            if (runnable instanceof HasPriority)
            {
                priority = ((HasPriority)runnable).getPriority();
                return;
            } else
            {
                priority = 0;
                return;
            }
        }

        ComparableFutureTask(Callable callable)
        {
            super(new WeakCallable(callable, null));
            if (callable instanceof HasPriority)
            {
                priority = ((HasPriority)callable).getPriority();
                return;
            } else
            {
                priority = 0;
                return;
            }
        }
    }

    private static class ComparableFutureTask.WeakCallable
        implements Callable
    {

        private final WeakReference callableRef;

        public Object call()
            throws Exception
        {
            Callable callable = (Callable)callableRef.get();
            if (callable != null)
            {
                return callable.call();
            } else
            {
                return null;
            }
        }

        private ComparableFutureTask.WeakCallable(Callable callable)
        {
            callableRef = new WeakReference(callable);
        }

        ComparableFutureTask.WeakCallable(Callable callable, ComparableFutureTask.WeakCallable weakcallable)
        {
            this(callable);
        }
    }

    private static class ComparableFutureTask.WeakRunnable
        implements Runnable
    {

        private final WeakReference runnableRef;

        public void run()
        {
            Runnable runnable = (Runnable)runnableRef.get();
            if (runnable != null)
            {
                runnable.run();
            }
        }

        private ComparableFutureTask.WeakRunnable(Runnable runnable)
        {
            runnableRef = new WeakReference(runnable);
        }

        ComparableFutureTask.WeakRunnable(Runnable runnable, ComparableFutureTask.WeakRunnable weakrunnable)
        {
            this(runnable);
        }
    }


    private final boolean usePriorities;

    WeakExecutor(String s, int i)
    {
        super(i, i, 60L, TimeUnit.SECONDS, new WeakQueue(), new _2D_.Lambda.paN_qX4OegT79dFg6kmGbliJfA0(s));
        usePriorities = false;
        Debug.Printf("Executor for %s: maxThreads %d", new Object[] {
            s, Integer.valueOf(i)
        });
        allowCoreThreadTimeOut(true);
    }

    public WeakExecutor(String s, int i, BlockingQueue blockingqueue)
    {
        super(i, i, 60L, TimeUnit.SECONDS, blockingqueue, new _2D_.Lambda.paN_qX4OegT79dFg6kmGbliJfA0._cls1(s));
        usePriorities = true;
        Debug.Printf("Executor for %s: maxThreads %d", new Object[] {
            s, Integer.valueOf(i)
        });
        allowCoreThreadTimeOut(true);
    }

    static Thread lambda$_2D_com_lumiyaviewer_lumiya_res_executors_WeakExecutor_1106(String s, Runnable runnable)
    {
        runnable = new Thread(runnable, s);
        Debug.Printf("Creating thread %s got %d", new Object[] {
            s, Long.valueOf(runnable.getId())
        });
        runnable.setPriority(4);
        return runnable;
    }

    static Thread lambda$_2D_com_lumiyaviewer_lumiya_res_executors_WeakExecutor_531(String s, Runnable runnable)
    {
        runnable = new Thread(runnable, s);
        Debug.Printf("Creating thread %s got %d", new Object[] {
            s, Long.valueOf(runnable.getId())
        });
        runnable.setPriority(4);
        return runnable;
    }

    protected RunnableFuture newTaskFor(Runnable runnable, Object obj)
    {
        if (usePriorities)
        {
            return new ComparableFutureTask(runnable, obj);
        } else
        {
            return super.newTaskFor(runnable, obj);
        }
    }

    protected RunnableFuture newTaskFor(Callable callable)
    {
        if (usePriorities)
        {
            return new ComparableFutureTask(callable);
        } else
        {
            return super.newTaskFor(callable);
        }
    }
}
