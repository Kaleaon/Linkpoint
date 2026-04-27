package com.lumiyaviewer.lumiya.res.collections

import com.google.common.collect.ObjectArrays
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * A weak-reference based blocking queue with priority support
 * Items implementing LowPriority are processed after regular items
 */
class WeakQueue<T> : BlockingQueue<T> {
    
    private val lock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val queue: MutableSet<T> = Collections.newSetFromMap(WeakHashMap())
    private val lowPriorityQueue: MutableSet<T> = Collections.newSetFromMap(WeakHashMap())

    /**
     * Marker interface for low priority items
     */
    interface LowPriority

    override fun add(element: T): Boolean {
        if (element == null) return false
        
        lock.withLock {
            if (element is LowPriority) {
                lowPriorityQueue.add(element)
            } else {
                queue.add(element)
            }
            notEmpty.signalAll()
            return true
        }
    }

    override fun addAll(elements: Collection<T>): Boolean {
        lock.withLock {
            for (element in elements) {
                if (element is LowPriority) {
                    lowPriorityQueue.add(element)
                } else {
                    queue.add(element)
                }
            }
            notEmpty.signalAll()
            return true
        }
    }

    override fun clear() {
        lock.withLock {
            queue.clear()
            lowPriorityQueue.clear()
        }
    }

    override fun contains(element: T): Boolean {
        lock.withLock {
            return queue.contains(element) || lowPriorityQueue.contains(element)
        }
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        lock.withLock {
            return queue.containsAll(elements) || lowPriorityQueue.containsAll(elements)
        }
    }

    override fun drainTo(c: MutableCollection<in T>): Int {
        lock.withLock {
            var count = 0
            for (item in queue) {
                item?.let {
                    c.add(it)
                    count++
                }
            }
            queue.clear()
            
            for (item in lowPriorityQueue) {
                item?.let {
                    c.add(it)
                    count++
                }
            }
            lowPriorityQueue.clear()
            
            return count
        }
    }

    override fun drainTo(c: MutableCollection<in T>, maxElements: Int): Int {
        lock.withLock {
            var count = 0
            val queueIterator = queue.iterator()
            
            while (queueIterator.hasNext() && count < maxElements) {
                val item = queueIterator.next()
                item?.let {
                    c.add(it)
                    count++
                }
                queueIterator.remove()
            }
            
            val lowPriorityIterator = lowPriorityQueue.iterator()
            while (lowPriorityIterator.hasNext() && count < maxElements) {
                val item = lowPriorityIterator.next()
                item?.let {
                    c.add(it)
                    count++
                }
                lowPriorityIterator.remove()
            }
            
            return count
        }
    }

    override fun element(): T {
        return peek() ?: throw NoSuchElementException()
    }

    override fun isEmpty(): Boolean {
        lock.withLock {
            return queue.isEmpty() && lowPriorityQueue.isEmpty()
        }
    }

    override fun iterator(): MutableIterator<T> {
        throw UnsupportedOperationException("Iterating over WeakQueue is not supported")
    }

    override fun offer(e: T): Boolean = add(e)

    override fun offer(e: T, timeout: Long, unit: TimeUnit): Boolean = add(e)

    override fun peek(): T? {
        lock.withLock {
            // Check regular queue first
            if (queue.isNotEmpty()) {
                for (item in queue) {
                    item?.let { return it }
                }
            }
            
            // Then check low priority queue
            if (lowPriorityQueue.isNotEmpty()) {
                for (item in lowPriorityQueue) {
                    item?.let { return it }
                }
            }
            
            return null
        }
    }

    override fun poll(): T? {
        lock.withLock {
            // Poll from regular queue first
            if (queue.isNotEmpty()) {
                val iterator = queue.iterator()
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    item?.let {
                        iterator.remove()
                        return it
                    }
                }
            }
            
            // Then poll from low priority queue
            if (lowPriorityQueue.isNotEmpty()) {
                val iterator = lowPriorityQueue.iterator()
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
            return queue.remove(element) or lowPriorityQueue.remove(element)
        }
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        lock.withLock {
            return queue.removeAll(elements.toSet()) or lowPriorityQueue.removeAll(elements.toSet())
        }
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        lock.withLock {
            return queue.retainAll(elements.toSet()) or lowPriorityQueue.retainAll(elements.toSet())
        }
    }

    override fun size(): Int {
        lock.withLock {
            return queue.size + lowPriorityQueue.size
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
            return ObjectArrays.concat(queue.toTypedArray(), lowPriorityQueue.toTypedArray(), Any::class.java)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T1> toArray(a: Array<T1>): Array<T1> {
        lock.withLock {
            val array = toArray()
            return if (array.size <= a.size) {
                Arrays.fill(a, null)
                System.arraycopy(array, 0, a, 0, array.size)
                a
            } else {
                array as Array<T1>
            }
        }
    }
}
