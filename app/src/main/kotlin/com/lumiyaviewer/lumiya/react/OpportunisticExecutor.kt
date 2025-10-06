package com.lumiyaviewer.lumiya.react

import com.lumiyaviewer.lumiya.Debug
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Executor
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class OpportunisticExecutor(name: String) : Executor {
    private val lock = ReentrantLock()
    private val notEmpty: Condition = lock.newCondition()
    private val queue: Queue<Runnable> = LinkedList()
    private val runOnceRunnables = mutableSetOf<Runnable>()
    
    private val worker = Runnable {
        while (true) {
            try {
                lock.withLock {
                    // Process regular queue items
                    while (true) {
                        val task = queue.poll() ?: break
                        lock.unlock()
                        try {
                            task.run()
                        } catch (e: Exception) {
                            Debug.Warning(e)
                        }
                        lock.lock()
                    }
                    
                    // Process run-once runnables
                    var hasRunOnceWork = false
                    if (runOnceRunnables.isNotEmpty()) {
                        hasRunOnceWork = true
                        
                        val iterator = runOnceRunnables.iterator()
                        while (iterator.hasNext()) {
                            val runOnceTask = iterator.next()
                            iterator.remove()
                            
                            lock.unlock()
                            try {
                                runOnceTask.run()
                            } catch (e: Exception) {
                                Debug.Warning(e)
                            }
                            lock.lock()
                        }
                    }
                    
                    // If no work was done, wait for new tasks
                    if (!hasRunOnceWork) {
                        try {
                            notEmpty.await()
                        } catch (e: InterruptedException) {
                            Thread.currentThread().interrupt()
                            return@Runnable
                        }
                    }
                }
            } catch (e: Exception) {
                Debug.Warning(e)
            }
        }
    }
    
    private val thread = Thread(worker, name).apply { start() }
    
    inner class RunOnceExecutor : Executor {
        override fun execute(command: Runnable) {
            lock.withLock {
                runOnceRunnables.add(command)
                notEmpty.signalAll()
            }
        }
    }
    
    override fun execute(command: Runnable) {
        try {
            lock.lock()
            if (Thread.currentThread().id == thread.id && queue.isEmpty()) {
                lock.unlock()
                command.run()
                lock.lock()
            } else {
                queue.offer(command)
                notEmpty.signalAll()
            }
        } catch (e: Throwable) {
            Debug.Warning(e)
        } finally {
            try {
                lock.unlock()
            } catch (e: Exception) {
                // Ignore unlock errors
            }
        }
    }
    
    fun getRunOnceExecutor(): RunOnceExecutor = RunOnceExecutor()
}
