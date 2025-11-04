// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.collections;

import java.util.Arrays;
import com.google.common.collect.ObjectArrays;
import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Condition;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.BlockingQueue;

public class WeakQueue<T> implements BlockingQueue<T>
{
    private final Lock lock;
    private final Set<T> lowPriorityQueue;
    private final Condition notEmpty;
    private final Set<T> queue;
    
    public WeakQueue() {
        this.queue = Collections.newSetFromMap(new WeakHashMap<T, Boolean>());
        this.lowPriorityQueue = Collections.newSetFromMap(new WeakHashMap<T, Boolean>());
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
    }
    
    @Override
    public boolean add(final T t) {
        if (t == null) {
            return false;
        }
        this.lock.lock();
        try {
            if (t instanceof LowPriority) {
                this.lowPriorityQueue.add(t);
            }
            else {
                this.queue.add(t);
            }
            this.notEmpty.signalAll();
            return true;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        while (true) {
            this.lock.lock();
            while (true) {
                Label_0065: {
                    try {
                        for (final T next : collection) {
                            if (!(next instanceof LowPriority)) {
                                break Label_0065;
                            }
                            this.lowPriorityQueue.add(next);
                        }
                        break;
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
                final T t;
                this.queue.add(t);
                continue;
            }
        }
        this.notEmpty.signalAll();
        this.lock.unlock();
        return true;
    }
    
    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.queue.clear();
            this.lowPriorityQueue.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean contains(final Object o) {
        this.lock.lock();
        try {
            return this.queue.contains(o) || this.lowPriorityQueue.contains(o);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        this.lock.lock();
        try {
            return this.queue.containsAll(collection) || this.lowPriorityQueue.containsAll(collection);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int drainTo(final Collection<? super T> collection) {
        this.lock.lock();
        int n = 0;
        try {
            for (final Object next : this.queue) {
                if (next != null) {
                    collection.add((Object)next);
                    ++n;
                }
            }
            this.queue.clear();
            for (final Object next2 : this.lowPriorityQueue) {
                if (next2 != null) {
                    collection.add((Object)next2);
                    ++n;
                }
            }
            this.lowPriorityQueue.clear();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int drainTo(final Collection<? super T> collection, final int n) {
        this.lock.lock();
        int n2 = 0;
        try {
            int n3;
            for (Iterator<T> iterator = this.queue.iterator(); iterator.hasNext() && n2 < n; n2 = n3) {
                final Object next = iterator.next();
                n3 = n2;
                if (next != null) {
                    collection.add((Object)next);
                    n3 = n2 + 1;
                }
                iterator.remove();
            }
        }
        finally {
            this.lock.unlock();
        }
        int n4;
        for (Iterator<T> iterator2 = this.lowPriorityQueue.iterator(); iterator2.hasNext() && n2 < n; n2 = n4) {
            final Object next2 = iterator2.next();
            n4 = n2;
            if (next2 != null) {
                final Collection<Object> collection2;
                collection2.add(next2);
                n4 = n2 + 1;
            }
            iterator2.remove();
        }
        this.lock.unlock();
        return n2;
    }
    
    @Override
    public T element() {
        final T peek = this.peek();
        if (peek == null) {
            throw new NoSuchElementException();
        }
        return peek;
    }
    
    @Override
    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.queue.isEmpty() && this.lowPriorityQueue.isEmpty();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Iterating over WeakQueue is not supported");
    }
    
    @Override
    public boolean offer(final T t) {
        return this.add(t);
    }
    
    @Override
    public boolean offer(final T t, final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.add(t);
    }
    
    @Override
    public T peek() {
        this.lock.lock();
        try {
            if (!this.queue.isEmpty()) {
                for (final T next : this.queue) {
                    if (next != null) {
                        return next;
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                for (final T next2 : this.lowPriorityQueue) {
                    if (next2 != null) {
                        return next2;
                    }
                }
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T poll() {
        this.lock.lock();
        try {
            if (!this.queue.isEmpty()) {
                final Iterator<T> iterator = this.queue.iterator();
                while (iterator.hasNext()) {
                    final T next = iterator.next();
                    if (next != null) {
                        iterator.remove();
                        return next;
                    }
                }
            }
            if (!this.lowPriorityQueue.isEmpty()) {
                final Iterator<T> iterator2 = this.lowPriorityQueue.iterator();
                while (iterator2.hasNext()) {
                    final T next2 = iterator2.next();
                    if (next2 != null) {
                        iterator2.remove();
                        return next2;
                    }
                }
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T poll(final long n, final TimeUnit timeUnit) throws InterruptedException {
        this.lock.lock();
        try {
            do {
                final T poll = this.poll();
                if (poll != null) {
                    return poll;
                }
            } while (this.notEmpty.await(n, timeUnit));
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void put(final T t) throws InterruptedException {
        this.add(t);
    }
    
    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public T remove() {
        final T poll = this.poll();
        if (poll == null) {
            throw new NoSuchElementException();
        }
        return poll;
    }
    
    @Override
    public boolean remove(final Object o) {
        this.lock.lock();
        try {
            final boolean remove = this.queue.remove(o);
            final boolean remove2 = this.lowPriorityQueue.remove(o);
            this.lock.unlock();
            return remove | remove2;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean removeAll(@Nonnull final Collection<?> collection) {
        this.lock.lock();
        try {
            final boolean removeAll = this.queue.removeAll(collection);
            final boolean removeAll2 = this.lowPriorityQueue.removeAll(collection);
            this.lock.unlock();
            return removeAll | removeAll2;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        this.lock.lock();
        try {
            final boolean retainAll = this.queue.retainAll(collection);
            final boolean retainAll2 = this.lowPriorityQueue.retainAll(collection);
            this.lock.unlock();
            return retainAll | retainAll2;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int size() {
        this.lock.lock();
        try {
            final int size = this.queue.size();
            final int size2 = this.lowPriorityQueue.size();
            this.lock.unlock();
            return size + size2;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T take() throws InterruptedException {
        this.lock.lock();
        try {
            T poll;
            while (true) {
                poll = this.poll();
                if (poll != null) {
                    break;
                }
                this.notEmpty.await();
            }
            return poll;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Object[] toArray() {
        this.lock.lock();
        try {
            return ObjectArrays.concat(this.queue.toArray(), this.lowPriorityQueue.toArray(), Object.class);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public <T1> T1[] toArray(final T1[] a) {
        this.lock.lock();
        try {
            final Object[] array = this.toArray();
            if (array.length <= a.length) {
                Arrays.fill(a, null);
                System.arraycopy(array, 0, a, 0, array.length);
                return a;
            }
            return (T1[])array;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public interface LowPriority
    {
    }
}
