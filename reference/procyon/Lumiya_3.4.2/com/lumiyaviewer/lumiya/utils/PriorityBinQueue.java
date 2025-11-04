// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Collection;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class PriorityBinQueue<T> implements BlockingQueue<T>
{
    private final Map<T, Integer> allItems;
    private final Lock lock;
    private final Condition notEmpty;
    private final int numBins;
    private final Set<T>[] queues;
    
    public PriorityBinQueue(final int numBins) {
        this.allItems = new IdentityHashMap<T, Integer>();
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
        this.numBins = numBins;
        this.queues = new Set[numBins];
        for (int i = 0; i < numBins; ++i) {
            this.queues[i] = new HashSet<T>();
        }
    }
    
    @Override
    public boolean add(final T t) {
        this.lock.lock();
        try {
            int n;
            if (t instanceof HasPriority) {
                final int priority = ((HasPriority)t).getPriority();
                if (priority < 0) {
                    n = 0;
                }
                else if ((n = priority) > this.numBins - 1) {
                    n = this.numBins - 1;
                }
            }
            else {
                Debug.Printf("Thread %s added item %s without a priority", Thread.currentThread().getName(), t.toString());
                n = 0;
            }
            final boolean add = this.queues[n].add(t);
            this.allItems.put(t, n);
            Debug.Printf("Thread %s added item to the queue, bin %d/%d", Thread.currentThread().getName(), n, this.numBins);
            this.notEmpty.signalAll();
            return add;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        boolean b = false;
        final Iterator<Object> iterator = (Iterator<Object>)collection.iterator();
        while (iterator.hasNext()) {
            b |= this.add(iterator.next());
        }
        return b;
    }
    
    @Override
    public void clear() {
        this.lock.lock();
        int i = 0;
        try {
            while (i < this.numBins) {
                this.queues[i].clear();
                ++i;
            }
            this.allItems.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean contains(final Object o) {
        this.lock.lock();
        try {
            return this.allItems.containsKey(o);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        this.lock.lock();
        final boolean b = true;
        try {
            final Iterator<Object> iterator = collection.iterator();
            do {
                final boolean b2 = b;
                if (iterator.hasNext()) {
                    continue;
                }
                return b2;
            } while (this.contains(iterator.next()));
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int drainTo(final Collection<? super T> collection) {
        int i = 0;
        this.lock.lock();
        int n = 0;
        try {
            while (i < this.numBins) {
                n += this.queues[i].size();
                collection.addAll((Collection<? extends T>)this.queues[i]);
                this.queues[i].clear();
                ++i;
            }
            this.allItems.clear();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int drainTo(final Collection<? super T> collection, final int n) {
        throw new UnsupportedOperationException();
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
            return this.allItems.isEmpty();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("Iterator not supported");
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
        int i = 0;
        try {
            while (i < this.numBins) {
                if (!this.queues[i].isEmpty()) {
                    final T next = this.queues[i].iterator().next();
                    if (next != null) {
                        return next;
                    }
                }
                ++i;
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
        int i = 0;
        try {
            while (i < this.numBins) {
                if (!this.queues[i].isEmpty()) {
                    final Iterator<T> iterator = this.queues[i].iterator();
                    final T next = iterator.next();
                    if (next != null) {
                        iterator.remove();
                        this.allItems.remove(next);
                        return next;
                    }
                }
                ++i;
            }
            return null;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T poll(final long n, final TimeUnit timeUnit) throws InterruptedException {
        T next = null;
        final T t = null;
        int i = 0;
        this.lock.lock();
        try {
            if (this.notEmpty.await(n, timeUnit)) {
                T t2 = t;
                while (true) {
                    next = t2;
                    if (i >= this.numBins) {
                        break;
                    }
                    if (!this.queues[i].isEmpty()) {
                        final Iterator<T> iterator = this.queues[i].iterator();
                        next = iterator.next();
                        if ((t2 = next) != null) {
                            iterator.remove();
                            this.allItems.remove(next);
                            Debug.Printf("Thread %s got item with priority %d", Thread.currentThread().getName(), i);
                            break;
                        }
                    }
                    ++i;
                }
            }
            return next;
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
        int i = 0;
        this.lock.lock();
        boolean b = false;
        try {
            while (i < this.numBins) {
                b |= this.queues[i].remove(o);
                ++i;
            }
            this.allItems.remove(o);
            return b;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        boolean b = false;
        final Iterator<Object> iterator = collection.iterator();
        while (iterator.hasNext()) {
            b |= this.remove(iterator.next());
        }
        return b;
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        int i = 0;
        this.lock.lock();
        boolean b = false;
        try {
            while (i < this.numBins) {
                b |= this.queues[i].retainAll(collection);
                ++i;
            }
            this.allItems.keySet().retainAll(collection);
            return b;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int size() {
        int i = 0;
        this.lock.lock();
        int n = 0;
        try {
            while (i < this.numBins) {
                n += this.queues[i].size();
                ++i;
            }
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T take() throws InterruptedException {
        this.lock.lock();
        while (true) {
            int i = 0;
            try {
                while (i < this.numBins) {
                    if (!this.queues[i].isEmpty()) {
                        final Iterator<T> iterator = this.queues[i].iterator();
                        final T next = iterator.next();
                        if (next != null) {
                            iterator.remove();
                            this.allItems.remove(next);
                            Debug.Printf("Thread %s got item with priority %d", Thread.currentThread().getName(), i);
                            return next;
                        }
                    }
                    ++i;
                }
                Debug.Printf("Thread %s waiting on the queue", Thread.currentThread().getName());
                this.notEmpty.await();
            }
            finally {
                this.lock.unlock();
            }
        }
    }
    
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        throw new UnsupportedOperationException();
    }
    
    public void updatePriority(final T t) {
        while (true) {
            int numBins = 0;
            this.lock.lock();
            while (true) {
                int n = 0;
                Label_0149: {
                    try {
                        if (t instanceof HasPriority) {
                            n = ((HasPriority)t).getPriority();
                            if (n >= 0) {
                                if (n <= this.numBins - 1) {
                                    break Label_0149;
                                }
                                numBins = this.numBins;
                                --numBins;
                            }
                            final Integer n2 = this.allItems.get(t);
                            if (n2 != null) {
                                n = n2;
                                if (n != numBins && this.queues[n].remove(t)) {
                                    this.queues[numBins].add(t);
                                    this.allItems.put(t, numBins);
                                }
                            }
                        }
                        return;
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
                numBins = n;
                continue;
            }
        }
    }
}
