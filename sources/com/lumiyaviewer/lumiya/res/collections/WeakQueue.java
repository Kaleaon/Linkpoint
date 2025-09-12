// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

public class WeakQueue
    implements BlockingQueue
{
    public static interface LowPriority
    {
    }


    private final Lock lock = new ReentrantLock();
    private final Set lowPriorityQueue = Collections.newSetFromMap(new WeakHashMap());
    private final Condition notEmpty;
    private final Set queue = Collections.newSetFromMap(new WeakHashMap());

    public WeakQueue()
    {
        notEmpty = lock.newCondition();
    }

    public boolean add(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        lock.lock();
        if (!(obj instanceof LowPriority))
        {
            break MISSING_BLOCK_LABEL_53;
        }
        lowPriorityQueue.add(obj);
_L1:
        notEmpty.signalAll();
        lock.unlock();
        return true;
        queue.add(obj);
          goto _L1
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean addAll(Collection collection)
    {
        lock.lock();
        collection = collection.iterator();
_L1:
        Object obj;
        do
        {
            if (!collection.hasNext())
            {
                break MISSING_BLOCK_LABEL_79;
            }
            obj = collection.next();
            if (!(obj instanceof LowPriority))
            {
                break MISSING_BLOCK_LABEL_65;
            }
            lowPriorityQueue.add(obj);
        } while (true);
        collection;
        lock.unlock();
        throw collection;
        queue.add(obj);
          goto _L1
        notEmpty.signalAll();
        lock.unlock();
        return true;
    }

    public void clear()
    {
        lock.lock();
        queue.clear();
        lowPriorityQueue.clear();
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
        if (queue.contains(obj)) goto _L2; else goto _L1
_L1:
        boolean flag = lowPriorityQueue.contains(obj);
_L4:
        lock.unlock();
        return flag;
_L2:
        flag = true;
        if (true) goto _L4; else goto _L3
_L3:
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean containsAll(Collection collection)
    {
        lock.lock();
        if (queue.containsAll(collection)) goto _L2; else goto _L1
_L1:
        boolean flag = lowPriorityQueue.containsAll(collection);
_L4:
        lock.unlock();
        return flag;
_L2:
        flag = true;
        if (true) goto _L4; else goto _L3
_L3:
        collection;
        lock.unlock();
        throw collection;
    }

    public int drainTo(Collection collection)
    {
        int i;
        lock.lock();
        i = 0;
        Iterator iterator1 = queue.iterator();
_L2:
        Object obj;
        do
        {
            if (!iterator1.hasNext())
            {
                break MISSING_BLOCK_LABEL_59;
            }
            obj = iterator1.next();
        } while (obj == null);
        collection.add(obj);
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        queue.clear();
        iterator1 = lowPriorityQueue.iterator();
_L4:
        do
        {
            if (!iterator1.hasNext())
            {
                break MISSING_BLOCK_LABEL_115;
            }
            obj = iterator1.next();
        } while (obj == null);
        collection.add(obj);
        i++;
        if (true) goto _L4; else goto _L3
_L3:
        lowPriorityQueue.clear();
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
        Iterator iterator1 = queue.iterator();
_L2:
        if (!iterator1.hasNext() || j >= i)
        {
            break; /* Loop/switch isn't completed */
        }
        Object obj = iterator1.next();
        int k;
        k = j;
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_69;
        }
        collection.add(obj);
        k = j + 1;
        iterator1.remove();
        j = k;
        if (true) goto _L2; else goto _L1
        collection;
        lock.unlock();
        throw collection;
_L1:
        iterator1 = lowPriorityQueue.iterator();
_L4:
        if (!iterator1.hasNext() || j >= i)
        {
            break; /* Loop/switch isn't completed */
        }
        obj = iterator1.next();
        k = j;
        if (obj == null)
        {
            break MISSING_BLOCK_LABEL_151;
        }
        collection.add(obj);
        k = j + 1;
        iterator1.remove();
        j = k;
        if (true) goto _L4; else goto _L3
_L3:
        lock.unlock();
        return j;
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
        if (!queue.isEmpty()) goto _L2; else goto _L1
_L1:
        boolean flag = lowPriorityQueue.isEmpty();
_L4:
        lock.unlock();
        return flag;
_L2:
        flag = false;
        if (true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Iterator iterator()
    {
        throw new UnsupportedOperationException("Iterating over WeakQueue is not supported");
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
        Iterator iterator1;
        if (queue.isEmpty())
        {
            break MISSING_BLOCK_LABEL_62;
        }
        iterator1 = queue.iterator();
        Object obj;
        do
        {
            if (!iterator1.hasNext())
            {
                break MISSING_BLOCK_LABEL_62;
            }
            obj = iterator1.next();
        } while (obj == null);
        lock.unlock();
        return obj;
        if (lowPriorityQueue.isEmpty())
        {
            break MISSING_BLOCK_LABEL_115;
        }
        iterator1 = lowPriorityQueue.iterator();
        Object obj1;
        do
        {
            if (!iterator1.hasNext())
            {
                break MISSING_BLOCK_LABEL_115;
            }
            obj1 = iterator1.next();
        } while (obj1 == null);
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
        Iterator iterator1;
        if (queue.isEmpty())
        {
            break MISSING_BLOCK_LABEL_68;
        }
        iterator1 = queue.iterator();
        Object obj;
        do
        {
            if (!iterator1.hasNext())
            {
                break MISSING_BLOCK_LABEL_68;
            }
            obj = iterator1.next();
        } while (obj == null);
        iterator1.remove();
        lock.unlock();
        return obj;
        if (lowPriorityQueue.isEmpty())
        {
            break MISSING_BLOCK_LABEL_127;
        }
        iterator1 = lowPriorityQueue.iterator();
        do
        {
            if (!iterator1.hasNext())
            {
                break MISSING_BLOCK_LABEL_127;
            }
            obj = iterator1.next();
        } while (obj == null);
        iterator1.remove();
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
        boolean flag;
        boolean flag1;
        flag = queue.remove(obj);
        flag1 = lowPriorityQueue.remove(obj);
        lock.unlock();
        return flag | flag1;
        obj;
        lock.unlock();
        throw obj;
    }

    public boolean removeAll(Collection collection)
    {
        lock.lock();
        boolean flag;
        boolean flag1;
        flag = queue.removeAll(collection);
        flag1 = lowPriorityQueue.removeAll(collection);
        lock.unlock();
        return flag | flag1;
        collection;
        lock.unlock();
        throw collection;
    }

    public boolean retainAll(Collection collection)
    {
        lock.lock();
        boolean flag;
        boolean flag1;
        flag = queue.retainAll(collection);
        flag1 = lowPriorityQueue.retainAll(collection);
        lock.unlock();
        return flag | flag1;
        collection;
        lock.unlock();
        throw collection;
    }

    public int size()
    {
        lock.lock();
        int i;
        int j;
        i = queue.size();
        j = lowPriorityQueue.size();
        lock.unlock();
        return i + j;
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
        lock.lock();
        Object aobj[] = ObjectArrays.concat(queue.toArray(), lowPriorityQueue.toArray(), java/lang/Object);
        lock.unlock();
        return aobj;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public Object[] toArray(Object aobj[])
    {
        lock.lock();
        Object aobj1[];
        aobj1 = toArray();
        if (aobj1.length > aobj.length)
        {
            break MISSING_BLOCK_LABEL_46;
        }
        Arrays.fill(aobj, null);
        System.arraycopy(((Object) (aobj1)), 0, ((Object) (aobj)), 0, aobj1.length);
        lock.unlock();
        return aobj;
        lock.unlock();
        return aobj1;
        aobj;
        lock.unlock();
        throw aobj;
    }
}
