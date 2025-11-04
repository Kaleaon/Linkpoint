// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.react;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Executor;

public abstract class ResultOperator<K, Tin, Tout> implements ResultHandler<K, Tin>
{
    @Nullable
    private final Executor executor;
    @Nonnull
    private final ResultHandler<K, Tout> toHandler;
    
    public ResultOperator(@Nonnull final ResultHandler<K, Tout> toHandler) {
        this.toHandler = toHandler;
        this.executor = null;
    }
    
    public ResultOperator(@Nonnull final ResultHandler<K, Tout> toHandler, @Nullable final Executor executor) {
        this.toHandler = toHandler;
        this.executor = executor;
    }
    
    protected abstract Tout onData(final Tin p0);
    
    @Override
    public void onResultData(@Nonnull final K k, final Tin tin) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$rbwdofHzZNihI1HZoTkj8gWFECo(this, k, tin));
        }
        else {
            this.toHandler.onResultData(k, this.onData(tin));
        }
    }
    
    @Override
    public void onResultError(@Nonnull final K k, final Throwable t) {
        if (this.executor != null) {
            this.executor.execute(new _$Lambda$rbwdofHzZNihI1HZoTkj8gWFECo$1(this, k, t));
        }
        else {
            this.toHandler.onResultError(k, t);
        }
    }
}
