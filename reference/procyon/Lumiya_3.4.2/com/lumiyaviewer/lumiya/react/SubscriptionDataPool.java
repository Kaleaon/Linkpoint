// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class SubscriptionDataPool<K, T> extends SubscriptionGenericDataPool<K, T>
{
    private final Map<K, SubscriptionList<K, T>> entries;
    
    public SubscriptionDataPool() {
        this.entries = new HashMap<K, SubscriptionList<K, T>>();
    }
    
    @Nullable
    @Override
    protected SubscriptionList<K, T> getExistingSubscriptions(@Nonnull final K k) {
        return this.entries.get(k);
    }
    
    @Nonnull
    @Override
    protected SubscriptionList<K, T> getSubscriptions(@Nonnull final K k) {
        SubscriptionList list;
        if ((list = this.entries.get(k)) == null) {
            list = new SubscriptionList();
            this.entries.put(k, list);
        }
        return list;
    }
    
    @Override
    public SubscriptionDataPool<K, T> setCanContainNulls(final boolean canContainNulls) {
        super.setCanContainNulls(canContainNulls);
        return this;
    }
}
