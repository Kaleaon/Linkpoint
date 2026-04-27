// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.executors;

import java.util.Map;
import java.util.Collections;
import java.util.IdentityHashMap;
import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.Queue;
import java.util.Set;

public class StartingExecutor
{
    private final Set<Startable> activeRequests;
    private final Object lock;
    private int maxConcurrentRequests;
    private volatile boolean paused;
    private final Queue<Startable> waitingRequests;
    
    public StartingExecutor() {
        this.waitingRequests = new WeakQueue<Startable>();
        this.activeRequests = Collections.newSetFromMap(new IdentityHashMap<Startable, Boolean>());
        this.lock = new Object();
        this.maxConcurrentRequests = 1;
        this.paused = false;
        this.maxConcurrentRequests = 1;
    }
    
    public StartingExecutor(final int maxConcurrentRequests) {
        this.waitingRequests = new WeakQueue<Startable>();
        this.activeRequests = Collections.newSetFromMap(new IdentityHashMap<Startable, Boolean>());
        this.lock = new Object();
        this.maxConcurrentRequests = 1;
        this.paused = false;
        this.maxConcurrentRequests = maxConcurrentRequests;
    }
    
    private void runQueue() {
        while (true) {
            if (this.paused) {
                return;
            }
            synchronized (this.lock) {
                if (this.activeRequests.size() < this.maxConcurrentRequests) {
                    final Startable startable = this.waitingRequests.poll();
                    if (startable != null) {
                        this.activeRequests.add(startable);
                        monitorexit(this.lock);
                        startable.start();
                        continue;
                    }
                }
            }
        }
    }
    
    public void cancelRequest(final Startable startable) {
        synchronized (this.lock) {
            this.waitingRequests.remove(startable);
            this.activeRequests.remove(startable);
            monitorexit(this.lock);
            this.runQueue();
        }
    }
    
    public void completeRequest(final Startable startable) {
        synchronized (this.lock) {
            this.activeRequests.remove(startable);
            monitorexit(this.lock);
            this.runQueue();
        }
    }
    
    public void pause() {
        this.paused = true;
    }
    
    public void queueRequest(final Startable startable) {
        synchronized (this.lock) {
            this.waitingRequests.add(startable);
            monitorexit(this.lock);
            this.runQueue();
        }
    }
    
    public void setMaxConcurrentTasks(final int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }
    
    public void unpause() {
        this.paused = false;
        this.runQueue();
    }
}
