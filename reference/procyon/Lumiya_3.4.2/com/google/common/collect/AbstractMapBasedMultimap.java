// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.ConcurrentModificationException;
import java.util.AbstractCollection;
import java.util.ListIterator;
import java.util.Comparator;
import java.util.NavigableSet;
import com.google.common.annotations.GwtIncompatible;
import java.util.NavigableMap;
import java.util.Collections;
import java.util.SortedSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.RandomAccess;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(emulated = true)
abstract class AbstractMapBasedMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable
{
    private static final long serialVersionUID = 2447537837011683357L;
    private transient Map<K, Collection<V>> map;
    private transient int totalSize;
    
    protected AbstractMapBasedMultimap(final Map<K, Collection<V>> map) {
        Preconditions.checkArgument(map.isEmpty());
        this.map = map;
    }
    
    static /* synthetic */ int access$212(final AbstractMapBasedMultimap abstractMapBasedMultimap, int totalSize) {
        totalSize += abstractMapBasedMultimap.totalSize;
        return abstractMapBasedMultimap.totalSize = totalSize;
    }
    
    static /* synthetic */ int access$220(final AbstractMapBasedMultimap abstractMapBasedMultimap, int totalSize) {
        totalSize = abstractMapBasedMultimap.totalSize - totalSize;
        return abstractMapBasedMultimap.totalSize = totalSize;
    }
    
    private Collection<V> getOrCreateCollection(@Nullable final K k) {
        final Collection collection = this.map.get(k);
        Collection<V> collection2;
        if (collection != null) {
            collection2 = collection;
        }
        else {
            final Collection<V> collection3 = this.createCollection(k);
            this.map.put(k, collection3);
            collection2 = collection3;
        }
        return collection2;
    }
    
    private Iterator<V> iteratorOrListIterator(final Collection<V> collection) {
        Iterator iterator;
        if (!(collection instanceof List)) {
            iterator = collection.iterator();
        }
        else {
            iterator = ((List)collection).listIterator();
        }
        return iterator;
    }
    
    private int removeValuesForKey(final Object o) {
        final Collection<V> collection = Maps.safeRemove(this.map, o);
        int size;
        if (collection == null) {
            size = 0;
        }
        else {
            size = collection.size();
            collection.clear();
            this.totalSize -= size;
        }
        return size;
    }
    
    private List<V> wrapList(@Nullable final K k, final List<V> list, @Nullable final WrappedCollection collection) {
        List<V> list2;
        if (!(list instanceof RandomAccess)) {
            list2 = new WrappedList(k, list, collection);
        }
        else {
            list2 = new RandomAccessWrappedList(k, list, collection);
        }
        return list2;
    }
    
    Map<K, Collection<V>> backingMap() {
        return this.map;
    }
    
    @Override
    public void clear() {
        final Iterator<Collection<V>> iterator = this.map.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().clear();
        }
        this.map.clear();
        this.totalSize = 0;
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    Map<K, Collection<V>> createAsMap() {
        Maps.ViewCachingAbstractMap<K, Collection<V>> viewCachingAbstractMap;
        if (!(this.map instanceof SortedMap)) {
            viewCachingAbstractMap = new AsMap(this.map);
        }
        else {
            viewCachingAbstractMap = new SortedAsMap((SortedMap)this.map);
        }
        return viewCachingAbstractMap;
    }
    
    abstract Collection<V> createCollection();
    
    Collection<V> createCollection(@Nullable final K k) {
        return this.createCollection();
    }
    
    @Override
    Set<K> createKeySet() {
        Maps.KeySet<K, Collection<V>> set;
        if (!(this.map instanceof SortedMap)) {
            set = new KeySet(this.map);
        }
        else {
            set = new SortedKeySet((SortedMap)this.map);
        }
        return (Set<K>)set;
    }
    
    Collection<V> createUnmodifiableEmptyCollection() {
        return this.unmodifiableCollectionSubclass(this.createCollection());
    }
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return super.entries();
    }
    
    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new Itr<Map.Entry<K, V>>() {
            Map.Entry<K, V> output(final K k, final V v) {
                return Maps.immutableEntry(k, v);
            }
        };
    }
    
    @Override
    public Collection<V> get(@Nullable final K k) {
        Collection<V> collection = this.map.get(k);
        if (collection == null) {
            collection = this.createCollection(k);
        }
        return this.wrapCollection(k, collection);
    }
    
    @Override
    public boolean put(@Nullable final K k, @Nullable final V v) {
        final Collection collection = this.map.get(k);
        if (collection != null) {
            if (!collection.add(v)) {
                return false;
            }
            ++this.totalSize;
            return true;
        }
        else {
            final Collection<V> collection2 = this.createCollection(k);
            if (!collection2.add(v)) {
                throw new AssertionError((Object)"New Collection violated the Collection spec");
            }
            ++this.totalSize;
            this.map.put(k, collection2);
            return true;
        }
    }
    
    @Override
    public Collection<V> removeAll(@Nullable final Object o) {
        final Collection collection = this.map.remove(o);
        if (collection != null) {
            final Collection<V> collection2 = this.createCollection();
            collection2.addAll(collection);
            this.totalSize -= collection.size();
            collection.clear();
            return this.unmodifiableCollectionSubclass(collection2);
        }
        return this.createUnmodifiableEmptyCollection();
    }
    
    @Override
    public Collection<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        final Iterator<? extends V> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            final Collection<V> orCreateCollection = this.getOrCreateCollection(k);
            final Collection<V> collection = this.createCollection();
            collection.addAll((Collection<? extends V>)orCreateCollection);
            this.totalSize -= orCreateCollection.size();
            orCreateCollection.clear();
            while (iterator.hasNext()) {
                if (orCreateCollection.add((Object)iterator.next())) {
                    ++this.totalSize;
                }
            }
            return this.unmodifiableCollectionSubclass(collection);
        }
        return this.removeAll(k);
    }
    
    final void setMap(final Map<K, Collection<V>> map) {
        this.map = map;
        this.totalSize = 0;
        for (final Collection collection : map.values()) {
            Preconditions.checkArgument(!collection.isEmpty());
            this.totalSize += collection.size();
        }
    }
    
    @Override
    public int size() {
        return this.totalSize;
    }
    
    Collection<V> unmodifiableCollectionSubclass(final Collection<V> c) {
        if (c instanceof SortedSet) {
            return (Collection<V>)Collections.unmodifiableSortedSet((SortedSet<Object>)c);
        }
        if (c instanceof Set) {
            return (Collection<V>)Collections.unmodifiableSet((Set<?>)c);
        }
        if (!(c instanceof List)) {
            return Collections.unmodifiableCollection((Collection<? extends V>)c);
        }
        return (Collection<V>)Collections.unmodifiableList((List<?>)c);
    }
    
    @Override
    Iterator<V> valueIterator() {
        return new Itr<V>() {
            @Override
            V output(final K k, final V v) {
                return v;
            }
        };
    }
    
    @Override
    public Collection<V> values() {
        return super.values();
    }
    
    Collection<V> wrapCollection(@Nullable final K k, final Collection<V> collection) {
        if (collection instanceof SortedSet) {
            return new WrappedSortedSet(k, (SortedSet<V>)collection, null);
        }
        if (collection instanceof Set) {
            return new WrappedSet(k, (Set<V>)collection);
        }
        if (!(collection instanceof List)) {
            return new WrappedCollection(k, collection, null);
        }
        return this.wrapList(k, (List<V>)collection, null);
    }
    
    private class AsMap extends ViewCachingAbstractMap<K, Collection<V>>
    {
        final transient Map<K, Collection<V>> submap;
        
        AsMap(final Map<K, Collection<V>> submap) {
            this.submap = submap;
        }
        
        @Override
        public void clear() {
            if (this.submap != AbstractMapBasedMultimap.this.map) {
                Iterators.clear(new AsMapIterator());
            }
            else {
                AbstractMapBasedMultimap.this.clear();
            }
        }
        
        @Override
        public boolean containsKey(final Object o) {
            return Maps.safeContainsKey(this.submap, o);
        }
        
        protected Set<Entry<K, Collection<V>>> createEntrySet() {
            return (Set<Entry<K, Collection<V>>>)new AsMapEntries();
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (this == o || this.submap.equals(o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Collection<V> get(final Object o) {
            final Collection<V> collection = Maps.safeGet(this.submap, o);
            if (collection != null) {
                return AbstractMapBasedMultimap.this.wrapCollection(o, collection);
            }
            return null;
        }
        
        @Override
        public int hashCode() {
            return this.submap.hashCode();
        }
        
        @Override
        public Set<K> keySet() {
            return AbstractMapBasedMultimap.this.keySet();
        }
        
        @Override
        public Collection<V> remove(final Object o) {
            final Collection collection = this.submap.remove(o);
            if (collection != null) {
                final Collection<V> collection2 = AbstractMapBasedMultimap.this.createCollection();
                collection2.addAll(collection);
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, collection.size());
                collection.clear();
                return collection2;
            }
            return null;
        }
        
        @Override
        public int size() {
            return this.submap.size();
        }
        
        @Override
        public String toString() {
            return this.submap.toString();
        }
        
        Entry<K, Collection<V>> wrapEntry(final Entry<K, Collection<V>> entry) {
            final K key = entry.getKey();
            return Maps.immutableEntry(key, AbstractMapBasedMultimap.this.wrapCollection(key, entry.getValue()));
        }
        
        class AsMapEntries extends Maps.EntrySet<K, Collection<V>>
        {
            @Override
            public boolean contains(final Object o) {
                return Collections2.safeContains(AsMap.this.submap.entrySet(), o);
            }
            
            @Override
            public Iterator<Entry<K, Collection<V>>> iterator() {
                return new AsMapIterator();
            }
            
            @Override
            Map<K, Collection<V>> map() {
                return AsMap.this;
            }
            
            @Override
            public boolean remove(final Object o) {
                if (this.contains(o)) {
                    AbstractMapBasedMultimap.this.removeValuesForKey(((Entry)o).getKey());
                    return true;
                }
                return false;
            }
        }
        
        class AsMapIterator implements Iterator<Entry<K, Collection<V>>>
        {
            Collection<V> collection;
            final Iterator<Entry<K, Collection<V>>> delegateIterator;
            
            AsMapIterator() {
                this.delegateIterator = AsMap.this.submap.entrySet().iterator();
            }
            
            @Override
            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }
            
            @Override
            public Entry<K, Collection<V>> next() {
                final Entry entry = this.delegateIterator.next();
                this.collection = entry.getValue();
                return AsMap.this.wrapEntry(entry);
            }
            
            @Override
            public void remove() {
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, this.collection.size());
                this.collection.clear();
            }
        }
    }
    
    private abstract class Itr<T> implements Iterator<T>
    {
        Collection<V> collection;
        K key;
        final Iterator<Map.Entry<K, Collection<V>>> keyIterator;
        Iterator<V> valueIterator;
        
        Itr() {
            this.keyIterator = AbstractMapBasedMultimap.this.map.entrySet().iterator();
            this.key = null;
            this.collection = null;
            this.valueIterator = Iterators.emptyModifiableIterator();
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (this.keyIterator.hasNext() || this.valueIterator.hasNext()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public T next() {
            if (!this.valueIterator.hasNext()) {
                final Map.Entry entry = this.keyIterator.next();
                this.key = entry.getKey();
                this.collection = (Collection)entry.getValue();
                this.valueIterator = this.collection.iterator();
            }
            return this.output(this.key, this.valueIterator.next());
        }
        
        abstract T output(final K p0, final V p1);
        
        @Override
        public void remove() {
            this.valueIterator.remove();
            if (this.collection.isEmpty()) {
                this.keyIterator.remove();
            }
            AbstractMapBasedMultimap.this.totalSize--;
        }
    }
    
    private class KeySet extends Maps.KeySet<K, Collection<V>>
    {
        KeySet(final Map<K, Collection<V>> map) {
            super(map);
        }
        
        @Override
        public void clear() {
            Iterators.clear(this.iterator());
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return ((Maps.KeySet<K, Collection<V>>)this).map().keySet().containsAll(collection);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (this == o || ((Maps.KeySet<K, Collection<V>>)this).map().keySet().equals(o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return ((Maps.KeySet<K, Collection<V>>)this).map().keySet().hashCode();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new Iterator<K>() {
                Map.Entry<K, Collection<V>> entry;
                final /* synthetic */ Iterator val$entryIterator = ((Maps.KeySet<K, Collection<V>>)this).map().entrySet().iterator();
                
                @Override
                public boolean hasNext() {
                    return this.val$entryIterator.hasNext();
                }
                
                @Override
                public K next() {
                    this.entry = (Map.Entry<K, Collection<V>>)this.val$entryIterator.next();
                    return this.entry.getKey();
                }
                
                @Override
                public void remove() {
                    CollectPreconditions.checkRemove(this.entry != null);
                    final Collection collection = this.entry.getValue();
                    this.val$entryIterator.remove();
                    AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, collection.size());
                    collection.clear();
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            final Collection collection = ((Maps.KeySet<K, Collection>)this).map().remove(o);
            int size;
            if (collection == null) {
                size = 0;
            }
            else {
                size = collection.size();
                collection.clear();
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, size);
            }
            return size > 0;
        }
    }
    
    @GwtIncompatible("NavigableAsMap")
    class NavigableAsMap extends SortedAsMap implements NavigableMap<K, Collection<V>>
    {
        NavigableAsMap(final NavigableMap<K, Collection<V>> navigableMap) {
            super(navigableMap);
        }
        
        @Override
        public Entry<K, Collection<V>> ceilingEntry(final K k) {
            final Entry<K, Collection<V>> entry = null;
            final Map.Entry<K, Collection<V>> ceilingEntry = this.sortedMap().ceilingEntry(k);
            Entry<K, Collection<V>> wrapEntry = entry;
            if (ceilingEntry != null) {
                wrapEntry = ((AsMap)this).wrapEntry(ceilingEntry);
            }
            return wrapEntry;
        }
        
        @Override
        public K ceilingKey(final K k) {
            return this.sortedMap().ceilingKey(k);
        }
        
        NavigableSet<K> createKeySet() {
            return new AbstractMapBasedMultimap.NavigableKeySet(this.sortedMap());
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.descendingMap().navigableKeySet();
        }
        
        @Override
        public NavigableMap<K, Collection<V>> descendingMap() {
            return new NavigableAsMap(this.sortedMap().descendingMap());
        }
        
        @Override
        public Entry<K, Collection<V>> firstEntry() {
            Entry<K, Collection<V>> wrapEntry = null;
            final Map.Entry<K, Collection<V>> firstEntry = this.sortedMap().firstEntry();
            if (firstEntry != null) {
                wrapEntry = ((AsMap)this).wrapEntry(firstEntry);
            }
            return wrapEntry;
        }
        
        @Override
        public Entry<K, Collection<V>> floorEntry(final K k) {
            final Entry<K, Collection<V>> entry = null;
            final Map.Entry<K, Collection<V>> floorEntry = this.sortedMap().floorEntry(k);
            Entry<K, Collection<V>> wrapEntry = entry;
            if (floorEntry != null) {
                wrapEntry = ((AsMap)this).wrapEntry(floorEntry);
            }
            return wrapEntry;
        }
        
        @Override
        public K floorKey(final K k) {
            return this.sortedMap().floorKey(k);
        }
        
        @Override
        public NavigableMap<K, Collection<V>> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public NavigableMap<K, Collection<V>> headMap(final K k, final boolean b) {
            return new NavigableAsMap(this.sortedMap().headMap(k, b));
        }
        
        @Override
        public Entry<K, Collection<V>> higherEntry(final K k) {
            final Entry<K, Collection<V>> entry = null;
            final Map.Entry<K, Collection<V>> higherEntry = this.sortedMap().higherEntry(k);
            Entry<K, Collection<V>> wrapEntry = entry;
            if (higherEntry != null) {
                wrapEntry = ((AsMap)this).wrapEntry(higherEntry);
            }
            return wrapEntry;
        }
        
        @Override
        public K higherKey(final K k) {
            return this.sortedMap().higherKey(k);
        }
        
        @Override
        public NavigableSet<K> keySet() {
            return (NavigableSet)super.keySet();
        }
        
        @Override
        public Entry<K, Collection<V>> lastEntry() {
            Entry<K, Collection<V>> wrapEntry = null;
            final Map.Entry<K, Collection<V>> lastEntry = this.sortedMap().lastEntry();
            if (lastEntry != null) {
                wrapEntry = ((AsMap)this).wrapEntry(lastEntry);
            }
            return wrapEntry;
        }
        
        @Override
        public Entry<K, Collection<V>> lowerEntry(final K k) {
            final Entry<K, Collection<V>> entry = null;
            final Map.Entry<K, Collection<V>> lowerEntry = this.sortedMap().lowerEntry(k);
            Entry<K, Collection<V>> wrapEntry = entry;
            if (lowerEntry != null) {
                wrapEntry = ((AsMap)this).wrapEntry(lowerEntry);
            }
            return wrapEntry;
        }
        
        @Override
        public K lowerKey(final K k) {
            return this.sortedMap().lowerKey(k);
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            return this.keySet();
        }
        
        Entry<K, Collection<V>> pollAsMapEntry(final Iterator<Entry<K, Collection<V>>> iterator) {
            if (iterator.hasNext()) {
                final Entry entry = (Entry)iterator.next();
                final Collection<V> collection = AbstractMapBasedMultimap.this.createCollection();
                collection.addAll((Collection<? extends V>)entry.getValue());
                iterator.remove();
                return Maps.immutableEntry(entry.getKey(), AbstractMapBasedMultimap.this.unmodifiableCollectionSubclass(collection));
            }
            return null;
        }
        
        @Override
        public Entry<K, Collection<V>> pollFirstEntry() {
            return this.pollAsMapEntry(((Maps.ViewCachingAbstractMap<K, Collection<V>>)this).entrySet().iterator());
        }
        
        @Override
        public Entry<K, Collection<V>> pollLastEntry() {
            return (Entry<K, Collection<V>>)this.pollAsMapEntry((Iterator<Entry<Object, Collection<V>>>)this.descendingMap().entrySet().iterator());
        }
        
        NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap)super.sortedMap();
        }
        
        @Override
        public NavigableMap<K, Collection<V>> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, Collection<V>> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return new NavigableAsMap(this.sortedMap().subMap(k, b, i, b2));
        }
        
        @Override
        public NavigableMap<K, Collection<V>> tailMap(final K k) {
            return this.tailMap(k, true);
        }
        
        @Override
        public NavigableMap<K, Collection<V>> tailMap(final K k, final boolean b) {
            return new NavigableAsMap(this.sortedMap().tailMap(k, b));
        }
    }
    
    private class SortedAsMap extends AsMap implements SortedMap<K, Collection<V>>
    {
        SortedSet<K> sortedKeySet;
        
        SortedAsMap(final SortedMap<K, Collection<V>> sortedMap) {
            super(sortedMap);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }
        
        SortedSet<K> createKeySet() {
            return new AbstractMapBasedMultimap.SortedKeySet(this.sortedMap());
        }
        
        @Override
        public K firstKey() {
            return this.sortedMap().firstKey();
        }
        
        @Override
        public SortedMap<K, Collection<V>> headMap(final K k) {
            return new SortedAsMap(this.sortedMap().headMap(k));
        }
        
        @Override
        public SortedSet<K> keySet() {
            SortedSet<K> sortedKeySet = this.sortedKeySet;
            if (sortedKeySet == null) {
                sortedKeySet = this.createKeySet();
                this.sortedKeySet = sortedKeySet;
            }
            return sortedKeySet;
        }
        
        @Override
        public K lastKey() {
            return this.sortedMap().lastKey();
        }
        
        SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap)this.submap;
        }
        
        @Override
        public SortedMap<K, Collection<V>> subMap(final K k, final K i) {
            return new SortedAsMap(this.sortedMap().subMap(k, i));
        }
        
        @Override
        public SortedMap<K, Collection<V>> tailMap(final K k) {
            return new SortedAsMap(this.sortedMap().tailMap(k));
        }
    }
    
    @GwtIncompatible("NavigableSet")
    class NavigableKeySet extends SortedKeySet implements NavigableSet<K>
    {
        NavigableKeySet(final NavigableMap<K, Collection<V>> navigableMap) {
            super(navigableMap);
        }
        
        @Override
        public K ceiling(final K k) {
            return this.sortedMap().ceilingKey(k);
        }
        
        @Override
        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }
        
        @Override
        public NavigableSet<K> descendingSet() {
            return new NavigableKeySet(this.sortedMap().descendingMap());
        }
        
        @Override
        public K floor(final K k) {
            return this.sortedMap().floorKey(k);
        }
        
        @Override
        public NavigableSet<K> headSet(final K k) {
            return this.headSet(k, false);
        }
        
        @Override
        public NavigableSet<K> headSet(final K k, final boolean b) {
            return new NavigableKeySet(this.sortedMap().headMap(k, b));
        }
        
        @Override
        public K higher(final K k) {
            return this.sortedMap().higherKey(k);
        }
        
        @Override
        public K lower(final K k) {
            return this.sortedMap().lowerKey(k);
        }
        
        @Override
        public K pollFirst() {
            return Iterators.pollNext(((KeySet)this).iterator());
        }
        
        @Override
        public K pollLast() {
            return Iterators.pollNext(this.descendingIterator());
        }
        
        NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap)super.sortedMap();
        }
        
        @Override
        public NavigableSet<K> subSet(final K k, final K i) {
            return this.subSet(k, true, i, false);
        }
        
        @Override
        public NavigableSet<K> subSet(final K k, final boolean b, final K i, final boolean b2) {
            return new NavigableKeySet(this.sortedMap().subMap(k, b, i, b2));
        }
        
        @Override
        public NavigableSet<K> tailSet(final K k) {
            return this.tailSet(k, true);
        }
        
        @Override
        public NavigableSet<K> tailSet(final K k, final boolean b) {
            return new NavigableKeySet(this.sortedMap().tailMap(k, b));
        }
    }
    
    private class SortedKeySet extends KeySet implements SortedSet<K>
    {
        SortedKeySet(final SortedMap<K, Collection<V>> sortedMap) {
            super(sortedMap);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }
        
        @Override
        public K first() {
            return this.sortedMap().firstKey();
        }
        
        @Override
        public SortedSet<K> headSet(final K k) {
            return new SortedKeySet(this.sortedMap().headMap(k));
        }
        
        @Override
        public K last() {
            return this.sortedMap().lastKey();
        }
        
        SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap)super.map();
        }
        
        @Override
        public SortedSet<K> subSet(final K k, final K i) {
            return new SortedKeySet(this.sortedMap().subMap(k, i));
        }
        
        @Override
        public SortedSet<K> tailSet(final K k) {
            return new SortedKeySet(this.sortedMap().tailMap(k));
        }
    }
    
    private class RandomAccessWrappedList extends WrappedList implements RandomAccess
    {
        RandomAccessWrappedList(@Nullable final K k, final List<V> list, @Nullable final WrappedCollection collection) {
            super(k, list, collection);
        }
    }
    
    private class WrappedList extends WrappedCollection implements List<V>
    {
        WrappedList(@Nullable final K k, final List<V> list, @Nullable final WrappedCollection collection) {
            super(k, list, collection);
        }
        
        @Override
        public void add(final int n, final V v) {
            ((WrappedCollection)this).refreshIfEmpty();
            final boolean empty = ((WrappedCollection)this).getDelegate().isEmpty();
            this.getListDelegate().add(n, v);
            AbstractMapBasedMultimap.this.totalSize++;
            if (empty) {
                ((WrappedCollection)this).addToMap();
            }
        }
        
        @Override
        public boolean addAll(int size, final Collection<? extends V> collection) {
            if (!collection.isEmpty()) {
                final int size2 = ((WrappedCollection)this).size();
                final boolean addAll = this.getListDelegate().addAll(size, collection);
                if (addAll) {
                    size = ((WrappedCollection)this).getDelegate().size();
                    AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, size - size2);
                    if (size2 == 0) {
                        ((WrappedCollection)this).addToMap();
                    }
                }
                return addAll;
            }
            return false;
        }
        
        @Override
        public V get(final int n) {
            ((WrappedCollection)this).refreshIfEmpty();
            return this.getListDelegate().get(n);
        }
        
        List<V> getListDelegate() {
            return (List)((WrappedCollection)this).getDelegate();
        }
        
        @Override
        public int indexOf(final Object o) {
            ((WrappedCollection)this).refreshIfEmpty();
            return this.getListDelegate().indexOf(o);
        }
        
        @Override
        public int lastIndexOf(final Object o) {
            ((WrappedCollection)this).refreshIfEmpty();
            return this.getListDelegate().lastIndexOf(o);
        }
        
        @Override
        public ListIterator<V> listIterator() {
            ((WrappedCollection)this).refreshIfEmpty();
            return new WrappedListIterator();
        }
        
        @Override
        public ListIterator<V> listIterator(final int n) {
            ((WrappedCollection)this).refreshIfEmpty();
            return new WrappedListIterator(n);
        }
        
        @Override
        public V remove(final int n) {
            ((WrappedCollection)this).refreshIfEmpty();
            final V remove = this.getListDelegate().remove(n);
            AbstractMapBasedMultimap.this.totalSize--;
            ((WrappedCollection)this).removeIfEmpty();
            return remove;
        }
        
        @Override
        public V set(final int n, final V v) {
            ((WrappedCollection)this).refreshIfEmpty();
            return this.getListDelegate().set(n, v);
        }
        
        @Override
        public List<V> subList(final int n, final int n2) {
            ((WrappedCollection)this).refreshIfEmpty();
            final AbstractMapBasedMultimap this$0 = AbstractMapBasedMultimap.this;
            final Object key = ((WrappedCollection)this).getKey();
            final List<V> subList = this.getListDelegate().subList(n, n2);
            AbstractCollection<V> ancestor = this;
            if (((WrappedCollection)this).getAncestor() != null) {
                ancestor = ((WrappedCollection)this).getAncestor();
            }
            return this$0.wrapList(key, subList, (WrappedCollection)ancestor);
        }
        
        private class WrappedListIterator extends WrappedIterator implements ListIterator<V>
        {
            WrappedListIterator() {
                (WrappedCollection)WrappedList.this.super();
            }
            
            public WrappedListIterator(final int n) {
                super(WrappedList.this.getListDelegate().listIterator(n));
            }
            
            private ListIterator<V> getDelegateListIterator() {
                return (ListIterator)((WrappedIterator)this).getDelegateIterator();
            }
            
            @Override
            public void add(final V v) {
                final boolean empty = WrappedList.this.isEmpty();
                this.getDelegateListIterator().add(v);
                AbstractMapBasedMultimap.this.totalSize++;
                if (empty) {
                    ((WrappedCollection)WrappedList.this).addToMap();
                }
            }
            
            @Override
            public boolean hasPrevious() {
                return this.getDelegateListIterator().hasPrevious();
            }
            
            @Override
            public int nextIndex() {
                return this.getDelegateListIterator().nextIndex();
            }
            
            @Override
            public V previous() {
                return this.getDelegateListIterator().previous();
            }
            
            @Override
            public int previousIndex() {
                return this.getDelegateListIterator().previousIndex();
            }
            
            @Override
            public void set(final V v) {
                this.getDelegateListIterator().set(v);
            }
        }
    }
    
    private class WrappedCollection extends AbstractCollection<V>
    {
        final WrappedCollection ancestor;
        final Collection<V> ancestorDelegate;
        Collection<V> delegate;
        final K key;
        
        WrappedCollection(@Nullable final K key, final Collection<V> delegate, @Nullable final WrappedCollection ancestor) {
            final Collection<V> collection = null;
            this.key = key;
            this.delegate = delegate;
            this.ancestor = ancestor;
            Collection<V> delegate2 = collection;
            if (ancestor != null) {
                delegate2 = ancestor.getDelegate();
            }
            this.ancestorDelegate = delegate2;
        }
        
        @Override
        public boolean add(final V v) {
            this.refreshIfEmpty();
            final boolean empty = this.delegate.isEmpty();
            final boolean add = this.delegate.add(v);
            if (add) {
                AbstractMapBasedMultimap.this.totalSize++;
                if (empty) {
                    this.addToMap();
                }
            }
            return add;
        }
        
        @Override
        public boolean addAll(final Collection<? extends V> collection) {
            if (!collection.isEmpty()) {
                final int size = this.size();
                final boolean addAll = this.delegate.addAll(collection);
                if (addAll) {
                    AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                    if (size == 0) {
                        this.addToMap();
                    }
                }
                return addAll;
            }
            return false;
        }
        
        void addToMap() {
            if (this.ancestor == null) {
                AbstractMapBasedMultimap.this.map.put(this.key, this.delegate);
            }
            else {
                this.ancestor.addToMap();
            }
        }
        
        @Override
        public void clear() {
            final int size = this.size();
            if (size != 0) {
                this.delegate.clear();
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, size);
                this.removeIfEmpty();
            }
        }
        
        @Override
        public boolean contains(final Object o) {
            this.refreshIfEmpty();
            return this.delegate.contains(o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            this.refreshIfEmpty();
            return this.delegate.containsAll(collection);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            if (o != this) {
                this.refreshIfEmpty();
                return this.delegate.equals(o);
            }
            return true;
        }
        
        WrappedCollection getAncestor() {
            return this.ancestor;
        }
        
        Collection<V> getDelegate() {
            return this.delegate;
        }
        
        K getKey() {
            return this.key;
        }
        
        @Override
        public int hashCode() {
            this.refreshIfEmpty();
            return this.delegate.hashCode();
        }
        
        @Override
        public Iterator<V> iterator() {
            this.refreshIfEmpty();
            return new WrappedIterator();
        }
        
        void refreshIfEmpty() {
            if (this.ancestor == null) {
                if (this.delegate.isEmpty()) {
                    final Collection delegate = AbstractMapBasedMultimap.this.map.get(this.key);
                    if (delegate != null) {
                        this.delegate = delegate;
                    }
                }
            }
            else {
                this.ancestor.refreshIfEmpty();
                if (this.ancestor.getDelegate() != this.ancestorDelegate) {
                    throw new ConcurrentModificationException();
                }
            }
        }
        
        @Override
        public boolean remove(final Object o) {
            this.refreshIfEmpty();
            final boolean remove = this.delegate.remove(o);
            if (remove) {
                AbstractMapBasedMultimap.this.totalSize--;
                this.removeIfEmpty();
            }
            return remove;
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            if (!collection.isEmpty()) {
                final int size = this.size();
                final boolean removeAll = this.delegate.removeAll(collection);
                if (removeAll) {
                    AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                    this.removeIfEmpty();
                }
                return removeAll;
            }
            return false;
        }
        
        void removeIfEmpty() {
            if (this.ancestor == null) {
                if (this.delegate.isEmpty()) {
                    AbstractMapBasedMultimap.this.map.remove(this.key);
                }
            }
            else {
                this.ancestor.removeIfEmpty();
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            final int size = this.size();
            final boolean retainAll = this.delegate.retainAll(collection);
            if (retainAll) {
                AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                this.removeIfEmpty();
            }
            return retainAll;
        }
        
        @Override
        public int size() {
            this.refreshIfEmpty();
            return this.delegate.size();
        }
        
        @Override
        public String toString() {
            this.refreshIfEmpty();
            return this.delegate.toString();
        }
        
        class WrappedIterator implements Iterator<V>
        {
            final Iterator<V> delegateIterator;
            final Collection<V> originalDelegate;
            
            WrappedIterator() {
                this.originalDelegate = WrappedCollection.this.delegate;
                this.delegateIterator = AbstractMapBasedMultimap.this.iteratorOrListIterator(WrappedCollection.this.delegate);
            }
            
            WrappedIterator(final Iterator<V> delegateIterator) {
                this.originalDelegate = WrappedCollection.this.delegate;
                this.delegateIterator = delegateIterator;
            }
            
            Iterator<V> getDelegateIterator() {
                this.validateIterator();
                return this.delegateIterator;
            }
            
            @Override
            public boolean hasNext() {
                this.validateIterator();
                return this.delegateIterator.hasNext();
            }
            
            @Override
            public V next() {
                this.validateIterator();
                return this.delegateIterator.next();
            }
            
            @Override
            public void remove() {
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.this.totalSize--;
                WrappedCollection.this.removeIfEmpty();
            }
            
            void validateIterator() {
                WrappedCollection.this.refreshIfEmpty();
                if (WrappedCollection.this.delegate == this.originalDelegate) {
                    return;
                }
                throw new ConcurrentModificationException();
            }
        }
    }
    
    @GwtIncompatible("NavigableSet")
    class WrappedNavigableSet extends WrappedSortedSet implements NavigableSet<V>
    {
        WrappedNavigableSet(@Nullable final K k, final NavigableSet<V> set, @Nullable final WrappedCollection collection) {
            super(k, set, collection);
        }
        
        private NavigableSet<V> wrap(final NavigableSet<V> set) {
            final AbstractMapBasedMultimap this$0 = AbstractMapBasedMultimap.this;
            final K key = this.key;
            AbstractCollection<V> ancestor = this;
            if (((WrappedCollection)this).getAncestor() != null) {
                ancestor = ((WrappedCollection)this).getAncestor();
            }
            return new WrappedNavigableSet(key, set, (WrappedCollection)ancestor);
        }
        
        @Override
        public V ceiling(final V v) {
            return this.getSortedSetDelegate().ceiling(v);
        }
        
        @Override
        public Iterator<V> descendingIterator() {
            return new WrappedIterator(this.getSortedSetDelegate().descendingIterator());
        }
        
        @Override
        public NavigableSet<V> descendingSet() {
            return this.wrap(this.getSortedSetDelegate().descendingSet());
        }
        
        @Override
        public V floor(final V v) {
            return this.getSortedSetDelegate().floor(v);
        }
        
        NavigableSet<V> getSortedSetDelegate() {
            return (NavigableSet)super.getSortedSetDelegate();
        }
        
        @Override
        public NavigableSet<V> headSet(final V v, final boolean b) {
            return this.wrap(this.getSortedSetDelegate().headSet(v, b));
        }
        
        @Override
        public V higher(final V v) {
            return this.getSortedSetDelegate().higher(v);
        }
        
        @Override
        public V lower(final V v) {
            return this.getSortedSetDelegate().lower(v);
        }
        
        @Override
        public V pollFirst() {
            return Iterators.pollNext(((WrappedCollection)this).iterator());
        }
        
        @Override
        public V pollLast() {
            return Iterators.pollNext(this.descendingIterator());
        }
        
        @Override
        public NavigableSet<V> subSet(final V v, final boolean b, final V v2, final boolean b2) {
            return this.wrap(this.getSortedSetDelegate().subSet(v, b, v2, b2));
        }
        
        @Override
        public NavigableSet<V> tailSet(final V v, final boolean b) {
            return this.wrap(this.getSortedSetDelegate().tailSet(v, b));
        }
    }
    
    private class WrappedSortedSet extends WrappedCollection implements SortedSet<V>
    {
        WrappedSortedSet(@Nullable final K k, final SortedSet<V> set, @Nullable final WrappedCollection collection) {
            super(k, set, collection);
        }
        
        @Override
        public Comparator<? super V> comparator() {
            return this.getSortedSetDelegate().comparator();
        }
        
        @Override
        public V first() {
            ((WrappedCollection)this).refreshIfEmpty();
            return this.getSortedSetDelegate().first();
        }
        
        SortedSet<V> getSortedSetDelegate() {
            return (SortedSet)((WrappedCollection)this).getDelegate();
        }
        
        @Override
        public SortedSet<V> headSet(final V v) {
            ((WrappedCollection)this).refreshIfEmpty();
            final AbstractMapBasedMultimap this$0 = AbstractMapBasedMultimap.this;
            final K key = ((WrappedCollection)this).getKey();
            final SortedSet<V> headSet = this.getSortedSetDelegate().headSet(v);
            AbstractCollection<V> ancestor = this;
            if (((WrappedCollection)this).getAncestor() != null) {
                ancestor = ((WrappedCollection)this).getAncestor();
            }
            return new WrappedSortedSet(key, headSet, (WrappedCollection)ancestor);
        }
        
        @Override
        public V last() {
            ((WrappedCollection)this).refreshIfEmpty();
            return this.getSortedSetDelegate().last();
        }
        
        @Override
        public SortedSet<V> subSet(final V v, final V v2) {
            ((WrappedCollection)this).refreshIfEmpty();
            final AbstractMapBasedMultimap this$0 = AbstractMapBasedMultimap.this;
            final K key = ((WrappedCollection)this).getKey();
            final SortedSet<V> subSet = this.getSortedSetDelegate().subSet(v, v2);
            AbstractCollection<V> ancestor = this;
            if (((WrappedCollection)this).getAncestor() != null) {
                ancestor = ((WrappedCollection)this).getAncestor();
            }
            return new WrappedSortedSet(key, subSet, (WrappedCollection)ancestor);
        }
        
        @Override
        public SortedSet<V> tailSet(final V v) {
            ((WrappedCollection)this).refreshIfEmpty();
            final AbstractMapBasedMultimap this$0 = AbstractMapBasedMultimap.this;
            final K key = ((WrappedCollection)this).getKey();
            final SortedSet<V> tailSet = this.getSortedSetDelegate().tailSet(v);
            AbstractCollection<V> ancestor = this;
            if (((WrappedCollection)this).getAncestor() != null) {
                ancestor = ((WrappedCollection)this).getAncestor();
            }
            return new WrappedSortedSet(key, tailSet, (WrappedCollection)ancestor);
        }
    }
    
    private class WrappedSet extends WrappedCollection implements Set<V>
    {
        WrappedSet(@Nullable final K k, final Set<V> set) {
            super(k, set, null);
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            if (!collection.isEmpty()) {
                final int size = ((WrappedCollection)this).size();
                final boolean removeAllImpl = Sets.removeAllImpl((Set)this.delegate, collection);
                if (removeAllImpl) {
                    AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                    ((WrappedCollection)this).removeIfEmpty();
                }
                return removeAllImpl;
            }
            return false;
        }
    }
}
