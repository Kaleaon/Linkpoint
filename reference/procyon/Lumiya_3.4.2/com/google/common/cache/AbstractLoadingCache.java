// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;

public abstract class AbstractLoadingCache<K, V> extends AbstractCache<K, V> implements LoadingCache<K, V>
{
    protected AbstractLoadingCache() {
    }
    
    @Override
    public final V apply(final K k) {
        return this.getUnchecked(k);
    }
    
    @Override
    public ImmutableMap<K, V> getAll(final Iterable<? extends K> iterable) throws ExecutionException {
        final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
        for (final K next : iterable) {
            if (!linkedHashMap.containsKey(next)) {
                linkedHashMap.put(next, this.get(next));
            }
        }
        return ImmutableMap.copyOf((Map<? extends K, ? extends V>)linkedHashMap);
    }
    
    @Override
    public V getUnchecked(final K k) {
        try {
            return this.get(k);
        }
        catch (final ExecutionException ex) {
            throw new UncheckedExecutionException(ex.getCause());
        }
    }
    
    @Override
    public void refresh(final K k) {
        throw new UnsupportedOperationException();
    }
}
