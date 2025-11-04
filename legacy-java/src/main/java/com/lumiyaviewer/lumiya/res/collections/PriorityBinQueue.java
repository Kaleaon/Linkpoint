package com.lumiyaviewer.lumiya.res.collections;

import android.R;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* loaded from: classes.dex */
public class PriorityBinQueue<T> implements BlockingQueue<T> {
    private final QueueFactory<T> queueFactory;
    private final SortedMap<Integer, Queue<T>> queues = new TreeMap();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();

    /* loaded from: classes.dex */
    public interface QueueFactory<T> {
        Queue<T> getQueue();
    }

    public PriorityBinQueue(QueueFactory<T> queueFactory) {
        this.queueFactory = queueFactory;
    }

    private int getPriority(Object obj) {
        if (obj instanceof HasPriority) {
            return ((HasPriority) obj).getPriority();
        }
        return 0;
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Queue, java.util.Collection
    public boolean add(T t) {
        this.lock.lock();
        try {
            int priority = getPriority(t);
            Debug.Printf("PriorityBinQueue: added %s with prio %d", t.toString(), Integer.valueOf(priority));
            Queue<T> queue = this.queues.get(Integer.valueOf(priority));
            if (queue == null) {
                queue = this.queueFactory.getQueue();
                this.queues.put(Integer.valueOf(priority), queue);
            }
            boolean add = queue.add(t);
            this.notEmpty.signalAll();
            return add;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean addAll(Collection<? extends T> collection) {
        this.lock.lock();
        boolean z = false;
        try {
            Iterator<T> it = collection.iterator();
            while (true) {
                boolean z2 = z;
                if (!it.hasNext()) {
                    return z2;
                }
                T next = it.next();
                int priority = getPriority(next);
                Queue<T> queue = this.queues.get(Integer.valueOf(priority));
                if (queue == null) {
                    queue = this.queueFactory.getQueue();
                    this.queues.put(Integer.valueOf(priority), queue);
                }
                z = queue.add(next) | z2;
                this.notEmpty.signalAll();
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public void clear() {
        this.lock.lock();
        try {
            this.queues.clear();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue, java.util.Collection
    public boolean contains(Object obj) {
        this.lock.lock();
        try {
            Queue<T> queue = this.queues.get(Integer.valueOf(getPriority(obj)));
            if (queue != null) {
                return queue.contains(obj);
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean containsAll(Collection<?> collection) {
        boolean z;
        this.lock.lock();
        try {
            Iterator<T> it = collection.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = true;
                    break;
                }
                T next = it.next();
                Queue<T> queue = this.queues.get(Integer.valueOf(getPriority(next)));
                if (queue != null && !queue.contains(next)) {
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
        try {
            Iterator<T> it = this.queues.values().iterator();
            while (it.hasNext()) {
                Queue queue = (Queue) it.next();
                while (true) {
                    R.bool boolVar = (Object) queue.poll();
                    if (boolVar != null) {
                        collection.add(boolVar);
                        i++;
                    }
                }
            }
            this.queues.clear();
            return i;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.concurrent.BlockingQueue
    public int drainTo(Collection<? super T> collection, int i) {
        int i2;
        this.lock.lock();
        int i3 = 0;
        try {
            Iterator<T> it = this.queues.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    i2 = i3;
                    break;
                }
                Queue queue = (Queue) it.next();
                while (true) {
                    R.bool boolVar = (Object) queue.poll();
                    if (boolVar == null || i3 >= i) {
                        break;
                    }
                    collection.add(boolVar);
                    i3++;
                }
                if (i3 >= i) {
                    i2 = i3;
                    break;
                }
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
        boolean z;
        this.lock.lock();
        try {
            Iterator<T> it = this.queues.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = true;
                    break;
                }
                if (!((Queue) it.next()).isEmpty()) {
                    z = false;
                    break;
                }
            }
            return z;
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
        this.lock.lock();
        try {
            Iterator<T> it = this.queues.values().iterator();
            while (it.hasNext()) {
                Queue<T> queue = (Queue) it.next();
                if (!queue.isEmpty()) {
                    for (T t : queue) {
                        if (t != null) {
                            return t;
                        }
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
            Iterator<T> it = this.queues.values().iterator();
            while (it.hasNext()) {
                Iterator it2 = ((Queue) it.next()).iterator();
                while (it2.hasNext()) {
                    T t = (T) it2.next();
                    if (t != null) {
                        it2.remove();
                        return t;
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
            Queue<T> queue = this.queues.get(Integer.valueOf(getPriority(obj)));
            if (queue != null) {
                return queue.remove(obj);
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean removeAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = false;
        try {
            for (T t : collection) {
                Queue<T> queue = this.queues.get(Integer.valueOf(getPriority(t)));
                z = queue != null ? queue.remove(t) | z : z;
            }
            return z;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public boolean retainAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = false;
        try {
            Iterator<T> it = this.queues.values().iterator();
            while (true) {
                boolean z2 = z;
                if (!it.hasNext()) {
                    return z2;
                }
                z = ((Queue) it.next()).retainAll(collection) | z2;
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public int size() {
        this.lock.lock();
        int i = 0;
        try {
            Iterator<T> it = this.queues.values().iterator();
            while (true) {
                int i2 = i;
                if (!it.hasNext()) {
                    return i2;
                }
                i = ((Queue) it.next()).size() + i2;
            }
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
        int i = 0;
        this.lock.lock();
        try {
            ArrayList<Object[]> arrayList = new ArrayList();
            Iterator<T> it = this.queues.values().iterator();
            int i2 = 0;
            while (it.hasNext()) {
                Object[] array = ((Queue) it.next()).toArray();
                int length = array.length + i2;
                arrayList.add(array);
                i2 = length;
            }
            Object[] objArr = new Object[i2];
            for (Object[] objArr2 : arrayList) {
                System.arraycopy(objArr2, 0, objArr, i, objArr2.length);
                i = objArr2.length + i;
            }
            arrayList.clear();
            return objArr;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // java.util.Collection
    public <T1> T1[] toArray(T1[] t1Arr) {
        int i = 0;
        this.lock.lock();
        try {
            ArrayList<Object[]> arrayList = new ArrayList();
            Iterator<T> it = this.queues.values().iterator();
            int i2 = 0;
            while (it.hasNext()) {
                Object[] array = ((Queue) it.next()).toArray();
                int length = array.length + i2;
                arrayList.add(array);
                i2 = length;
            }
            if (t1Arr.length >= i2) {
                Arrays.fill(t1Arr, (Object) null);
            } else {
                t1Arr = (T1[]) new Object[i2];
            }
            for (Object[] objArr : arrayList) {
                System.arraycopy(objArr, 0, t1Arr, i, objArr.length);
                i = objArr.length + i;
            }
            arrayList.clear();
            return t1Arr;
        } finally {
            this.lock.unlock();
        }
    }
}
