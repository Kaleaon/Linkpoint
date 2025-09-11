package com.lumiyaviewer.lumiya.react;

import com.lumiyaviewer.lumiya.react.-$Lambda$CF5cnl0on0-506QrOcvyL8_AER8.AnonymousClass1;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Subscription<K, T> implements RefreshableOne {
    @Nullable
    private final Executor executor;
    @Nonnull
    private final K key;
    @Nonnull
    private final OnData<T> onData;
    @Nullable
    private final OnError onError;
    @Nonnull
    private final SubscriptionReference<K, T> reference;
    @Nonnull
    private final Unsubscribable<K, T> subscriptionPool;

    public interface OnData<T> {
        void onData(T t);
    }

    public interface OnError {
        void onError(Throwable th);
    }

    static class SubscriptionReference<K, T> extends WeakReference<Subscription<K, T>> {
        @Nonnull
        private final K key;

        SubscriptionReference(Subscription<K, T> subscription, ReferenceQueue<? super Subscription<K, T>> referenceQueue) {
            super(subscription, referenceQueue);
            this.key = subscription.getKey();
        }

        @Nonnull
        public K getKey() {
            return this.key;
        }
    }

    Subscription(@Nonnull K k, @Nonnull Unsubscribable<K, T> unsubscribable, @Nullable Executor executor, @Nonnull OnData<T> onData, @Nullable OnError onError, @Nullable ReferenceQueue<Subscription<K, T>> referenceQueue) {
        this.key = k;
        this.subscriptionPool = unsubscribable;
        this.onData = onData;
        this.onError = onError;
        this.executor = executor;
        this.reference = new SubscriptionReference(this, referenceQueue);
    }

    @Nonnull
    public final K getKey() {
        return this.key;
    }

    SubscriptionReference<K, T> getReference() {
        return this.reference;
    }

    /* renamed from: handleDataCallback */
    /* synthetic */ void handleDataCallback(Object obj) {
        this.onData.onData(obj);
    }

    /* renamed from: handleErrorCallback */
    /* synthetic */ void handleErrorCallback(Throwable th) {
        this.onError.onError(th);
    }

    void onData(T t) {
        if (this.executor != null) {
            this.executor.execute(new -$Lambda$CF5cnl0on0-506QrOcvyL8_AER8(this, t));
        } else {
            this.onData.onData(t);
        }
    }

    void onError(Throwable th) {
        if (this.onError == null) {
            return;
        }
        if (this.executor != null) {
            this.executor.execute(new AnonymousClass1(this, th));
        } else {
            this.onError.onError(th);
        }
    }

    public void requestRefresh() {
        if (this.subscriptionPool instanceof Refreshable) {
            ((Refreshable) this.subscriptionPool).requestUpdate(this.key);
        }
    }

    public void unsubscribe() {
        this.subscriptionPool.unsubscribe(this);
    }
}
