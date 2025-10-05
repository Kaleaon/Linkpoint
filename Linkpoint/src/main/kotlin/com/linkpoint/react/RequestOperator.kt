package com.linkpoint.react

import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class RequestOperator<K, T> : RequestHandler<K> {
    private val Executor executor
    private val ResultHandler<K, T> resultHandler
    private val RequestHandler<K> toHandler

    public RequestOperator(RequestHandler<K> requestHandler, ResultHandler<K, T> resultHandler) {
        this.toHandler = requestHandler
        this.resultHandler = resultHandler
        this.executor = null
    }

    public RequestOperator(RequestHandler<K> requestHandler, ResultHandler<K, T> resultHandler, Executor executor) {
        this.toHandler = requestHandler
        this.resultHandler = resultHandler
        this.executor = executor
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestOperator_1579  reason: not valid java name */
    public /* synthetic */ Unit m31lambda$com_lumiyaviewer_lumiya_react_RequestOperator_1579(Object obj) {
        this.toHandler.onRequestCancelled(obj)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_RequestOperator_996  reason: not valid java name */
    public /* synthetic */ Unit m32lambda$com_lumiyaviewer_lumiya_react_RequestOperator_996(Object obj) {
        Object processRequest = processRequest(obj)
        if (processRequest != null) {
            this.resultHandler.onResultData(obj, processRequest)
        } else {
            this.toHandler.onRequest(obj)
        }
    }

    public Unit onRequest(K k) {
        if (this.executor != null) {
            this.executor.execute(() -> m32lambda$com_lumiyaviewer_lumiya_react_RequestOperator_996(k))
            return
        }
        Object processRequest = processRequest(k)
        if (processRequest != null) {
            this.resultHandler.onResultData(k, processRequest)
        } else {
            this.toHandler.onRequest(k)
        }
    }

    public Unit onRequestCancelled(K k) {
        if (this.executor != null) {
            this.executor.execute(() -> m31lambda$com_lumiyaviewer_lumiya_react_RequestOperator_1579(k))
        } else {
            this.toHandler.onRequestCancelled(k)
        }
    }

    protected abstract T processRequest(K k)
}
