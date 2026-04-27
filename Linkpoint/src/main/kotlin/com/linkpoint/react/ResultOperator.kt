package com.linkpoint.react

import java.util.concurrent.Executor
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class ResultOperator<K, Tin, Tout> : ResultHandler<K, Tin> {
    private val Executor executor
    private val ResultHandler<K, Tout> toHandler

    public ResultOperator(ResultHandler<K, Tout> resultHandler) {
        this.toHandler = resultHandler
        this.executor = null
    }

    public ResultOperator(ResultHandler<K, Tout> resultHandler, Executor executor) {
        this.toHandler = resultHandler
        this.executor = executor
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_ResultOperator_1065  reason: not valid java name */
    public /* synthetic */ Unit m36lambda$com_lumiyaviewer_lumiya_react_ResultOperator_1065(Object obj, Throwable th) {
        this.toHandler.onResultError(obj, th)
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_ResultOperator_796  reason: not valid java name */
    public /* synthetic */ Unit m37lambda$com_lumiyaviewer_lumiya_react_ResultOperator_796(Object obj, Object obj2) {
        this.toHandler.onResultData(obj, onData(obj2))
    }

    protected abstract Tout onData(Tin tin)

    fun onResultData(K k, tin: Tin) {
        if (this.executor != null) {
            this.executor.execute(() -> this.toHandler.onResultData(k, onData(tin)))
        } else {
            this.toHandler.onResultData(k, onData(tin))
        }
    }

    fun onResultError(K k, th: Throwable) {
        if (this.executor != null) {
            this.executor.execute(() -> this.toHandler.onResultError(k, th))
        } else {
            this.toHandler.onResultError(k, th)
        }
    }
}
