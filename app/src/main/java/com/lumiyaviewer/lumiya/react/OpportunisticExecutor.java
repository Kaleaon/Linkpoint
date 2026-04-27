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
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    
                    // Process regular queue items
                    Runnable task;
                    while ((task = queue.poll()) != null) {
                        lock.unlock();
                        try {
                            task.run();
                        } catch (Exception e) {
                            Debug.Warning(e);
                        }
                        lock.lock();
                    }
                    
                    // Process run-once runnables
                    boolean hasRunOnceWork = false;
                    if (!runOnceRunnables.isEmpty()) {
                        hasRunOnceWork = true;
                        
                        // Process all run-once runnables
                        var iterator = runOnceRunnables.iterator();
                        while (iterator.hasNext()) {
                            Runnable runOnceTask = iterator.next();
                            iterator.remove();
                            
                            lock.unlock();
                            try {
                                runOnceTask.run();
                            } catch (Exception e) {
                                Debug.Warning(e);
                            }
                            lock.lock();
                        }
                    }
                    
                    // If no work was done, wait for new tasks
                    if (!hasRunOnceWork) {
                        try {
                            notEmpty.await();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    
                } catch (Exception e) {
                    Debug.Warning(e);
                } finally {
                    try {
                        lock.unlock();
                    } catch (Exception e) {
                        Debug.Warning(e);
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
