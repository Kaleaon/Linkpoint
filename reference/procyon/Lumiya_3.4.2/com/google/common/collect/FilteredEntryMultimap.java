// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import com.google.common.base.MoreObjects;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.Collection;
import com.google.common.base.Preconditions;
import java.util.Map;
import com.google.common.base.Predicate;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
class FilteredEntryMultimap<K, V> extends AbstractMultimap<K, V> implements FilteredMultimap<K, V>
{
    final Predicate<? super Map.Entry<K, V>> predicate;
    final Multimap<K, V> unfiltered;
    
    FilteredEntryMultimap(final Multimap<K, V> multimap, final Predicate<? super Map.Entry<K, V>> predicate) {
        this.unfiltered = Preconditions.checkNotNull(multimap);
        this.predicate = Preconditions.checkNotNull(predicate);
    }
    
    static <E> Collection<E> filterCollection(final Collection<E> collection, final Predicate<? super E> predicate) {
        if (!(collection instanceof Set)) {
            return Collections2.filter(collection, predicate);
        }
        return (Collection<E>)Sets.filter((Set<Object>)collection, (Predicate<? super Object>)predicate);
    }
    
    private boolean satisfies(final K k, final V v) {
        return this.predicate.apply(Maps.immutableEntry(k, v));
    }
    
    @Override
    public void clear() {
        this.entries().clear();
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.asMap().get(o) != null;
    }
    
    @Override
    Map<K, Collection<V>> createAsMap() {
        return new AsMap();
    }
    
    @Override
    Collection<Map.Entry<K, V>> createEntries() {
        return filterCollection(this.unfiltered.entries(), this.predicate);
    }
    
    @Override
    Multiset<K> createKeys() {
        return (Multiset<K>)new Keys();
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
        return this.predicate;
    }
    
    @Override
    public Collection<V> get(final K k) {
        return filterCollection(this.unfiltered.get(k), new ValuePredicate(k));
    }
    
    @Override
    public Set<K> keySet() {
        return this.asMap().keySet();
    }
    
    @Override
    public Collection<V> removeAll(@Nullable final Object o) {
        return MoreObjects.firstNonNull(this.asMap().remove(o), this.unmodifiableEmptyCollection());
    }
    
    boolean removeEntriesIf(final Predicate<? super Map.Entry<K, Collection<V>>> predicate) {
        final Iterator<Map.Entry<K, Collection<V>>> iterator = this.unfiltered.asMap().entrySet().iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator.next();
            final Object key = entry.getKey();
            final Collection<Object> filterCollection = filterCollection((Collection<Object>)entry.getValue(), (Predicate<? super Object>)new ValuePredicate((K)key));
            if (!filterCollection.isEmpty() && predicate.apply((Object)Maps.immutableEntry(key, filterCollection))) {
                if (filterCollection.size() != ((Collection)entry.getValue()).size()) {
                    filterCollection.clear();
                }
                else {
                    iterator.remove();
                }
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public int size() {
        return this.entries().size();
    }
    
    @Override
    public Multimap<K, V> unfiltered() {
        return this.unfiltered;
    }
    
    Collection<V> unmodifiableEmptyCollection() {
        Object o;
        if (!(this.unfiltered instanceof SetMultimap)) {
            o = Collections.emptyList();
        }
        else {
            o = Collections.emptySet();
        }
        return (Collection<V>)o;
    }
    
    class AsMap extends ViewCachingAbstractMap<K, Collection<V>>
    {
        @Override
        public void clear() {
            FilteredEntryMultimap.this.clear();
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return this.get(o) != null;
        }
        
        @Override
        Set<Entry<K, Collection<V>>> createEntrySet() {
            return (Set<Entry<K, Collection<V>>>)new EntrySetImpl();
        }
        
        @Override
        Set<K> createKeySet() {
            return (Set<K>)new KeySetImpl();
        }
        
        @Override
        Collection<Collection<V>> createValues() {
            return (Collection<Collection<V>>)new ValuesImpl();
        }
        
        @Override
        public Collection<V> get(@Nullable final Object o) {
            final Collection collection = FilteredEntryMultimap.this.unfiltered.asMap().get(o);
            if (collection != null) {
                Collection<Object> filterCollection = FilteredEntryMultimap.filterCollection((Collection<Object>)collection, (Predicate<? super Object>)new ValuePredicate((K)o));
                if (filterCollection.isEmpty()) {
                    filterCollection = null;
                }
                return (Collection<V>)filterCollection;
            }
            return null;
        }
        
        @Override
        public Collection<V> remove(@Nullable final Object o) {
            final Collection collection = FilteredEntryMultimap.this.unfiltered.asMap().get(o);
            if (collection == null) {
                return null;
            }
            final ArrayList<Object> arrayList = Lists.newArrayList();
            final Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                final Object next = iterator.next();
                if (FilteredEntryMultimap.this.satisfies(o, next)) {
                    iterator.remove();
                    arrayList.add(next);
                }
            }
            if (arrayList.isEmpty()) {
                return null;
            }
            if (!(FilteredEntryMultimap.this.unfiltered instanceof SetMultimap)) {
                return (Collection<V>)Collections.unmodifiableList((List<?>)arrayList);
            }
            return (Collection<V>)Collections.unmodifiableSet((Set<?>)Sets.newLinkedHashSet((Iterable<?>)arrayList));
        }
        
        class EntrySetImpl extends Maps.EntrySet<K, Collection<V>>
        {
            @Override
            public Iterator<Entry<K, Collection<V>>> iterator() {
                return new AbstractIterator<Entry<K, Collection<V>>>() {
                    final Iterator<Entry<K, Collection<V>>> backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
                    
                    @Override
                    protected Entry<K, Collection<V>> computeNext() {
                        while (this.backingIterator.hasNext()) {
                            final Entry entry = this.backingIterator.next();
                            final Object key = entry.getKey();
                            final Collection<Object> filterCollection = FilteredEntryMultimap.filterCollection((Collection<Object>)entry.getValue(), (Predicate<? super Object>)new ValuePredicate((K)key));
                            if (!filterCollection.isEmpty()) {
                                return (Entry<K, Collection<V>>)Maps.immutableEntry(key, filterCollection);
                            }
                        }
                        return (Entry<K, Collection<V>>)((AbstractIterator<Map.Entry>)this).endOfData();
                    }
                };
            }
            
            @Override
            Map<K, Collection<V>> map() {
                return AsMap.this;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in((Collection<? extends Entry<K, Collection<V>>>)collection));
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in((Collection<? extends Entry<K, Collection<V>>>)collection)));
            }
            
            @Override
            public int size() {
                return Iterators.size(this.iterator());
            }
        }
        
        class KeySetImpl extends Maps.KeySet<K, Collection<V>>
        {
            KeySetImpl() {
                super(AsMap.this);
            }
            
            @Override
            public boolean remove(@Nullable final Object o) {
                return AsMap.this.remove(o) != null;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(collection)));
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in((Collection<? extends K>)collection))));
            }
        }
        
        class ValuesImpl extends Maps.Values<K, Collection<V>>
        {
            ValuesImpl() {
                super(AsMap.this);
            }
            
            @Override
            public boolean remove(@Nullable final Object o) {
                if (o instanceof Collection) {
                    final Collection collection = (Collection)o;
                    final Iterator<Map.Entry<K, Collection<V>>> iterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
                    while (iterator.hasNext()) {
                        final Map.Entry entry = iterator.next();
                        final Collection<Object> filterCollection = FilteredEntryMultimap.filterCollection((Collection<Object>)entry.getValue(), (Predicate<? super Object>)new ValuePredicate((K)entry.getKey()));
                        if (!filterCollection.isEmpty() && collection.equals(filterCollection)) {
                            if (filterCollection.size() != ((Collection)entry.getValue()).size()) {
                                filterCollection.clear();
                            }
                            else {
                                iterator.remove();
                            }
                            return true;
                        }
                    }
                }
                return false;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(collection)));
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in((Collection<? extends V>)collection))));
            }
        }
    }
    
    class Keys extends Multimaps.Keys<K, V>
    {
        Keys() {
            super(FilteredEntryMultimap.this);
        }
        
        @Override
        public Set<Entry<K>> entrySet() {
            return (Set<Entry<K>>)new Multisets.EntrySet<K>() {
                private boolean removeEntriesIf(final Predicate<? super Entry<K>> predicate) {
                    return FilteredEntryMultimap.this.removeEntriesIf(new Predicate<Map.Entry<K, Collection<V>>>() {
                        @Override
                        public boolean apply(final Map.Entry<K, Collection<V>> entry) {
                            return predicate.apply(Multisets.immutableEntry(entry.getKey(), entry.getValue().size()));
                        }
                    });
                }
                
                @Override
                public Iterator<Entry<K>> iterator() {
                    return ((Multimaps.Keys<K, V>)Keys.this).entryIterator();
                }
                
                @Override
                Multiset<K> multiset() {
                    return (Multiset<K>)Keys.this;
                }
                
                @Override
                public boolean removeAll(final Collection<?> collection) {
                    return this.removeEntriesIf(Predicates.in((Collection<? extends Entry<K>>)collection));
                }
                
                @Override
                public boolean retainAll(final Collection<?> collection) {
                    return this.removeEntriesIf(Predicates.not(Predicates.in((Collection<? extends Entry<K>>)collection)));
                }
                
                @Override
                public int size() {
                    return FilteredEntryMultimap.this.keySet().size();
                }
            };
        }
        
        @Override
        public int remove(@Nullable final Object o, final int n) {
            CollectPreconditions.checkNonnegative(n, "occurrences");
            if (n == 0) {
                return ((Multimaps.Keys)this).count(o);
            }
            final Collection collection = FilteredEntryMultimap.this.unfiltered.asMap().get(o);
            if (collection != null) {
                final Iterator iterator = collection.iterator();
                int n2 = 0;
                while (iterator.hasNext()) {
                    if (FilteredEntryMultimap.this.satisfies(o, iterator.next())) {
                        final int n3 = n2 + 1;
                        if ((n2 = n3) > n) {
                            continue;
                        }
                        iterator.remove();
                        n2 = n3;
                    }
                }
                return n2;
            }
            return 0;
        }
    }
    
    final class ValuePredicate implements Predicate<V>
    {
        private final K key;
        
        ValuePredicate(final K key) {
            this.key = key;
        }
        
        @Override
        public boolean apply(@Nullable final V v) {
            return FilteredEntryMultimap.this.satisfies(this.key, v);
        }
    }
}
