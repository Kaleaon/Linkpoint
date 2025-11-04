// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class SubscriptionList<K, T>
{
    private T lastData;
    private Throwable lastError;
    private final Set<Subscription.SubscriptionReference<K, T>> subscriptions;
    
    public SubscriptionList() {
        this.subscriptions = new HashSet<Subscription.SubscriptionReference<K, T>>();
        this.lastData = null;
        this.lastError = null;
    }
    
    public final void addSubscription(final Subscription<K, T> subscription) {
        this.subscriptions.add(subscription.getReference());
    }
    
    public final T getData() {
        return this.lastData;
    }
    
    public final Throwable getError() {
        return this.lastError;
    }
    
    @Nullable
    public final List<Subscription<K, T>> getSubscriptions(final boolean b) {
        final Iterator<Subscription.SubscriptionReference<K, T>> iterator = this.subscriptions.iterator();
        List<Subscription> list = null;
        while (iterator.hasNext()) {
            final Subscription subscription = (Subscription)((Subscription.SubscriptionReference)iterator.next()).get();
            if (subscription != null) {
                List<Subscription> list2;
                if ((list2 = list) == null) {
                    list2 = new ArrayList<Subscription>(this.subscriptions.size());
                }
                list2.add(subscription);
                list = list2;
            }
            else {
                if (!b) {
                    continue;
                }
                iterator.remove();
            }
        }
        return (List<Subscription<K, T>>)list;
    }
    
    public final boolean isEmpty() {
        return this.subscriptions.isEmpty();
    }
    
    public final void removeByReference(final Subscription.SubscriptionReference subscriptionReference) {
        this.subscriptions.remove(subscriptionReference);
    }
    
    public final void removeSubscription(final Subscription<K, T> subscription) {
        this.subscriptions.remove(subscription.getReference());
    }
    
    public final void setData(final T lastData) {
        this.lastData = lastData;
        this.lastError = null;
    }
    
    public final void setError(final Throwable lastError) {
        this.lastData = null;
        this.lastError = lastError;
    }
}
