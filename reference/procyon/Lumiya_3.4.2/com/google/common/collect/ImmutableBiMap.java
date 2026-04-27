// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.Arrays;
import com.google.common.base.Function;
import java.util.Set;
import java.util.Collection;
import com.google.common.annotations.Beta;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableBiMap<K, V> extends ImmutableMap<K, V> implements BiMap<K, V>
{
    ImmutableBiMap() {
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    @Beta
    public static <K, V> ImmutableBiMap<K, V> copyOf(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
        final Entry[] array = (Entry[])Iterables.toArray((Iterable<? extends Map.Entry>)iterable, (Map.Entry[])ImmutableBiMap.EMPTY_ENTRY_ARRAY);
        switch (array.length) {
            default: {
                return (ImmutableBiMap<K, V>)RegularImmutableBiMap.fromEntries((Entry<Object, Object>[])array);
            }
            case 0: {
                return of();
            }
            case 1: {
                final Entry entry = array[0];
                return of((K)entry.getKey(), entry.getValue());
            }
        }
    }
    
    public static <K, V> ImmutableBiMap<K, V> copyOf(final Map<? extends K, ? extends V> map) {
        if (map instanceof ImmutableBiMap) {
            final ImmutableBiMap immutableBiMap = (ImmutableBiMap)map;
            if (!immutableBiMap.isPartialView()) {
                return immutableBiMap;
            }
        }
        return copyOf((Iterable<? extends Entry<? extends K, ? extends V>>)map.entrySet());
    }
    
    public static <K, V> ImmutableBiMap<K, V> of() {
        return (ImmutableBiMap<K, V>)RegularImmutableBiMap.EMPTY;
    }
    
    public static <K, V> ImmutableBiMap<K, V> of(final K k, final V v) {
        return new SingletonImmutableBiMap<K, V>(k, v);
    }
    
    public static <K, V> ImmutableBiMap<K, V> of(final K k, final V v, final K i, final V v2) {
        return (ImmutableBiMap<K, V>)RegularImmutableBiMap.fromEntries(ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2));
    }
    
    public static <K, V> ImmutableBiMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        return (ImmutableBiMap<K, V>)RegularImmutableBiMap.fromEntries(ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2), ImmutableMap.entryOf(j, v3));
    }
    
    public static <K, V> ImmutableBiMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        return (ImmutableBiMap<K, V>)RegularImmutableBiMap.fromEntries(ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2), ImmutableMap.entryOf(j, v3), ImmutableMap.entryOf(l, v4));
    }
    
    public static <K, V> ImmutableBiMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
        return (ImmutableBiMap<K, V>)RegularImmutableBiMap.fromEntries(ImmutableMap.entryOf(k, v), ImmutableMap.entryOf(i, v2), ImmutableMap.entryOf(j, v3), ImmutableMap.entryOf(l, v4), ImmutableMap.entryOf(m, v5));
    }
    
    @Deprecated
    @Override
    public V forcePut(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public abstract ImmutableBiMap<V, K> inverse();
    
    @Override
    public ImmutableSet<V> values() {
        return (ImmutableSet<V>)this.inverse().keySet();
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    public static final class Builder<K, V> extends ImmutableMap.Builder<K, V>
    {
        public Builder() {
        }
        
        Builder(final int n) {
            super(n);
        }
        
        public ImmutableBiMap<K, V> build() {
            switch (this.size) {
                default: {
                    if (this.valueComparator != null) {
                        if (this.entriesUsed) {
                            this.entries = (ImmutableMapEntry<K, V>[])ObjectArrays.arraysCopyOf((ImmutableMapEntry<K, V>[])this.entries, this.size);
                        }
                        Arrays.sort(this.entries, 0, this.size, (Comparator<? super ImmutableMapEntry<K, V>>)Ordering.from(this.valueComparator).onResultOf((Function<Entry<?, ? extends V>, ? extends V>)Maps.valueFunction()));
                    }
                    this.entriesUsed = (this.size == this.entries.length);
                    return RegularImmutableBiMap.fromEntryArray(this.size, this.entries);
                }
                case 0: {
                    return ImmutableBiMap.of();
                }
                case 1: {
                    return ImmutableBiMap.of((K)this.entries[0].getKey(), (V)this.entries[0].getValue());
                }
            }
        }
        
        @Beta
        public Builder<K, V> orderEntriesByValue(final Comparator<? super V> comparator) {
            super.orderEntriesByValue(comparator);
            return this;
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
        
        SerializedForm(final ImmutableBiMap<?, ?> immutableBiMap) {
            super(immutableBiMap);
        }
        
        @Override
        Object readResolve() {
            return ((ImmutableMap.SerializedForm)this).createMap(new ImmutableBiMap.Builder<Object, Object>());
        }
    }
}
