package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public class AsyncLimitsRequestHandler<K> extends AsyncRequestHandler<K> implements RequestHandlerLimits {
    private final boolean isCancellable;
    private final int maxRequests;
    private final long requestTimeout;

    public AsyncLimitsRequestHandler(@Nonnull Executor executor, @Nonnull RequestHandler<K> requestHandler, boolean z, int i, long j) {
        super(executor, requestHandler);
        this.isCancellable = z;
        this.maxRequests = i;
        this.requestTimeout = j;
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandlerLimits
    public int getMaxRequestsInFlight() {
        return this.maxRequests;
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandlerLimits
    public long getRequestTimeout() {
        return this.requestTimeout;
    }

    @Override // com.lumiyaviewer.lumiya.react.RequestHandlerLimits
    public boolean isRequestCancellable() {
        return this.isCancellable;
    }
}
