// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public class SubscriptionSingleDataPool<T> extends SubscriptionGenericDataPool<SubscriptionSingleKey, T>
{
    private final SubscriptionList<SubscriptionSingleKey, T> entry;
    
    public SubscriptionSingleDataPool() {
        this.entry = new SubscriptionList<SubscriptionSingleKey, T>();
    }
    
    public static SubscriptionSingleKey getSingleDataKey() {
        return SubscriptionSingleKey.Value;
    }
    
    public T getData() {
        return this.entry.getData();
    }
    
    @Nullable
    @Override
    protected SubscriptionList<SubscriptionSingleKey, T> getExistingSubscriptions(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
        return this.entry;
    }
    
    public SubscriptionSingleKey getKey() {
        return SubscriptionSingleKey.Value;
    }
    
    @Nonnull
    @Override
    protected SubscriptionList<SubscriptionSingleKey, T> getSubscriptions(@Nonnull final SubscriptionSingleKey subscriptionSingleKey) {
        return this.entry;
    }
}
