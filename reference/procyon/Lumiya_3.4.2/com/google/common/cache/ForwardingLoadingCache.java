// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;

public abstract class ForwardingLoadingCache<K, V> extends ForwardingCache<K, V> implements LoadingCache<K, V>
{
    protected ForwardingLoadingCache() {
    }
    
    @Override
    public V apply(final K k) {
        return this.delegate().apply(k);
    }
    
    @Override
    protected abstract LoadingCache<K, V> delegate();
    
    @Override
    public V get(final K k) throws ExecutionException {
        return this.delegate().get(k);
    }
    
    @Override
    public ImmutableMap<K, V> getAll(final Iterable<? extends K> iterable) throws ExecutionException {
        return this.delegate().getAll(iterable);
    }
    
    @Override
    public V getUnchecked(final K k) {
        return this.delegate().getUnchecked(k);
    }
    
    @Override
    public void refresh(final K k) {
        this.delegate().refresh(k);
    }
    
    public abstract static class SimpleForwardingLoadingCache<K, V> extends ForwardingLoadingCache<K, V>
    {
        private final LoadingCache<K, V> delegate;
        
        protected SimpleForwardingLoadingCache(final LoadingCache<K, V> loadingCache) {
            this.delegate = Preconditions.checkNotNull(loadingCache);
        }
        
        @Override
        protected final LoadingCache<K, V> delegate() {
            return this.delegate;
        }
    }
}
