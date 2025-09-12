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

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        r0.start();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void runQueue() {
        /*
            r3 = this;
        L_0x0000:
            boolean r0 = r3.paused
            if (r0 != 0) goto L_0x0012
            java.lang.Object r1 = r3.lock
            monitor-enter(r1)
            java.util.Set<com.lumiyaviewer.lumiya.res.executors.Startable> r0 = r3.activeRequests     // Catch:{ all -> 0x0027 }
            int r0 = r0.size()     // Catch:{ all -> 0x0027 }
            int r2 = r3.maxConcurrentRequests     // Catch:{ all -> 0x0027 }
            if (r0 < r2) goto L_0x0013
        L_0x0011:
            monitor-exit(r1)
        L_0x0012:
            return
        L_0x0013:
            java.util.Queue<com.lumiyaviewer.lumiya.res.executors.Startable> r0 = r3.waitingRequests     // Catch:{ all -> 0x0027 }
            java.lang.Object r0 = r0.poll()     // Catch:{ all -> 0x0027 }
            com.lumiyaviewer.lumiya.res.executors.Startable r0 = (com.lumiyaviewer.lumiya.res.executors.Startable) r0     // Catch:{ all -> 0x0027 }
            if (r0 == 0) goto L_0x0011
            java.util.Set<com.lumiyaviewer.lumiya.res.executors.Startable> r2 = r3.activeRequests     // Catch:{ all -> 0x0027 }
            r2.add(r0)     // Catch:{ all -> 0x0027 }
            monitor-exit(r1)
            r0.start()
            goto L_0x0000
        L_0x0027:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.executors.StartingExecutor.runQueue():void");
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
