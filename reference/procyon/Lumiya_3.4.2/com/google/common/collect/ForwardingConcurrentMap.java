// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V> implements ConcurrentMap<K, V>
{
    protected ForwardingConcurrentMap() {
    }
    
    @Override
    protected abstract ConcurrentMap<K, V> delegate();
    
    @Override
    public V putIfAbsent(final K k, final V v) {
        return this.delegate().putIfAbsent(k, v);
    }
    
    @Override
    public boolean remove(final Object o, final Object o2) {
        return this.delegate().remove(o, o2);
    }
    
    @Override
    public V replace(final K k, final V v) {
        return this.delegate().replace(k, v);
    }
    
    @Override
    public boolean replace(final K k, final V v, final V v2) {
        return this.delegate().replace(k, v, v2);
    }
}
