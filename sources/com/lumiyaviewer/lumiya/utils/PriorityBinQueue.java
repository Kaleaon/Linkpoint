package com.lumiyaviewer.lumiya.utils;

import com.lumiyaviewer.lumiya.Debug;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityBinQueue<T> implements BlockingQueue<T> {
    private final Map<T, Integer> allItems = new IdentityHashMap();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();
    private final int numBins;
    private final Set<T>[] queues;

    public PriorityBinQueue(int i) {
        this.numBins = i;
        this.queues = new Set[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.queues[i2] = new HashSet();
        }
    }

    public boolean add(T t) {
        int i;
        this.lock.lock();
        try {
            if (t instanceof HasPriority) {
                i = ((HasPriority) t).getPriority();
                if (i < 0) {
                    i = 0;
                } else if (i > this.numBins - 1) {
                    i = this.numBins - 1;
                }
            } else {
                Debug.Printf("Thread %s added item %s without a priority", Thread.currentThread().getName(), t.toString());
                i = 0;
            }
            boolean add = this.queues[i].add(t);
            this.allItems.put(t, Integer.valueOf(i));
            Debug.Printf("Thread %s added item to the queue, bin %d/%d", Thread.currentThread().getName(), Integer.valueOf(i), Integer.valueOf(this.numBins));
            this.notEmpty.signalAll();
            return add;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean addAll(Collection<? extends T> collection) {
        boolean z = false;
        for (T add : collection) {
            z |= add(add);
        }
        return z;
    }

    public void clear() {
        this.lock.lock();
        int i = 0;
        while (i < this.numBins) {
            try {
                this.queues[i].clear();
                i++;
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.clear();
    }

    public boolean contains(Object obj) {
        this.lock.lock();
        try {
            return this.allItems.containsKey(obj);
        } finally {
            this.lock.unlock();
        }
    }

    public boolean containsAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = true;
        try {
            Iterator<T> it = collection.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (!contains(it.next())) {
                        z = false;
                        break;
                    }
                } else {
                    break;
                }
            }
            return z;
        } finally {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super T> collection) {
        int i = 0;
        this.lock.lock();
        int i2 = 0;
        while (i < this.numBins) {
            try {
                i2 += this.queues[i].size();
                collection.addAll(this.queues[i]);
                this.queues[i].clear();
                i++;
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.clear();
        return i2;
    }

    public int drainTo(Collection<? super T> collection, int i) {
        throw new UnsupportedOperationException();
    }

    public T element() {
        T peek = peek();
        if (peek != null) {
            return peek;
        }
        throw new NoSuchElementException();
    }

    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.allItems.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Iterator not supported");
    }

    public boolean offer(T t) {
        return add(t);
    }

    public boolean offer(T t, long j, TimeUnit timeUnit) throws InterruptedException {
        return add(t);
    }

    public T peek() {
        T next;
        this.lock.lock();
        int i = 0;
        while (i < this.numBins) {
            try {
                if (!this.queues[i].isEmpty() && (next = this.queues[i].iterator().next()) != null) {
                    return next;
                }
                i++;
            } finally {
                this.lock.unlock();
            }
        }
        this.lock.unlock();
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0015, code lost:
        r1 = r4.queues[r0].iterator();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public T poll() {
        /*
            r4 = this;
            r3 = 0
            java.util.concurrent.locks.Lock r0 = r4.lock
            r0.lock()
            r0 = 0
        L_0x0007:
            int r1 = r4.numBins     // Catch:{ all -> 0x003a }
            if (r0 >= r1) goto L_0x0034
            java.util.Set<T>[] r1 = r4.queues     // Catch:{ all -> 0x003a }
            r1 = r1[r0]     // Catch:{ all -> 0x003a }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x003a }
            if (r1 != 0) goto L_0x0031
            java.util.Set<T>[] r1 = r4.queues     // Catch:{ all -> 0x003a }
            r1 = r1[r0]     // Catch:{ all -> 0x003a }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x003a }
            java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x003a }
            if (r2 == 0) goto L_0x0031
            r1.remove()     // Catch:{ all -> 0x003a }
            java.util.Map<T, java.lang.Integer> r0 = r4.allItems     // Catch:{ all -> 0x003a }
            r0.remove(r2)     // Catch:{ all -> 0x003a }
            java.util.concurrent.locks.Lock r0 = r4.lock
            r0.unlock()
            return r2
        L_0x0031:
            int r0 = r0 + 1
            goto L_0x0007
        L_0x0034:
            java.util.concurrent.locks.Lock r0 = r4.lock
            r0.unlock()
            return r3
        L_0x003a:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r4.lock
            r1.unlock()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.utils.PriorityBinQueue.poll():java.lang.Object");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001d, code lost:
        r2 = r7.queues[r1].iterator();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public T poll(long r8, java.util.concurrent.TimeUnit r10) throws java.lang.InterruptedException {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            java.util.concurrent.locks.Lock r2 = r7.lock
            r2.lock()
            java.util.concurrent.locks.Condition r2 = r7.notEmpty     // Catch:{ all -> 0x0057 }
            boolean r2 = r2.await(r8, r10)     // Catch:{ all -> 0x0057 }
            if (r2 == 0) goto L_0x004e
        L_0x000f:
            int r2 = r7.numBins     // Catch:{ all -> 0x0057 }
            if (r1 >= r2) goto L_0x004e
            java.util.Set<T>[] r2 = r7.queues     // Catch:{ all -> 0x0057 }
            r2 = r2[r1]     // Catch:{ all -> 0x0057 }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0057 }
            if (r2 != 0) goto L_0x0054
            java.util.Set<T>[] r0 = r7.queues     // Catch:{ all -> 0x0057 }
            r0 = r0[r1]     // Catch:{ all -> 0x0057 }
            java.util.Iterator r2 = r0.iterator()     // Catch:{ all -> 0x0057 }
            java.lang.Object r0 = r2.next()     // Catch:{ all -> 0x0057 }
            if (r0 == 0) goto L_0x0054
            r2.remove()     // Catch:{ all -> 0x0057 }
            java.util.Map<T, java.lang.Integer> r2 = r7.allItems     // Catch:{ all -> 0x0057 }
            r2.remove(r0)     // Catch:{ all -> 0x0057 }
            java.lang.String r2 = "Thread %s got item with priority %d"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0057 }
            java.lang.Thread r4 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0057 }
            java.lang.String r4 = r4.getName()     // Catch:{ all -> 0x0057 }
            r5 = 0
            r3[r5] = r4     // Catch:{ all -> 0x0057 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x0057 }
            r4 = 1
            r3[r4] = r1     // Catch:{ all -> 0x0057 }
            com.lumiyaviewer.lumiya.Debug.Printf(r2, r3)     // Catch:{ all -> 0x0057 }
        L_0x004e:
            java.util.concurrent.locks.Lock r1 = r7.lock
            r1.unlock()
            return r0
        L_0x0054:
            int r1 = r1 + 1
            goto L_0x000f
        L_0x0057:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r7.lock
            r1.unlock()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.utils.PriorityBinQueue.poll(long, java.util.concurrent.TimeUnit):java.lang.Object");
    }

    public void put(T t) throws InterruptedException {
        add(t);
    }

    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    public T remove() {
        T poll = poll();
        if (poll != null) {
            return poll;
        }
        throw new NoSuchElementException();
    }

    public boolean remove(Object obj) {
        int i = 0;
        this.lock.lock();
        boolean z = false;
        while (i < this.numBins) {
            try {
                z |= this.queues[i].remove(obj);
                i++;
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.remove(obj);
        return z;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean z = false;
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            z |= remove(it.next());
        }
        return z;
    }

    public boolean retainAll(Collection<?> collection) {
        int i = 0;
        this.lock.lock();
        boolean z = false;
        while (i < this.numBins) {
            try {
                z |= this.queues[i].retainAll(collection);
                i++;
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.keySet().retainAll(collection);
        return z;
    }

    public int size() {
        int i = 0;
        this.lock.lock();
        int i2 = 0;
        while (i < this.numBins) {
            try {
                i2 += this.queues[i].size();
                i++;
            } finally {
                this.lock.unlock();
            }
        }
        return i2;
    }

    public T take() throws InterruptedException {
        Iterator<T> it;
        T next;
        this.lock.lock();
        while (true) {
            int i = 0;
            while (i < this.numBins) {
                try {
                    if (this.queues[i].isEmpty() || (next = it.next()) == null) {
                        i++;
                    } else {
                        (it = this.queues[i].iterator()).remove();
                        this.allItems.remove(next);
                        Debug.Printf("Thread %s got item with priority %d", Thread.currentThread().getName(), Integer.valueOf(i));
                        return next;
                    }
                } finally {
                    this.lock.unlock();
                }
            }
            Debug.Printf("Thread %s waiting on the queue", Thread.currentThread().getName());
            this.notEmpty.await();
        }
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    public <T> T[] toArray(T[] tArr) {
        throw new UnsupportedOperationException();
    }

    public void updatePriority(T t) {
        int intValue;
        int i = 0;
        this.lock.lock();
        try {
            if (t instanceof HasPriority) {
                int priority = ((HasPriority) t).getPriority();
                if (priority >= 0) {
                    i = priority > this.numBins + -1 ? this.numBins - 1 : priority;
                }
                Integer num = this.allItems.get(t);
                if (!(num == null || (intValue = num.intValue()) == i || !this.queues[intValue].remove(t))) {
                    this.queues[i].add(t);
                    this.allItems.put(t, Integer.valueOf(i));
                }
            }
        } finally {
            this.lock.unlock();
        }
    }
}
