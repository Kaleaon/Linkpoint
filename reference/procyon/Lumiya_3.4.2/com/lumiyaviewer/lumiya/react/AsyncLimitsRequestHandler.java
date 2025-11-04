// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;

public class AsyncLimitsRequestHandler<K> extends AsyncRequestHandler<K> implements RequestHandlerLimits
{
    private final boolean isCancellable;
    private final int maxRequests;
    private final long requestTimeout;
    
    public AsyncLimitsRequestHandler(@Nonnull final Executor executor, @Nonnull final RequestHandler<K> requestHandler, final boolean isCancellable, final int maxRequests, final long requestTimeout) {
        super(executor, requestHandler);
        this.isCancellable = isCancellable;
        this.maxRequests = maxRequests;
        this.requestTimeout = requestTimeout;
    }
    
    @Override
    public int getMaxRequestsInFlight() {
        return this.maxRequests;
    }
    
    @Override
    public long getRequestTimeout() {
        return this.requestTimeout;
    }
    
    @Override
    public boolean isRequestCancellable() {
        return this.isCancellable;
    }
}
