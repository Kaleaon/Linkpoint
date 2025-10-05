package com.linkpoint.res.collections

import com.linkpoint.Debug
import com.linkpoint.utils.HasPriority
import java.util.ArrayList
import java.util.Arrays
import java.util.Collection
import java.util.Iterator
import java.util.NoSuchElementException
import java.util.Queue
import java.util.SortedMap
import java.util.TreeMap
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class PriorityBinQueue<T> : BlockingQueue<T> {
    private val Lock lock = ReentrantLock()
    private val Condition notEmpty = this.lock.newCondition()
    private val QueueFactory<T> queueFactory
    private val SortedMap<Integer, Queue<T>> queues = TreeMap()

    interface QueueFactory<T> {
        Queue<T> getQueue()
    }

    public PriorityBinQueue(QueueFactory<T> queueFactory2) {
        this.queueFactory = queueFactory2
    }

    private Int getPriority(Object obj) {
        if (obj instanceof HasPriority) {
            return ((HasPriority) obj).getPriority()
        }
        return 0
    }

    public Boolean add(T t) {
        this.lock.lock()
        try {
            Int priority = getPriority(t)
            Debug.Printf("PriorityBinQueue: added %s with prio %d", t.toString(), Integer.valueOf(priority))
            Queue<T> queue = (Queue) this.queues.get(Integer.valueOf(priority))
            if (queue == null) {
                queue = this.queueFactory.getQueue()
                this.queues.put(Integer.valueOf(priority), queue)
            }
            Boolean add = queue.add(t)
            this.notEmpty.signalAll()
            return add
        } finally {
            this.lock.unlock()
        }
    }

    public Boolean addAll(Collection<? : T> collection) {
        this.lock.lock()
        Boolean z = false
        try {
            Iterator<T> it = collection.iterator()
            while (true) {
                Boolean z2 = z
                if (!it.hasNext()) {
                    return z2
                }
                T next = it.next()
                Int priority = getPriority(next)
                Queue<T> queue = (Queue) this.queues.get(Integer.valueOf(priority))
                if (queue == null) {
                    queue = this.queueFactory.getQueue()
                    this.queues.put(Integer.valueOf(priority), queue)
                }
                z = queue.add(next) | z2
                this.notEmpty.signalAll()
            }
        } finally {
            this.lock.unlock()
        }
    }

    public Unit clear() {
        this.lock.lock()
        try {
            this.queues.clear()
        } finally {
            this.lock.unlock()
        }
    }

    public Boolean contains(Object obj) {
        this.lock.lock()
        try {
            Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(obj)))
            if (queue != null) {
                return queue.contains(obj)
            }
            this.lock.unlock()
            return false
        } finally {
            this.lock.unlock()
        }
    }

    public Boolean containsAll(Collection<?> collection) {
        this.lock.lock()
        try {
            Iterator<T> it = collection.iterator()
            while (true) {
                if (!it.hasNext()) {
                    z = true
                    break
                }
                T next = it.next()
                Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(next)))
                if (queue != null && !queue.contains(next)) {
                    z = false
                    break
                }
            }
            return z
        } finally {
            this.lock.unlock()
        }
    }

    public Int drainTo(Collection<? super T> collection) {
        this.lock.lock()
        Int i = 0
        try {
            for (Queue queue : this.queues.values()) {
                while (true) {
                    Object poll = queue.poll()
                    if (poll != null) {
                        collection.add(poll)
                        i++
                    }
                }
            }
            this.queues.clear()
            return i
        } finally {
            this.lock.unlock()
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x002c A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Int drainTo(java.util.Collection<? super T> r5, Int r6) {
        /*
            r4 = this
            java.util.concurrent.locks.Lock r0 = r4.lock
            r0.lock()
            r1 = 0
            java.util.SortedMap<java.lang.Integer, java.util.Queue<T>> r0 = r4.queues     // Catch:{ all -> 0x0033 }
            java.util.Collection r0 = r0.values()     // Catch:{ all -> 0x0033 }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ all -> 0x0033 }
        L_0x0010:
            Boolean r0 = r2.hasNext()     // Catch:{ all -> 0x0033 }
            if (r0 == 0) goto L_0x003a
            java.lang.Object r0 = r2.next()     // Catch:{ all -> 0x0033 }
            java.util.Queue r0 = (java.util.Queue) r0     // Catch:{ all -> 0x0033 }
        L_0x001c:
            java.lang.Object r3 = r0.poll()     // Catch:{ all -> 0x0033 }
            if (r3 == 0) goto L_0x002a
            if (r1 >= r6) goto L_0x002a
            r5.add(r3)     // Catch:{ all -> 0x0033 }
            Int r1 = r1 + 1
            goto L_0x001c
        L_0x002a:
            if (r1 < r6) goto L_0x0010
            r0 = r1
        L_0x002d:
            java.util.concurrent.locks.Lock r1 = r4.lock
            r1.unlock()
            return r0
        L_0x0033:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r4.lock
            r1.unlock()
            throw r0
        L_0x003a:
            r0 = r1
            goto L_0x002d
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.collections.PriorityBinQueue.drainTo(java.util.Collection, Int):Int")
    }

    public T element() {
        T peek = peek()
        if (peek != null) {
            return peek
        }
        throw NoSuchElementException()
    }

    public Boolean isEmpty() {
        this.lock.lock()
        try {
            Iterator<T> it = this.queues.values().iterator()
            while (true) {
                if (it.hasNext()) {
                    if (!((Queue) it.next()).isEmpty()) {
                        z = false
                        break
                    }
                } else {
                    z = true
                    break
                }
            }
            return z
        } finally {
            this.lock.unlock()
        }
    }

    public Iterator<T> iterator() {
        throw UnsupportedOperationException("Iterator not supported")
    }

    public Boolean offer(T t) {
        return add(t)
    }

    public Boolean offer(T t, Long j, TimeUnit timeUnit) throws InterruptedException {
        return add(t)
    }

    public T peek() {
        this.lock.lock()
        try {
            for (Queue queue : this.queues.values()) {
                if (!queue.isEmpty()) {
                    for (T next : queue) {
                        if (next != null) {
                            return next
                        }
                    }
                    continue
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
            for (Queue it : this.queues.values()) {
                Iterator it2 = it.iterator()
                while (true) {
                    if (it2.hasNext()) {
                        T next = it2.next()
                        if (next != null) {
                            it2.remove()
                            return next
                        }
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

    public Unit put(T t) throws InterruptedException {
        add(t)
    }

    public Int remainingCapacity() {
        return Integer.MAX_VALUE
    }

    public T remove() {
        T poll = poll()
        if (poll != null) {
            return poll
        }
        throw NoSuchElementException()
    }

    public Boolean remove(Object obj) {
        this.lock.lock()
        try {
            Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(obj)))
            if (queue != null) {
                return queue.remove(obj)
            }
            this.lock.unlock()
            return false
        } finally {
            this.lock.unlock()
        }
    }

    public Boolean removeAll(Collection<?> collection) {
        this.lock.lock()
        Boolean z = false
        try {
            for (T next : collection) {
                Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(next)))
                z = queue != null ? queue.remove(next) | z : z
            }
            return z
        } finally {
            this.lock.unlock()
        }
    }

    public Boolean retainAll(Collection<?> collection) {
        this.lock.lock()
        Boolean z = false
        try {
            Iterator<T> it = this.queues.values().iterator()
            while (true) {
                Boolean z2 = z
                if (!it.hasNext()) {
                    return z2
                }
                z = ((Queue) it.next()).retainAll(collection) | z2
            }
        } finally {
            this.lock.unlock()
        }
    }

    public Int size() {
        this.lock.lock()
        Int i = 0
        try {
            Iterator<T> it = this.queues.values().iterator()
            while (true) {
                Int i2 = i
                if (!it.hasNext()) {
                    return i2
                }
                i = ((Queue) it.next()).size() + i2
            }
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

    public Object[] toArray() {
        Int i = 0
        this.lock.lock()
        try {
            ArrayList<Object[]> arrayList = ArrayList<>()
            Int i2 = 0
            for (Queue array : this.queues.values()) {
                Object[] array2 = array.toArray()
                arrayList.add(array2)
                i2 = array2.length + i2
            }
            Object[] objArr = Object[i2]
            for (Object[] objArr2 : arrayList) {
                System.arraycopy(objArr2, 0, objArr, i, objArr2.length)
                i = objArr2.length + i
            }
            arrayList.clear()
            return objArr
        } finally {
            this.lock.unlock()
        }
    }

    public <T1> T1[] toArray(T1[] t1Arr) {
        Int i = 0
        this.lock.lock()
        try {
            ArrayList<Object[]> arrayList = ArrayList<>()
            Int i2 = 0
            for (Queue array : this.queues.values()) {
                Object[] array2 = array.toArray()
                arrayList.add(array2)
                i2 = array2.length + i2
            }
            if (t1Arr.length >= i2) {
                Arrays.fill(t1Arr, (Object) null)
            } else {
                t1Arr = Object[i2]
            }
            for (Object[] objArr : arrayList) {
                System.arraycopy(objArr, 0, t1Arr, i, objArr.length)
                i = objArr.length + i
            }
            arrayList.clear()
            return t1Arr
        } finally {
            this.lock.unlock()
        }
    }
}
