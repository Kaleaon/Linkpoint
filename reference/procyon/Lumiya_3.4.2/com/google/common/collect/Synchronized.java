// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.ListIterator;
import java.util.Iterator;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.SortedMap;
import java.util.Queue;
import java.util.NavigableSet;
import java.util.NavigableMap;
import com.google.common.annotations.VisibleForTesting;
import java.util.RandomAccess;
import com.google.common.annotations.GwtIncompatible;
import java.util.Deque;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.SortedSet;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Synchronized
{
    private Synchronized() {
    }
    
    static <K, V> BiMap<K, V> biMap(final BiMap<K, V> biMap, @Nullable final Object o) {
        if (!(biMap instanceof SynchronizedBiMap) && !(biMap instanceof ImmutableBiMap)) {
            return new SynchronizedBiMap<K, V>((BiMap)biMap, o, (BiMap)null);
        }
        return biMap;
    }
    
    private static <E> Collection<E> collection(final Collection<E> collection, @Nullable final Object o) {
        return new SynchronizedCollection<E>((Collection)collection, o);
    }
    
    @GwtIncompatible("Deque")
    static <E> Deque<E> deque(final Deque<E> deque, @Nullable final Object o) {
        return new SynchronizedDeque<E>(deque, o);
    }
    
    private static <E> List<E> list(final List<E> list, @Nullable final Object o) {
        SynchronizedObject synchronizedObject;
        if (!(list instanceof RandomAccess)) {
            synchronizedObject = new SynchronizedList<Object>(list, o);
        }
        else {
            synchronizedObject = new SynchronizedRandomAccessList<Object>(list, o);
        }
        return (List<E>)synchronizedObject;
    }
    
    static <K, V> ListMultimap<K, V> listMultimap(final ListMultimap<K, V> listMultimap, @Nullable final Object o) {
        if (!(listMultimap instanceof SynchronizedListMultimap) && !(listMultimap instanceof ImmutableListMultimap)) {
            return new SynchronizedListMultimap<K, V>(listMultimap, o);
        }
        return listMultimap;
    }
    
    @VisibleForTesting
    static <K, V> Map<K, V> map(final Map<K, V> map, @Nullable final Object o) {
        return new SynchronizedMap<K, V>(map, o);
    }
    
    static <K, V> Multimap<K, V> multimap(final Multimap<K, V> multimap, @Nullable final Object o) {
        if (!(multimap instanceof SynchronizedMultimap) && !(multimap instanceof ImmutableMultimap)) {
            return new SynchronizedMultimap<K, V>(multimap, o);
        }
        return multimap;
    }
    
    static <E> Multiset<E> multiset(final Multiset<E> multiset, @Nullable final Object o) {
        if (!(multiset instanceof SynchronizedMultiset) && !(multiset instanceof ImmutableMultiset)) {
            return new SynchronizedMultiset<E>(multiset, o);
        }
        return multiset;
    }
    
    @GwtIncompatible("NavigableMap")
    static <K, V> NavigableMap<K, V> navigableMap(final NavigableMap<K, V> navigableMap) {
        return navigableMap(navigableMap, null);
    }
    
    @GwtIncompatible("NavigableMap")
    static <K, V> NavigableMap<K, V> navigableMap(final NavigableMap<K, V> navigableMap, @Nullable final Object o) {
        return new SynchronizedNavigableMap<K, V>(navigableMap, o);
    }
    
    @GwtIncompatible("NavigableSet")
    static <E> NavigableSet<E> navigableSet(final NavigableSet<E> set) {
        return navigableSet(set, null);
    }
    
    @GwtIncompatible("NavigableSet")
    static <E> NavigableSet<E> navigableSet(final NavigableSet<E> set, @Nullable final Object o) {
        return new SynchronizedNavigableSet<E>(set, o);
    }
    
    @GwtIncompatible("works but is needed only for NavigableMap")
    private static <K, V> Map.Entry<K, V> nullableSynchronizedEntry(@Nullable final Map.Entry<K, V> entry, @Nullable final Object o) {
        if (entry != null) {
            return new SynchronizedEntry<K, V>(entry, o);
        }
        return null;
    }
    
    static <E> Queue<E> queue(final Queue<E> queue, @Nullable final Object o) {
        Queue<E> queue2 = queue;
        if (!(queue instanceof SynchronizedQueue)) {
            queue2 = new SynchronizedQueue<E>(queue, o);
        }
        return queue2;
    }
    
    @VisibleForTesting
    static <E> Set<E> set(final Set<E> set, @Nullable final Object o) {
        return new SynchronizedSet<E>(set, o);
    }
    
    static <K, V> SetMultimap<K, V> setMultimap(final SetMultimap<K, V> setMultimap, @Nullable final Object o) {
        if (!(setMultimap instanceof SynchronizedSetMultimap) && !(setMultimap instanceof ImmutableSetMultimap)) {
            return new SynchronizedSetMultimap<K, V>(setMultimap, o);
        }
        return setMultimap;
    }
    
    static <K, V> SortedMap<K, V> sortedMap(final SortedMap<K, V> sortedMap, @Nullable final Object o) {
        return new SynchronizedSortedMap<K, V>(sortedMap, o);
    }
    
    private static <E> SortedSet<E> sortedSet(final SortedSet<E> set, @Nullable final Object o) {
        return new SynchronizedSortedSet<E>(set, o);
    }
    
    static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(final SortedSetMultimap<K, V> sortedSetMultimap, @Nullable final Object o) {
        if (!(sortedSetMultimap instanceof SynchronizedSortedSetMultimap)) {
            return new SynchronizedSortedSetMultimap<K, V>(sortedSetMultimap, o);
        }
        return sortedSetMultimap;
    }
    
    private static <E> Collection<E> typePreservingCollection(final Collection<E> collection, @Nullable final Object o) {
        if (collection instanceof SortedSet) {
            return (Collection<E>)sortedSet((SortedSet<Object>)collection, o);
        }
        if (collection instanceof Set) {
            return (Collection<E>)set((Set<Object>)collection, o);
        }
        if (!(collection instanceof List)) {
            return (Collection<E>)collection((Collection<Object>)collection, o);
        }
        return (Collection<E>)list((List<Object>)collection, o);
    }
    
    private static <E> Set<E> typePreservingSet(final Set<E> set, @Nullable final Object o) {
        if (!(set instanceof SortedSet)) {
            return (Set<E>)set((Set<Object>)set, o);
        }
        return (Set<E>)sortedSet((SortedSet<Object>)set, o);
    }
    
    private static class SynchronizedAsMap<K, V> extends SynchronizedMap<K, Collection<V>>
    {
        private static final long serialVersionUID = 0L;
        transient Set<Entry<K, Collection<V>>> asMapEntrySet;
        transient Collection<Collection<V>> asMapValues;
        
        SynchronizedAsMap(final Map<K, Collection<V>> map, @Nullable final Object o) {
            super(map, o);
        }
        
        @Override
        public boolean containsValue(final Object o) {
            return this.values().contains(o);
        }
        
        @Override
        public Set<Entry<K, Collection<V>>> entrySet() {
            synchronized (this.mutex) {
                if (this.asMapEntrySet == null) {
                    this.asMapEntrySet = (Set<Entry<K, Collection<V>>>)new SynchronizedAsMapEntries((Set<Entry<Object, Collection<Object>>>)this.delegate().entrySet(), this.mutex);
                }
                return this.asMapEntrySet;
            }
        }
        
        @Override
        public Collection<V> get(final Object o) {
            synchronized (this.mutex) {
                final Collection collection = super.get(o);
                Collection access$400;
                if (collection != null) {
                    access$400 = typePreservingCollection((Collection<Object>)collection, this.mutex);
                }
                else {
                    access$400 = null;
                }
                return access$400;
            }
        }
        
        @Override
        public Collection<Collection<V>> values() {
            synchronized (this.mutex) {
                if (this.asMapValues == null) {
                    this.asMapValues = (Collection<Collection<V>>)new SynchronizedAsMapValues((Collection<Collection<Object>>)this.delegate().values(), this.mutex);
                }
                return this.asMapValues;
            }
        }
    }
    
    private static class SynchronizedMap<K, V> extends SynchronizedObject implements Map<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient Set<Entry<K, V>> entrySet;
        transient Set<K> keySet;
        transient Collection<V> values;
        
        SynchronizedMap(final Map<K, V> map, @Nullable final Object o) {
            super(map, o);
        }
        
        @Override
        public void clear() {
            synchronized (this.mutex) {
                this.delegate().clear();
            }
        }
        
        @Override
        public boolean containsKey(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().containsKey(o);
            }
        }
        
        @Override
        public boolean containsValue(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().containsValue(o);
            }
        }
        
        Map<K, V> delegate() {
            return (Map)super.delegate();
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entrySet(), this.mutex);
                }
                return this.entrySet;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public V get(Object value) {
            synchronized (this.mutex) {
                value = this.delegate().get(value);
                return (V)value;
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (this.mutex) {
                return this.delegate().isEmpty();
            }
        }
        
        @Override
        public Set<K> keySet() {
            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = Synchronized.set(this.delegate().keySet(), this.mutex);
                }
                return this.keySet;
            }
        }
        
        @Override
        public V put(final K k, final V v) {
            synchronized (this.mutex) {
                return this.delegate().put(k, v);
            }
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            synchronized (this.mutex) {
                this.delegate().putAll(map);
            }
        }
        
        @Override
        public V remove(Object remove) {
            synchronized (this.mutex) {
                remove = this.delegate().remove(remove);
                return (V)remove;
            }
        }
        
        @Override
        public int size() {
            synchronized (this.mutex) {
                return this.delegate().size();
            }
        }
        
        @Override
        public Collection<V> values() {
            synchronized (this.mutex) {
                if (this.values == null) {
                    this.values = (Collection<V>)collection((Collection<Object>)this.delegate().values(), this.mutex);
                }
                return this.values;
            }
        }
    }
    
    static class SynchronizedObject implements Serializable
    {
        @GwtIncompatible("not needed in emulated source")
        private static final long serialVersionUID = 0L;
        final Object delegate;
        final Object mutex;
        
        SynchronizedObject(Object mutex, @Nullable final Object o) {
            this.delegate = Preconditions.checkNotNull(mutex);
            if (o != null) {
                mutex = o;
            }
            else {
                mutex = this;
            }
            this.mutex = mutex;
        }
        
        @GwtIncompatible("java.io.ObjectOutputStream")
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            synchronized (this.mutex) {
                objectOutputStream.defaultWriteObject();
            }
        }
        
        Object delegate() {
            return this.delegate;
        }
        
        @Override
        public String toString() {
            synchronized (this.mutex) {
                return this.delegate.toString();
            }
        }
    }
    
    private static class SynchronizedAsMapEntries<K, V> extends SynchronizedSet<Map.Entry<K, Collection<V>>>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedAsMapEntries(final Set<Map.Entry<K, Collection<V>>> set, @Nullable final Object o) {
            super(set, o);
        }
        
        @Override
        public boolean contains(final Object o) {
            synchronized (this.mutex) {
                return Maps.containsEntryImpl(this.delegate(), o);
            }
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            synchronized (this.mutex) {
                return Collections2.containsAllImpl(this.delegate(), collection);
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                return Sets.equalsImpl(this.delegate(), o);
            }
        }
        
        @Override
        public Iterator<Map.Entry<K, Collection<V>>> iterator() {
            return new TransformedIterator<Map.Entry<K, Collection<V>>, Map.Entry<K, Collection<V>>>(super.iterator()) {
                @Override
                Map.Entry<K, Collection<V>> transform(final Map.Entry<K, Collection<V>> entry) {
                    return new ForwardingMapEntry<K, Collection<V>>() {
                        @Override
                        protected Entry<K, Collection<V>> delegate() {
                            return entry;
                        }
                        
                        @Override
                        public Collection<V> getValue() {
                            return (Collection<V>)typePreservingCollection((Collection<Object>)entry.getValue(), SynchronizedAsMapEntries.this.mutex);
                        }
                    };
                }
            };
        }
        
        @Override
        public boolean remove(final Object o) {
            synchronized (this.mutex) {
                return Maps.removeEntryImpl(this.delegate(), o);
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            synchronized (this.mutex) {
                return Iterators.removeAll(this.delegate().iterator(), collection);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            synchronized (this.mutex) {
                return Iterators.retainAll(this.delegate().iterator(), collection);
            }
        }
        
        @Override
        public Object[] toArray() {
            synchronized (this.mutex) {
                return ObjectArrays.toArrayImpl(this.delegate());
            }
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            synchronized (this.mutex) {
                return ObjectArrays.toArrayImpl(this.delegate(), array);
            }
        }
    }
    
    static class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSet(final Set<E> set, @Nullable final Object o) {
            super((Collection)set, o);
        }
        
        Set<E> delegate() {
            return (Set)super.delegate();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
    }
    
    @VisibleForTesting
    static class SynchronizedCollection<E> extends SynchronizedObject implements Collection<E>
    {
        private static final long serialVersionUID = 0L;
        
        private SynchronizedCollection(final Collection<E> collection, @Nullable final Object o) {
            super(collection, o);
        }
        
        @Override
        public boolean add(final E e) {
            synchronized (this.mutex) {
                return this.delegate().add(e);
            }
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            synchronized (this.mutex) {
                return this.delegate().addAll(collection);
            }
        }
        
        @Override
        public void clear() {
            synchronized (this.mutex) {
                this.delegate().clear();
            }
        }
        
        @Override
        public boolean contains(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().contains(o);
            }
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            synchronized (this.mutex) {
                return this.delegate().containsAll(collection);
            }
        }
        
        Collection<E> delegate() {
            return (Collection)super.delegate();
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (this.mutex) {
                return this.delegate().isEmpty();
            }
        }
        
        @Override
        public Iterator<E> iterator() {
            return this.delegate().iterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().remove(o);
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            synchronized (this.mutex) {
                return this.delegate().removeAll(collection);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            synchronized (this.mutex) {
                return this.delegate().retainAll(collection);
            }
        }
        
        @Override
        public int size() {
            synchronized (this.mutex) {
                return this.delegate().size();
            }
        }
        
        @Override
        public Object[] toArray() {
            synchronized (this.mutex) {
                return this.delegate().toArray();
            }
        }
        
        @Override
        public <T> T[] toArray(final T[] array) {
            synchronized (this.mutex) {
                return this.delegate().toArray(array);
            }
        }
    }
    
    private static class SynchronizedAsMapValues<V> extends SynchronizedCollection<Collection<V>>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedAsMapValues(final Collection<Collection<V>> collection, @Nullable final Object o) {
            super((Collection)collection, o);
        }
        
        @Override
        public Iterator<Collection<V>> iterator() {
            return new TransformedIterator<Collection<V>, Collection<V>>(super.iterator()) {
                @Override
                Collection<V> transform(final Collection<V> collection) {
                    return (Collection<V>)typePreservingCollection((Collection<Object>)collection, SynchronizedAsMapValues.this.mutex);
                }
            };
        }
    }
    
    @VisibleForTesting
    static class SynchronizedBiMap<K, V> extends SynchronizedMap<K, V> implements BiMap<K, V>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private transient BiMap<V, K> inverse;
        private transient Set<V> valueSet;
        
        private SynchronizedBiMap(final BiMap<K, V> biMap, @Nullable final Object o, @Nullable final BiMap<V, K> inverse) {
            super(biMap, o);
            this.inverse = inverse;
        }
        
        BiMap<K, V> delegate() {
            return (BiMap)super.delegate();
        }
        
        @Override
        public V forcePut(final K k, final V v) {
            synchronized (this.mutex) {
                return this.delegate().forcePut(k, v);
            }
        }
        
        @Override
        public BiMap<V, K> inverse() {
            synchronized (this.mutex) {
                if (this.inverse == null) {
                    this.inverse = (BiMap<V, K>)new SynchronizedBiMap((BiMap<Object, Object>)this.delegate().inverse(), this.mutex, (BiMap<Object, Object>)this);
                }
                return this.inverse;
            }
        }
        
        @Override
        public Set<V> values() {
            synchronized (this.mutex) {
                if (this.valueSet == null) {
                    this.valueSet = Synchronized.set(this.delegate().values(), this.mutex);
                }
                return this.valueSet;
            }
        }
    }
    
    @GwtIncompatible("Deque")
    private static final class SynchronizedDeque<E> extends SynchronizedQueue<E> implements Deque<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedDeque(final Deque<E> deque, @Nullable final Object o) {
            super(deque, o);
        }
        
        @Override
        public void addFirst(final E e) {
            synchronized (this.mutex) {
                this.delegate().addFirst(e);
            }
        }
        
        @Override
        public void addLast(final E e) {
            synchronized (this.mutex) {
                this.delegate().addLast(e);
            }
        }
        
        Deque<E> delegate() {
            return (Deque)super.delegate();
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            synchronized (this.mutex) {
                return this.delegate().descendingIterator();
            }
        }
        
        @Override
        public E getFirst() {
            synchronized (this.mutex) {
                return this.delegate().getFirst();
            }
        }
        
        @Override
        public E getLast() {
            synchronized (this.mutex) {
                return this.delegate().getLast();
            }
        }
        
        @Override
        public boolean offerFirst(final E e) {
            synchronized (this.mutex) {
                return this.delegate().offerFirst(e);
            }
        }
        
        @Override
        public boolean offerLast(final E e) {
            synchronized (this.mutex) {
                return this.delegate().offerLast(e);
            }
        }
        
        @Override
        public E peekFirst() {
            synchronized (this.mutex) {
                return this.delegate().peekFirst();
            }
        }
        
        @Override
        public E peekLast() {
            synchronized (this.mutex) {
                return this.delegate().peekLast();
            }
        }
        
        @Override
        public E pollFirst() {
            synchronized (this.mutex) {
                return this.delegate().pollFirst();
            }
        }
        
        @Override
        public E pollLast() {
            synchronized (this.mutex) {
                return this.delegate().pollLast();
            }
        }
        
        @Override
        public E pop() {
            synchronized (this.mutex) {
                return this.delegate().pop();
            }
        }
        
        @Override
        public void push(final E e) {
            synchronized (this.mutex) {
                this.delegate().push(e);
            }
        }
        
        @Override
        public E removeFirst() {
            synchronized (this.mutex) {
                return this.delegate().removeFirst();
            }
        }
        
        @Override
        public boolean removeFirstOccurrence(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().removeFirstOccurrence(o);
            }
        }
        
        @Override
        public E removeLast() {
            synchronized (this.mutex) {
                return this.delegate().removeLast();
            }
        }
        
        @Override
        public boolean removeLastOccurrence(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().removeLastOccurrence(o);
            }
        }
    }
    
    @GwtIncompatible("works but is needed only for NavigableMap")
    private static class SynchronizedEntry<K, V> extends SynchronizedObject implements Entry<K, V>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedEntry(final Entry<K, V> entry, @Nullable final Object o) {
            super(entry, o);
        }
        
        Entry<K, V> delegate() {
            return (Entry)super.delegate();
        }
        
        @Override
        public boolean equals(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public K getKey() {
            synchronized (this.mutex) {
                return this.delegate().getKey();
            }
        }
        
        @Override
        public V getValue() {
            synchronized (this.mutex) {
                return this.delegate().getValue();
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public V setValue(final V value) {
            synchronized (this.mutex) {
                return this.delegate().setValue(value);
            }
        }
    }
    
    private static class SynchronizedList<E> extends SynchronizedCollection<E> implements List<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedList(final List<E> list, @Nullable final Object o) {
            super((Collection)list, o);
        }
        
        @Override
        public void add(final int n, final E e) {
            synchronized (this.mutex) {
                this.delegate().add(n, e);
            }
        }
        
        @Override
        public boolean addAll(final int n, final Collection<? extends E> collection) {
            synchronized (this.mutex) {
                return this.delegate().addAll(n, collection);
            }
        }
        
        List<E> delegate() {
            return (List)super.delegate();
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public E get(final int n) {
            synchronized (this.mutex) {
                return this.delegate().get(n);
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public int indexOf(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().indexOf(o);
            }
        }
        
        @Override
        public int lastIndexOf(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().lastIndexOf(o);
            }
        }
        
        @Override
        public ListIterator<E> listIterator() {
            return this.delegate().listIterator();
        }
        
        @Override
        public ListIterator<E> listIterator(final int n) {
            return this.delegate().listIterator(n);
        }
        
        @Override
        public E remove(final int n) {
            synchronized (this.mutex) {
                return this.delegate().remove(n);
            }
        }
        
        @Override
        public E set(final int n, final E e) {
            synchronized (this.mutex) {
                return this.delegate().set(n, e);
            }
        }
        
        @Override
        public List<E> subList(final int n, final int n2) {
            synchronized (this.mutex) {
                return (List<E>)list((List<Object>)this.delegate().subList(n, n2), this.mutex);
            }
        }
    }
    
    private static class SynchronizedListMultimap<K, V> extends SynchronizedMultimap<K, V> implements ListMultimap<K, V>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedListMultimap(final ListMultimap<K, V> listMultimap, @Nullable final Object o) {
            super(listMultimap, o);
        }
        
        ListMultimap<K, V> delegate() {
            return (ListMultimap)super.delegate();
        }
        
        @Override
        public List<V> get(final K k) {
            synchronized (this.mutex) {
                return (List<V>)list((List<Object>)this.delegate().get(k), this.mutex);
            }
        }
        
        @Override
        public List<V> removeAll(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().removeAll(o);
            }
        }
        
        @Override
        public List<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            synchronized (this.mutex) {
                return this.delegate().replaceValues(k, iterable);
            }
        }
    }
    
    private static class SynchronizedMultimap<K, V> extends SynchronizedObject implements Multimap<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient Map<K, Collection<V>> asMap;
        transient Collection<Map.Entry<K, V>> entries;
        transient Set<K> keySet;
        transient Multiset<K> keys;
        transient Collection<V> valuesCollection;
        
        SynchronizedMultimap(final Multimap<K, V> multimap, @Nullable final Object o) {
            super(multimap, o);
        }
        
        @Override
        public Map<K, Collection<V>> asMap() {
            synchronized (this.mutex) {
                if (this.asMap == null) {
                    this.asMap = (Map<K, Collection<V>>)new SynchronizedAsMap((Map<Object, Collection<Object>>)this.delegate().asMap(), this.mutex);
                }
                return this.asMap;
            }
        }
        
        @Override
        public void clear() {
            synchronized (this.mutex) {
                this.delegate().clear();
            }
        }
        
        @Override
        public boolean containsEntry(final Object o, final Object o2) {
            synchronized (this.mutex) {
                return this.delegate().containsEntry(o, o2);
            }
        }
        
        @Override
        public boolean containsKey(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().containsKey(o);
            }
        }
        
        @Override
        public boolean containsValue(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().containsValue(o);
            }
        }
        
        Multimap<K, V> delegate() {
            return (Multimap)super.delegate();
        }
        
        @Override
        public Collection<Map.Entry<K, V>> entries() {
            synchronized (this.mutex) {
                if (this.entries == null) {
                    this.entries = (Collection<Map.Entry<K, V>>)typePreservingCollection((Collection<Object>)this.delegate().entries(), this.mutex);
                }
                return this.entries;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public Collection<V> get(final K k) {
            synchronized (this.mutex) {
                return (Collection<V>)typePreservingCollection((Collection<Object>)this.delegate().get(k), this.mutex);
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public boolean isEmpty() {
            synchronized (this.mutex) {
                return this.delegate().isEmpty();
            }
        }
        
        @Override
        public Set<K> keySet() {
            synchronized (this.mutex) {
                if (this.keySet == null) {
                    this.keySet = (Set<K>)typePreservingSet((Set<Object>)this.delegate().keySet(), this.mutex);
                }
                return this.keySet;
            }
        }
        
        @Override
        public Multiset<K> keys() {
            synchronized (this.mutex) {
                if (this.keys == null) {
                    this.keys = Synchronized.multiset(this.delegate().keys(), this.mutex);
                }
                return this.keys;
            }
        }
        
        @Override
        public boolean put(final K k, final V v) {
            synchronized (this.mutex) {
                return this.delegate().put(k, v);
            }
        }
        
        @Override
        public boolean putAll(final Multimap<? extends K, ? extends V> multimap) {
            synchronized (this.mutex) {
                return this.delegate().putAll(multimap);
            }
        }
        
        @Override
        public boolean putAll(final K k, final Iterable<? extends V> iterable) {
            synchronized (this.mutex) {
                return this.delegate().putAll(k, iterable);
            }
        }
        
        @Override
        public boolean remove(final Object o, final Object o2) {
            synchronized (this.mutex) {
                return this.delegate().remove(o, o2);
            }
        }
        
        @Override
        public Collection<V> removeAll(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().removeAll(o);
            }
        }
        
        @Override
        public Collection<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            synchronized (this.mutex) {
                return this.delegate().replaceValues(k, iterable);
            }
        }
        
        @Override
        public int size() {
            synchronized (this.mutex) {
                return this.delegate().size();
            }
        }
        
        @Override
        public Collection<V> values() {
            synchronized (this.mutex) {
                if (this.valuesCollection == null) {
                    this.valuesCollection = (Collection<V>)collection((Collection<Object>)this.delegate().values(), this.mutex);
                }
                return this.valuesCollection;
            }
        }
    }
    
    private static class SynchronizedMultiset<E> extends SynchronizedCollection<E> implements Multiset<E>
    {
        private static final long serialVersionUID = 0L;
        transient Set<E> elementSet;
        transient Set<Entry<E>> entrySet;
        
        SynchronizedMultiset(final Multiset<E> multiset, @Nullable final Object o) {
            super((Collection)multiset, o);
        }
        
        @Override
        public int add(final E e, int add) {
            synchronized (this.mutex) {
                add = this.delegate().add(e, add);
                return add;
            }
        }
        
        @Override
        public int count(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().count(o);
            }
        }
        
        Multiset<E> delegate() {
            return (Multiset)super.delegate();
        }
        
        @Override
        public Set<E> elementSet() {
            synchronized (this.mutex) {
                if (this.elementSet == null) {
                    this.elementSet = (Set<E>)typePreservingSet((Set<Object>)this.delegate().elementSet(), this.mutex);
                }
                return this.elementSet;
            }
        }
        
        @Override
        public Set<Entry<E>> entrySet() {
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = (Set<Entry<E>>)typePreservingSet((Set<Object>)this.delegate().entrySet(), this.mutex);
                }
                return this.entrySet;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            synchronized (this.mutex) {
                return this.delegate().equals(o);
            }
        }
        
        @Override
        public int hashCode() {
            synchronized (this.mutex) {
                return this.delegate().hashCode();
            }
        }
        
        @Override
        public int remove(final Object o, int remove) {
            synchronized (this.mutex) {
                remove = this.delegate().remove(o, remove);
                return remove;
            }
        }
        
        @Override
        public int setCount(final E e, int setCount) {
            synchronized (this.mutex) {
                setCount = this.delegate().setCount(e, setCount);
                return setCount;
            }
        }
        
        @Override
        public boolean setCount(final E e, final int n, final int n2) {
            synchronized (this.mutex) {
                return this.delegate().setCount(e, n, n2);
            }
        }
    }
    
    @GwtIncompatible("NavigableMap")
    @VisibleForTesting
    static class SynchronizedNavigableMap<K, V> extends SynchronizedSortedMap<K, V> implements NavigableMap<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient NavigableSet<K> descendingKeySet;
        transient NavigableMap<K, V> descendingMap;
        transient NavigableSet<K> navigableKeySet;
        
        SynchronizedNavigableMap(final NavigableMap<K, V> navigableMap, @Nullable final Object o) {
            super(navigableMap, o);
        }
        
        @Override
        public Entry<K, V> ceilingEntry(final K k) {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().ceilingEntry(k), this.mutex);
            }
        }
        
        @Override
        public K ceilingKey(final K k) {
            synchronized (this.mutex) {
                return this.delegate().ceilingKey(k);
            }
        }
        
        NavigableMap<K, V> delegate() {
            return (NavigableMap)super.delegate();
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            synchronized (this.mutex) {
                if (this.descendingKeySet != null) {
                    return this.descendingKeySet;
                }
                return this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), this.mutex);
            }
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            synchronized (this.mutex) {
                if (this.descendingMap != null) {
                    return this.descendingMap;
                }
                return this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), this.mutex);
            }
        }
        
        @Override
        public Entry<K, V> firstEntry() {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().firstEntry(), this.mutex);
            }
        }
        
        @Override
        public Entry<K, V> floorEntry(final K k) {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().floorEntry(k), this.mutex);
            }
        }
        
        @Override
        public K floorKey(final K k) {
            synchronized (this.mutex) {
                return this.delegate().floorKey(k);
            }
        }
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            synchronized (this.mutex) {
                return Synchronized.navigableMap(this.delegate().headMap(k, b), this.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public Entry<K, V> higherEntry(final K k) {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().higherEntry(k), this.mutex);
            }
        }
        
        @Override
        public K higherKey(final K k) {
            synchronized (this.mutex) {
                return this.delegate().higherKey(k);
            }
        }
        
        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }
        
        @Override
        public Entry<K, V> lastEntry() {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().lastEntry(), this.mutex);
            }
        }
        
        @Override
        public Entry<K, V> lowerEntry(final K k) {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().lowerEntry(k), this.mutex);
            }
        }
        
        @Override
        public K lowerKey(final K k) {
            synchronized (this.mutex) {
                return this.delegate().lowerKey(k);
            }
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            synchronized (this.mutex) {
                if (this.navigableKeySet != null) {
                    return this.navigableKeySet;
                }
                return this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), this.mutex);
            }
        }
        
        @Override
        public Entry<K, V> pollFirstEntry() {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().pollFirstEntry(), this.mutex);
            }
        }
        
        @Override
        public Entry<K, V> pollLastEntry() {
            synchronized (this.mutex) {
                return (Entry<K, V>)nullableSynchronizedEntry((Entry<Object, Object>)(Entry)this.delegate().pollLastEntry(), this.mutex);
            }
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            synchronized (this.mutex) {
                return Synchronized.navigableMap(this.delegate().subMap(k, b, i, b2), this.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            synchronized (this.mutex) {
                return Synchronized.navigableMap(this.delegate().tailMap(k, b), this.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return this.tailMap(k, true);
        }
    }
    
    static class SynchronizedSortedMap<K, V> extends SynchronizedMap<K, V> implements SortedMap<K, V>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSortedMap(final SortedMap<K, V> sortedMap, @Nullable final Object o) {
            super(sortedMap, o);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            synchronized (this.mutex) {
                return this.delegate().comparator();
            }
        }
        
        SortedMap<K, V> delegate() {
            return (SortedMap)super.delegate();
        }
        
        @Override
        public K firstKey() {
            synchronized (this.mutex) {
                return this.delegate().firstKey();
            }
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            synchronized (this.mutex) {
                return Synchronized.sortedMap(this.delegate().headMap(k), this.mutex);
            }
        }
        
        @Override
        public K lastKey() {
            synchronized (this.mutex) {
                return this.delegate().lastKey();
            }
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            synchronized (this.mutex) {
                return Synchronized.sortedMap(this.delegate().subMap(k, i), this.mutex);
            }
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            synchronized (this.mutex) {
                return Synchronized.sortedMap(this.delegate().tailMap(k), this.mutex);
            }
        }
    }
    
    @GwtIncompatible("NavigableSet")
    @VisibleForTesting
    static class SynchronizedNavigableSet<E> extends SynchronizedSortedSet<E> implements NavigableSet<E>
    {
        private static final long serialVersionUID = 0L;
        transient NavigableSet<E> descendingSet;
        
        SynchronizedNavigableSet(final NavigableSet<E> set, @Nullable final Object o) {
            super(set, o);
        }
        
        @Override
        public E ceiling(final E e) {
            synchronized (this.mutex) {
                return this.delegate().ceiling(e);
            }
        }
        
        NavigableSet<E> delegate() {
            return (NavigableSet)super.delegate();
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            return this.delegate().descendingIterator();
        }
        
        @Override
        public NavigableSet<E> descendingSet() {
            synchronized (this.mutex) {
                if (this.descendingSet != null) {
                    return this.descendingSet;
                }
                return this.descendingSet = Synchronized.navigableSet(this.delegate().descendingSet(), this.mutex);
            }
        }
        
        @Override
        public E floor(final E e) {
            synchronized (this.mutex) {
                return this.delegate().floor(e);
            }
        }
        
        @Override
        public NavigableSet<E> headSet(final E e, final boolean b) {
            synchronized (this.mutex) {
                return Synchronized.navigableSet(this.delegate().headSet(e, b), this.mutex);
            }
        }
        
        @Override
        public SortedSet<E> headSet(final E e) {
            return this.headSet(e, false);
        }
        
        @Override
        public E higher(final E e) {
            synchronized (this.mutex) {
                return this.delegate().higher(e);
            }
        }
        
        @Override
        public E lower(final E e) {
            synchronized (this.mutex) {
                return this.delegate().lower(e);
            }
        }
        
        @Override
        public E pollFirst() {
            synchronized (this.mutex) {
                return this.delegate().pollFirst();
            }
        }
        
        @Override
        public E pollLast() {
            synchronized (this.mutex) {
                return this.delegate().pollLast();
            }
        }
        
        @Override
        public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
            synchronized (this.mutex) {
                return Synchronized.navigableSet(this.delegate().subSet(e, b, e2, b2), this.mutex);
            }
        }
        
        @Override
        public SortedSet<E> subSet(final E e, final E e2) {
            return this.subSet(e, true, e2, false);
        }
        
        @Override
        public NavigableSet<E> tailSet(final E e, final boolean b) {
            synchronized (this.mutex) {
                return Synchronized.navigableSet(this.delegate().tailSet(e, b), this.mutex);
            }
        }
        
        @Override
        public SortedSet<E> tailSet(final E e) {
            return this.tailSet(e, true);
        }
    }
    
    static class SynchronizedSortedSet<E> extends SynchronizedSet<E> implements SortedSet<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSortedSet(final SortedSet<E> set, @Nullable final Object o) {
            super(set, o);
        }
        
        @Override
        public Comparator<? super E> comparator() {
            synchronized (this.mutex) {
                return this.delegate().comparator();
            }
        }
        
        SortedSet<E> delegate() {
            return (SortedSet)super.delegate();
        }
        
        @Override
        public E first() {
            synchronized (this.mutex) {
                return this.delegate().first();
            }
        }
        
        @Override
        public SortedSet<E> headSet(final E e) {
            synchronized (this.mutex) {
                return (SortedSet<E>)sortedSet((SortedSet<Object>)this.delegate().headSet(e), this.mutex);
            }
        }
        
        @Override
        public E last() {
            synchronized (this.mutex) {
                return this.delegate().last();
            }
        }
        
        @Override
        public SortedSet<E> subSet(final E e, final E e2) {
            synchronized (this.mutex) {
                return (SortedSet<E>)sortedSet((SortedSet<Object>)this.delegate().subSet(e, e2), this.mutex);
            }
        }
        
        @Override
        public SortedSet<E> tailSet(final E e) {
            synchronized (this.mutex) {
                return (SortedSet<E>)sortedSet((SortedSet<Object>)this.delegate().tailSet(e), this.mutex);
            }
        }
    }
    
    private static class SynchronizedQueue<E> extends SynchronizedCollection<E> implements Queue<E>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedQueue(final Queue<E> queue, @Nullable final Object o) {
            super((Collection)queue, o);
        }
        
        Queue<E> delegate() {
            return (Queue)super.delegate();
        }
        
        @Override
        public E element() {
            synchronized (this.mutex) {
                return this.delegate().element();
            }
        }
        
        @Override
        public boolean offer(final E e) {
            synchronized (this.mutex) {
                return this.delegate().offer(e);
            }
        }
        
        @Override
        public E peek() {
            synchronized (this.mutex) {
                return this.delegate().peek();
            }
        }
        
        @Override
        public E poll() {
            synchronized (this.mutex) {
                return this.delegate().poll();
            }
        }
        
        @Override
        public E remove() {
            synchronized (this.mutex) {
                return this.delegate().remove();
            }
        }
    }
    
    private static class SynchronizedRandomAccessList<E> extends SynchronizedList<E> implements RandomAccess
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedRandomAccessList(final List<E> list, @Nullable final Object o) {
            super(list, o);
        }
    }
    
    private static class SynchronizedSetMultimap<K, V> extends SynchronizedMultimap<K, V> implements SetMultimap<K, V>
    {
        private static final long serialVersionUID = 0L;
        transient Set<Map.Entry<K, V>> entrySet;
        
        SynchronizedSetMultimap(final SetMultimap<K, V> setMultimap, @Nullable final Object o) {
            super(setMultimap, o);
        }
        
        SetMultimap<K, V> delegate() {
            return (SetMultimap)super.delegate();
        }
        
        @Override
        public Set<Map.Entry<K, V>> entries() {
            synchronized (this.mutex) {
                if (this.entrySet == null) {
                    this.entrySet = Synchronized.set(this.delegate().entries(), this.mutex);
                }
                return this.entrySet;
            }
        }
        
        @Override
        public Set<V> get(final K k) {
            synchronized (this.mutex) {
                return Synchronized.set(this.delegate().get(k), this.mutex);
            }
        }
        
        @Override
        public Set<V> removeAll(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().removeAll(o);
            }
        }
        
        @Override
        public Set<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            synchronized (this.mutex) {
                return this.delegate().replaceValues(k, iterable);
            }
        }
    }
    
    private static class SynchronizedSortedSetMultimap<K, V> extends SynchronizedSetMultimap<K, V> implements SortedSetMultimap<K, V>
    {
        private static final long serialVersionUID = 0L;
        
        SynchronizedSortedSetMultimap(final SortedSetMultimap<K, V> sortedSetMultimap, @Nullable final Object o) {
            super(sortedSetMultimap, o);
        }
        
        SortedSetMultimap<K, V> delegate() {
            return (SortedSetMultimap)super.delegate();
        }
        
        @Override
        public SortedSet<V> get(final K k) {
            synchronized (this.mutex) {
                return (SortedSet<V>)sortedSet((SortedSet<Object>)this.delegate().get(k), this.mutex);
            }
        }
        
        @Override
        public SortedSet<V> removeAll(final Object o) {
            synchronized (this.mutex) {
                return this.delegate().removeAll(o);
            }
        }
        
        @Override
        public SortedSet<V> replaceValues(final K k, final Iterable<? extends V> iterable) {
            synchronized (this.mutex) {
                return this.delegate().replaceValues(k, iterable);
            }
        }
        
        @Override
        public Comparator<? super V> valueComparator() {
            synchronized (this.mutex) {
                return this.delegate().valueComparator();
            }
        }
    }
}
