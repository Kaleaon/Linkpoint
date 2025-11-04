// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractListMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements ListMultimap<K, V>
{
    private static final long serialVersionUID = 6588350623831699109L;
    
    protected AbstractListMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    abstract List<V> createCollection();
    
    @Override
    List<V> createUnmodifiableEmptyCollection() {
        return (List<V>)ImmutableList.of();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return super.equals(o);
    }
    
    @Override
    public List<V> get(@Nullable final K k) {
        return (List)super.get(k);
    }
    
    @Override
    public boolean put(@Nullable final K k, @Nullable final V v) {
        return super.put(k, v);
    }
    
    @Override
    public List<V> removeAll(@Nullable final Object o) {
        return (List)super.removeAll(o);
    }
    
    @Override
    public List<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        return (List)super.replaceValues(k, iterable);
    }
}
