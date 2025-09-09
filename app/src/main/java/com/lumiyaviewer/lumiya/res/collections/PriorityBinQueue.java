package com.lumiyaviewer.lumiya.res.collections;

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

public class PriorityBinQueue<T> implements BlockingQueue<T> {
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = this.lock.newCondition();
    private final QueueFactory<T> queueFactory;
    private final SortedMap<Integer, Queue<T>> queues = new TreeMap();

    public interface QueueFactory<T> {
        Queue<T> getQueue();
    }

    public PriorityBinQueue(QueueFactory<T> queueFactory) {
        this.queueFactory = queueFactory;
    }

    private int getPriority(Object obj) {
        return obj instanceof HasPriority ? ((HasPriority) obj).getPriority() : 0;
    }

    public boolean add(T t) {
        this.lock.lock();
        try {
            int priority = getPriority(t);
            Debug.Printf("PriorityBinQueue: added %s with prio %d", t.toString(), Integer.valueOf(priority));
            Queue queue = (Queue) this.queues.get(Integer.valueOf(priority));
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

    public boolean addAll(Collection<? extends T> collection) {
        this.lock.lock();
        boolean z = false;
        boolean z2;
        try {
            Iterator it = collection.iterator();
            while (true) {
                z2 = z;
                if (!it.hasNext()) {
                    break;
                }
                Object next = it.next();
                int priority = getPriority(next);
                Queue queue = (Queue) this.queues.get(Integer.valueOf(priority));
                if (queue == null) {
                    queue = this.queueFactory.getQueue();
                    this.queues.put(Integer.valueOf(priority), queue);
                }
                z = queue.add(next) | z2;
                this.notEmpty.signalAll();
            }
            return z2;
        } finally {
            z2 = this.lock;
            z2.unlock();
        }
    }

    public void clear() {
        this.lock.lock();
        try {
            this.queues.clear();
        } finally {
            this.lock.unlock();
        }
    }

    public boolean contains(Object obj) {
        this.lock.lock();
        try {
            Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(obj)));
            if (queue != null) {
                boolean contains = queue.contains(obj);
                return contains;
            }
            this.lock.unlock();
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean containsAll(Collection<?> collection) {
        this.lock.lock();
        try {
            boolean z;
            for (Object next : collection) {
                Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(next)));
                if (queue != null && !queue.contains(next)) {
                    z = false;
                    break;
                }
            }
            z = true;
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
            this.lock.unlock();
        }
    }

    public int drainTo(Collection<? super T> collection) {
        this.lock.lock();
        int i = 0;
        try {
            for (Queue queue : this.queues.values()) {
                while (true) {
                    Object poll = queue.poll();
                    if (poll != null) {
                        collection.add(poll);
                        i++;
                    }
                }
            }
            this.queues.clear();
            return i;
        } finally {
            i = this.lock;
            i.unlock();
        }
    }

    /* DevToolsApp WARNING: Removed duplicated region for block: B:20:0x002c A:{SYNTHETIC} */
    public int drainTo(java.util.Collection<? super T> r5, int r6) {
        /*
        r4 = this;
        r0 = r4.lock;
        r0.lock();
        r1 = 0;
        r0 = r4.queues;	 Catch:{ all -> 0x0033 }
        r0 = r0.values();	 Catch:{ all -> 0x0033 }
        r2 = r0.iterator();	 Catch:{ all -> 0x0033 }
    L_0x0010:
        r0 = r2.hasNext();	 Catch:{ all -> 0x0033 }
        if (r0 == 0) goto L_0x003a;
    L_0x0016:
        r0 = r2.next();	 Catch:{ all -> 0x0033 }
        r0 = (java.util.Queue) r0;	 Catch:{ all -> 0x0033 }
    L_0x001c:
        r3 = r0.poll();	 Catch:{ all -> 0x0033 }
        if (r3 == 0) goto L_0x002a;
    L_0x0022:
        if (r1 >= r6) goto L_0x002a;
    L_0x0024:
        r5.add(r3);	 Catch:{ all -> 0x0033 }
        r1 = r1 + 1;
        goto L_0x001c;
    L_0x002a:
        if (r1 < r6) goto L_0x0010;
    L_0x002c:
        r0 = r1;
    L_0x002d:
        r1 = r4.lock;
        r1.unlock();
        return r0;
    L_0x0033:
        r0 = move-exception;
        r1 = r4.lock;
        r1.unlock();
        throw r0;
    L_0x003a:
        r0 = r1;
        goto L_0x002d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.res.collections.PriorityBinQueue.drainTo(java.util.Collection, int):int");
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
            boolean z;
            for (Queue isEmpty : this.queues.values()) {
                if (!isEmpty.isEmpty()) {
                    z = false;
                    break;
                }
            }
            z = true;
            this.lock.unlock();
            return z;
        } catch (Throwable th) {
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
        this.lock.lock();
        try {
            for (Queue queue : this.queues.values()) {
                if (!queue.isEmpty()) {
                    for (T next : queue) {
                        if (next != null) {
                            return next;
                        }
                    }
                    continue;
                }
            }
            this.lock.unlock();
            return null;
        } finally {
            this.lock.unlock();
        }
    }

    public T poll() {
        this.lock.lock();
        try {
            for (Queue it : this.queues.values()) {
                Iterator it2 = it.iterator();
                while (it2.hasNext()) {
                    T next = it2.next();
                    if (next != null) {
                        it2.remove();
                        return next;
                    }
                }
            }
            this.lock.unlock();
            return null;
        } finally {
            this.lock.unlock();
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
            Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(obj)));
            if (queue != null) {
                boolean remove = queue.remove(obj);
                return remove;
            }
            this.lock.unlock();
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean removeAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = false;
        try {
            for (Object next : collection) {
                Queue queue = (Queue) this.queues.get(Integer.valueOf(getPriority(next)));
                z = queue != null ? queue.remove(next) | z : z;
            }
            return z;
        } finally {
            z = this.lock;
            z.unlock();
        }
    }

    public boolean retainAll(Collection<?> collection) {
        this.lock.lock();
        boolean z = false;
        boolean z2;
        try {
            Iterator it = this.queues.values().iterator();
            while (true) {
                z2 = z;
                if (!it.hasNext()) {
                    break;
                }
                z = ((Queue) it.next()).retainAll(collection) | z2;
            }
            return z2;
        } finally {
            z2 = this.lock;
            z2.unlock();
        }
    }

    public int size() {
        this.lock.lock();
        int i = 0;
        int i2;
        try {
            Iterator it = this.queues.values().iterator();
            while (true) {
                i2 = i;
                if (!it.hasNext()) {
                    break;
                }
                i = ((Queue) it.next()).size() + i2;
            }
            return i2;
        } finally {
            i2 = this.lock;
            i2.unlock();
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
        int i = 0;
        this.lock.lock();
        try {
            Object<Object[]> arrayList = new ArrayList();
            int i2 = 0;
            for (Queue toArray : this.queues.values()) {
                Object toArray2 = toArray.toArray();
                int length = toArray2.length + i2;
                arrayList.add(toArray2);
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

    public <T1> T1[] toArray(T1[] t1Arr) {
        int i = 0;
        this.lock.lock();
        try {
            Object<Object[]> arrayList = new ArrayList();
            int i2 = 0;
            for (Queue toArray : this.queues.values()) {
                Object toArray2 = toArray.toArray();
                int length = toArray2.length + i2;
                arrayList.add(toArray2);
                i2 = length;
            }
            if (t1Arr.length >= i2) {
                Arrays.fill(t1Arr, null);
            } else {
                t1Arr = new Object[i2];
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
