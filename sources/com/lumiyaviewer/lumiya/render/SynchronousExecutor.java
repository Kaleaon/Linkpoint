// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.Debug;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class SynchronousExecutor
    implements Executor
{

    private final Queue queue = new ConcurrentLinkedQueue();

    public SynchronousExecutor()
    {
    }

    public void execute(Runnable runnable)
    {
        queue.add(runnable);
    }

    public void runQueuedTasks()
    {
        do
        {
            Runnable runnable = (Runnable)queue.poll();
            if (runnable != null)
            {
                try
                {
                    runnable.run();
                }
                catch (Exception exception)
                {
                    Debug.Warning(exception);
                }
            } else
            {
                return;
            }
        } while (true);
    }
}
