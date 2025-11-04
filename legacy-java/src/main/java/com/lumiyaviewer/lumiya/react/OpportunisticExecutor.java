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

/* loaded from: classes.dex */
public class OpportunisticExecutor implements Executor {
    private final Thread thread;
    private final Queue<Runnable> queue = new LinkedList();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();
    private final Set<Runnable> runOnceRunnables = new HashSet();
    private final Runnable worker = new Runnable() { // from class: com.lumiyaviewer.lumiya.react.OpportunisticExecutor.1
        @Override // java.lang.Runnable
        public void run() {
            Runnable runnable;
            while (true) {
                try {
                    OpportunisticExecutor.this.lock.lock();
                    while (true) {
                        runnable = (Runnable) OpportunisticExecutor.this.queue.poll();
                        if (runnable == null) {
                            boolean z = false;
                            while (true) {
                                if (!OpportunisticExecutor.this.runOnceRunnables.isEmpty()) {
                                    Iterator it = OpportunisticExecutor.this.runOnceRunnables.iterator();
                                    if (!it.hasNext()) {
                                        z = true;
                                        break;
                                    }
                                    Runnable runnable2 = (Runnable) it.next();
                                    it.remove();
                                    try {
                                        OpportunisticExecutor.this.lock.unlock();
                                        try {
                                            runnable2.run();
                                        } catch (Exception e) {
                                            Debug.Warning(e);
                                        }
                                        OpportunisticExecutor.this.lock.lock();
                                        z = true;
                                    } catch (Throwable th) {
                                        OpportunisticExecutor.this.lock.lock();
                                        throw th;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                            if (!z) {
                                OpportunisticExecutor.this.notEmpty.await();
                            }
                        } else {
                            try {
                                break;
                            } catch (Exception e2) {
                                Debug.Warning(e2);
                            }
                        }
                        Debug.Warning(e2);
                    }
                    OpportunisticExecutor.this.lock.unlock();
                    runnable.run();
                } catch (Throwable th2) {
                    OpportunisticExecutor.this.lock.unlock();
                    throw th2;
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RunOnceExecutor implements Executor {
        private RunOnceExecutor() {
        }

        /* synthetic */ RunOnceExecutor(OpportunisticExecutor opportunisticExecutor, RunOnceExecutor runOnceExecutor) {
            this();
        }

        @Override // java.util.concurrent.Executor
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

    @Override // java.util.concurrent.Executor
    public void execute(@Nonnull Runnable runnable) {
        try {
            this.lock.lock();
            if (Thread.currentThread().getId() == this.thread.getId() && this.queue.isEmpty()) {
                this.lock.unlock();
                try {
                    runnable.run();
                } catch (Exception e) {
                    Debug.Warning(e);
                }
                this.lock.lock();
            } else {
                this.queue.offer(runnable);
                this.notEmpty.signalAll();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public RunOnceExecutor getRunOnceExecutor() {
        return new RunOnceExecutor(this, null);
    }
}
