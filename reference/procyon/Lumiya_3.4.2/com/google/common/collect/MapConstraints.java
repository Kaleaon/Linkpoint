// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.SortedSet;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Deprecated
@Beta
@GwtCompatible
public final class MapConstraints
{
    private MapConstraints() {
    }
    
    private static <K, V> Map<K, V> checkMap(final Map<? extends K, ? extends V> m, final MapConstraint<? super K, ? super V> mapConstraint) {
        final LinkedHashMap linkedHashMap = new LinkedHashMap((Map<? extends K, ? extends V>)m);
        for (final Map.Entry<? super K, V> entry : linkedHashMap.entrySet()) {
            mapConstraint.checkKeyValue(entry.getKey(), (Object)entry.getValue());
        }
        return linkedHashMap;
    }
    
    private static <K, V> Collection<V> checkValues(final K k, final Iterable<? extends V> iterable, final MapConstraint<? super K, ? super V> mapConstraint) {
        final ArrayList<Object> arrayList = Lists.newArrayList((Iterable<?>)iterable);
        final Iterator<Object> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            mapConstraint.checkKeyValue(k, (Object)iterator.next());
        }
        return (Collection<V>)arrayList;
    }
    
    private static <K, V> Set<Map.Entry<K, Collection<V>>> constrainedAsMapEntries(final Set<Map.Entry<K, Collection<V>>> set, final MapConstraint<? super K, ? super V> mapConstraint) {
        return (Set<Map.Entry<K, Collection<V>>>)new ConstrainedAsMapEntries((Set<Map.Entry<Object, Collection<Object>>>)set, (MapConstraint<? super Object, ? super Object>)mapConstraint);
    }
    
    private static <K, V> Map.Entry<K, Collection<V>> constrainedAsMapEntry(final Map.Entry<K, Collection<V>> entry, final MapConstraint<? super K, ? super V> mapConstraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(mapConstraint);
        return new ForwardingMapEntry<K, Collection<V>>() {
            @Override
            protected Entry<K, Collection<V>> delegate() {
                return entry;
            }
            
            @Override
            public Collection<V> getValue() {
                return Constraints.constrainedTypePreservingCollection(entry.getValue(), new Constraint<V>() {
                    @Override
                    public V checkElement(final V v) {
                        mapConstraint.checkKeyValue(((ForwardingMapEntry<Object, V>)ForwardingMapEntry.this).getKey(), v);
                        return v;
                    }
                });
            }
        };
    }
    
    public static <K, V> BiMap<K, V> constrainedBiMap(final BiMap<K, V> biMap, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedBiMap<K, V>(biMap, null, mapConstraint);
    }
    
    private static <K, V> Collection<Map.Entry<K, V>> constrainedEntries(final Collection<Map.Entry<K, V>> collection, final MapConstraint<? super K, ? super V> mapConstraint) {
        if (!(collection instanceof Set)) {
            return (Collection<Map.Entry<K, V>>)new ConstrainedEntries((Collection<Map.Entry<Object, Object>>)collection, (MapConstraint<? super Object, ? super Object>)mapConstraint);
        }
        return (Collection<Map.Entry<K, V>>)constrainedEntrySet((Set<Map.Entry<Object, Object>>)collection, (MapConstraint<? super Object, ? super Object>)mapConstraint);
    }
    
    private static <K, V> Map.Entry<K, V> constrainedEntry(final Map.Entry<K, V> entry, final MapConstraint<? super K, ? super V> mapConstraint) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkNotNull(mapConstraint);
        return new ForwardingMapEntry<K, V>() {
            @Override
            protected Entry<K, V> delegate() {
                return entry;
            }
            
            @Override
            public V setValue(final V value) {
                mapConstraint.checkKeyValue(((ForwardingMapEntry<Object, V>)this).getKey(), value);
                return entry.setValue(value);
            }
        };
    }
    
    private static <K, V> Set<Map.Entry<K, V>> constrainedEntrySet(final Set<Map.Entry<K, V>> set, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedEntrySet<K, V>(set, mapConstraint);
    }
    
    public static <K, V> ListMultimap<K, V> constrainedListMultimap(final ListMultimap<K, V> listMultimap, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedListMultimap<K, V>(listMultimap, mapConstraint);
    }
    
    public static <K, V> Map<K, V> constrainedMap(final Map<K, V> map, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedMap<K, V>(map, mapConstraint);
    }
    
    public static <K, V> Multimap<K, V> constrainedMultimap(final Multimap<K, V> multimap, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedMultimap<K, V>(multimap, mapConstraint);
    }
    
    public static <K, V> SetMultimap<K, V> constrainedSetMultimap(final SetMultimap<K, V> setMultimap, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedSetMultimap<K, V>(setMultimap, mapConstraint);
    }
    
    public static <K, V> SortedSetMultimap<K, V> constrainedSortedSetMultimap(final SortedSetMultimap<K, V> sortedSetMultimap, final MapConstraint<? super K, ? super V> mapConstraint) {
        return new ConstrainedSortedSetMultimap<K, V>(sortedSetMultimap, mapConstraint);
    }
    
    public static MapConstraint<Object, Object> notNull() {
        return NotNullMapConstraint.INSTANCE;
    }
    
    static class ConstrainedAsMapEntries<K, V> extends ForwardingSet<Map.Entry<K, Collection<V>>>
    {
        private final MapConstraint<? super K, ? super V> constraint;
        private final Set<Map.Entry<K, Collection<V>>> entries;
        
        ConstrainedAsMapEntries(final Set<Map.Entry<K, Collection<V>>> entries, final MapConstraint<? super K, ? super V> constraint) {
            this.entries = entries;
            this.constraint = constraint;
        }
        
        @Override
        public boolean contains(final Object o) {
            return Maps.containsEntryImpl(this.delegate(), o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return this.standardContainsAll(collection);
        }
        
        @Override
        protected Set<Map.Entry<K, Collection<V>>> delegate() {
            return this.entries;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return this.standardEquals(o);
        }
        
        @Override
        public int hashCode() {
            return this.standardHashCode();
        }
        
        @Override
        public Iterator<Map.Entry<K, Collection<V>>> iterator() {
            return new TransformedIterator<Map.Entry<K, Collection<V>>, Map.Entry<K, Collection<V>>>(this.entries.iterator()) {
                @Override
                Map.Entry<K, Collection<V>> transform(final Map.Entry<K, Collection<V>> entry) {
                    return (Map.Entry<K, Collection<V>>)constrainedAsMapEntry((Map.Entry<Object, Collection<Object>>)entry, ConstrainedAsMapEntries.this.constraint);
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            return Maps.removeEntryImpl(this.delegate(), o);
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
        
        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.standardToArray(array);
        }
    }
    
    private static class ConstrainedAsMapValues<K, V> extends ForwardingCollection<Collection<V>>
    {
        final Collection<Collection<V>> delegate;
        final Set<Map.Entry<K, Collection<V>>> entrySet;
        
        ConstrainedAsMapValues(final Collection<Collection<V>> delegate, final Set<Map.Entry<K, Collection<V>>> entrySet) {
            this.delegate = delegate;
            this.entrySet = entrySet;
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.standardContains(o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return this.standardContainsAll(collection);
        }
        
        @Override
        protected Collection<Collection<V>> delegate() {
            return this.delegate;
        }
        
        @Override
        public Iterator<Collection<V>> iterator() {
            return new Iterator<Collection<V>>() {
                final /* synthetic */ Iterator val$iterator = ConstrainedAsMapValues.this.entrySet.iterator();
                
                @Override
                public boolean hasNext() {
                    return this.val$iterator.hasNext();
                }
                
                @Override
                public Collection<V> next() {
                    return this.val$iterator.next().getValue();
                }
                
                @Override
                public void remove() {
                    this.val$iterator.remove();
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.standardRemove(o);
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
        
        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.standardToArray(array);
        }
    }
    
    private static class ConstrainedBiMap<K, V> extends ConstrainedMap<K, V> implements BiMap<K, V>
    {
        volatile BiMap<V, K> inverse;
        
        ConstrainedBiMap(final BiMap<K, V> biMap, @Nullable final BiMap<V, K> inverse, final MapConstraint<? super K, ? super V> mapConstraint) {
            super(biMap, mapConstraint);
            this.inverse = inverse;
        }
        
        @Override
        protected BiMap<K, V> delegate() {
            return (BiMap)super.delegate();
        }
        
        @Override
        public V forcePut(final K k, final V v) {
            this.constraint.checkKeyValue((Object)k, (Object)v);
            return this.delegate().forcePut(k, v);
        }
        
        @Override
        public BiMap<V, K> inverse() {
            if (this.inverse == null) {
                this.inverse = (BiMap<V, K>)new ConstrainedBiMap((BiMap<Object, Object>)this.delegate().inverse(), (BiMap<Object, Object>)this, new InverseConstraint<Object, Object>((MapConstraint<? super Object, ? super Object>)this.constraint));
            }
            return this.inverse;
        }
        
        @Override
        public Set<V> values() {
            return this.delegate().values();
        }
    }
    
    static class ConstrainedMap<K, V> extends ForwardingMap<K, V>
    {
        final MapConstraint<? super K, ? super V> constraint;
        private final Map<K, V> delegate;
        private transient Set<Entry<K, V>> entrySet;
        
        ConstrainedMap(final Map<K, V> map, final MapConstraint<? super K, ? super V> mapConstraint) {
            this.delegate = Preconditions.checkNotNull(map);
            this.constraint = Preconditions.checkNotNull(mapConstraint);
        }
        
        @Override
        protected Map<K, V> delegate() {
            return this.delegate;
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> entrySet = this.entrySet;
            if (entrySet == null) {
                entrySet = (Set<Entry<K, V>>)constrainedEntrySet((Set<Entry<Object, Object>>)this.delegate.entrySet(), this.constraint);
                this.entrySet = entrySet;
            }
            return entrySet;
        }
        
        @Override
        public V put(final K k, final V v) {
            this.constraint.checkKeyValue((Object)k, (Object)v);
            return this.delegate.put(k, v);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            this.delegate.putAll(checkMap((Map<?, ?>)map, (MapConstraint<? super Object, ? super Object>)this.constraint));
        }
    }
    
    private static class ConstrainedEntries<K, V> extends ForwardingCollection<Map.Entry<K, V>>
    {
        final MapConstraint<? super K, ? super V> constraint;
        final Collection<Map.Entry<K, V>> entries;
        
        ConstrainedEntries(final Collection<Map.Entry<K, V>> entries, final MapConstraint<? super K, ? super V> constraint) {
            this.entries = entries;
            this.constraint = constraint;
        }
        
        @Override
        public boolean contains(final Object o) {
            return Maps.containsEntryImpl(this.delegate(), o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return this.standardContainsAll(collection);
        }
        
        @Override
        protected Collection<Map.Entry<K, V>> delegate() {
            return this.entries;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new TransformedIterator<Map.Entry<K, V>, Map.Entry<K, V>>(this.entries.iterator()) {
                @Override
                Map.Entry<K, V> transform(final Map.Entry<K, V> entry) {
                    return (Map.Entry<K, V>)constrainedEntry((Map.Entry<Object, Object>)entry, ConstrainedEntries.this.constraint);
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            return Maps.removeEntryImpl(this.delegate(), o);
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return this.standardRemoveAll(collection);
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return this.standardRetainAll(collection);
        }
        
        @Override
        public Object[] toArray() {
            return this.standardToArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            return this.standardToArray(array);
        }
    }
    
    static class ConstrainedEntrySet<K, V> extends ConstrainedEntries<K, V> implements Set<Map.Entry<K, V>>
    {
        ConstrainedEntrySet(final Set<Map.Entry<K, V>> set, final MapConstraint<? super K, ? super V> mapConstraint) {
            super(set, mapConstraint);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return Sets.equalsImpl(this, o);
        }
        
        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }
    
    private static class ConstrainedListMultimap<K, V> extends ConstrainedMultimap<K, V> implements ListMultimap<K, V>
    {
        ConstrainedListMultimap(final ListMultimap<K, V> listMultimap, final MapConstraint<? super K, ? super V> mapConstraint) {
            super(listMultimap, mapConstraint);
        }
        
        @Override
        public List<V> get(final K k) {
            return (List)super.get(k);
        }
        
        @Override
        public List<V> removeAll(final Object o) {
            return (List)super.removeAll(o);
        }
        
        @Override
        public List<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            return (List)super.replaceValues(k, iterable);
        }
    }
    
    private static class ConstrainedMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable
    {
        transient Map<K, Collection<V>> asMap;
        final MapConstraint<? super K, ? super V> constraint;
        final Multimap<K, V> delegate;
        transient Collection<Map.Entry<K, V>> entries;
        
        public ConstrainedMultimap(final Multimap<K, V> multimap, final MapConstraint<? super K, ? super V> mapConstraint) {
            this.delegate = Preconditions.checkNotNull(multimap);
            this.constraint = Preconditions.checkNotNull(mapConstraint);
        }
        
        @Override
        public Map<K, Collection<V>> asMap() {
            Map<K, Collection<V>> asMap = this.asMap;
            if (asMap == null) {
                asMap = new AsMap();
                this.asMap = asMap;
            }
            return asMap;
        }
        
        @Override
        protected Multimap<K, V> delegate() {
            return this.delegate;
        }
        
        @Override
        public Collection<Map.Entry<K, V>> entries() {
            Collection<Map.Entry<K, V>> entries = this.entries;
            if (entries == null) {
                entries = (Collection<Map.Entry<K, V>>)constrainedEntries((Collection<Map.Entry<Object, Object>>)this.delegate.entries(), this.constraint);
                this.entries = entries;
            }
            return entries;
        }
        
        @Override
        public Collection<V> get(final K k) {
            return Constraints.constrainedTypePreservingCollection(this.delegate.get(k), new Constraint<V>() {
                @Override
                public V checkElement(final V v) {
                    ConstrainedMultimap.this.constraint.checkKeyValue((Object)k, (Object)v);
                    return v;
                }
            });
        }
        
        @Override
        public boolean put(final K k, final V v) {
            this.constraint.checkKeyValue((Object)k, (Object)v);
            return this.delegate.put(k, v);
        }
        
        @Override
        public boolean putAll(final Multimap<? extends K, ? extends V> multimap) {
            final Iterator<Map.Entry<? extends K, ? extends V>> iterator = multimap.entries().iterator();
            boolean b = false;
            while (iterator.hasNext()) {
                final Map.Entry<K, V> entry = (Map.Entry<K, V>)iterator.next();
                b |= this.put(entry.getKey(), entry.getValue());
            }
            return b;
        }
        
        @Override
        public boolean putAll(final K k, final Iterable<? extends V> iterable) {
            return this.delegate.putAll(k, checkValues(k, (Iterable<?>)iterable, (MapConstraint<? super Object, ? super Object>)this.constraint));
        }
        
        @Override
        public Collection<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            return this.delegate.replaceValues(k, checkValues(k, (Iterable<?>)iterable, (MapConstraint<? super Object, ? super Object>)this.constraint));
        }
        
        class AsMap extends ForwardingMap<K, Collection<V>>
        {
            Set<Entry<K, Collection<V>>> entrySet;
            final /* synthetic */ Map val$asMapDelegate;
            Collection<Collection<V>> values;
            
            AsMap(final Map val$asMapDelegate) {
                this.val$asMapDelegate = val$asMapDelegate;
            }
            
            @Override
            public boolean containsValue(final Object o) {
                return this.values().contains(o);
            }
            
            @Override
            protected Map<K, Collection<V>> delegate() {
                return this.val$asMapDelegate;
            }
            
            @Override
            public Set<Entry<K, Collection<V>>> entrySet() {
                Set<Entry<K, Collection<V>>> entrySet = this.entrySet;
                if (entrySet == null) {
                    entrySet = (Set<Entry<K, Collection<V>>>)constrainedAsMapEntries((Set<Entry<Object, Collection<Object>>>)this.val$asMapDelegate.entrySet(), ConstrainedMultimap.this.constraint);
                    this.entrySet = entrySet;
                }
                return entrySet;
            }
            
            @Override
            public Collection<V> get(final Object o) {
                try {
                    Collection<V> value = ConstrainedMultimap.this.get(o);
                    if (value.isEmpty()) {
                        value = null;
                    }
                    return value;
                }
                catch (final ClassCastException ex) {
                    return null;
                }
            }
            
            @Override
            public Collection<Collection<V>> values() {
                Collection<Collection<V>> values = this.values;
                if (values == null) {
                    values = (Collection<Collection<V>>)new ConstrainedAsMapValues((Collection<Collection<Object>>)this.delegate().values(), this.entrySet());
                    this.values = values;
                }
                return values;
            }
        }
    }
    
    private static class ConstrainedSetMultimap<K, V> extends ConstrainedMultimap<K, V> implements SetMultimap<K, V>
    {
        ConstrainedSetMultimap(final SetMultimap<K, V> setMultimap, final MapConstraint<? super K, ? super V> mapConstraint) {
            super(setMultimap, mapConstraint);
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
    }
    
    private static class ConstrainedSortedSetMultimap<K, V> extends ConstrainedSetMultimap<K, V> implements SortedSetMultimap<K, V>
    {
        ConstrainedSortedSetMultimap(final SortedSetMultimap<K, V> sortedSetMultimap, final MapConstraint<? super K, ? super V> mapConstraint) {
            super(sortedSetMultimap, mapConstraint);
        }
        
        @Override
        public SortedSet<V> get(final K k) {
            return (SortedSet)super.get(k);
        }
        
        @Override
        public SortedSet<V> removeAll(final Object o) {
            return (SortedSet)super.removeAll(o);
        }
        
        @Override
        public SortedSet<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            return (SortedSet)super.replaceValues(k, iterable);
        }
        
        @Override
        public Comparator<? super V> valueComparator() {
            return ((SortedSetMultimap)this.delegate()).valueComparator();
        }
    }
    
    private static class InverseConstraint<K, V> implements MapConstraint<K, V>
    {
        final MapConstraint<? super V, ? super K> constraint;
        
        public InverseConstraint(final MapConstraint<? super V, ? super K> mapConstraint) {
            this.constraint = Preconditions.checkNotNull(mapConstraint);
        }
        
        @Override
        public void checkKeyValue(final K k, final V v) {
            this.constraint.checkKeyValue((Object)v, (Object)k);
        }
    }
    
    private enum NotNullMapConstraint implements MapConstraint<Object, Object>
    {
        INSTANCE;
        
        @Override
        public void checkKeyValue(final Object o, final Object o2) {
            Preconditions.checkNotNull(o);
            Preconditions.checkNotNull(o2);
        }
        
        @Override
        public String toString() {
            return "Not null";
        }
    }
}
