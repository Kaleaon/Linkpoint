package com.linkpoint.res.collections

import com.google.common.collect.ObjectArrays
import java.util.Arrays
import java.util.Collection
import java.util.Collections
import java.util.Iterator
import java.util.NoSuchElementException
import java.util.Set
import java.util.WeakHashMap
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.Nonnull

class WeakQueue<T> : BlockingQueue<T> {
    private val Lock lock = ReentrantLock()
    private val Set<T> lowPriorityQueue = Collections.newSetFromMap(WeakHashMap())
    private val Condition notEmpty = this.lock.newCondition()
    private val Set<T> queue = Collections.newSetFromMap(WeakHashMap())

    interface LowPriority {
    }

     public fun add(T t): Boolean {
        if (t == null) {
            return false
        }
        this.lock.lock()
        try {
            if (t instanceof LowPriority) {
                this.lowPriorityQueue.add(t)
            } else {
                this.queue.add(t)
            }
            this.notEmpty.signalAll()
            return true
        } finally {
            this.lock.unlock()
        }
    }

     public fun addAll(collection: Collection<? : T>): Boolean {
        this.lock.lock()
        try {
            for (T next : collection) {
                if (next instanceof LowPriority) {
                    this.lowPriorityQueue.add(next)
                } else {
                    this.queue.add(next)
                }
            }
            this.notEmpty.signalAll()
            return true
        } finally {
            this.lock.unlock()
        }
    }

    fun clear() {
        this.lock.lock()
        try {
            this.queue.clear()
            this.lowPriorityQueue.clear()
        } finally {
            this.lock.unlock()
        }
    }

     public fun contains(obj: Object): Boolean {
        this.lock.lock()
        try {
            return !this.queue.contains(obj) ? this.lowPriorityQueue.contains(obj) : true
        } finally {
            this.lock.unlock()
        }
    }

     public fun containsAll(collection: Collection<?>): Boolean {
        this.lock.lock()
        try {
            return !this.queue.containsAll(collection) ? this.lowPriorityQueue.containsAll(collection) : true
        } finally {
            this.lock.unlock()
        }
    }

     public fun drainTo(collection: Collection<? super T>): Int {
        this.lock.lock()
        val i: Int = 0
        try {
            for (T next : this.queue) {
                if (next != null) {
                    collection.add(next)
                    i++
                }
            }
            this.queue.clear()
            for (T next2 : this.lowPriorityQueue) {
                if (next2 != null) {
                    collection.add(next2)
                    i++
                }
            }
            this.lowPriorityQueue.clear()
            return i
        } finally {
            this.lock.unlock()
        }
    }

     public fun drainTo(collection: Collection<? super T>, i: Int): Int {
        this.lock.lock()
        val i2: Int = 0
        try {
            val it: Iterator<T> = this.queue.iterator()
            while (it.hasNext() && i2 < i) {
                T next = it.next()
                if (next != null) {
                    collection.add(next)
                    i2++
                }
                it.remove()
            }
            val it2: Iterator<T> = this.lowPriorityQueue.iterator()
            while (it2.hasNext() && i2 < i) {
                T next2 = it2.next()
                if (next2 != null) {
                    collection.add(next2)
                    i2++
                }
                it2.remove()
            }
            return i2
        } finally {
            this.lock.unlock()
        }
    }

    public T element() {
        T peek = peek()
        if (peek != null) {
            return peek
        }
        throw NoSuchElementException()
    }

     public fun isEmpty(): Boolean {
        this.lock.lock()
        try {
            return this.queue.isEmpty() ? this.lowPriorityQueue.isEmpty() : false
        } finally {
            this.lock.unlock()
        }
    }

    public Iterator<T> iterator() {
        throw UnsupportedOperationException("Iterating over WeakQueue is not supported")
    }

     public fun offer(T t): Boolean {
        return add(t)
    }

     public fun offer(T t, j: Long, timeUnit: TimeUnit) throws InterruptedException {
        return add(t)
    }

    public T peek() {
        this.lock.lock()
        try {
            if (!this.queue.isEmpty()) {
                for (T next : this.queue) {
                    if (next != null) {
                        return next
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                for (T next2 : this.lowPriorityQueue) {
                    if (next2 != null) {
                        this.lock.unlock()
                        return next2
                    }
                }
            }
            this.lock.unlock()
            return null
        } finally {
            this.lock.unlock()
        }
    }

    public T poll() {
        this.lock.lock()
        try {
            if (!this.queue.isEmpty()) {
                val it: Iterator<T> = this.queue.iterator()
                while (it.hasNext()) {
                    T next = it.next()
                    if (next != null) {
                        it.remove()
                        return next
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                val it2: Iterator<T> = this.lowPriorityQueue.iterator()
                while (it2.hasNext()) {
                    T next2 = it2.next()
                    if (next2 != null) {
                        it2.remove()
                        this.lock.unlock()
                        return next2
                    }
                }
            }
            this.lock.unlock()
            return null
        } finally {
            this.lock.unlock()
        }
    }

    public T poll(Long j, TimeUnit timeUnit) throws InterruptedException {
        this.lock.lock()
        do {
            try {
                T poll = poll()
                if (poll != null) {
                    this.lock.unlock()
                    return poll
                }
            } finally {
                this.lock.unlock()
            }
        } while (this.notEmpty.await(j, timeUnit))
        return null
    }

    fun put(T t) throws InterruptedException {
        add(t)
    }

     public fun remainingCapacity(): Int {
        return Integer.MAX_VALUE
    }

    public T remove() {
        T poll = poll()
        if (poll != null) {
            return poll
        }
        throw NoSuchElementException()
    }

     public fun remove(obj: Object): Boolean {
        this.lock.lock()
        try {
            return this.queue.remove(obj) | this.lowPriorityQueue.remove(obj)
        } finally {
            this.lock.unlock()
        }
    }

     public fun removeAll(collection: Collection<?>): Boolean {
        this.lock.lock()
        try {
            return this.queue.removeAll(collection) | this.lowPriorityQueue.removeAll(collection)
        } finally {
            this.lock.unlock()
        }
    }

     public fun retainAll(collection: Collection<?>): Boolean {
        this.lock.lock()
        try {
            return this.queue.retainAll(collection) | this.lowPriorityQueue.retainAll(collection)
        } finally {
            this.lock.unlock()
        }
    }

     public fun size(): Int {
        this.lock.lock()
        try {
            return this.queue.size() + this.lowPriorityQueue.size()
        } finally {
            this.lock.unlock()
        }
    }

    public T take() throws InterruptedException {
        this.lock.lock()
        while (true) {
            try {
                T poll = poll()
                if (poll != null) {
                    return poll
                }
                this.notEmpty.await()
            } finally {
                this.lock.unlock()
            }
        }
    }

    public Array<Any> toArray() {
        this.lock.lock()
        try {
            return ObjectArrays.concat(this.queue.toArray(), this.lowPriorityQueue.toArray(), Object.class)
        } finally {
            this.lock.unlock()
        }
    }

    public <T1> Array<T1> toArray(Array<T1> t1Arr) {
        this.lock.lock()
        try {
            val array: Array<T1> = toArray()
            if (array.length <= t1Arr.length) {
                Arrays.fill(t1Arr, (Object) null)
                System.arraycopy(array, 0, t1Arr, 0, array.length)
                return t1Arr
            }
            this.lock.unlock()
            return array
        } finally {
            this.lock.unlock()
        }
    }
}
