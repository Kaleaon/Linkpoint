// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.Debug;
import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.concurrent.Executor;

public class SynchronousExecutor implements Executor
{
    private final Queue<Runnable> queue;
    
    public SynchronousExecutor() {
        this.queue = new ConcurrentLinkedQueue<Runnable>();
    }
    
    @Override
    public void execute(@Nonnull final Runnable runnable) {
        this.queue.add(runnable);
    }
    
    public void runQueuedTasks() {
        while (true) {
            final Runnable runnable = this.queue.poll();
            if (runnable == null) {
                break;
            }
            try {
                runnable.run();
            }
            catch (final Exception ex) {
                Debug.Warning(ex);
            }
        }
    }
}
