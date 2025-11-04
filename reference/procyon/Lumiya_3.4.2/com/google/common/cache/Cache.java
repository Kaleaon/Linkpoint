// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public interface Cache<K, V>
{
    ConcurrentMap<K, V> asMap();
    
    void cleanUp();
    
    V get(final K p0, final Callable<? extends V> p1) throws ExecutionException;
    
    ImmutableMap<K, V> getAllPresent(final Iterable<?> p0);
    
    @Nullable
    V getIfPresent(final Object p0);
    
    void invalidate(final Object p0);
    
    void invalidateAll();
    
    void invalidateAll(final Iterable<?> p0);
    
    void put(final K p0, final V p1);
    
    void putAll(final Map<? extends K, ? extends V> p0);
    
    long size();
    
    CacheStats stats();
}
