// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            HasPriority

public class PriorityBinQueue
    implements BlockingQueue
{

    private final Map allItems = new IdentityHashMap();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty;
    private final int numBins;
    private final Set queues[];

    public PriorityBinQueue(int i)
    {
        notEmpty = lock.newCondition();
        numBins = i;
        queues = new Set[i];
        for (int j = 0; j < i; j++)
        {
            queues[j] = new HashSet();
        }

    }

    public boolean add(Object obj)
    {
        lock.lock();
        if (!(obj instanceof HasPriority)) goto _L2; else goto _L1
_L1:
        int j = ((HasPriority)obj).getPriority();
        if (j >= 0) goto _L4; else goto _L3
_L3:
        int i = 0;
_L6:
        boolean flag;
        flag = queues[i].add(obj);
        allItems.put(obj, Integer.valueOf(i));
        Debug.Printf("Thread %s added item to the queue, bin %d/%d", new Object[] {
            Thread.currentThread().getName(), Integer.valueOf(i), Integer.valueOf(numBins)
        });
        notEmpty.signalAll();
        lock.unlock();
        return flag;
_L4:
        i = j;
        if (j <= numBins - 1) goto _L6; else goto _L5
_L5:
        i = numBins - 1;
          goto _L6
_L2:
        Debug.Printf("Thread %s added item %s without a priority", new Object[] {
            Thread.currentThread().getName(), obj.toString()
        });
        i = 0;
          goto _L6
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean addAll(Collection collection)
    {
        boolean flag = false;
        for (collection = collection.iterator(); collection.hasNext();)
        {
            flag |= add(collection.next());
        }

        return flag;
    }

    public void clear()
    {
        int i;
        lock.lock();
        i = 0;
_L2:
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        queues[i].clear();
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        allItems.clear();
        lock.unlock();
        return;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public boolean contains(Object obj)
    {
        lock.lock();
        boolean flag = allItems.containsKey(obj);
        lock.unlock();
        return flag;
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean containsAll(Collection collection)
    {
        boolean flag1;
        lock.lock();
        flag1 = true;
        collection = collection.iterator();
_L2:
        boolean flag = flag1;
        if (!collection.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        flag = contains(collection.next());
        if (flag)
        {
            continue; /* Loop/switch isn't completed */
        }
        flag = false;
        break; /* Loop/switch isn't completed */
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return flag;
        collection;
        lock.unlock();
        throw collection;
    }

    public int drainTo(Collection collection)
    {
        int i;
        int j;
        i = 0;
        lock.lock();
        j = 0;
_L2:
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        j += queues[i].size();
        collection.addAll(queues[i]);
        queues[i].clear();
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        allItems.clear();
        lock.unlock();
        return j;
        collection;
        lock.unlock();
        throw collection;
    }

    public int drainTo(Collection collection, int i)
    {
        throw new UnsupportedOperationException();
    }

    public Object element()
    {
        Object obj = peek();
        if (obj == null)
        {
            throw new NoSuchElementException();
        } else
        {
            return obj;
        }
    }

    public boolean isEmpty()
    {
        lock.lock();
        boolean flag = allItems.isEmpty();
        lock.unlock();
        return flag;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Iterator iterator()
    {
        throw new UnsupportedOperationException("Iterator not supported");
    }

    public boolean offer(Object obj)
    {
        return add(obj);
    }

    public boolean offer(Object obj, long l, TimeUnit timeunit)
        throws InterruptedException
    {
        return add(obj);
    }

    public Object peek()
    {
        int i;
        lock.lock();
        i = 0;
_L2:
        Object obj;
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        if (queues[i].isEmpty())
        {
            break MISSING_BLOCK_LABEL_65;
        }
        obj = queues[i].iterator().next();
        if (obj != null)
        {
            lock.unlock();
            return obj;
        }
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return null;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object poll()
    {
        int i;
        lock.lock();
        i = 0;
_L2:
        Iterator iterator1;
        Object obj;
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        if (queues[i].isEmpty())
        {
            break MISSING_BLOCK_LABEL_84;
        }
        iterator1 = queues[i].iterator();
        obj = iterator1.next();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        iterator1.remove();
        allItems.remove(obj);
        lock.unlock();
        return obj;
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return null;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object poll(long l, TimeUnit timeunit)
        throws InterruptedException
    {
        Object obj;
        Iterator iterator1;
        int i;
        obj = null;
        iterator1 = null;
        i = 0;
        lock.lock();
        if (!notEmpty.await(l, timeunit)) goto _L2; else goto _L1
_L1:
        timeunit = iterator1;
_L8:
        obj = timeunit;
        if (i >= numBins) goto _L2; else goto _L3
_L3:
        if (queues[i].isEmpty()) goto _L5; else goto _L4
_L4:
        iterator1 = queues[i].iterator();
        obj = iterator1.next();
        timeunit = ((TimeUnit) (obj));
        if (obj == null) goto _L5; else goto _L6
_L6:
        iterator1.remove();
        allItems.remove(obj);
        Debug.Printf("Thread %s got item with priority %d", new Object[] {
            Thread.currentThread().getName(), Integer.valueOf(i)
        });
_L2:
        lock.unlock();
        return obj;
_L5:
        i++;
        if (true) goto _L8; else goto _L7
_L7:
        timeunit;
        lock.unlock();
        throw timeunit;
    }

    public void put(Object obj)
        throws InterruptedException
    {
        add(obj);
    }

    public int remainingCapacity()
    {
        return 0x7fffffff;
    }

    public Object remove()
    {
        Object obj = poll();
        if (obj == null)
        {
            throw new NoSuchElementException();
        } else
        {
            return obj;
        }
    }

    public boolean remove(Object obj)
    {
        int i;
        boolean flag;
        i = 0;
        lock.lock();
        flag = false;
_L2:
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        flag |= queues[i].remove(obj);
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        allItems.remove(obj);
        lock.unlock();
        return flag;
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean removeAll(Collection collection)
    {
        boolean flag = false;
        for (collection = collection.iterator(); collection.hasNext();)
        {
            flag |= remove(collection.next());
        }

        return flag;
    }

    public boolean retainAll(Collection collection)
    {
        int i;
        boolean flag;
        i = 0;
        lock.lock();
        flag = false;
_L2:
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        flag |= queues[i].retainAll(collection);
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        allItems.keySet().retainAll(collection);
        lock.unlock();
        return flag;
        collection;
        lock.unlock();
        throw collection;
    }

    public int size()
    {
        int i;
        int j;
        i = 0;
        lock.lock();
        j = 0;
_L2:
        int k;
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        k = queues[i].size();
        j += k;
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return j;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object take()
        throws InterruptedException
    {
        lock.lock();
_L4:
        int i = 0;
_L2:
        Iterator iterator1;
        Object obj;
        if (i >= numBins)
        {
            break; /* Loop/switch isn't completed */
        }
        if (queues[i].isEmpty())
        {
            break MISSING_BLOCK_LABEL_109;
        }
        iterator1 = queues[i].iterator();
        obj = iterator1.next();
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_109;
        }
        iterator1.remove();
        allItems.remove(obj);
        Debug.Printf("Thread %s got item with priority %d", new Object[] {
            Thread.currentThread().getName(), Integer.valueOf(i)
        });
        lock.unlock();
        return obj;
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        Debug.Printf("Thread %s waiting on the queue", new Object[] {
            Thread.currentThread().getName()
        });
        notEmpty.await();
        if (true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object[] toArray()
    {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray(Object aobj[])
    {
        throw new UnsupportedOperationException();
    }

    public void updatePriority(Object obj)
    {
        int i;
        i = 0;
        lock.lock();
        if (!(obj instanceof HasPriority)) goto _L2; else goto _L1
_L1:
        int j = ((HasPriority)obj).getPriority();
        if (j >= 0) goto _L4; else goto _L3
_L3:
        Integer integer = (Integer)allItems.get(obj);
        if (integer == null) goto _L2; else goto _L5
_L5:
        j = integer.intValue();
        if (j == i) goto _L2; else goto _L6
_L6:
        if (queues[j].remove(obj))
        {
            queues[i].add(obj);
            allItems.put(obj, Integer.valueOf(i));
        }
_L2:
        lock.unlock();
        return;
_L4:
        if (j <= numBins - 1)
        {
            break MISSING_BLOCK_LABEL_153;
        }
        i = numBins;
        i--;
        continue; /* Loop/switch isn't completed */
        obj;
        lock.unlock();
        throw obj;
        i = j;
        if (true) goto _L3; else goto _L7
_L7:
    }
}
