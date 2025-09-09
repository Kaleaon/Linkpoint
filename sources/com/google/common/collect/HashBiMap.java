package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class HashBiMap<K, V> extends Maps.IteratorBasedAbstractMap<K, V> implements BiMap<K, V>, Serializable {
    private static final double LOAD_FACTOR = 1.0d;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0;
    /* access modifiers changed from: private */
    public transient BiEntry<K, V> firstInKeyInsertionOrder;
    private transient BiEntry<K, V>[] hashTableKToV;
    private transient BiEntry<K, V>[] hashTableVToK;
    private transient BiMap<V, K> inverse;
    private transient BiEntry<K, V> lastInKeyInsertionOrder;
    private transient int mask;
    /* access modifiers changed from: private */
    public transient int modCount;
    /* access modifiers changed from: private */
    public transient int size;

    private static final class BiEntry<K, V> extends ImmutableEntry<K, V> {
        final int keyHash;
        @Nullable
        BiEntry<K, V> nextInKToVBucket;
        @Nullable
        BiEntry<K, V> nextInKeyInsertionOrder;
        @Nullable
        BiEntry<K, V> nextInVToKBucket;
        @Nullable
        BiEntry<K, V> prevInKeyInsertionOrder;
        final int valueHash;

        BiEntry(K k, int i, V v, int i2) {
            super(k, v);
            this.keyHash = i;
            this.valueHash = i2;
        }
    }

    private final class Inverse extends AbstractMap<V, K> implements BiMap<V, K>, Serializable {

        private final class InverseKeySet extends Maps.KeySet<V, K> {
            InverseKeySet() {
                super(Inverse.this);
            }

            public Iterator<V> iterator() {
                return new HashBiMap<K, V>.Itr<V>() {
                    {
                        HashBiMap hashBiMap = HashBiMap.this;
                    }

                    /* access modifiers changed from: package-private */
                    public V output(BiEntry<K, V> biEntry) {
                        return biEntry.value;
                    }
                };
            }

            public boolean remove(@Nullable Object obj) {
                BiEntry access$400 = HashBiMap.this.seekByValue(obj, Hashing.smearedHash(obj));
                if (access$400 == null) {
                    return false;
                }
                HashBiMap.this.delete(access$400);
                return true;
            }
        }

        private Inverse() {
        }

        public void clear() {
            forward().clear();
        }

        public boolean containsKey(@Nullable Object obj) {
            return forward().containsValue(obj);
        }

        public Set<Map.Entry<V, K>> entrySet() {
            return new Maps.EntrySet<V, K>() {
                public Iterator<Map.Entry<V, K>> iterator() {
                    return new HashBiMap<K, V>.Itr<Map.Entry<V, K>>() {

                        /* renamed from: com.google.common.collect.HashBiMap$Inverse$1$1$InverseEntry */
                        class InverseEntry extends AbstractMapEntry<V, K> {
                            BiEntry<K, V> delegate;

                            InverseEntry(BiEntry<K, V> biEntry) {
                                this.delegate = biEntry;
                            }

                            public V getKey() {
                                return this.delegate.value;
                            }

                            public K getValue() {
                                return this.delegate.key;
                            }

                            public K setValue(K k) {
                                K k2 = this.delegate.key;
                                int smearedHash = Hashing.smearedHash(k);
                                if (smearedHash == this.delegate.keyHash && Objects.equal(k, k2)) {
                                    return k;
                                }
                                Preconditions.checkArgument(HashBiMap.this.seekByKey(k, smearedHash) == null, "value already present: %s", k);
                                HashBiMap.this.delete(this.delegate);
                                BiEntry<K, V> biEntry = new BiEntry<>(k, smearedHash, this.delegate.value, this.delegate.valueHash);
                                this.delegate = biEntry;
                                HashBiMap.this.insert(biEntry, (BiEntry<K, V>) null);
                                AnonymousClass1.this.expectedModCount = HashBiMap.this.modCount;
                                return k2;
                            }
                        }

                        {
                            HashBiMap hashBiMap = HashBiMap.this;
                        }

                        /* access modifiers changed from: package-private */
                        public Map.Entry<V, K> output(BiEntry<K, V> biEntry) {
                            return new InverseEntry(biEntry);
                        }
                    };
                }

                /* access modifiers changed from: package-private */
                public Map<V, K> map() {
                    return Inverse.this;
                }
            };
        }

        public K forcePut(@Nullable V v, @Nullable K k) {
            return HashBiMap.this.putInverse(v, k, true);
        }

        /* access modifiers changed from: package-private */
        public BiMap<K, V> forward() {
            return HashBiMap.this;
        }

        public K get(@Nullable Object obj) {
            return Maps.keyOrNull(HashBiMap.this.seekByValue(obj, Hashing.smearedHash(obj)));
        }

        public BiMap<K, V> inverse() {
            return forward();
        }

        public Set<V> keySet() {
            return new InverseKeySet();
        }

        public K put(@Nullable V v, @Nullable K k) {
            return HashBiMap.this.putInverse(v, k, false);
        }

        public K remove(@Nullable Object obj) {
            BiEntry access$400 = HashBiMap.this.seekByValue(obj, Hashing.smearedHash(obj));
            if (access$400 == null) {
                return null;
            }
            HashBiMap.this.delete(access$400);
            access$400.prevInKeyInsertionOrder = null;
            access$400.nextInKeyInsertionOrder = null;
            return access$400.key;
        }

        public int size() {
            return HashBiMap.this.size;
        }

        public Set<K> values() {
            return forward().keySet();
        }

        /* access modifiers changed from: package-private */
        public Object writeReplace() {
            return new InverseSerializedForm(HashBiMap.this);
        }
    }

    private static final class InverseSerializedForm<K, V> implements Serializable {
        private final HashBiMap<K, V> bimap;

        InverseSerializedForm(HashBiMap<K, V> hashBiMap) {
            this.bimap = hashBiMap;
        }

        /* access modifiers changed from: package-private */
        public Object readResolve() {
            return this.bimap.inverse();
        }
    }

    abstract class Itr<T> implements Iterator<T> {
        int expectedModCount = HashBiMap.this.modCount;
        BiEntry<K, V> next = HashBiMap.this.firstInKeyInsertionOrder;
        BiEntry<K, V> toRemove = null;

        Itr() {
        }

        public boolean hasNext() {
            if (HashBiMap.this.modCount == this.expectedModCount) {
                return this.next != null;
            }
            throw new ConcurrentModificationException();
        }

        public T next() {
            if (hasNext()) {
                BiEntry<K, V> biEntry = this.next;
                this.next = biEntry.nextInKeyInsertionOrder;
                this.toRemove = biEntry;
                return output(biEntry);
            }
            throw new NoSuchElementException();
        }

        /* access modifiers changed from: package-private */
        public abstract T output(BiEntry<K, V> biEntry);

        public void remove() {
            if (HashBiMap.this.modCount == this.expectedModCount) {
                CollectPreconditions.checkRemove(this.toRemove != null);
                HashBiMap.this.delete(this.toRemove);
                this.expectedModCount = HashBiMap.this.modCount;
                this.toRemove = null;
                return;
            }
            throw new ConcurrentModificationException();
        }
    }

    private final class KeySet extends Maps.KeySet<K, V> {
        KeySet() {
            super(HashBiMap.this);
        }

        public Iterator<K> iterator() {
            return new HashBiMap<K, V>.Itr<K>() {
                {
                    HashBiMap hashBiMap = HashBiMap.this;
                }

                /* access modifiers changed from: package-private */
                public K output(BiEntry<K, V> biEntry) {
                    return biEntry.key;
                }
            };
        }

        public boolean remove(@Nullable Object obj) {
            BiEntry access$300 = HashBiMap.this.seekByKey(obj, Hashing.smearedHash(obj));
            if (access$300 == null) {
                return false;
            }
            HashBiMap.this.delete(access$300);
            access$300.prevInKeyInsertionOrder = null;
            access$300.nextInKeyInsertionOrder = null;
            return true;
        }
    }

    private HashBiMap(int i) {
        init(i);
    }

    public static <K, V> HashBiMap<K, V> create() {
        return create(16);
    }

    public static <K, V> HashBiMap<K, V> create(int i) {
        return new HashBiMap<>(i);
    }

    public static <K, V> HashBiMap<K, V> create(Map<? extends K, ? extends V> map) {
        HashBiMap<K, V> create = create(map.size());
        create.putAll(map);
        return create;
    }

    private BiEntry<K, V>[] createTable(int i) {
        return new BiEntry[i];
    }

    /* access modifiers changed from: private */
    public void delete(BiEntry<K, V> biEntry) {
        BiEntry<K, V> biEntry2 = null;
        int i = biEntry.keyHash & this.mask;
        BiEntry<K, V> biEntry3 = null;
        for (BiEntry<K, V> biEntry4 = this.hashTableKToV[i]; biEntry4 != biEntry; biEntry4 = biEntry4.nextInKToVBucket) {
            biEntry3 = biEntry4;
        }
        if (biEntry3 != null) {
            biEntry3.nextInKToVBucket = biEntry.nextInKToVBucket;
        } else {
            this.hashTableKToV[i] = biEntry.nextInKToVBucket;
        }
        int i2 = this.mask & biEntry.valueHash;
        for (BiEntry<K, V> biEntry5 = this.hashTableVToK[i2]; biEntry5 != biEntry; biEntry5 = biEntry5.nextInVToKBucket) {
            biEntry2 = biEntry5;
        }
        if (biEntry2 != null) {
            biEntry2.nextInVToKBucket = biEntry.nextInVToKBucket;
        } else {
            this.hashTableVToK[i2] = biEntry.nextInVToKBucket;
        }
        if (biEntry.prevInKeyInsertionOrder != null) {
            biEntry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = biEntry.nextInKeyInsertionOrder;
        } else {
            this.firstInKeyInsertionOrder = biEntry.nextInKeyInsertionOrder;
        }
        if (biEntry.nextInKeyInsertionOrder != null) {
            biEntry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = biEntry.prevInKeyInsertionOrder;
        } else {
            this.lastInKeyInsertionOrder = biEntry.prevInKeyInsertionOrder;
        }
        this.size--;
        this.modCount++;
    }

    private void init(int i) {
        CollectPreconditions.checkNonnegative(i, "expectedSize");
        int closedTableSize = Hashing.closedTableSize(i, LOAD_FACTOR);
        this.hashTableKToV = createTable(closedTableSize);
        this.hashTableVToK = createTable(closedTableSize);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        this.size = 0;
        this.mask = closedTableSize - 1;
        this.modCount = 0;
    }

    /* access modifiers changed from: private */
    public void insert(BiEntry<K, V> biEntry, @Nullable BiEntry<K, V> biEntry2) {
        int i = biEntry.keyHash & this.mask;
        biEntry.nextInKToVBucket = this.hashTableKToV[i];
        this.hashTableKToV[i] = biEntry;
        int i2 = biEntry.valueHash & this.mask;
        biEntry.nextInVToKBucket = this.hashTableVToK[i2];
        this.hashTableVToK[i2] = biEntry;
        if (biEntry2 != null) {
            biEntry.prevInKeyInsertionOrder = biEntry2.prevInKeyInsertionOrder;
            if (biEntry.prevInKeyInsertionOrder != null) {
                biEntry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = biEntry;
            } else {
                this.firstInKeyInsertionOrder = biEntry;
            }
            biEntry.nextInKeyInsertionOrder = biEntry2.nextInKeyInsertionOrder;
            if (biEntry.nextInKeyInsertionOrder != null) {
                biEntry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = biEntry;
            } else {
                this.lastInKeyInsertionOrder = biEntry;
            }
        } else {
            biEntry.prevInKeyInsertionOrder = this.lastInKeyInsertionOrder;
            biEntry.nextInKeyInsertionOrder = null;
            if (this.lastInKeyInsertionOrder != null) {
                this.lastInKeyInsertionOrder.nextInKeyInsertionOrder = biEntry;
            } else {
                this.firstInKeyInsertionOrder = biEntry;
            }
            this.lastInKeyInsertionOrder = biEntry;
        }
        this.size++;
        this.modCount++;
    }

    private V put(@Nullable K k, @Nullable V v, boolean z) {
        int smearedHash = Hashing.smearedHash(k);
        int smearedHash2 = Hashing.smearedHash(v);
        BiEntry seekByKey = seekByKey(k, smearedHash);
        if (seekByKey != null && smearedHash2 == seekByKey.valueHash && Objects.equal(v, seekByKey.value)) {
            return v;
        }
        BiEntry seekByValue = seekByValue(v, smearedHash2);
        if (seekByValue != null) {
            if (!z) {
                throw new IllegalArgumentException("value already present: " + v);
            }
            delete(seekByValue);
        }
        BiEntry biEntry = new BiEntry(k, smearedHash, v, smearedHash2);
        if (seekByKey == null) {
            insert(biEntry, (BiEntry) null);
            rehashIfNecessary();
            return null;
        }
        delete(seekByKey);
        insert(biEntry, seekByKey);
        seekByKey.prevInKeyInsertionOrder = null;
        seekByKey.nextInKeyInsertionOrder = null;
        rehashIfNecessary();
        return seekByKey.value;
    }

    /* access modifiers changed from: private */
    @Nullable
    public K putInverse(@Nullable V v, @Nullable K k, boolean z) {
        int smearedHash = Hashing.smearedHash(v);
        int smearedHash2 = Hashing.smearedHash(k);
        BiEntry seekByValue = seekByValue(v, smearedHash);
        if (seekByValue != null && smearedHash2 == seekByValue.keyHash && Objects.equal(k, seekByValue.key)) {
            return k;
        }
        BiEntry seekByKey = seekByKey(k, smearedHash2);
        if (seekByKey != null) {
            if (!z) {
                throw new IllegalArgumentException("value already present: " + k);
            }
            delete(seekByKey);
        }
        if (seekByValue != null) {
            delete(seekByValue);
        }
        insert(new BiEntry(k, smearedHash2, v, smearedHash), seekByKey);
        if (seekByKey != null) {
            seekByKey.prevInKeyInsertionOrder = null;
            seekByKey.nextInKeyInsertionOrder = null;
        }
        rehashIfNecessary();
        return Maps.keyOrNull(seekByValue);
    }

    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        init(16);
        Serialization.populateMap(this, objectInputStream, Serialization.readCount(objectInputStream));
    }

    private void rehashIfNecessary() {
        BiEntry<K, V>[] biEntryArr = this.hashTableKToV;
        if (Hashing.needsResizing(this.size, biEntryArr.length, LOAD_FACTOR)) {
            int length = biEntryArr.length * 2;
            this.hashTableKToV = createTable(length);
            this.hashTableVToK = createTable(length);
            this.mask = length - 1;
            this.size = 0;
            for (BiEntry<K, V> biEntry = this.firstInKeyInsertionOrder; biEntry != null; biEntry = biEntry.nextInKeyInsertionOrder) {
                insert(biEntry, biEntry);
            }
            this.modCount++;
        }
    }

    /* access modifiers changed from: private */
    public BiEntry<K, V> seekByKey(@Nullable Object obj, int i) {
        for (BiEntry<K, V> biEntry = this.hashTableKToV[this.mask & i]; biEntry != null; biEntry = biEntry.nextInKToVBucket) {
            if (i == biEntry.keyHash && Objects.equal(obj, biEntry.key)) {
                return biEntry;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public BiEntry<K, V> seekByValue(@Nullable Object obj, int i) {
        for (BiEntry<K, V> biEntry = this.hashTableVToK[this.mask & i]; biEntry != null; biEntry = biEntry.nextInVToKBucket) {
            if (i == biEntry.valueHash && Objects.equal(obj, biEntry.value)) {
                return biEntry;
            }
        }
        return null;
    }

    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMap(this, objectOutputStream);
    }

    public void clear() {
        this.size = 0;
        Arrays.fill(this.hashTableKToV, (Object) null);
        Arrays.fill(this.hashTableVToK, (Object) null);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        this.modCount++;
    }

    public boolean containsKey(@Nullable Object obj) {
        return seekByKey(obj, Hashing.smearedHash(obj)) != null;
    }

    public boolean containsValue(@Nullable Object obj) {
        return seekByValue(obj, Hashing.smearedHash(obj)) != null;
    }

    /* access modifiers changed from: package-private */
    public Iterator<Map.Entry<K, V>> entryIterator() {
        return new HashBiMap<K, V>.Itr<Map.Entry<K, V>>() {

            /* renamed from: com.google.common.collect.HashBiMap$1$MapEntry */
            class MapEntry extends AbstractMapEntry<K, V> {
                BiEntry<K, V> delegate;

                MapEntry(BiEntry<K, V> biEntry) {
                    this.delegate = biEntry;
                }

                public K getKey() {
                    return this.delegate.key;
                }

                public V getValue() {
                    return this.delegate.value;
                }

                public V setValue(V v) {
                    V v2 = this.delegate.value;
                    int smearedHash = Hashing.smearedHash(v);
                    if (smearedHash == this.delegate.valueHash && Objects.equal(v, v2)) {
                        return v;
                    }
                    Preconditions.checkArgument(HashBiMap.this.seekByValue(v, smearedHash) == null, "value already present: %s", v);
                    HashBiMap.this.delete(this.delegate);
                    BiEntry<K, V> biEntry = new BiEntry<>(this.delegate.key, this.delegate.keyHash, v, smearedHash);
                    HashBiMap.this.insert(biEntry, this.delegate);
                    this.delegate.prevInKeyInsertionOrder = null;
                    this.delegate.nextInKeyInsertionOrder = null;
                    AnonymousClass1.this.expectedModCount = HashBiMap.this.modCount;
                    if (AnonymousClass1.this.toRemove == this.delegate) {
                        AnonymousClass1.this.toRemove = biEntry;
                    }
                    this.delegate = biEntry;
                    return v2;
                }
            }

            /* access modifiers changed from: package-private */
            public Map.Entry<K, V> output(BiEntry<K, V> biEntry) {
                return new MapEntry(biEntry);
            }
        };
    }

    public /* bridge */ /* synthetic */ Set entrySet() {
        return super.entrySet();
    }

    public V forcePut(@Nullable K k, @Nullable V v) {
        return put(k, v, true);
    }

    @Nullable
    public V get(@Nullable Object obj) {
        return Maps.valueOrNull(seekByKey(obj, Hashing.smearedHash(obj)));
    }

    public BiMap<V, K> inverse() {
        if (this.inverse != null) {
            return this.inverse;
        }
        Inverse inverse2 = new Inverse();
        this.inverse = inverse2;
        return inverse2;
    }

    public Set<K> keySet() {
        return new KeySet();
    }

    public V put(@Nullable K k, @Nullable V v) {
        return put(k, v, false);
    }

    public V remove(@Nullable Object obj) {
        BiEntry seekByKey = seekByKey(obj, Hashing.smearedHash(obj));
        if (seekByKey == null) {
            return null;
        }
        delete(seekByKey);
        seekByKey.prevInKeyInsertionOrder = null;
        seekByKey.nextInKeyInsertionOrder = null;
        return seekByKey.value;
    }

    public int size() {
        return this.size;
    }

    public Set<V> values() {
        return inverse().keySet();
    }
}
