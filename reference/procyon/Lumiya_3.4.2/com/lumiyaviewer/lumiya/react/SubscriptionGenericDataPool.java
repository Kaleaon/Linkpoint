// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.Executor;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

public abstract class SubscriptionGenericDataPool<K, T> implements Subscribable<K, T>, Unsubscribable<K, T>
{
    private boolean canContainNulls;
    private final Object lock;
    
    public SubscriptionGenericDataPool() {
        this.lock = new Object();
        this.canContainNulls = false;
    }
    
    private void invokeSubscription(final Subscription<K, T> subscription, final T t, final Throwable t2) {
        if (t2 != null) {
            subscription.onError(t2);
        }
        else if (t != null || this.canContainNulls) {
            subscription.onData(t);
        }
    }
    
    @Nullable
    protected abstract SubscriptionList<K, T> getExistingSubscriptions(@Nonnull final K p0);
    
    @Nonnull
    protected abstract SubscriptionList<K, T> getSubscriptions(@Nonnull final K p0);
    
    public SubscriptionGenericDataPool<K, T> setCanContainNulls(final boolean canContainNulls) {
        this.canContainNulls = canContainNulls;
        return this;
    }
    
    public void setData(@Nonnull final K k, @Nullable final T t) {
        this.setData(k, t, null);
    }
    
    public void setData(@Nonnull final K k, @Nullable final T data, @Nullable final Throwable error) {
        synchronized (this.lock) {
            final SubscriptionList<K, T> subscriptions = this.getSubscriptions(k);
            if (error != null) {
                subscriptions.setError(error);
            }
            else {
                subscriptions.setData(data);
            }
            final List<Subscription<K, T>> subscriptions2 = subscriptions.getSubscriptions(true);
            monitorexit(this.lock);
            if (subscriptions2 != null) {
                final Iterator<Object> iterator = subscriptions2.iterator();
                while (iterator.hasNext()) {
                    this.invokeSubscription(iterator.next(), data, error);
                }
            }
        }
    }
    
    public void setError(@Nonnull final K k, final Throwable t) {
        this.setData(k, null, t);
    }
    
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
        final Subscription subscription = new Subscription((K)k, (Unsubscribable<K, T>)this, executor, (Subscription.OnData<T>)onData, onError, null);
        synchronized (this.lock) {
            final SubscriptionList<K, T> subscriptions = this.getSubscriptions(k);
            subscriptions.addSubscription(subscription);
            final Throwable error = subscriptions.getError();
            final T data = subscriptions.getData();
            monitorexit(this.lock);
            this.invokeSubscription(subscription, data, error);
            return subscription;
        }
    }
    
    @Override
    public void unsubscribe(final Subscription<K, T> subscription) {
        final K key = subscription.getKey();
        synchronized (this.lock) {
            final SubscriptionList<K, T> existingSubscriptions = this.getExistingSubscriptions(key);
            if (existingSubscriptions != null) {
                existingSubscriptions.removeSubscription(subscription);
            }
        }
    }
}
