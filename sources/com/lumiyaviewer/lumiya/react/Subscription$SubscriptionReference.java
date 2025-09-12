// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            Subscription

static class key extends WeakReference
{

    private final Object key;

    public Object getKey()
    {
        return key;
    }

    (Subscription subscription, ReferenceQueue referencequeue)
    {
        super(subscription, referencequeue);
        key = subscription.getKey();
    }
}
