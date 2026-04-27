// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RefreshableOne, UnsubscribableOne, Subscription, Subscribable

public class SubscriptionData
    implements Subscription.OnData, Subscription.OnError, Loadable, RefreshableOne, UnsubscribableOne
{
    public static class DataNotReadyException extends Exception
    {

        public DataNotReadyException(String s)
        {
            super(s);
        }

        public DataNotReadyException(String s, Throwable throwable)
        {
            super(s, throwable);
        }
    }


    private Object data;
    private Throwable error;
    private final Executor executor;
    private final AtomicBoolean inLoadableListeners;
    private final AtomicInteger listenersInvokeAgain;
    private final List loadableStatusListeners;
    private final Object lock;
    private final Subscription.OnData onData;
    private final Subscription.OnError onError;
    private final AtomicReference subscription;

    public SubscriptionData(Executor executor1)
    {
        lock = new Object();
        subscription = new AtomicReference();
        data = null;
        error = null;
        loadableStatusListeners = new LinkedList();
        inLoadableListeners = new AtomicBoolean(false);
        listenersInvokeAgain = new AtomicInteger(0);
        executor = executor1;
        onData = null;
        onError = null;
    }

    public SubscriptionData(Executor executor1, Subscription.OnData ondata)
    {
        lock = new Object();
        subscription = new AtomicReference();
        data = null;
        error = null;
        loadableStatusListeners = new LinkedList();
        inLoadableListeners = new AtomicBoolean(false);
        listenersInvokeAgain = new AtomicInteger(0);
        executor = executor1;
        onData = ondata;
        onError = null;
    }

    public SubscriptionData(Executor executor1, Subscription.OnData ondata, Subscription.OnError onerror)
    {
        lock = new Object();
        subscription = new AtomicReference();
        data = null;
        error = null;
        loadableStatusListeners = new LinkedList();
        inLoadableListeners = new AtomicBoolean(false);
        listenersInvokeAgain = new AtomicInteger(0);
        executor = executor1;
        onData = ondata;
        onError = onerror;
    }

    private void invokeLoadableListeners()
    {
        if (inLoadableListeners.getAndSet(true))
        {
            break MISSING_BLOCK_LABEL_91;
        }
_L2:
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        ImmutableList immutablelist = ImmutableList.copyOf(loadableStatusListeners);
        obj;
        JVM INSTR monitorexit ;
        for (obj = immutablelist.iterator(); ((Iterator) (obj)).hasNext(); ((com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.LoadableStatusListener)((Iterator) (obj)).next()).onLoadableStatusChange(this, getLoadableStatus())) { }
        continue; /* Loop/switch isn't completed */
        Exception exception;
        exception;
        throw exception;
        if (listenersInvokeAgain.getAndSet(0) != 0) goto _L2; else goto _L1
_L1:
        inLoadableListeners.set(false);
        return;
        listenersInvokeAgain.incrementAndGet();
        return;
    }

    public void addLoadableStatusListener(com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.LoadableStatusListener loadablestatuslistener)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        loadableStatusListeners.add(loadablestatuslistener);
        obj;
        JVM INSTR monitorexit ;
        return;
        loadablestatuslistener;
        throw loadablestatuslistener;
    }

    public void assertHasData()
        throws DataNotReadyException
    {
        get();
    }

    public Object get()
        throws DataNotReadyException
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        Object obj;
        if (data == null)
        {
            break MISSING_BLOCK_LABEL_23;
        }
        obj = data;
        obj1;
        JVM INSTR monitorexit ;
        return obj;
        if (error == null)
        {
            break MISSING_BLOCK_LABEL_56;
        }
        obj = new DataNotReadyException(error.getMessage(), error);
_L1:
        throw obj;
        obj;
        obj1;
        JVM INSTR monitorexit ;
        throw obj;
        obj = new DataNotReadyException("Data not ready");
          goto _L1
    }

    public Object getData()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Object obj1 = data;
        obj;
        JVM INSTR monitorexit ;
        return obj1;
        Exception exception;
        exception;
        throw exception;
    }

    public Throwable getError()
    {
        return error;
    }

    public com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status getLoadableStatus()
    {
        if (subscription.get() == null)
        {
            return com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Idle;
        }
        if (error != null)
        {
            return com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Error;
        }
        if (data != null)
        {
            return com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loaded;
        } else
        {
            return com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable.Status.Loading;
        }
    }

    public boolean hasData()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Object obj1 = data;
        boolean flag;
        if (obj1 != null)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        obj;
        JVM INSTR monitorexit ;
        return flag;
        Exception exception;
        exception;
        throw exception;
    }

    public boolean isSubscribed()
    {
        return subscription.get() != null;
    }

    public void onData(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        data = obj;
        error = null;
        obj1;
        JVM INSTR monitorexit ;
        if (onData != null)
        {
            onData.onData(obj);
        }
        invokeLoadableListeners();
        return;
        obj;
        throw obj;
    }

    public void onError(Throwable throwable)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        data = null;
        error = throwable;
        obj;
        JVM INSTR monitorexit ;
        if (onError != null)
        {
            onError.onError(throwable);
        }
        invokeLoadableListeners();
        return;
        throwable;
        throw throwable;
    }

    public void requestRefresh()
    {
        Subscription subscription1 = (Subscription)subscription.get();
        if (subscription1 != null)
        {
            subscription1.requestRefresh();
        }
    }

    public void subscribe(Subscribable subscribable, Object obj)
    {
        Object obj1 = (Subscription)subscription.getAndSet(null);
        if (obj1 == null) goto _L2; else goto _L1
_L1:
        ((Subscription) (obj1)).unsubscribe();
        obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        data = null;
        error = null;
        obj1;
        JVM INSTR monitorexit ;
_L2:
        subscription.set(subscribable.subscribe(obj, executor, this, this));
        invokeLoadableListeners();
        return;
        subscribable;
        throw subscribable;
    }

    public void unsubscribe()
    {
        Subscription subscription1 = (Subscription)subscription.getAndSet(null);
        if (subscription1 != null)
        {
            subscription1.unsubscribe();
        }
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        data = null;
        error = null;
        obj;
        JVM INSTR monitorexit ;
        invokeLoadableListeners();
        return;
        Exception exception;
        exception;
        throw exception;
    }
}
