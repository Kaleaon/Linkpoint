// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            Subscribable, Subscription

public class SubscriptionPoolUncached
    implements Subscribable
{

    public SubscriptionPoolUncached()
    {
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
        return null;
    }
}
