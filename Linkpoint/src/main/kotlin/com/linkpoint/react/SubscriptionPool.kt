package com.linkpoint.react

import javax.annotation.Nonnull

class SubscriptionPool<K, T> : Unsubscribable<K, T>, Refreshable<K>, ResultHandler<K, T>, Subscribable<K, T>, RequestSource<K, T> {
    
    private DisposeHandler<T> disposeHandler
    
    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_SubscriptionPool_8106  reason: not valid java name */
    public /* synthetic */ Unit m40lambda$com_lumiyaviewer_lumiya_react_SubscriptionPool_8106(Object obj) {
        if (this.disposeHandler != null) {
            this.disposeHandler.onDispose(obj)
        }
    }
    
    // Stub implementations
    override Unit unsubscribe(Subscription<K, T> subscription) {}
    
    override Unit requestUpdate(K k) {}
    
    override Unit onResultData(K k, T t) {}
    
    override Unit onResultError(K k, Throwable th) {}
    
    override Subscription<K, T> subscribe(K k, Subscription.OnData<T> onData) {
        return null
    }
    
    override Subscription<K, T> subscribe(K k, Subscription.OnData<T> onData, @javax.annotation.Nullable Subscription.OnError onError) {
        return null
    }
    
    override Subscription<K, T> subscribe(K k, @javax.annotation.Nullable java.util.concurrent.Executor executor, Subscription.OnData<T> onData) {
        return null
    }
    
    override Subscription<K, T> subscribe(K k, @javax.annotation.Nullable java.util.concurrent.Executor executor, Subscription.OnData<T> onData, @javax.annotation.Nullable Subscription.OnError onError) {
        return null
    }
    
    override ResultHandler<K, T> attachRequestHandler(RequestHandler<K> requestHandler) {
        return this
    }
    
    override Unit detachRequestHandler(RequestHandler<K> requestHandler) {}
}