package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.Debug;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class SynchronousExecutor implements Executor {
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue();

    @Override // java.util.concurrent.Executor
    public void execute(@Nonnull Runnable runnable) {
        this.queue.add(runnable);
    }

    public void runQueuedTasks() {
        while (true) {
            Runnable poll = this.queue.poll();
            if (poll == null) {
                return;
            }
            try {
                poll.run();
            } catch (Exception e) {
                Debug.Warning(e);
            }
        }
    }
}
