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
 * Higher priority items are processed before lower priority items
 */
class PriorityBinQueue<T>(
    private val queueFactory: QueueFactory<T>
) : BlockingQueue<T> {
    
    private val lock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val queues: SortedMap<Int, Queue<T>> = TreeMap()

    /**
     * Factory for creating queue instances for each priority bin
     */
    fun interface QueueFactory<T> {
        fun getQueue(): Queue<T>
    }

    /**
     * Get priority of an item
     */
    private fun getPriority(item: Any?): Int {
        return if (item is HasPriority) item.getPriority() else 0
    }

    override fun add(element: T): Boolean {
        lock.withLock {
            val priority = getPriority(element)
            Debug.Printf("PriorityBinQueue: added %s with prio %d", element.toString(), priority)
            
            val queue = queues.getOrPut(priority) { queueFactory.getQueue() }
            val result = queue.add(element)
            notEmpty.signalAll()
            return result
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        lock.withLock {
            var modified = false
            for (element in elements) {
                val priority = getPriority(element)
                val queue = queues.getOrPut(priority) { queueFactory.getQueue() }
                modified = queue.add(element) or modified
                notEmpty.signalAll()
            }
            return modified
        }
    }

    override fun clear() {
        lock.withLock {
            queues.clear()
        }
    }

    override fun contains(element: T): Boolean {
        lock.withLock {
            val queue = queues[getPriority(element)]
            return queue?.contains(element) ?: false
        }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        lock.withLock {
            return elements.all { element ->
                val queue = queues[getPriority(element)]
                queue?.contains(element) == true
            }
        }
    }

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

    override fun element(): T {
        return peek() ?: throw NoSuchElementException()
    }

    override fun isEmpty(): Boolean {
        lock.withLock {
            return queues.values.all { it.isEmpty() }
        }
    }

    override fun iterator(): MutableIterator<T> {
        throw UnsupportedOperationException("Iterator not supported for PriorityBinQueue")
    }

    override fun offer(e: T): Boolean = add(e)

    override fun offer(e: T, timeout: Long, unit: TimeUnit): Boolean = add(e)

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
            do {
                poll()?.let { return it }
            } while (notEmpty.await(timeout, unit))
            return null
        }
    }

    override fun put(e: T) {
        add(e)
    }

    override fun remainingCapacity(): Int = Integer.MAX_VALUE

    override fun remove(): T {
        return poll() ?: throw NoSuchElementException()
    }

    override fun remove(element: T): Boolean {
        lock.withLock {
            val queue = queues[getPriority(element)]
            return queue?.remove(element) ?: false
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        lock.withLock {
            var modified = false
            for (element in elements) {
                val queue = queues[getPriority(element)]
                queue?.let {
                    modified = it.remove(element) or modified
                }
            }
            return modified
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        lock.withLock {
            var modified = false
            for (queue in queues.values) {
                modified = queue.retainAll(elements.toSet()) or modified
            }
            return modified
        }
    }

    override fun size(): Int {
        lock.withLock {
            return queues.values.sumOf { it.size }
        }
    }

    override fun take(): T {
        lock.withLock {
            while (true) {
                poll()?.let { return it }
                notEmpty.await()
            }
        }
    }

    override fun toArray(): Array<Any?> {
        lock.withLock {
            val arrays = queues.values.map { it.toTypedArray() }
            val totalSize = arrays.sumOf { it.size }
            val result = arrayOfNulls<Any>(totalSize)
            
            var offset = 0
            for (array in arrays) {
                System.arraycopy(array, 0, result, offset, array.size)
                offset += array.size
            }
            
            return result
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T1> toArray(a: Array<T1>): Array<T1> {
        lock.withLock {
            val arrays = queues.values.map { it.toTypedArray() }
            val totalSize = arrays.sumOf { it.size }
            
            val result = if (a.size >= totalSize) {
                Arrays.fill(a, null)
                a
            } else {
                arrayOfNulls<Any>(totalSize) as Array<T1>
            }
            
            var offset = 0
            for (array in arrays) {
                System.arraycopy(array, 0, result, offset, array.size)
                offset += array.size
            }
            
            return result
        }
    }
}
