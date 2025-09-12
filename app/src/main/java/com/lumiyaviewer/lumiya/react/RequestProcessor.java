package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RequestProcessor<K, Tup, Tdown> implements RequestHandler<K>, RequestSource<K, Tdown>, ResultHandler<K, Tdown>, Refreshable<K> {
    @Nullable
    private final Executor executor;
    @Nullable
    private RequestHandler<K> requestHandler;
    @Nonnull
    private final ResultHandler<K, Tup> resultHandler;

    public RequestProcessor(@Nonnull RequestSource<K, Tup> requestSource, @Nullable Executor executor) {
        this.executor = executor;
        this.resultHandler = requestSource.attachRequestHandler(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: processRequestInternal */
    public void m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(@Nonnull K k) {
        Object processRequest = processRequest(k);
        if (processRequest != null) {
            this.resultHandler.onResultData(k, processRequest);
        }
        if (!isRequestComplete(k, processRequest) && this.requestHandler != null) {
            this.requestHandler.onRequest(k);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: requestUpdateInternal */
    public void m33lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(@Nonnull K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequest(k);
        }
    }

    public ResultHandler<K, Tdown> attachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        this.requestHandler = requestHandler;
        return this;
    }

    public void detachRequestHandler(@Nonnull RequestHandler<K> requestHandler) {
        if (this.requestHandler == requestHandler) {
            this.requestHandler = null;
        }
    }

    protected boolean isRequestComplete(@Nonnull K k, Tup tup) {
        return tup != null;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestProcessor_2159  reason: not valid java name */
    public /* synthetic */ void m34lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_2159(Object obj, Object obj2) {
        this.resultHandler.onResultData(obj, processResult(obj, obj2));
    }

    public void onRequest(@Nonnull K k) {
        if (this.executor != null) {
            this.executor.execute(() -> m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(k));
        } else {
            m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(k);
        }
    }

    public void onRequestCancelled(@Nonnull K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequestCancelled(k);
        }
    }

    public void onResultData(@Nonnull K k, Tdown tdown) {
        if (this.executor != null) {
            this.executor.execute(() -> this.resultHandler.onResultData(k, processResult(k, tdown)));
        } else {
            this.resultHandler.onResultData(k, processResult(k, tdown));
        }
    }

    public void onResultError(@Nonnull K k, Throwable th) {
        this.resultHandler.onResultError(k, th);
    }

    @Nullable
    protected abstract Tup processRequest(@Nonnull K k);

    protected abstract Tup processResult(@Nonnull K k, Tdown tdown);

    public void requestUpdate(K k) {
        if (this.executor != null) {
            this.executor.execute(() -> m33lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(k));
        } else {
            m33lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(k);
        }
    }
}
