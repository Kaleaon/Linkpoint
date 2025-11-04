// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.LinkedList;
import java.util.Set;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.Executor;

public class OpportunisticExecutor implements Executor
{
    private final Lock lock;
    private final Condition notEmpty;
    private final Queue<Runnable> queue;
    private final Set<Runnable> runOnceRunnables;
    private final Thread thread;
    private final Runnable worker;
    
    public OpportunisticExecutor(final String name) {
        this.queue = new LinkedList<Runnable>();
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
        this.runOnceRunnables = new HashSet<Runnable>();
        this.worker = new Runnable() {
            @Override
            public void run() {
                while (true) {
                Label_0189:
                    while (true) {
                        boolean b = false;
                        Label_0170: {
                            Label_0168: {
                                try {
                                    OpportunisticExecutor.this.lock.lock();
                                    if (OpportunisticExecutor.this.queue.poll() == null) {
                                        b = false;
                                        while (!OpportunisticExecutor.this.runOnceRunnables.isEmpty()) {
                                            final Iterator iterator = OpportunisticExecutor.this.runOnceRunnables.iterator();
                                            if (!iterator.hasNext()) {
                                                break Label_0168;
                                            }
                                            final Runnable runnable = (Runnable)iterator.next();
                                            iterator.remove();
                                            try {
                                                OpportunisticExecutor.this.lock.unlock();
                                                try {
                                                    runnable.run();
                                                    OpportunisticExecutor.this.lock.lock();
                                                    b = true;
                                                }
                                                catch (final Exception ex) {
                                                    Debug.Warning(ex);
                                                }
                                            }
                                            finally {
                                                OpportunisticExecutor.this.lock.lock();
                                            }
                                        }
                                        break Label_0170;
                                    }
                                    break Label_0189;
                                }
                                finally {
                                    OpportunisticExecutor.this.lock.unlock();
                                }
                            }
                            b = true;
                        }
                        if (!b) {
                            OpportunisticExecutor.this.notEmpty.await();
                            continue;
                        }
                        continue;
                    }
                    OpportunisticExecutor.this.lock.unlock();
                    final Runnable runnable2;
                    runnable2.run();
                }
            }
        };
        (this.thread = new Thread(this.worker, name)).start();
    }
    
    @Override
    public void execute(@Nonnull final Runnable runnable) {
        while (true) {
            try {
                this.lock.lock();
                if (Thread.currentThread().getId() == this.thread.getId() && this.queue.isEmpty()) {
                    this.lock.unlock();
                    try {
                        runnable.run();
                        this.lock.lock();
                        return;
                    }
                    catch (final Exception ex) {
                        Debug.Warning(ex);
                    }
                }
            }
            finally {
                this.lock.unlock();
            }
            final Runnable runnable2;
            this.queue.offer(runnable2);
            this.notEmpty.signalAll();
        }
    }
    
    public RunOnceExecutor getRunOnceExecutor() {
        return new RunOnceExecutor((RunOnceExecutor)null);
    }
    
    private class RunOnceExecutor implements Executor
    {
        @Override
        public void execute(@Nonnull final Runnable runnable) {
            try {
                OpportunisticExecutor.this.lock.lock();
                OpportunisticExecutor.this.runOnceRunnables.add(runnable);
                OpportunisticExecutor.this.notEmpty.signalAll();
            }
            finally {
                OpportunisticExecutor.this.lock.unlock();
            }
        }
    }
}
