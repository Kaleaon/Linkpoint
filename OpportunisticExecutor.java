package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;

public class OpportunisticExecutor implements Executor {
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();
    private final Queue<Runnable> queue = new LinkedList();
    private final Set<Runnable> runOnceRunnables = new HashSet();
    private final Thread thread;
    private final Runnable worker = new Runnable() {
        /* DevToolsApp WARNING: Missing block: B:36:?, code:
            com.lumiyaviewer.lumiya.react.OpportunisticExecutor.-get0(r3.this$0).unlock();
            r0.run();
     */
        public void run() {
            /*
            r3 = this;
        L_0x0000:
            r0 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r0 = r0.lock;	 Catch:{ all -> 0x0065 }
            r0.lock();	 Catch:{ all -> 0x0065 }
        L_0x0009:
            r0 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r0 = r0.queue;	 Catch:{ all -> 0x0065 }
            r0 = r0.poll();	 Catch:{ all -> 0x0065 }
            r0 = (java.lang.Runnable) r0;	 Catch:{ all -> 0x0065 }
            if (r0 != 0) goto L_0x0082;
        L_0x0017:
            r0 = 0;
        L_0x0018:
            r1 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r1 = r1.runOnceRunnables;	 Catch:{ all -> 0x0065 }
            r1 = r1.isEmpty();	 Catch:{ all -> 0x0065 }
            if (r1 != 0) goto L_0x0076;
        L_0x0024:
            r1 = 1;
            r0 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r0 = r0.runOnceRunnables;	 Catch:{ all -> 0x0065 }
            r2 = r0.iterator();	 Catch:{ all -> 0x0065 }
            r0 = r2.hasNext();	 Catch:{ all -> 0x0065 }
            if (r0 == 0) goto L_0x0075;
        L_0x0035:
            r0 = r2.next();	 Catch:{ all -> 0x0065 }
            r0 = (java.lang.Runnable) r0;	 Catch:{ all -> 0x0065 }
            r2.remove();	 Catch:{ all -> 0x0065 }
            r2 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x005a }
            r2 = r2.lock;	 Catch:{ all -> 0x005a }
            r2.unlock();	 Catch:{ all -> 0x005a }
            r0.run();	 Catch:{ Exception -> 0x0055 }
        L_0x004a:
            r0 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r0 = r0.lock;	 Catch:{ all -> 0x0065 }
            r0.lock();	 Catch:{ all -> 0x0065 }
            r0 = r1;
            goto L_0x0018;
        L_0x0055:
            r0 = move-exception;
            com.lumiyaviewer.lumiya.Debug.Warning(r0);	 Catch:{ all -> 0x005a }
            goto L_0x004a;
        L_0x005a:
            r0 = move-exception;
            r1 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r1 = r1.lock;	 Catch:{ all -> 0x0065 }
            r1.lock();	 Catch:{ all -> 0x0065 }
            throw r0;	 Catch:{ all -> 0x0065 }
        L_0x0065:
            r0 = move-exception;
            r1 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ Exception -> 0x0070 }
            r1 = r1.lock;	 Catch:{ Exception -> 0x0070 }
            r1.unlock();	 Catch:{ Exception -> 0x0070 }
            throw r0;	 Catch:{ Exception -> 0x0070 }
        L_0x0070:
            r0 = move-exception;
            com.lumiyaviewer.lumiya.Debug.Warning(r0);
            goto L_0x0000;
        L_0x0075:
            r0 = r1;
        L_0x0076:
            if (r0 != 0) goto L_0x0009;
        L_0x0078:
            r0 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ all -> 0x0065 }
            r0 = r0.notEmpty;	 Catch:{ all -> 0x0065 }
            r0.await();	 Catch:{ all -> 0x0065 }
            goto L_0x0009;
        L_0x0082:
            r1 = com.lumiyaviewer.lumiya.react.OpportunisticExecutor.this;	 Catch:{ Exception -> 0x0070 }
            r1 = r1.lock;	 Catch:{ Exception -> 0x0070 }
            r1.unlock();	 Catch:{ Exception -> 0x0070 }
            r0.run();	 Catch:{ Exception -> 0x0070 }
            goto L_0x0000;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.react.OpportunisticExecutor.1.run():void");
        }
    };

    private class RunOnceExecutor implements Executor {
        private RunOnceExecutor() {
        }

        /* synthetic */ RunOnceExecutor(OpportunisticExecutor opportunisticExecutor, RunOnceExecutor runOnceExecutor) {
            this();
        }

        public void execute(@Nonnull Runnable runnable) {
            try {
                OpportunisticExecutor.this.lock.lock();
                OpportunisticExecutor.this.runOnceRunnables.add(runnable);
                OpportunisticExecutor.this.notEmpty.signalAll();
            } finally {
                OpportunisticExecutor.this.lock.unlock();
            }
        }
    }

    public OpportunisticExecutor(String str) {
        this.thread = new Thread(this.worker, str);
        this.thread.start();
    }

    public void execute(@Nonnull Runnable runnable) {
        try {
            this.lock.lock();
            if (Thread.currentThread().getId() == this.thread.getId() && this.queue.isEmpty()) {
                this.lock.unlock();
                runnable.run();
                this.lock.lock();
            } else {
                this.queue.offer(runnable);
                this.notEmpty.signalAll();
            }
        } catch (Throwable e) {
            Debug.Warning(e);
        } catch (Throwable th) {
            this.lock.unlock();
        }
        this.lock.unlock();
    }

    public RunOnceExecutor getRunOnceExecutor() {
        return new RunOnceExecutor(this, null);
    }
}
