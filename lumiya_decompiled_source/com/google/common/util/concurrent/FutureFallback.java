package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
@Beta
@GwtCompatible
@Deprecated
/* loaded from: classes.dex */
public interface FutureFallback<V> {
    ListenableFuture<V> create(Throwable th) throws Exception;
}
