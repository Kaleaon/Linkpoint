// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import com.google.common.base.Predicate;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class FilteredKeySetMultimap<K, V> extends FilteredKeyMultimap<K, V> implements FilteredSetMultimap<K, V>
{
    FilteredKeySetMultimap(final SetMultimap<K, V> setMultimap, final Predicate<? super K> predicate) {
        super(setMultimap, predicate);
    }
    
    @Override
    Set<Map.Entry<K, V>> createEntries() {
        return new EntrySet();
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return (Set)super.entries();
    }
    
    @Override
    public Set<V> get(final K k) {
        return (Set)super.get(k);
    }
    
    @Override
    public Set<V> removeAll(final Object o) {
        return (Set)super.removeAll(o);
    }
    
    @Override
    public Set<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
        return (Set)super.replaceValues(k, iterable);
    }
    
    @Override
    public SetMultimap<K, V> unfiltered() {
        return (SetMultimap)this.unfiltered;
    }
    
    class EntrySet extends Entries implements Set<Map.Entry<K, V>>
    {
        @Override
        public boolean equals(@Nullable final Object o) {
            return Sets.equalsImpl(this, o);
        }
        
        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }
}
