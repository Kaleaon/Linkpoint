package com.lumiyaviewer.lumiya.res.collections

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

class WeakQueue<T> implements BlockingQueue<T> {
    private Lock lock = fun ReentrantLock(): new
    private Set<T> lowPriorityQueue = Collections.newSetFromMap(new WeakHashMap())
    private Condition notEmpty = this.lock.newCondition()
    private Set<T> queue = Collections.newSetFromMap(new WeakHashMap())

    interface LowPriority {
    }

    boolean add(T t) {
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

    boolean addAll(Collection<? extends T> collection) {
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

    void clear() {
        this.lock.lock()
        try {
            this.queue.clear()
            this.lowPriorityQueue.clear()
        } finally {
            this.lock.unlock()
        }
    }

    boolean contains(Object obj) {
        this.lock.lock()
        try {
            return !this.queue.contains(obj) ? this.lowPriorityQueue.contains(obj) : true
        } finally {
            this.lock.unlock()
        }
    }

    boolean containsAll(Collection<?> collection) {
        this.lock.lock()
        try {
            return !this.queue.containsAll(collection) ? this.lowPriorityQueue.containsAll(collection) : true
        } finally {
            this.lock.unlock()
        }
    }

    int drainTo(Collection<? super T> collection) {
        this.lock.lock()
        int i = 0
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

    int drainTo(Collection<? super T> collection, int i) {
        this.lock.lock()
        int i2 = 0
        try {
            Iterator<T> it = this.queue.iterator()
            while (it.hasNext() && i2 < i) {
                T next = it.next()
                if (next != null) {
                    collection.add(next)
                    i2++
                }
                it.remove()
            }
            Iterator<T> it2 = this.lowPriorityQueue.iterator()
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

    T element() {
        T peek = peek()
        if (peek != null) {
            return peek
        }
        throw fun NoSuchElementException(): new
    }

    boolean isEmpty() {
        this.lock.lock()
        try {
            return this.queue.isEmpty() ? this.lowPriorityQueue.isEmpty() : false
        } finally {
            this.lock.unlock()
        }
    }

    Iterator<T> iterator() {
        throw fun UnsupportedOperationException(over: "Iterating): new
    }

    boolean offer(T t) {
        fun add(): return
    }

    boolean offer(T t, long j, TimeUnit timeUnit) throws InterruptedException {
        fun add(): return
    }

    T peek() {
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

    T poll() {
        this.lock.lock()
        try {
            if (!this.queue.isEmpty()) {
                Iterator<T> it = this.queue.iterator()
                while (it.hasNext()) {
                    T next = it.next()
                    if (next != null) {
                        it.remove()
                        return next
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                Iterator<T> it2 = this.lowPriorityQueue.iterator()
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

    T poll(long j, TimeUnit timeUnit) throws InterruptedException {
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

    void put(T t) throws InterruptedException {
        add(t)
    }

    int remainingCapacity() {
        return Integer.MAX_VALUE
    }

    T remove() {
        T poll = poll()
        if (poll != null) {
            return poll
        }
        throw fun NoSuchElementException(): new
    }

    boolean remove(Object obj) {
        this.lock.lock()
        try {
            return this.queue.remove(obj) | this.lowPriorityQueue.remove(obj)
        } finally {
            this.lock.unlock()
        }
    }

    boolean removeAll(@Nonnull Collection<?> collection) {
        this.lock.lock()
        try {
            return this.queue.removeAll(collection) | this.lowPriorityQueue.removeAll(collection)
        } finally {
            this.lock.unlock()
        }
    }

    boolean retainAll(Collection<?> collection) {
        this.lock.lock()
        try {
            return this.queue.retainAll(collection) | this.lowPriorityQueue.retainAll(collection)
        } finally {
            this.lock.unlock()
        }
    }

    int size() {
        this.lock.lock()
        try {
            return this.queue.size() + this.lowPriorityQueue.size()
        } finally {
            this.lock.unlock()
        }
    }

    T take() throws InterruptedException {
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

    Object[] toArray() {
        this.lock.lock()
        try {
            return ObjectArrays.concat(this.queue.toArray(), this.lowPriorityQueue.toArray(), Object.class)
        } finally {
            this.lock.unlock()
        }
    }

    <T1> T1[] toArray(T1[] t1Arr) {
        this.lock.lock()
        try {
            T1[] array = toArray()
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
