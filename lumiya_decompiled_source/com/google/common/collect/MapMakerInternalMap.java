package com.google.common.collect;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.collect.GenericMapMaker;
import com.google.common.collect.MapMaker;
import com.google.common.primitives.Ints;
import com.google.j2objc.annotations.Weak;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MapMakerInternalMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    static final long CLEANUP_EXECUTOR_DELAY_SECS = 60;
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final int DRAIN_MAX = 16;
    static final int DRAIN_THRESHOLD = 63;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    private static final long serialVersionUID = 5;
    final int concurrencyLevel;
    final transient EntryFactory entryFactory;
    transient Set<Map.Entry<K, V>> entrySet;
    final long expireAfterAccessNanos;
    final long expireAfterWriteNanos;
    final Equivalence<Object> keyEquivalence;
    transient Set<K> keySet;
    final Strength keyStrength;
    final int maximumSize;
    final MapMaker.RemovalListener<K, V> removalListener;
    final Queue<MapMaker.RemovalNotification<K, V>> removalNotificationQueue;
    final transient int segmentMask;
    final transient int segmentShift;
    final transient Segment<K, V>[] segments;
    final Ticker ticker;
    final Equivalence<Object> valueEquivalence;
    final Strength valueStrength;
    transient Collection<V> values;
    private static final Logger logger = Logger.getLogger(MapMakerInternalMap.class.getName());
    static final ValueReference<Object, Object> UNSET = new ValueReference<Object, Object>() { // from class: com.google.common.collect.MapMakerInternalMap.1
        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public void clear(ValueReference<Object, Object> valueReference) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ValueReference<Object, Object> copyFor(ReferenceQueue<Object> referenceQueue, @Nullable Object obj, ReferenceEntry<Object, Object> referenceEntry) {
            return this;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public Object get() {
            return null;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ReferenceEntry<Object, Object> getEntry() {
            return null;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public boolean isComputingReference() {
            return false;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public Object waitForValue() {
            return null;
        }
    };
    static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue<Object>() { // from class: com.google.common.collect.MapMakerInternalMap.2
        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<Object> iterator() {
            return Iterators.emptyIterator();
        }

        @Override // java.util.Queue
        public boolean offer(Object obj) {
            return true;
        }

        @Override // java.util.Queue
        public Object peek() {
            return null;
        }

        @Override // java.util.Queue
        public Object poll() {
            return null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return 0;
        }
    };

    /* loaded from: classes.dex */
    static abstract class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V> {
        AbstractReferenceEntry() {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public int getHash() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public K getKey() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNext() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ValueReference<K, V> getValueReference() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setValueReference(ValueReference<K, V> valueReference) {
            throw new UnsupportedOperationException();
        }
    }

    /* loaded from: classes.dex */
    static abstract class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V> implements Serializable {
        private static final long serialVersionUID = 3;
        final int concurrencyLevel;
        transient ConcurrentMap<K, V> delegate;
        final long expireAfterAccessNanos;
        final long expireAfterWriteNanos;
        final Equivalence<Object> keyEquivalence;
        final Strength keyStrength;
        final int maximumSize;
        final MapMaker.RemovalListener<? super K, ? super V> removalListener;
        final Equivalence<Object> valueEquivalence;
        final Strength valueStrength;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AbstractSerializationProxy(Strength strength, Strength strength2, Equivalence<Object> equivalence, Equivalence<Object> equivalence2, long j, long j2, int i, int i2, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> concurrentMap) {
            this.keyStrength = strength;
            this.valueStrength = strength2;
            this.keyEquivalence = equivalence;
            this.valueEquivalence = equivalence2;
            this.expireAfterWriteNanos = j;
            this.expireAfterAccessNanos = j2;
            this.maximumSize = i;
            this.concurrencyLevel = i2;
            this.removalListener = removalListener;
            this.delegate = concurrentMap;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.common.collect.ForwardingConcurrentMap, com.google.common.collect.ForwardingMap, com.google.common.collect.ForwardingObject
        public ConcurrentMap<K, V> delegate() {
            return this.delegate;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Multi-variable type inference failed */
        public void readEntries(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            while (true) {
                Object readObject = objectInputStream.readObject();
                if (readObject == null) {
                    return;
                }
                this.delegate.put(readObject, objectInputStream.readObject());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* JADX WARN: Type inference failed for: r2v1, types: [com.google.common.collect.MapMaker] */
        /* JADX WARN: Type inference failed for: r3v5, types: [com.google.common.collect.MapMaker] */
        public MapMaker readMapMaker(ObjectInputStream objectInputStream) throws IOException {
            ?? concurrencyLevel = new MapMaker().initialCapacity(objectInputStream.readInt()).setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence2(this.keyEquivalence).concurrencyLevel(this.concurrencyLevel);
            concurrencyLevel.removalListener(this.removalListener);
            if (!(this.expireAfterWriteNanos <= 0)) {
                concurrencyLevel.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
            }
            if (!(this.expireAfterAccessNanos <= 0)) {
                concurrencyLevel.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
            }
            if (this.maximumSize != -1) {
                concurrencyLevel.maximumSize(this.maximumSize);
            }
            return concurrencyLevel;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void writeMapTo(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeInt(this.delegate.size());
            for (Map.Entry<K, V> entry : this.delegate.entrySet()) {
                objectOutputStream.writeObject(entry.getKey());
                objectOutputStream.writeObject(entry.getValue());
            }
            objectOutputStream.writeObject(null);
        }
    }

    /* loaded from: classes.dex */
    static final class CleanupMapTask implements Runnable {
        final WeakReference<MapMakerInternalMap<?, ?>> mapReference;

        public CleanupMapTask(MapMakerInternalMap<?, ?> mapMakerInternalMap) {
            this.mapReference = new WeakReference<>(mapMakerInternalMap);
        }

        @Override // java.lang.Runnable
        public void run() {
            MapMakerInternalMap<?, ?> mapMakerInternalMap = this.mapReference.get();
            if (mapMakerInternalMap == null) {
                throw new CancellationException();
            }
            Segment<?, ?>[] segmentArr = mapMakerInternalMap.segments;
            for (Segment<?, ?> segment : segmentArr) {
                segment.runCleanup();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum EntryFactory {
        STRONG { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.1
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new StrongEntry(k, i, referenceEntry);
            }
        },
        STRONG_EXPIRABLE { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.2
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                copyExpirableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new StrongExpirableEntry(k, i, referenceEntry);
            }
        },
        STRONG_EVICTABLE { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.3
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new StrongEvictableEntry(k, i, referenceEntry);
            }
        },
        STRONG_EXPIRABLE_EVICTABLE { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.4
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                copyExpirableEntry(referenceEntry, copyEntry);
                copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new StrongExpirableEvictableEntry(k, i, referenceEntry);
            }
        },
        WEAK { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.5
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new WeakEntry(segment.keyReferenceQueue, k, i, referenceEntry);
            }
        },
        WEAK_EXPIRABLE { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.6
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                copyExpirableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new WeakExpirableEntry(segment.keyReferenceQueue, k, i, referenceEntry);
            }
        },
        WEAK_EVICTABLE { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.7
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new WeakEvictableEntry(segment.keyReferenceQueue, k, i, referenceEntry);
            }
        },
        WEAK_EXPIRABLE_EVICTABLE { // from class: com.google.common.collect.MapMakerInternalMap.EntryFactory.8
            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                copyExpirableEntry(referenceEntry, copyEntry);
                copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.EntryFactory
            <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
                return new WeakExpirableEvictableEntry(segment.keyReferenceQueue, k, i, referenceEntry);
            }
        };
        
        static final int EVICTABLE_MASK = 2;
        static final int EXPIRABLE_MASK = 1;
        static final EntryFactory[][] factories = {new EntryFactory[]{STRONG, STRONG_EXPIRABLE, STRONG_EVICTABLE, STRONG_EXPIRABLE_EVICTABLE}, new EntryFactory[0], new EntryFactory[]{WEAK, WEAK_EXPIRABLE, WEAK_EVICTABLE, WEAK_EXPIRABLE_EVICTABLE}};

        static EntryFactory getFactory(Strength strength, boolean z, boolean z2) {
            return factories[strength.ordinal()][(z2 ? (char) 2 : (char) 0) | (!z ? (char) 0 : (char) 1)];
        }

        <K, V> ReferenceEntry<K, V> copyEntry(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
            return newEntry(segment, referenceEntry.getKey(), referenceEntry.getHash(), referenceEntry2);
        }

        <K, V> void copyEvictableEntry(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
            MapMakerInternalMap.connectEvictables(referenceEntry.getPreviousEvictable(), referenceEntry2);
            MapMakerInternalMap.connectEvictables(referenceEntry2, referenceEntry.getNextEvictable());
            MapMakerInternalMap.nullifyEvictable(referenceEntry);
        }

        <K, V> void copyExpirableEntry(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
            referenceEntry2.setExpirationTime(referenceEntry.getExpirationTime());
            MapMakerInternalMap.connectExpirables(referenceEntry.getPreviousExpirable(), referenceEntry2);
            MapMakerInternalMap.connectExpirables(referenceEntry2, referenceEntry.getNextExpirable());
            MapMakerInternalMap.nullifyExpirable(referenceEntry);
        }

        abstract <K, V> ReferenceEntry<K, V> newEntry(Segment<K, V> segment, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry);
    }

    /* loaded from: classes.dex */
    final class EntryIterator extends MapMakerInternalMap<K, V>.HashIterator<Map.Entry<K, V>> {
        EntryIterator() {
            super();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.HashIterator, java.util.Iterator
        public Map.Entry<K, V> next() {
            return nextEntry();
        }
    }

    /* loaded from: classes.dex */
    final class EntrySet extends SafeToArraySet<Map.Entry<K, V>> {
        EntrySet() {
            super();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            MapMakerInternalMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            Map.Entry entry;
            Object key;
            Object obj2;
            return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && (obj2 = MapMakerInternalMap.this.get(key)) != null && MapMakerInternalMap.this.valueEquivalence.equivalent(entry.getValue(), obj2);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            Map.Entry entry;
            Object key;
            return (obj instanceof Map.Entry) && (key = (entry = (Map.Entry) obj).getKey()) != null && MapMakerInternalMap.this.remove(key, entry.getValue());
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return MapMakerInternalMap.this.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class EvictionQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>> {
        final ReferenceEntry<K, V> head = new AbstractReferenceEntry<K, V>() { // from class: com.google.common.collect.MapMakerInternalMap.EvictionQueue.1
            ReferenceEntry<K, V> nextEvictable = this;
            ReferenceEntry<K, V> previousEvictable = this;

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public ReferenceEntry<K, V> getNextEvictable() {
                return this.nextEvictable;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public ReferenceEntry<K, V> getPreviousEvictable() {
                return this.previousEvictable;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
                this.nextEvictable = referenceEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
                this.previousEvictable = referenceEntry;
            }
        };

        EvictionQueue() {
        }

        @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
        public void clear() {
            ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable();
            while (nextEvictable != this.head) {
                ReferenceEntry<K, V> nextEvictable2 = nextEvictable.getNextEvictable();
                MapMakerInternalMap.nullifyEvictable(nextEvictable);
                nextEvictable = nextEvictable2;
            }
            this.head.setNextEvictable(this.head);
            this.head.setPreviousEvictable(this.head);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object obj) {
            return ((ReferenceEntry) obj).getNextEvictable() != NullEntry.INSTANCE;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.head.getNextEvictable() == this.head;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(peek()) { // from class: com.google.common.collect.MapMakerInternalMap.EvictionQueue.2
                protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> referenceEntry) {
                    ReferenceEntry<K, V> nextEvictable = referenceEntry.getNextEvictable();
                    if (nextEvictable != EvictionQueue.this.head) {
                        return nextEvictable;
                    }
                    return null;
                }

                @Override // com.google.common.collect.AbstractSequentialIterator
                protected /* bridge */ /* synthetic */ Object computeNext(Object obj) {
                    return computeNext((ReferenceEntry) ((ReferenceEntry) obj));
                }
            };
        }

        public boolean offer(ReferenceEntry<K, V> referenceEntry) {
            MapMakerInternalMap.connectEvictables(referenceEntry.getPreviousEvictable(), referenceEntry.getNextEvictable());
            MapMakerInternalMap.connectEvictables(this.head.getPreviousEvictable(), referenceEntry);
            MapMakerInternalMap.connectEvictables(referenceEntry, this.head);
            return true;
        }

        @Override // java.util.Queue
        public /* bridge */ /* synthetic */ boolean offer(Object obj) {
            return offer((ReferenceEntry) ((ReferenceEntry) obj));
        }

        @Override // java.util.Queue
        public ReferenceEntry<K, V> peek() {
            ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable();
            if (nextEvictable != this.head) {
                return nextEvictable;
            }
            return null;
        }

        @Override // java.util.Queue
        public ReferenceEntry<K, V> poll() {
            ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable();
            if (nextEvictable != this.head) {
                remove(nextEvictable);
                return nextEvictable;
            }
            return null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object obj) {
            ReferenceEntry referenceEntry = (ReferenceEntry) obj;
            ReferenceEntry<K, V> previousEvictable = referenceEntry.getPreviousEvictable();
            ReferenceEntry<K, V> nextEvictable = referenceEntry.getNextEvictable();
            MapMakerInternalMap.connectEvictables(previousEvictable, nextEvictable);
            MapMakerInternalMap.nullifyEvictable(referenceEntry);
            return nextEvictable != NullEntry.INSTANCE;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            int i = 0;
            for (ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable(); nextEvictable != this.head; nextEvictable = nextEvictable.getNextEvictable()) {
                i++;
            }
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class ExpirationQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>> {
        final ReferenceEntry<K, V> head = new AbstractReferenceEntry<K, V>() { // from class: com.google.common.collect.MapMakerInternalMap.ExpirationQueue.1
            ReferenceEntry<K, V> nextExpirable = this;
            ReferenceEntry<K, V> previousExpirable = this;

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public long getExpirationTime() {
                return Long.MAX_VALUE;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public ReferenceEntry<K, V> getNextExpirable() {
                return this.nextExpirable;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public ReferenceEntry<K, V> getPreviousExpirable() {
                return this.previousExpirable;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public void setExpirationTime(long j) {
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
                this.nextExpirable = referenceEntry;
            }

            @Override // com.google.common.collect.MapMakerInternalMap.AbstractReferenceEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
            public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
                this.previousExpirable = referenceEntry;
            }
        };

        ExpirationQueue() {
        }

        @Override // java.util.AbstractQueue, java.util.AbstractCollection, java.util.Collection
        public void clear() {
            ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable();
            while (nextExpirable != this.head) {
                ReferenceEntry<K, V> nextExpirable2 = nextExpirable.getNextExpirable();
                MapMakerInternalMap.nullifyExpirable(nextExpirable);
                nextExpirable = nextExpirable2;
            }
            this.head.setNextExpirable(this.head);
            this.head.setPreviousExpirable(this.head);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object obj) {
            return ((ReferenceEntry) obj).getNextExpirable() != NullEntry.INSTANCE;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return this.head.getNextExpirable() == this.head;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(peek()) { // from class: com.google.common.collect.MapMakerInternalMap.ExpirationQueue.2
                protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> referenceEntry) {
                    ReferenceEntry<K, V> nextExpirable = referenceEntry.getNextExpirable();
                    if (nextExpirable != ExpirationQueue.this.head) {
                        return nextExpirable;
                    }
                    return null;
                }

                @Override // com.google.common.collect.AbstractSequentialIterator
                protected /* bridge */ /* synthetic */ Object computeNext(Object obj) {
                    return computeNext((ReferenceEntry) ((ReferenceEntry) obj));
                }
            };
        }

        public boolean offer(ReferenceEntry<K, V> referenceEntry) {
            MapMakerInternalMap.connectExpirables(referenceEntry.getPreviousExpirable(), referenceEntry.getNextExpirable());
            MapMakerInternalMap.connectExpirables(this.head.getPreviousExpirable(), referenceEntry);
            MapMakerInternalMap.connectExpirables(referenceEntry, this.head);
            return true;
        }

        @Override // java.util.Queue
        public /* bridge */ /* synthetic */ boolean offer(Object obj) {
            return offer((ReferenceEntry) ((ReferenceEntry) obj));
        }

        @Override // java.util.Queue
        public ReferenceEntry<K, V> peek() {
            ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable();
            if (nextExpirable != this.head) {
                return nextExpirable;
            }
            return null;
        }

        @Override // java.util.Queue
        public ReferenceEntry<K, V> poll() {
            ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable();
            if (nextExpirable != this.head) {
                remove(nextExpirable);
                return nextExpirable;
            }
            return null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean remove(Object obj) {
            ReferenceEntry referenceEntry = (ReferenceEntry) obj;
            ReferenceEntry<K, V> previousExpirable = referenceEntry.getPreviousExpirable();
            ReferenceEntry<K, V> nextExpirable = referenceEntry.getNextExpirable();
            MapMakerInternalMap.connectExpirables(previousExpirable, nextExpirable);
            MapMakerInternalMap.nullifyExpirable(referenceEntry);
            return nextExpirable != NullEntry.INSTANCE;
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            int i = 0;
            for (ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable(); nextExpirable != this.head; nextExpirable = nextExpirable.getNextExpirable()) {
                i++;
            }
            return i;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public abstract class HashIterator<E> implements Iterator<E> {
        Segment<K, V> currentSegment;
        AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
        MapMakerInternalMap<K, V>.WriteThroughEntry lastReturned;
        ReferenceEntry<K, V> nextEntry;
        MapMakerInternalMap<K, V>.WriteThroughEntry nextExternal;
        int nextSegmentIndex;
        int nextTableIndex = -1;

        HashIterator() {
            this.nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
            advance();
        }

        final void advance() {
            this.nextExternal = null;
            if (nextInChain() || nextInTable()) {
                return;
            }
            while (this.nextSegmentIndex >= 0) {
                Segment<K, V>[] segmentArr = MapMakerInternalMap.this.segments;
                int i = this.nextSegmentIndex;
                this.nextSegmentIndex = i - 1;
                this.currentSegment = segmentArr[i];
                if (this.currentSegment.count != 0) {
                    this.currentTable = this.currentSegment.table;
                    this.nextTableIndex = this.currentTable.length() - 1;
                    if (nextInTable()) {
                        return;
                    }
                }
            }
        }

        boolean advanceTo(ReferenceEntry<K, V> referenceEntry) {
            try {
                K key = referenceEntry.getKey();
                Object liveValue = MapMakerInternalMap.this.getLiveValue(referenceEntry);
                if (liveValue == null) {
                    return false;
                }
                this.nextExternal = new WriteThroughEntry(key, liveValue);
                return true;
            } finally {
                this.currentSegment.postReadCleanup();
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.nextExternal != null;
        }

        @Override // java.util.Iterator
        public abstract E next();

        MapMakerInternalMap<K, V>.WriteThroughEntry nextEntry() {
            if (this.nextExternal != null) {
                this.lastReturned = this.nextExternal;
                advance();
                return this.lastReturned;
            }
            throw new NoSuchElementException();
        }

        boolean nextInChain() {
            if (this.nextEntry != null) {
                ReferenceEntry<K, V> next = this.nextEntry.getNext();
                while (true) {
                    this.nextEntry = next;
                    if (this.nextEntry == null) {
                        break;
                    } else if (advanceTo(this.nextEntry)) {
                        return true;
                    } else {
                        next = this.nextEntry.getNext();
                    }
                }
            }
            return false;
        }

        boolean nextInTable() {
            while (this.nextTableIndex >= 0) {
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.currentTable;
                int i = this.nextTableIndex;
                this.nextTableIndex = i - 1;
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(i);
                this.nextEntry = referenceEntry;
                if (referenceEntry != null && (advanceTo(this.nextEntry) || nextInChain())) {
                    return true;
                }
            }
            return false;
        }

        @Override // java.util.Iterator
        public void remove() {
            CollectPreconditions.checkRemove(this.lastReturned != null);
            MapMakerInternalMap.this.remove(this.lastReturned.getKey());
            this.lastReturned = null;
        }
    }

    /* loaded from: classes.dex */
    final class KeyIterator extends MapMakerInternalMap<K, V>.HashIterator<K> {
        KeyIterator() {
            super();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.HashIterator, java.util.Iterator
        public K next() {
            return nextEntry().getKey();
        }
    }

    /* loaded from: classes.dex */
    final class KeySet extends SafeToArraySet<K> {
        KeySet() {
            super();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            MapMakerInternalMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object obj) {
            return MapMakerInternalMap.this.containsKey(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object obj) {
            return MapMakerInternalMap.this.remove(obj) != null;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return MapMakerInternalMap.this.size();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public enum NullEntry implements ReferenceEntry<Object, Object> {
        INSTANCE;

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return 0L;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public int getHash() {
            return 0;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public Object getKey() {
            return null;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<Object, Object> getNext() {
            return null;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<Object, Object> getNextEvictable() {
            return this;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<Object, Object> getNextExpirable() {
            return this;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<Object, Object> getPreviousEvictable() {
            return this;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<Object, Object> getPreviousExpirable() {
            return this;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ValueReference<Object, Object> getValueReference() {
            return null;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<Object, Object> referenceEntry) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<Object, Object> referenceEntry) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<Object, Object> referenceEntry) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<Object, Object> referenceEntry) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setValueReference(ValueReference<Object, Object> valueReference) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ReferenceEntry<K, V> {
        long getExpirationTime();

        int getHash();

        K getKey();

        ReferenceEntry<K, V> getNext();

        ReferenceEntry<K, V> getNextEvictable();

        ReferenceEntry<K, V> getNextExpirable();

        ReferenceEntry<K, V> getPreviousEvictable();

        ReferenceEntry<K, V> getPreviousExpirable();

        ValueReference<K, V> getValueReference();

        void setExpirationTime(long j);

        void setNextEvictable(ReferenceEntry<K, V> referenceEntry);

        void setNextExpirable(ReferenceEntry<K, V> referenceEntry);

        void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry);

        void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry);

        void setValueReference(ValueReference<K, V> valueReference);
    }

    /* loaded from: classes.dex */
    private static abstract class SafeToArraySet<E> extends AbstractSet<E> {
        private SafeToArraySet() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public Object[] toArray() {
            return MapMakerInternalMap.toArrayList(this).toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public <E> E[] toArray(E[] eArr) {
            return (E[]) MapMakerInternalMap.toArrayList(this).toArray(eArr);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Segment<K, V> extends ReentrantLock {
        volatile int count;
        @GuardedBy("this")
        final Queue<ReferenceEntry<K, V>> evictionQueue;
        @GuardedBy("this")
        final Queue<ReferenceEntry<K, V>> expirationQueue;
        final ReferenceQueue<K> keyReferenceQueue;
        @Weak
        final MapMakerInternalMap<K, V> map;
        final int maxSegmentSize;
        int modCount;
        final AtomicInteger readCount = new AtomicInteger();
        final Queue<ReferenceEntry<K, V>> recencyQueue;
        volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
        int threshold;
        final ReferenceQueue<V> valueReferenceQueue;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Segment(MapMakerInternalMap<K, V> mapMakerInternalMap, int i, int i2) {
            this.map = mapMakerInternalMap;
            this.maxSegmentSize = i2;
            initTable(newEntryArray(i));
            this.keyReferenceQueue = !mapMakerInternalMap.usesKeyReferences() ? null : new ReferenceQueue<>();
            this.valueReferenceQueue = mapMakerInternalMap.usesValueReferences() ? new ReferenceQueue<>() : null;
            this.recencyQueue = (mapMakerInternalMap.evictsBySize() || mapMakerInternalMap.expiresAfterAccess()) ? new ConcurrentLinkedQueue<>() : MapMakerInternalMap.discardingQueue();
            this.evictionQueue = !mapMakerInternalMap.evictsBySize() ? MapMakerInternalMap.discardingQueue() : new EvictionQueue<>();
            this.expirationQueue = !mapMakerInternalMap.expires() ? MapMakerInternalMap.discardingQueue() : new ExpirationQueue<>();
        }

        void clear() {
            if (this.count == 0) {
                return;
            }
            lock();
            try {
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
                    for (int i = 0; i < atomicReferenceArray.length(); i++) {
                        for (ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(i); referenceEntry != null; referenceEntry = referenceEntry.getNext()) {
                            if (!referenceEntry.getValueReference().isComputingReference()) {
                                enqueueNotification(referenceEntry, MapMaker.RemovalCause.EXPLICIT);
                            }
                        }
                    }
                }
                for (int i2 = 0; i2 < atomicReferenceArray.length(); i2++) {
                    atomicReferenceArray.set(i2, null);
                }
                clearReferenceQueues();
                this.evictionQueue.clear();
                this.expirationQueue.clear();
                this.readCount.set(0);
                this.modCount++;
                this.count = 0;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        void clearKeyReferenceQueue() {
            do {
            } while (this.keyReferenceQueue.poll() != null);
        }

        void clearReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                clearKeyReferenceQueue();
            }
            if (this.map.usesValueReferences()) {
                clearValueReferenceQueue();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public boolean clearValue(K k, int i, ValueReference<K, V> valueReference) {
            lock();
            try {
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (referenceEntry2.getValueReference() != valueReference) {
                            return false;
                        }
                        atomicReferenceArray.set(length, removeFromChain(referenceEntry, referenceEntry2));
                        return true;
                    }
                }
                return false;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        void clearValueReferenceQueue() {
            do {
            } while (this.valueReferenceQueue.poll() != null);
        }

        boolean containsKey(Object obj, int i) {
            try {
                if (this.count == 0) {
                    return false;
                }
                ReferenceEntry<K, V> liveEntry = getLiveEntry(obj, i);
                if (liveEntry != null) {
                    return liveEntry.getValueReference().get() != null;
                }
                return false;
            } finally {
                postReadCleanup();
            }
        }

        @VisibleForTesting
        boolean containsValue(Object obj) {
            try {
                if (this.count != 0) {
                    AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                    int length = atomicReferenceArray.length();
                    for (int i = 0; i < length; i++) {
                        for (ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(i); referenceEntry != null; referenceEntry = referenceEntry.getNext()) {
                            V liveValue = getLiveValue(referenceEntry);
                            if (liveValue != null && this.map.valueEquivalence.equivalent(obj, liveValue)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            } finally {
                postReadCleanup();
            }
        }

        @GuardedBy("this")
        ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
            if (referenceEntry.getKey() != null) {
                ValueReference<K, V> valueReference = referenceEntry.getValueReference();
                V v = valueReference.get();
                if (v == null && !valueReference.isComputingReference()) {
                    return null;
                }
                ReferenceEntry<K, V> copyEntry = this.map.entryFactory.copyEntry(this, referenceEntry, referenceEntry2);
                copyEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, v, copyEntry));
                return copyEntry;
            }
            return null;
        }

        @GuardedBy("this")
        void drainKeyReferenceQueue() {
            int i = 0;
            while (true) {
                int i2 = i;
                Reference<? extends K> poll = this.keyReferenceQueue.poll();
                if (poll == null) {
                    return;
                }
                this.map.reclaimKey((ReferenceEntry) poll);
                i = i2 + 1;
                if (i == 16) {
                    return;
                }
            }
        }

        @GuardedBy("this")
        void drainRecencyQueue() {
            while (true) {
                ReferenceEntry<K, V> poll = this.recencyQueue.poll();
                if (poll == null) {
                    return;
                }
                if (this.evictionQueue.contains(poll)) {
                    this.evictionQueue.add(poll);
                }
                if (this.map.expiresAfterAccess() && this.expirationQueue.contains(poll)) {
                    this.expirationQueue.add(poll);
                }
            }
        }

        @GuardedBy("this")
        void drainReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                drainKeyReferenceQueue();
            }
            if (this.map.usesValueReferences()) {
                drainValueReferenceQueue();
            }
        }

        @GuardedBy("this")
        void drainValueReferenceQueue() {
            int i = 0;
            while (true) {
                int i2 = i;
                Reference<? extends V> poll = this.valueReferenceQueue.poll();
                if (poll == null) {
                    return;
                }
                this.map.reclaimValue((ValueReference) poll);
                i = i2 + 1;
                if (i == 16) {
                    return;
                }
            }
        }

        void enqueueNotification(ReferenceEntry<K, V> referenceEntry, MapMaker.RemovalCause removalCause) {
            enqueueNotification(referenceEntry.getKey(), referenceEntry.getHash(), referenceEntry.getValueReference().get(), removalCause);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void enqueueNotification(@Nullable K k, int i, @Nullable V v, MapMaker.RemovalCause removalCause) {
            if (this.map.removalNotificationQueue == MapMakerInternalMap.DISCARDING_QUEUE) {
                return;
            }
            this.map.removalNotificationQueue.offer(new MapMaker.RemovalNotification<>(k, v, removalCause));
        }

        @GuardedBy("this")
        boolean evictEntries() {
            if (this.map.evictsBySize() && this.count >= this.maxSegmentSize) {
                drainRecencyQueue();
                ReferenceEntry<K, V> remove = this.evictionQueue.remove();
                if (removeEntry(remove, remove.getHash(), MapMaker.RemovalCause.SIZE)) {
                    return true;
                }
                throw new AssertionError();
            }
            return false;
        }

        @GuardedBy("this")
        void expand() {
            int i;
            int i2;
            ReferenceEntry<K, V> referenceEntry;
            AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
            int length = atomicReferenceArray.length();
            if (length < 1073741824) {
                int i3 = this.count;
                AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray = newEntryArray(length << 1);
                this.threshold = (newEntryArray.length() * 3) / 4;
                int length2 = newEntryArray.length() - 1;
                int i4 = 0;
                while (i4 < length) {
                    ReferenceEntry<K, V> referenceEntry2 = atomicReferenceArray.get(i4);
                    if (referenceEntry2 == null) {
                        i = i3;
                    } else {
                        ReferenceEntry<K, V> next = referenceEntry2.getNext();
                        int hash = referenceEntry2.getHash() & length2;
                        if (next != null) {
                            ReferenceEntry<K, V> referenceEntry3 = referenceEntry2;
                            while (next != null) {
                                int hash2 = next.getHash() & length2;
                                if (hash2 == hash) {
                                    referenceEntry = referenceEntry3;
                                } else {
                                    hash = hash2;
                                    referenceEntry = next;
                                }
                                next = next.getNext();
                                referenceEntry3 = referenceEntry;
                            }
                            newEntryArray.set(hash, referenceEntry3);
                            ReferenceEntry<K, V> referenceEntry4 = referenceEntry2;
                            i = i3;
                            while (referenceEntry4 != referenceEntry3) {
                                int hash3 = referenceEntry4.getHash() & length2;
                                ReferenceEntry<K, V> copyEntry = copyEntry(referenceEntry4, newEntryArray.get(hash3));
                                if (copyEntry == null) {
                                    removeCollectedEntry(referenceEntry4);
                                    i2 = i - 1;
                                } else {
                                    newEntryArray.set(hash3, copyEntry);
                                    i2 = i;
                                }
                                referenceEntry4 = referenceEntry4.getNext();
                                i = i2;
                            }
                        } else {
                            newEntryArray.set(hash, referenceEntry2);
                            i = i3;
                        }
                    }
                    i4++;
                    i3 = i;
                }
                this.table = newEntryArray;
                this.count = i3;
            }
        }

        @GuardedBy("this")
        void expireEntries() {
            ReferenceEntry<K, V> peek;
            drainRecencyQueue();
            if (this.expirationQueue.isEmpty()) {
                return;
            }
            long read = this.map.ticker.read();
            do {
                peek = this.expirationQueue.peek();
                if (peek == null || !this.map.isExpired(peek, read)) {
                    return;
                }
            } while (removeEntry(peek, peek.getHash(), MapMaker.RemovalCause.EXPIRED));
            throw new AssertionError();
        }

        V get(Object obj, int i) {
            try {
                ReferenceEntry<K, V> liveEntry = getLiveEntry(obj, i);
                if (liveEntry != null) {
                    V v = liveEntry.getValueReference().get();
                    if (v == null) {
                        tryDrainReferenceQueues();
                    } else {
                        recordRead(liveEntry);
                    }
                    return v;
                }
                return null;
            } finally {
                postReadCleanup();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ReferenceEntry<K, V> getEntry(Object obj, int i) {
            if (this.count != 0) {
                for (ReferenceEntry<K, V> first = getFirst(i); first != null; first = first.getNext()) {
                    if (first.getHash() == i) {
                        K key = first.getKey();
                        if (key == null) {
                            tryDrainReferenceQueues();
                        } else if (this.map.keyEquivalence.equivalent(obj, key)) {
                            return first;
                        }
                    }
                }
            }
            return null;
        }

        ReferenceEntry<K, V> getFirst(int i) {
            AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
            return atomicReferenceArray.get((atomicReferenceArray.length() - 1) & i);
        }

        ReferenceEntry<K, V> getLiveEntry(Object obj, int i) {
            ReferenceEntry<K, V> entry = getEntry(obj, i);
            if (entry != null) {
                if (this.map.expires() && this.map.isExpired(entry)) {
                    tryExpireEntries();
                    return null;
                }
                return entry;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public V getLiveValue(ReferenceEntry<K, V> referenceEntry) {
            if (referenceEntry.getKey() == null) {
                tryDrainReferenceQueues();
                return null;
            }
            V v = referenceEntry.getValueReference().get();
            if (v == null) {
                tryDrainReferenceQueues();
                return null;
            } else if (this.map.expires() && this.map.isExpired(referenceEntry)) {
                tryExpireEntries();
                return null;
            } else {
                return v;
            }
        }

        void initTable(AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray) {
            this.threshold = (atomicReferenceArray.length() * 3) / 4;
            if (this.threshold == this.maxSegmentSize) {
                this.threshold++;
            }
            this.table = atomicReferenceArray;
        }

        boolean isCollected(ValueReference<K, V> valueReference) {
            return !valueReference.isComputingReference() && valueReference.get() == null;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @GuardedBy("this")
        public ReferenceEntry<K, V> newEntry(K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            return this.map.entryFactory.newEntry(this, k, i, referenceEntry);
        }

        AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(int i) {
            return new AtomicReferenceArray<>(i);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 63) != 0) {
                return;
            }
            runCleanup();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void postWriteCleanup() {
            runUnlockedCleanup();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @GuardedBy("this")
        public void preWriteCleanup() {
            runLockedCleanup();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public V put(K k, int i, V v, boolean z) {
            lock();
            try {
                preWriteCleanup();
                int i2 = this.count + 1;
                if (i2 > this.threshold) {
                    expand();
                    i2 = this.count + 1;
                }
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        ValueReference<K, V> valueReference = referenceEntry2.getValueReference();
                        V v2 = valueReference.get();
                        if (v2 != null) {
                            if (z) {
                                recordLockedRead(referenceEntry2);
                                return v2;
                            }
                            this.modCount++;
                            enqueueNotification(k, i, v2, MapMaker.RemovalCause.REPLACED);
                            setValue(referenceEntry2, v);
                            return v2;
                        }
                        this.modCount++;
                        setValue(referenceEntry2, v);
                        if (!valueReference.isComputingReference()) {
                            enqueueNotification(k, i, v2, MapMaker.RemovalCause.COLLECTED);
                            i2 = this.count;
                        } else if (evictEntries()) {
                            i2 = this.count + 1;
                        }
                        this.count = i2;
                        return null;
                    }
                }
                this.modCount++;
                ReferenceEntry<K, V> newEntry = newEntry(k, i, referenceEntry);
                setValue(newEntry, v);
                atomicReferenceArray.set(length, newEntry);
                this.count = !evictEntries() ? i2 : this.count + 1;
                return null;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        boolean reclaimKey(ReferenceEntry<K, V> referenceEntry, int i) {
            lock();
            try {
                int i2 = this.count;
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry2 = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry3 = referenceEntry2; referenceEntry3 != null; referenceEntry3 = referenceEntry3.getNext()) {
                    if (referenceEntry3 == referenceEntry) {
                        this.modCount++;
                        enqueueNotification(referenceEntry3.getKey(), i, referenceEntry3.getValueReference().get(), MapMaker.RemovalCause.COLLECTED);
                        atomicReferenceArray.set(length, removeFromChain(referenceEntry2, referenceEntry3));
                        this.count--;
                        return true;
                    }
                }
                return false;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        boolean reclaimValue(K k, int i, ValueReference<K, V> valueReference) {
            lock();
            try {
                int i2 = this.count;
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (referenceEntry2.getValueReference() != valueReference) {
                            unlock();
                            if (!isHeldByCurrentThread()) {
                                postWriteCleanup();
                            }
                            return false;
                        }
                        this.modCount++;
                        enqueueNotification(k, i, valueReference.get(), MapMaker.RemovalCause.COLLECTED);
                        atomicReferenceArray.set(length, removeFromChain(referenceEntry, referenceEntry2));
                        this.count--;
                        unlock();
                        if (!isHeldByCurrentThread()) {
                            postWriteCleanup();
                        }
                        return true;
                    }
                }
                return false;
            } finally {
                unlock();
                if (!isHeldByCurrentThread()) {
                    postWriteCleanup();
                }
            }
        }

        void recordExpirationTime(ReferenceEntry<K, V> referenceEntry, long j) {
            referenceEntry.setExpirationTime(this.map.ticker.read() + j);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @GuardedBy("this")
        public void recordLockedRead(ReferenceEntry<K, V> referenceEntry) {
            this.evictionQueue.add(referenceEntry);
            if (this.map.expiresAfterAccess()) {
                recordExpirationTime(referenceEntry, this.map.expireAfterAccessNanos);
                this.expirationQueue.add(referenceEntry);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void recordRead(ReferenceEntry<K, V> referenceEntry) {
            if (this.map.expiresAfterAccess()) {
                recordExpirationTime(referenceEntry, this.map.expireAfterAccessNanos);
            }
            this.recencyQueue.add(referenceEntry);
        }

        @GuardedBy("this")
        void recordWrite(ReferenceEntry<K, V> referenceEntry) {
            drainRecencyQueue();
            this.evictionQueue.add(referenceEntry);
            if (this.map.expires()) {
                recordExpirationTime(referenceEntry, !this.map.expiresAfterAccess() ? this.map.expireAfterWriteNanos : this.map.expireAfterAccessNanos);
                this.expirationQueue.add(referenceEntry);
            }
        }

        V remove(Object obj, int i) {
            MapMaker.RemovalCause removalCause;
            lock();
            try {
                preWriteCleanup();
                int i2 = this.count;
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(obj, key)) {
                        ValueReference<K, V> valueReference = referenceEntry2.getValueReference();
                        V v = valueReference.get();
                        if (v != null) {
                            removalCause = MapMaker.RemovalCause.EXPLICIT;
                        } else if (!isCollected(valueReference)) {
                            return null;
                        } else {
                            removalCause = MapMaker.RemovalCause.COLLECTED;
                        }
                        this.modCount++;
                        enqueueNotification(key, i, v, removalCause);
                        atomicReferenceArray.set(length, removeFromChain(referenceEntry, referenceEntry2));
                        this.count--;
                        return v;
                    }
                }
                return null;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        boolean remove(Object obj, int i, Object obj2) {
            MapMaker.RemovalCause removalCause;
            lock();
            try {
                preWriteCleanup();
                int i2 = this.count;
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(obj, key)) {
                        ValueReference<K, V> valueReference = referenceEntry2.getValueReference();
                        V v = valueReference.get();
                        if (this.map.valueEquivalence.equivalent(obj2, v)) {
                            removalCause = MapMaker.RemovalCause.EXPLICIT;
                        } else if (!isCollected(valueReference)) {
                            return false;
                        } else {
                            removalCause = MapMaker.RemovalCause.COLLECTED;
                        }
                        this.modCount++;
                        enqueueNotification(key, i, v, removalCause);
                        atomicReferenceArray.set(length, removeFromChain(referenceEntry, referenceEntry2));
                        this.count--;
                        return removalCause == MapMaker.RemovalCause.EXPLICIT;
                    }
                }
                return false;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        void removeCollectedEntry(ReferenceEntry<K, V> referenceEntry) {
            enqueueNotification(referenceEntry, MapMaker.RemovalCause.COLLECTED);
            this.evictionQueue.remove(referenceEntry);
            this.expirationQueue.remove(referenceEntry);
        }

        @GuardedBy("this")
        boolean removeEntry(ReferenceEntry<K, V> referenceEntry, int i, MapMaker.RemovalCause removalCause) {
            int i2 = this.count;
            AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
            int length = i & (atomicReferenceArray.length() - 1);
            ReferenceEntry<K, V> referenceEntry2 = atomicReferenceArray.get(length);
            for (ReferenceEntry<K, V> referenceEntry3 = referenceEntry2; referenceEntry3 != null; referenceEntry3 = referenceEntry3.getNext()) {
                if (referenceEntry3 == referenceEntry) {
                    this.modCount++;
                    enqueueNotification(referenceEntry3.getKey(), i, referenceEntry3.getValueReference().get(), removalCause);
                    atomicReferenceArray.set(length, removeFromChain(referenceEntry2, referenceEntry3));
                    this.count--;
                    return true;
                }
            }
            return false;
        }

        @GuardedBy("this")
        ReferenceEntry<K, V> removeFromChain(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
            int i;
            this.evictionQueue.remove(referenceEntry2);
            this.expirationQueue.remove(referenceEntry2);
            int i2 = this.count;
            ReferenceEntry<K, V> next = referenceEntry2.getNext();
            while (referenceEntry != referenceEntry2) {
                ReferenceEntry<K, V> copyEntry = copyEntry(referenceEntry, next);
                if (copyEntry == null) {
                    removeCollectedEntry(referenceEntry);
                    i = i2 - 1;
                } else {
                    next = copyEntry;
                    i = i2;
                }
                referenceEntry = referenceEntry.getNext();
                i2 = i;
            }
            this.count = i2;
            return next;
        }

        V replace(K k, int i, V v) {
            lock();
            try {
                preWriteCleanup();
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        ValueReference<K, V> valueReference = referenceEntry2.getValueReference();
                        V v2 = valueReference.get();
                        if (v2 != null) {
                            this.modCount++;
                            enqueueNotification(k, i, v2, MapMaker.RemovalCause.REPLACED);
                            setValue(referenceEntry2, v);
                            return v2;
                        }
                        if (isCollected(valueReference)) {
                            int i2 = this.count;
                            this.modCount++;
                            enqueueNotification(key, i, v2, MapMaker.RemovalCause.COLLECTED);
                            atomicReferenceArray.set(length, removeFromChain(referenceEntry, referenceEntry2));
                            this.count--;
                        }
                        return null;
                    }
                }
                return null;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        boolean replace(K k, int i, V v, V v2) {
            lock();
            try {
                preWriteCleanup();
                AtomicReferenceArray<ReferenceEntry<K, V>> atomicReferenceArray = this.table;
                int length = i & (atomicReferenceArray.length() - 1);
                ReferenceEntry<K, V> referenceEntry = atomicReferenceArray.get(length);
                for (ReferenceEntry<K, V> referenceEntry2 = referenceEntry; referenceEntry2 != null; referenceEntry2 = referenceEntry2.getNext()) {
                    K key = referenceEntry2.getKey();
                    if (referenceEntry2.getHash() == i && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        ValueReference<K, V> valueReference = referenceEntry2.getValueReference();
                        V v3 = valueReference.get();
                        if (v3 != null) {
                            if (!this.map.valueEquivalence.equivalent(v, v3)) {
                                recordLockedRead(referenceEntry2);
                                return false;
                            }
                            this.modCount++;
                            enqueueNotification(k, i, v3, MapMaker.RemovalCause.REPLACED);
                            setValue(referenceEntry2, v2);
                            return true;
                        }
                        if (isCollected(valueReference)) {
                            int i2 = this.count;
                            this.modCount++;
                            enqueueNotification(key, i, v3, MapMaker.RemovalCause.COLLECTED);
                            atomicReferenceArray.set(length, removeFromChain(referenceEntry, referenceEntry2));
                            this.count--;
                        }
                        return false;
                    }
                }
                return false;
            } finally {
                unlock();
                postWriteCleanup();
            }
        }

        void runCleanup() {
            runLockedCleanup();
            runUnlockedCleanup();
        }

        void runLockedCleanup() {
            if (tryLock()) {
                try {
                    drainReferenceQueues();
                    expireEntries();
                    this.readCount.set(0);
                } finally {
                    unlock();
                }
            }
        }

        void runUnlockedCleanup() {
            if (isHeldByCurrentThread()) {
                return;
            }
            this.map.processPendingNotifications();
        }

        @GuardedBy("this")
        void setValue(ReferenceEntry<K, V> referenceEntry, V v) {
            referenceEntry.setValueReference(this.map.valueStrength.referenceValue(this, referenceEntry, v));
            recordWrite(referenceEntry);
        }

        void tryDrainReferenceQueues() {
            if (tryLock()) {
                try {
                    drainReferenceQueues();
                } finally {
                    unlock();
                }
            }
        }

        void tryExpireEntries() {
            if (tryLock()) {
                try {
                    expireEntries();
                } finally {
                    unlock();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class SerializationProxy<K, V> extends AbstractSerializationProxy<K, V> {
        private static final long serialVersionUID = 3;

        SerializationProxy(Strength strength, Strength strength2, Equivalence<Object> equivalence, Equivalence<Object> equivalence2, long j, long j2, int i, int i2, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> concurrentMap) {
            super(strength, strength2, equivalence, equivalence2, j, j2, i, i2, removalListener, concurrentMap);
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.delegate = readMapMaker(objectInputStream).makeMap();
            readEntries(objectInputStream);
        }

        private Object readResolve() {
            return this.delegate;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            writeMapTo(objectOutputStream);
        }
    }

    /* loaded from: classes.dex */
    static class SoftEntry<K, V> extends SoftReference<K> implements ReferenceEntry<K, V> {
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;

        SoftEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(k, referenceQueue);
            this.valueReference = MapMakerInternalMap.unset();
            this.hash = i;
            this.next = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public int getHash() {
            return this.hash;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public K getKey() {
            return get();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setValueReference(ValueReference<K, V> valueReference) {
            ValueReference<K, V> valueReference2 = this.valueReference;
            this.valueReference = valueReference;
            valueReference2.clear(valueReference);
        }
    }

    /* loaded from: classes.dex */
    static final class SoftEvictableEntry<K, V> extends SoftEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> previousEvictable;

        SoftEvictableEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, i, referenceEntry);
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.nextEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.previousEvictable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class SoftExpirableEntry<K, V> extends SoftEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;

        SoftExpirableEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, i, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return this.time;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            this.time = j;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.nextExpirable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.previousExpirable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class SoftExpirableEvictableEntry<K, V> extends SoftEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousEvictable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;

        SoftExpirableEvictableEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, i, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return this.time;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            this.time = j;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.nextEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.nextExpirable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.previousEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.SoftEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.previousExpirable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V> {
        final ReferenceEntry<K, V> entry;

        SoftValueReference(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            super(v, referenceQueue);
            this.entry = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public void clear(ValueReference<K, V> valueReference) {
            clear();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            return new SoftValueReference(referenceQueue, v, referenceEntry);
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public boolean isComputingReference() {
            return false;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public V waitForValue() {
            return get();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum Strength {
        STRONG { // from class: com.google.common.collect.MapMakerInternalMap.Strength.1
            @Override // com.google.common.collect.MapMakerInternalMap.Strength
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }

            @Override // com.google.common.collect.MapMakerInternalMap.Strength
            <K, V> ValueReference<K, V> referenceValue(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, V v) {
                return new StrongValueReference(v);
            }
        },
        SOFT { // from class: com.google.common.collect.MapMakerInternalMap.Strength.2
            @Override // com.google.common.collect.MapMakerInternalMap.Strength
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }

            @Override // com.google.common.collect.MapMakerInternalMap.Strength
            <K, V> ValueReference<K, V> referenceValue(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, V v) {
                return new SoftValueReference(segment.valueReferenceQueue, v, referenceEntry);
            }
        },
        WEAK { // from class: com.google.common.collect.MapMakerInternalMap.Strength.3
            @Override // com.google.common.collect.MapMakerInternalMap.Strength
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }

            @Override // com.google.common.collect.MapMakerInternalMap.Strength
            <K, V> ValueReference<K, V> referenceValue(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, V v) {
                return new WeakValueReference(segment.valueReferenceQueue, v, referenceEntry);
            }
        };

        /* JADX INFO: Access modifiers changed from: package-private */
        public abstract Equivalence<Object> defaultEquivalence();

        abstract <K, V> ValueReference<K, V> referenceValue(Segment<K, V> segment, ReferenceEntry<K, V> referenceEntry, V v);
    }

    /* loaded from: classes.dex */
    static class StrongEntry<K, V> implements ReferenceEntry<K, V> {
        final int hash;
        final K key;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference = MapMakerInternalMap.unset();

        StrongEntry(K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            this.key = k;
            this.hash = i;
            this.next = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public int getHash() {
            return this.hash;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public K getKey() {
            return this.key;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setValueReference(ValueReference<K, V> valueReference) {
            ValueReference<K, V> valueReference2 = this.valueReference;
            this.valueReference = valueReference;
            valueReference2.clear(valueReference);
        }
    }

    /* loaded from: classes.dex */
    static final class StrongEvictableEntry<K, V> extends StrongEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> previousEvictable;

        StrongEvictableEntry(K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(k, i, referenceEntry);
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.nextEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.previousEvictable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class StrongExpirableEntry<K, V> extends StrongEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;

        StrongExpirableEntry(K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(k, i, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return this.time;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            this.time = j;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.nextExpirable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.previousExpirable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class StrongExpirableEvictableEntry<K, V> extends StrongEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousEvictable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;

        StrongExpirableEvictableEntry(K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(k, i, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return this.time;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            this.time = j;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.nextEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.nextExpirable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.previousEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.StrongEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.previousExpirable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class StrongValueReference<K, V> implements ValueReference<K, V> {
        final V referent;

        StrongValueReference(V v) {
            this.referent = v;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public void clear(ValueReference<K, V> valueReference) {
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            return this;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public V get() {
            return this.referent;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public boolean isComputingReference() {
            return false;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public V waitForValue() {
            return get();
        }
    }

    /* loaded from: classes.dex */
    final class ValueIterator extends MapMakerInternalMap<K, V>.HashIterator<V> {
        ValueIterator() {
            super();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.HashIterator, java.util.Iterator
        public V next() {
            return nextEntry().getValue();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ValueReference<K, V> {
        void clear(@Nullable ValueReference<K, V> valueReference);

        ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, @Nullable V v, ReferenceEntry<K, V> referenceEntry);

        V get();

        ReferenceEntry<K, V> getEntry();

        boolean isComputingReference();

        V waitForValue() throws ExecutionException;
    }

    /* loaded from: classes.dex */
    final class Values extends AbstractCollection<V> {
        Values() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public void clear() {
            MapMakerInternalMap.this.clear();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean contains(Object obj) {
            return MapMakerInternalMap.this.containsValue(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public int size() {
            return MapMakerInternalMap.this.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public Object[] toArray() {
            return MapMakerInternalMap.toArrayList(this).toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection
        public <E> E[] toArray(E[] eArr) {
            return (E[]) MapMakerInternalMap.toArrayList(this).toArray(eArr);
        }
    }

    /* loaded from: classes.dex */
    static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V> {
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;

        WeakEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(k, referenceQueue);
            this.valueReference = MapMakerInternalMap.unset();
            this.hash = i;
            this.next = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public int getHash() {
            return this.hash;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public K getKey() {
            return (K) get();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setValueReference(ValueReference<K, V> valueReference) {
            ValueReference<K, V> valueReference2 = this.valueReference;
            this.valueReference = valueReference;
            valueReference2.clear(valueReference);
        }
    }

    /* loaded from: classes.dex */
    static final class WeakEvictableEntry<K, V> extends WeakEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> previousEvictable;

        WeakEvictableEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, i, referenceEntry);
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.nextEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.previousEvictable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class WeakExpirableEntry<K, V> extends WeakEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;

        WeakExpirableEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, i, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return this.time;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            this.time = j;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.nextExpirable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.previousExpirable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class WeakExpirableEvictableEntry<K, V> extends WeakEntry<K, V> implements ReferenceEntry<K, V> {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousEvictable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;

        WeakExpirableEvictableEntry(ReferenceQueue<K> referenceQueue, K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, i, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public long getExpirationTime() {
            return this.time;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setExpirationTime(long j) {
            this.time = j;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.nextEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setNextExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.nextExpirable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousEvictable(ReferenceEntry<K, V> referenceEntry) {
            this.previousEvictable = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.WeakEntry, com.google.common.collect.MapMakerInternalMap.ReferenceEntry
        public void setPreviousExpirable(ReferenceEntry<K, V> referenceEntry) {
            this.previousExpirable = referenceEntry;
        }
    }

    /* loaded from: classes.dex */
    static final class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V> {
        final ReferenceEntry<K, V> entry;

        WeakValueReference(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            super(v, referenceQueue);
            this.entry = referenceEntry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public void clear(ValueReference<K, V> valueReference) {
            clear();
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ValueReference<K, V> copyFor(ReferenceQueue<V> referenceQueue, V v, ReferenceEntry<K, V> referenceEntry) {
            return new WeakValueReference(referenceQueue, v, referenceEntry);
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public boolean isComputingReference() {
            return false;
        }

        @Override // com.google.common.collect.MapMakerInternalMap.ValueReference
        public V waitForValue() {
            return get();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class WriteThroughEntry extends AbstractMapEntry<K, V> {
        final K key;
        V value;

        WriteThroughEntry(K k, V v) {
            this.key = k;
            this.value = v;
        }

        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Map.Entry) {
                Map.Entry entry = (Map.Entry) obj;
                return this.key.equals(entry.getKey()) && this.value.equals(entry.getValue());
            }
            return false;
        }

        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
        public K getKey() {
            return this.key;
        }

        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
        public V getValue() {
            return this.value;
        }

        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }

        @Override // com.google.common.collect.AbstractMapEntry, java.util.Map.Entry
        public V setValue(V v) {
            V v2 = (V) MapMakerInternalMap.this.put(this.key, v);
            this.value = v;
            return v2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MapMakerInternalMap(MapMaker mapMaker) {
        int i = 1;
        int i2 = 0;
        this.concurrencyLevel = Math.min(mapMaker.getConcurrencyLevel(), 65536);
        this.keyStrength = mapMaker.getKeyStrength();
        this.valueStrength = mapMaker.getValueStrength();
        this.keyEquivalence = mapMaker.getKeyEquivalence();
        this.valueEquivalence = this.valueStrength.defaultEquivalence();
        this.maximumSize = mapMaker.maximumSize;
        this.expireAfterAccessNanos = mapMaker.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = mapMaker.getExpireAfterWriteNanos();
        this.entryFactory = EntryFactory.getFactory(this.keyStrength, expires(), evictsBySize());
        this.ticker = mapMaker.getTicker();
        this.removalListener = mapMaker.getRemovalListener();
        this.removalNotificationQueue = this.removalListener != GenericMapMaker.NullListener.INSTANCE ? new ConcurrentLinkedQueue<>() : discardingQueue();
        int min = Math.min(mapMaker.getInitialCapacity(), 1073741824);
        min = evictsBySize() ? Math.min(min, this.maximumSize) : min;
        int i3 = 1;
        int i4 = 0;
        while (i3 < this.concurrencyLevel && (!evictsBySize() || i3 * 2 <= this.maximumSize)) {
            i4++;
            i3 <<= 1;
        }
        this.segmentShift = 32 - i4;
        this.segmentMask = i3 - 1;
        this.segments = newSegmentArray(i3);
        int i5 = min / i3;
        while (i < (i5 * i3 >= min ? i5 : i5 + 1)) {
            i <<= 1;
        }
        if (!evictsBySize()) {
            while (i2 < this.segments.length) {
                this.segments[i2] = createSegment(i, -1);
                i2++;
            }
            return;
        }
        int i6 = (this.maximumSize / i3) + 1;
        int i7 = this.maximumSize % i3;
        while (i2 < this.segments.length) {
            if (i2 == i7) {
                i6--;
            }
            this.segments[i2] = createSegment(i, i6);
            i2++;
        }
    }

    static <K, V> void connectEvictables(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
        referenceEntry.setNextEvictable(referenceEntry2);
        referenceEntry2.setPreviousEvictable(referenceEntry);
    }

    static <K, V> void connectExpirables(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
        referenceEntry.setNextExpirable(referenceEntry2);
        referenceEntry2.setPreviousExpirable(referenceEntry);
    }

    static <E> Queue<E> discardingQueue() {
        return (Queue<E>) DISCARDING_QUEUE;
    }

    static <K, V> ReferenceEntry<K, V> nullEntry() {
        return NullEntry.INSTANCE;
    }

    static <K, V> void nullifyEvictable(ReferenceEntry<K, V> referenceEntry) {
        ReferenceEntry<K, V> nullEntry = nullEntry();
        referenceEntry.setNextEvictable(nullEntry);
        referenceEntry.setPreviousEvictable(nullEntry);
    }

    static <K, V> void nullifyExpirable(ReferenceEntry<K, V> referenceEntry) {
        ReferenceEntry<K, V> nullEntry = nullEntry();
        referenceEntry.setNextExpirable(nullEntry);
        referenceEntry.setPreviousExpirable(nullEntry);
    }

    static int rehash(int i) {
        int i2 = ((i << 15) ^ (-12931)) + i;
        int i3 = i2 ^ (i2 >>> 10);
        int i4 = i3 + (i3 << 3);
        int i5 = i4 ^ (i4 >>> 6);
        int i6 = i5 + (i5 << 2) + (i5 << 14);
        return i6 ^ (i6 >>> 16);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <E> ArrayList<E> toArrayList(Collection<E> collection) {
        ArrayList<E> arrayList = new ArrayList<>(collection.size());
        Iterators.addAll(arrayList, collection.iterator());
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> ValueReference<K, V> unset() {
        return (ValueReference<K, V>) UNSET;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        for (Segment<K, V> segment : this.segments) {
            segment.clear();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(@Nullable Object obj) {
        if (obj != null) {
            int hash = hash(obj);
            return segmentFor(hash).containsKey(obj, hash);
        }
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x002f, code lost:
        r4 = r4 + r3.modCount;
        r2 = r2 + 1;
     */
    @Override // java.util.AbstractMap, java.util.Map
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean containsValue(@javax.annotation.Nullable java.lang.Object r15) {
        /*
            r14 = this;
            if (r15 == 0) goto Le
            com.google.common.collect.MapMakerInternalMap$Segment<K, V>[] r7 = r14.segments
            r4 = -1
            r0 = 0
            r6 = r0
            r8 = r4
        L9:
            r0 = 3
            if (r6 < r0) goto L10
        Lc:
            r0 = 0
            return r0
        Le:
            r0 = 0
            return r0
        L10:
            r2 = 0
            int r10 = r7.length
            r0 = 0
            r4 = r2
            r2 = r0
        L16:
            if (r2 < r10) goto L21
            int r0 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r0 == 0) goto Lc
            int r0 = r6 + 1
            r6 = r0
            r8 = r4
            goto L9
        L21:
            r3 = r7[r2]
            int r0 = r3.count
            java.util.concurrent.atomic.AtomicReferenceArray<com.google.common.collect.MapMakerInternalMap$ReferenceEntry<K, V>> r11 = r3.table
            r0 = 0
            r1 = r0
        L29:
            int r0 = r11.length()
            if (r1 < r0) goto L37
            int r0 = r3.modCount
            long r0 = (long) r0
            long r4 = r4 + r0
            int r0 = r2 + 1
            r2 = r0
            goto L16
        L37:
            java.lang.Object r0 = r11.get(r1)
            com.google.common.collect.MapMakerInternalMap$ReferenceEntry r0 = (com.google.common.collect.MapMakerInternalMap.ReferenceEntry) r0
        L3d:
            if (r0 != 0) goto L43
            int r0 = r1 + 1
            r1 = r0
            goto L29
        L43:
            java.lang.Object r12 = r3.getLiveValue(r0)
            if (r12 != 0) goto L4e
        L49:
            com.google.common.collect.MapMakerInternalMap$ReferenceEntry r0 = r0.getNext()
            goto L3d
        L4e:
            com.google.common.base.Equivalence<java.lang.Object> r13 = r14.valueEquivalence
            boolean r12 = r13.equivalent(r15, r12)
            if (r12 == 0) goto L49
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.MapMakerInternalMap.containsValue(java.lang.Object):boolean");
    }

    @VisibleForTesting
    ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> referenceEntry, ReferenceEntry<K, V> referenceEntry2) {
        return segmentFor(referenceEntry.getHash()).copyEntry(referenceEntry, referenceEntry2);
    }

    Segment<K, V> createSegment(int i, int i2) {
        return new Segment<>(this, i, i2);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = this.entrySet;
        if (set == null) {
            EntrySet entrySet = new EntrySet();
            this.entrySet = entrySet;
            return entrySet;
        }
        return set;
    }

    boolean evictsBySize() {
        return this.maximumSize != -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean expires() {
        return expiresAfterWrite() || expiresAfterAccess();
    }

    boolean expiresAfterAccess() {
        return !((this.expireAfterAccessNanos > 0L ? 1 : (this.expireAfterAccessNanos == 0L ? 0 : -1)) <= 0);
    }

    boolean expiresAfterWrite() {
        return !((this.expireAfterWriteNanos > 0L ? 1 : (this.expireAfterWriteNanos == 0L ? 0 : -1)) <= 0);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V get(@Nullable Object obj) {
        if (obj != null) {
            int hash = hash(obj);
            return segmentFor(hash).get(obj, hash);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ReferenceEntry<K, V> getEntry(@Nullable Object obj) {
        if (obj != null) {
            int hash = hash(obj);
            return segmentFor(hash).getEntry(obj, hash);
        }
        return null;
    }

    V getLiveValue(ReferenceEntry<K, V> referenceEntry) {
        V v;
        if (referenceEntry.getKey() == null || (v = referenceEntry.getValueReference().get()) == null) {
            return null;
        }
        if (expires() && isExpired(referenceEntry)) {
            return null;
        }
        return v;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int hash(Object obj) {
        return rehash(this.keyEquivalence.hash(obj));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        Segment<K, V>[] segmentArr = this.segments;
        long j = 0;
        for (int i = 0; i < segmentArr.length; i++) {
            if (segmentArr[i].count != 0) {
                return false;
            }
            j += segmentArr[i].modCount;
        }
        if (j != 0) {
            for (int i2 = 0; i2 < segmentArr.length; i2++) {
                if (segmentArr[i2].count != 0) {
                    return false;
                }
                j -= segmentArr[i2].modCount;
            }
            return j == 0;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isExpired(ReferenceEntry<K, V> referenceEntry) {
        return isExpired(referenceEntry, this.ticker.read());
    }

    boolean isExpired(ReferenceEntry<K, V> referenceEntry, long j) {
        return !(((j - referenceEntry.getExpirationTime()) > 0L ? 1 : ((j - referenceEntry.getExpirationTime()) == 0L ? 0 : -1)) <= 0);
    }

    @VisibleForTesting
    boolean isLive(ReferenceEntry<K, V> referenceEntry) {
        return segmentFor(referenceEntry.getHash()).getLiveValue(referenceEntry) != null;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<K> keySet() {
        Set<K> set = this.keySet;
        if (set == null) {
            KeySet keySet = new KeySet();
            this.keySet = keySet;
            return keySet;
        }
        return set;
    }

    @VisibleForTesting
    ReferenceEntry<K, V> newEntry(K k, int i, @Nullable ReferenceEntry<K, V> referenceEntry) {
        return segmentFor(i).newEntry(k, i, referenceEntry);
    }

    final Segment<K, V>[] newSegmentArray(int i) {
        return new Segment[i];
    }

    @VisibleForTesting
    ValueReference<K, V> newValueReference(ReferenceEntry<K, V> referenceEntry, V v) {
        return this.valueStrength.referenceValue(segmentFor(referenceEntry.getHash()), referenceEntry, v);
    }

    void processPendingNotifications() {
        while (true) {
            MapMaker.RemovalNotification<K, V> poll = this.removalNotificationQueue.poll();
            if (poll == null) {
                return;
            }
            try {
                this.removalListener.onRemoval(poll);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception thrown by removal listener", (Throwable) e);
            }
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V put(K k, V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        int hash = hash(k);
        return segmentFor(hash).put(k, hash, v, false);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V putIfAbsent(K k, V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        int hash = hash(k);
        return segmentFor(hash).put(k, hash, v, true);
    }

    void reclaimKey(ReferenceEntry<K, V> referenceEntry) {
        int hash = referenceEntry.getHash();
        segmentFor(hash).reclaimKey(referenceEntry, hash);
    }

    void reclaimValue(ValueReference<K, V> valueReference) {
        ReferenceEntry<K, V> entry = valueReference.getEntry();
        int hash = entry.getHash();
        segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public V remove(@Nullable Object obj) {
        if (obj != null) {
            int hash = hash(obj);
            return segmentFor(hash).remove(obj, hash);
        }
        return null;
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean remove(@Nullable Object obj, @Nullable Object obj2) {
        if (obj == null || obj2 == null) {
            return false;
        }
        int hash = hash(obj);
        return segmentFor(hash).remove(obj, hash, obj2);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public V replace(K k, V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        int hash = hash(k);
        return segmentFor(hash).replace(k, hash, v);
    }

    @Override // java.util.Map, java.util.concurrent.ConcurrentMap
    public boolean replace(K k, @Nullable V v, V v2) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v2);
        if (v != null) {
            int hash = hash(k);
            return segmentFor(hash).replace(k, hash, v, v2);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Segment<K, V> segmentFor(int i) {
        return this.segments[(i >>> this.segmentShift) & this.segmentMask];
    }

    @Override // java.util.AbstractMap, java.util.Map
    public int size() {
        long j = 0;
        for (Segment<K, V> segment : this.segments) {
            j += segment.count;
        }
        return Ints.saturatedCast(j);
    }

    boolean usesKeyReferences() {
        return this.keyStrength != Strength.STRONG;
    }

    boolean usesValueReferences() {
        return this.valueStrength != Strength.STRONG;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Collection<V> values() {
        Collection<V> collection = this.values;
        if (collection == null) {
            Values values = new Values();
            this.values = values;
            return values;
        }
        return collection;
    }

    Object writeReplace() {
        return new SerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this);
    }
}
