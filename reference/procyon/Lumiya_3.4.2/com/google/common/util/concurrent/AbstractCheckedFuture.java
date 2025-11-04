// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractCheckedFuture<V, X extends Exception> extends SimpleForwardingListenableFuture<V> implements CheckedFuture<V, X>
{
    protected AbstractCheckedFuture(final ListenableFuture<V> listenableFuture) {
        super(listenableFuture);
    }
    
    @Override
    public V checkedGet() throws X, Exception {
        try {
            return this.get();
        }
        catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw this.mapException(ex);
        }
        catch (final CancellationException ex2) {
            throw this.mapException(ex2);
        }
        catch (final ExecutionException ex3) {
            throw this.mapException(ex3);
        }
    }
    
    @Override
    public V checkedGet(final long n, final TimeUnit timeUnit) throws TimeoutException, X, Exception {
        try {
            return this.get(n, timeUnit);
        }
        catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw this.mapException(ex);
        }
        catch (final CancellationException ex2) {
            throw this.mapException(ex2);
        }
        catch (final ExecutionException ex3) {
            throw this.mapException(ex3);
        }
    }
    
    protected abstract X mapException(final Exception p0);
}
