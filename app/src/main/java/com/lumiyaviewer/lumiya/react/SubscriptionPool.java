package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;

public class SubscriptionPool<K, T> implements Unsubscribable<K, T>, Refreshable<K>, ResultHandler<K, T>, Subscribable<K, T>, RequestSource<K, T> {
    
    private DisposeHandler<T> disposeHandler;
    
    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_SubscriptionPool_8106  reason: not valid java name */
    public /* synthetic */ void m40lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8106(Object obj) {
        if (this.disposeHandler != null) {
            this.disposeHandler.onDispose(obj);
        }
    }
    
    // Stub implementations
    @Override
    public void unsubscribe(Subscription<K, T> subscription) {}
    
    @Override
    public void requestUpdate(K k) {}
    
    @Override
    public void onResultData(@Nonnull K k, T t) {}
    
    @Override
    public void onResultError(@Nonnull K k, Throwable th) {}
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData) {
        return null;
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull K k, @Nonnull Subscription.OnData<T> onData, @javax.annotation.Nullable Subscription.OnError onError) {
        return null;
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull K k, @javax.annotation.Nullable java.util.concurrent.Executor executor, @Nonnull Subscription.OnData<T> onData) {
        return null;
    }
    
    @Override
    public Subscription<K, T> subscribe(@Nonnull K k, @javax.annotation.Nullable java.util.concurrent.Executor executor, @Nonnull Subscription.OnData<T> onData, @javax.annotation.Nullable Subscription.OnError onError) {
        return null;
    }
    
    @Override
    public ResultHandler<K, T> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        return this;
    }
    
    @Override
    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {}
}