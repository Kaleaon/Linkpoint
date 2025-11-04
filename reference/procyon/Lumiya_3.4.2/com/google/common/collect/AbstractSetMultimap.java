// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractSetMultimap<K, V> extends AbstractMapBasedMultimap<K, V> implements SetMultimap<K, V>
{
    private static final long serialVersionUID = 7431625294878419160L;
    
    protected AbstractSetMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    abstract Set<V> createCollection();
    
    @Override
    Set<V> createUnmodifiableEmptyCollection() {
        return (Set<V>)ImmutableSet.of();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set)super.entries();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return super.equals(o);
    }
    
    @Override
    public Set<V> get(@Nullable final K k) {
        return (Set)super.get(k);
    }
    
    @Override
    public boolean put(@Nullable final K k, @Nullable final V v) {
        return super.put(k, v);
    }
    
    @Override
    public Set<V> removeAll(@Nullable final Object o) {
        return (Set)super.removeAll(o);
    }
    
    @Override
    public Set<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        return (Set)super.replaceValues(k, iterable);
    }
}
