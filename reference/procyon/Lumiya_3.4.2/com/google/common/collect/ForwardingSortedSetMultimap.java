// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.SortedSet;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V> implements SortedSetMultimap<K, V>
{
    protected ForwardingSortedSetMultimap() {
    }
    
    @Override
    protected abstract SortedSetMultimap<K, V> delegate();
    
    @Override
    public SortedSet<V> get(@Nullable final K k) {
        return this.delegate().get(k);
    }
    
    @Override
    public SortedSet<V> removeAll(@Nullable final Object o) {
        return this.delegate().removeAll(o);
    }
    
    @Override
    public SortedSet<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        return this.delegate().replaceValues(k, iterable);
    }
    
    @Override
    public Comparator<? super V> valueComparator() {
        return this.delegate().valueComparator();
    }
}
