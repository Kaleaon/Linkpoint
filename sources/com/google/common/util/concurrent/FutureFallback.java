package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
@Deprecated
@Beta
public interface FutureFallback<V> {
    ListenableFuture<V> create(Throwable th) throws Exception;
}
