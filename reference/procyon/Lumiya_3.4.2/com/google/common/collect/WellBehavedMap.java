// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class WellBehavedMap<K, V> extends ForwardingMap<K, V>
{
    private final Map<K, V> delegate;
    private Set<Entry<K, V>> entrySet;
    
    private WellBehavedMap(final Map<K, V> delegate) {
        this.delegate = delegate;
    }
    
    static <K, V> WellBehavedMap<K, V> wrap(final Map<K, V> map) {
        return new WellBehavedMap<K, V>(map);
    }
    
    @Override
    protected Map<K, V> delegate() {
        return this.delegate;
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            return this.entrySet = (Set<Entry<K, V>>)new EntrySet();
        }
        return entrySet;
    }
    
    private final class EntrySet extends Maps.EntrySet<K, V>
    {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new TransformedIterator<K, Entry<K, V>>(WellBehavedMap.this.keySet().iterator()) {
                @Override
                Entry<K, V> transform(final K k) {
                    return new AbstractMapEntry<K, V>() {
                        @Override
                        public K getKey() {
                            return k;
                        }
                        
                        @Override
                        public V getValue() {
                            return WellBehavedMap.this.get(k);
                        }
                        
                        @Override
                        public V setValue(final V v) {
                            return WellBehavedMap.this.put(k, v);
                        }
                    };
                }
            };
        }
        
        @Override
        Map<K, V> map() {
            return WellBehavedMap.this;
        }
    }
}
