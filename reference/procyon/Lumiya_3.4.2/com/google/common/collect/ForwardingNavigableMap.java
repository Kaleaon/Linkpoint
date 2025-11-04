// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.NoSuchElementException;
import com.google.common.annotations.Beta;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.Map;
import java.util.NavigableMap;

public abstract class ForwardingNavigableMap<K, V> extends ForwardingSortedMap<K, V> implements NavigableMap<K, V>
{
    protected ForwardingNavigableMap() {
    }
    
    @Override
    public Entry<K, V> ceilingEntry(final K k) {
        return this.delegate().ceilingEntry(k);
    }
    
    @Override
    public K ceilingKey(final K k) {
        return this.delegate().ceilingKey(k);
    }
    
    @Override
    protected abstract NavigableMap<K, V> delegate();
    
    @Override
    public NavigableSet<K> descendingKeySet() {
        return this.delegate().descendingKeySet();
    }
    
    @Override
    public NavigableMap<K, V> descendingMap() {
        return this.delegate().descendingMap();
    }
    
    @Override
    public Entry<K, V> firstEntry() {
        return this.delegate().firstEntry();
    }
    
    @Override
    public Entry<K, V> floorEntry(final K k) {
        return this.delegate().floorEntry(k);
    }
    
    @Override
    public K floorKey(final K k) {
        return this.delegate().floorKey(k);
    }
    
    @Override
    public NavigableMap<K, V> headMap(final K k, final boolean b) {
        return this.delegate().headMap(k, b);
    }
    
    @Override
    public Entry<K, V> higherEntry(final K k) {
        return this.delegate().higherEntry(k);
    }
    
    @Override
    public K higherKey(final K k) {
        return this.delegate().higherKey(k);
    }
    
    @Override
    public Entry<K, V> lastEntry() {
        return this.delegate().lastEntry();
    }
    
    @Override
    public Entry<K, V> lowerEntry(final K k) {
        return this.delegate().lowerEntry(k);
    }
    
    @Override
    public K lowerKey(final K k) {
        return this.delegate().lowerKey(k);
    }
    
    @Override
    public NavigableSet<K> navigableKeySet() {
        return this.delegate().navigableKeySet();
    }
    
    @Override
    public Entry<K, V> pollFirstEntry() {
        return this.delegate().pollFirstEntry();
    }
    
    @Override
    public Entry<K, V> pollLastEntry() {
        return this.delegate().pollLastEntry();
    }
    
    protected Entry<K, V> standardCeilingEntry(final K k) {
        return this.tailMap(k, true).firstEntry();
    }
    
    protected K standardCeilingKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.ceilingEntry((K)k));
    }
    
    @Beta
    protected NavigableSet<K> standardDescendingKeySet() {
        return this.descendingMap().navigableKeySet();
    }
    
    protected Entry<K, V> standardFirstEntry() {
        return (Entry<K, V>)Iterables.getFirst((Iterable<? extends Map.Entry>)this.entrySet(), (Map.Entry)null);
    }
    
    protected K standardFirstKey() {
        final Entry<K, V> firstEntry = this.firstEntry();
        if (firstEntry != null) {
            return firstEntry.getKey();
        }
        throw new NoSuchElementException();
    }
    
    protected Entry<K, V> standardFloorEntry(final K k) {
        return this.headMap(k, true).lastEntry();
    }
    
    protected K standardFloorKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.floorEntry((K)k));
    }
    
    protected SortedMap<K, V> standardHeadMap(final K k) {
        return this.headMap(k, false);
    }
    
    protected Entry<K, V> standardHigherEntry(final K k) {
        return this.tailMap(k, false).firstEntry();
    }
    
    protected K standardHigherKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.higherEntry((K)k));
    }
    
    protected Entry<K, V> standardLastEntry() {
        return (Entry<K, V>)Iterables.getFirst(this.descendingMap().entrySet(), (Map.Entry)null);
    }
    
    protected K standardLastKey() {
        final Entry<K, V> lastEntry = this.lastEntry();
        if (lastEntry != null) {
            return lastEntry.getKey();
        }
        throw new NoSuchElementException();
    }
    
    protected Entry<K, V> standardLowerEntry(final K k) {
        return this.headMap(k, false).lastEntry();
    }
    
    protected K standardLowerKey(final K k) {
        return Maps.keyOrNull((Entry<K, ?>)this.lowerEntry((K)k));
    }
    
    protected Entry<K, V> standardPollFirstEntry() {
        return (Entry<K, V>)Iterators.pollNext((Iterator<Map.Entry>)this.entrySet().iterator());
    }
    
    protected Entry<K, V> standardPollLastEntry() {
        return (Entry)Iterators.pollNext(this.descendingMap().entrySet().iterator());
    }
    
    @Override
    protected SortedMap<K, V> standardSubMap(final K k, final K i) {
        return this.subMap(k, true, i, false);
    }
    
    protected SortedMap<K, V> standardTailMap(final K k) {
        return this.tailMap(k, true);
    }
    
    @Override
    public NavigableMap<K, V> subMap(final K k, final boolean b, final K i, final boolean b2) {
        return this.delegate().subMap(k, b, i, b2);
    }
    
    @Override
    public NavigableMap<K, V> tailMap(final K k, final boolean b) {
        return this.delegate().tailMap(k, b);
    }
    
    @Beta
    protected class StandardDescendingMap extends DescendingMap<K, V>
    {
        public StandardDescendingMap() {
        }
        
        protected Iterator<Entry<K, V>> entryIterator() {
            return new Iterator<Entry<K, V>>() {
                private Entry<K, V> nextOrNull = StandardDescendingMap.this.forward().lastEntry();
                private Entry<K, V> toRemove = null;
                
                @Override
                public boolean hasNext() {
                    return this.nextOrNull != null;
                }
                
                @Override
                public Entry<K, V> next() {
                    Label_0047: {
                        if (!this.hasNext()) {
                            break Label_0047;
                        }
                        try {
                            return this.nextOrNull;
                            throw new NoSuchElementException();
                        }
                        finally {
                            this.toRemove = this.nextOrNull;
                            this.nextOrNull = StandardDescendingMap.this.forward().lowerEntry(this.nextOrNull.getKey());
                        }
                    }
                }
                
                @Override
                public void remove() {
                    CollectPreconditions.checkRemove(this.toRemove != null);
                    StandardDescendingMap.this.forward().remove(this.toRemove.getKey());
                    this.toRemove = null;
                }
            };
        }
        
        @Override
        NavigableMap<K, V> forward() {
            return ForwardingNavigableMap.this;
        }
    }
    
    @Beta
    protected class StandardNavigableKeySet extends NavigableKeySet<K, V>
    {
        public StandardNavigableKeySet() {
            super(ForwardingNavigableMap.this);
        }
    }
}
