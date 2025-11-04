// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import com.google.common.annotations.Beta;

@Beta
public interface TimeLimiter
{
     <T> T callWithTimeout(final Callable<T> p0, final long p1, final TimeUnit p2, final boolean p3) throws Exception;
    
     <T> T newProxy(final T p0, final Class<T> p1, final long p2, final TimeUnit p3);
}
