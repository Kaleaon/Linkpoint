// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            PriorityBinQueue, HasPriority

public class PriorityExecutorService
{
    private static class ComparePriority
        implements Comparator
    {

        public volatile int compare(Object obj, Object obj1)
        {
            return compare((Runnable)obj, (Runnable)obj1);
        }

        public int compare(Runnable runnable, Runnable runnable1)
        {
            int j = 0;
            int i;
            if (runnable instanceof HasPriority)
            {
                i = ((HasPriority)runnable).getPriority();
            } else
            {
                i = 0;
            }
            if (runnable1 instanceof HasPriority)
            {
                j = ((HasPriority)runnable1).getPriority();
            }
            return i - j;
        }

        private ComparePriority()
        {
        }
    }

    private static class ExecutorWithListener extends ThreadPoolExecutor
    {

        private OnExecutionCompleteListener onExecutionCompleteListener;

        protected void afterExecute(Runnable runnable, Throwable throwable)
        {
            if (onExecutionCompleteListener != null)
            {
                onExecutionCompleteListener.onExecutionComplete(runnable, throwable);
            }
        }

        public ExecutorWithListener(String s, int i, int j, long l, TimeUnit timeunit, BlockingQueue blockingqueue, 
                OnExecutionCompleteListener onexecutioncompletelistener)
        {
            super(i, j, l, timeunit, blockingqueue, new _cls1(s));
            onExecutionCompleteListener = null;
            onExecutionCompleteListener = onexecutioncompletelistener;
        }
    }

    public static interface OnExecutionCompleteListener
    {

        public abstract void onExecutionComplete(Runnable runnable, Throwable throwable);
    }


    private ThreadPoolExecutor exe;
    private PriorityBinQueue queue;

    public PriorityExecutorService(String s, int i, int j, OnExecutionCompleteListener onexecutioncompletelistener)
    {
        queue = new PriorityBinQueue(j);
        exe = new ExecutorWithListener(s, i, i, 10L, TimeUnit.SECONDS, queue, onexecutioncompletelistener);
    }

    public void cancel(Runnable runnable)
    {
        queue.remove(runnable);
    }

    public void execute(Runnable runnable)
    {
        exe.execute(runnable);
    }

    public int getNumThreads()
    {
        return exe.getCorePoolSize();
    }

    public int getNumWaitingTasks()
    {
        return exe.getQueue().size() + exe.getActiveCount();
    }

    public boolean isShutdown()
    {
        return exe.isShutdown();
    }

    public void setNumThreads(int i)
    {
        if (i != exe.getCorePoolSize() && i > 0)
        {
            exe.setCorePoolSize(i);
            exe.setMaximumPoolSize(i);
        }
    }

    public void shutdownNow()
    {
        exe.shutdownNow();
    }

    public void updatePriority(Runnable runnable)
    {
        queue.updatePriority(runnable);
    }

    // Unreferenced inner class com/lumiyaviewer/lumiya/utils/PriorityExecutorService$ExecutorWithListener$1

/* anonymous class */
    static final class ExecutorWithListener._cls1
        implements ThreadFactory
    {

        final String val$threadName;

        public Thread newThread(Runnable runnable)
        {
            return new Thread(runnable, threadName);
        }

            
            {
                threadName = s;
                super();
            }
    }

}
