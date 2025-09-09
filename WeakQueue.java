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

public class WeakQueue<T> implements BlockingQueue<T> {
    private final Lock lock = new ReentrantLock();
    private final Set<T> lowPriorityQueue = Collections.newSetFromMap(new WeakHashMap());
    private final Condition notEmpty = this.lock.newCondition();
    private final Set<T> queue = Collections.newSetFromMap(new WeakHashMap());

    public interface LowPriority {
    }

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

    public boolean addAll(Collection<? extends T> collection) {
        this.lock.lock();
        try {
            for (Object next : collection) {
                if (next instanceof LowPriority) {
                    this.lowPriorityQueue.add(next);
                } else {
                    this.queue.add(next);
                }
            }
            this.notEmpty.signalAll();
            return true;
        } finally {
            this.lock.unlock();
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            this.queue.clear();
            this.lowPriorityQueue.clear();
        } finally {
            this.lock.unlock();
        }
    }

    public boolean contains(Object obj) {
        this.lock.lock();
        try {
            boolean contains = !this.queue.contains(obj) ? this.lowPriorityQueue.contains(obj) : true;
            this.lock.unlock();
            return contains;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public boolean containsAll(Collection<?> collection) {
        this.lock.lock();
        try {
            boolean containsAll = !this.queue.containsAll(collection) ? this.lowPriorityQueue.containsAll(collection) : true;
            this.lock.unlock();
            return containsAll;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super T> collection) {
        this.lock.lock();
        int i = 0;
        try {
            for (Object next : this.queue) {
                if (next != null) {
                    collection.add(next);
                    i++;
                }
            }
            this.queue.clear();
            for (Object next2 : this.lowPriorityQueue) {
                if (next2 != null) {
                    collection.add(next2);
                    i++;
                }
            }
            this.lowPriorityQueue.clear();
            return i;
        } finally {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super T> collection, int i) {
        this.lock.lock();
        int i2 = 0;
        try {
            Object next;
            Iterator it = this.queue.iterator();
            while (it.hasNext() && i2 < i) {
                next = it.next();
                if (next != null) {
                    collection.add(next);
                    i2++;
                }
                it.remove();
            }
            it = this.lowPriorityQueue.iterator();
            while (it.hasNext() && i2 < i) {
                next = it.next();
                if (next != null) {
                    collection.add(next);
                    i2++;
                }
                it.remove();
            }
            this.lock.unlock();
            return i2;
        } catch (Throwable th) {
            this.lock.unlock();
        }
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
            boolean isEmpty = this.queue.isEmpty() ? this.lowPriorityQueue.isEmpty() : false;
            this.lock.unlock();
            return isEmpty;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Iterating over WeakQueue is not supported");
    }

    public boolean offer(T t) {
        return add(t);
    }

    public boolean offer(T t, long j, TimeUnit timeUnit) throws InterruptedException {
        return add(t);
    }

    public T peek() {
        this.lock.lock();
        T t;
        try {
            if (!this.queue.isEmpty()) {
                for (T t2 : this.queue) {
                    if (t2 != null) {
                        return t2;
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                for (T t22 : this.lowPriorityQueue) {
                    if (t22 != null) {
                        this.lock.unlock();
                        return t22;
                    }
                }
            }
            this.lock.unlock();
            return null;
        } finally {
            t22 = this.lock;
            t22.unlock();
        }
    }

    public T poll() {
        this.lock.lock();
        T next;
        try {
            Iterator it;
            if (!this.queue.isEmpty()) {
                it = this.queue.iterator();
                while (it.hasNext()) {
                    next = it.next();
                    if (next != null) {
                        it.remove();
                        return next;
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                it = this.lowPriorityQueue.iterator();
                while (it.hasNext()) {
                    next = it.next();
                    if (next != null) {
                        it.remove();
                        this.lock.unlock();
                        return next;
                    }
                }
            }
            this.lock.unlock();
            return null;
        } finally {
            next = this.lock;
            next.unlock();
        }
    }

    public T poll(long j, TimeUnit timeUnit) throws InterruptedException {
        T t = null;
        this.lock.lock();
        while (true) {
            try {
                T poll = poll();
                if (poll == null) {
                    if (!this.notEmpty.await(j, timeUnit)) {
                        break;
                    }
                }
                this.lock.unlock();
                return poll;
            } finally {
                t = this.lock;
                t.unlock();
            }
        }
        return t;
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
        this.lock.lock();
        try {
            boolean remove = this.queue.remove(obj) | this.lowPriorityQueue.remove(obj);
            return remove;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean removeAll(@Nonnull Collection<?> collection) {
        this.lock.lock();
        try {
            boolean removeAll = this.queue.removeAll(collection) | this.lowPriorityQueue.removeAll(collection);
            return removeAll;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean retainAll(Collection<?> collection) {
        this.lock.lock();
        try {
            boolean retainAll = this.queue.retainAll(collection) | this.lowPriorityQueue.retainAll(collection);
            return retainAll;
        } finally {
            this.lock.unlock();
        }
    }

    public int size() {
        this.lock.lock();
        try {
            int size = this.queue.size() + this.lowPriorityQueue.size();
            return size;
        } finally {
            this.lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        T poll;
        this.lock.lock();
        while (true) {
            try {
                poll = poll();
                if (poll != null) {
                    break;
                }
                this.notEmpty.await();
            } finally {
                this.lock.unlock();
            }
        }
        return poll;
    }

    public Object[] toArray() {
        this.lock.lock();
        try {
            Object[] concat = ObjectArrays.concat(this.queue.toArray(), this.lowPriorityQueue.toArray(), Object.class);
            return concat;
        } finally {
            this.lock.unlock();
        }
    }

    public <T1> T1[] toArray(T1[] t1Arr) {
        this.lock.lock();
        try {
            Object toArray = toArray();
            if (toArray.length <= t1Arr.length) {
                Arrays.fill(t1Arr, null);
                System.arraycopy(toArray, 0, t1Arr, 0, toArray.length);
                return t1Arr;
            }
            this.lock.unlock();
            return toArray;
        } finally {
            this.lock.unlock();
        }
    }
}
