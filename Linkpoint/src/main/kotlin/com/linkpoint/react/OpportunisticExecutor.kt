package com.linkpoint.react

import com.linkpoint.Debug
import java.util.HashSet
import java.util.LinkedList
import java.util.Queue
import java.util.Set
import java.util.concurrent.Executor
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.Nonnull

class OpportunisticExecutor : Executor {
    private val Lock lock = ReentrantLock()
    private val Condition notEmpty = this.lock.newCondition()
    private val Queue<Runnable> queue = LinkedList()
    private val Set<Runnable> runOnceRunnables = HashSet()
    private val Thread thread
    private val Runnable worker = Runnable() {
        override Unit run() {
            while (true) {
                try {
                    lock.lock()
                    
                    // Process regular queue items
                    Runnable task
                    while ((task = queue.poll()) != null) {
                        lock.unlock()
                        try {
                            task.run()
                        } catch (Exception e) {
                            Debug.Warning(e)
                        }
                        lock.lock()
                    }
                    
                    // Process run-once runnables
                    val hasRunOnceWork: Boolean = false
                    if (!runOnceRunnables.isEmpty()) {
                        hasRunOnceWork = true
                        
                        // Process all run-once runnables
                        var iterator = runOnceRunnables.iterator()
                        while (iterator.hasNext()) {
                            val runOnceTask: Runnable = iterator.next()
                            iterator.remove()
                            
                            lock.unlock()
                            try {
                                runOnceTask.run()
                            } catch (Exception e) {
                                Debug.Warning(e)
                            }
                            lock.lock()
                        }
                    }
                    
                    // If no work was done, wait for tasks
                    if (!hasRunOnceWork) {
                        try {
                            notEmpty.await()
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt()
                            break
                        }
                    }
                    
                } catch (Exception e) {
                    Debug.Warning(e)
                } finally {
                    try {
                        lock.unlock()
                    } catch (Exception e) {
                        Debug.Warning(e)
                    }
                }
            }
        }
    }

    private class RunOnceExecutor : Executor {
        private RunOnceExecutor() {
        }

        /* synthetic */ RunOnceExecutor(OpportunisticExecutor opportunisticExecutor, RunOnceExecutor runOnceExecutor) {
            this()
        }

        fun execute(runnable: Runnable) {
            try {
                OpportunisticExecutor.this.lock.lock()
                OpportunisticExecutor.this.runOnceRunnables.add(runnable)
                OpportunisticExecutor.this.notEmpty.signalAll()
            } finally {
                OpportunisticExecutor.this.lock.unlock()
            }
        }
    }

    public OpportunisticExecutor(String str) {
        this.thread = Thread(this.worker, str)
        this.thread.start()
    }

    fun execute(runnable: Runnable) {
        try {
            this.lock.lock()
            if (Thread.currentThread().getId() == this.thread.getId() && this.queue.isEmpty()) {
                this.lock.unlock()
                runnable.run()
                this.lock.lock()
            } else {
                this.queue.offer(runnable)
                this.notEmpty.signalAll()
            }
        } catch (Throwable e) {
            Debug.Warning(e)
        } catch (Throwable th) {
            this.lock.unlock()
        }
        this.lock.unlock()
    }

     public fun getRunOnceExecutor(): RunOnceExecutor {
        return RunOnceExecutor(this, null)
    }
}
