// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.ConcurrentModificationException;
import java.util.Arrays;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.LinkedHashMap;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
public final class LinkedHashMultimap<K, V> extends AbstractSetMultimap<K, V>
{
    private static final int DEFAULT_KEY_CAPACITY = 16;
    private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
    @VisibleForTesting
    static final double VALUE_SET_LOAD_FACTOR = 1.0;
    @GwtIncompatible("java serialization not supported")
    private static final long serialVersionUID = 1L;
    private transient ValueEntry<K, V> multimapHeaderEntry;
    @VisibleForTesting
    transient int valueSetCapacity;
    
    private LinkedHashMultimap(final int initialCapacity, final int valueSetCapacity) {
        super(new LinkedHashMap(initialCapacity));
        this.valueSetCapacity = 2;
        CollectPreconditions.checkNonnegative(valueSetCapacity, "expectedValuesPerKey");
        this.valueSetCapacity = valueSetCapacity;
        succeedsInMultimap(this.multimapHeaderEntry = new ValueEntry<K, V>(null, null, 0, null), this.multimapHeaderEntry);
    }
    
    public static <K, V> LinkedHashMultimap<K, V> create() {
        return new LinkedHashMultimap<K, V>(16, 2);
    }
    
    public static <K, V> LinkedHashMultimap<K, V> create(final int n, final int n2) {
        return new LinkedHashMultimap<K, V>(Maps.capacity(n), Maps.capacity(n2));
    }
    
    public static <K, V> LinkedHashMultimap<K, V> create(final Multimap<? extends K, ? extends V> multimap) {
        final LinkedHashMultimap<Object, Object> create = create(multimap.keySet().size(), 2);
        create.putAll(multimap);
        return (LinkedHashMultimap<K, V>)create;
    }
    
    private static <K, V> void deleteFromMultimap(final ValueEntry<K, V> valueEntry) {
        succeedsInMultimap(valueEntry.getPredecessorInMultimap(), valueEntry.getSuccessorInMultimap());
    }
    
    private static <K, V> void deleteFromValueSet(final ValueSetLink<K, V> valueSetLink) {
        succeedsInValueSet(valueSetLink.getPredecessorInValueSet(), valueSetLink.getSuccessorInValueSet());
    }
    
    @GwtIncompatible("java.io.ObjectInputStream")
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        succeedsInMultimap(this.multimapHeaderEntry = new ValueEntry<K, V>(null, null, 0, null), this.multimapHeaderEntry);
        this.valueSetCapacity = 2;
        final int int1 = objectInputStream.readInt();
        final LinkedHashMap map = new LinkedHashMap();
        for (int i = 0; i < int1; ++i) {
            final Object object = objectInputStream.readObject();
            map.put(object, this.createCollection((K)object));
        }
        for (int int2 = objectInputStream.readInt(), j = 0; j < int2; ++j) {
            ((Collection)map.get(objectInputStream.readObject())).add(objectInputStream.readObject());
        }
        this.setMap(map);
    }
    
    private static <K, V> void succeedsInMultimap(final ValueEntry<K, V> predecessorInMultimap, final ValueEntry<K, V> successorInMultimap) {
        predecessorInMultimap.setSuccessorInMultimap(successorInMultimap);
        successorInMultimap.setPredecessorInMultimap(predecessorInMultimap);
    }
    
    private static <K, V> void succeedsInValueSet(final ValueSetLink<K, V> predecessorInValueSet, final ValueSetLink<K, V> successorInValueSet) {
        predecessorInValueSet.setSuccessorInValueSet(successorInValueSet);
        successorInValueSet.setPredecessorInValueSet(predecessorInValueSet);
    }
    
    @GwtIncompatible("java.io.ObjectOutputStream")
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeInt(this.keySet().size());
        final Iterator iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            objectOutputStream.writeObject(iterator.next());
        }
        objectOutputStream.writeInt(this.size());
        for (final Map.Entry<Object, V> entry : this.entries()) {
            objectOutputStream.writeObject(entry.getKey());
            objectOutputStream.writeObject(entry.getValue());
        }
    }
    
    @Override
    public void clear() {
        super.clear();
        succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
    }
    
    @Override
    Collection<V> createCollection(final K k) {
        return new ValueSet(k, this.valueSetCapacity);
    }
    
    @Override
    Set<V> createCollection() {
        return new LinkedHashSet<V>(this.valueSetCapacity);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entries() {
        return super.entries();
    }
    
    @Override
    Iterator<Map.Entry<K, V>> entryIterator() {
        return new Iterator<Map.Entry<K, V>>() {
            ValueEntry<K, V> nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
            ValueEntry<K, V> toRemove;
            
            @Override
            public boolean hasNext() {
                return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
            }
            
            @Override
            public Map.Entry<K, V> next() {
                if (this.hasNext()) {
                    final ValueEntry<K, V> nextEntry = this.nextEntry;
                    this.toRemove = nextEntry;
                    this.nextEntry = this.nextEntry.successorInMultimap;
                    return nextEntry;
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(this.toRemove != null);
                LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
                this.toRemove = null;
            }
        };
    }
    
    @Override
    public Set<V> replaceValues(@Nullable final K k, final Iterable<? extends V> iterable) {
        return super.replaceValues(k, iterable);
    }
    
    @Override
    Iterator<V> valueIterator() {
        return Maps.valueIterator(this.entryIterator());
    }
    
    @Override
    public Collection<V> values() {
        return super.values();
    }
    
    @VisibleForTesting
    static final class ValueEntry<K, V> extends ImmutableEntry<K, V> implements ValueSetLink<K, V>
    {
        @Nullable
        ValueEntry<K, V> nextInValueBucket;
        ValueEntry<K, V> predecessorInMultimap;
        ValueSetLink<K, V> predecessorInValueSet;
        final int smearedValueHash;
        ValueEntry<K, V> successorInMultimap;
        ValueSetLink<K, V> successorInValueSet;
        
        ValueEntry(@Nullable final K k, @Nullable final V v, final int smearedValueHash, @Nullable final ValueEntry<K, V> nextInValueBucket) {
            super(k, v);
            this.smearedValueHash = smearedValueHash;
            this.nextInValueBucket = nextInValueBucket;
        }
        
        public ValueEntry<K, V> getPredecessorInMultimap() {
            return this.predecessorInMultimap;
        }
        
        @Override
        public ValueSetLink<K, V> getPredecessorInValueSet() {
            return this.predecessorInValueSet;
        }
        
        public ValueEntry<K, V> getSuccessorInMultimap() {
            return this.successorInMultimap;
        }
        
        @Override
        public ValueSetLink<K, V> getSuccessorInValueSet() {
            return this.successorInValueSet;
        }
        
        boolean matchesValue(@Nullable final Object o, final int n) {
            boolean b = false;
            if (this.smearedValueHash == n && Objects.equal(this.getValue(), o)) {
                b = true;
            }
            return b;
        }
        
        public void setPredecessorInMultimap(final ValueEntry<K, V> predecessorInMultimap) {
            this.predecessorInMultimap = predecessorInMultimap;
        }
        
        @Override
        public void setPredecessorInValueSet(final ValueSetLink<K, V> predecessorInValueSet) {
            this.predecessorInValueSet = predecessorInValueSet;
        }
        
        public void setSuccessorInMultimap(final ValueEntry<K, V> successorInMultimap) {
            this.successorInMultimap = successorInMultimap;
        }
        
        @Override
        public void setSuccessorInValueSet(final ValueSetLink<K, V> successorInValueSet) {
            this.successorInValueSet = successorInValueSet;
        }
    }
    
    @VisibleForTesting
    final class ValueSet extends ImprovedAbstractSet<V> implements ValueSetLink<K, V>
    {
        private ValueSetLink<K, V> firstEntry;
        @VisibleForTesting
        ValueEntry<K, V>[] hashTable;
        private final K key;
        private ValueSetLink<K, V> lastEntry;
        private int modCount;
        private int size;
        
        ValueSet(final K key, final int n) {
            this.size = 0;
            this.modCount = 0;
            this.key = key;
            this.firstEntry = this;
            this.lastEntry = this;
            this.hashTable = new ValueEntry[Hashing.closedTableSize(n, 1.0)];
        }
        
        private int mask() {
            return this.hashTable.length - 1;
        }
        
        private void rehashIfNecessary() {
            if (Hashing.needsResizing(this.size, this.hashTable.length, 1.0)) {
                final ValueEntry[] hashTable = new ValueEntry[this.hashTable.length * 2];
                this.hashTable = hashTable;
                final int length = hashTable.length;
                for (ValueSetLink<K, V> valueSetLink = this.firstEntry; valueSetLink != this; valueSetLink = (ValueSetLink<K, V>)valueSetLink.getSuccessorInValueSet()) {
                    final ValueEntry valueEntry = (ValueEntry)valueSetLink;
                    final int n = valueEntry.smearedValueHash & length - 1;
                    valueEntry.nextInValueBucket = hashTable[n];
                    hashTable[n] = valueEntry;
                }
            }
        }
        
        @Override
        public boolean add(@Nullable final V v) {
            final int smearedHash = Hashing.smearedHash(v);
            final int n = smearedHash & this.mask();
            ValueSetLink<K, V> nextInValueBucket;
            ValueEntry<K, V> valueEntry;
            for (valueEntry = (ValueEntry<K, V>)(nextInValueBucket = (ValueSetLink<K, V>)this.hashTable[n]); nextInValueBucket != null; nextInValueBucket = ((ValueEntry)nextInValueBucket).nextInValueBucket) {
                if (((ValueEntry)nextInValueBucket).matchesValue(v, smearedHash)) {
                    return false;
                }
            }
            final ValueEntry valueEntry2 = new ValueEntry(this.key, v, smearedHash, (ValueEntry<Object, Object>)valueEntry);
            succeedsInValueSet((ValueSetLink)this.lastEntry, (ValueSetLink<Object, Object>)valueEntry2);
            succeedsInValueSet((ValueSetLink)valueEntry2, (ValueSetLink<Object, Object>)this);
            succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), (ValueEntry<Object, Object>)valueEntry2);
            succeedsInMultimap(valueEntry2, (ValueEntry<Object, Object>)LinkedHashMultimap.this.multimapHeaderEntry);
            this.hashTable[n] = (ValueEntry<K, V>)valueEntry2;
            ++this.size;
            ++this.modCount;
            this.rehashIfNecessary();
            return true;
        }
        
        @Override
        public void clear() {
            Arrays.fill(this.hashTable, null);
            this.size = 0;
            for (ValueSetLink<K, V> valueSetLink = this.firstEntry; valueSetLink != this; valueSetLink = (ValueSetLink<K, V>)valueSetLink.getSuccessorInValueSet()) {
                deleteFromMultimap((ValueEntry<Object, Object>)(ValueEntry)valueSetLink);
            }
            succeedsInValueSet((ValueSetLink)this, (ValueSetLink<Object, Object>)this);
            ++this.modCount;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            final int smearedHash = Hashing.smearedHash(o);
            for (ValueSetLink<K, V> nextInValueBucket = (ValueSetLink<K, V>)this.hashTable[this.mask() & smearedHash]; nextInValueBucket != null; nextInValueBucket = ((ValueEntry)nextInValueBucket).nextInValueBucket) {
                if (((ValueEntry)nextInValueBucket).matchesValue(o, smearedHash)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public ValueSetLink<K, V> getPredecessorInValueSet() {
            return this.lastEntry;
        }
        
        @Override
        public ValueSetLink<K, V> getSuccessorInValueSet() {
            return this.firstEntry;
        }
        
        @Override
        public Iterator<V> iterator() {
            return new Iterator<V>() {
                int expectedModCount = ValueSet.this.modCount;
                ValueSetLink<K, V> nextEntry = ValueSet.this.firstEntry;
                ValueEntry<K, V> toRemove;
                
                private void checkForComodification() {
                    if (ValueSet.this.modCount == this.expectedModCount) {
                        return;
                    }
                    throw new ConcurrentModificationException();
                }
                
                @Override
                public boolean hasNext() {
                    this.checkForComodification();
                    return this.nextEntry != ValueSet.this;
                }
                
                @Override
                public V next() {
                    if (this.hasNext()) {
                        final ValueEntry toRemove = (ValueEntry)this.nextEntry;
                        final Object value = toRemove.getValue();
                        this.toRemove = toRemove;
                        this.nextEntry = toRemove.getSuccessorInValueSet();
                        return (V)value;
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public void remove() {
                    this.checkForComodification();
                    CollectPreconditions.checkRemove(this.toRemove != null);
                    ValueSet.this.remove(this.toRemove.getValue());
                    this.expectedModCount = ValueSet.this.modCount;
                    this.toRemove = null;
                }
            };
        }
        
        @Override
        public boolean remove(@Nullable final Object o) {
            ValueEntry valueEntry = null;
            final int smearedHash = Hashing.smearedHash(o);
            final int n = smearedHash & this.mask();
            ValueEntry<K, V> nextInValueBucket;
            for (ValueEntry<K, V> valueEntry2 = this.hashTable[n]; valueEntry2 != null; valueEntry2 = nextInValueBucket) {
                if (valueEntry2.matchesValue(o, smearedHash)) {
                    if (valueEntry != null) {
                        valueEntry.nextInValueBucket = (ValueEntry<K, V>)valueEntry2.nextInValueBucket;
                    }
                    else {
                        this.hashTable[n] = valueEntry2.nextInValueBucket;
                    }
                    deleteFromValueSet((ValueSetLink<Object, Object>)(ValueSetLink)valueEntry2);
                    deleteFromMultimap((ValueEntry<Object, Object>)valueEntry2);
                    --this.size;
                    ++this.modCount;
                    return true;
                }
                nextInValueBucket = valueEntry2.nextInValueBucket;
                valueEntry = valueEntry2;
            }
            return false;
        }
        
        @Override
        public void setPredecessorInValueSet(final ValueSetLink<K, V> lastEntry) {
            this.lastEntry = lastEntry;
        }
        
        @Override
        public void setSuccessorInValueSet(final ValueSetLink<K, V> firstEntry) {
            this.firstEntry = firstEntry;
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
    
    private interface ValueSetLink<K, V>
    {
        ValueSetLink<K, V> getPredecessorInValueSet();
        
        ValueSetLink<K, V> getSuccessorInValueSet();
        
        void setPredecessorInValueSet(final ValueSetLink<K, V> p0);
        
        void setSuccessorInValueSet(final ValueSetLink<K, V> p0);
    }
}
