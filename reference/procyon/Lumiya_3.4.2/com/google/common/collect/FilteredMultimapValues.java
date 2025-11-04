// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Predicates;
import java.util.Collection;
import com.google.common.base.Predicate;
import com.google.common.base.Objects;
import java.util.Map;
import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import com.google.common.annotations.GwtCompatible;
import java.util.AbstractCollection;

@GwtCompatible
final class FilteredMultimapValues<K, V> extends AbstractCollection<V>
{
    @Weak
    private final FilteredMultimap<K, V> multimap;
    
    FilteredMultimapValues(final FilteredMultimap<K, V> filteredMultimap) {
        this.multimap = Preconditions.checkNotNull(filteredMultimap);
    }
    
    @Override
    public void clear() {
        this.multimap.clear();
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        return this.multimap.containsValue(o);
    }
    
    @Override
    public Iterator<V> iterator() {
        return Maps.valueIterator((Iterator<Map.Entry<Object, V>>)this.multimap.entries().iterator());
    }
    
    @Override
    public boolean remove(@Nullable final Object o) {
        final Predicate<? super Map.Entry<K, V>> entryPredicate = this.multimap.entryPredicate();
        final Iterator<Map.Entry<K, V>> iterator = this.multimap.unfiltered().entries().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)iterator.next();
            if (entryPredicate.apply((Object)entry) && Objects.equal(entry.getValue(), o)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), (Predicate<? super Map.Entry<K, V>>)Maps.valuePredicateOnEntries(Predicates.in(collection))));
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        return Iterables.removeIf(this.multimap.unfiltered().entries(), Predicates.and(this.multimap.entryPredicate(), (Predicate<? super Map.Entry<K, V>>)Maps.valuePredicateOnEntries(Predicates.not(Predicates.in((Collection<? extends V>)collection)))));
    }
    
    @Override
    public int size() {
        return this.multimap.size();
    }
}
