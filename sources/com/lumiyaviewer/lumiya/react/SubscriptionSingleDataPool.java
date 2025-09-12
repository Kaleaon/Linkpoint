// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.react;


// Referenced classes of package com.lumiyaviewer.lumiya.react:
//            SubscriptionGenericDataPool, SubscriptionList, SubscriptionSingleKey

public class SubscriptionSingleDataPool extends SubscriptionGenericDataPool
{

    private final SubscriptionList entry = new SubscriptionList();

    public SubscriptionSingleDataPool()
    {
    }

    public static SubscriptionSingleKey getSingleDataKey()
    {
        return SubscriptionSingleKey.Value;
    }

    public Object getData()
    {
        return entry.getData();
    }

    protected SubscriptionList getExistingSubscriptions(SubscriptionSingleKey subscriptionsinglekey)
    {
        return entry;
    }

    protected volatile SubscriptionList getExistingSubscriptions(Object obj)
    {
        return getExistingSubscriptions((SubscriptionSingleKey)obj);
    }

    public SubscriptionSingleKey getKey()
    {
        return SubscriptionSingleKey.Value;
    }

    protected SubscriptionList getSubscriptions(SubscriptionSingleKey subscriptionsinglekey)
    {
        return entry;
    }

    protected volatile SubscriptionList getSubscriptions(Object obj)
    {
        return getSubscriptions((SubscriptionSingleKey)obj);
    }
}
