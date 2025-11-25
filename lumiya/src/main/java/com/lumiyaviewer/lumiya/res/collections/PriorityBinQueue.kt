package com.lumiyaviewer.lumiya.res.collections

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.utils.HasPriority
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Priority-based blocking queue that bins items by priority level
 */
class PriorityBinQueue<T>(
    private val queueFactory: QueueFactory<T>
) : java.util.AbstractQueue<T>(), BlockingQueue<T> {
    
    private val lock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val queues: SortedMap<Int, Queue<T>> = TreeMap()

    fun interface QueueFactory<T> {
        fun getQueue(): Queue<T>
    }

    private fun getPriority(item: Any?): Int {
        return if (item is HasPriority) item.getPriority() else 0
    }

    override fun offer(e: T): Boolean {
        if (e == null) throw NullPointerException()
        lock.withLock {
            val priority = getPriority(e)
            val queue = queues.getOrPut(priority) { queueFactory.getQueue() }
            val result = queue.add(e)
            notEmpty.signalAll()
            return result
        }
    }

    override fun offer(e: T, timeout: Long, unit: TimeUnit): Boolean {
        return offer(e)
    }

    override fun poll(): T? {
        lock.withLock {
            for (queue in queues.values) {
                val iterator = queue.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    item?.let {
                        iterator.remove()
                        return it
                    }
                }
            }
            return null
        }
    }

    override fun poll(timeout: Long, unit: TimeUnit): T? {
        lock.withLock {
            var nanos = unit.toNanos(timeout)
            while (true) {
                val item = poll()
                if (item != null) return item
                if (nanos <= 0) return null
                nanos = notEmpty.awaitNanos(nanos)
            }
        }
    }

    override fun peek(): T? {
        lock.withLock {
            for (queue in queues.values) {
                if (queue.isNotEmpty()) {
                    for (item in queue) {
                        item?.let { return it }
                    }
                }
            }
            return null
        }
    }

    override fun put(e: T) {
        offer(e)
    }

    override fun take(): T {
        lock.withLock {
            while (true) {
                val item = poll()
                if (item != null) return item
                notEmpty.await()
            }
        }
    }

    override fun remainingCapacity(): Int = Integer.MAX_VALUE

    override fun drainTo(c: MutableCollection<in T>): Int {
        lock.withLock {
            var count = 0
            for (queue in queues.values) {
                while (true) {
                    val item = queue.poll() ?: break
                    c.add(item)
                    count++
                }
            }
            queues.clear()
            return count
        }
    }

    override fun drainTo(c: MutableCollection<in T>, maxElements: Int): Int {
        lock.withLock {
            var count = 0
            for (queue in queues.values) {
                while (count < maxElements) {
                    val item = queue.poll() ?: break
                    c.add(item)
                    count++
                }
                if (count >= maxElements) break
            }
            return count
        }
    }

    override fun iterator(): MutableIterator<T> {
        throw UnsupportedOperationException("Iterator not supported for PriorityBinQueue")
    }

    override val size: Int
        get() = lock.withLock {
            queues.values.sumOf { it.size }
        }
}
