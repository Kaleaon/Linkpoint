package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.Set;

/* loaded from: classes.dex */
public class StartingExecutor {
    private int maxConcurrentRequests;
    private final Queue<Startable> waitingRequests = new WeakQueue();
    private final Set<Startable> activeRequests = Collections.newSetFromMap(new IdentityHashMap());
    private final Object lock = new Object();
    private volatile boolean paused = false;

    public StartingExecutor() {
        this.maxConcurrentRequests = 1;
        this.maxConcurrentRequests = 1;
    }

    public StartingExecutor(int i) {
        this.maxConcurrentRequests = 1;
        this.maxConcurrentRequests = i;
    }

    private void runQueue() {
        Startable poll;
        while (!this.paused) {
            synchronized (this.lock) {
                if (this.activeRequests.size() >= this.maxConcurrentRequests || (poll = this.waitingRequests.poll()) == null) {
                    return;
                } else {
                    this.activeRequests.add(poll);
                }
            }
            poll.start();
        }
    }

    public void cancelRequest(Startable startable) {
        synchronized (this.lock) {
            this.waitingRequests.remove(startable);
            this.activeRequests.remove(startable);
        }
        runQueue();
    }

    public void completeRequest(Startable startable) {
        synchronized (this.lock) {
            this.activeRequests.remove(startable);
        }
        runQueue();
    }

    public void pause() {
        this.paused = true;
    }

    public void queueRequest(Startable startable) {
        synchronized (this.lock) {
            this.waitingRequests.add(startable);
        }
        runQueue();
    }

    public void setMaxConcurrentTasks(int i) {
        this.maxConcurrentRequests = i;
    }

    public void unpause() {
        this.paused = false;
        runQueue();
    }
}
