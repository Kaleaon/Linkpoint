package com.lumiyaviewer.lumiya.res.collections

import java.util.ArrayList
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class WeakQueue<T> : java.util.AbstractQueue<T>(), BlockingQueue<T> {
    
    // Interface needed by SpatialObjectIndex
    interface LowPriority
    
    private val lock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val innerList = ArrayList<T>()

    override fun offer(e: T): Boolean {
        if (e == null) throw NullPointerException()
        lock.withLock {
            val result = innerList.add(e)
            notEmpty.signalAll()
            return result
        }
    }

    override fun offer(e: T, timeout: Long, unit: TimeUnit): Boolean = offer(e)

    override fun poll(): T? {
        lock.withLock {
            if (innerList.isEmpty()) return null
            return innerList.removeAt(0)
        }
    }

    override fun poll(timeout: Long, unit: TimeUnit): T? {
        lock.withLock {
            var nanos = unit.toNanos(timeout)
            while (innerList.isEmpty()) {
                if (nanos <= 0) return null
                nanos = notEmpty.awaitNanos(nanos)
            }
            return innerList.removeAt(0)
        }
    }

    override fun peek(): T? {
        lock.withLock {
            return innerList.firstOrNull()
        }
    }

    override fun put(e: T) {
        offer(e)
    }

    override fun take(): T {
        lock.withLock {
            while (innerList.isEmpty()) {
                notEmpty.await()
            }
            return innerList.removeAt(0)
        }
    }

    override fun remainingCapacity(): Int = Integer.MAX_VALUE

    override fun drainTo(c: MutableCollection<in T>): Int {
        lock.withLock {
            val count = innerList.size
            c.addAll(innerList)
            innerList.clear()
            return count
        }
    }

    override fun drainTo(c: MutableCollection<in T>, maxElements: Int): Int {
        lock.withLock {
            val count = kotlin.math.min(innerList.size, maxElements)
            for (i in 0 until count) {
                c.add(innerList[i])
            }
            if (count > 0) {
                innerList.subList(0, count).clear()
            }
            return count
        }
    }

    override fun iterator(): MutableIterator<T> {
        return innerList.iterator()
    }

    override val size: Int
        get() = lock.withLock { innerList.size }
}
