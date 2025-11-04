// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.Collection;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractMultimap<K, V> implements Multimap<K, V>
{
    private transient Map<K, Collection<V>> asMap;
    private transient Collection<Map.Entry<K, V>> entries;
    private transient Set<K> keySet;
    private transient Multiset<K> keys;
    private transient Collection<V> values;
    
    @Override
    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> asMap = this.asMap;
        if (asMap == null) {
            asMap = this.createAsMap();
            this.asMap = asMap;
        }
        return asMap;
    }
    
    @Override
    public boolean containsEntry(@Nullable final Object o, @Nullable final Object o2) {
        final Collection collection = this.asMap().get(o);
        return collection != null && collection.contains(o2);
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        final Iterator<Collection<V>> iterator = this.asMap().values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().contains(o)) {
                return true;
            }
        }
        return false;
    }
    
    abstract Map<K, Collection<V>> createAsMap();
    
    Collection<Map.Entry<K, V>> createEntries() {
        if (!(this instanceof SetMultimap)) {
            return (Collection<Map.Entry<K, V>>)new Entries();
        }
        return (Collection<Map.Entry<K, V>>)new EntrySet();
    }
    
    Set<K> createKeySet() {
        return (Set<K>)new Maps.KeySet((Map<Object, Object>)this.asMap());
    }
    
    Multiset<K> createKeys() {
        return (Multiset<K>)new Multimaps.Keys((Multimap<Object, Object>)this);
    }
    
    Collection<V> createValues() {
        return new Values();
    }
    
    @Override
    public Collection<Map.Entry<K, V>> entries() {
        Collection<Map.Entry<K, V>> entries = this.entries;
        if (entries == null) {
            entries = this.createEntries();
            this.entries = entries;
        }
        return entries;
    }
    
    abstract Iterator<Map.Entry<K, V>> entryIterator();
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return Multimaps.equalsImpl(this, o);
    }
    
    @Override
    public int hashCode() {
        return this.asMap().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        boolean b = false;
        if (this.size() == 0) {
            b = true;
        }
        return b;
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
    public Multiset<K> keys() {
        Multiset<K> keys = this.keys;
        if (keys == null) {
            keys = this.createKeys();
            this.keys = keys;
        }
        return keys;
    }
    
    @Override
    public boolean put(@Nullable final K k, @Nullable final V v) {
        return this.get(k).add(v);
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
    public boolean putAll(@Nullable final K k, final Iterable<? extends V> iterable) {
        final boolean b = true;
        boolean b2 = true;
        Preconditions.checkNotNull(iterable);
        if (!(iterable instanceof Collection)) {
            final Iterator iterator = iterable.iterator();
            if (!iterator.hasNext() || !Iterators.addAll(this.get(k), iterator)) {
                b2 = false;
            }
            return b2;
        }
        final Collection collection = (Collection)iterable;
        if (!collection.isEmpty()) {
            final boolean b3 = b;
            if (this.get(k).addAll(collection)) {
                return b3;
            }
        }
        return false;
    }
    
    @Override
    public boolean remove(@Nullable final Object o, @Nullable final Object o2) {
        final Collection collection = this.asMap().get(o);
        return collection != null && collection.remove(o2);
    }
    
    @Override
    public Collection<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        Preconditions.checkNotNull(iterable);
        final Collection<V> removeAll = this.removeAll(k);
        this.putAll(k, iterable);
        return removeAll;
    }
    
    @Override
    public String toString() {
        return this.asMap().toString();
    }
    
    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entries().iterator());
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
    
    private class Entries extends Multimaps.Entries<K, V>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return AbstractMultimap.this.entryIterator();
        }
        
        @Override
        Multimap<K, V> multimap() {
            return AbstractMultimap.this;
        }
    }
    
    private class EntrySet extends Entries implements Set<Map.Entry<K, V>>
    {
        @Override
        public boolean equals(@Nullable final Object o) {
            return Sets.equalsImpl(this, o);
        }
        
        @Override
        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }
    }
    
    class Values extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            AbstractMultimap.this.clear();
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return AbstractMultimap.this.containsValue(o);
        }
        
        @Override
        public Iterator<V> iterator() {
            return AbstractMultimap.this.valueIterator();
        }
        
        @Override
        public int size() {
            return AbstractMultimap.this.size();
        }
    }
}
