package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.vr.cardboard.VrSettingsProviderContract;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true, serializable = true)
class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
    static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap<>((ImmutableMapEntry<K, V>[]) null, (ImmutableMapEntry<K, V>[]) null, (Map.Entry[]) ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
    static final double MAX_LOAD_FACTOR = 1.2d;
    /* access modifiers changed from: private */
    public final transient Map.Entry<K, V>[] entries;
    /* access modifiers changed from: private */
    public final transient int hashCode;
    private transient ImmutableBiMap<V, K> inverse;
    private final transient ImmutableMapEntry<K, V>[] keyTable;
    /* access modifiers changed from: private */
    public final transient int mask;
    /* access modifiers changed from: private */
    public final transient ImmutableMapEntry<K, V>[] valueTable;

    private final class Inverse extends ImmutableBiMap<V, K> {

        final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
            InverseEntrySet() {
            }

            /* access modifiers changed from: package-private */
            public ImmutableList<Map.Entry<V, K>> createAsList() {
                return new ImmutableAsList<Map.Entry<V, K>>() {
                    /* access modifiers changed from: package-private */
                    public ImmutableCollection<Map.Entry<V, K>> delegateCollection() {
                        return InverseEntrySet.this;
                    }

                    public Map.Entry<V, K> get(int i) {
                        Map.Entry entry = RegularImmutableBiMap.this.entries[i];
                        return Maps.immutableEntry(entry.getValue(), entry.getKey());
                    }
                };
            }

            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }

            /* access modifiers changed from: package-private */
            public boolean isHashCodeFast() {
                return true;
            }

            public UnmodifiableIterator<Map.Entry<V, K>> iterator() {
                return asList().iterator();
            }

            /* access modifiers changed from: package-private */
            public ImmutableMap<V, K> map() {
                return Inverse.this;
            }
        }

        private Inverse() {
        }

        /* access modifiers changed from: package-private */
        public ImmutableSet<Map.Entry<V, K>> createEntrySet() {
            return new InverseEntrySet();
        }

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

        public ImmutableBiMap<K, V> inverse() {
            return RegularImmutableBiMap.this;
        }

        /* access modifiers changed from: package-private */
        public boolean isPartialView() {
            return false;
        }

        public int size() {
            return inverse().size();
        }

        /* access modifiers changed from: package-private */
        public Object writeReplace() {
            return new InverseSerializedForm(RegularImmutableBiMap.this);
        }
    }

    private static class InverseSerializedForm<K, V> implements Serializable {
        private static final long serialVersionUID = 1;
        private final ImmutableBiMap<K, V> forward;

        InverseSerializedForm(ImmutableBiMap<K, V> immutableBiMap) {
            this.forward = immutableBiMap;
        }

        /* access modifiers changed from: package-private */
        public Object readResolve() {
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

    static <K, V> RegularImmutableBiMap<K, V> fromEntries(Map.Entry<K, V>... entryArr) {
        return fromEntryArray(entryArr.length, entryArr);
    }

    static <K, V> RegularImmutableBiMap<K, V> fromEntryArray(int i, Map.Entry<K, V>[] entryArr) {
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
            ImmutableMapEntry immutableMapEntry2 = entryArr[i5];
            K key = immutableMapEntry2.getKey();
            V value = immutableMapEntry2.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int hashCode2 = key.hashCode();
            int hashCode3 = value.hashCode();
            int smear = Hashing.smear(hashCode2) & i2;
            int smear2 = Hashing.smear(hashCode3) & i2;
            ImmutableMapEntry immutableMapEntry3 = createEntryArray[smear];
            RegularImmutableMap.checkNoConflictInKeyBucket(key, immutableMapEntry2, immutableMapEntry3);
            ImmutableMapEntry immutableMapEntry4 = createEntryArray2[smear2];
            checkNoConflictInValueBucket(value, immutableMapEntry2, immutableMapEntry4);
            if (immutableMapEntry4 == null && immutableMapEntry3 == null) {
                immutableMapEntry = !((immutableMapEntry2 instanceof ImmutableMapEntry) && immutableMapEntry2.isReusable()) ? new ImmutableMapEntry(key, value) : immutableMapEntry2;
            } else {
                immutableMapEntry = new ImmutableMapEntry.NonTerminalImmutableBiMapEntry(key, value, immutableMapEntry3, immutableMapEntry4);
            }
            createEntryArray[smear] = immutableMapEntry;
            createEntryArray2[smear2] = immutableMapEntry;
            createEntryArray3[i5] = immutableMapEntry;
            i3 += hashCode2 ^ hashCode3;
            i4 = i5 + 1;
        }
    }

    /* access modifiers changed from: package-private */
    public ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return !isEmpty() ? new ImmutableMapEntrySet.RegularEntrySet(this, this.entries) : ImmutableSet.of();
    }

    @Nullable
    public V get(@Nullable Object obj) {
        if (this.keyTable != null) {
            return RegularImmutableMap.get(obj, this.keyTable, this.mask);
        }
        return null;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public ImmutableBiMap<V, K> inverse() {
        if (isEmpty()) {
            return ImmutableBiMap.of();
        }
        ImmutableBiMap<V, K> immutableBiMap = this.inverse;
        if (immutableBiMap != null) {
            return immutableBiMap;
        }
        Inverse inverse2 = new Inverse();
        this.inverse = inverse2;
        return inverse2;
    }

    /* access modifiers changed from: package-private */
    public boolean isHashCodeFast() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isPartialView() {
        return false;
    }

    public int size() {
        return this.entries.length;
    }
}
