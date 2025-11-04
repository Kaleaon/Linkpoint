// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingSetMultimap<K, V> extends ForwardingMultimap<K, V> implements SetMultimap<K, V>
{
    @Override
    protected abstract SetMultimap<K, V> delegate();
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return this.delegate().entries();
    }
    
    @Override
    public Set<V> get(@Nullable final K k) {
        return this.delegate().get(k);
    }
    
    @Override
    public Set<V> removeAll(@Nullable final Object o) {
        return this.delegate().removeAll(o);
    }
    
    @Override
    public Set<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k, iterable);
    }
}
