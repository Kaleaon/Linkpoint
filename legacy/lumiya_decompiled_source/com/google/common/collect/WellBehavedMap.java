package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
/* loaded from: classes.dex */
public final class WellBehavedMap<K, V> extends ForwardingMap<K, V> {
    private final Map<K, V> delegate;
    private Set<Map.Entry<K, V>> entrySet;

    /* loaded from: classes.dex */
    private final class EntrySet extends Maps.EntrySet<K, V> {
        private EntrySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new TransformedIterator<K, Map.Entry<K, V>>(WellBehavedMap.this.keySet().iterator()) { // from class: com.google.common.collect.WellBehavedMap.EntrySet.1
                /* JADX INFO: Access modifiers changed from: package-private */
                @Override // com.google.common.collect.TransformedIterator
                public /* bridge */ /* synthetic */ Object transform(Object obj) {
                    return transform((AnonymousClass1) obj);
                }

                /* JADX INFO: Access modifiers changed from: package-private */
                @Override // com.google.common.collect.TransformedIterator
                public Map.Entry<K, V> transform(final K k) {
                    return new AbstractMapEntry<K, V>() { // from class: com.google.common.collect.WellBehavedMap.EntrySet.1.1
                        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
                        public K getKey() {
                            return (K) k;
                        }

                        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
                        public V getValue() {
                            return WellBehavedMap.this.get(k);
                        }

                        /* JADX WARN: Multi-variable type inference failed */
                        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
                        public V setValue(V v) {
                            return (V) WellBehavedMap.this.put(k, v);
                        }
                    };
                }
            };
        }

        @Override // com.google.common.collect.Maps.EntrySet
        Map<K, V> map() {
            return WellBehavedMap.this;
        }
    }

    private WellBehavedMap(Map<K, V> map) {
        this.delegate = map;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> WellBehavedMap<K, V> wrap(Map<K, V> map) {
        return new WellBehavedMap<>(map);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.common.collect.ForwardingMap, com.google.common.collect.ForwardingObject
    public Map<K, V> delegate() {
        return this.delegate;
    }

    @Override // com.google.common.collect.ForwardingMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = this.entrySet;
        if (set == null) {
            EntrySet entrySet = new EntrySet();
            this.entrySet = entrySet;
            return entrySet;
        }
        return set;
    }
}
