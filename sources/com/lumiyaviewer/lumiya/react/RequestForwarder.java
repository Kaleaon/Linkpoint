// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RequestHandler, RequestSource, ResultHandler, Subscribable, 
//            UnsubscribableOne, Subscription

public abstract class RequestForwarder
    implements RequestHandler
{
    private class DownstreamSubscription
        implements Subscription.OnData, Subscription.OnError, UnsubscribableOne
    {

        private final Object key;
        private final Subscription subscription;
        final RequestForwarder this$0;

        void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestForwarder$DownstreamSubscription_827(Object obj)
        {
            RequestForwarder._2D_wrap0(RequestForwarder.this, key, obj);
        }

        public void onData(Object obj)
        {
            if (RequestForwarder._2D_get0(RequestForwarder.this) != null)
            {
                RequestForwarder._2D_get0(RequestForwarder.this).execute(new _2D_.Lambda.swF2K5wPKI2_xA_2D_bWP_2D_XwHVnywU(this, obj));
                return;
            } else
            {
                RequestForwarder._2D_wrap0(RequestForwarder.this, key, obj);
                return;
            }
        }

        public void onError(Throwable throwable)
        {
            RequestForwarder._2D_get1(RequestForwarder.this).onResultError(key, throwable);
        }

        public void unsubscribe()
        {
            subscription.unsubscribe();
        }

        private DownstreamSubscription(Object obj, Object obj1)
        {
            this$0 = RequestForwarder.this;
            super();
            key = obj;
            subscription = RequestForwarder._2D_get2(RequestForwarder.this).subscribe(obj1, RequestForwarder._2D_get0(RequestForwarder.this), this, this);
        }

        DownstreamSubscription(Object obj, Object obj1, DownstreamSubscription downstreamsubscription)
        {
            this(obj, obj1);
        }
    }


    private final Executor executor;
    private final Object lock = new Object();
    private final ResultHandler resultHandler;
    private final Subscribable subscribable;
    private final Map subscriptions = new HashMap();

    static Executor _2D_get0(RequestForwarder requestforwarder)
    {
        return requestforwarder.executor;
    }

    static ResultHandler _2D_get1(RequestForwarder requestforwarder)
    {
        return requestforwarder.resultHandler;
    }

    static Subscribable _2D_get2(RequestForwarder requestforwarder)
    {
        return requestforwarder.subscribable;
    }

    static void _2D_wrap0(RequestForwarder requestforwarder, Object obj, Object obj1)
    {
        requestforwarder.processResultInternal(obj, obj1);
    }

    public RequestForwarder(RequestSource requestsource, Subscribable subscribable1, Executor executor1)
    {
        executor = executor1;
        subscribable = subscribable1;
        resultHandler = requestsource.attachRequestHandler(this);
    }

    private void processRequestInternal(Object obj)
    {
        DownstreamSubscription downstreamsubscription = new DownstreamSubscription(obj, getDownstreamKey(obj), null);
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        obj = (DownstreamSubscription)subscriptions.put(obj, downstreamsubscription);
        obj1;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            ((DownstreamSubscription) (obj)).unsubscribe();
        }
        return;
        obj;
        throw obj;
    }

    private void processResultInternal(Object obj, Object obj1)
    {
        obj1 = processResult(obj1);
        resultHandler.onResultData(obj, obj1);
    }

    protected abstract Object getDownstreamKey(Object obj);

    void lambda$_2D_com_lumiyaviewer_lumiya_react_RequestForwarder_2672(Object obj)
    {
        processRequestInternal(obj);
    }

    public void onRequest(Object obj)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.swF2K5wPKI2_xA_2D_bWP_2D_XwHVnywU._cls1(this, obj));
            return;
        } else
        {
            processRequestInternal(obj);
            return;
        }
    }

    public void onRequestCancelled(Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        obj = (DownstreamSubscription)subscriptions.remove(obj);
        obj1;
        JVM INSTR monitorexit ;
        if (obj != null)
        {
            ((DownstreamSubscription) (obj)).unsubscribe();
        }
        return;
        obj;
        throw obj;
    }

    protected abstract Object processResult(Object obj);
}
