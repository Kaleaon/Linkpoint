// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.base.Preconditions;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import com.google.common.collect.ForwardingObject;

public abstract class ForwardingCache<K, V> extends ForwardingObject implements Cache<K, V>
{
    protected ForwardingCache() {
    }
    
    @Override
    public ConcurrentMap<K, V> asMap() {
        return this.delegate().asMap();
    }
    
    @Override
    public void cleanUp() {
        this.delegate().cleanUp();
    }
    
    @Override
    protected abstract Cache<K, V> delegate();
    
    @Override
    public V get(final K k, final Callable<? extends V> callable) throws ExecutionException {
        return this.delegate().get(k, callable);
    }
    
    @Override
    public ImmutableMap<K, V> getAllPresent(final Iterable<?> iterable) {
        return this.delegate().getAllPresent(iterable);
    }
    
    @Nullable
    @Override
    public V getIfPresent(final Object o) {
        return this.delegate().getIfPresent(o);
    }
    
    @Override
    public void invalidate(final Object o) {
        this.delegate().invalidate(o);
    }
    
    @Override
    public void invalidateAll() {
        this.delegate().invalidateAll();
    }
    
    @Override
    public void invalidateAll(final Iterable<?> iterable) {
        this.delegate().invalidateAll(iterable);
    }
    
    @Override
    public void put(final K k, final V v) {
        this.delegate().put(k, v);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        this.delegate().putAll(map);
    }
    
    @Override
    public long size() {
        return this.delegate().size();
    }
    
    @Override
    public CacheStats stats() {
        return this.delegate().stats();
    }
    
    public abstract static class SimpleForwardingCache<K, V> extends ForwardingCache<K, V>
    {
        private final Cache<K, V> delegate;
        
        protected SimpleForwardingCache(final Cache<K, V> cache) {
            this.delegate = Preconditions.checkNotNull(cache);
        }
        
        @Override
        protected final Cache<K, V> delegate() {
            return this.delegate;
        }
    }
}
