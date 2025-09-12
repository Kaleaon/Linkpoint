// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            PriorityExecutorService

private static class onExecutionCompleteListener extends ThreadPoolExecutor
{

    private stener onExecutionCompleteListener;

    protected void afterExecute(Runnable runnable, Throwable throwable)
    {
        if (onExecutionCompleteListener != null)
        {
            onExecutionCompleteListener.onExecutionComplete(runnable, throwable);
        }
    }

    public stener(String s, int i, int j, long l, TimeUnit timeunit, BlockingQueue blockingqueue, 
            stener stener)
    {
        super(i, j, l, timeunit, blockingqueue, new ThreadFactory(s) {

            final String val$threadName;

            public Thread newThread(Runnable runnable)
            {
                return new Thread(runnable, threadName);
            }

            
            {
                threadName = s;
                super();
            }
        });
        onExecutionCompleteListener = null;
        onExecutionCompleteListener = stener;
    }
}
