// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.NavigableSet;
import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedMap;
import com.google.common.base.Preconditions;
import com.google.common.annotations.Beta;
import java.util.Map;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.util.NavigableMap;

@GwtCompatible(emulated = true, serializable = true)
public final class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V> implements NavigableMap<K, V>
{
    private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP;
    private static final Comparator<Comparable> NATURAL_ORDER;
    private static final long serialVersionUID = 0L;
    private transient ImmutableSortedMap<K, V> descendingMap;
    private final transient RegularImmutableSortedSet<K> keySet;
    private final transient ImmutableList<V> valueList;
    
    static {
        NATURAL_ORDER = Ordering.natural();
        NATURAL_EMPTY_MAP = new ImmutableSortedMap<Comparable, Object>((RegularImmutableSortedSet<Comparable>)ImmutableSortedSet.emptySet((Comparator<? super Comparable>)Ordering.natural()), ImmutableList.of());
    }
    
    ImmutableSortedMap(final RegularImmutableSortedSet<K> set, final ImmutableList<V> list) {
        this(set, list, null);
    }
    
    ImmutableSortedMap(final RegularImmutableSortedSet<K> keySet, final ImmutableList<V> valueList, final ImmutableSortedMap<K, V> descendingMap) {
        this.keySet = keySet;
        this.valueList = valueList;
        this.descendingMap = descendingMap;
    }
    
    @Beta
    public static <K, V> ImmutableSortedMap<K, V> copyOf(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
        return copyOf(iterable, (Comparator<? super K>)(Ordering)ImmutableSortedMap.NATURAL_ORDER);
    }
    
    @Beta
    public static <K, V> ImmutableSortedMap<K, V> copyOf(final Iterable<? extends Entry<? extends K, ? extends V>> iterable, final Comparator<? super K> comparator) {
        return fromEntries((Comparator<? super K>)Preconditions.checkNotNull((Comparator<? super K>)comparator), false, iterable);
    }
    
    public static <K, V> ImmutableSortedMap<K, V> copyOf(final Map<? extends K, ? extends V> map) {
        return copyOfInternal(map, (Comparator<? super K>)(Ordering)ImmutableSortedMap.NATURAL_ORDER);
    }
    
    public static <K, V> ImmutableSortedMap<K, V> copyOf(final Map<? extends K, ? extends V> map, final Comparator<? super K> comparator) {
        return (ImmutableSortedMap<K, V>)copyOfInternal((Map<?, ?>)map, (Comparator<? super Object>)Preconditions.checkNotNull(comparator));
    }
    
    private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(final Map<? extends K, ? extends V> map, final Comparator<? super K> comparator) {
        boolean equals = false;
        if (map instanceof SortedMap) {
            final Comparator comparator2 = ((SortedMap)map).comparator();
            if (comparator2 != null) {
                equals = comparator.equals(comparator2);
            }
            else {
                equals = (comparator == ImmutableSortedMap.NATURAL_ORDER);
            }
        }
        if (equals && map instanceof ImmutableSortedMap) {
            final ImmutableSortedMap immutableSortedMap = (ImmutableSortedMap)map;
            if (!immutableSortedMap.isPartialView()) {
                return immutableSortedMap;
            }
        }
        return (ImmutableSortedMap<K, V>)fromEntries((Comparator<? super Object>)comparator, equals, (Iterable<? extends Entry<?, ?>>)map.entrySet());
    }
    
    public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(final SortedMap<K, ? extends V> sortedMap) {
        Comparator<Comparable> comparator = sortedMap.comparator();
        if (comparator == null) {
            comparator = ImmutableSortedMap.NATURAL_ORDER;
        }
        if (sortedMap instanceof ImmutableSortedMap) {
            final ImmutableSortedMap immutableSortedMap = (ImmutableSortedMap)sortedMap;
            if (!immutableSortedMap.isPartialView()) {
                return immutableSortedMap;
            }
        }
        return (ImmutableSortedMap<K, V>)fromEntries((Comparator<? super Object>)comparator, true, (Iterable<? extends Entry<?, ?>>)sortedMap.entrySet());
    }
    
    static <K, V> ImmutableSortedMap<K, V> emptyMap(final Comparator<? super K> obj) {
        if (!Ordering.natural().equals(obj)) {
            return new ImmutableSortedMap<K, V>(ImmutableSortedSet.emptySet(obj), ImmutableList.of());
        }
        return of();
    }
    
    private static <K, V> ImmutableSortedMap<K, V> fromEntries(final Comparator<? super K> comparator, final boolean b, final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
        final Entry[] array = (Entry[])Iterables.toArray((Iterable<? extends Map.Entry>)iterable, (Map.Entry[])ImmutableSortedMap.EMPTY_ENTRY_ARRAY);
        return fromEntries(comparator, b, (Entry<K, V>[])array, array.length);
    }
    
    private static <K, V> ImmutableSortedMap<K, V> fromEntries(final Comparator<? super K> comparator, final boolean b, final Entry<K, V>[] a, final int toIndex) {
        int i = 0;
        switch (toIndex) {
            default: {
                final Object[] array = new Object[toIndex];
                final Object[] array2 = new Object[toIndex];
                if (!b) {
                    Arrays.sort(a, 0, toIndex, (Comparator<? super Entry<K, V>>)Ordering.from(comparator).onKeys());
                    K key = a[0].getKey();
                    array[0] = key;
                    array2[0] = a[0].getValue();
                    K key2;
                    for (int j = 1; j < toIndex; ++j, key = key2) {
                        key2 = a[j].getKey();
                        final V value = a[j].getValue();
                        CollectPreconditions.checkEntryNotNull(key2, value);
                        array[j] = key2;
                        array2[j] = value;
                        ImmutableMap.checkNoConflict(comparator.compare(key, key2) != 0, "key", a[j - 1], a[j]);
                    }
                }
                else {
                    while (i < toIndex) {
                        final K key3 = a[i].getKey();
                        final V value2 = a[i].getValue();
                        CollectPreconditions.checkEntryNotNull(key3, value2);
                        array[i] = key3;
                        array2[i] = value2;
                        ++i;
                    }
                }
                return new ImmutableSortedMap<K, V>(new RegularImmutableSortedSet<Object>(new RegularImmutableList<Object>(array), (Comparator<? super Object>)comparator), new RegularImmutableList<Object>(array2));
            }
            case 0: {
                return emptyMap(comparator);
            }
            case 1: {
                return of(comparator, a[0].getKey(), a[0].getValue());
            }
        }
    }
    
    private ImmutableSortedMap<K, V> getSubMap(final int n, final int n2) {
        if (n == 0 && n2 == this.size()) {
            return this;
        }
        if (n != n2) {
            return new ImmutableSortedMap<K, V>(this.keySet.getSubSet(n, n2), this.valueList.subList(n, n2));
        }
        return emptyMap(this.comparator());
    }
    
    public static <K extends Comparable<?>, V> Builder<K, V> naturalOrder() {
        return new Builder<K, V>(Ordering.natural());
    }
    
    public static <K, V> ImmutableSortedMap<K, V> of() {
        return (ImmutableSortedMap<K, V>)ImmutableSortedMap.NATURAL_EMPTY_MAP;
    }
    
    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(final K k, final V v) {
        return of(Ordering.natural(), k, v);
    }
    
    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2) {
        return ofEntries((ImmutableMapEntry<K, V>[])new ImmutableMapEntry[] { ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2) });
    }
    
    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        return ofEntries((ImmutableMapEntry<K, V>[])new ImmutableMapEntry[] { ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2), ImmutableMap.entryOf(j, v3) });
    }
    
    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        return ofEntries((ImmutableMapEntry<K, V>[])new ImmutableMapEntry[] { ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2), ImmutableMap.entryOf(j, v3), ImmutableMap.entryOf(l, v4) });
    }
    
    public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
        return ofEntries((ImmutableMapEntry<K, V>[])new ImmutableMapEntry[] { ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2), ImmutableMap.entryOf(j, v3), ImmutableMap.entryOf(l, v4), ImmutableMap.entryOf(m, v5) });
    }
    
    private static <K, V> ImmutableSortedMap<K, V> of(final Comparator<? super K> comparator, final K k, final V v) {
        return new ImmutableSortedMap<K, V>(new RegularImmutableSortedSet<K>(ImmutableList.of(k), Preconditions.checkNotNull(comparator)), ImmutableList.of(v));
    }
    
    private static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> ofEntries(final ImmutableMapEntry<K, V>... array) {
        return fromEntries(Ordering.natural(), false, array, array.length);
    }
    
    public static <K, V> Builder<K, V> orderedBy(final Comparator<K> comparator) {
        return new Builder<K, V>(comparator);
    }
    
    public static <K extends Comparable<?>, V> Builder<K, V> reverseOrder() {
        return new Builder<K, V>(Ordering.natural().reverse());
    }
    
    @Override
    public Entry<K, V> ceilingEntry(final K k) {
        return this.tailMap(k, true).firstEntry();
    }
    
    @Override
    public K ceilingKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.ceilingEntry((K)k));
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.keySet().comparator();
    }
    
    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        Serializable of;
        if (!this.isEmpty()) {
            of = new EntrySet();
        }
        else {
            of = ImmutableSet.of();
        }
        return (ImmutableSet<Entry<K, V>>)of;
    }
    
    @Override
    public ImmutableSortedSet<K> descendingKeySet() {
        return this.keySet.descendingSet();
    }
    
    @Override
    public ImmutableSortedMap<K, V> descendingMap() {
        final ImmutableSortedMap<K, V> descendingMap = this.descendingMap;
        if (descendingMap != null) {
            return descendingMap;
        }
        if (!this.isEmpty()) {
            return new ImmutableSortedMap<K, V>((RegularImmutableSortedSet)this.keySet.descendingSet(), this.valueList.reverse(), this);
        }
        return emptyMap((Comparator<? super K>)Ordering.from(this.comparator()).reverse());
    }
    
    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        return super.entrySet();
    }
    
    @Override
    public Entry<K, V> firstEntry() {
        Entry entry;
        if (!this.isEmpty()) {
            entry = (Entry)this.entrySet().asList().get(0);
        }
        else {
            entry = null;
        }
        return entry;
    }
    
    @Override
    public K firstKey() {
        return this.keySet().first();
    }
    
    @Override
    public Entry<K, V> floorEntry(final K k) {
        return this.headMap(k, true).lastEntry();
    }
    
    @Override
    public K floorKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.floorEntry((K)k));
    }
    
    @Override
    public V get(@Nullable Object value) {
        final int index = this.keySet.indexOf(value);
        if (index != -1) {
            value = this.valueList.get(index);
        }
        else {
            value = null;
        }
        return (V)value;
    }
    
    @Override
    public ImmutableSortedMap<K, V> headMap(final K k) {
        return this.headMap(k, false);
    }
    
    @Override
    public ImmutableSortedMap<K, V> headMap(final K k, final boolean b) {
        return this.getSubMap(0, this.keySet.headIndex(Preconditions.checkNotNull(k), b));
    }
    
    @Override
    public Entry<K, V> higherEntry(final K k) {
        return this.tailMap(k, false).firstEntry();
    }
    
    @Override
    public K higherKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.higherEntry((K)k));
    }
    
    @Override
    boolean isPartialView() {
        boolean b = false;
        if (this.keySet.isPartialView() || this.valueList.isPartialView()) {
            b = true;
        }
        return b;
    }
    
    @Override
    public ImmutableSortedSet<K> keySet() {
        return this.keySet;
    }
    
    @Override
    public Entry<K, V> lastEntry() {
        Entry entry;
        if (!this.isEmpty()) {
            entry = (Entry)this.entrySet().asList().get(this.size() - 1);
        }
        else {
            entry = null;
        }
        return entry;
    }
    
    @Override
    public K lastKey() {
        return this.keySet().last();
    }
    
    @Override
    public Entry<K, V> lowerEntry(final K k) {
        return this.headMap(k, false).lastEntry();
    }
    
    @Override
    public K lowerKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.lowerEntry((K)k));
    }
    
    @Override
    public ImmutableSortedSet<K> navigableKeySet() {
        return this.keySet;
    }
    
    @Deprecated
    @Override
    public final Entry<K, V> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final Entry<K, V> pollLastEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.valueList.size();
    }
    
    @Override
    public ImmutableSortedMap<K, V> subMap(final K k, final K i) {
        return this.subMap(k, true, i, false);
    }
    
    @Override
    public ImmutableSortedMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(i);
        Preconditions.checkArgument(this.comparator().compare((Object)k, (Object)i) <= 0, "expected fromKey <= toKey but %s > %s", k, i);
        return this.headMap(i, b2).tailMap(k, b);
    }
    
    @Override
    public ImmutableSortedMap<K, V> tailMap(final K k) {
        return this.tailMap(k, true);
    }
    
    @Override
    public ImmutableSortedMap<K, V> tailMap(final K k, final boolean b) {
        return this.getSubMap(this.keySet.tailIndex(Preconditions.checkNotNull(k), b), this.size());
    }
    
    @Override
    public ImmutableCollection<V> values() {
        return this.valueList;
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    class EntrySet extends ImmutableMapEntrySet<K, V>
    {
        @Override
        ImmutableList<Entry<K, V>> createAsList() {
            return new ImmutableAsList<Entry<K, V>>() {
                @Override
                ImmutableCollection<Entry<K, V>> delegateCollection() {
                    return (ImmutableCollection<Entry<K, V>>)EntrySet.this;
                }
                
                @Override
                public Entry<K, V> get(final int n) {
                    return Maps.immutableEntry(ImmutableSortedMap.this.keySet.asList().get(n), ImmutableSortedMap.this.valueList.get(n));
                }
            };
        }
        
        @Override
        public UnmodifiableIterator<Entry<K, V>> iterator() {
            return (UnmodifiableIterator<Entry<K, V>>)this.asList().iterator();
        }
        
        @Override
        ImmutableMap<K, V> map() {
            return ImmutableSortedMap.this;
        }
    }
    
    public static class Builder<K, V> extends ImmutableMap.Builder<K, V>
    {
        private final Comparator<? super K> comparator;
        
        public Builder(final Comparator<? super K> comparator) {
            this.comparator = Preconditions.checkNotNull(comparator);
        }
        
        public ImmutableSortedMap<K, V> build() {
            switch (this.size) {
                default: {
                    return (ImmutableSortedMap<K, V>)fromEntries(this.comparator, false, (Entry<Object, Object>[])this.entries, this.size);
                }
                case 0: {
                    return ImmutableSortedMap.emptyMap(this.comparator);
                }
                case 1: {
                    return (ImmutableSortedMap<K, V>)of(this.comparator, this.entries[0].getKey(), this.entries[0].getValue());
                }
            }
        }
        
        @Deprecated
        @Beta
        public Builder<K, V> orderEntriesByValue(final Comparator<? super V> comparator) {
            throw new UnsupportedOperationException("Not available on ImmutableSortedMap.Builder");
        }
        
        public Builder<K, V> put(final K k, final V v) {
            super.put(k, v);
            return this;
        }
        
        public Builder<K, V> put(final Entry<? extends K, ? extends V> entry) {
            super.put(entry);
            return this;
        }
        
        @Beta
        public Builder<K, V> putAll(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
            super.putAll(iterable);
            return this;
        }
        
        public Builder<K, V> putAll(final Map<? extends K, ? extends V> map) {
            super.putAll(map);
            return this;
        }
    }
    
    private static class SerializedForm extends ImmutableMap.SerializedForm
    {
        private static final long serialVersionUID = 0L;
        private final Comparator<Object> comparator;
        
        SerializedForm(final ImmutableSortedMap<?, ?> immutableSortedMap) {
            super(immutableSortedMap);
            this.comparator = (Comparator<Object>)immutableSortedMap.comparator();
        }
        
        @Override
        Object readResolve() {
            return ((ImmutableMap.SerializedForm)this).createMap(new ImmutableSortedMap.Builder<Object, Object>(this.comparator));
        }
    }
}
