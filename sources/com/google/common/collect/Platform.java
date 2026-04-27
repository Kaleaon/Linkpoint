package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible(emulated = true)
final class Platform {
    private Platform() {
    }

    static <K, V> SortedMap<K, V> mapsAsMapSortedSet(SortedSet<K> sortedSet, Function<? super K, V> function) {
        return !(sortedSet instanceof NavigableSet) ? Maps.asMapSortedIgnoreNavigable(sortedSet, function) : Maps.asMap((NavigableSet) sortedSet, function);
    }

    static <K, V> SortedMap<K, V> mapsFilterSortedMap(SortedMap<K, V> sortedMap, Predicate<? super Map.Entry<K, V>> predicate) {
        return !(sortedMap instanceof NavigableMap) ? Maps.filterSortedIgnoreNavigable(sortedMap, predicate) : Maps.filterEntries((NavigableMap) sortedMap, predicate);
    }

    static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(SortedMap<K, V1> sortedMap, Maps.EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return !(sortedMap instanceof NavigableMap) ? Maps.transformEntriesIgnoreNavigable(sortedMap, entryTransformer) : Maps.transformEntries((NavigableMap) sortedMap, entryTransformer);
    }

    static <T> T[] newArray(T[] tArr, int i) {
        return (Object[]) Array.newInstance(tArr.getClass().getComponentType(), i);
    }

    static <E> Queue<E> newFastestQueue(int i) {
        return new ArrayDeque(i);
    }

    static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }

    static <E> SortedSet<E> setsFilterSortedSet(SortedSet<E> sortedSet, Predicate<? super E> predicate) {
        return !(sortedSet instanceof NavigableSet) ? Sets.filterSortedIgnoreNavigable(sortedSet, predicate) : Sets.filter((NavigableSet) sortedSet, predicate);
    }

    static MapMaker tryWeakKeys(MapMaker mapMaker) {
        return mapMaker.weakKeys();
    }
}
