// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.Future;

@GwtCompatible
public interface ListenableFuture<V> extends Future<V>
{
    void addListener(final Runnable p0, final Executor p1);
}
