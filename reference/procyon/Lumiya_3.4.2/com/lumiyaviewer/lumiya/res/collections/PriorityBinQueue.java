// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.collections;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Collection;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.util.concurrent.locks.ReentrantLock;
import java.util.TreeMap;
import java.util.Queue;
import java.util.SortedMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.BlockingQueue;

public class PriorityBinQueue<T> implements BlockingQueue<T>
{
    private final Lock lock;
    private final Condition notEmpty;
    private final QueueFactory<T> queueFactory;
    private final SortedMap<Integer, Queue<T>> queues;
    
    public PriorityBinQueue(final QueueFactory<T> queueFactory) {
        this.queues = new TreeMap<Integer, Queue<T>>();
        this.lock = new ReentrantLock();
        this.notEmpty = this.lock.newCondition();
        this.queueFactory = queueFactory;
    }
    
    private int getPriority(final Object o) {
        if (o instanceof HasPriority) {
            return ((HasPriority)o).getPriority();
        }
        return 0;
    }
    
    @Override
    public boolean add(final T t) {
        this.lock.lock();
        try {
            final int priority = this.getPriority(t);
            Debug.Printf("PriorityBinQueue: added %s with prio %d", t.toString(), priority);
            Queue<T> queue;
            if ((queue = this.queues.get(priority)) == null) {
                queue = this.queueFactory.getQueue();
                this.queues.put(priority, queue);
            }
            final boolean add = queue.add(t);
            this.notEmpty.signalAll();
            return add;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> collection) {
        this.lock.lock();
        try {
            final Iterator<Object> iterator = (Iterator<Object>)collection.iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final T next = iterator.next();
                final int priority = this.getPriority(next);
                Queue<T> queue;
                if ((queue = this.queues.get(priority)) == null) {
                    queue = this.queueFactory.getQueue();
                    this.queues.put(priority, queue);
                }
                final boolean add = queue.add(next);
                this.notEmpty.signalAll();
                n |= (add ? 1 : 0);
            }
            return n != 0;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.queues.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean contains(final Object o) {
        this.lock.lock();
        try {
            final Queue queue = this.queues.get(this.getPriority(o));
            return queue != null && queue.contains(o);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        while (true) {
            this.lock.lock();
            try {
                for (final Object next : collection) {
                    final Queue queue = this.queues.get(this.getPriority(next));
                    if (queue != null && !queue.contains(next)) {
                        return false;
                    }
                }
                return true;
                return false;
            }
            finally {
                this.lock.unlock();
            }
            return true;
        }
    }
    
    @Override
    public int drainTo(final Collection<? super T> collection) {
        this.lock.lock();
        int n = 0;
        try {
            for (final Queue queue : this.queues.values()) {
                int n2 = n;
                while (true) {
                    final Object poll = queue.poll();
                    n = n2;
                    if (poll == null) {
                        break;
                    }
                    collection.add((Object)poll);
                    ++n2;
                }
            }
            this.queues.clear();
            return n;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int drainTo(final Collection<? super T> collection, int n) {
        while (true) {
            this.lock.lock();
            int n2 = 0;
            try {
                for (final Queue queue : this.queues.values()) {
                    int n3 = n2;
                    while (true) {
                        final Object poll = queue.poll();
                        if (poll == null || n3 >= n) {
                            break;
                        }
                        collection.add((Object)poll);
                        ++n3;
                    }
                    if ((n2 = n3) >= n) {
                        return n3;
                    }
                }
                return n2;
                int n3 = 0;
                n = n3;
                return n;
            }
            finally {
                this.lock.unlock();
            }
            n = n2;
            return n;
        }
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
        while (true) {
            this.lock.lock();
            try {
                final Iterator<Object> iterator = this.queues.values().iterator();
                while (iterator.hasNext()) {
                    if (!iterator.next().isEmpty()) {
                        return false;
                    }
                }
                return true;
                return false;
            }
            finally {
                this.lock.unlock();
            }
            return true;
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
        try {
            for (final Queue queue : this.queues.values()) {
                if (!queue.isEmpty()) {
                    for (final Object next : queue) {
                        if (next != null) {
                            return (T)next;
                        }
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
            final Iterator<Object> iterator = this.queues.values().iterator();
            while (iterator.hasNext()) {
                final Iterator iterator2 = iterator.next().iterator();
                while (iterator2.hasNext()) {
                    final Object next = iterator2.next();
                    if (next != null) {
                        iterator2.remove();
                        return (T)next;
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
            final Queue queue = this.queues.get(this.getPriority(o));
            return queue != null && queue.remove(o);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        while (true) {
            this.lock.lock();
            boolean b = false;
            while (true) {
                Label_0106: {
                    try {
                        for (final Object next : collection) {
                            final Queue queue = this.queues.get(this.getPriority(next));
                            if (queue == null) {
                                break Label_0106;
                            }
                            b |= queue.remove(next);
                        }
                        return b;
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
                continue;
            }
        }
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        this.lock.lock();
        try {
            final Iterator<Object> iterator = this.queues.values().iterator();
            boolean b = false;
            while (iterator.hasNext()) {
                b |= iterator.next().retainAll(collection);
            }
            return b;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int size() {
        this.lock.lock();
        try {
            final Iterator<Object> iterator = this.queues.values().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                n += iterator.next().size();
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
        final int n = 0;
        this.lock.lock();
        try {
            final ArrayList list = new ArrayList();
            final Iterator<Object> iterator = this.queues.values().iterator();
            int n2 = 0;
            while (iterator.hasNext()) {
                final Object[] array = iterator.next().toArray();
                final int length = array.length;
                list.add(array);
                n2 += length;
            }
            final Object[] array2 = new Object[n2];
            final Iterator iterator2 = list.iterator();
            int n3 = n;
            while (iterator2.hasNext()) {
                final Object[] array3 = (Object[])iterator2.next();
                System.arraycopy(array3, 0, array2, n3, array3.length);
                n3 += array3.length;
            }
            list.clear();
            return array2;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public <T1> T1[] toArray(T1[] a) {
        final int n = 0;
        this.lock.lock();
        try {
            final ArrayList list = new ArrayList();
            final Iterator<Object> iterator = this.queues.values().iterator();
            int n2 = 0;
            while (iterator.hasNext()) {
                final Object[] array = iterator.next().toArray();
                final int length = array.length;
                list.add(array);
                n2 += length;
            }
            if (a.length >= n2) {
                Arrays.fill(a, null);
            }
            else {
                a = (T1[])new Object[n2];
            }
            final Iterator iterator2 = list.iterator();
            int n3 = n;
            while (iterator2.hasNext()) {
                final Object[] array2 = (Object[])iterator2.next();
                System.arraycopy(array2, 0, a, n3, array2.length);
                n3 += array2.length;
            }
            list.clear();
            return a;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public interface QueueFactory<T>
    {
        Queue<T> getQueue();
    }
}
