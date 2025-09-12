// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            UnsubscribableOne, RequestForwarder, Subscribable, ResultHandler, 
//            Subscription

private class <init>
    implements <init>, <init>, UnsubscribableOne
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
            RequestForwarder._2D_get0(RequestForwarder.this).execute(new it>(this, obj));
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

    private (Object obj, Object obj1)
    {
        this$0 = RequestForwarder.this;
        super();
        key = obj;
        subscription = RequestForwarder._2D_get2(RequestForwarder.this).subscribe(obj1, RequestForwarder._2D_get0(RequestForwarder.this), this, this);
    }

    subscription(Object obj, Object obj1, subscription subscription1)
    {
        this(obj, obj1);
    }
}
