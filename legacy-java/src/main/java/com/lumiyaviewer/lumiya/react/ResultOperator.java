package com.lumiyaviewer.lumiya.react;

import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_ResultOperator_1065, reason: not valid java name */
    public /* synthetic */ void m46lambda$com_lumiyaviewer_lumiya_react_ResultOperator_1065(Object obj, Throwable th) {
        this.toHandler.onResultError(obj, th);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_react_ResultOperator_796, reason: not valid java name */
    public /* synthetic */ void m47lambda$com_lumiyaviewer_lumiya_react_ResultOperator_796(Object obj, Object obj2) {
        this.toHandler.onResultData(obj, onData(obj2));
    }

    protected abstract Tout onData(Tin tin);

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultData(@Nonnull final K k, final Tin tin) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$rbwdofHzZNihI1HZoTkj8gWFECo
                private final /* synthetic */ void $m$0() {
                    ((ResultOperator) this).m47lambda$com_lumiyaviewer_lumiya_react_ResultOperator_796(k, tin);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            this.toHandler.onResultData(k, onData(tin));
        }
    }

    @Override // com.lumiyaviewer.lumiya.react.ResultHandler
    public void onResultError(@Nonnull final K k, final Throwable th) {
        if (this.executor != null) {
            this.executor.execute(new Runnable() { // from class: com.lumiyaviewer.lumiya.react.-$Lambda$rbwdofHzZNihI1HZoTkj8gWFECo.1
                private final /* synthetic */ void $m$0() {
                    ((ResultOperator) this).m46lambda$com_lumiyaviewer_lumiya_react_ResultOperator_1065(k, (Throwable) th);
                }

                @Override // java.lang.Runnable
                public final void run() {
                    $m$0();
                }
            });
        } else {
            this.toHandler.onResultError(k, th);
        }
    }
}
