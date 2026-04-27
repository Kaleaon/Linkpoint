package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ResultOperator<K, Tin, Tout> implements ResultHandler<K, Tin> {
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<K, Tout> toHandler;

    public ResultOperator(@Nonnull ResultHandler<K, Tout> resultHandler) {
        this.toHandler = resultHandler;
        this.executor = null;
    }

    public ResultOperator(@Nonnull ResultHandler<K, Tout> resultHandler, @Nullable Executor executor) {
        this.toHandler = resultHandler;
        this.executor = executor;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_ResultOperator_1065  reason: not valid java name */
    public /* synthetic */ void m36lambda$com_lumiyaviewer_lumiya_react_ResultOperator_1065(Object obj, Throwable th) {
        this.toHandler.onResultError(obj, th);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_ResultOperator_796  reason: not valid java name */
    public /* synthetic */ void m37lambda$com_lumiyaviewer_lumiya_react_ResultOperator_796(Object obj, Object obj2) {
        this.toHandler.onResultData(obj, onData(obj2));
    }

    protected abstract Tout onData(Tin tin);

    public void onResultData(@Nonnull K k, Tin tin) {
        if (this.executor != null) {
            this.executor.execute(() -> this.toHandler.onResultData(k, onData(tin)));
        } else {
            this.toHandler.onResultData(k, onData(tin));
        }
    }

    public void onResultError(@Nonnull K k, Throwable th) {
        if (this.executor != null) {
            this.executor.execute(() -> this.toHandler.onResultError(k, th));
        } else {
            this.toHandler.onResultError(k, th);
        }
    }
}
