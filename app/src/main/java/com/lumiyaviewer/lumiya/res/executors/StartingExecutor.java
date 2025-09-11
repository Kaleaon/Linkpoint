package com.lumiyaviewer.lumiya.res.executors;

import com.lumiyaviewer.lumiya.res.collections.WeakQueue;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Queue;
import java.util.Set;

public class StartingExecutor {
    private final Set<Startable> activeRequests;
    private final Object lock;
    private int maxConcurrentRequests;
    private volatile boolean paused;
    private final Queue<Startable> waitingRequests;

    public StartingExecutor() {
        this.waitingRequests = new WeakQueue();
        this.activeRequests = Collections.newSetFromMap(new IdentityHashMap());
        this.lock = new Object();
        this.maxConcurrentRequests = 1;
        this.paused = false;
        this.maxConcurrentRequests = 1;
    }

    public StartingExecutor(int i) {
        this.waitingRequests = new WeakQueue();
        this.activeRequests = Collections.newSetFromMap(new IdentityHashMap());
        this.lock = new Object();
        this.maxConcurrentRequests = 1;
        this.paused = false;
        this.maxConcurrentRequests = i;
    }

    /* DevToolsApp WARNING: Missing block: B:13:0x0023, code:
            r0.start();
     */
    /* DevToolsApp WARNING: Missing block: B:21:?, code:
            return;
     */
    private void runQueue() {
        while (!this.paused) {
            Startable request;
            synchronized (this.lock) {
                // Check if we've reached the maximum concurrent requests limit
                if (this.activeRequests.size() >= this.maxConcurrentRequests) {
                    return;
                }
                
                // Poll the next waiting request
                request = this.waitingRequests.poll();
                if (request == null) {
                    return;
                }
                
                // Add to active requests before starting
                this.activeRequests.add(request);
            }
            
            // Start the request outside of the synchronized block
            request.start();
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
