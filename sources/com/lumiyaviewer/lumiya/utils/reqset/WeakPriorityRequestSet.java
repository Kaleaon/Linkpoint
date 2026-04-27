// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.reqset;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Referenced classes of package com.lumiyaviewer.lumiya.utils.reqset:
//            RequestListener, WeakRequestSet

public class WeakPriorityRequestSet
{

    private final Set listeners = new HashSet();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty;
    private final Map priorityBins = new TreeMap();

    public WeakPriorityRequestSet()
    {
        notEmpty = lock.newCondition();
    }

    private void invokeListeners()
    {
        Object obj;
        obj = new LinkedList();
        lock.lock();
        Iterator iterator = listeners.iterator();
_L1:
        RequestListener requestlistener;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_88;
        }
        requestlistener = (RequestListener)((WeakReference)iterator.next()).get();
        if (requestlistener != null)
        {
            break MISSING_BLOCK_LABEL_77;
        }
        iterator.remove();
          goto _L1
        obj;
        lock.unlock();
        throw obj;
        ((List) (obj)).add(requestlistener);
          goto _L1
        lock.unlock();
        for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((RequestListener)((Iterator) (obj)).next()).onNewRequest()) { }
        return;
    }

    public void addListener(RequestListener requestlistener)
    {
        lock.lock();
        Iterator iterator = listeners.iterator();
        boolean flag = false;
_L2:
        RequestListener requestlistener1;
        if (!iterator.hasNext())
        {
            break MISSING_BLOCK_LABEL_60;
        }
        requestlistener1 = (RequestListener)((WeakReference)iterator.next()).get();
        if (requestlistener1 != null)
        {
            break MISSING_BLOCK_LABEL_108;
        }
        iterator.remove();
        continue; /* Loop/switch isn't completed */
        if (flag)
        {
            break MISSING_BLOCK_LABEL_83;
        }
        listeners.add(new WeakReference(requestlistener));
        lock.unlock();
        return;
        requestlistener;
        lock.unlock();
        throw requestlistener;
        if (requestlistener1 == requestlistener)
        {
            flag = true;
        }
        if (true) goto _L2; else goto _L1
_L1:
    }

    public void addRequest(int i, Object obj, Object obj1)
    {
        lock.lock();
        WeakRequestSet weakrequestset1 = (WeakRequestSet)priorityBins.get(Integer.valueOf(i));
        WeakRequestSet weakrequestset;
        weakrequestset = weakrequestset1;
        if (weakrequestset1 != null)
        {
            break MISSING_BLOCK_LABEL_61;
        }
        weakrequestset = new WeakRequestSet();
        priorityBins.put(Integer.valueOf(i), weakrequestset);
        boolean flag = weakrequestset.addRequest(obj, obj1);
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_84;
        }
        notEmpty.signalAll();
        lock.unlock();
        if (flag)
        {
            invokeListeners();
        }
        return;
        obj;
        lock.unlock();
        throw obj;
    }

    public void completeRequest(Object obj)
    {
        lock.lock();
        for (Iterator iterator = priorityBins.values().iterator(); iterator.hasNext(); ((WeakRequestSet)iterator.next()).completeRequest(obj)) { }
        break MISSING_BLOCK_LABEL_61;
        obj;
        lock.unlock();
        throw obj;
        lock.unlock();
        return;
    }

    public Object getRequest()
    {
        Object obj;
        obj = null;
        lock.lock();
        Iterator iterator = priorityBins.entrySet().iterator();
_L2:
        Object obj1;
        if (!iterator.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj1 = ((WeakRequestSet)((java.util.Map.Entry)iterator.next()).getValue()).getRequest();
        obj = obj1;
        if (obj1 == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        obj = obj1;
        break; /* Loop/switch isn't completed */
        if (true) goto _L2; else goto _L1
_L1:
        lock.unlock();
        return obj;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }

    public void removeListener(RequestListener requestlistener)
    {
        lock.lock();
        Iterator iterator = listeners.iterator();
_L2:
        RequestListener requestlistener1;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_74;
            }
            requestlistener1 = (RequestListener)((WeakReference)iterator.next()).get();
        } while (requestlistener1 != null && requestlistener1 != requestlistener);
        iterator.remove();
        if (true) goto _L2; else goto _L1
_L1:
        requestlistener;
        lock.unlock();
        throw requestlistener;
        lock.unlock();
        return;
    }

    public void waitRequest()
        throws InterruptedException
    {
        lock.lock();
        if (getRequest() == null)
        {
            notEmpty.await();
        }
        lock.unlock();
        return;
        Exception exception;
        exception;
        lock.unlock();
        throw exception;
    }
}
