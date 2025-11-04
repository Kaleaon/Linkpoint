// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import com.google.common.base.Function;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.EnumMap;
import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableMap<K, V> implements Map<K, V>, Serializable
{
    static final Entry<?, ?>[] EMPTY_ENTRY_ARRAY;
    private transient ImmutableSet<Entry<K, V>> entrySet;
    private transient ImmutableSet<K> keySet;
    private transient ImmutableSetMultimap<K, V> multimapView;
    private transient ImmutableCollection<V> values;
    
    static {
        EMPTY_ENTRY_ARRAY = new Entry[0];
    }
    
    ImmutableMap() {
    }
    
    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }
    
    static void checkNoConflict(final boolean b, final String str, final Entry<?, ?> obj, final Entry<?, ?> obj2) {
        if (b) {
            return;
        }
        throw new IllegalArgumentException("Multiple entries with same " + str + ": " + obj + " and " + obj2);
    }
    
    @Beta
    public static <K, V> ImmutableMap<K, V> copyOf(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
        final Entry[] array = (Entry[])Iterables.toArray((Iterable<? extends Entry>)iterable, (Entry[])ImmutableMap.EMPTY_ENTRY_ARRAY);
        switch (array.length) {
            default: {
                return (ImmutableMap<K, V>)RegularImmutableMap.fromEntries((Entry<Object, Object>[])array);
            }
            case 0: {
                return of();
            }
            case 1: {
                final Entry entry = array[0];
                return of((K)entry.getKey(), (V)entry.getValue());
            }
        }
    }
    
    public static <K, V> ImmutableMap<K, V> copyOf(final Map<? extends K, ? extends V> map) {
        if (map instanceof ImmutableMap && !(map instanceof ImmutableSortedMap)) {
            final ImmutableMap immutableMap = (ImmutableMap)map;
            if (!immutableMap.isPartialView()) {
                return immutableMap;
            }
        }
        else if (map instanceof EnumMap) {
            return copyOfEnumMap((EnumMap<K, ? extends V>)map);
        }
        return copyOf((Iterable<? extends Entry<? extends K, ? extends V>>)map.entrySet());
    }
    
    private static <K extends Enum<K>, V> ImmutableMap<K, V> copyOfEnumMap(final EnumMap<K, ? extends V> m) {
        final EnumMap enumMap = new EnumMap((EnumMap<K, ? extends V>)m);
        for (final Entry<Object, V> entry : enumMap.entrySet()) {
            CollectPreconditions.checkEntryNotNull(entry.getKey(), entry.getValue());
        }
        return ImmutableEnumMap.asImmutable((EnumMap<K, V>)enumMap);
    }
    
    static <K, V> ImmutableMapEntry<K, V> entryOf(final K k, final V v) {
        return new ImmutableMapEntry<K, V>(k, v);
    }
    
    public static <K, V> ImmutableMap<K, V> of() {
        return (ImmutableMap<K, V>)ImmutableBiMap.of();
    }
    
    public static <K, V> ImmutableMap<K, V> of(final K k, final V v) {
        return ImmutableBiMap.of(k, v);
    }
    
    public static <K, V> ImmutableMap<K, V> of(final K k, final V v, final K i, final V v2) {
        return (ImmutableMap<K, V>)RegularImmutableMap.fromEntries(entryOf(k, v), entryOf(i, v2));
    }
    
    public static <K, V> ImmutableMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3) {
        return (ImmutableMap<K, V>)RegularImmutableMap.fromEntries(entryOf(k, v), entryOf(i, v2), entryOf(j, v3));
    }
    
    public static <K, V> ImmutableMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4) {
        return (ImmutableMap<K, V>)RegularImmutableMap.fromEntries(entryOf(k, v), entryOf(i, v2), entryOf(j, v3), entryOf(l, v4));
    }
    
    public static <K, V> ImmutableMap<K, V> of(final K k, final V v, final K i, final V v2, final K j, final V v3, final K l, final V v4, final K m, final V v5) {
        return (ImmutableMap<K, V>)RegularImmutableMap.fromEntries(entryOf(k, v), entryOf(i, v2), entryOf(j, v3), entryOf(l, v4), entryOf(m, v5));
    }
    
    @Beta
    public ImmutableSetMultimap<K, V> asMultimap() {
        if (!this.isEmpty()) {
            ImmutableSetMultimap<K, V> multimapView = this.multimapView;
            if (multimapView == null) {
                multimapView = new ImmutableSetMultimap<K, V>(new MapViewOfValuesAsSingletonSets(), this.size(), null);
                this.multimapView = multimapView;
            }
            return multimapView;
        }
        return ImmutableSetMultimap.of();
    }
    
    @Deprecated
    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.get(o) != null;
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return this.values().contains(o);
    }
    
    abstract ImmutableSet<Entry<K, V>> createEntrySet();
    
    ImmutableSet<K> createKeySet() {
        ImmutableSet<Object> of;
        if (!this.isEmpty()) {
            of = (ImmutableSet<Object>)new ImmutableMapKeySet((ImmutableMap<Object, Object>)this);
        }
        else {
            of = ImmutableSet.of();
        }
        return (ImmutableSet<K>)of;
    }
    
    @Override
    public ImmutableSet<Entry<K, V>> entrySet() {
        ImmutableSet<Entry<K, V>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = this.createEntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return Maps.equalsImpl(this, o);
    }
    
    @Override
    public abstract V get(@Nullable final Object p0);
    
    @Override
    public int hashCode() {
        return Sets.hashCodeImpl(this.entrySet());
    }
    
    @Override
    public boolean isEmpty() {
        boolean b = false;
        if (this.size() == 0) {
            b = true;
        }
        return b;
    }
    
    boolean isHashCodeFast() {
        return false;
    }
    
    abstract boolean isPartialView();
    
    UnmodifiableIterator<K> keyIterator() {
        return new UnmodifiableIterator<K>() {
            final /* synthetic */ UnmodifiableIterator val$entryIterator = ImmutableMap.this.entrySet().iterator();
            
            @Override
            public boolean hasNext() {
                return this.val$entryIterator.hasNext();
            }
            
            @Override
            public K next() {
                return ((Entry)this.val$entryIterator.next()).getKey();
            }
        };
    }
    
    @Override
    public ImmutableSet<K> keySet() {
        ImmutableSet<K> keySet = this.keySet;
        if (keySet == null) {
            keySet = this.createKeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @Deprecated
    @Override
    public final V put(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final void putAll(final Map<? extends K, ? extends V> map) {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final V remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return Maps.toStringImpl(this);
    }
    
    @Override
    public ImmutableCollection<V> values() {
        ImmutableCollection<V> values = this.values;
        if (values == null) {
            values = (ImmutableCollection<V>)new ImmutableMapValues((ImmutableMap<Object, Object>)this);
            this.values = values;
        }
        return values;
    }
    
    Object writeReplace() {
        return new SerializedForm(this);
    }
    
    public static class Builder<K, V>
    {
        ImmutableMapEntry<K, V>[] entries;
        boolean entriesUsed;
        int size;
        Comparator<? super V> valueComparator;
        
        public Builder() {
            this(4);
        }
        
        Builder(final int n) {
            this.entries = new ImmutableMapEntry[n];
            this.size = 0;
            this.entriesUsed = false;
        }
        
        private void ensureCapacity(final int n) {
            if (n > this.entries.length) {
                this.entries = ObjectArrays.arraysCopyOf(this.entries, ImmutableCollection.Builder.expandedCapacity(this.entries.length, n));
                this.entriesUsed = false;
            }
        }
        
        public ImmutableMap<K, V> build() {
            switch (this.size) {
                default: {
                    if (this.valueComparator != null) {
                        if (this.entriesUsed) {
                            this.entries = ObjectArrays.arraysCopyOf(this.entries, this.size);
                        }
                        Arrays.sort(this.entries, 0, this.size, (Comparator<? super ImmutableMapEntry<K, V>>)Ordering.from(this.valueComparator).onResultOf((Function<Entry<?, ? extends V>, ? extends V>)Maps.valueFunction()));
                    }
                    this.entriesUsed = (this.size == this.entries.length);
                    return RegularImmutableMap.fromEntryArray(this.size, this.entries);
                }
                case 0: {
                    return ImmutableMap.of();
                }
                case 1: {
                    return ImmutableMap.of(this.entries[0].getKey(), this.entries[0].getValue());
                }
            }
        }
        
        @Beta
        public Builder<K, V> orderEntriesByValue(final Comparator<? super V> comparator) {
            Preconditions.checkState(this.valueComparator == null, (Object)"valueComparator was already set");
            this.valueComparator = Preconditions.checkNotNull(comparator, (Object)"valueComparator");
            return this;
        }
        
        public Builder<K, V> put(final K k, final V v) {
            this.ensureCapacity(this.size + 1);
            this.entries[this.size++] = ImmutableMap.entryOf(k, v);
            return this;
        }
        
        public Builder<K, V> put(final Entry<? extends K, ? extends V> entry) {
            return (Builder<K, V>)this.put(entry.getKey(), entry.getValue());
        }
        
        @Beta
        public Builder<K, V> putAll(final Iterable<? extends Entry<? extends K, ? extends V>> iterable) {
            if (iterable instanceof Collection) {
                this.ensureCapacity(((Collection)iterable).size() + this.size);
            }
            final Iterator iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.put((Entry<? extends K, ? extends V>)iterator.next());
            }
            return this;
        }
        
        public Builder<K, V> putAll(final Map<? extends K, ? extends V> map) {
            return this.putAll((Iterable<? extends Entry<? extends K, ? extends V>>)map.entrySet());
        }
    }
    
    abstract static class IteratorBasedImmutableMap<K, V> extends ImmutableMap<K, V>
    {
        @Override
        ImmutableSet<Entry<K, V>> createEntrySet() {
            return (ImmutableSet<Entry<K, V>>)new EntrySetImpl();
        }
        
        abstract UnmodifiableIterator<Entry<K, V>> entryIterator();
        
        class EntrySetImpl extends ImmutableMapEntrySet<K, V>
        {
            @Override
            public UnmodifiableIterator<Entry<K, V>> iterator() {
                return IteratorBasedImmutableMap.this.entryIterator();
            }
            
            @Override
            ImmutableMap<K, V> map() {
                return IteratorBasedImmutableMap.this;
            }
        }
    }
    
    private final class MapViewOfValuesAsSingletonSets extends IteratorBasedImmutableMap<K, ImmutableSet<V>>
    {
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return ImmutableMap.this.containsKey(o);
        }
        
        @Override
        UnmodifiableIterator<Entry<K, ImmutableSet<V>>> entryIterator() {
            return new UnmodifiableIterator<Entry<K, ImmutableSet<V>>>() {
                final /* synthetic */ Iterator val$backingIterator = ImmutableMap.this.entrySet().iterator();
                
                @Override
                public boolean hasNext() {
                    return this.val$backingIterator.hasNext();
                }
                
                @Override
                public Entry<K, ImmutableSet<V>> next() {
                    return new AbstractMapEntry<K, ImmutableSet<V>>() {
                        final /* synthetic */ Entry val$backingEntry = (Entry)ImmutableMap$MapViewOfValuesAsSingletonSets$1.this.val$backingIterator.next();
                        
                        @Override
                        public K getKey() {
                            return this.val$backingEntry.getKey();
                        }
                        
                        @Override
                        public ImmutableSet<V> getValue() {
                            return ImmutableSet.of(this.val$backingEntry.getValue());
                        }
                    };
                }
            };
        }
        
        @Override
        public ImmutableSet<V> get(@Nullable final Object o) {
            final ImmutableSet<V> set = null;
            final V value = ImmutableMap.this.get(o);
            ImmutableSet<V> of = set;
            if (value != null) {
                of = ImmutableSet.of(value);
            }
            return of;
        }
        
        @Override
        public int hashCode() {
            return ImmutableMap.this.hashCode();
        }
        
        @Override
        boolean isHashCodeFast() {
            return ImmutableMap.this.isHashCodeFast();
        }
        
        @Override
        boolean isPartialView() {
            return ImmutableMap.this.isPartialView();
        }
        
        @Override
        public ImmutableSet<K> keySet() {
            return ImmutableMap.this.keySet();
        }
        
        @Override
        public int size() {
            return ImmutableMap.this.size();
        }
    }
    
    static class SerializedForm implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final Object[] keys;
        private final Object[] values;
        
        SerializedForm(final ImmutableMap<?, ?> immutableMap) {
            this.keys = new Object[immutableMap.size()];
            this.values = new Object[immutableMap.size()];
            final Iterator iterator = immutableMap.entrySet().iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Entry<Object, V> entry = iterator.next();
                this.keys[n] = entry.getKey();
                this.values[n] = entry.getValue();
                ++n;
            }
        }
        
        Object createMap(final Builder<Object, Object> builder) {
            for (int i = 0; i < this.keys.length; ++i) {
                builder.put(this.keys[i], this.values[i]);
            }
            return builder.build();
        }
        
        Object readResolve() {
            return this.createMap((Builder<Object, Object>)new Builder(this.keys.length));
        }
    }
}
