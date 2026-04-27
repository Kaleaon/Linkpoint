package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.Subscription;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public abstract class SubscriptionGenericDataPool<K, T> implements Subscribable<K, T>, Unsubscribable<K, T> {
    private final Object lock = new Object();
    private boolean canContainNulls = false;

    private void invokeSubscription(Subscription<K, T> subscription, T t, Throwable th) {
        if (th != null) {
            subscription.onError(th);
        } else if (t != null || this.canContainNulls) {
            subscription.onData(t);
        }
    }

    @Nullable
    protected abstract SubscriptionList<K, T> getExistingSubscriptions(@Nonnull K k);

    @Nonnull
    protected abstract SubscriptionList<K, T> getSubscriptions(@Nonnull K k);

    public SubscriptionGenericDataPool<K, T> setCanContainNulls(boolean z) {
        this.canContainNulls = z;
        return this;
    }

    public void setData(@Nonnull K k, @Nullable T t) {
        setData(k, t, null);
    }

    public void setData(@Nonnull K k, @Nullable T t, @Nullable Throwable th) {
        List<Subscription<K, T>> subscriptions;
        synchronized (this.lock) {
            SubscriptionList<K, T> subscriptions2 = getSubscriptions(k);
            if (th != null) {
                subscriptions2.setError(th);
            } else {
                subscriptions2.setData(t);
            }
            subscriptions = subscriptions2.getSubscriptions(true);
        }
        if (subscriptions != null) {
            Iterator<T> it = subscriptions.iterator();
            while (it.hasNext()) {
                invokeSubscription((Subscription) it.next(), t, th);
            }
        }
    }

    public void setError(@Nonnull K k, Throwable th) {
        setData(k, null, th);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, null, onData, null);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        return subscribe(k, null, onData, onError);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, executor, onData, null);
    }

    @Override // com.lumiyaviewer.lumiya.react.Subscribable
    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        Throwable error;
        T data;
        Subscription<K, T> subscription = new Subscription<>(k, this, executor, onData, onError, null);
        synchronized (this.lock) {
            SubscriptionList<K, T> subscriptions = getSubscriptions(k);
            subscriptions.addSubscription(subscription);
            error = subscriptions.getError();
            data = subscriptions.getData();
        }
        invokeSubscription(subscription, data, error);
        return subscription;
    }

    @Override // com.lumiyaviewer.lumiya.react.Unsubscribable
    public void unsubscribe(Subscription<K, T> subscription) {
        K key = subscription.getKey();
        synchronized (this.lock) {
            SubscriptionList<K, T> existingSubscriptions = getExistingSubscriptions(key);
            if (existingSubscriptions != null) {
                existingSubscriptions.removeSubscription(subscription);
            }
        }
    }
}
