package com.lumiyaviewer.lumiya.react;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
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

    /* loaded from: classes.dex */
    public interface OnData<T> {
        void onData(T t);
    }

    /* loaded from: classes.dex */
    public interface OnError {
        void onError(Throwable th);
    }

    /* loaded from: classes.dex */
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public Subscription(@Nonnull K k, @Nonnull Unsubscribable<K, T> unsubscribable, @Nullable Executor executor, @Nonnull OnData<T> onData, @Nullable OnError onError, @Nullable ReferenceQueue<Subscription<K, T>> referenceQueue) {
        this.key = k;
        this.subscriptionPool = unsubscribable;
        this.onData = onData;
        this.onError = onError;
        this.executor = executor;
        this.reference = new SubscriptionReference<>(this, referenceQueue);
    }

    @Nonnull
    public final K getKey() {
        return this.key;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SubscriptionReference<K, T> getReference() {
        return this.reference;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_Subscription_1719  reason: not valid java name */
    public /* synthetic */ void m49lambda$com_lumiyaviewer_lumiya_react_Subscription_1719(Object obj) {
        this.onData.onData(obj);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_Subscription_1938  reason: not valid java name */
    public /* synthetic */ void m50lambda$com_lumiyaviewer_lumiya_react_Subscription_1938(Throwable th) {
        this.onError.onError(th);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onData(final T t) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$CF5cnl0on0-506QrOcvyL8_AER8
                private final /* synthetic */ void $m$0() {
                    ((Subscription) this).m49lambda$com_lumiyaviewer_lumiya_react_Subscription_1719(t);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            this.onData.onData(t);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onError(final Throwable th) {
        if (this.onError != null) {
            if (this.executor != null) {
                this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$CF5cnl0on0-506QrOcvyL8_AER8.1
                    private final /* synthetic */ void $m$0() {
                        ((Subscription) this).m50lambda$com_lumiyaviewer_lumiya_react_Subscription_1938((Throwable) th);
                    }

                    @Override // java.lang.Runnable
                    public final void run() {
                        $m$0();
                    }
                });
            } else {
                this.onError.onError(th);
            }
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.RefreshableOne
    public void requestRefresh() {
        if (this.subscriptionPool instanceof Refreshable) {
            ((Refreshable) this.subscriptionPool).requestUpdate(this.key);
        }
    }

    public void unsubscribe() {
        this.subscriptionPool.unsubscribe(this);
    }
}
