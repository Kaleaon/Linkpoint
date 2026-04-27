// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.collections;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityBinQueue
    implements BlockingQueue
{
    public static interface QueueFactory
    {

        public abstract Queue getQueue();
    }


    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty;
    private final QueueFactory queueFactory;
    private final SortedMap queues = new TreeMap();

    public PriorityBinQueue(QueueFactory queuefactory)
    {
        notEmpty = lock.newCondition();
        queueFactory = queuefactory;
    }

    private int getPriority(Object obj)
    {
        if (obj instanceof HasPriority)
        {
            return ((HasPriority)obj).getPriority();
        } else
        {
            return 0;
        }
    }

    public boolean add(Object obj)
    {
        lock.lock();
        Queue queue1;
        int i;
        i = getPriority(obj);
        Debug.Printf("PriorityBinQueue: added %s with prio %d", new Object[] {
            obj.toString(), Integer.valueOf(i)
        });
        queue1 = (Queue)queues.get(Integer.valueOf(i));
        Queue queue;
        queue = queue1;
        if (queue1 != null)
        {
            break MISSING_BLOCK_LABEL_90;
        }
        queue = queueFactory.getQueue();
        queues.put(Integer.valueOf(i), queue);
        boolean flag;
        flag = queue.add(obj);
        notEmpty.signalAll();
        lock.unlock();
        return flag;
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean addAll(Collection collection)
    {
        lock.lock();
        Iterator iterator1 = collection.iterator();
        boolean flag = false;
_L2:
        Queue queue;
        Object obj;
        int i;
        if (!iterator1.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj = iterator1.next();
        i = getPriority(obj);
        queue = (Queue)queues.get(Integer.valueOf(i));
        collection = queue;
        if (queue != null)
        {
            break MISSING_BLOCK_LABEL_94;
        }
        collection = queueFactory.getQueue();
        queues.put(Integer.valueOf(i), collection);
        boolean flag1;
        flag1 = collection.add(obj);
        notEmpty.signalAll();
        flag = flag1 | flag;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return flag;
        collection;
        lock.unlock();
        throw collection;
    }

    public void clear()
    {
        lock.lock();
        queues.clear();
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
        Queue queue;
        int i = getPriority(obj);
        queue = (Queue)queues.get(Integer.valueOf(i));
        if (queue == null)
        {
            break MISSING_BLOCK_LABEL_57;
        }
        boolean flag = queue.contains(obj);
        lock.unlock();
        return flag;
        lock.unlock();
        return false;
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean containsAll(Collection collection)
    {
        lock.lock();
        collection = collection.iterator();
_L4:
        if (!collection.hasNext()) goto _L2; else goto _L1
_L1:
        Object obj;
        Queue queue;
        obj = collection.next();
        int i = getPriority(obj);
        queue = (Queue)queues.get(Integer.valueOf(i));
        if (queue == null) goto _L4; else goto _L3
_L3:
        boolean flag = queue.contains(obj);
        if (flag) goto _L4; else goto _L5
_L5:
        flag = false;
_L7:
        lock.unlock();
        return flag;
        collection;
        lock.unlock();
        throw collection;
_L2:
        flag = true;
        if (true) goto _L7; else goto _L6
_L6:
    }

    public int drainTo(Collection collection)
    {
        int i;
        lock.lock();
        i = 0;
        Iterator iterator1 = queues.values().iterator();
_L2:
        Queue queue;
        if (!iterator1.hasNext())
        {
            break MISSING_BLOCK_LABEL_85;
        }
        queue = (Queue)iterator1.next();
        int j = i;
_L3:
        Object obj = queue.poll();
        i = j;
        if (obj == null) goto _L2; else goto _L1
_L1:
        collection.add(obj);
        j++;
          goto _L3
        queues.clear();
        lock.unlock();
        return i;
        collection;
        lock.unlock();
        throw collection;
    }

    public int drainTo(Collection collection, int i)
    {
        int j;
        lock.lock();
        j = 0;
        Iterator iterator1 = queues.values().iterator();
_L6:
        if (!iterator1.hasNext()) goto _L2; else goto _L1
_L1:
        Queue queue = (Queue)iterator1.next();
        int k = j;
_L4:
        Object obj = queue.poll();
        if (obj == null || k >= i)
        {
            break; /* Loop/switch isn't completed */
        }
        collection.add(obj);
        k++;
        if (true) goto _L4; else goto _L3
_L3:
        j = k;
        if (k < i) goto _L6; else goto _L5
_L5:
        lock.unlock();
        return k;
        collection;
        lock.unlock();
        throw collection;
_L2:
        k = j;
        if (true) goto _L5; else goto _L7
_L7:
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
        Iterator iterator1 = queues.values().iterator();
_L4:
        if (!iterator1.hasNext()) goto _L2; else goto _L1
_L1:
        boolean flag = ((Queue)iterator1.next()).isEmpty();
        if (flag) goto _L4; else goto _L3
_L3:
        flag = false;
_L6:
        lock.unlock();
        return flag;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
_L2:
        flag = true;
        if (true) goto _L6; else goto _L5
_L5:
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
        lock.lock();
        Iterator iterator1 = queues.values().iterator();
_L2:
        Object obj;
        if (!iterator1.hasNext())
        {
            break MISSING_BLOCK_LABEL_90;
        }
        obj = (Queue)iterator1.next();
        if (((Queue) (obj)).isEmpty())
        {
            continue; /* Loop/switch isn't completed */
        }
        obj = ((Queue) (obj)).iterator();
        Object obj1;
        do
        {
            if (!((Iterator) (obj)).hasNext())
            {
                continue; /* Loop/switch isn't completed */
            }
            obj1 = ((Iterator) (obj)).next();
        } while (obj1 == null);
        break; /* Loop/switch isn't completed */
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return obj1;
        lock.unlock();
        return null;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object poll()
    {
        lock.lock();
        Iterator iterator1 = queues.values().iterator();
_L2:
        Iterator iterator2;
        if (!iterator1.hasNext())
        {
            break MISSING_BLOCK_LABEL_85;
        }
        iterator2 = ((Queue)iterator1.next()).iterator();
_L4:
        if (!iterator2.hasNext()) goto _L2; else goto _L1
_L1:
        Object obj = iterator2.next();
        if (obj == null) goto _L4; else goto _L3
_L3:
        iterator2.remove();
        lock.unlock();
        return obj;
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
        lock.lock();
_L2:
        Object obj = poll();
        if (obj != null)
        {
            lock.unlock();
            return obj;
        }
        boolean flag = notEmpty.await(l, timeunit);
        if (flag) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return null;
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
        lock.lock();
        Queue queue;
        int i = getPriority(obj);
        queue = (Queue)queues.get(Integer.valueOf(i));
        if (queue == null)
        {
            break MISSING_BLOCK_LABEL_57;
        }
        boolean flag = queue.remove(obj);
        lock.unlock();
        return flag;
        lock.unlock();
        return false;
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean removeAll(Collection collection)
    {
        boolean flag;
        lock.lock();
        flag = false;
        collection = collection.iterator();
_L2:
        Object obj;
        Queue queue;
        do
        {
            if (!collection.hasNext())
            {
                break MISSING_BLOCK_LABEL_83;
            }
            obj = collection.next();
            int i = getPriority(obj);
            queue = (Queue)queues.get(Integer.valueOf(i));
        } while (queue == null);
        boolean flag1 = queue.remove(obj);
        flag = flag1 | flag;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return flag;
        collection;
        lock.unlock();
        throw collection;
    }

    public boolean retainAll(Collection collection)
    {
        lock.lock();
        Iterator iterator1 = queues.values().iterator();
        boolean flag = false;
_L2:
        boolean flag1;
        if (!iterator1.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        flag1 = ((Queue)iterator1.next()).retainAll(collection);
        flag = flag1 | flag;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return flag;
        collection;
        lock.unlock();
        throw collection;
    }

    public int size()
    {
        lock.lock();
        Iterator iterator1 = queues.values().iterator();
        int i = 0;
_L2:
        int j;
        if (!iterator1.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        j = ((Queue)iterator1.next()).size();
        i = j + i;
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return i;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object take()
        throws InterruptedException
    {
        lock.lock();
_L1:
        Object obj = poll();
        if (obj != null)
        {
            lock.unlock();
            return obj;
        }
        notEmpty.await();
          goto _L1
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object[] toArray()
    {
        boolean flag;
        flag = false;
        lock.lock();
        ArrayList arraylist;
        Iterator iterator1;
        arraylist = new ArrayList();
        iterator1 = queues.values().iterator();
        int i = 0;
_L2:
        int j;
        if (!iterator1.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        Object aobj1[] = ((Queue)iterator1.next()).toArray();
        j = aobj1.length;
        arraylist.add(((Object) (aobj1)));
        i = j + i;
        if (true) goto _L2; else goto _L1
_L1:
        Object aobj[];
        Iterator iterator2;
        aobj = new Object[i];
        iterator2 = arraylist.iterator();
        i = ((flag) ? 1 : 0);
        while (iterator2.hasNext()) 
        {
            Object aobj2[] = (Object[])iterator2.next();
            System.arraycopy(((Object) (aobj2)), 0, ((Object) (aobj)), i, aobj2.length);
            i = aobj2.length + i;
        }
        arraylist.clear();
        lock.unlock();
        return aobj;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object[] toArray(Object aobj[])
    {
        boolean flag;
        flag = false;
        lock.lock();
        ArrayList arraylist;
        Iterator iterator1;
        arraylist = new ArrayList();
        iterator1 = queues.values().iterator();
        int i = 0;
_L2:
        int j;
        if (!iterator1.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        Object aobj1[] = ((Queue)iterator1.next()).toArray();
        j = aobj1.length;
        arraylist.add(((Object) (aobj1)));
        i = j + i;
        if (true) goto _L2; else goto _L1
_L1:
        if (aobj.length < i)
        {
            break MISSING_BLOCK_LABEL_153;
        }
        Arrays.fill(aobj, null);
_L3:
        iterator1 = arraylist.iterator();
        i = ((flag) ? 1 : 0);
        while (iterator1.hasNext()) 
        {
            Object aobj2[] = (Object[])iterator1.next();
            System.arraycopy(((Object) (aobj2)), 0, ((Object) (aobj)), i, aobj2.length);
            i = aobj2.length + i;
        }
        break MISSING_BLOCK_LABEL_162;
        aobj = new Object[i];
          goto _L3
        arraylist.clear();
        lock.unlock();
        return aobj;
        aobj;
        lock.unlock();
        throw aobj;
    }
}
