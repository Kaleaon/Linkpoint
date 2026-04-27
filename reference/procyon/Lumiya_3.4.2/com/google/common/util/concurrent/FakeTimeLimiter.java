// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import com.google.common.annotations.Beta;

@Beta
public final class FakeTimeLimiter implements TimeLimiter
{
    @Override
    public <T> T callWithTimeout(final Callable<T> callable, final long n, final TimeUnit timeUnit, final boolean b) throws Exception {
        Preconditions.checkNotNull(timeUnit);
        return callable.call();
    }
    
    @Override
    public <T> T newProxy(final T t, final Class<T> clazz, final long n, final TimeUnit timeUnit) {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(clazz);
        Preconditions.checkNotNull(timeUnit);
        return t;
    }
}
