// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ConcurrentMap;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;

@GwtCompatible
public interface LoadingCache<K, V> extends Cache<K, V>, Function<K, V>
{
    @Deprecated
    V apply(final K p0);
    
    ConcurrentMap<K, V> asMap();
    
    V get(final K p0) throws ExecutionException;
    
    ImmutableMap<K, V> getAll(final Iterable<? extends K> p0) throws ExecutionException;
    
    V getUnchecked(final K p0);
    
    void refresh(final K p0);
}
