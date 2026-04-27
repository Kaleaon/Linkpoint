// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            SubscriptionGenericDataPool, SubscriptionList

public class SubscriptionDataPool extends SubscriptionGenericDataPool
{

    private final Map entries = new HashMap();

    public SubscriptionDataPool()
    {
    }

    protected SubscriptionList getExistingSubscriptions(Object obj)
    {
        return (SubscriptionList)entries.get(obj);
    }

    protected SubscriptionList getSubscriptions(Object obj)
    {
        SubscriptionList subscriptionlist1 = (SubscriptionList)entries.get(obj);
        SubscriptionList subscriptionlist = subscriptionlist1;
        if (subscriptionlist1 == null)
        {
            subscriptionlist = new SubscriptionList();
            entries.put(obj, subscriptionlist);
        }
        return subscriptionlist;
    }

    public SubscriptionDataPool setCanContainNulls(boolean flag)
    {
        super.setCanContainNulls(flag);
        return this;
    }

    public volatile SubscriptionGenericDataPool setCanContainNulls(boolean flag)
    {
        return setCanContainNulls(flag);
    }
}
