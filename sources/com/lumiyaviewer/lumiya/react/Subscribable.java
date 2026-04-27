// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            Subscription

public interface Subscribable
{

    public abstract Subscription subscribe(Object obj, Subscription.OnData ondata);

    public abstract Subscription subscribe(Object obj, Subscription.OnData ondata, Subscription.OnError onerror);

    public abstract Subscription subscribe(Object obj, Executor executor, Subscription.OnData ondata);

    public abstract Subscription subscribe(Object obj, Executor executor, Subscription.OnData ondata, Subscription.OnError onerror);
}
