// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class FilteredKeyMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V>
{
    final Predicate<? super K> keyPredicate;
    final Multimap<K, V> unfiltered;
    
    FilteredKeyMultimap(final Multimap<K, V> multimap, final Predicate<? super K> predicate) {
        this.unfiltered = Preconditions.checkNotNull(multimap);
        this.keyPredicate = Preconditions.checkNotNull(predicate);
    }
    
    @Override
    public void clear() {
        this.keySet().clear();
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.unfiltered.containsKey(o) && this.keyPredicate.apply((Object)o);
    }
    
    @Override
    Map<K, Collection<V>> createAsMap() {
        return Maps.filterKeys(this.unfiltered.asMap(), this.keyPredicate);
    }
    
    @Override
    Collection<Map.Entry<K, V>> createEntries() {
        return new Entries();
    }
    
    @Override
    Set<K> createKeySet() {
        return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
    }
    
    @Override
    Multiset<K> createKeys() {
        return Multisets.filter(this.unfiltered.keys(), this.keyPredicate);
    }
    
    @Override
    Collection<V> createValues() {
        return (Collection<V>)new FilteredMultimapValues((FilteredMultimap<Object, Object>)this);
    }
    
    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @Override
    public Predicate<? super Map.Entry<K, V>> entryPredicate() {
        return (Predicate<? super Map.Entry<K, V>>)Maps.keyPredicateOnEntries((Predicate<? super Object>)this.keyPredicate);
    }
    
    @Override
    public Collection<V> get(final K k) {
        if (this.keyPredicate.apply(k)) {
            return this.unfiltered.get(k);
        }
        if (!(this.unfiltered instanceof SetMultimap)) {
            return (Collection<V>)new AddRejectingList(k);
        }
        return (Collection<V>)new AddRejectingSet(k);
    }
    
    @Override
    public Collection<V> removeAll(final Object o) {
        Collection<V> collection;
        if (!this.containsKey(o)) {
            collection = this.unmodifiableEmptyCollection();
        }
        else {
            collection = this.unfiltered.removeAll(o);
        }
        return collection;
    }
    
    @Override
    public int size() {
        final Iterator<Collection<V>> iterator = (Iterator<Collection<V>>)this.asMap().values().iterator();
        int n = 0;
        while (iterator.hasNext()) {
            n += iterator.next().size();
        }
        return n;
    }
    
    @Override
    public Multimap<K, V> unfiltered() {
        return this.unfiltered;
    }
    
    Collection<V> unmodifiableEmptyCollection() {
        if (!(this.unfiltered instanceof SetMultimap)) {
            return (Collection<V>)ImmutableList.of();
        }
        return (Collection<V>)ImmutableSet.of();
    }
    
    static class AddRejectingList<K, V> extends ForwardingList<V>
    {
        final K key;
        
        AddRejectingList(final K key) {
            this.key = key;
        }
        
        @Override
        public void add(final int n, final V v) {
            Preconditions.checkPositionIndex(n, 0);
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }
        
        @Override
        public boolean add(final V v) {
            this.add(0, v);
            return true;
        }
        
        @Override
        public boolean addAll(final int n, final Collection<? extends V> collection) {
            Preconditions.checkNotNull(collection);
            Preconditions.checkPositionIndex(n, 0);
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }
        
        @Override
        public boolean addAll(final Collection<? extends V> collection) {
            this.addAll(0, collection);
            return true;
        }
        
        @Override
        protected List<V> delegate() {
            return Collections.emptyList();
        }
    }
    
    static class AddRejectingSet<K, V> extends ForwardingSet<V>
    {
        final K key;
        
        AddRejectingSet(final K key) {
            this.key = key;
        }
        
        @Override
        public boolean add(final V v) {
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }
        
        @Override
        public boolean addAll(final Collection<? extends V> collection) {
            Preconditions.checkNotNull(collection);
            throw new IllegalArgumentException("Key does not satisfy predicate: " + this.key);
        }
        
        @Override
        protected Set<V> delegate() {
            return Collections.emptySet();
        }
    }
    
    class Entries extends ForwardingCollection<Map.Entry<K, V>>
    {
        @Override
        protected Collection<Map.Entry<K, V>> delegate() {
            return Collections2.filter(FilteredKeyMultimap.this.unfiltered.entries(), FilteredKeyMultimap.this.entryPredicate());
        }
        
        @Override
        public boolean remove(@Nullable final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry entry = (Map.Entry)o;
                if (FilteredKeyMultimap.this.unfiltered.containsKey(entry.getKey()) && FilteredKeyMultimap.this.keyPredicate.apply((Object)entry.getKey())) {
                    return FilteredKeyMultimap.this.unfiltered.remove(entry.getKey(), entry.getValue());
                }
            }
            return false;
        }
    }
}
