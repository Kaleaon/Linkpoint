// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public abstract class RequestFinalProcessor<K, T> implements RequestHandler<K>
{
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<K, T> resultHandler;
    
    public RequestFinalProcessor(@Nonnull final RequestSource<K, T> requestSource, @Nullable final Executor executor) {
        this.executor = executor;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }
    
    protected void cancelRequest(@Nonnull final K k) {
    }
    
    @Override
    public void onRequest(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$psFcS6_5kKyuCZBH4SbOZwtpXG8(this, k));
        }
        else {
            try {
                this.resultHandler.onResultData(k, this.processRequest(k));
            }
            catch (final Throwable t) {
                this.resultHandler.onResultError(k, t);
            }
        }
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final K k) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$psFcS6_5kKyuCZBH4SbOZwtpXG8$1(this, k));
        }
        else {
            this.cancelRequest(k);
        }
    }
    
    protected abstract T processRequest(@Nonnull final K p0) throws Throwable;
}
