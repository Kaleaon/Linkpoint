// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nonnull;
import java.util.concurrent.Future;
import java.util.Map;

public class AsyncCancellableRequestHandler<K> implements RequestHandler<K>
{
    @Nonnull
    private final Map<K, Future<?>> activeRequests;
    @Nonnull
    private final RequestHandler<K> baseHandler;
    @Nonnull
    private final ExecutorService executor;
    private final Object lock;
    
    public AsyncCancellableRequestHandler(@Nonnull final ExecutorService executor, @Nonnull final RequestHandler<K> baseHandler) {
        this.activeRequests = new ConcurrentHashMap<K, Future<?>>();
        this.lock = new Object();
        this.executor = executor;
        this.baseHandler = baseHandler;
    }
    
    @Override
    public void onRequest(@Nonnull final K k) {
        synchronized (this.lock) {
            this.activeRequests.put(k, this.executor.submit(new _$Lambda$W2IjgG3sQFB_K_ukBg8_XysJz_I(this, k)));
        }
    }
    
    @Override
    public void onRequestCancelled(@Nonnull final K k) {
        synchronized (this.lock) {
            final Future future = this.activeRequests.remove(k);
            if (future != null) {
                future.cancel(true);
            }
        }
    }
}
