package com.lumiyaviewer.lumiya.res.collections;

import com.google.common.collect.ObjectArrays;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class WeakQueue<T> implements BlockingQueue<T> {
    private final Set<T> queue = Collections.newSetFromMap(new WeakHashMap());
    private final Set<T> lowPriorityQueue = Collections.newSetFromMap(new WeakHashMap());
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();

    /* loaded from: classes.dex */
    public interface LowPriority {
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Queue, java.util.Collection
    public boolean add(T t) {
        if (t == null) {
            return false;
        }
        this.lock.lock();
        try {
            if (t instanceof LowPriority) {
                this.lowPriorityQueue.add(t);
            } else {
                this.queue.add(t);
            }
            this.notEmpty.signalAll();
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends T> collection) {
        this.lock.lock();
        try {
            for (T t : collection) {
                if (t instanceof LowPriority) {
                    this.lowPriorityQueue.add(t);
                } else {
                    this.queue.add(t);
                }
            }
            this.notEmpty.signalAll();
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public void clear() {
        this.lock.lock();
        try {
            this.queue.clear();
            this.lowPriorityQueue.clear();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Collection
    public boolean contains(Object obj) {
        this.lock.lock();
        try {
            return !this.queue.contains(obj) ? this.lowPriorityQueue.contains(obj) : true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        this.lock.lock();
        try {
            return !this.queue.containsAll(collection) ? this.lowPriorityQueue.containsAll(collection) : true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> collection) {
        this.lock.lock();
        int i = 0;
        try {
            for (T t : this.queue) {
                if (t != null) {
                    collection.add(t);
                    i++;
                }
            }
            this.queue.clear();
            for (T t2 : this.lowPriorityQueue) {
                if (t2 != null) {
                    collection.add(t2);
                    i++;
                }
            }
            this.lowPriorityQueue.clear();
            return i;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> collection, int i) {
        this.lock.lock();
        int i2 = 0;
        try {
            Iterator<T> it = this.queue.iterator();
            while (it.hasNext() && i2 < i) {
                Object obj = (T) it.next();
                if (obj != null) {
                    collection.add(obj);
                    i2++;
                }
                it.remove();
            }
            Iterator<T> it2 = this.lowPriorityQueue.iterator();
            while (it2.hasNext() && i2 < i) {
                Object obj2 = (T) it2.next();
                if (obj2 != null) {
                    collection.add(obj2);
                    i2++;
                }
                it2.remove();
            }
            return i2;
        } finally {
            this.lock.unlock();
        }
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
            return this.queue.isEmpty() ? this.lowPriorityQueue.isEmpty() : false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        throw new IllegalStateException("Iterating over WeakQueue is not supported");
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
        this.lock.lock();
        try {
            if (!this.queue.isEmpty()) {
                for (T t : this.queue) {
                    if (t != null) {
                        return t;
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                for (T t2 : this.lowPriorityQueue) {
                    if (t2 != null) {
                        return t2;
                    }
                }
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Queue
    public T poll() {
        this.lock.lock();
        try {
            if (!this.queue.isEmpty()) {
                Iterator<T> it = this.queue.iterator();
                while (it.hasNext()) {
                    T next = it.next();
                    if (next != null) {
                        it.remove();
                        return next;
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                Iterator<T> it2 = this.lowPriorityQueue.iterator();
                while (it2.hasNext()) {
                    T next2 = it2.next();
                    if (next2 != null) {
                        it2.remove();
                        return next2;
                    }
                }
            }
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public T poll(long j, TimeUnit timeUnit) throws InterruptedException {
        this.lock.lock();
        do {
            try {
                T poll = poll();
                if (poll != null) {
                    return poll;
                }
            } finally {
                this.lock.unlock();
            }
        } while (this.notEmpty.await(j, timeUnit));
        return null;
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
        try {
            return this.queue.remove(obj) | this.lowPriorityQueue.remove(obj);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean removeAll(@Nonnull Collection<?> collection) {
        this.lock.lock();
        try {
            return this.queue.removeAll(collection) | this.lowPriorityQueue.removeAll(collection);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> collection) {
        this.lock.lock();
        try {
            return this.queue.retainAll(collection) | this.lowPriorityQueue.retainAll(collection);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public int size() {
        this.lock.lock();
        try {
            return this.queue.size() + this.lowPriorityQueue.size();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public T take() throws InterruptedException {
        this.lock.lock();
        while (true) {
            try {
                T poll = poll();
                if (poll != null) {
                    return poll;
                }
                this.notEmpty.await();
            } finally {
                this.lock.unlock();
            }
        }
    }

    @Override // java.util.Collection
    public Object[] toArray() {
        this.lock.lock();
        try {
            return ObjectArrays.concat(this.queue.toArray(), this.lowPriorityQueue.toArray(), Object.class);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public <T1> T1[] toArray(T1[] t1Arr) {
        this.lock.lock();
        try {
            T1[] t1Arr2 = (T1[]) toArray();
            if (t1Arr2.length <= t1Arr.length) {
                Arrays.fill(t1Arr, (Object) null);
                System.arraycopy(t1Arr2, 0, t1Arr, 0, t1Arr2.length);
                return t1Arr;
            }
            return t1Arr2;
        } finally {
            this.lock.unlock();
        }
    }
}
