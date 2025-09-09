package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;

public class OpportunisticExecutor implements Executor {
    /* access modifiers changed from: private */
    public final Lock lock = new ReentrantLock();
    /* access modifiers changed from: private */
    public final Condition notEmpty = this.lock.newCondition();
    /* access modifiers changed from: private */
    public final Queue<Runnable> queue = new LinkedList();
    /* access modifiers changed from: private */
    public final Set<Runnable> runOnceRunnables = new HashSet();
    private final Thread thread;
    private final Runnable worker = new Runnable() {
        public void run() {
            Runnable runnable;
            while (true) {
                try {
                    OpportunisticExecutor.this.lock.lock();
                    while (true) {
                        runnable = (Runnable) OpportunisticExecutor.this.queue.poll();
                        if (runnable != null) {
                            break;
                        }
                        boolean z = false;
                        while (true) {
                            if (OpportunisticExecutor.this.runOnceRunnables.isEmpty()) {
                                break;
                            }
                            Iterator it = OpportunisticExecutor.this.runOnceRunnables.iterator();
                            if (!it.hasNext()) {
                                z = true;
                                break;
                            }
                            Runnable runnable2 = (Runnable) it.next();
                            it.remove();
                            try {
                                runnable2.run();
                            } catch (Exception e) {
                                Debug.Warning(e);
                            } catch (Throwable th) {
                                OpportunisticExecutor.this.lock.lock();
                                throw th;
                            }
                            OpportunisticExecutor.this.lock.lock();
                            z = true;
                        }
                        if (!z) {
                            OpportunisticExecutor.this.notEmpty.await();
                        }
                    }
                    OpportunisticExecutor.this.lock.unlock();
                    runnable.run();
                } finally {
                    try {
                        OpportunisticExecutor.this.lock.unlock();
                    } catch (Exception e2) {
                        Debug.Warning(e2);
                    }
                }
            }
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
            if (Thread.currentThread().getId() != this.thread.getId() || !this.queue.isEmpty()) {
                this.queue.offer(runnable);
                this.notEmpty.signalAll();
            } else {
                this.lock.unlock();
                runnable.run();
                this.lock.lock();
            }
        } catch (Exception e) {
            Debug.Warning(e);
        } catch (Throwable th) {
            this.lock.unlock();
            throw th;
        }
        this.lock.unlock();
    }

    public RunOnceExecutor getRunOnceExecutor() {
        return new RunOnceExecutor(this, (RunOnceExecutor) null);
    }
}
