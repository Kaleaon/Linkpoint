// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Deprecated
@Beta
@GwtCompatible
public interface FutureFallback<V>
{
    ListenableFuture<V> create(final Throwable p0) throws Exception;
}
