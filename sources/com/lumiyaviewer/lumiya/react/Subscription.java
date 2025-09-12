// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            RefreshableOne, Refreshable, Unsubscribable

public class Subscription
    implements RefreshableOne
{
    public static interface OnData
    {

        public abstract void onData(Object obj);
    }

    public static interface OnError
    {

        public abstract void onError(Throwable throwable);
    }

    static class SubscriptionReference extends WeakReference
    {

        private final Object key;

        public Object getKey()
        {
            return key;
        }

        SubscriptionReference(Subscription subscription, ReferenceQueue referencequeue)
        {
            super(subscription, referencequeue);
            key = subscription.getKey();
        }
    }


    private final Executor executor;
    private final Object key;
    private final OnData onData;
    private final OnError onError;
    private final SubscriptionReference reference;
    private final Unsubscribable subscriptionPool;

    Subscription(Object obj, Unsubscribable unsubscribable, Executor executor1, OnData ondata, OnError onerror, ReferenceQueue referencequeue)
    {
        key = obj;
        subscriptionPool = unsubscribable;
        onData = ondata;
        onError = onerror;
        executor = executor1;
        reference = new SubscriptionReference(this, referencequeue);
    }

    public final Object getKey()
    {
        return key;
    }

    SubscriptionReference getReference()
    {
        return reference;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_Subscription_1719(Object obj)
    {
        onData.onData(obj);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_react_Subscription_1938(Throwable throwable)
    {
        onError.onError(throwable);
    }

    void onData(Object obj)
    {
        if (executor != null)
        {
            executor.execute(new _2D_.Lambda.CF5cnl0on0_2D_506QrOcvyL8_AER8(this, obj));
            return;
        } else
        {
            onData.onData(obj);
            return;
        }
    }

    void onError(Throwable throwable)
    {
label0:
        {
            if (onError != null)
            {
                if (executor == null)
                {
                    break label0;
                }
                executor.execute(new _2D_.Lambda.CF5cnl0on0_2D_506QrOcvyL8_AER8._cls1(this, throwable));
            }
            return;
        }
        onError.onError(throwable);
    }

    public void requestRefresh()
    {
        if (subscriptionPool instanceof Refreshable)
        {
            ((Refreshable)subscriptionPool).requestUpdate(key);
        }
    }

    public void unsubscribe()
    {
        subscriptionPool.unsubscribe(this);
    }
}
