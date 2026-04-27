// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
final class RegularImmutableMap<K, V> extends ImmutableMap<K, V>
{
    private static final double MAX_LOAD_FACTOR = 1.2;
    private static final long serialVersionUID = 0L;
    private final transient Entry<K, V>[] entries;
    private final transient int mask;
    private final transient ImmutableMapEntry<K, V>[] table;
    
    private RegularImmutableMap(final Entry<K, V>[] entries, final ImmutableMapEntry<K, V>[] table, final int mask) {
        this.entries = entries;
        this.table = table;
        this.mask = mask;
    }
    
    static void checkNoConflictInKeyBucket(final Object o, final Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> nextInKeyBucket) {
        while (nextInKeyBucket != null) {
            ImmutableMap.checkNoConflict(!o.equals(nextInKeyBucket.getKey()), "key", entry, nextInKeyBucket);
            nextInKeyBucket = nextInKeyBucket.getNextInKeyBucket();
        }
    }
    
    static <K, V> RegularImmutableMap<K, V> fromEntries(final Entry<K, V>... array) {
        return fromEntryArray(array.length, array);
    }
    
    static <K, V> RegularImmutableMap<K, V> fromEntryArray(final int n, final Entry<K, V>[] array) {
        Preconditions.checkPositionIndex(n, ((ImmutableMapEntry[])array).length);
        ImmutableMapEntry<Object, Object>[] entryArray;
        if (n != ((ImmutableMapEntry[])array).length) {
            entryArray = ImmutableMapEntry.createEntryArray(n);
        }
        else {
            entryArray = (ImmutableMapEntry<Object, Object>[])array;
        }
        final int closedTableSize = Hashing.closedTableSize(n, 1.2);
        final ImmutableMapEntry<Object, Object>[] entryArray2 = ImmutableMapEntry.createEntryArray(closedTableSize);
        final int n2 = closedTableSize - 1;
        for (int i = 0; i < n; ++i) {
            final ImmutableMapEntry immutableMapEntry = (ImmutableMapEntry)array[i];
            final K key = immutableMapEntry.getKey();
            final Object value = immutableMapEntry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            final int n3 = Hashing.smear(key.hashCode()) & n2;
            final ImmutableMapEntry<Object, Object> immutableMapEntry2 = entryArray2[n3];
            ImmutableMapEntry immutableMapEntry3;
            if (immutableMapEntry2 != null) {
                immutableMapEntry3 = new ImmutableMapEntry.NonTerminalImmutableMapEntry(key, value, immutableMapEntry2);
            }
            else {
                int n4;
                if (immutableMapEntry instanceof ImmutableMapEntry && immutableMapEntry.isReusable()) {
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                if (n4 == 0) {
                    immutableMapEntry3 = new ImmutableMapEntry(key, value);
                }
                else {
                    immutableMapEntry3 = immutableMapEntry;
                }
            }
            entryArray2[n3] = immutableMapEntry3;
            checkNoConflictInKeyBucket(key, entryArray[i] = immutableMapEntry3, immutableMapEntry2);
        }
        return new RegularImmutableMap<K, V>(entryArray, entryArray2, n2);
    }
    
    @Nullable
    static <V> V get(@Nullable final Object o, ImmutableMapEntry<?, V>[] nextInKeyBucket, final int n) {
        if (o != null) {
            for (nextInKeyBucket = nextInKeyBucket[Hashing.smear(o.hashCode()) & n]; nextInKeyBucket != null; nextInKeyBucket = nextInKeyBucket.getNextInKeyBucket()) {
                if (o.equals(nextInKeyBucket.getKey())) {
                    return (V)nextInKeyBucket.getValue();
                }
            }
            return null;
        }
        return null;
    }
    
    @Override
    ImmutableSet<Entry<K, V>> createEntrySet() {
        return (ImmutableSet<Entry<K, V>>)new ImmutableMapEntrySet.RegularEntrySet((ImmutableMap<Object, Object>)this, (Entry<Object, Object>[])this.entries);
    }
    
    @Override
    public V get(@Nullable final Object o) {
        return get(o, this.table, this.mask);
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public int size() {
        return this.entries.length;
    }
}
