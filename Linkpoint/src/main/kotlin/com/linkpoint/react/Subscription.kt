package com.linkpoint.react

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

class Subscription<K, T> : RefreshableOne {
    private val Executor executor
    private val K key
    private val OnData<T> onData
    private val OnError onError
    private val SubscriptionReference<K, T> reference
    private val Unsubscribable<K, T> subscriptionPool

    interface OnData<T> {
        Unit onData(T t)
    }

    interface OnError {
        Unit onError(Throwable th)
    }

    static class SubscriptionReference<K, T> : WeakReference<Subscription<K, T>> {
        private val K key

        SubscriptionReference(Subscription<K, T> subscription, ReferenceQueue<? super Subscription<K, T>> referenceQueue) {
            super(subscription, referenceQueue)
            this.key = subscription.getKey()
        }

        public K getKey() {
            return this.key
        }
    }

    Subscription(K k, Unsubscribable<K, T> unsubscribable, Executor executor, OnData<T> onData, OnError onError, ReferenceQueue<Subscription<K, T>> referenceQueue) {
        this.key = k
        this.subscriptionPool = unsubscribable
        this.onData = onData
        this.onError = onError
        this.executor = executor
        this.reference = SubscriptionReference(this, referenceQueue)
    }

    val K getKey() {
        return this.key
    }

    SubscriptionReference<K, T> getReference() {
        return this.reference
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_Subscription_1719  reason: not valid java name */
    public /* synthetic */ Unit m38lambda$com_lumiyaviewer_lumiya_react_Subscription_1719(Object obj) {
        this.onData.onData(obj)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_Subscription_1938  reason: not valid java name */
    public /* synthetic */ Unit m39lambda$com_lumiyaviewer_lumiya_react_Subscription_1938(Throwable th) {
        this.onError.onError(th)
    }

    /* access modifiers changed from: package-private */
    Unit onData(T t) {
        if (this.executor != null) {
            this.executor.execute(() -> this.onData.onData(t))
        } else {
            this.onData.onData(t)
        }
    }

    /* access modifiers changed from: package-private */
    Unit onError(Throwable th) {
        if (this.onError == null) {
            return
        }
        if (this.executor != null) {
            this.executor.execute(() -> this.onError.onError(th))
        } else {
            this.onError.onError(th)
        }
    }

    fun requestRefresh() {
        if (this.subscriptionPool instanceof Refreshable) {
            ((Refreshable) this.subscriptionPool).requestUpdate(this.key)
        }
    }

    fun unsubscribe() {
        this.subscriptionPool.unsubscribe(this)
    }
}
