package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.Debug;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public class SynchronousExecutor implements Executor {
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue();

    public void execute(@Nonnull Runnable runnable) {
        this.queue.add(runnable);
    }

    public void runQueuedTasks() {
        while (true) {
            Runnable poll = this.queue.poll();
            if (poll != null) {
                try {
                    poll.run();
                } catch (Exception e) {
                    Debug.Warning(e);
                }
            } else {
                return;
            }
        }
    }
}
