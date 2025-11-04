// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V>
{
    static final RegularImmutableBiMap<Object, Object> EMPTY;
    static final double MAX_LOAD_FACTOR = 1.2;
    private final transient Entry<K, V>[] entries;
    private final transient int hashCode;
    private transient ImmutableBiMap<V, K> inverse;
    private final transient ImmutableMapEntry<K, V>[] keyTable;
    private final transient int mask;
    private final transient ImmutableMapEntry<K, V>[] valueTable;
    
    static {
        EMPTY = new RegularImmutableBiMap<Object, Object>(null, null, (Entry<Object, Object>[])ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
    }
    
    private RegularImmutableBiMap(final ImmutableMapEntry<K, V>[] keyTable, final ImmutableMapEntry<K, V>[] valueTable, final Entry<K, V>[] entries, final int mask, final int hashCode) {
        this.keyTable = keyTable;
        this.valueTable = valueTable;
        this.entries = entries;
        this.mask = mask;
        this.hashCode = hashCode;
    }
    
    private static void checkNoConflictInValueBucket(final Object o, final Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> nextInValueBucket) {
        while (nextInValueBucket != null) {
            ImmutableMap.checkNoConflict(!o.equals(nextInValueBucket.getValue()), "value", entry, nextInValueBucket);
            nextInValueBucket = nextInValueBucket.getNextInValueBucket();
        }
    }
    
    static <K, V> RegularImmutableBiMap<K, V> fromEntries(final Entry<K, V>... array) {
        return fromEntryArray(array.length, array);
    }
    
    static <K, V> RegularImmutableBiMap<K, V> fromEntryArray(final int n, final Entry<K, V>[] array) {
        Preconditions.checkPositionIndex(n, ((ImmutableMapEntry[])array).length);
        final int closedTableSize = Hashing.closedTableSize(n, 1.2);
        final int n2 = closedTableSize - 1;
        final ImmutableMapEntry<Object, Object>[] entryArray = ImmutableMapEntry.createEntryArray(closedTableSize);
        final ImmutableMapEntry<Object, Object>[] entryArray2 = ImmutableMapEntry.createEntryArray(closedTableSize);
        ImmutableMapEntry<Object, Object>[] entryArray3;
        if (n != ((ImmutableMapEntry[])array).length) {
            entryArray3 = ImmutableMapEntry.createEntryArray(n);
        }
        else {
            entryArray3 = (ImmutableMapEntry<Object, Object>[])array;
        }
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            final ImmutableMapEntry immutableMapEntry = (ImmutableMapEntry)array[i];
            final Object key = immutableMapEntry.getKey();
            final Object value = immutableMapEntry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            final int hashCode = key.hashCode();
            final int hashCode2 = value.hashCode();
            final int n4 = Hashing.smear(hashCode) & n2;
            final int n5 = Hashing.smear(hashCode2) & n2;
            final ImmutableMapEntry<Object, Object> immutableMapEntry2 = entryArray[n4];
            RegularImmutableMap.checkNoConflictInKeyBucket(key, immutableMapEntry, immutableMapEntry2);
            final ImmutableMapEntry<Object, Object> immutableMapEntry3 = entryArray2[n5];
            checkNoConflictInValueBucket(value, immutableMapEntry, immutableMapEntry3);
            ImmutableMapEntry immutableMapEntry4;
            if (immutableMapEntry3 == null && immutableMapEntry2 == null) {
                int n6;
                if (immutableMapEntry instanceof ImmutableMapEntry && immutableMapEntry.isReusable()) {
                    n6 = 1;
                }
                else {
                    n6 = 0;
                }
                if (n6 == 0) {
                    immutableMapEntry4 = new ImmutableMapEntry(key, value);
                }
                else {
                    immutableMapEntry4 = immutableMapEntry;
                }
            }
            else {
                immutableMapEntry4 = new ImmutableMapEntry.NonTerminalImmutableBiMapEntry(key, value, immutableMapEntry2, immutableMapEntry3);
            }
            entryArray[n4] = immutableMapEntry4;
            entryArray3[i] = (entryArray2[n5] = immutableMapEntry4);
            n3 += (hashCode ^ hashCode2);
        }
        return new RegularImmutableBiMap<K, V>(entryArray, entryArray2, entryArray3, n2, n3);
    }
    
    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        ImmutableSet<Object> of;
        if (!this.isEmpty()) {
            of = (ImmutableSet<Object>)new ImmutableMapEntrySet.RegularEntrySet((ImmutableMap<Object, Object>)this, (Entry<Object, Object>[])this.entries);
        }
        else {
            of = ImmutableSet.of();
        }
        return (ImmutableSet<Entry<K, V>>)of;
    }
    
    @Nullable
    @Override
    public V get(@Nullable final Object o) {
        V value = null;
        if (this.keyTable != null) {
            value = RegularImmutableMap.get(o, this.keyTable, this.mask);
        }
        return value;
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public ImmutableBiMap<V, K> inverse() {
        if (!this.isEmpty()) {
            ImmutableBiMap<V, K> inverse = this.inverse;
            if (inverse == null) {
                inverse = new Inverse();
                this.inverse = inverse;
            }
            return inverse;
        }
        return ImmutableBiMap.of();
    }
    
    @Override
    boolean isHashCodeFast() {
        return true;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int size() {
        return this.entries.length;
    }
    
    private final class Inverse extends ImmutableBiMap<V, K>
    {
        @Override
        ImmutableSet<Entry<V, K>> createEntrySet() {
            return (ImmutableSet<Entry<V, K>>)new InverseEntrySet();
        }
        
        @Override
        public K get(@Nullable final Object o) {
            if (o != null && RegularImmutableBiMap.this.valueTable != null) {
                for (ImmutableMapEntry nextInValueBucket = RegularImmutableBiMap.this.valueTable[Hashing.smear(o.hashCode()) & RegularImmutableBiMap.this.mask]; nextInValueBucket != null; nextInValueBucket = nextInValueBucket.getNextInValueBucket()) {
                    if (o.equals(nextInValueBucket.getValue())) {
                        return (K)nextInValueBucket.getKey();
                    }
                }
                return null;
            }
            return null;
        }
        
        @Override
        public ImmutableBiMap<K, V> inverse() {
            return RegularImmutableBiMap.this;
        }
        
        @Override
        boolean isPartialView() {
            return false;
        }
        
        @Override
        public int size() {
            return this.inverse().size();
        }
        
        @Override
        Object writeReplace() {
            return new InverseSerializedForm(RegularImmutableBiMap.this);
        }
        
        final class InverseEntrySet extends ImmutableMapEntrySet<V, K>
        {
            @Override
            ImmutableList<Entry<V, K>> createAsList() {
                return new ImmutableAsList<Entry<V, K>>() {
                    @Override
                    ImmutableCollection<Entry<V, K>> delegateCollection() {
                        return (ImmutableCollection<Entry<V, K>>)InverseEntrySet.this;
                    }
                    
                    @Override
                    public Entry<V, K> get(final int n) {
                        final Entry entry = RegularImmutableBiMap.this.entries[n];
                        return Maps.immutableEntry((V)entry.getValue(), entry.getKey());
                    }
                };
            }
            
            @Override
            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }
            
            @Override
            boolean isHashCodeFast() {
                return true;
            }
            
            @Override
            public UnmodifiableIterator<Entry<V, K>> iterator() {
                return (UnmodifiableIterator<Entry<V, K>>)this.asList().iterator();
            }
            
            @Override
            ImmutableMap<V, K> map() {
                return Inverse.this;
            }
        }
    }
    
    private static class InverseSerializedForm<K, V> implements Serializable
    {
        private static final long serialVersionUID = 1L;
        private final ImmutableBiMap<K, V> forward;
        
        InverseSerializedForm(final ImmutableBiMap<K, V> forward) {
            this.forward = forward;
        }
        
        Object readResolve() {
            return this.forward.inverse();
        }
    }
}
