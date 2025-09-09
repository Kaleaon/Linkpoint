package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.Subscription;
import java.lang.ref.ReferenceQueue;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SubscriptionGenericDataPool<K, T> implements Subscribable<K, T>, Unsubscribable<K, T> {
    private boolean canContainNulls = false;
    private final Object lock = new Object();

    private void invokeSubscription(Subscription<K, T> subscription, T t, Throwable th) {
        if (th != null) {
            subscription.onError(th);
        } else if (t != null || this.canContainNulls) {
            subscription.onData(t);
        }
    }

    /* access modifiers changed from: protected */
    @Nullable
    public abstract SubscriptionList<K, T> getExistingSubscriptions(@Nonnull K k);

    /* access modifiers changed from: protected */
    @Nonnull
    public abstract SubscriptionList<K, T> getSubscriptions(@Nonnull K k);

    public SubscriptionGenericDataPool<K, T> setCanContainNulls(boolean z) {
        this.canContainNulls = z;
        return this;
    }

    public void setData(@Nonnull K k, @Nullable T t) {
        setData(k, t, (Throwable) null);
    }

    public void setData(@Nonnull K k, @Nullable T t, @Nullable Throwable th) {
        List<Subscription> subscriptions;
        synchronized (this.lock) {
            SubscriptionList subscriptions2 = getSubscriptions(k);
            if (th != null) {
                subscriptions2.setError(th);
            } else {
                subscriptions2.setData(t);
            }
            subscriptions = subscriptions2.getSubscriptions(true);
        }
        if (subscriptions != null) {
            for (Subscription invokeSubscription : subscriptions) {
                invokeSubscription(invokeSubscription, t, th);
            }
        }
    }

    public void setError(@Nonnull K k, Throwable th) {
        setData(k, (Object) null, th);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, (Executor) null, onData, (Subscription.OnError) null);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        return subscribe(k, (Executor) null, onData, onError);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData) {
        return subscribe(k, executor, onData, (Subscription.OnError) null);
    }

    public Subscription<K, T> subscribe(@Nonnull K k, @Nullable Executor executor, @Nonnull Subscription.OnData<T> onData, @Nullable Subscription.OnError onError) {
        Throwable error;
        Object data;
        Subscription<K, T> subscription = new Subscription<>(k, this, executor, onData, onError, (ReferenceQueue) null);
        synchronized (this.lock) {
            SubscriptionList subscriptions = getSubscriptions(k);
            subscriptions.addSubscription(subscription);
            error = subscriptions.getError();
            data = subscriptions.getData();
        }
        invokeSubscription(subscription, data, error);
        return subscription;
    }

    public void unsubscribe(Subscription<K, T> subscription) {
        K key = subscription.getKey();
        synchronized (this.lock) {
            SubscriptionList existingSubscriptions = getExistingSubscriptions(key);
            if (existingSubscriptions != null) {
                existingSubscriptions.removeSubscription(subscription);
            }
        }
    }
}
