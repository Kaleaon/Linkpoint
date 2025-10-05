package com.linkpoint.react

import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class RequestProcessor<K, Tup, Tdown> : RequestHandler<K>, RequestSource<K, Tdown>, ResultHandler<K, Tdown>, Refreshable<K> {
    private val Executor executor
    private RequestHandler<K> requestHandler
    private val ResultHandler<K, Tup> resultHandler

    public RequestProcessor(RequestSource<K, Tup> requestSource, Executor executor) {
        this.executor = executor
        this.resultHandler = requestSource.attachRequestHandler(this)
    }

    /* access modifiers changed from: private */
    /* renamed from: processRequestInternal */
    public Unit m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(K k) {
        Object processRequest = processRequest(k)
        if (processRequest != null) {
            this.resultHandler.onResultData(k, processRequest)
        }
        if (!isRequestComplete(k, processRequest) && this.requestHandler != null) {
            this.requestHandler.onRequest(k)
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: requestUpdateInternal */
    public Unit m33lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequest(k)
        }
    }

    public ResultHandler<K, Tdown> attachRequestHandler(RequestHandler<K> requestHandler) {
        this.requestHandler = requestHandler
        return this
    }

    public Unit detachRequestHandler(RequestHandler<K> requestHandler) {
        if (this.requestHandler == requestHandler) {
            this.requestHandler = null
        }
    }

    protected Boolean isRequestComplete(K k, Tup tup) {
        return tup != null
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestProcessor_2159  reason: not valid java name */
    public /* synthetic */ Unit m34lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_2159(Object obj, Object obj2) {
        this.resultHandler.onResultData(obj, processResult(obj, obj2))
    }

    public Unit onRequest(K k) {
        if (this.executor != null) {
            this.executor.execute(() -> m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(k))
        } else {
            m35lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_940(k)
        }
    }

    public Unit onRequestCancelled(K k) {
        if (this.requestHandler != null) {
            this.requestHandler.onRequestCancelled(k)
        }
    }

    public Unit onResultData(K k, Tdown tdown) {
        if (this.executor != null) {
            this.executor.execute(() -> this.resultHandler.onResultData(k, processResult(k, tdown)))
        } else {
            this.resultHandler.onResultData(k, processResult(k, tdown))
        }
    }

    public Unit onResultError(K k, Throwable th) {
        this.resultHandler.onResultError(k, th)
    }

    protected abstract Tup processRequest(K k)

    protected abstract Tup processResult(K k, Tdown tdown)

    public Unit requestUpdate(K k) {
        if (this.executor != null) {
            this.executor.execute(() -> m33lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(k))
        } else {
            m33lambda$com_lumiyaviewer_lumiya_react_RequestProcessor_1507(k)
        }
    }
}
