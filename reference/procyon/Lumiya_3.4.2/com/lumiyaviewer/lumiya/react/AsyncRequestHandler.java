// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

public class AsyncRequestHandler<K> implements RequestHandler<K>
{
    @Nonnull
    private final RequestHandler<K> baseHandler;
    @Nonnull
    private final Executor executor;
    
    public AsyncRequestHandler(@Nonnull final Executor executor, @Nonnull final RequestHandler<K> baseHandler) {
        this.executor = executor;
        this.baseHandler = baseHandler;
    }
    
    @Override
    public void onRequest(@Nonnull final K k) {
        this.executor.execute(new _$Lambda$SNlq3T7EFvfEVtJpS5BhPof2E2o(this, k));
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final K k) {
        this.executor.execute(new _$Lambda$SNlq3T7EFvfEVtJpS5BhPof2E2o$1(this, k));
    }
}
