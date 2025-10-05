package com.linkpoint.react

import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class RequestFinalProcessor<K, T> : RequestHandler<K> {
    private val Executor executor
    private val ResultHandler<K, T> resultHandler

    public RequestFinalProcessor(RequestSource<K, T> requestSource, Executor executor) {
        this.executor = executor
        this.resultHandler = requestSource.attachRequestHandler(this)
    }

    /* renamed from: cancelRequest */
    protected Unit handleRequestCancellation(K k) {
    }

    /* access modifiers changed from: private */
    /* renamed from: handleRequestProcessingInternal */
    public /* synthetic */ Unit m24lambda$com_lumiyaviewer_lumiya_react_RequestFinalProcessor_673(Object obj) {
        try {
            this.resultHandler.onResultData(obj, processRequest(obj))
        } catch (Throwable th) {
            this.resultHandler.onResultError(obj, th)
        }
    }

    public Unit onRequest(K k) {
        if (this.executor != null) {
            this.executor.execute(-$Lambda$psFcS6-5kKyuCZBH4SbOZwtpXG8(this, k))
            return
        }
        try {
            this.resultHandler.onResultData(k, processRequest(k))
        } catch (Throwable th) {
            this.resultHandler.onResultError(k, th)
        }
    }

    public Unit onRequestCancelled(K k) {
        if (this.executor != null) {
            this.executor.execute(AnonymousClass1(this, k))
        } else {
            handleRequestCancellation(k)
        }
    }

    protected abstract T processRequest(K k) throws Throwable
}
