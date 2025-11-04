// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements SortedSetMultimap<K, V>
{
    private static final long serialVersionUID = 430848587173315748L;
    
    protected AbstractSortedSetMultimap(final Map<K, Collection<V>> map) {
        super(map);
    }
    
    @Override
    public Map<K, Collection<V>> asMap() {
        return super.asMap();
    }
    
    @Override
    abstract SortedSet<V> createCollection();
    
    @Override
    SortedSet<V> createUnmodifiableEmptyCollection() {
        if (this.valueComparator() != null) {
            return (SortedSet<V>)ImmutableSortedSet.emptySet((Comparator<? super Object>)this.valueComparator());
        }
        return Collections.unmodifiableSortedSet(this.createCollection());
    }
    
    @Override
    public SortedSet<V> get(@Nullable final K k) {
        return (SortedSet)super.get(k);
    }
    
    @Override
    public SortedSet<V> removeAll(@Nullable final Object o) {
        return (SortedSet)super.removeAll(o);
    }
    
    @Override
    public SortedSet<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        return (SortedSet)super.replaceValues(k, iterable);
    }
    
    @Override
    public Collection<V> values() {
        return super.values();
    }
}
