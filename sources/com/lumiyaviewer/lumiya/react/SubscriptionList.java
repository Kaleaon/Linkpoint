// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            Subscription

public class SubscriptionList
{

    private Object lastData;
    private Throwable lastError;
    private final Set subscriptions = new HashSet();

    public SubscriptionList()
    {
        lastData = null;
        lastError = null;
    }

    public final void addSubscription(Subscription subscription)
    {
        subscriptions.add(subscription.getReference());
    }

    public final Object getData()
    {
        return lastData;
    }

    public final Throwable getError()
    {
        return lastError;
    }

    public final List getSubscriptions(boolean flag)
    {
        Iterator iterator = subscriptions.iterator();
        ArrayList arraylist = null;
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            Subscription subscription = (Subscription)((Subscription.SubscriptionReference)iterator.next()).get();
            if (subscription != null)
            {
                ArrayList arraylist1 = arraylist;
                if (arraylist == null)
                {
                    arraylist1 = new ArrayList(subscriptions.size());
                }
                arraylist1.add(subscription);
                arraylist = arraylist1;
            } else
            if (flag)
            {
                iterator.remove();
            }
        } while (true);
        return arraylist;
    }

    public final boolean isEmpty()
    {
        return subscriptions.isEmpty();
    }

    public final void removeByReference(Subscription.SubscriptionReference subscriptionreference)
    {
        subscriptions.remove(subscriptionreference);
    }

    public final void removeSubscription(Subscription subscription)
    {
        subscriptions.remove(subscription.getReference());
    }

    public final void setData(Object obj)
    {
        lastData = obj;
        lastError = null;
    }

    public final void setError(Throwable throwable)
    {
        lastData = null;
        lastError = throwable;
    }
}
