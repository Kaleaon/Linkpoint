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
/* loaded from: classes.dex */
final class Platform {
    private Platform() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> SortedMap<K, V> mapsAsMapSortedSet(SortedSet<K> sortedSet, Function<? super K, V> function) {
        return !(sortedSet instanceof NavigableSet) ? Maps.asMapSortedIgnoreNavigable(sortedSet, function) : Maps.asMap((NavigableSet) sortedSet, (Function) function);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> SortedMap<K, V> mapsFilterSortedMap(SortedMap<K, V> sortedMap, Predicate<? super Map.Entry<K, V>> predicate) {
        return !(sortedMap instanceof NavigableMap) ? Maps.filterSortedIgnoreNavigable(sortedMap, predicate) : Maps.filterEntries((NavigableMap) sortedMap, (Predicate) predicate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(SortedMap<K, V1> sortedMap, Maps.EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        return !(sortedMap instanceof NavigableMap) ? Maps.transformEntriesIgnoreNavigable(sortedMap, entryTransformer) : Maps.transformEntries((NavigableMap) sortedMap, (Maps.EntryTransformer) entryTransformer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> T[] newArray(T[] tArr, int i) {
        return (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <E> Queue<E> newFastestQueue(int i) {
        return new ArrayDeque(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <E> SortedSet<E> setsFilterSortedSet(SortedSet<E> sortedSet, Predicate<? super E> predicate) {
        return !(sortedSet instanceof NavigableSet) ? Sets.filterSortedIgnoreNavigable(sortedSet, predicate) : Sets.filter((NavigableSet) sortedSet, (Predicate) predicate);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.google.common.collect.MapMaker] */
    public static MapMaker tryWeakKeys(MapMaker mapMaker) {
        return mapMaker.weakKeys();
    }
}
