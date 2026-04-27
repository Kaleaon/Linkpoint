// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collections;
import java.util.Set;
import java.util.ArrayDeque;
import java.util.Queue;
import java.lang.reflect.Array;
import java.util.NavigableMap;
import java.util.Map;
import com.google.common.base.Predicate;
import java.util.NavigableSet;
import java.util.SortedMap;
import com.google.common.base.Function;
import java.util.SortedSet;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class Platform
{
    private Platform() {
    }
    
    static <K, V> SortedMap<K, V> mapsAsMapSortedSet(final SortedSet<K> set, final Function<? super K, V> function) {
        Object o;
        if (!(set instanceof NavigableSet)) {
            o = Maps.asMapSortedIgnoreNavigable(set, function);
        }
        else {
            o = Maps.asMap((NavigableSet<Object>)set, (Function<? super Object, V>)function);
        }
        return (SortedMap<K, V>)o;
    }
    
    static <K, V> SortedMap<K, V> mapsFilterSortedMap(final SortedMap<K, V> sortedMap, final Predicate<? super Map.Entry<K, V>> predicate) {
        Object o;
        if (!(sortedMap instanceof NavigableMap)) {
            o = Maps.filterSortedIgnoreNavigable(sortedMap, predicate);
        }
        else {
            o = Maps.filterEntries((NavigableMap<Object, Object>)sortedMap, (Predicate<? super Map.Entry<Object, Object>>)predicate);
        }
        return (SortedMap<K, V>)o;
    }
    
    static <K, V1, V2> SortedMap<K, V2> mapsTransformEntriesSortedMap(final SortedMap<K, V1> sortedMap, final Maps.EntryTransformer<? super K, ? super V1, V2> entryTransformer) {
        Object o;
        if (!(sortedMap instanceof NavigableMap)) {
            o = Maps.transformEntriesIgnoreNavigable(sortedMap, (Maps.EntryTransformer<? super K, ? super V1, V2>)entryTransformer);
        }
        else {
            o = Maps.transformEntries((NavigableMap<Object, Object>)sortedMap, (Maps.EntryTransformer<? super Object, ? super Object, V2>)entryTransformer);
        }
        return (SortedMap<K, V2>)o;
    }
    
    static <T> T[] newArray(final T[] array, final int length) {
        return (T[])Array.newInstance(array.getClass().getComponentType(), length);
    }
    
    static <E> Queue<E> newFastestQueue(final int numElements) {
        return new ArrayDeque<E>(numElements);
    }
    
    static <E> Set<E> newSetFromMap(final Map<E, Boolean> map) {
        return Collections.newSetFromMap(map);
    }
    
    static <E> SortedSet<E> setsFilterSortedSet(final SortedSet<E> set, final Predicate<? super E> predicate) {
        Object o;
        if (!(set instanceof NavigableSet)) {
            o = Sets.filterSortedIgnoreNavigable(set, predicate);
        }
        else {
            o = Sets.filter((NavigableSet<Object>)set, (Predicate<? super Object>)predicate);
        }
        return (SortedSet<E>)o;
    }
    
    static MapMaker tryWeakKeys(final MapMaker mapMaker) {
        return mapMaker.weakKeys();
    }
}
