// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public abstract class RequestProcessor<K, Tup, Tdown> implements RequestHandler<K>, RequestSource<K, Tdown>, ResultHandler<K, Tdown>, Refreshable<K>
{
    @Nullable
    private final Executor executor;
    @Nullable
    private RequestHandler<K> requestHandler;
    @Nonnull
    private final ResultHandler<K, Tup> resultHandler;
    
    public RequestProcessor(@Nonnull final RequestSource<K, Tup> requestSource, @Nullable final Executor executor) {
        this.executor = executor;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }
    
    private void processRequestInternal(@Nonnull final K k) {
        final Tup processRequest = this.processRequest(k);
        if (processRequest != null) {
            this.resultHandler.onResultData(k, processRequest);
        }
        if (!this.isRequestComplete(k, processRequest) && this.requestHandler != null) {
            this.requestHandler.onRequest(k);
        }
    }
    
    private void requestUpdateInternal(@Nonnull final K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequest(k);
        }
    }
    
    @Override
    public ResultHandler<K, Tdown> attachRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }
    
    @Override
    public void detachRequestHandler(@Nonnull final RequestHandler<K> requestHandler) {
        if (this.requestHandler == requestHandler) {
            this.requestHandler = null;
        }
    }
    
    protected boolean isRequestComplete(@Nonnull final K k, final Tup tup) {
        return tup != null;
    }
    
    @Override
    public void onRequest(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$s15PKVbd3BFZx563Ff4DOHT5V_w(this, k));
        }
        else {
            this.processRequestInternal(k);
        }
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequestCancelled(k);
        }
    }
    
    @Override
    public void onResultData(@Nonnull final K k, final Tdown tdown) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$s15PKVbd3BFZx563Ff4DOHT5V_w$2(this, k, tdown));
        }
        else {
            this.resultHandler.onResultData(k, this.processResult(k, tdown));
        }
    }
    
    @Override
    public void onResultError(@Nonnull final K k, final Throwable t) {
        this.resultHandler.onResultError(k, t);
    }
    
    @Nullable
    protected abstract Tup processRequest(@Nonnull final K p0);
    
    protected abstract Tup processResult(@Nonnull final K p0, final Tdown p1);
    
    @Override
    public void requestUpdate(final K k) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$s15PKVbd3BFZx563Ff4DOHT5V_w$1(this, k));
        }
        else {
            this.requestUpdateInternal(k);
        }
    }
}
