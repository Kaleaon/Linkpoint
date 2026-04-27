// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nullable;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public class SubscriptionPoolUncached<K, T> implements Subscribable<K, T>
{
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nonnull final Subscription.OnData<T> onData) {
        return this.subscribe(k, null, onData, null);
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nonnull final Subscription.OnData<T> onData, @Nullable final Subscription.OnError onError) {
        return this.subscribe(k, null, onData, onError);
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nullable final Executor executor, @Nonnull final Subscription.OnData<T> onData) {
        return this.subscribe(k, executor, onData, null);
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull final K k, @Nullable final Executor executor, @Nonnull final Subscription.OnData<T> onData, @Nullable final Subscription.OnError onError) {
        return null;
    }
}
