package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import java.util.Map;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible(emulated = true, serializable = true)
/* loaded from: classes.dex */
public final class RegularImmutableMap<K, V> extends ImmutableMap<K, V> {
    private static final double MAX_LOAD_FACTOR = 1.2d;
    private static final long serialVersionUID = 0;
    private final transient Map.Entry<K, V>[] entries;
    private final transient int mask;
    private final transient ImmutableMapEntry<K, V>[] table;

    private RegularImmutableMap(Map.Entry<K, V>[] entryArr, ImmutableMapEntry<K, V>[] immutableMapEntryArr, int i) {
        this.entries = entryArr;
        this.table = immutableMapEntryArr;
        this.mask = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void checkNoConflictInKeyBucket(Object obj, Map.Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> immutableMapEntry) {
        while (immutableMapEntry != null) {
            checkNoConflict(!obj.equals(immutableMapEntry.getKey()), "key", entry, immutableMapEntry);
            immutableMapEntry = immutableMapEntry.getNextInKeyBucket();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> RegularImmutableMap<K, V> fromEntries(Map.Entry<K, V>... entryArr) {
        return fromEntryArray(entryArr.length, entryArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> RegularImmutableMap<K, V> fromEntryArray(int i, Map.Entry<K, V>[] entryArr) {
        Preconditions.checkPositionIndex(i, entryArr.length);
        ImmutableMapEntry[] createEntryArray = i != entryArr.length ? ImmutableMapEntry.createEntryArray(i) : entryArr;
        int closedTableSize = Hashing.closedTableSize(i, MAX_LOAD_FACTOR);
        ImmutableMapEntry[] createEntryArray2 = ImmutableMapEntry.createEntryArray(closedTableSize);
        int i2 = closedTableSize - 1;
        for (int i3 = 0; i3 < i; i3++) {
            Map.Entry<K, V> entry = entryArr[i3];
            K key = entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int smear = Hashing.smear(key.hashCode()) & i2;
            ImmutableMapEntry immutableMapEntry = createEntryArray2[smear];
            ImmutableMapEntry nonTerminalImmutableMapEntry = immutableMapEntry != null ? new ImmutableMapEntry.NonTerminalImmutableMapEntry(key, value, immutableMapEntry) : !((entry instanceof ImmutableMapEntry) && ((ImmutableMapEntry) entry).isReusable()) ? new ImmutableMapEntry(key, value) : (ImmutableMapEntry) entry;
            createEntryArray2[smear] = nonTerminalImmutableMapEntry;
            createEntryArray[i3] = nonTerminalImmutableMapEntry;
            checkNoConflictInKeyBucket(key, nonTerminalImmutableMapEntry, immutableMapEntry);
        }
        return new RegularImmutableMap<>(createEntryArray, createEntryArray2, i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Nullable
    public static <V> V get(@Nullable Object obj, ImmutableMapEntry<?, V>[] immutableMapEntryArr, int i) {
        if (obj != null) {
            for (ImmutableMapEntry<?, V> immutableMapEntry = immutableMapEntryArr[Hashing.smear(obj.hashCode()) & i]; immutableMapEntry != null; immutableMapEntry = immutableMapEntry.getNextInKeyBucket()) {
                if (obj.equals(immutableMapEntry.getKey())) {
                    return immutableMapEntry.getValue();
                }
            }
            return null;
        }
        return null;
    }

    @Override // com.google.common.collect.ImmutableMap
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return new ImmutableMapEntrySet.RegularEntrySet(this, this.entries);
    }

    @Override // com.google.common.collect.ImmutableMap, java.util.Map
    public V get(@Nullable Object obj) {
        return (V) get(obj, this.table, this.mask);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableMap
    public boolean isPartialView() {
        return false;
    }

    @Override // java.util.Map
    public int size() {
        return this.entries.length;
    }
}
