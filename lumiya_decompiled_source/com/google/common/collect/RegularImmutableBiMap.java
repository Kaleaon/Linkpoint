package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.vr.cardboard.VrSettingsProviderContract;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible(emulated = true, serializable = true)
/* loaded from: classes.dex */
public class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
    static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap<>(null, null, ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
    static final double MAX_LOAD_FACTOR = 1.2d;
    private final transient Map.Entry<K, V>[] entries;
    private final transient int hashCode;
    private transient ImmutableBiMap<V, K> inverse;
    private final transient ImmutableMapEntry<K, V>[] keyTable;
    private final transient int mask;
    private final transient ImmutableMapEntry<K, V>[] valueTable;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class Inverse extends ImmutableBiMap<V, K> {

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
            InverseEntrySet() {
            }

            @Override // com.google.common.collect.ImmutableCollection
            ImmutableList<Map.Entry<V, K>> createAsList() {
                return new ImmutableAsList<Map.Entry<V, K>>() { // from class: com.google.common.collect.RegularImmutableBiMap.Inverse.InverseEntrySet.1
                    @Override // com.google.common.collect.ImmutableAsList
                    ImmutableCollection<Map.Entry<V, K>> delegateCollection() {
                        return InverseEntrySet.this;
                    }

                    @Override // java.util.List
                    public Map.Entry<V, K> get(int i) {
                        Map.Entry entry = RegularImmutableBiMap.this.entries[i];
                        return Maps.immutableEntry(entry.getValue(), entry.getKey());
                    }
                };
            }

            @Override // com.google.common.collect.ImmutableMapEntrySet, com.google.common.collect.ImmutableSet, java.util.Collection, java.util.Set
            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }

            @Override // com.google.common.collect.ImmutableMapEntrySet, com.google.common.collect.ImmutableSet
            boolean isHashCodeFast() {
                return true;
            }

            @Override // com.google.common.collect.ImmutableSet, com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set, java.util.NavigableSet, com.google.common.collect.SortedIterable
            public UnmodifiableIterator<Map.Entry<V, K>> iterator() {
                return (UnmodifiableIterator<Map.Entry<K, V>>) asList().iterator();
            }

            @Override // com.google.common.collect.ImmutableMapEntrySet
            ImmutableMap<V, K> map() {
                return Inverse.this;
            }
        }

        private Inverse() {
        }

        @Override // com.google.common.collect.ImmutableMap
        ImmutableSet<Map.Entry<V, K>> createEntrySet() {
            return new InverseEntrySet();
        }

        @Override // com.google.common.collect.ImmutableMap, java.util.Map
        public K get(@Nullable Object obj) {
            if (obj == null || RegularImmutableBiMap.this.valueTable == null) {
                return null;
            }
            for (ImmutableMapEntry immutableMapEntry = RegularImmutableBiMap.this.valueTable[Hashing.smear(obj.hashCode()) & RegularImmutableBiMap.this.mask]; immutableMapEntry != null; immutableMapEntry = immutableMapEntry.getNextInValueBucket()) {
                if (obj.equals(immutableMapEntry.getValue())) {
                    return immutableMapEntry.getKey();
                }
            }
            return null;
        }

        @Override // com.google.common.collect.ImmutableBiMap, com.google.common.collect.BiMap
        public ImmutableBiMap<K, V> inverse() {
            return RegularImmutableBiMap.this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.collect.ImmutableMap
        public boolean isPartialView() {
            return false;
        }

        @Override // java.util.Map
        public int size() {
            return inverse().size();
        }

        @Override // com.google.common.collect.ImmutableBiMap, com.google.common.collect.ImmutableMap
        Object writeReplace() {
            return new InverseSerializedForm(RegularImmutableBiMap.this);
        }
    }

    /* loaded from: classes.dex */
    private static class InverseSerializedForm<K, V> implements Serializable {
        private static final long serialVersionUID = 1;
        private final ImmutableBiMap<K, V> forward;

        InverseSerializedForm(ImmutableBiMap<K, V> immutableBiMap) {
            this.forward = immutableBiMap;
        }

        Object readResolve() {
            return this.forward.inverse();
        }
    }

    private RegularImmutableBiMap(ImmutableMapEntry<K, V>[] immutableMapEntryArr, ImmutableMapEntry<K, V>[] immutableMapEntryArr2, Map.Entry<K, V>[] entryArr, int i, int i2) {
        this.keyTable = immutableMapEntryArr;
        this.valueTable = immutableMapEntryArr2;
        this.entries = entryArr;
        this.mask = i;
        this.hashCode = i2;
    }

    private static void checkNoConflictInValueBucket(Object obj, Map.Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> immutableMapEntry) {
        while (immutableMapEntry != null) {
            checkNoConflict(!obj.equals(immutableMapEntry.getValue()), VrSettingsProviderContract.SETTING_VALUE_KEY, entry, immutableMapEntry);
            immutableMapEntry = immutableMapEntry.getNextInValueBucket();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> RegularImmutableBiMap<K, V> fromEntries(Map.Entry<K, V>... entryArr) {
        return fromEntryArray(entryArr.length, entryArr);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> RegularImmutableBiMap<K, V> fromEntryArray(int i, Map.Entry<K, V>[] entryArr) {
        ImmutableMapEntry immutableMapEntry;
        Preconditions.checkPositionIndex(i, entryArr.length);
        int closedTableSize = Hashing.closedTableSize(i, MAX_LOAD_FACTOR);
        int i2 = closedTableSize - 1;
        ImmutableMapEntry[] createEntryArray = ImmutableMapEntry.createEntryArray(closedTableSize);
        ImmutableMapEntry[] createEntryArray2 = ImmutableMapEntry.createEntryArray(closedTableSize);
        Map.Entry<K, V>[] createEntryArray3 = i != entryArr.length ? ImmutableMapEntry.createEntryArray(i) : entryArr;
        int i3 = 0;
        int i4 = 0;
        while (true) {
            int i5 = i4;
            if (i5 >= i) {
                return new RegularImmutableBiMap<>(createEntryArray, createEntryArray2, createEntryArray3, i2, i3);
            }
            Map.Entry<K, V> entry = entryArr[i5];
            K key = entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int hashCode = key.hashCode();
            int hashCode2 = value.hashCode();
            int smear = Hashing.smear(hashCode) & i2;
            int smear2 = Hashing.smear(hashCode2) & i2;
            ImmutableMapEntry immutableMapEntry2 = createEntryArray[smear];
            RegularImmutableMap.checkNoConflictInKeyBucket(key, entry, immutableMapEntry2);
            ImmutableMapEntry immutableMapEntry3 = createEntryArray2[smear2];
            checkNoConflictInValueBucket(value, entry, immutableMapEntry3);
            if (immutableMapEntry3 == null && immutableMapEntry2 == null) {
                immutableMapEntry = !((entry instanceof ImmutableMapEntry) && ((ImmutableMapEntry) entry).isReusable()) ? new ImmutableMapEntry(key, value) : (ImmutableMapEntry) entry;
            } else {
                immutableMapEntry = new ImmutableMapEntry.NonTerminalImmutableBiMapEntry(key, value, immutableMapEntry2, immutableMapEntry3);
            }
            createEntryArray[smear] = immutableMapEntry;
            createEntryArray2[smear2] = immutableMapEntry;
            createEntryArray3[i5] = immutableMapEntry;
            i3 += hashCode ^ hashCode2;
            i4 = i5 + 1;
        }
    }

    @Override // com.google.common.collect.ImmutableMap
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return !isEmpty() ? new ImmutableMapEntrySet.RegularEntrySet(this, this.entries) : ImmutableSet.of();
    }

    @Override // com.google.common.collect.ImmutableMap, java.util.Map
    @Nullable
    public V get(@Nullable Object obj) {
        if (this.keyTable != null) {
            return (V) RegularImmutableMap.get(obj, this.keyTable, this.mask);
        }
        return null;
    }

    @Override // com.google.common.collect.ImmutableMap, java.util.Map
    public int hashCode() {
        return this.hashCode;
    }

    @Override // com.google.common.collect.ImmutableBiMap, com.google.common.collect.BiMap
    public ImmutableBiMap<V, K> inverse() {
        if (isEmpty()) {
            return ImmutableBiMap.of();
        }
        ImmutableBiMap<V, K> immutableBiMap = this.inverse;
        if (immutableBiMap != null) {
            return immutableBiMap;
        }
        Inverse inverse = new Inverse();
        this.inverse = inverse;
        return inverse;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableMap
    public boolean isHashCodeFast() {
        return true;
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
