// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            WeakExecutor

private static class priority extends FutureTask
    implements Comparable
{
    private static class WeakCallable
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

        private WeakCallable(Callable callable)
        {
            callableRef = new WeakReference(callable);
        }

        WeakCallable(Callable callable, WeakCallable weakcallable)
        {
            this(callable);
        }
    }

    private static class WeakRunnable
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

        private WeakRunnable(Runnable runnable)
        {
            runnableRef = new WeakReference(runnable);
        }

        WeakRunnable(Runnable runnable, WeakRunnable weakrunnable)
        {
            this(runnable);
        }
    }


    private final int priority;

    public int compareTo(WeakRunnable.runnableRef runnableref)
    {
        if (runnableref == this)
        {
            return 0;
        } else
        {
            return priority - runnableref.priority;
        }
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((compareTo)obj);
    }

    WeakRunnable(Runnable runnable, Object obj)
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

    WeakCallable(Callable callable)
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
