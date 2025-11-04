// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public interface Subscribable<K, T>
{
    Subscription<K, T> subscribe(@Nonnull final K p0, @Nonnull final Subscription.OnData<T> p1);
    
    Subscription<K, T> subscribe(@Nonnull final K p0, @Nonnull final Subscription.OnData<T> p1, @Nullable final Subscription.OnError p2);
    
    Subscription<K, T> subscribe(@Nonnull final K p0, @Nullable final Executor p1, @Nonnull final Subscription.OnData<T> p2);
    
    Subscription<K, T> subscribe(@Nonnull final K p0, @Nullable final Executor p1, @Nonnull final Subscription.OnData<T> p2, @Nullable final Subscription.OnError p3);
}
