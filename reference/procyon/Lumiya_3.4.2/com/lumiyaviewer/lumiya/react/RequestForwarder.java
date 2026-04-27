// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public abstract class RequestForwarder<Kup, Tup, Kdown, Tdown> implements RequestHandler<Kup>
{
    @Nullable
    private final Executor executor;
    private final Object lock;
    @Nonnull
    private final ResultHandler<Kup, Tup> resultHandler;
    @Nonnull
    private final Subscribable<Kdown, Tdown> subscribable;
    private final Map<Kup, DownstreamSubscription> subscriptions;
    
    public RequestForwarder(@Nonnull final RequestSource<Kup, Tup> requestSource, @Nonnull final Subscribable<Kdown, Tdown> subscribable, @Nullable final Executor executor) {
        this.lock = new Object();
        this.subscriptions = new HashMap<Kup, DownstreamSubscription>();
        this.executor = executor;
        this.subscribable = subscribable;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }
    
    private void processRequestInternal(@Nonnull final Kup kup) {
        final DownstreamSubscription downstreamSubscription = new DownstreamSubscription((Object)kup, this.getDownstreamKey(kup), (DownstreamSubscription)null);
        synchronized (this.lock) {
            final DownstreamSubscription downstreamSubscription2 = this.subscriptions.put(kup, downstreamSubscription);
            monitorexit(this.lock);
            if (downstreamSubscription2 != null) {
                downstreamSubscription2.unsubscribe();
            }
        }
    }
    
    private void processResultInternal(@Nonnull final Kup kup, @Nullable final Tdown tdown) {
        this.resultHandler.onResultData(kup, this.processResult(tdown));
    }
    
    protected abstract Kdown getDownstreamKey(@Nonnull final Kup p0);
    
    @Override
    public void onRequest(@Nonnull final Kup kup) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$swF2K5wPKI2_xA_bWP_XwHVnywU$1(this, kup));
        }
        else {
            this.processRequestInternal(kup);
        }
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final Kup kup) {
        synchronized (this.lock) {
            final DownstreamSubscription downstreamSubscription = this.subscriptions.remove(kup);
            monitorexit(this.lock);
            if (downstreamSubscription != null) {
                downstreamSubscription.unsubscribe();
            }
        }
    }
    
    protected abstract Tup processResult(@Nullable final Tdown p0);
    
    private class DownstreamSubscription implements OnData<Tdown>, OnError, UnsubscribableOne
    {
        private final Kup key;
        private final Subscription<Kdown, Tdown> subscription;
        
        private DownstreamSubscription(final Kup key, final Kdown kdown) {
            this.key = key;
            this.subscription = RequestForwarder.this.subscribable.subscribe(kdown, RequestForwarder.this.executor, this, this);
        }
        
        @Override
        public void onData(final Tdown tdown) {
            if (RequestForwarder.this.executor != null) {
                RequestForwarder.this.executor.execute(new _$Lambda$swF2K5wPKI2_xA_bWP_XwHVnywU(this, tdown));
            }
            else {
                RequestForwarder.this.processResultInternal(this.key, tdown);
            }
        }
        
        @Override
        public void onError(final Throwable t) {
            RequestForwarder.this.resultHandler.onResultError(this.key, t);
        }
        
        @Override
        public void unsubscribe() {
            this.subscription.unsubscribe();
        }
    }
}
