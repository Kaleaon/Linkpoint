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

/* loaded from: classes.dex */
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

    @Override // java.util.concurrent.BlockingQueue, java.util.Queue, java.util.Collection
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

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends T> collection) {
        boolean z = false;
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            z |= add(it.next());
        }
        return z;
    }

    @Override // java.util.Collection
    public void clear() {
        this.lock.lock();
        for (int i = 0; i < this.numBins; i++) {
            try {
                this.queues[i].clear();
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.clear();
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Collection
    public boolean contains(Object obj) {
        this.lock.lock();
        try {
            return this.allItems.containsKey(obj);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = true;
        try {
            Iterator<T> it = collection.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (!contains(it.next())) {
                    z = false;
                    break;
                }
            }
            return z;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> collection) {
        this.lock.lock();
        int i = 0;
        for (int i2 = 0; i2 < this.numBins; i2++) {
            try {
                i += this.queues[i2].size();
                collection.addAll(this.queues[i2]);
                this.queues[i2].clear();
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.clear();
        return i;
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> collection, int i) {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Queue
    public T element() {
        T peek = peek();
        if (peek == null) {
            throw new NoSuchElementException();
        }
        return peek;
    }

    @Override // java.util.Collection
    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.allItems.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Iterator not supported");
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Queue
    public boolean offer(T t) {
        return add(t);
    }

    @Override // java.util.concurrent.BlockingQueue
    public boolean offer(T t, long j, TimeUnit timeUnit) throws InterruptedException {
        return add(t);
    }

    @Override // java.util.Queue
    public T peek() {
        T next;
        this.lock.lock();
        for (int i = 0; i < this.numBins; i++) {
            try {
                if (!this.queues[i].isEmpty() && (next = this.queues[i].iterator().next()) != null) {
                    return next;
                }
            } finally {
                this.lock.unlock();
            }
        }
        return null;
    }

    @Override // java.util.Queue
    public T poll() {
        Iterator<T> it;
        T next;
        this.lock.lock();
        for (int i = 0; i < this.numBins; i++) {
            try {
                if (!this.queues[i].isEmpty() && (next = (it = this.queues[i].iterator()).next()) != null) {
                    it.remove();
                    this.allItems.remove(next);
                    return next;
                }
            } finally {
                this.lock.unlock();
            }
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x002b, code lost:
    
        r2.remove();
        r7.allItems.remove(r0);
        com.lumiyaviewer.lumiya.Debug.Printf("Thread %s got item with priority %d", java.lang.Thread.currentThread().getName(), java.lang.Integer.valueOf(r1));
     */
    @Override // java.util.concurrent.BlockingQueue
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public T poll(long r8, java.util.concurrent.TimeUnit r10) throws java.lang.InterruptedException {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            java.util.concurrent.locks.Lock r2 = r7.lock
            r2.lock()
            java.util.concurrent.locks.Condition r2 = r7.notEmpty     // Catch: java.lang.Throwable -> L57
            boolean r2 = r2.await(r8, r10)     // Catch: java.lang.Throwable -> L57
            if (r2 == 0) goto L4e
        Lf:
            int r2 = r7.numBins     // Catch: java.lang.Throwable -> L57
            if (r1 >= r2) goto L4e
            java.util.Set<T>[] r2 = r7.queues     // Catch: java.lang.Throwable -> L57
            r2 = r2[r1]     // Catch: java.lang.Throwable -> L57
            boolean r2 = r2.isEmpty()     // Catch: java.lang.Throwable -> L57
            if (r2 != 0) goto L54
            java.util.Set<T>[] r0 = r7.queues     // Catch: java.lang.Throwable -> L57
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L57
            java.util.Iterator r2 = r0.iterator()     // Catch: java.lang.Throwable -> L57
            java.lang.Object r0 = r2.next()     // Catch: java.lang.Throwable -> L57
            if (r0 == 0) goto L54
            r2.remove()     // Catch: java.lang.Throwable -> L57
            java.util.Map<T, java.lang.Integer> r2 = r7.allItems     // Catch: java.lang.Throwable -> L57
            r2.remove(r0)     // Catch: java.lang.Throwable -> L57
            java.lang.String r2 = "Thread %s got item with priority %d"
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch: java.lang.Throwable -> L57
            java.lang.Thread r4 = java.lang.Thread.currentThread()     // Catch: java.lang.Throwable -> L57
            java.lang.String r4 = r4.getName()     // Catch: java.lang.Throwable -> L57
            r5 = 0
            r3[r5] = r4     // Catch: java.lang.Throwable -> L57
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch: java.lang.Throwable -> L57
            r4 = 1
            r3[r4] = r1     // Catch: java.lang.Throwable -> L57
            com.lumiyaviewer.lumiya.Debug.Printf(r2, r3)     // Catch: java.lang.Throwable -> L57
        L4e:
            java.util.concurrent.locks.Lock r1 = r7.lock
            r1.unlock()
            return r0
        L54:
            int r1 = r1 + 1
            goto Lf
        L57:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r7.lock
            r1.unlock()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.utils.PriorityBinQueue.poll(long, java.util.concurrent.TimeUnit):java.lang.Object");
    }

    @Override // java.util.concurrent.BlockingQueue
    public void put(T t) throws InterruptedException {
        add(t);
    }

    @Override // java.util.concurrent.BlockingQueue
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override // java.util.Queue
    public T remove() {
        T poll = poll();
        if (poll == null) {
            throw new NoSuchElementException();
        }
        return poll;
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Collection
    public boolean remove(Object obj) {
        this.lock.lock();
        boolean z = false;
        for (int i = 0; i < this.numBins; i++) {
            try {
                z |= this.queues[i].remove(obj);
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.remove(obj);
        return z;
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<?> collection) {
        boolean z = false;
        Iterator<T> it = collection.iterator();
        while (it.hasNext()) {
            z |= remove(it.next());
        }
        return z;
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = false;
        for (int i = 0; i < this.numBins; i++) {
            try {
                z |= this.queues[i].retainAll(collection);
            } finally {
                this.lock.unlock();
            }
        }
        this.allItems.keySet().retainAll(collection);
        return z;
    }

    @Override // java.util.Collection
    public int size() {
        this.lock.lock();
        int i = 0;
        for (int i2 = 0; i2 < this.numBins; i2++) {
            try {
                i += this.queues[i2].size();
            } finally {
                this.lock.unlock();
            }
        }
        return i;
    }

    @Override // java.util.concurrent.BlockingQueue
    public T take() throws InterruptedException {
        Iterator<T> it;
        T next;
        this.lock.lock();
        while (true) {
            for (int i = 0; i < this.numBins; i++) {
                try {
                    if (!this.queues[i].isEmpty() && (next = (it = this.queues[i].iterator()).next()) != null) {
                        it.remove();
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

    @Override // java.util.Collection
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override // java.util.Collection
    public <T> T[] toArray(T[] tArr) {
        throw new UnsupportedOperationException();
    }

    public void updatePriority(T t) {
        int intValue;
        this.lock.lock();
        try {
            if (t instanceof HasPriority) {
                int priority = ((HasPriority) t).getPriority();
                int i = priority >= 0 ? priority > this.numBins + (-1) ? this.numBins - 1 : priority : 0;
                Integer num = this.allItems.get(t);
                if (num != null && (intValue = num.intValue()) != i && this.queues[intValue].remove(t)) {
                    this.queues[i].add(t);
                    this.allItems.put(t, Integer.valueOf(i));
                }
            }
        } finally {
            this.lock.unlock();
        }
    }
}
