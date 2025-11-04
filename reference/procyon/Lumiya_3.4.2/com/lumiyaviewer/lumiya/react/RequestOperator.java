// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public abstract class RequestOperator<K, T> implements RequestHandler<K>
{
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<K, T> resultHandler;
    @Nonnull
    private final RequestHandler<K> toHandler;
    
    public RequestOperator(@Nonnull final RequestHandler<K> toHandler, @Nonnull final ResultHandler<K, T> resultHandler) {
        this.toHandler = toHandler;
        this.resultHandler = resultHandler;
        this.executor = null;
    }
    
    public RequestOperator(@Nonnull final RequestHandler<K> toHandler, @Nonnull final ResultHandler<K, T> resultHandler, @Nullable final Executor executor) {
        this.toHandler = toHandler;
        this.resultHandler = resultHandler;
        this.executor = executor;
    }
    
    @Override
    public void onRequest(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$3htMVvcf7XlS6QCgMv3cESjj4go(this, k));
        }
        else {
            final T processRequest = this.processRequest(k);
            if (processRequest != null) {
                this.resultHandler.onResultData(k, processRequest);
            }
            else {
                this.toHandler.onRequest(k);
            }
        }
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$3htMVvcf7XlS6QCgMv3cESjj4go$1(this, k));
        }
        else {
            this.toHandler.onRequestCancelled(k);
        }
    }
    
    protected abstract T processRequest(@Nonnull final K p0);
}
