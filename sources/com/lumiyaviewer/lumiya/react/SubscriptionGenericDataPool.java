// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.Iterator;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            Subscribable, Unsubscribable, Subscription, SubscriptionList

public abstract class SubscriptionGenericDataPool
    implements Subscribable, Unsubscribable
{

    private boolean canContainNulls;
    private final Object lock = new Object();

    public SubscriptionGenericDataPool()
    {
        canContainNulls = false;
    }

    private void invokeSubscription(Subscription subscription, Object obj, Throwable throwable)
    {
        if (throwable != null)
        {
            subscription.onError(throwable);
        } else
        if (obj != null || canContainNulls)
        {
            subscription.onData(obj);
            return;
        }
    }

    protected abstract SubscriptionList getExistingSubscriptions(Object obj);

    protected abstract SubscriptionList getSubscriptions(Object obj);

    public SubscriptionGenericDataPool setCanContainNulls(boolean flag)
    {
        canContainNulls = flag;
        return this;
    }

    public void setData(Object obj, Object obj1)
    {
        setData(obj, obj1, null);
    }

    public void setData(Object obj, Object obj1, Throwable throwable)
    {
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        obj = getSubscriptions(obj);
        if (throwable == null)
        {
            break MISSING_BLOCK_LABEL_71;
        }
        ((SubscriptionList) (obj)).setError(throwable);
_L1:
        obj = ((SubscriptionList) (obj)).getSubscriptions(true);
        obj2;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); invokeSubscription((Subscription)((Iterator) (obj)).next(), obj1, throwable)) { }
        }
        break MISSING_BLOCK_LABEL_85;
        ((SubscriptionList) (obj)).setData(obj1);
          goto _L1
        obj;
        throw obj;
    }

    public void setError(Object obj, Throwable throwable)
    {
        setData(obj, null, throwable);
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
        ondata = new Subscription(obj, this, executor, ondata, onerror, null);
        executor = ((Executor) (lock));
        executor;
        JVM INSTR monitorenter ;
        onerror = getSubscriptions(obj);
        onerror.addSubscription(ondata);
        obj = onerror.getError();
        onerror = ((Subscription.OnError) (onerror.getData()));
        executor;
        JVM INSTR monitorexit ;
        invokeSubscription(ondata, onerror, ((Throwable) (obj)));
        return ondata;
        obj;
        throw obj;
    }

    public void unsubscribe(Subscription subscription)
    {
        Object obj1 = subscription.getKey();
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        obj1 = getExistingSubscriptions(obj1);
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_27;
        }
        ((SubscriptionList) (obj1)).removeSubscription(subscription);
        obj;
        JVM INSTR monitorexit ;
        return;
        subscription;
        throw subscription;
    }
}
