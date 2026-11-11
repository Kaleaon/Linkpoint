package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.Exception;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Beta
/* loaded from: classes.dex */
public abstract class ForwardingCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture<V> implements CheckedFuture<V, X> {

    @Beta
    /* loaded from: classes.dex */
    public static abstract class SimpleForwardingCheckedFuture<V, X extends Exception> extends ForwardingCheckedFuture<V, X> {
        private final CheckedFuture<V, X> delegate;

        protected SimpleForwardingCheckedFuture(CheckedFuture<V, X> checkedFuture) {
            this.delegate = (CheckedFuture) Preconditions.checkNotNull(checkedFuture);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.common.util.concurrent.ForwardingCheckedFuture, com.google.common.util.concurrent.ForwardingListenableFuture, com.google.common.util.concurrent.ForwardingFuture, com.google.common.collect.ForwardingObject
        public final CheckedFuture<V, X> delegate() {
            return this.delegate;
        }
    }

    @Override // com.google.common.util.concurrent.CheckedFuture
    public V checkedGet() throws Exception {
        return delegate().checkedGet();
    }

    @Override // com.google.common.util.concurrent.CheckedFuture
    public V checkedGet(long j, TimeUnit timeUnit) throws TimeoutException, Exception {
        return delegate().checkedGet(j, timeUnit);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.common.util.concurrent.ForwardingListenableFuture, com.google.common.util.concurrent.ForwardingFuture, com.google.common.collect.ForwardingObject
    public abstract CheckedFuture<V, X> delegate();
}
