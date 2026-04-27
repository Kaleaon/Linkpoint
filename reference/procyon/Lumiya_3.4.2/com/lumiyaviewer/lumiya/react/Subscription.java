// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public class Subscription<K, T> implements RefreshableOne
{
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
    
    Subscription(@Nonnull final K key, @Nonnull final Unsubscribable<K, T> subscriptionPool, @Nullable final Executor executor, @Nonnull final OnData<T> onData, @Nullable final OnError onError, @Nullable final ReferenceQueue<Subscription<K, T>> referenceQueue) {
        this.key = key;
        this.subscriptionPool = subscriptionPool;
        this.onData = onData;
        this.onError = onError;
        this.executor = executor;
        this.reference = new SubscriptionReference<K, T>(this, referenceQueue);
    }
    
    @Nonnull
    public final K getKey() {
        return this.key;
    }
    
    SubscriptionReference<K, T> getReference() {
        return this.reference;
    }
    
    void onData(final T t) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$CF5cnl0on0_506QrOcvyL8_AER8(this, t));
        }
        else {
            this.onData.onData(t);
        }
    }
    
    void onError(final Throwable t) {
        if (this.onError != null) {
            if (this.executor != null) {
                this.executor.execute(new _$Lambda$CF5cnl0on0_506QrOcvyL8_AER8$1(this, t));
            }
            else {
                this.onError.onError(t);
            }
        }
    }
    
    @Override
    public void requestRefresh() {
        if (this.subscriptionPool instanceof Refreshable) {
            ((Refreshable)this.subscriptionPool).requestUpdate(this.key);
        }
    }
    
    public void unsubscribe() {
        this.subscriptionPool.unsubscribe(this);
    }
    
    public interface OnData<T>
    {
        void onData(final T p0);
    }
    
    public interface OnError
    {
        void onError(final Throwable p0);
    }
    
    static class SubscriptionReference<K, T> extends WeakReference<Subscription<K, T>>
    {
        @Nonnull
        private final K key;
        
        SubscriptionReference(final Subscription<K, T> referent, final ReferenceQueue<? super Subscription<K, T>> q) {
            super(referent, q);
            this.key = referent.getKey();
        }
        
        @Nonnull
        public K getKey() {
            return this.key;
        }
    }
}
