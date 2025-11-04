// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Set;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.Arrays;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.util.Map;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(emulated = true)
public final class HashBiMap<K, V> extends IteratorBasedAbstractMap<K, V> implements BiMap<K, V>, Serializable
{
    private static final double LOAD_FACTOR = 1.0;
    @GwtIncompatible("Not needed in emulated source")
    private static final long serialVersionUID = 0L;
    private transient BiEntry<K, V> firstInKeyInsertionOrder;
    private transient BiEntry<K, V>[] hashTableKToV;
    private transient BiEntry<K, V>[] hashTableVToK;
    private transient BiMap<V, K> inverse;
    private transient BiEntry<K, V> lastInKeyInsertionOrder;
    private transient int mask;
    private transient int modCount;
    private transient int size;
    
    private HashBiMap(final int n) {
        this.init(n);
    }
    
    public static <K, V> HashBiMap<K, V> create() {
        return create(16);
    }
    
    public static <K, V> HashBiMap<K, V> create(final int n) {
        return new HashBiMap<K, V>(n);
    }
    
    public static <K, V> HashBiMap<K, V> create(final Map<? extends K, ? extends V> m) {
        final HashBiMap<Object, Object> create = create(m.size());
        create.putAll(m);
        return (HashBiMap<K, V>)create;
    }
    
    private BiEntry<K, V>[] createTable(final int n) {
        return new BiEntry[n];
    }
    
    private void delete(final BiEntry<K, V> biEntry) {
        final BiEntry biEntry2 = null;
        final int n = biEntry.keyHash & this.mask;
        BiEntry<K, V> biEntry3 = this.hashTableKToV[n];
        BiEntry<K, V> biEntry4 = null;
        while (biEntry3 != biEntry) {
            final BiEntry<K, V> nextInKToVBucket = biEntry3.nextInKToVBucket;
            biEntry4 = biEntry3;
            biEntry3 = nextInKToVBucket;
        }
        if (biEntry4 != null) {
            biEntry4.nextInKToVBucket = biEntry.nextInKToVBucket;
        }
        else {
            this.hashTableKToV[n] = biEntry.nextInKToVBucket;
        }
        final int n2 = this.mask & biEntry.valueHash;
        BiEntry<K, V> biEntry5 = this.hashTableVToK[n2];
        BiEntry<K, V> biEntry6 = biEntry2;
        while (biEntry5 != biEntry) {
            final BiEntry<K, V> nextInVToKBucket = biEntry5.nextInVToKBucket;
            biEntry6 = biEntry5;
            biEntry5 = nextInVToKBucket;
        }
        if (biEntry6 != null) {
            biEntry6.nextInVToKBucket = biEntry.nextInVToKBucket;
        }
        else {
            this.hashTableVToK[n2] = biEntry.nextInVToKBucket;
        }
        if (biEntry.prevInKeyInsertionOrder != null) {
            biEntry.prevInKeyInsertionOrder.nextInKeyInsertionOrder = biEntry.nextInKeyInsertionOrder;
        }
        else {
            this.firstInKeyInsertionOrder = biEntry.nextInKeyInsertionOrder;
        }
        if (biEntry.nextInKeyInsertionOrder != null) {
            biEntry.nextInKeyInsertionOrder.prevInKeyInsertionOrder = biEntry.prevInKeyInsertionOrder;
        }
        else {
            this.lastInKeyInsertionOrder = biEntry.prevInKeyInsertionOrder;
        }
        --this.size;
        ++this.modCount;
    }
    
    private void init(int closedTableSize) {
        CollectPreconditions.checkNonnegative(closedTableSize, "expectedSize");
        closedTableSize = Hashing.closedTableSize(closedTableSize, 1.0);
        this.hashTableKToV = this.createTable(closedTableSize);
        this.hashTableVToK = this.createTable(closedTableSize);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        this.size = 0;
        this.mask = closedTableSize - 1;
        this.modCount = 0;
    }
    
    private void insert(final BiEntry<K, V> lastInKeyInsertionOrder, @Nullable final BiEntry<K, V> biEntry) {
        final int n = lastInKeyInsertionOrder.keyHash & this.mask;
        lastInKeyInsertionOrder.nextInKToVBucket = this.hashTableKToV[n];
        this.hashTableKToV[n] = lastInKeyInsertionOrder;
        final int n2 = lastInKeyInsertionOrder.valueHash & this.mask;
        lastInKeyInsertionOrder.nextInVToKBucket = this.hashTableVToK[n2];
        this.hashTableVToK[n2] = lastInKeyInsertionOrder;
        if (biEntry != null) {
            lastInKeyInsertionOrder.prevInKeyInsertionOrder = biEntry.prevInKeyInsertionOrder;
            if (lastInKeyInsertionOrder.prevInKeyInsertionOrder != null) {
                lastInKeyInsertionOrder.prevInKeyInsertionOrder.nextInKeyInsertionOrder = lastInKeyInsertionOrder;
            }
            else {
                this.firstInKeyInsertionOrder = lastInKeyInsertionOrder;
            }
            lastInKeyInsertionOrder.nextInKeyInsertionOrder = biEntry.nextInKeyInsertionOrder;
            if (lastInKeyInsertionOrder.nextInKeyInsertionOrder != null) {
                lastInKeyInsertionOrder.nextInKeyInsertionOrder.prevInKeyInsertionOrder = lastInKeyInsertionOrder;
            }
            else {
                this.lastInKeyInsertionOrder = lastInKeyInsertionOrder;
            }
        }
        else {
            lastInKeyInsertionOrder.prevInKeyInsertionOrder = this.lastInKeyInsertionOrder;
            lastInKeyInsertionOrder.nextInKeyInsertionOrder = null;
            if (this.lastInKeyInsertionOrder != null) {
                this.lastInKeyInsertionOrder.nextInKeyInsertionOrder = lastInKeyInsertionOrder;
            }
            else {
                this.firstInKeyInsertionOrder = lastInKeyInsertionOrder;
            }
            this.lastInKeyInsertionOrder = lastInKeyInsertionOrder;
        }
        ++this.size;
        ++this.modCount;
    }
    
    private V put(@Nullable final K k, @Nullable final V obj, final boolean b) {
        final int smearedHash = Hashing.smearedHash(k);
        final int smearedHash2 = Hashing.smearedHash(obj);
        final BiEntry<K, V> seekByKey = this.seekByKey(k, smearedHash);
        if (seekByKey != null && smearedHash2 == seekByKey.valueHash && Objects.equal(obj, seekByKey.value)) {
            return obj;
        }
        final BiEntry<K, V> seekByValue = this.seekByValue(obj, smearedHash2);
        if (seekByValue != null) {
            if (!b) {
                throw new IllegalArgumentException("value already present: " + obj);
            }
            this.delete(seekByValue);
        }
        final BiEntry biEntry = new BiEntry<K, V>(k, smearedHash, obj, smearedHash2);
        if (seekByKey == null) {
            this.insert((BiEntry<K, V>)biEntry, null);
            this.rehashIfNecessary();
            return null;
        }
        this.delete(seekByKey);
        this.insert((BiEntry<K, V>)biEntry, seekByKey);
        seekByKey.prevInKeyInsertionOrder = null;
        seekByKey.nextInKeyInsertionOrder = null;
        this.rehashIfNecessary();
        return seekByKey.value;
    }
    
    @Nullable
    private K putInverse(@Nullable final V v, @Nullable final K obj, final boolean b) {
        final int smearedHash = Hashing.smearedHash(v);
        final int smearedHash2 = Hashing.smearedHash(obj);
        final BiEntry<K, V> seekByValue = this.seekByValue(v, smearedHash);
        if (seekByValue != null && smearedHash2 == seekByValue.keyHash && Objects.equal(obj, seekByValue.key)) {
            return obj;
        }
        final BiEntry<K, V> seekByKey = this.seekByKey(obj, smearedHash2);
        if (seekByKey != null) {
            if (!b) {
                throw new IllegalArgumentException("value already present: " + obj);
            }
            this.delete(seekByKey);
        }
        if (seekByValue != null) {
            this.delete(seekByValue);
        }
        this.insert(new BiEntry<K, V>(obj, smearedHash2, v, smearedHash), seekByKey);
        if (seekByKey != null) {
            seekByKey.prevInKeyInsertionOrder = null;
            seekByKey.nextInKeyInsertionOrder = null;
        }
        this.rehashIfNecessary();
        return Maps.keyOrNull(seekByValue);
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(16);
        Serialization.populateMap((Map<Object, Object>)this, objectInputStream, Serialization.readCount(objectInputStream));
    }
    
    private void rehashIfNecessary() {
        final BiEntry<K, V>[] hashTableKToV = this.hashTableKToV;
        if (Hashing.needsResizing(this.size, hashTableKToV.length, 1.0)) {
            final int n = hashTableKToV.length * 2;
            this.hashTableKToV = this.createTable(n);
            this.hashTableVToK = this.createTable(n);
            this.mask = n - 1;
            this.size = 0;
            for (BiEntry<K, V> biEntry = this.firstInKeyInsertionOrder; biEntry != null; biEntry = biEntry.nextInKeyInsertionOrder) {
                this.insert(biEntry, biEntry);
            }
            ++this.modCount;
        }
    }
    
    private BiEntry<K, V> seekByKey(@Nullable final Object o, final int n) {
        for (BiEntry<K, V> nextInKToVBucket = this.hashTableKToV[this.mask & n]; nextInKToVBucket != null; nextInKToVBucket = nextInKToVBucket.nextInKToVBucket) {
            if (n == nextInKToVBucket.keyHash && Objects.equal(o, nextInKToVBucket.key)) {
                return nextInKToVBucket;
            }
        }
        return null;
    }
    
    private BiEntry<K, V> seekByValue(@Nullable final Object o, final int n) {
        for (BiEntry<K, V> nextInVToKBucket = this.hashTableVToK[this.mask & n]; nextInVToKBucket != null; nextInVToKBucket = nextInVToKBucket.nextInVToKBucket) {
            if (n == nextInVToKBucket.valueHash && Objects.equal(o, nextInVToKBucket.value)) {
                return nextInVToKBucket;
            }
        }
        return null;
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        Serialization.writeMap((Map<Object, Object>)this, objectOutputStream);
    }
    
    @Override
    public void clear() {
        this.size = 0;
        Arrays.fill(this.hashTableKToV, null);
        Arrays.fill(this.hashTableVToK, null);
        this.firstInKeyInsertionOrder = null;
        this.lastInKeyInsertionOrder = null;
        ++this.modCount;
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        return this.seekByKey(o, Hashing.smearedHash(o)) != null;
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        return this.seekByValue(o, Hashing.smearedHash(o)) != null;
    }
    
    @Override
    Iterator<Entry<K, V>> entryIterator() {
        return new Itr<Entry<K, V>>() {
            Entry<K, V> output(final BiEntry<K, V> biEntry) {
                return new MapEntry(biEntry);
            }
            
            class MapEntry extends AbstractMapEntry<K, V>
            {
                BiEntry<K, V> delegate;
                
                MapEntry(final BiEntry<K, V> delegate) {
                    this.delegate = delegate;
                }
                
                @Override
                public K getKey() {
                    return this.delegate.key;
                }
                
                @Override
                public V getValue() {
                    return this.delegate.value;
                }
                
                @Override
                public V setValue(final V v) {
                    final V value = this.delegate.value;
                    final int smearedHash = Hashing.smearedHash(v);
                    if (smearedHash == this.delegate.valueHash && Objects.equal(v, value)) {
                        return v;
                    }
                    Preconditions.checkArgument(HashBiMap.this.seekByValue(v, smearedHash) == null, "value already present: %s", v);
                    HashBiMap.this.delete((BiEntry)this.delegate);
                    final BiEntry biEntry = new BiEntry(this.delegate.key, this.delegate.keyHash, v, smearedHash);
                    HashBiMap.this.insert(biEntry, (BiEntry)this.delegate);
                    this.delegate.prevInKeyInsertionOrder = null;
                    this.delegate.nextInKeyInsertionOrder = null;
                    Itr.this.expectedModCount = HashBiMap.this.modCount;
                    if (Itr.this.toRemove == this.delegate) {
                        Itr.this.toRemove = (BiEntry<K, V>)biEntry;
                    }
                    this.delegate = (BiEntry<K, V>)biEntry;
                    return value;
                }
            }
        };
    }
    
    @Override
    public V forcePut(@Nullable final K k, @Nullable final V v) {
        return this.put(k, v, true);
    }
    
    @Nullable
    @Override
    public V get(@Nullable final Object o) {
        return Maps.valueOrNull(this.seekByKey(o, Hashing.smearedHash(o)));
    }
    
    @Override
    public BiMap<V, K> inverse() {
        BiMap<V, K> inverse;
        if (this.inverse != null) {
            inverse = this.inverse;
        }
        else {
            inverse = new Inverse();
            this.inverse = inverse;
        }
        return inverse;
    }
    
    @Override
    public Set<K> keySet() {
        return (Set<K>)new KeySet();
    }
    
    @Override
    public V put(@Nullable final K k, @Nullable final V v) {
        return this.put(k, v, false);
    }
    
    @Override
    public V remove(@Nullable final Object o) {
        final BiEntry<K, V> seekByKey = this.seekByKey(o, Hashing.smearedHash(o));
        if (seekByKey != null) {
            this.delete(seekByKey);
            seekByKey.prevInKeyInsertionOrder = null;
            seekByKey.nextInKeyInsertionOrder = null;
            return seekByKey.value;
        }
        return null;
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Set<V> values() {
        return this.inverse().keySet();
    }
    
    private static final class BiEntry<K, V> extends ImmutableEntry<K, V>
    {
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
        
        BiEntry(final K k, final int keyHash, final V v, final int valueHash) {
            super(k, v);
            this.keyHash = keyHash;
            this.valueHash = valueHash;
        }
    }
    
    private final class Inverse extends AbstractMap<V, K> implements BiMap<V, K>, Serializable
    {
        @Override
        public void clear() {
            this.forward().clear();
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return this.forward().containsValue(o);
        }
        
        @Override
        public Set<Entry<V, K>> entrySet() {
            return (Set<Entry<V, K>>)new Maps.EntrySet<V, K>() {
                @Override
                public Iterator<Entry<V, K>> iterator() {
                    return new Itr<Entry<V, K>>() {
                        Entry<V, K> output(final BiEntry<K, V> biEntry) {
                            return new InverseEntry(biEntry);
                        }
                        
                        class InverseEntry extends AbstractMapEntry<V, K>
                        {
                            BiEntry<K, V> delegate;
                            
                            InverseEntry(final BiEntry<K, V> delegate) {
                                this.delegate = delegate;
                            }
                            
                            @Override
                            public V getKey() {
                                return this.delegate.value;
                            }
                            
                            @Override
                            public K getValue() {
                                return this.delegate.key;
                            }
                            
                            @Override
                            public K setValue(final K k) {
                                final K key = this.delegate.key;
                                final int smearedHash = Hashing.smearedHash(k);
                                if (smearedHash == this.delegate.keyHash && Objects.equal(k, key)) {
                                    return k;
                                }
                                Preconditions.checkArgument(HashBiMap.this.seekByKey(k, smearedHash) == null, "value already present: %s", k);
                                HashBiMap.this.delete((BiEntry)this.delegate);
                                final BiEntry delegate = new BiEntry(k, smearedHash, this.delegate.value, this.delegate.valueHash);
                                this.delegate = (BiEntry<K, V>)delegate;
                                HashBiMap.this.insert(delegate, null);
                                Itr.this.expectedModCount = HashBiMap.this.modCount;
                                return key;
                            }
                        }
                    };
                }
                
                @Override
                Map<V, K> map() {
                    return Inverse.this;
                }
            };
        }
        
        @Override
        public K forcePut(@Nullable final V v, @Nullable final K k) {
            return (K)HashBiMap.this.putInverse(v, k, true);
        }
        
        BiMap<K, V> forward() {
            return HashBiMap.this;
        }
        
        @Override
        public K get(@Nullable final Object o) {
            return Maps.keyOrNull((Entry<K, ?>)HashBiMap.this.seekByValue(o, Hashing.smearedHash(o)));
        }
        
        @Override
        public BiMap<K, V> inverse() {
            return this.forward();
        }
        
        @Override
        public Set<V> keySet() {
            return (Set<V>)new InverseKeySet();
        }
        
        @Override
        public K put(@Nullable final V v, @Nullable final K k) {
            return (K)HashBiMap.this.putInverse(v, k, false);
        }
        
        @Override
        public K remove(@Nullable final Object o) {
            final BiEntry access$400 = HashBiMap.this.seekByValue(o, Hashing.smearedHash(o));
            if (access$400 != null) {
                HashBiMap.this.delete(access$400);
                access$400.prevInKeyInsertionOrder = null;
                access$400.nextInKeyInsertionOrder = null;
                return (K)access$400.key;
            }
            return null;
        }
        
        @Override
        public int size() {
            return HashBiMap.this.size;
        }
        
        @Override
        public Set<K> values() {
            return this.forward().keySet();
        }
        
        Object writeReplace() {
            return new InverseSerializedForm(HashBiMap.this);
        }
        
        private final class InverseKeySet extends Maps.KeySet<V, K>
        {
            InverseKeySet() {
                super(Inverse.this);
            }
            
            @Override
            public Iterator<V> iterator() {
                return new Itr<V>() {
                    @Override
                    V output(final BiEntry<K, V> biEntry) {
                        return biEntry.value;
                    }
                };
            }
            
            @Override
            public boolean remove(@Nullable final Object o) {
                final BiEntry access$400 = HashBiMap.this.seekByValue(o, Hashing.smearedHash(o));
                if (access$400 != null) {
                    HashBiMap.this.delete(access$400);
                    return true;
                }
                return false;
            }
        }
    }
    
    abstract class Itr<T> implements Iterator<T>
    {
        int expectedModCount;
        BiEntry<K, V> next;
        BiEntry<K, V> toRemove;
        
        Itr() {
            this.next = HashBiMap.this.firstInKeyInsertionOrder;
            this.toRemove = null;
            this.expectedModCount = HashBiMap.this.modCount;
        }
        
        @Override
        public boolean hasNext() {
            if (HashBiMap.this.modCount == this.expectedModCount) {
                return this.next != null;
            }
            throw new ConcurrentModificationException();
        }
        
        @Override
        public T next() {
            if (this.hasNext()) {
                final BiEntry<K, V> next = this.next;
                this.next = next.nextInKeyInsertionOrder;
                this.toRemove = next;
                return this.output(next);
            }
            throw new NoSuchElementException();
        }
        
        abstract T output(final BiEntry<K, V> p0);
        
        @Override
        public void remove() {
            if (HashBiMap.this.modCount == this.expectedModCount) {
                CollectPreconditions.checkRemove(this.toRemove != null);
                HashBiMap.this.delete((BiEntry)this.toRemove);
                this.expectedModCount = HashBiMap.this.modCount;
                this.toRemove = null;
                return;
            }
            throw new ConcurrentModificationException();
        }
    }
    
    private static final class InverseSerializedForm<K, V> implements Serializable
    {
        private final HashBiMap<K, V> bimap;
        
        InverseSerializedForm(final HashBiMap<K, V> bimap) {
            this.bimap = bimap;
        }
        
        Object readResolve() {
            return this.bimap.inverse();
        }
    }
    
    private final class KeySet extends Maps.KeySet<K, V>
    {
        KeySet() {
            super(HashBiMap.this);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new Itr<K>() {
                @Override
                K output(final BiEntry<K, V> biEntry) {
                    return biEntry.key;
                }
            };
        }
        
        @Override
        public boolean remove(@Nullable final Object o) {
            final BiEntry access$300 = HashBiMap.this.seekByKey(o, Hashing.smearedHash(o));
            if (access$300 != null) {
                HashBiMap.this.delete(access$300);
                access$300.prevInKeyInsertionOrder = null;
                access$300.nextInKeyInsertionOrder = null;
                return true;
            }
            return false;
        }
    }
}
