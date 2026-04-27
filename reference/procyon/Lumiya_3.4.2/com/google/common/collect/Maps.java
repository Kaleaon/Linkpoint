// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import com.google.j2objc.annotations.Weak;
import java.util.HashSet;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.Properties;
import com.google.common.base.Predicates;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Predicate;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.LinkedHashMap;
import com.google.common.base.Equivalence;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.SortedMap;
import com.google.common.annotations.GwtIncompatible;
import java.util.NavigableMap;
import com.google.common.base.Preconditions;
import com.google.common.base.Function;
import com.google.common.annotations.Beta;
import com.google.common.base.Converter;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Set;
import java.util.Map;
import com.google.common.base.Joiner;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Maps
{
    static final Joiner.MapJoiner STANDARD_JOINER;
    
    static {
        STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");
    }
    
    private Maps() {
    }
    
    @Beta
    public static <A, B> Converter<A, B> asConverter(final BiMap<A, B> biMap) {
        return new BiMapConverter<A, B>(biMap);
    }
    
    static <K, V1, V2> Function<Map.Entry<K, V1>, Map.Entry<K, V2>> asEntryToEntryFunction(final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        Preconditions.checkNotNull(entryTransformer);
        return new Function<Map.Entry<K, V1>, Map.Entry<K, V2>>() {
            @Override
            public Map.Entry<K, V2> apply(final Map.Entry<K, V1> entry) {
                return Maps.transformEntry(entryTransformer, entry);
            }
        };
    }
    
    static <K, V1, V2> Function<Map.Entry<K, V1>, V2> asEntryToValueFunction(final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        Preconditions.checkNotNull(entryTransformer);
        return new Function<Map.Entry<K, V1>, V2>() {
            @Override
            public V2 apply(final Map.Entry<K, V1> entry) {
                return entryTransformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }
    
    static <K, V1, V2> EntryTransformer<K, V1, V2> asEntryTransformer(final Function<? super V1, V2> function) {
        Preconditions.checkNotNull(function);
        return (EntryTransformer<K, V1, V2>)new EntryTransformer<K, V1, V2>() {
            @Override
            public V2 transformEntry(final K k, final V1 v1) {
                return function.apply(v1);
            }
        };
    }
    
    public static <K, V> Map<K, V> asMap(final Set<K> set, final Function<? super K, V> function) {
        if (!(set instanceof SortedSet)) {
            return new AsMapView<K, V>((Set<Object>)set, (Function<? super Object, Object>)function);
        }
        return (Map<K, V>)asMap((SortedSet<Object>)set, (Function<? super Object, V>)function);
    }
    
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> asMap(final NavigableSet<K> set, final Function<? super K, V> function) {
        return new NavigableAsMapView<K, V>(set, function);
    }
    
    public static <K, V> SortedMap<K, V> asMap(final SortedSet<K> set, final Function<? super K, V> function) {
        return Platform.mapsAsMapSortedSet(set, function);
    }
    
    static <K, V> Iterator<Map.Entry<K, V>> asMapEntryIterator(final Set<K> set, final Function<? super K, V> function) {
        return new TransformedIterator<K, Map.Entry<K, V>>(set.iterator()) {
            @Override
            Map.Entry<K, V> transform(final K k) {
                return Maps.immutableEntry(k, function.apply(k));
            }
        };
    }
    
    static <K, V> SortedMap<K, V> asMapSortedIgnoreNavigable(final SortedSet<K> set, final Function<? super K, V> function) {
        return new SortedAsMapView<K, V>(set, function);
    }
    
    static <K, V1, V2> Function<V1, V2> asValueToValueFunction(final EntryTransformer<? super K, V1, V2> entryTransformer, final K k) {
        Preconditions.checkNotNull(entryTransformer);
        return new Function<V1, V2>() {
            @Override
            public V2 apply(@Nullable final V1 v1) {
                return entryTransformer.transformEntry(k, v1);
            }
        };
    }
    
    static int capacity(final int n) {
        if (n < 3) {
            CollectPreconditions.checkNonnegative(n, "expectedSize");
            return n + 1;
        }
        if (n >= 1073741824) {
            return Integer.MAX_VALUE;
        }
        return (int)(n / 0.75f + 1.0f);
    }
    
    static <K, V> boolean containsEntryImpl(final Collection<Map.Entry<K, V>> collection, final Object o) {
        return o instanceof Map.Entry && collection.contains(unmodifiableEntry((Map.Entry<?, ?>)o));
    }
    
    static boolean containsKeyImpl(final Map<?, ?> map, @Nullable final Object o) {
        return Iterators.contains(keyIterator(map.entrySet().iterator()), o);
    }
    
    static boolean containsValueImpl(final Map<?, ?> map, @Nullable final Object o) {
        return Iterators.contains(valueIterator(map.entrySet().iterator()), o);
    }
    
    public static <K, V> MapDifference<K, V> difference(final Map<? extends K, ? extends V> map, final Map<? extends K, ? extends V> map2) {
        if (!(map instanceof SortedMap)) {
            return difference(map, map2, (Equivalence<? super V>)Equivalence.equals());
        }
        return (MapDifference<K, V>)difference((SortedMap<Object, ?>)map, (Map<?, ?>)map2);
    }
    
    @Beta
    public static <K, V> MapDifference<K, V> difference(final Map<? extends K, ? extends V> map, final Map<? extends K, ? extends V> m, final Equivalence<? super V> equivalence) {
        Preconditions.checkNotNull(equivalence);
        final LinkedHashMap<Object, Object> linkedHashMap = newLinkedHashMap();
        final LinkedHashMap linkedHashMap2 = new LinkedHashMap((Map<? extends K, ? extends V>)m);
        final LinkedHashMap<Object, Object> linkedHashMap3 = newLinkedHashMap();
        final LinkedHashMap<Object, Object> linkedHashMap4 = newLinkedHashMap();
        doDifference(map, m, (Equivalence<? super Object>)equivalence, linkedHashMap, linkedHashMap2, linkedHashMap3, (Map<Object, MapDifference.ValueDifference<Object>>)linkedHashMap4);
        return new MapDifferenceImpl<K, V>(linkedHashMap, linkedHashMap2, linkedHashMap3, (Map<Object, MapDifference.ValueDifference<Object>>)linkedHashMap4);
    }
    
    public static <K, V> SortedMapDifference<K, V> difference(final SortedMap<K, ? extends V> sortedMap, final Map<? extends K, ? extends V> map) {
        Preconditions.checkNotNull(sortedMap);
        Preconditions.checkNotNull(map);
        final Comparator<? super Object> orNaturalOrder = orNaturalOrder(sortedMap.comparator());
        final TreeMap<Object, Object> treeMap = newTreeMap(orNaturalOrder);
        final TreeMap<Object, Object> treeMap2 = newTreeMap(orNaturalOrder);
        treeMap2.putAll(map);
        final TreeMap<Object, Object> treeMap3 = newTreeMap(orNaturalOrder);
        final TreeMap<Object, Object> treeMap4 = newTreeMap(orNaturalOrder);
        doDifference(sortedMap, map, Equivalence.equals(), treeMap, treeMap2, treeMap3, (Map<Object, MapDifference.ValueDifference<Object>>)treeMap4);
        return new SortedMapDifferenceImpl<K, V>(treeMap, treeMap2, treeMap3, (SortedMap<Object, MapDifference.ValueDifference<Object>>)treeMap4);
    }
    
    private static <K, V> void doDifference(final Map<? extends K, ? extends V> map, final Map<? extends K, ? extends V> map2, final Equivalence<? super V> equivalence, final Map<K, V> map3, final Map<K, V> map4, final Map<K, V> map5, final Map<K, MapDifference.ValueDifference<V>> map6) {
        for (final Map.Entry<Object, V> entry : map.entrySet()) {
            final Object key = entry.getKey();
            final V value = entry.getValue();
            if (!map2.containsKey(key)) {
                map3.put((K)key, (V)value);
            }
            else {
                final V remove = map4.remove(key);
                if (!equivalence.equivalent((V)value, remove)) {
                    map6.put((K)key, ValueDifferenceImpl.create(value, remove));
                }
                else {
                    map5.put((K)key, (V)value);
                }
            }
        }
    }
    
    static boolean equalsImpl(final Map<?, ?> map, final Object o) {
        return map == o || (o instanceof Map && map.entrySet().equals(((Map)o).entrySet()));
    }
    
    @CheckReturnValue
    public static <K, V> BiMap<K, V> filterEntries(final BiMap<K, V> biMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        Preconditions.checkNotNull(biMap);
        Preconditions.checkNotNull(predicate);
        BiMap<Object, Object> filterFiltered;
        if (!(biMap instanceof FilteredEntryBiMap)) {
            filterFiltered = new FilteredEntryBiMap<Object, Object>((BiMap<Object, Object>)biMap, (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        else {
            filterFiltered = filterFiltered((FilteredEntryBiMap<Object, Object>)biMap, (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        return (BiMap<K, V>)filterFiltered;
    }
    
    @CheckReturnValue
    public static <K, V> Map<K, V> filterEntries(final Map<K, V> map, final Predicate<? super Map.Entry<K, V>> predicate) {
        if (map instanceof SortedMap) {
            return (Map<K, V>)filterEntries((SortedMap<Object, Object>)map, (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        if (!(map instanceof BiMap)) {
            Preconditions.checkNotNull(predicate);
            Map<Object, Object> filterFiltered;
            if (!(map instanceof AbstractFilteredMap)) {
                filterFiltered = new FilteredEntryMap<Object, Object>((Map<Object, Object>)Preconditions.checkNotNull(map), (Predicate<? super Map.Entry<Object, Object>>)predicate);
            }
            else {
                filterFiltered = filterFiltered((AbstractFilteredMap<Object, Object>)map, (Predicate<? super Map.Entry<Object, Object>>)predicate);
            }
            return (Map<K, V>)filterFiltered;
        }
        return (Map<K, V>)filterEntries((BiMap<Object, Object>)map, (Predicate<? super Map.Entry<Object, Object>>)predicate);
    }
    
    @CheckReturnValue
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> filterEntries(final NavigableMap<K, V> navigableMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        NavigableMap<Object, Object> filterFiltered;
        if (!(navigableMap instanceof FilteredEntryNavigableMap)) {
            filterFiltered = new FilteredEntryNavigableMap<Object, Object>((NavigableMap<Object, Object>)Preconditions.checkNotNull(navigableMap), (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        else {
            filterFiltered = filterFiltered((FilteredEntryNavigableMap<Object, Object>)navigableMap, (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        return (NavigableMap<K, V>)filterFiltered;
    }
    
    @CheckReturnValue
    public static <K, V> SortedMap<K, V> filterEntries(final SortedMap<K, V> sortedMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        return Platform.mapsFilterSortedMap(sortedMap, predicate);
    }
    
    private static <K, V> BiMap<K, V> filterFiltered(final FilteredEntryBiMap<K, V> filteredEntryBiMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        return new FilteredEntryBiMap<K, V>(filteredEntryBiMap.unfiltered(), Predicates.and(filteredEntryBiMap.predicate, predicate));
    }
    
    private static <K, V> Map<K, V> filterFiltered(final AbstractFilteredMap<K, V> abstractFilteredMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        return new FilteredEntryMap<K, V>(abstractFilteredMap.unfiltered, Predicates.and(abstractFilteredMap.predicate, predicate));
    }
    
    @GwtIncompatible("NavigableMap")
    private static <K, V> NavigableMap<K, V> filterFiltered(final FilteredEntryNavigableMap<K, V> filteredEntryNavigableMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        return new FilteredEntryNavigableMap<K, V>(((FilteredEntryNavigableMap<Object, Object>)filteredEntryNavigableMap).unfiltered, Predicates.and((Predicate<? super Map.Entry<K, V>>)((FilteredEntryNavigableMap<Object, Object>)filteredEntryNavigableMap).entryPredicate, predicate));
    }
    
    private static <K, V> SortedMap<K, V> filterFiltered(final FilteredEntrySortedMap<K, V> filteredEntrySortedMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        return new FilteredEntrySortedMap<K, V>(filteredEntrySortedMap.sortedMap(), Predicates.and(filteredEntrySortedMap.predicate, predicate));
    }
    
    @CheckReturnValue
    public static <K, V> BiMap<K, V> filterKeys(final BiMap<K, V> biMap, final Predicate<? super K> predicate) {
        Preconditions.checkNotNull(predicate);
        return (BiMap<K, V>)filterEntries((BiMap<Object, Object>)biMap, keyPredicateOnEntries((Predicate<? super Object>)predicate));
    }
    
    @CheckReturnValue
    public static <K, V> Map<K, V> filterKeys(final Map<K, V> map, final Predicate<? super K> predicate) {
        if (map instanceof SortedMap) {
            return (Map<K, V>)filterKeys((SortedMap<Object, Object>)map, (Predicate<? super Object>)predicate);
        }
        if (!(map instanceof BiMap)) {
            Preconditions.checkNotNull(predicate);
            final Predicate<Map.Entry<Object, ?>> keyPredicateOnEntries = keyPredicateOnEntries((Predicate<? super Object>)predicate);
            Map<Object, Object> filterFiltered;
            if (!(map instanceof AbstractFilteredMap)) {
                filterFiltered = new FilteredKeyMap<Object, Object>((Map<Object, Object>)Preconditions.checkNotNull(map), (Predicate<? super Object>)predicate, keyPredicateOnEntries);
            }
            else {
                filterFiltered = filterFiltered((AbstractFilteredMap<Object, Object>)map, keyPredicateOnEntries);
            }
            return (Map<K, V>)filterFiltered;
        }
        return (Map<K, V>)filterKeys((BiMap<Object, Object>)map, (Predicate<? super Object>)predicate);
    }
    
    @CheckReturnValue
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> filterKeys(final NavigableMap<K, V> navigableMap, final Predicate<? super K> predicate) {
        return (NavigableMap<K, V>)filterEntries((NavigableMap<Object, Object>)navigableMap, keyPredicateOnEntries((Predicate<? super Object>)predicate));
    }
    
    @CheckReturnValue
    public static <K, V> SortedMap<K, V> filterKeys(final SortedMap<K, V> sortedMap, final Predicate<? super K> predicate) {
        return (SortedMap<K, V>)filterEntries((SortedMap<Object, Object>)sortedMap, keyPredicateOnEntries((Predicate<? super Object>)predicate));
    }
    
    static <K, V> SortedMap<K, V> filterSortedIgnoreNavigable(final SortedMap<K, V> sortedMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        Preconditions.checkNotNull(predicate);
        SortedMap<Object, Object> filterFiltered;
        if (!(sortedMap instanceof FilteredEntrySortedMap)) {
            filterFiltered = new FilteredEntrySortedMap<Object, Object>((SortedMap<Object, Object>)Preconditions.checkNotNull(sortedMap), (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        else {
            filterFiltered = filterFiltered((FilteredEntrySortedMap<Object, Object>)sortedMap, (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        return (SortedMap<K, V>)filterFiltered;
    }
    
    @CheckReturnValue
    public static <K, V> BiMap<K, V> filterValues(final BiMap<K, V> biMap, final Predicate<? super V> predicate) {
        return (BiMap<K, V>)filterEntries((BiMap<Object, Object>)biMap, valuePredicateOnEntries((Predicate<? super Object>)predicate));
    }
    
    @CheckReturnValue
    public static <K, V> Map<K, V> filterValues(final Map<K, V> map, final Predicate<? super V> predicate) {
        if (map instanceof SortedMap) {
            return (Map<K, V>)filterValues((SortedMap<Object, Object>)map, (Predicate<? super Object>)predicate);
        }
        if (!(map instanceof BiMap)) {
            return (Map<K, V>)filterEntries((Map<Object, Object>)map, valuePredicateOnEntries((Predicate<? super Object>)predicate));
        }
        return (Map<K, V>)filterValues((BiMap<Object, Object>)map, (Predicate<? super Object>)predicate);
    }
    
    @CheckReturnValue
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> filterValues(final NavigableMap<K, V> navigableMap, final Predicate<? super V> predicate) {
        return (NavigableMap<K, V>)filterEntries((NavigableMap<Object, Object>)navigableMap, valuePredicateOnEntries((Predicate<? super Object>)predicate));
    }
    
    @CheckReturnValue
    public static <K, V> SortedMap<K, V> filterValues(final SortedMap<K, V> sortedMap, final Predicate<? super V> predicate) {
        return (SortedMap<K, V>)filterEntries((SortedMap<Object, Object>)sortedMap, valuePredicateOnEntries((Predicate<? super Object>)predicate));
    }
    
    @GwtIncompatible("java.util.Properties")
    public static ImmutableMap<String, String> fromProperties(final Properties properties) {
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String key = (String)propertyNames.nextElement();
            builder.put(key, properties.getProperty(key));
        }
        return builder.build();
    }
    
    @GwtCompatible(serializable = true)
    public static <K, V> Map.Entry<K, V> immutableEntry(@Nullable final K k, @Nullable final V v) {
        return new ImmutableEntry<K, V>(k, v);
    }
    
    @Beta
    @GwtCompatible(serializable = true)
    public static <K extends Enum<K>, V> ImmutableMap<K, V> immutableEnumMap(final Map<K, ? extends V> m) {
        if (m instanceof ImmutableEnumMap) {
            return (ImmutableEnumMap<K, V>)m;
        }
        if (!m.isEmpty()) {
            for (final Map.Entry<T, V> entry : m.entrySet()) {
                Preconditions.checkNotNull((Object)entry.getKey());
                Preconditions.checkNotNull(entry.getValue());
            }
            return ImmutableEnumMap.asImmutable(new EnumMap<K, V>(m));
        }
        return ImmutableMap.of();
    }
    
    static <E> ImmutableMap<E, Integer> indexMap(final Collection<E> collection) {
        int i = 0;
        final ImmutableMap.Builder<E, Integer> builder = new ImmutableMap.Builder<E, Integer>(collection.size());
        final Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            builder.put((E)iterator.next(), i);
            ++i;
        }
        return builder.build();
    }
    
    static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
        return (Function<Map.Entry<K, ?>, K>)EntryFunction.KEY;
    }
    
    static <K, V> Iterator<K> keyIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return Iterators.transform((Iterator<Object>)iterator, (Function<? super Object, ? extends K>)keyFunction());
    }
    
    @Nullable
    static <K> K keyOrNull(@Nullable final Map.Entry<K, ?> entry) {
        K key = null;
        if (entry != null) {
            key = entry.getKey();
        }
        return key;
    }
    
    static <K> Predicate<Map.Entry<K, ?>> keyPredicateOnEntries(final Predicate<? super K> predicate) {
        return Predicates.compose((Predicate<Object>)predicate, (Function<Map.Entry<K, ?>, ?>)keyFunction());
    }
    
    public static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new MapMaker().makeMap();
    }
    
    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(final Class<K> clazz) {
        return new EnumMap<K, V>(Preconditions.checkNotNull(clazz));
    }
    
    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(final Map<K, ? extends V> m) {
        return new EnumMap<K, V>(m);
    }
    
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }
    
    public static <K, V> HashMap<K, V> newHashMap(final Map<? extends K, ? extends V> m) {
        return new HashMap<K, V>(m);
    }
    
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(final int n) {
        return new HashMap<K, V>(capacity(n));
    }
    
    public static <K, V> IdentityHashMap<K, V> newIdentityHashMap() {
        return new IdentityHashMap<K, V>();
    }
    
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
        return new LinkedHashMap<K, V>();
    }
    
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final Map<? extends K, ? extends V> m) {
        return new LinkedHashMap<K, V>(m);
    }
    
    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(final int n) {
        return new LinkedHashMap<K, V>(capacity(n));
    }
    
    public static <K extends Comparable, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<K, V>();
    }
    
    public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@Nullable final Comparator<C> comparator) {
        return new TreeMap<K, V>(comparator);
    }
    
    public static <K, V> TreeMap<K, V> newTreeMap(final SortedMap<K, ? extends V> m) {
        return new TreeMap<K, V>(m);
    }
    
    static <E> Comparator<? super E> orNaturalOrder(@Nullable final Comparator<? super E> comparator) {
        if (comparator == null) {
            return (Comparator<? super E>)Ordering.natural();
        }
        return comparator;
    }
    
    static <K, V> void putAllImpl(final Map<K, V> map, final Map<? extends K, ? extends V> map2) {
        for (final Map.Entry<K, V> entry : map2.entrySet()) {
            map.put(entry.getKey(), (V)entry.getValue());
        }
    }
    
    static <K, V> boolean removeEntryImpl(final Collection<Map.Entry<K, V>> collection, final Object o) {
        return o instanceof Map.Entry && collection.remove(unmodifiableEntry((Map.Entry<?, ?>)o));
    }
    
    @GwtIncompatible("NavigableSet")
    private static <E> NavigableSet<E> removeOnlyNavigableSet(final NavigableSet<E> set) {
        return new ForwardingNavigableSet<E>() {
            @Override
            public boolean add(final E e) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean addAll(final Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            protected NavigableSet<E> delegate() {
                return set;
            }
            
            @Override
            public NavigableSet<E> descendingSet() {
                return (NavigableSet<E>)removeOnlyNavigableSet((NavigableSet<Object>)super.descendingSet());
            }
            
            @Override
            public NavigableSet<E> headSet(final E e, final boolean b) {
                return (NavigableSet<E>)removeOnlyNavigableSet((NavigableSet<Object>)super.headSet(e, b));
            }
            
            @Override
            public SortedSet<E> headSet(final E e) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.headSet(e));
            }
            
            @Override
            public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
                return (NavigableSet<E>)removeOnlyNavigableSet((NavigableSet<Object>)super.subSet(e, b, e2, b2));
            }
            
            @Override
            public SortedSet<E> subSet(final E e, final E e2) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.subSet(e, e2));
            }
            
            @Override
            public NavigableSet<E> tailSet(final E e, final boolean b) {
                return (NavigableSet<E>)removeOnlyNavigableSet((NavigableSet<Object>)super.tailSet(e, b));
            }
            
            @Override
            public SortedSet<E> tailSet(final E e) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.tailSet(e));
            }
        };
    }
    
    private static <E> Set<E> removeOnlySet(final Set<E> set) {
        return new ForwardingSet<E>() {
            @Override
            public boolean add(final E e) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean addAll(final Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            protected Set<E> delegate() {
                return set;
            }
        };
    }
    
    private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> set) {
        return new ForwardingSortedSet<E>() {
            @Override
            public boolean add(final E e) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean addAll(final Collection<? extends E> collection) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            protected SortedSet<E> delegate() {
                return set;
            }
            
            @Override
            public SortedSet<E> headSet(final E e) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.headSet(e));
            }
            
            @Override
            public SortedSet<E> subSet(final E e, final E e2) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.subSet(e, e2));
            }
            
            @Override
            public SortedSet<E> tailSet(final E e) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.tailSet(e));
            }
        };
    }
    
    static boolean safeContainsKey(final Map<?, ?> map, final Object o) {
        Preconditions.checkNotNull(map);
        try {
            return map.containsKey(o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NullPointerException ex2) {
            return false;
        }
    }
    
    static <V> V safeGet(final Map<?, V> map, @Nullable final Object o) {
        Preconditions.checkNotNull(map);
        try {
            return map.get(o);
        }
        catch (final ClassCastException ex) {
            return null;
        }
        catch (final NullPointerException ex2) {
            return null;
        }
    }
    
    static <V> V safeRemove(final Map<?, V> map, final Object o) {
        Preconditions.checkNotNull(map);
        try {
            return map.remove(o);
        }
        catch (final ClassCastException ex) {
            return null;
        }
        catch (final NullPointerException ex2) {
            return null;
        }
    }
    
    public static <K, V> BiMap<K, V> synchronizedBiMap(final BiMap<K, V> biMap) {
        return Synchronized.biMap(biMap, null);
    }
    
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> synchronizedNavigableMap(final NavigableMap<K, V> navigableMap) {
        return Synchronized.navigableMap(navigableMap);
    }
    
    public static <K, V> ImmutableMap<K, V> toMap(final Iterable<K> iterable, final Function<? super K, V> function) {
        return toMap(iterable.iterator(), function);
    }
    
    public static <K, V> ImmutableMap<K, V> toMap(final Iterator<K> iterator, final Function<? super K, V> function) {
        Preconditions.checkNotNull(function);
        final LinkedHashMap<Object, Object> linkedHashMap = newLinkedHashMap();
        while (iterator.hasNext()) {
            final K next = iterator.next();
            linkedHashMap.put(next, function.apply(next));
        }
        return ImmutableMap.copyOf((Map<? extends K, ? extends V>)linkedHashMap);
    }
    
    static String toStringImpl(final Map<?, ?> map) {
        final StringBuilder append = Collections2.newStringBuilderForCollection(map.size()).append('{');
        Maps.STANDARD_JOINER.appendTo(append, map);
        return append.append('}').toString();
    }
    
    public static <K, V1, V2> Map<K, V2> transformEntries(final Map<K, V1> map, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        if (!(map instanceof SortedMap)) {
            return (Map<K, V2>)new TransformedEntriesMap((Map<Object, Object>)map, (EntryTransformer<? super Object, ? super Object, Object>)entryTransformer);
        }
        return (Map<K, V2>)transformEntries((SortedMap<Object, Object>)map, (EntryTransformer<? super Object, ? super Object, V2>)entryTransformer);
    }
    
    @GwtIncompatible("NavigableMap")
    public static <K, V1, V2> NavigableMap<K, V2> transformEntries(final NavigableMap<K, V1> navigableMap, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return new TransformedEntriesNavigableMap<K, Object, V2>(navigableMap, (EntryTransformer<? super K, ?, V2>)entryTransformer);
    }
    
    public static <K, V1, V2> SortedMap<K, V2> transformEntries(final SortedMap<K, V1> sortedMap, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return Platform.mapsTransformEntriesSortedMap(sortedMap, entryTransformer);
    }
    
    static <K, V1, V2> SortedMap<K, V2> transformEntriesIgnoreNavigable(final SortedMap<K, V1> sortedMap, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return new TransformedEntriesSortedMap<K, Object, V2>(sortedMap, (EntryTransformer<? super K, ?, V2>)entryTransformer);
    }
    
    static <V2, K, V1> Map.Entry<K, V2> transformEntry(final EntryTransformer<? super K, ? super V1, V2> entryTransformer, final Map.Entry<K, V1> entry) {
        Preconditions.checkNotNull(entryTransformer);
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V2>() {
            @Override
            public K getKey() {
                return entry.getKey();
            }
            
            @Override
            public V2 getValue() {
                return entryTransformer.transformEntry(entry.getKey(), entry.getValue());
            }
        };
    }
    
    public static <K, V1, V2> Map<K, V2> transformValues(final Map<K, V1> map, final Function<? super V1, V2> function) {
        return transformEntries(map, (EntryTransformer<? super K, ? super V1, V2>)asEntryTransformer((Function<? super V1, V2>)function));
    }
    
    @GwtIncompatible("NavigableMap")
    public static <K, V1, V2> NavigableMap<K, V2> transformValues(final NavigableMap<K, V1> navigableMap, final Function<? super V1, V2> function) {
        return transformEntries(navigableMap, (EntryTransformer<? super K, ? super V1, V2>)asEntryTransformer((Function<? super V1, V2>)function));
    }
    
    public static <K, V1, V2> SortedMap<K, V2> transformValues(final SortedMap<K, V1> sortedMap, final Function<? super V1, V2> function) {
        return transformEntries(sortedMap, (EntryTransformer<? super K, ? super V1, V2>)asEntryTransformer((Function<? super V1, V2>)function));
    }
    
    public static <K, V> ImmutableMap<K, V> uniqueIndex(final Iterable<V> iterable, final Function<? super V, K> function) {
        return uniqueIndex(iterable.iterator(), function);
    }
    
    public static <K, V> ImmutableMap<K, V> uniqueIndex(final Iterator<V> iterator, final Function<? super V, K> function) {
        Preconditions.checkNotNull(function);
        final ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        while (true) {
            Label_0025: {
                if (iterator.hasNext()) {
                    break Label_0025;
                }
                try {
                    return builder.build();
                    final V next = iterator.next();
                    builder.put(function.apply(next), next);
                }
                catch (final IllegalArgumentException ex) {
                    throw new IllegalArgumentException(ex.getMessage() + ". To index multiple values under a key, use Multimaps.index.");
                }
            }
        }
    }
    
    public static <K, V> BiMap<K, V> unmodifiableBiMap(final BiMap<? extends K, ? extends V> biMap) {
        return new UnmodifiableBiMap<K, V>(biMap, null);
    }
    
    static <K, V> Map.Entry<K, V> unmodifiableEntry(final Map.Entry<? extends K, ? extends V> entry) {
        Preconditions.checkNotNull(entry);
        return new AbstractMapEntry<K, V>() {
            @Override
            public K getKey() {
                return entry.getKey();
            }
            
            @Override
            public V getValue() {
                return entry.getValue();
            }
        };
    }
    
    static <K, V> UnmodifiableIterator<Map.Entry<K, V>> unmodifiableEntryIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return new UnmodifiableIterator<Map.Entry<K, V>>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            @Override
            public Map.Entry<K, V> next() {
                return Maps.unmodifiableEntry((Map.Entry<? extends K, ? extends V>)iterator.next());
            }
        };
    }
    
    static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(final Set<Map.Entry<K, V>> s) {
        return new UnmodifiableEntrySet<K, V>(Collections.unmodifiableSet((Set<? extends Map.Entry<K, V>>)s));
    }
    
    private static <K, V> Map<K, V> unmodifiableMap(final Map<K, V> m) {
        if (!(m instanceof SortedMap)) {
            return Collections.unmodifiableMap((Map<? extends K, ? extends V>)m);
        }
        return (Map<K, V>)Collections.unmodifiableSortedMap((SortedMap<Object, ?>)m);
    }
    
    @GwtIncompatible("NavigableMap")
    public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(final NavigableMap<K, V> navigableMap) {
        Preconditions.checkNotNull(navigableMap);
        if (!(navigableMap instanceof UnmodifiableNavigableMap)) {
            return new UnmodifiableNavigableMap<K, V>(navigableMap);
        }
        return navigableMap;
    }
    
    @Nullable
    private static <K, V> Map.Entry<K, V> unmodifiableOrNull(@Nullable final Map.Entry<K, V> entry) {
        Object unmodifiableEntry = null;
        if (entry != null) {
            unmodifiableEntry = unmodifiableEntry((Map.Entry<?, ?>)entry);
        }
        return (Map.Entry<K, V>)unmodifiableEntry;
    }
    
    static <V> Function<Map.Entry<?, V>, V> valueFunction() {
        return (Function<Map.Entry<?, V>, V>)EntryFunction.VALUE;
    }
    
    static <K, V> Iterator<V> valueIterator(final Iterator<Map.Entry<K, V>> iterator) {
        return Iterators.transform((Iterator<Object>)iterator, (Function<? super Object, ? extends V>)valueFunction());
    }
    
    @Nullable
    static <V> V valueOrNull(@Nullable final Map.Entry<?, V> entry) {
        V value = null;
        if (entry != null) {
            value = entry.getValue();
        }
        return value;
    }
    
    static <V> Predicate<Map.Entry<?, V>> valuePredicateOnEntries(final Predicate<? super V> predicate) {
        return Predicates.compose((Predicate<Object>)predicate, (Function<Map.Entry<?, V>, ?>)valueFunction());
    }
    
    private abstract static class AbstractFilteredMap<K, V> extends ViewCachingAbstractMap<K, V>
    {
        final Predicate<? super Entry<K, V>> predicate;
        final Map<K, V> unfiltered;
        
        AbstractFilteredMap(final Map<K, V> unfiltered, final Predicate<? super Entry<K, V>> predicate) {
            this.unfiltered = unfiltered;
            this.predicate = predicate;
        }
        
        boolean apply(@Nullable final Object o, @Nullable final V v) {
            return this.predicate.apply((Object)Maps.immutableEntry(o, v));
        }
        
        @Override
        public boolean containsKey(final Object o) {
            boolean b = false;
            if (this.unfiltered.containsKey(o) && this.apply(o, this.unfiltered.get(o))) {
                b = true;
            }
            return b;
        }
        
        @Override
        Collection<V> createValues() {
            return (Collection<V>)new FilteredMapValues((Map<Object, Object>)this, (Map<Object, Object>)this.unfiltered, (Predicate<? super Entry<Object, Object>>)this.predicate);
        }
        
        @Override
        public V get(final Object o) {
            final V value = this.unfiltered.get(o);
            if (value != null) {
                final V v = value;
                if (this.apply(o, value)) {
                    return v;
                }
            }
            return null;
        }
        
        @Override
        public boolean isEmpty() {
            return this.entrySet().isEmpty();
        }
        
        @Override
        public V put(final K k, final V v) {
            Preconditions.checkArgument(this.apply(k, v));
            return this.unfiltered.put(k, v);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            for (final Map.Entry<Object, V> entry : map.entrySet()) {
                Preconditions.checkArgument(this.apply(entry.getKey(), entry.getValue()));
            }
            this.unfiltered.putAll(map);
        }
        
        @Override
        public V remove(Object remove) {
            if (!this.containsKey(remove)) {
                remove = null;
            }
            else {
                remove = this.unfiltered.remove(remove);
            }
            return (V)remove;
        }
    }
    
    @GwtCompatible
    abstract static class ViewCachingAbstractMap<K, V> extends AbstractMap<K, V>
    {
        private transient Set<Entry<K, V>> entrySet;
        private transient Set<K> keySet;
        private transient Collection<V> values;
        
        abstract Set<Entry<K, V>> createEntrySet();
        
        Set<K> createKeySet() {
            return (Set<K>)new Maps.KeySet((Map<Object, Object>)this);
        }
        
        Collection<V> createValues() {
            return (Collection<V>)new Values((Map<Object, Object>)this);
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> entrySet = this.entrySet;
            if (entrySet == null) {
                entrySet = this.createEntrySet();
                this.entrySet = entrySet;
            }
            return entrySet;
        }
        
        @Override
        public Set<K> keySet() {
            Set<K> keySet = this.keySet;
            if (keySet == null) {
                keySet = this.createKeySet();
                this.keySet = keySet;
            }
            return keySet;
        }
        
        @Override
        public Collection<V> values() {
            Collection<V> values = this.values;
            if (values == null) {
                values = this.createValues();
                this.values = values;
            }
            return values;
        }
    }
    
    private static class AsMapView<K, V> extends ViewCachingAbstractMap<K, V>
    {
        final Function<? super K, V> function;
        private final Set<K> set;
        
        AsMapView(final Set<K> set, final Function<? super K, V> function) {
            this.set = Preconditions.checkNotNull(set);
            this.function = Preconditions.checkNotNull(function);
        }
        
        Set<K> backingSet() {
            return this.set;
        }
        
        @Override
        public void clear() {
            this.backingSet().clear();
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return this.backingSet().contains(o);
        }
        
        protected Set<Entry<K, V>> createEntrySet() {
            return (Set<Entry<K, V>>)new EntrySetImpl();
        }
        
        public Set<K> createKeySet() {
            return (Set<K>)removeOnlySet((Set<Object>)this.backingSet());
        }
        
        @Override
        Collection<V> createValues() {
            return Collections2.transform(this.set, this.function);
        }
        
        @Override
        public V get(@Nullable final Object o) {
            if (!Collections2.safeContains(this.backingSet(), o)) {
                return null;
            }
            return this.function.apply((Object)o);
        }
        
        @Override
        public V remove(@Nullable final Object o) {
            if (!this.backingSet().remove(o)) {
                return null;
            }
            return this.function.apply((Object)o);
        }
        
        @Override
        public int size() {
            return this.backingSet().size();
        }
        
        class EntrySetImpl extends Maps.EntrySet<K, V>
        {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
            }
            
            @Override
            Map<K, V> map() {
                return AsMapView.this;
            }
        }
    }
    
    private static final class BiMapConverter<A, B> extends Converter<A, B> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final BiMap<A, B> bimap;
        
        BiMapConverter(final BiMap<A, B> biMap) {
            this.bimap = Preconditions.checkNotNull(biMap);
        }
        
        private static <X, Y> Y convert(final BiMap<X, Y> biMap, final X x) {
            final Y value = biMap.get(x);
            Preconditions.checkArgument(value != null, "No non-null mapping present for input: %s", x);
            return value;
        }
        
        @Override
        protected A doBackward(final B b) {
            return convert(this.bimap.inverse(), b);
        }
        
        @Override
        protected B doForward(final A a) {
            return convert(this.bimap, a);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            return o instanceof BiMapConverter && this.bimap.equals(((BiMapConverter)o).bimap);
        }
        
        @Override
        public int hashCode() {
            return this.bimap.hashCode();
        }
        
        @Override
        public String toString() {
            return "Maps.asConverter(" + this.bimap + ")";
        }
    }
    
    @GwtIncompatible("NavigableMap")
    abstract static class DescendingMap<K, V> extends ForwardingMap<K, V> implements NavigableMap<K, V>
    {
        private transient Comparator<? super K> comparator;
        private transient Set<Entry<K, V>> entrySet;
        private transient NavigableSet<K> navigableKeySet;
        
        private static <T> Ordering<T> reverse(final Comparator<T> comparator) {
            return Ordering.from(comparator).reverse();
        }
        
        @Override
        public Entry<K, V> ceilingEntry(final K k) {
            return this.forward().floorEntry(k);
        }
        
        @Override
        public K ceilingKey(final K k) {
            return this.forward().floorKey(k);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            Comparator<? super Object> comparator = (Comparator<? super Object>)this.comparator;
            if (comparator == null) {
                Object o = this.forward().comparator();
                if (o == null) {
                    o = Ordering.natural();
                }
                comparator = reverse((Comparator<Object>)o);
                this.comparator = comparator;
            }
            return comparator;
        }
        
        Set<Entry<K, V>> createEntrySet() {
            return (Set<Entry<K, V>>)new EntrySetImpl();
        }
        
        @Override
        protected final Map<K, V> delegate() {
            return this.forward();
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.forward().navigableKeySet();
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            return this.forward();
        }
        
        abstract Iterator<Entry<K, V>> entryIterator();
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> entrySet = this.entrySet;
            if (entrySet == null) {
                entrySet = this.createEntrySet();
                this.entrySet = entrySet;
            }
            return entrySet;
        }
        
        @Override
        public Entry<K, V> firstEntry() {
            return this.forward().lastEntry();
        }
        
        @Override
        public K firstKey() {
            return this.forward().lastKey();
        }
        
        @Override
        public Entry<K, V> floorEntry(final K k) {
            return this.forward().ceilingEntry(k);
        }
        
        @Override
        public K floorKey(final K k) {
            return this.forward().ceilingKey(k);
        }
        
        abstract NavigableMap<K, V> forward();
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            return this.forward().tailMap(k, b).descendingMap();
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public Entry<K, V> higherEntry(final K k) {
            return this.forward().lowerEntry(k);
        }
        
        @Override
        public K higherKey(final K k) {
            return this.forward().lowerKey(k);
        }
        
        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }
        
        @Override
        public Entry<K, V> lastEntry() {
            return this.forward().firstEntry();
        }
        
        @Override
        public K lastKey() {
            return this.forward().firstKey();
        }
        
        @Override
        public Entry<K, V> lowerEntry(final K k) {
            return this.forward().higherEntry(k);
        }
        
        @Override
        public K lowerKey(final K k) {
            return this.forward().higherKey(k);
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            NavigableSet<K> navigableKeySet = this.navigableKeySet;
            if (navigableKeySet == null) {
                navigableKeySet = new NavigableKeySet<K, Object>(this);
                this.navigableKeySet = navigableKeySet;
            }
            return navigableKeySet;
        }
        
        @Override
        public Entry<K, V> pollFirstEntry() {
            return this.forward().pollLastEntry();
        }
        
        @Override
        public Entry<K, V> pollLastEntry() {
            return this.forward().pollFirstEntry();
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return this.forward().subMap(i, b2, k, b).descendingMap();
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            return this.forward().headMap(k, b).descendingMap();
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return this.tailMap(k, true);
        }
        
        @Override
        public String toString() {
            return this.standardToString();
        }
        
        @Override
        public Collection<V> values() {
            return (Collection<V>)new Values((Map<Object, Object>)this);
        }
        
        class EntrySetImpl extends Maps.EntrySet<K, V>
        {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return DescendingMap.this.entryIterator();
            }
            
            @Override
            Map<K, V> map() {
                return DescendingMap.this;
            }
        }
    }
    
    private enum EntryFunction implements Function<Map.Entry<?, ?>, Object>
    {
        KEY {
            @Nullable
            @Override
            public Object apply(final Map.Entry<?, ?> entry) {
                return entry.getKey();
            }
        }, 
        VALUE {
            @Nullable
            @Override
            public Object apply(final Map.Entry<?, ?> entry) {
                return entry.getValue();
            }
        };
    }
    
    abstract static class EntrySet<K, V> extends ImprovedAbstractSet<Map.Entry<K, V>>
    {
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(Object safeGet) {
            boolean b = false;
            if (!(safeGet instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)safeGet;
            final Object key = entry.getKey();
            safeGet = Maps.safeGet(this.map(), key);
            if (Objects.equal(safeGet, entry.getValue())) {
                if (safeGet != null || this.map().containsKey(key)) {
                    b = true;
                }
            }
            return b;
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        abstract Map<K, V> map();
        
        @Override
        public boolean remove(final Object o) {
            return this.contains(o) && this.map().keySet().remove(((Map.Entry)o).getKey());
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            try {
                return super.removeAll(Preconditions.checkNotNull(collection));
            }
            catch (final UnsupportedOperationException ex) {
                return Sets.removeAllImpl(this, collection.iterator());
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            try {
                return super.retainAll(Preconditions.checkNotNull(collection));
            }
            catch (final UnsupportedOperationException ex) {
                final HashSet<Object> hashSetWithExpectedSize = Sets.newHashSetWithExpectedSize(collection.size());
                for (final Object next : collection) {
                    if (this.contains(next)) {
                        hashSetWithExpectedSize.add(((Map.Entry<Object, V>)next).getKey());
                    }
                }
                return this.map().keySet().retainAll(hashSetWithExpectedSize);
            }
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
    }
    
    public interface EntryTransformer<K, V1, V2>
    {
        V2 transformEntry(@Nullable final K p0, @Nullable final V1 p1);
    }
    
    static final class FilteredEntryBiMap<K, V> extends FilteredEntryMap<K, V> implements BiMap<K, V>
    {
        private final BiMap<V, K> inverse;
        
        FilteredEntryBiMap(final BiMap<K, V> biMap, final Predicate<? super Entry<K, V>> predicate) {
            super(biMap, predicate);
            this.inverse = (BiMap<V, K>)new FilteredEntryBiMap((BiMap<Object, Object>)biMap.inverse(), inversePredicate((Predicate<? super Entry<Object, Object>>)predicate), (BiMap<Object, Object>)this);
        }
        
        private FilteredEntryBiMap(final BiMap<K, V> biMap, final Predicate<? super Entry<K, V>> predicate, final BiMap<V, K> inverse) {
            super(biMap, predicate);
            this.inverse = inverse;
        }
        
        private static <K, V> Predicate<Entry<V, K>> inversePredicate(final Predicate<? super Entry<K, V>> predicate) {
            return new Predicate<Entry<V, K>>() {
                @Override
                public boolean apply(final Entry<V, K> entry) {
                    return predicate.apply(Maps.immutableEntry(entry.getValue(), entry.getKey()));
                }
            };
        }
        
        @Override
        public V forcePut(@Nullable final K k, @Nullable final V v) {
            Preconditions.checkArgument(this.apply(k, v));
            return this.unfiltered().forcePut(k, v);
        }
        
        @Override
        public BiMap<V, K> inverse() {
            return this.inverse;
        }
        
        BiMap<K, V> unfiltered() {
            return (BiMap)this.unfiltered;
        }
        
        @Override
        public Set<V> values() {
            return this.inverse.keySet();
        }
    }
    
    static class FilteredEntryMap<K, V> extends AbstractFilteredMap<K, V>
    {
        final Set<Entry<K, V>> filteredEntrySet;
        
        FilteredEntryMap(final Map<K, V> map, final Predicate<? super Entry<K, V>> predicate) {
            super(map, predicate);
            this.filteredEntrySet = Sets.filter(map.entrySet(), this.predicate);
        }
        
        protected Set<Entry<K, V>> createEntrySet() {
            return new EntrySet();
        }
        
        @Override
        Set<K> createKeySet() {
            return (Set<K>)new KeySet();
        }
        
        private class EntrySet extends ForwardingSet<Entry<K, V>>
        {
            @Override
            protected Set<Entry<K, V>> delegate() {
                return FilteredEntryMap.this.filteredEntrySet;
            }
            
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new TransformedIterator<Entry<K, V>, Entry<K, V>>(FilteredEntryMap.this.filteredEntrySet.iterator()) {
                    @Override
                    Entry<K, V> transform(final Entry<K, V> entry) {
                        return new ForwardingMapEntry<K, V>() {
                            @Override
                            protected Entry<K, V> delegate() {
                                return entry;
                            }
                            
                            @Override
                            public V setValue(final V value) {
                                Preconditions.checkArgument(FilteredEntryMap.this.apply(((ForwardingMapEntry<Object, V>)this).getKey(), value));
                                return super.setValue(value);
                            }
                        };
                    }
                };
            }
        }
        
        class KeySet extends Maps.KeySet<K, V>
        {
            KeySet() {
                super(FilteredEntryMap.this);
            }
            
            private boolean removeIf(final Predicate<? super K> predicate) {
                return Iterables.removeIf(FilteredEntryMap.this.unfiltered.entrySet(), Predicates.and(FilteredEntryMap.this.predicate, (Predicate<? super Map.Entry<K, V>>)Maps.keyPredicateOnEntries((Predicate<? super Object>)predicate)));
            }
            
            @Override
            public boolean remove(final Object o) {
                if (!FilteredEntryMap.this.containsKey(o)) {
                    return false;
                }
                FilteredEntryMap.this.unfiltered.remove(o);
                return true;
            }
            
            @Override
            public boolean removeAll(final Collection<?> collection) {
                return this.removeIf(Predicates.in((Collection<? extends K>)collection));
            }
            
            @Override
            public boolean retainAll(final Collection<?> collection) {
                return this.removeIf(Predicates.not(Predicates.in((Collection<? extends K>)collection)));
            }
            
            @Override
            public Object[] toArray() {
                return Lists.newArrayList(((Maps.KeySet<?, V>)this).iterator()).toArray();
            }
            
            @Override
            public <T> T[] toArray(final T[] a) {
                return Lists.newArrayList(((Maps.KeySet<?, V>)this).iterator()).toArray(a);
            }
        }
    }
    
    static class KeySet<K, V> extends ImprovedAbstractSet<K>
    {
        @Weak
        final Map<K, V> map;
        
        KeySet(final Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map().containsKey(o);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(this.map().entrySet().iterator());
        }
        
        Map<K, V> map() {
            return this.map;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!this.contains(o)) {
                return false;
            }
            this.map().remove(o);
            return true;
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
    }
    
    @GwtIncompatible("NavigableMap")
    private static class FilteredEntryNavigableMap<K, V> extends AbstractNavigableMap<K, V>
    {
        private final Predicate<? super Entry<K, V>> entryPredicate;
        private final Map<K, V> filteredDelegate;
        private final NavigableMap<K, V> unfiltered;
        
        FilteredEntryNavigableMap(final NavigableMap<K, V> navigableMap, final Predicate<? super Entry<K, V>> entryPredicate) {
            this.unfiltered = Preconditions.checkNotNull(navigableMap);
            this.entryPredicate = entryPredicate;
            this.filteredDelegate = new FilteredEntryMap<K, V>(navigableMap, entryPredicate);
        }
        
        @Override
        public void clear() {
            this.filteredDelegate.clear();
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.unfiltered.comparator();
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return this.filteredDelegate.containsKey(o);
        }
        
        @Override
        Iterator<Entry<K, V>> descendingEntryIterator() {
            return (Iterator<Entry<K, V>>)Iterators.filter(this.unfiltered.descendingMap().entrySet().iterator(), (Predicate<? super Map.Entry<Object, Object>>)this.entryPredicate);
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            return Maps.filterEntries(this.unfiltered.descendingMap(), this.entryPredicate);
        }
        
        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return (Iterator<Entry<K, V>>)Iterators.filter(this.unfiltered.entrySet().iterator(), (Predicate<? super Map.Entry<Object, Object>>)this.entryPredicate);
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            return this.filteredDelegate.entrySet();
        }
        
        @Nullable
        @Override
        public V get(@Nullable final Object o) {
            return this.filteredDelegate.get(o);
        }
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            return Maps.filterEntries(this.unfiltered.headMap(k, b), this.entryPredicate);
        }
        
        @Override
        public boolean isEmpty() {
            boolean b = false;
            if (!Iterables.any(this.unfiltered.entrySet(), (Predicate<? super Map.Entry<Object, Object>>)this.entryPredicate)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            return new NavigableKeySet<K, V>(this) {
                @Override
                public boolean removeAll(final Collection<?> collection) {
                    return Iterators.removeIf((Iterator<Object>)FilteredEntryNavigableMap.this.unfiltered.entrySet().iterator(), Predicates.and(FilteredEntryNavigableMap.this.entryPredicate, (Predicate<? super Object>)Maps.keyPredicateOnEntries(Predicates.in(collection))));
                }
                
                @Override
                public boolean retainAll(final Collection<?> collection) {
                    return Iterators.removeIf((Iterator<Object>)FilteredEntryNavigableMap.this.unfiltered.entrySet().iterator(), Predicates.and(FilteredEntryNavigableMap.this.entryPredicate, (Predicate<? super Object>)Maps.keyPredicateOnEntries(Predicates.not(Predicates.in((Collection<? extends K>)collection)))));
                }
            };
        }
        
        @Override
        public Entry<K, V> pollFirstEntry() {
            return (Entry)Iterables.removeFirstMatching(this.unfiltered.entrySet(), (Predicate<? super Map.Entry<Object, Object>>)this.entryPredicate);
        }
        
        @Override
        public Entry<K, V> pollLastEntry() {
            return (Entry)Iterables.removeFirstMatching(this.unfiltered.descendingMap().entrySet(), (Predicate<? super Map.Entry<Object, Object>>)this.entryPredicate);
        }
        
        @Override
        public V put(final K k, final V v) {
            return this.filteredDelegate.put(k, v);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            this.filteredDelegate.putAll(map);
        }
        
        @Override
        public V remove(@Nullable final Object o) {
            return this.filteredDelegate.remove(o);
        }
        
        @Override
        public int size() {
            return this.filteredDelegate.size();
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return Maps.filterEntries(this.unfiltered.subMap(k, b, i, b2), this.entryPredicate);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            return Maps.filterEntries(this.unfiltered.tailMap(k, b), this.entryPredicate);
        }
        
        @Override
        public Collection<V> values() {
            return (Collection<V>)new FilteredMapValues((Map<Object, Object>)this, (Map<Object, Object>)this.unfiltered, (Predicate<? super Entry<Object, Object>>)this.entryPredicate);
        }
    }
    
    abstract static class IteratorBasedAbstractMap<K, V> extends AbstractMap<K, V>
    {
        @Override
        public void clear() {
            Iterators.clear(this.entryIterator());
        }
        
        abstract Iterator<Entry<K, V>> entryIterator();
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            return (Set<Entry<K, V>>)new Maps.EntrySet<K, V>() {
                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return IteratorBasedAbstractMap.this.entryIterator();
                }
                
                @Override
                Map<K, V> map() {
                    return IteratorBasedAbstractMap.this;
                }
            };
        }
        
        @Override
        public abstract int size();
    }
    
    @GwtIncompatible("NavigableMap")
    static class NavigableKeySet<K, V> extends Maps.SortedKeySet<K, V> implements NavigableSet<K>
    {
        NavigableKeySet(final NavigableMap<K, V> navigableMap) {
            super(navigableMap);
        }
        
        @Override
        public K ceiling(final K k) {
            return this.map().ceilingKey(k);
        }
        
        @Override
        public Iterator<K> descendingIterator() {
            return this.descendingSet().iterator();
        }
        
        @Override
        public NavigableSet<K> descendingSet() {
            return this.map().descendingKeySet();
        }
        
        @Override
        public K floor(final K k) {
            return this.map().floorKey(k);
        }
        
        @Override
        public NavigableSet<K> headSet(final K k, final boolean b) {
            return this.map().headMap(k, b).navigableKeySet();
        }
        
        @Override
        public SortedSet<K> headSet(final K k) {
            return this.headSet(k, false);
        }
        
        @Override
        public K higher(final K k) {
            return this.map().higherKey(k);
        }
        
        @Override
        public K lower(final K k) {
            return this.map().lowerKey(k);
        }
        
        NavigableMap<K, V> map() {
            return (NavigableMap)this.map;
        }
        
        @Override
        public K pollFirst() {
            return Maps.keyOrNull((Map.Entry<K, ?>)this.map().pollFirstEntry());
        }
        
        @Override
        public K pollLast() {
            return Maps.keyOrNull((Map.Entry<K, ?>)this.map().pollLastEntry());
        }
        
        @Override
        public NavigableSet<K> subSet(final K k, final boolean b, final K i, final boolean b2) {
            return this.map().subMap(k, b, i, b2).navigableKeySet();
        }
        
        @Override
        public SortedSet<K> subSet(final K k, final K i) {
            return this.subSet(k, true, i, false);
        }
        
        @Override
        public NavigableSet<K> tailSet(final K k, final boolean b) {
            return this.map().tailMap(k, b).navigableKeySet();
        }
        
        @Override
        public SortedSet<K> tailSet(final K k) {
            return this.tailSet(k, true);
        }
    }
    
    static class SortedKeySet<K, V> extends Maps.KeySet<K, V> implements SortedSet<K>
    {
        SortedKeySet(final SortedMap<K, V> sortedMap) {
            super(sortedMap);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.map().comparator();
        }
        
        @Override
        public K first() {
            return this.map().firstKey();
        }
        
        @Override
        public SortedSet<K> headSet(final K k) {
            return new SortedKeySet<K, Object>(this.map().headMap(k));
        }
        
        @Override
        public K last() {
            return this.map().lastKey();
        }
        
        SortedMap<K, V> map() {
            return (SortedMap)super.map();
        }
        
        @Override
        public SortedSet<K> subSet(final K k, final K i) {
            return new SortedKeySet<K, Object>(this.map().subMap(k, i));
        }
        
        @Override
        public SortedSet<K> tailSet(final K k) {
            return new SortedKeySet<K, Object>(this.map().tailMap(k));
        }
    }
    
    private static class FilteredEntrySortedMap<K, V> extends FilteredEntryMap<K, V> implements SortedMap<K, V>
    {
        FilteredEntrySortedMap(final SortedMap<K, V> sortedMap, final Predicate<? super Entry<K, V>> predicate) {
            super(sortedMap, predicate);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.sortedMap().comparator();
        }
        
        SortedSet<K> createKeySet() {
            return new SortedKeySet();
        }
        
        @Override
        public K firstKey() {
            return this.keySet().iterator().next();
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return new FilteredEntrySortedMap((SortedMap<Object, Object>)this.sortedMap().headMap(k), (Predicate<? super Entry<Object, Object>>)this.predicate);
        }
        
        @Override
        public SortedSet<K> keySet() {
            return (SortedSet)super.keySet();
        }
        
        @Override
        public K lastKey() {
            SortedMap<K, V> sortedMap = this.sortedMap();
            K lastKey;
            while (true) {
                lastKey = sortedMap.lastKey();
                if (this.apply(lastKey, this.unfiltered.get(lastKey))) {
                    break;
                }
                sortedMap = this.sortedMap().headMap(lastKey);
            }
            return lastKey;
        }
        
        SortedMap<K, V> sortedMap() {
            return (SortedMap)this.unfiltered;
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return new FilteredEntrySortedMap((SortedMap<Object, Object>)this.sortedMap().subMap(k, i), (Predicate<? super Entry<Object, Object>>)this.predicate);
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return new FilteredEntrySortedMap((SortedMap<Object, Object>)this.sortedMap().tailMap(k), (Predicate<? super Entry<Object, Object>>)this.predicate);
        }
        
        class SortedKeySet extends FilteredEntryMap.KeySet implements SortedSet<K>
        {
            SortedKeySet() {
                (FilteredEntryMap)FilteredEntrySortedMap.this.super();
            }
            
            @Override
            public Comparator<? super K> comparator() {
                return FilteredEntrySortedMap.this.sortedMap().comparator();
            }
            
            @Override
            public K first() {
                return FilteredEntrySortedMap.this.firstKey();
            }
            
            @Override
            public SortedSet<K> headSet(final K k) {
                return (SortedSet)FilteredEntrySortedMap.this.headMap(k).keySet();
            }
            
            @Override
            public K last() {
                return FilteredEntrySortedMap.this.lastKey();
            }
            
            @Override
            public SortedSet<K> subSet(final K k, final K i) {
                return (SortedSet)FilteredEntrySortedMap.this.subMap(k, i).keySet();
            }
            
            @Override
            public SortedSet<K> tailSet(final K k) {
                return (SortedSet)FilteredEntrySortedMap.this.tailMap(k).keySet();
            }
        }
    }
    
    private static class FilteredKeyMap<K, V> extends AbstractFilteredMap<K, V>
    {
        Predicate<? super K> keyPredicate;
        
        FilteredKeyMap(final Map<K, V> map, final Predicate<? super K> keyPredicate, final Predicate<? super Entry<K, V>> predicate) {
            super(map, predicate);
            this.keyPredicate = keyPredicate;
        }
        
        @Override
        public boolean containsKey(final Object o) {
            boolean b = false;
            if (this.unfiltered.containsKey(o) && this.keyPredicate.apply((Object)o)) {
                b = true;
            }
            return b;
        }
        
        protected Set<Entry<K, V>> createEntrySet() {
            return Sets.filter(this.unfiltered.entrySet(), this.predicate);
        }
        
        @Override
        Set<K> createKeySet() {
            return Sets.filter(this.unfiltered.keySet(), this.keyPredicate);
        }
    }
    
    private static final class FilteredMapValues<K, V> extends Values<K, V>
    {
        Predicate<? super Map.Entry<K, V>> predicate;
        Map<K, V> unfiltered;
        
        FilteredMapValues(final Map<K, V> map, final Map<K, V> unfiltered, final Predicate<? super Map.Entry<K, V>> predicate) {
            super(map);
            this.unfiltered = unfiltered;
            this.predicate = predicate;
        }
        
        private boolean removeIf(final Predicate<? super V> predicate) {
            return Iterables.removeIf(this.unfiltered.entrySet(), Predicates.and(this.predicate, (Predicate<? super Map.Entry<K, V>>)Maps.valuePredicateOnEntries((Predicate<? super Object>)predicate)));
        }
        
        @Override
        public boolean remove(final Object o) {
            return Iterables.removeFirstMatching(this.unfiltered.entrySet(), Predicates.and(this.predicate, (Predicate<? super Map.Entry<K, V>>)Maps.valuePredicateOnEntries(Predicates.equalTo(o)))) != null;
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return this.removeIf(Predicates.in((Collection<? extends V>)collection));
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return this.removeIf(Predicates.not(Predicates.in((Collection<? extends V>)collection)));
        }
        
        @Override
        public Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return Lists.newArrayList(this.iterator()).toArray(a);
        }
    }
    
    static class MapDifferenceImpl<K, V> implements MapDifference<K, V>
    {
        final Map<K, ValueDifference<V>> differences;
        final Map<K, V> onBoth;
        final Map<K, V> onlyOnLeft;
        final Map<K, V> onlyOnRight;
        
        MapDifferenceImpl(final Map<K, V> map, final Map<K, V> map2, final Map<K, V> map3, final Map<K, ValueDifference<V>> map4) {
            this.onlyOnLeft = (Map<K, V>)unmodifiableMap((Map<Object, Object>)map);
            this.onlyOnRight = (Map<K, V>)unmodifiableMap((Map<Object, Object>)map2);
            this.onBoth = (Map<K, V>)unmodifiableMap((Map<Object, Object>)map3);
            this.differences = (Map<K, ValueDifference<V>>)unmodifiableMap((Map<Object, Object>)map4);
        }
        
        @Override
        public boolean areEqual() {
            final boolean b = false;
            boolean b2;
            if (!this.onlyOnLeft.isEmpty()) {
                b2 = b;
            }
            else {
                b2 = b;
                if (this.onlyOnRight.isEmpty()) {
                    b2 = b;
                    if (this.differences.isEmpty()) {
                        b2 = true;
                    }
                }
            }
            return b2;
        }
        
        @Override
        public Map<K, ValueDifference<V>> entriesDiffering() {
            return this.differences;
        }
        
        @Override
        public Map<K, V> entriesInCommon() {
            return this.onBoth;
        }
        
        @Override
        public Map<K, V> entriesOnlyOnLeft() {
            return this.onlyOnLeft;
        }
        
        @Override
        public Map<K, V> entriesOnlyOnRight() {
            return this.onlyOnRight;
        }
        
        @Override
        public boolean equals(final Object o) {
            boolean b = true;
            if (o == this) {
                return true;
            }
            if (!(o instanceof MapDifference)) {
                return false;
            }
            final MapDifference mapDifference = (MapDifference)o;
            if (!this.entriesOnlyOnLeft().equals(mapDifference.entriesOnlyOnLeft()) || !this.entriesOnlyOnRight().equals(mapDifference.entriesOnlyOnRight()) || !this.entriesInCommon().equals(mapDifference.entriesInCommon()) || !this.entriesDiffering().equals(mapDifference.entriesDiffering())) {
                b = false;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.entriesOnlyOnLeft(), this.entriesOnlyOnRight(), this.entriesInCommon(), this.entriesDiffering());
        }
        
        @Override
        public String toString() {
            if (!this.areEqual()) {
                final StringBuilder sb = new StringBuilder("not equal");
                if (!this.onlyOnLeft.isEmpty()) {
                    sb.append(": only on left=").append(this.onlyOnLeft);
                }
                if (!this.onlyOnRight.isEmpty()) {
                    sb.append(": only on right=").append(this.onlyOnRight);
                }
                if (!this.differences.isEmpty()) {
                    sb.append(": value differences=").append(this.differences);
                }
                return sb.toString();
            }
            return "equal";
        }
    }
    
    @GwtIncompatible("NavigableMap")
    private static final class NavigableAsMapView<K, V> extends AbstractNavigableMap<K, V>
    {
        private final Function<? super K, V> function;
        private final NavigableSet<K> set;
        
        NavigableAsMapView(final NavigableSet<K> set, final Function<? super K, V> function) {
            this.set = Preconditions.checkNotNull(set);
            this.function = Preconditions.checkNotNull(function);
        }
        
        @Override
        public void clear() {
            this.set.clear();
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.set.comparator();
        }
        
        @Override
        Iterator<Entry<K, V>> descendingEntryIterator() {
            return (Iterator<Entry<K, V>>)this.descendingMap().entrySet().iterator();
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            return Maps.asMap(this.set.descendingSet(), this.function);
        }
        
        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return Maps.asMapEntryIterator(this.set, this.function);
        }
        
        @Nullable
        @Override
        public V get(@Nullable final Object o) {
            if (!Collections2.safeContains(this.set, o)) {
                return null;
            }
            return this.function.apply((Object)o);
        }
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            return Maps.asMap(this.set.headSet(k, b), this.function);
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            return (NavigableSet<K>)removeOnlyNavigableSet((NavigableSet<Object>)this.set);
        }
        
        @Override
        public int size() {
            return this.set.size();
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return Maps.asMap(this.set.subSet(k, b, i, b2), this.function);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            return Maps.asMap(this.set.tailSet(k, b), this.function);
        }
    }
    
    private static class SortedAsMapView<K, V> extends AsMapView<K, V> implements SortedMap<K, V>
    {
        SortedAsMapView(final SortedSet<K> set, final Function<? super K, V> function) {
            super(set, function);
        }
        
        SortedSet<K> backingSet() {
            return (SortedSet)super.backingSet();
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.backingSet().comparator();
        }
        
        @Override
        public K firstKey() {
            return this.backingSet().first();
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return Maps.asMap(this.backingSet().headSet(k), this.function);
        }
        
        @Override
        public Set<K> keySet() {
            return (Set<K>)removeOnlySortedSet((SortedSet<Object>)this.backingSet());
        }
        
        @Override
        public K lastKey() {
            return this.backingSet().last();
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return Maps.asMap(this.backingSet().subSet(k, i), this.function);
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return Maps.asMap(this.backingSet().tailSet(k), this.function);
        }
    }
    
    static class SortedMapDifferenceImpl<K, V> extends MapDifferenceImpl<K, V> implements SortedMapDifference<K, V>
    {
        SortedMapDifferenceImpl(final SortedMap<K, V> sortedMap, final SortedMap<K, V> sortedMap2, final SortedMap<K, V> sortedMap3, final SortedMap<K, ValueDifference<V>> sortedMap4) {
            super(sortedMap, sortedMap2, sortedMap3, sortedMap4);
        }
        
        @Override
        public SortedMap<K, ValueDifference<V>> entriesDiffering() {
            return (SortedMap)super.entriesDiffering();
        }
        
        @Override
        public SortedMap<K, V> entriesInCommon() {
            return (SortedMap)super.entriesInCommon();
        }
        
        @Override
        public SortedMap<K, V> entriesOnlyOnLeft() {
            return (SortedMap)super.entriesOnlyOnLeft();
        }
        
        @Override
        public SortedMap<K, V> entriesOnlyOnRight() {
            return (SortedMap)super.entriesOnlyOnRight();
        }
    }
    
    static class TransformedEntriesMap<K, V1, V2> extends IteratorBasedAbstractMap<K, V2>
    {
        final Map<K, V1> fromMap;
        final EntryTransformer<? super K, ? super V1, V2> transformer;
        
        TransformedEntriesMap(final Map<K, V1> map, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
            this.fromMap = Preconditions.checkNotNull(map);
            this.transformer = (EntryTransformer<? super K, ? super V1, V2>)Preconditions.checkNotNull((EntryTransformer)entryTransformer);
        }
        
        @Override
        public void clear() {
            this.fromMap.clear();
        }
        
        @Override
        public boolean containsKey(final Object o) {
            return this.fromMap.containsKey(o);
        }
        
        @Override
        Iterator<Entry<K, V2>> entryIterator() {
            return Iterators.transform(this.fromMap.entrySet().iterator(), (Function<? super Map.Entry<K, V1>, ? extends Entry<K, V2>>)Maps.asEntryToEntryFunction((EntryTransformer<? super Object, ? super Object, V2>)this.transformer));
        }
        
        @Override
        public V2 get(Object transformEntry) {
            final Object o = null;
            final V1 value = this.fromMap.get(transformEntry);
            if (value == null && !this.fromMap.containsKey(transformEntry)) {
                transformEntry = o;
            }
            else {
                transformEntry = this.transformer.transformEntry((Object)transformEntry, (Object)value);
            }
            return (V2)transformEntry;
        }
        
        @Override
        public Set<K> keySet() {
            return this.fromMap.keySet();
        }
        
        @Override
        public V2 remove(Object transformEntry) {
            if (!this.fromMap.containsKey(transformEntry)) {
                transformEntry = null;
            }
            else {
                transformEntry = this.transformer.transformEntry((Object)transformEntry, (Object)this.fromMap.remove(transformEntry));
            }
            return (V2)transformEntry;
        }
        
        @Override
        public int size() {
            return this.fromMap.size();
        }
        
        @Override
        public Collection<V2> values() {
            return (Collection<V2>)new Values((Map<Object, Object>)this);
        }
    }
    
    @GwtIncompatible("NavigableMap")
    private static class TransformedEntriesNavigableMap<K, V1, V2> extends TransformedEntriesSortedMap<K, V1, V2> implements NavigableMap<K, V2>
    {
        TransformedEntriesNavigableMap(final NavigableMap<K, V1> navigableMap, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
            super(navigableMap, entryTransformer);
        }
        
        @Nullable
        private Entry<K, V2> transformEntry(@Nullable final Entry<K, V1> entry) {
            Entry<K, V2> transformEntry = null;
            if (entry != null) {
                transformEntry = Maps.transformEntry(this.transformer, entry);
            }
            return transformEntry;
        }
        
        @Override
        public Entry<K, V2> ceilingEntry(final K k) {
            return this.transformEntry(this.fromMap().ceilingEntry(k));
        }
        
        @Override
        public K ceilingKey(final K k) {
            return this.fromMap().ceilingKey(k);
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            return this.fromMap().descendingKeySet();
        }
        
        @Override
        public NavigableMap<K, V2> descendingMap() {
            return Maps.transformEntries(this.fromMap().descendingMap(), this.transformer);
        }
        
        @Override
        public Entry<K, V2> firstEntry() {
            return this.transformEntry(this.fromMap().firstEntry());
        }
        
        @Override
        public Entry<K, V2> floorEntry(final K k) {
            return this.transformEntry(this.fromMap().floorEntry(k));
        }
        
        @Override
        public K floorKey(final K k) {
            return this.fromMap().floorKey(k);
        }
        
        protected NavigableMap<K, V1> fromMap() {
            return (NavigableMap)super.fromMap();
        }
        
        @Override
        public NavigableMap<K, V2> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public NavigableMap<K, V2> headMap(final K k, final boolean b) {
            return Maps.transformEntries(this.fromMap().headMap(k, b), this.transformer);
        }
        
        @Override
        public Entry<K, V2> higherEntry(final K k) {
            return this.transformEntry(this.fromMap().higherEntry(k));
        }
        
        @Override
        public K higherKey(final K k) {
            return this.fromMap().higherKey(k);
        }
        
        @Override
        public Entry<K, V2> lastEntry() {
            return this.transformEntry(this.fromMap().lastEntry());
        }
        
        @Override
        public Entry<K, V2> lowerEntry(final K k) {
            return this.transformEntry(this.fromMap().lowerEntry(k));
        }
        
        @Override
        public K lowerKey(final K k) {
            return this.fromMap().lowerKey(k);
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            return this.fromMap().navigableKeySet();
        }
        
        @Override
        public Entry<K, V2> pollFirstEntry() {
            return this.transformEntry(this.fromMap().pollFirstEntry());
        }
        
        @Override
        public Entry<K, V2> pollLastEntry() {
            return this.transformEntry(this.fromMap().pollLastEntry());
        }
        
        @Override
        public NavigableMap<K, V2> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, V2> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return Maps.transformEntries(this.fromMap().subMap(k, b, i, b2), this.transformer);
        }
        
        @Override
        public NavigableMap<K, V2> tailMap(final K k) {
            return this.tailMap(k, true);
        }
        
        @Override
        public NavigableMap<K, V2> tailMap(final K k, final boolean b) {
            return Maps.transformEntries(this.fromMap().tailMap(k, b), this.transformer);
        }
    }
    
    static class TransformedEntriesSortedMap<K, V1, V2> extends TransformedEntriesMap<K, V1, V2> implements SortedMap<K, V2>
    {
        TransformedEntriesSortedMap(final SortedMap<K, V1> sortedMap, final EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
            super(sortedMap, entryTransformer);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return this.fromMap().comparator();
        }
        
        @Override
        public K firstKey() {
            return this.fromMap().firstKey();
        }
        
        protected SortedMap<K, V1> fromMap() {
            return (SortedMap)this.fromMap;
        }
        
        @Override
        public SortedMap<K, V2> headMap(final K k) {
            return Maps.transformEntries(this.fromMap().headMap(k), this.transformer);
        }
        
        @Override
        public K lastKey() {
            return this.fromMap().lastKey();
        }
        
        @Override
        public SortedMap<K, V2> subMap(final K k, final K i) {
            return Maps.transformEntries(this.fromMap().subMap(k, i), this.transformer);
        }
        
        @Override
        public SortedMap<K, V2> tailMap(final K k) {
            return Maps.transformEntries(this.fromMap().tailMap(k), this.transformer);
        }
    }
    
    private static class UnmodifiableBiMap<K, V> extends ForwardingMap<K, V> implements BiMap<K, V>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final BiMap<? extends K, ? extends V> delegate;
        BiMap<V, K> inverse;
        final Map<K, V> unmodifiableMap;
        transient Set<V> values;
        
        UnmodifiableBiMap(final BiMap<? extends K, ? extends V> biMap, @Nullable final BiMap<V, K> inverse) {
            this.unmodifiableMap = Collections.unmodifiableMap((Map<? extends K, ? extends V>)biMap);
            this.delegate = biMap;
            this.inverse = inverse;
        }
        
        @Override
        protected Map<K, V> delegate() {
            return this.unmodifiableMap;
        }
        
        @Override
        public V forcePut(final K k, final V v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public BiMap<V, K> inverse() {
            BiMap<V, K> inverse = this.inverse;
            if (inverse == null) {
                inverse = (BiMap<V, K>)new UnmodifiableBiMap(this.delegate.inverse(), (BiMap<Object, Object>)this);
                this.inverse = inverse;
            }
            return inverse;
        }
        
        @Override
        public Set<V> values() {
            Object values = this.values;
            if (values == null) {
                values = Collections.unmodifiableSet(this.delegate.values());
                this.values = (Set<V>)values;
            }
            return (Set<V>)values;
        }
    }
    
    static class UnmodifiableEntries<K, V> extends ForwardingCollection<Map.Entry<K, V>>
    {
        private final Collection<Map.Entry<K, V>> entries;
        
        UnmodifiableEntries(final Collection<Map.Entry<K, V>> entries) {
            this.entries = entries;
        }
        
        @Override
        protected Collection<Map.Entry<K, V>> delegate() {
            return this.entries;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return Maps.unmodifiableEntryIterator(this.entries.iterator());
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
    
    static class UnmodifiableEntrySet<K, V> extends UnmodifiableEntries<K, V> implements Set<Map.Entry<K, V>>
    {
        UnmodifiableEntrySet(final Set<Map.Entry<K, V>> set) {
            super(set);
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
    
    @GwtIncompatible("NavigableMap")
    static class UnmodifiableNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V>, Serializable
    {
        private final NavigableMap<K, V> delegate;
        private transient UnmodifiableNavigableMap<K, V> descendingMap;
        
        UnmodifiableNavigableMap(final NavigableMap<K, V> delegate) {
            this.delegate = delegate;
        }
        
        UnmodifiableNavigableMap(final NavigableMap<K, V> delegate, final UnmodifiableNavigableMap<K, V> descendingMap) {
            this.delegate = delegate;
            this.descendingMap = descendingMap;
        }
        
        @Override
        public Entry<K, V> ceilingEntry(final K k) {
            return (Entry<K, V>)unmodifiableOrNull((Entry<Object, Object>)(Entry)this.delegate.ceilingEntry(k));
        }
        
        @Override
        public K ceilingKey(final K k) {
            return this.delegate.ceilingKey(k);
        }
        
        @Override
        protected SortedMap<K, V> delegate() {
            return Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>)this.delegate);
        }
        
        @Override
        public NavigableSet<K> descendingKeySet() {
            return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            UnmodifiableNavigableMap<K, V> descendingMap = this.descendingMap;
            if (descendingMap == null) {
                descendingMap = new UnmodifiableNavigableMap<K, V>(this.delegate.descendingMap(), this);
                this.descendingMap = descendingMap;
            }
            return descendingMap;
        }
        
        @Override
        public Entry<K, V> firstEntry() {
            return (Entry<K, V>)unmodifiableOrNull((Entry<Object, Object>)(Entry)this.delegate.firstEntry());
        }
        
        @Override
        public Entry<K, V> floorEntry(final K k) {
            return (Entry<K, V>)unmodifiableOrNull((Entry<Object, Object>)(Entry)this.delegate.floorEntry(k));
        }
        
        @Override
        public K floorKey(final K k) {
            return this.delegate.floorKey(k);
        }
        
        @Override
        public NavigableMap<K, V> headMap(final K k, final boolean b) {
            return Maps.unmodifiableNavigableMap(this.delegate.headMap(k, b));
        }
        
        @Override
        public SortedMap<K, V> headMap(final K k) {
            return this.headMap(k, false);
        }
        
        @Override
        public Entry<K, V> higherEntry(final K k) {
            return (Entry<K, V>)unmodifiableOrNull((Entry<Object, Object>)(Entry)this.delegate.higherEntry(k));
        }
        
        @Override
        public K higherKey(final K k) {
            return this.delegate.higherKey(k);
        }
        
        @Override
        public Set<K> keySet() {
            return this.navigableKeySet();
        }
        
        @Override
        public Entry<K, V> lastEntry() {
            return (Entry<K, V>)unmodifiableOrNull((Entry<Object, Object>)(Entry)this.delegate.lastEntry());
        }
        
        @Override
        public Entry<K, V> lowerEntry(final K k) {
            return (Entry<K, V>)unmodifiableOrNull((Entry<Object, Object>)(Entry)this.delegate.lowerEntry(k));
        }
        
        @Override
        public K lowerKey(final K k) {
            return this.delegate.lowerKey(k);
        }
        
        @Override
        public NavigableSet<K> navigableKeySet() {
            return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
        }
        
        @Override
        public final Entry<K, V> pollFirstEntry() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public final Entry<K, V> pollLastEntry() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
            return Maps.unmodifiableNavigableMap(this.delegate.subMap(k, b, i, b2));
        }
        
        @Override
        public SortedMap<K, V> subMap(final K k, final K i) {
            return this.subMap(k, true, i, false);
        }
        
        @Override
        public NavigableMap<K, V> tailMap(final K k, final boolean b) {
            return Maps.unmodifiableNavigableMap(this.delegate.tailMap(k, b));
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K k) {
            return this.tailMap(k, true);
        }
    }
    
    static class ValueDifferenceImpl<V> implements ValueDifference<V>
    {
        private final V left;
        private final V right;
        
        private ValueDifferenceImpl(@Nullable final V left, @Nullable final V right) {
            this.left = left;
            this.right = right;
        }
        
        static <V> ValueDifference<V> create(@Nullable final V v, @Nullable final V v2) {
            return new ValueDifferenceImpl<V>(v, v2);
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof ValueDifference)) {
                return false;
            }
            final ValueDifference valueDifference = (ValueDifference)o;
            if (Objects.equal(this.left, valueDifference.leftValue()) && Objects.equal(this.right, valueDifference.rightValue())) {
                b = true;
            }
            return b;
        }
        
        @Override
        public int hashCode() {
            return Objects.hashCode(this.left, this.right);
        }
        
        @Override
        public V leftValue() {
            return this.left;
        }
        
        @Override
        public V rightValue() {
            return this.right;
        }
        
        @Override
        public String toString() {
            return "(" + this.left + ", " + this.right + ")";
        }
    }
    
    static class Values<K, V> extends AbstractCollection<V>
    {
        @Weak
        final Map<K, V> map;
        
        Values(final Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return this.map().containsValue(o);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public Iterator<V> iterator() {
            return Maps.valueIterator(this.map().entrySet().iterator());
        }
        
        final Map<K, V> map() {
            return this.map;
        }
        
        @Override
        public boolean remove(final Object o) {
            try {
                return super.remove(o);
            }
            catch (final UnsupportedOperationException ex) {
                for (final Map.Entry<K, Object> entry : this.map().entrySet()) {
                    if (Objects.equal(o, entry.getValue())) {
                        this.map().remove(entry.getKey());
                        return true;
                    }
                }
                return false;
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            try {
                return super.removeAll(Preconditions.checkNotNull(collection));
            }
            catch (final UnsupportedOperationException ex) {
                final HashSet<Object> hashSet = Sets.newHashSet();
                for (final Map.Entry<K, Object> entry : this.map().entrySet()) {
                    if (collection.contains(entry.getValue())) {
                        hashSet.add(entry.getKey());
                    }
                }
                return this.map().keySet().removeAll(hashSet);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            try {
                return super.retainAll(Preconditions.checkNotNull(collection));
            }
            catch (final UnsupportedOperationException ex) {
                final HashSet<Object> hashSet = Sets.newHashSet();
                for (final Map.Entry<K, Object> entry : this.map().entrySet()) {
                    if (collection.contains(entry.getValue())) {
                        hashSet.add(entry.getKey());
                    }
                }
                return this.map().keySet().retainAll(hashSet);
            }
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
    }
}
