// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.Debug;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            Unsubscribable, Refreshable, ResultHandler, Subscribable, 
//            RequestSource, SubscriptionList, RequestHandler, DisposeHandler, 
//            Subscription

public class SubscriptionPool
    implements Unsubscribable, Refreshable, ResultHandler, Subscribable, RequestSource
{
    private static class SubscriptionRequestedList extends SubscriptionList
    {

        boolean requested;

        private SubscriptionRequestedList()
        {
            requested = false;
        }

        SubscriptionRequestedList(SubscriptionRequestedList subscriptionrequestedlist)
        {
            this();
        }
    }


    private Executor cacheInvalidateExecutor;
    private Refreshable cacheInvalidateHandler;
    private Executor disposeExecutor;
    private DisposeHandler disposeHandler;
    private final Map entries;
    private final Object lock;
    private final ReferenceQueue refQueue;
    private RequestHandler requestHandler;
    private boolean requestOnce;

    public SubscriptionPool()
    {
        entries = new HashMap();
        lock = new Object();
        refQueue = new ReferenceQueue();
        requestHandler = null;
        disposeHandler = null;
        disposeExecutor = null;
        cacheInvalidateExecutor = null;
        cacheInvalidateHandler = null;
        requestOnce = false;
        requestHandler = null;
    }

    public SubscriptionPool(RequestHandler requesthandler)
    {
        entries = new HashMap();
        lock = new Object();
        refQueue = new ReferenceQueue();
        requestHandler = null;
        disposeHandler = null;
        disposeExecutor = null;
        cacheInvalidateExecutor = null;
        cacheInvalidateHandler = null;
        requestOnce = false;
        requestHandler = requesthandler;
    }

    private void collectReferences()
    {
_L2:
        Subscription.SubscriptionReference subscriptionreference;
        Object obj1;
        java.lang.ref.Reference reference = refQueue.poll();
        if (reference == null)
        {
            break; /* Loop/switch isn't completed */
        }
        if (!(reference instanceof Subscription.SubscriptionReference))
        {
            continue; /* Loop/switch isn't completed */
        }
        subscriptionreference = (Subscription.SubscriptionReference)reference;
        obj1 = subscriptionreference.getKey();
        if (obj1 == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        Debug.Printf("UserPic: collecting reference for %s", new Object[] {
            obj1.toString()
        });
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        SubscriptionList subscriptionlist = (SubscriptionList)entries.get(obj1);
        if (subscriptionlist == null)
        {
            break MISSING_BLOCK_LABEL_124;
        }
        subscriptionlist.removeByReference(subscriptionreference);
        if (subscriptionlist.isEmpty())
        {
            entries.remove(obj1);
            if (requestHandler != null)
            {
                requestHandler.onRequestCancelled(obj1);
            }
            disposeOldData(subscriptionlist);
        }
        obj;
        JVM INSTR monitorexit ;
        if (true) goto _L2; else goto _L1
        Exception exception;
        exception;
        throw exception;
_L1:
        obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Debug.Printf("UserPic: subscriptions = %d", new Object[] {
            Integer.valueOf(entries.size())
        });
        obj;
        JVM INSTR monitorexit ;
        return;
        exception;
        throw exception;
    }

    private void disposeOldData(SubscriptionList subscriptionlist)
    {
label0:
        {
            if (disposeHandler != null)
            {
                subscriptionlist = ((SubscriptionList) (subscriptionlist.getData()));
                if (subscriptionlist != null)
                {
                    if (disposeExecutor == null)
                    {
                        break label0;
                    }
                    disposeExecutor.execute(new _2D_.Lambda.FQ4ueWG6sVQMwgP3YGPP2nbRyFo(this, subscriptionlist));
                }
            }
            return;
        }
        disposeHandler.onDispose(subscriptionlist);
    }

    public ResultHandler attachRequestHandler(RequestHandler requesthandler)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        requestHandler = requesthandler;
        obj;
        JVM INSTR monitorexit ;
        return this;
        requesthandler;
        throw requesthandler;
    }

    public void detachRequestHandler(RequestHandler requesthandler)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (requestHandler == requesthandler)
        {
            requestHandler = null;
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        requesthandler;
        throw requesthandler;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_SubscriptionPool_8106(Object obj)
    {
        disposeHandler.onDispose(obj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_SubscriptionPool_8749(Object obj)
    {
        cacheInvalidateHandler.requestUpdate(obj);
    }

    public void onResultData(Object obj, Object obj1)
    {
        collectReferences();
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        obj = (SubscriptionRequestedList)entries.get(obj);
        if (obj == null) goto _L2; else goto _L1
_L1:
        ((SubscriptionRequestedList) (obj)).setData(obj1);
        obj.requested = false;
        obj = ((SubscriptionRequestedList) (obj)).getSubscriptions(false);
_L4:
        obj2;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((Subscription)((Iterator) (obj)).next()).onData(obj1)) { }
        }
        break MISSING_BLOCK_LABEL_88;
        obj;
        throw obj;
        return;
_L2:
        obj = null;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void onResultError(Object obj, Throwable throwable)
    {
        collectReferences();
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        obj = (SubscriptionRequestedList)entries.get(obj);
        if (obj == null) goto _L2; else goto _L1
_L1:
        ((SubscriptionRequestedList) (obj)).setError(throwable);
        obj.requested = false;
        obj = ((SubscriptionRequestedList) (obj)).getSubscriptions(false);
_L4:
        obj1;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); ((Subscription)((Iterator) (obj)).next()).onError(throwable)) { }
        }
        break MISSING_BLOCK_LABEL_88;
        obj;
        throw obj;
        return;
_L2:
        obj = null;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public void requestUpdate(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        if (cacheInvalidateHandler == null) goto _L2; else goto _L1
_L1:
        if (cacheInvalidateExecutor == null) goto _L4; else goto _L3
_L3:
        cacheInvalidateExecutor.execute(new _2D_.Lambda.FQ4ueWG6sVQMwgP3YGPP2nbRyFo._cls1(this, obj));
_L2:
        SubscriptionRequestedList subscriptionrequestedlist = (SubscriptionRequestedList)entries.get(obj);
        if (subscriptionrequestedlist == null) goto _L6; else goto _L5
_L5:
        if (requestHandler == null) goto _L6; else goto _L7
_L7:
        if (!(requestHandler instanceof Refreshable)) goto _L9; else goto _L8
_L8:
        ((Refreshable)requestHandler).requestUpdate(obj);
_L6:
        obj1;
        JVM INSTR monitorexit ;
        return;
_L4:
        cacheInvalidateHandler.requestUpdate(obj);
          goto _L2
        obj;
        throw obj;
_L9:
        if (requestOnce && !(subscriptionrequestedlist.requested ^ true)) goto _L6; else goto _L10
_L10:
        subscriptionrequestedlist.requested = true;
        requestHandler.onRequest(obj);
          goto _L6
    }

    public void requestUpdateAll()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Iterator iterator = entries.entrySet().iterator();
_L2:
        Object obj1;
        Object obj2;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_115;
            }
            obj2 = (java.util.Map.Entry)iterator.next();
            obj1 = ((java.util.Map.Entry) (obj2)).getKey();
            obj2 = (SubscriptionRequestedList)((java.util.Map.Entry) (obj2)).getValue();
        } while (obj2 == null);
        if (requestHandler == null || requestOnce && !(((SubscriptionRequestedList) (obj2)).requested ^ true)) goto _L2; else goto _L1
_L1:
        obj2.requested = true;
        requestHandler.onRequest(obj1);
          goto _L2
        Exception exception;
        exception;
        throw exception;
        obj;
        JVM INSTR monitorexit ;
    }

    public void requestUpdateSome(Predicate predicate)
    {
        HashSet hashset;
        RequestHandler requesthandler;
        hashset = null;
        requesthandler = requestHandler;
        if (requesthandler == null)
        {
            break MISSING_BLOCK_LABEL_186;
        }
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Iterator iterator = entries.entrySet().iterator();
_L2:
        Object obj1;
        Object obj2;
        do
        {
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_142;
            }
            obj2 = (java.util.Map.Entry)iterator.next();
            obj1 = ((java.util.Map.Entry) (obj2)).getKey();
            obj2 = (SubscriptionRequestedList)((java.util.Map.Entry) (obj2)).getValue();
        } while (obj2 == null);
        if (!predicate.apply(obj1) || requestOnce && !(((SubscriptionRequestedList) (obj2)).requested ^ true))
        {
            continue; /* Loop/switch isn't completed */
        }
        obj2.requested = true;
        if (hashset != null)
        {
            break MISSING_BLOCK_LABEL_130;
        }
        hashset = new HashSet();
        hashset.add(obj1);
        if (true) goto _L2; else goto _L1
_L1:
        obj;
        JVM INSTR monitorexit ;
        if (hashset != null)
        {
            for (predicate = hashset.iterator(); predicate.hasNext(); requesthandler.onRequest(predicate.next())) { }
        }
        break MISSING_BLOCK_LABEL_186;
        predicate;
        throw predicate;
    }

    public SubscriptionPool setCacheInvalidateHandler(Refreshable refreshable, Executor executor)
    {
        cacheInvalidateHandler = refreshable;
        cacheInvalidateExecutor = executor;
        return this;
    }

    public SubscriptionPool setDisposeHandler(DisposeHandler disposehandler, Executor executor)
    {
        disposeHandler = disposehandler;
        disposeExecutor = executor;
        return this;
    }

    public SubscriptionPool setRequestOnce(boolean flag)
    {
        requestOnce = flag;
        return this;
    }

    public Subscription subscribe(Object obj, Subscription.OnData ondata)
    {
        return subscribe(obj, null, ondata, null);
    }

    public Subscription subscribe(Object obj, Subscription.OnData ondata, Subscription.OnError onerror)
    {
        return subscribe(obj, null, ondata, onerror);
    }

    public Subscription subscribe(Object obj, Executor executor, Subscription.OnData ondata)
    {
        return subscribe(obj, executor, ondata, null);
    }

    public Subscription subscribe(Object obj, Executor executor, Subscription.OnData ondata, Subscription.OnError onerror)
    {
        Subscription subscription = new Subscription(obj, this, executor, ondata, onerror, refQueue);
        onerror = ((Subscription.OnError) (lock));
        onerror;
        JVM INSTR monitorenter ;
        boolean flag = false;
        ondata = (SubscriptionRequestedList)entries.get(obj);
        executor = ondata;
        if (ondata != null)
        {
            break MISSING_BLOCK_LABEL_75;
        }
        executor = new SubscriptionRequestedList(null);
        entries.put(obj, executor);
        flag = true;
        executor.addSubscription(subscription);
        ondata = executor.getError();
        if (ondata == null) goto _L2; else goto _L1
_L1:
        subscription.onError(ondata);
_L4:
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_123;
        }
        if (requestHandler != null)
        {
            executor.requested = true;
            requestHandler.onRequest(obj);
        }
        onerror;
        JVM INSTR monitorexit ;
        collectReferences();
        return subscription;
_L2:
        ondata = ((Subscription.OnData) (executor.getData()));
        if (ondata == null)
        {
            continue; /* Loop/switch isn't completed */
        }
        subscription.onData(ondata);
        if (true) goto _L4; else goto _L3
_L3:
        obj;
        throw obj;
    }

    public void unsubscribe(Subscription subscription)
    {
        Object obj1 = subscription.getKey();
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        SubscriptionList subscriptionlist = (SubscriptionList)entries.get(obj1);
        if (subscriptionlist == null)
        {
            break MISSING_BLOCK_LABEL_80;
        }
        subscriptionlist.removeSubscription(subscription);
        if (subscriptionlist.isEmpty())
        {
            entries.remove(obj1);
            if (requestHandler != null)
            {
                requestHandler.onRequestCancelled(obj1);
            }
            disposeOldData(subscriptionlist);
        }
        obj;
        JVM INSTR monitorexit ;
        collectReferences();
        return;
        subscription;
        throw subscription;
    }

    public SubscriptionPool withRequestHandler(RequestHandler requesthandler)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        requestHandler = requesthandler;
        obj;
        JVM INSTR monitorexit ;
        return this;
        requesthandler;
        throw requesthandler;
    }
}
