// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Set;
import java.util.SortedMap;
import java.util.NoSuchElementException;
import java.util.NavigableSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.NavigableMap;

abstract class AbstractNavigableMap<K, V> extends IteratorBasedAbstractMap<K, V> implements NavigableMap<K, V>
{
    @Nullable
    @Override
    public Entry<K, V> ceilingEntry(final K k) {
        return this.tailMap(k, true).firstEntry();
    }
    
    @Override
    public K ceilingKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.ceilingEntry((K)k));
    }
    
    abstract Iterator<Entry<K, V>> descendingEntryIterator();
    
    @Override
    public NavigableSet<K> descendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }
    
    @Override
    public NavigableMap<K, V> descendingMap() {
        return new DescendingMap();
    }
    
    @Nullable
    @Override
    public Entry<K, V> firstEntry() {
        return (Entry<K, V>)Iterators.getNext((Iterator<? extends Map.Entry>)this.entryIterator(), (Map.Entry)null);
    }
    
    @Override
    public K firstKey() {
        final Entry<K, V> firstEntry = this.firstEntry();
        if (firstEntry != null) {
            return firstEntry.getKey();
        }
        throw new NoSuchElementException();
    }
    
    @Nullable
    @Override
    public Entry<K, V> floorEntry(final K k) {
        return this.headMap(k, true).lastEntry();
    }
    
    @Override
    public K floorKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.floorEntry((K)k));
    }
    
    @Nullable
    @Override
    public abstract V get(@Nullable final Object p0);
    
    @Override
    public SortedMap<K, V> headMap(final K k) {
        return (SortedMap<K, V>)this.headMap(k, false);
    }
    
    @Nullable
    @Override
    public Entry<K, V> higherEntry(final K k) {
        return this.tailMap(k, false).firstEntry();
    }
    
    @Override
    public K higherKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.higherEntry((K)k));
    }
    
    @Override
    public Set<K> keySet() {
        return this.navigableKeySet();
    }
    
    @Nullable
    @Override
    public Entry<K, V> lastEntry() {
        return (Entry<K, V>)Iterators.getNext((Iterator<? extends Map.Entry>)this.descendingEntryIterator(), (Map.Entry)null);
    }
    
    @Override
    public K lastKey() {
        final Entry<K, V> lastEntry = this.lastEntry();
        if (lastEntry != null) {
            return lastEntry.getKey();
        }
        throw new NoSuchElementException();
    }
    
    @Nullable
    @Override
    public Entry<K, V> lowerEntry(final K k) {
        return this.headMap(k, false).lastEntry();
    }
    
    @Override
    public K lowerKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.lowerEntry((K)k));
    }
    
    @Override
    public NavigableSet<K> navigableKeySet() {
        return new Maps.NavigableKeySet<K, Object>(this);
    }
    
    @Nullable
    @Override
    public Entry<K, V> pollFirstEntry() {
        return (Entry<K, V>)Iterators.pollNext((Iterator<Map.Entry>)this.entryIterator());
    }
    
    @Nullable
    @Override
    public Entry<K, V> pollLastEntry() {
        return (Entry<K, V>)Iterators.pollNext((Iterator<Map.Entry>)this.descendingEntryIterator());
    }
    
    @Override
    public SortedMap<K, V> subMap(final K k, final K i) {
        return (SortedMap<K, V>)this.subMap(k, true, i, false);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K k) {
        return (SortedMap<K, V>)this.tailMap(k, true);
    }
    
    private final class DescendingMap extends Maps.DescendingMap<K, V>
    {
        @Override
        Iterator<Entry<K, V>> entryIterator() {
            return AbstractNavigableMap.this.descendingEntryIterator();
        }
        
        @Override
        NavigableMap<K, V> forward() {
            return AbstractNavigableMap.this;
        }
    }
}
