// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.concurrent.ExecutionException;
import java.lang.ref.SoftReference;
import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.j2objc.annotations.Weak;
import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.locks.ReentrantLock;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.lang.ref.WeakReference;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.google.common.primitives.Ints;
import com.google.common.base.Preconditions;
import java.util.logging.Level;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;
import java.util.AbstractQueue;
import javax.annotation.Nullable;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import com.google.common.base.Ticker;
import com.google.common.base.Equivalence;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Queue;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

class MapMakerInternalMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
{
    static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final Queue<?> DISCARDING_QUEUE;
    static final int DRAIN_MAX = 16;
    static final int DRAIN_THRESHOLD = 63;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final ValueReference<Object, Object> UNSET;
    private static final Logger logger;
    private static final long serialVersionUID = 5L;
    final int concurrencyLevel;
    final transient EntryFactory entryFactory;
    transient Set<Entry<K, V>> entrySet;
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
    
    static {
        logger = Logger.getLogger(MapMakerInternalMap.class.getName());
        UNSET = (ValueReference)new ValueReference<Object, Object>() {
            @Override
            public void clear(final ValueReference<Object, Object> valueReference) {
            }
            
            @Override
            public ValueReference<Object, Object> copyFor(final ReferenceQueue<Object> referenceQueue, @Nullable final Object o, final ReferenceEntry<Object, Object> referenceEntry) {
                return this;
            }
            
            @Override
            public Object get() {
                return null;
            }
            
            @Override
            public ReferenceEntry<Object, Object> getEntry() {
                return null;
            }
            
            @Override
            public boolean isComputingReference() {
                return false;
            }
            
            @Override
            public Object waitForValue() {
                return null;
            }
        };
        DISCARDING_QUEUE = new AbstractQueue<Object>() {
            @Override
            public Iterator<Object> iterator() {
                return Iterators.emptyIterator();
            }
            
            @Override
            public boolean offer(final Object o) {
                return true;
            }
            
            @Override
            public Object peek() {
                return null;
            }
            
            @Override
            public Object poll() {
                return null;
            }
            
            @Override
            public int size() {
                return 0;
            }
        };
    }
    
    MapMakerInternalMap(final MapMaker mapMaker) {
        final int n = 1;
        final int n2 = 0;
        final int n3 = 0;
        this.concurrencyLevel = Math.min(mapMaker.getConcurrencyLevel(), 65536);
        this.keyStrength = mapMaker.getKeyStrength();
        this.valueStrength = mapMaker.getValueStrength();
        this.keyEquivalence = mapMaker.getKeyEquivalence();
        this.valueEquivalence = this.valueStrength.defaultEquivalence();
        this.maximumSize = mapMaker.maximumSize;
        this.expireAfterAccessNanos = mapMaker.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = mapMaker.getExpireAfterWriteNanos();
        this.entryFactory = EntryFactory.getFactory(this.keyStrength, this.expires(), this.evictsBySize());
        this.ticker = mapMaker.getTicker();
        this.removalListener = mapMaker.getRemovalListener();
        Queue<Object> discardingQueue;
        if (this.removalListener != GenericMapMaker.NullListener.INSTANCE) {
            discardingQueue = (Queue<Object>)new ConcurrentLinkedQueue<MapMaker.RemovalNotification<K, V>>();
        }
        else {
            discardingQueue = (Queue<Object>)discardingQueue();
        }
        this.removalNotificationQueue = (Queue<MapMaker.RemovalNotification<K, V>>)discardingQueue;
        int a = Math.min(mapMaker.getInitialCapacity(), 1073741824);
        if (this.evictsBySize()) {
            a = Math.min(a, this.maximumSize);
        }
        int n4 = 1;
        int n5 = 0;
        while (n4 < this.concurrencyLevel && (!this.evictsBySize() || n4 * 2 <= this.maximumSize)) {
            ++n5;
            n4 <<= 1;
        }
        this.segmentShift = 32 - n5;
        this.segmentMask = n4 - 1;
        this.segments = this.newSegmentArray(n4);
        int n6 = a / n4;
        int i;
        if (n6 * n4 >= a) {
            i = n;
        }
        else {
            ++n6;
            i = n;
        }
        while (i < n6) {
            i <<= 1;
        }
        if (!this.evictsBySize()) {
            for (int j = n3; j < this.segments.length; ++j) {
                this.segments[j] = this.createSegment(i, -1);
            }
        }
        else {
            final int n7 = this.maximumSize / n4 + 1;
            final int maximumSize = this.maximumSize;
            int k = n2;
            int n8 = n7;
            while (k < this.segments.length) {
                if (k == maximumSize % n4) {
                    --n8;
                }
                this.segments[k] = this.createSegment(i, n8);
                ++k;
            }
        }
    }
    
    static <K, V> void connectEvictables(final ReferenceEntry<K, V> previousEvictable, final ReferenceEntry<K, V> nextEvictable) {
        previousEvictable.setNextEvictable(nextEvictable);
        nextEvictable.setPreviousEvictable(previousEvictable);
    }
    
    static <K, V> void connectExpirables(final ReferenceEntry<K, V> previousExpirable, final ReferenceEntry<K, V> nextExpirable) {
        previousExpirable.setNextExpirable(nextExpirable);
        nextExpirable.setPreviousExpirable(previousExpirable);
    }
    
    static <E> Queue<E> discardingQueue() {
        return (Queue<E>)MapMakerInternalMap.DISCARDING_QUEUE;
    }
    
    static <K, V> ReferenceEntry<K, V> nullEntry() {
        return (ReferenceEntry<K, V>)NullEntry.INSTANCE;
    }
    
    static <K, V> void nullifyEvictable(final ReferenceEntry<K, V> referenceEntry) {
        final ReferenceEntry<Object, Object> nullEntry = (ReferenceEntry<Object, Object>)nullEntry();
        referenceEntry.setNextEvictable((ReferenceEntry<K, V>)nullEntry);
        referenceEntry.setPreviousEvictable((ReferenceEntry<K, V>)nullEntry);
    }
    
    static <K, V> void nullifyExpirable(final ReferenceEntry<K, V> referenceEntry) {
        final ReferenceEntry<Object, Object> nullEntry = (ReferenceEntry<Object, Object>)nullEntry();
        referenceEntry.setNextExpirable((ReferenceEntry<K, V>)nullEntry);
        referenceEntry.setPreviousExpirable((ReferenceEntry<K, V>)nullEntry);
    }
    
    static int rehash(int n) {
        n += (n << 15 ^ 0xFFFFCD7D);
        n ^= n >>> 10;
        n += n << 3;
        n ^= n >>> 6;
        n += (n << 2) + (n << 14);
        return n ^ n >>> 16;
    }
    
    private static <E> ArrayList<E> toArrayList(final Collection<E> collection) {
        final ArrayList list = new ArrayList(collection.size());
        Iterators.addAll((Collection<Object>)list, collection.iterator());
        return list;
    }
    
    static <K, V> ValueReference<K, V> unset() {
        return (ValueReference<K, V>)MapMakerInternalMap.UNSET;
    }
    
    @Override
    public void clear() {
        final Segment<K, V>[] segments = this.segments;
        for (int length = segments.length, i = 0; i < length; ++i) {
            segments[i].clear();
        }
    }
    
    @Override
    public boolean containsKey(@Nullable final Object o) {
        if (o != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).containsKey(o, hash);
        }
        return false;
    }
    
    @Override
    public boolean containsValue(@Nullable final Object o) {
        if (o != null) {
            final Segment<K, V>[] segments = this.segments;
            int i = 0;
            long n = -1L;
            while (i < 3) {
                final int length = segments.length;
                long n2 = 0L;
                for (final Segment<K, V> segment : segments) {
                    final int count = segment.count;
                    final AtomicReferenceArray<ReferenceEntry<K, V>> table = segment.table;
                    for (int k = 0; k < table.length(); ++k) {
                        for (ReferenceEntry<K, V> next = table.get(k); next != null; next = next.getNext()) {
                            final V liveValue = segment.getLiveValue(next);
                            if (liveValue != null && this.valueEquivalence.equivalent(o, liveValue)) {
                                return true;
                            }
                        }
                    }
                    n2 += segment.modCount;
                }
                if (n2 == n) {
                    break;
                }
                ++i;
                n = n2;
            }
            return false;
        }
        return false;
    }
    
    @VisibleForTesting
    ReferenceEntry<K, V> copyEntry(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
        return this.segmentFor(referenceEntry.getHash()).copyEntry(referenceEntry, referenceEntry2);
    }
    
    Segment<K, V> createSegment(final int n, final int n2) {
        return new Segment<K, V>(this, n, n2);
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet;
        if ((entrySet = this.entrySet) == null) {
            entrySet = new EntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    boolean evictsBySize() {
        return this.maximumSize != -1;
    }
    
    boolean expires() {
        boolean b = false;
        if (this.expiresAfterWrite() || this.expiresAfterAccess()) {
            b = true;
        }
        return b;
    }
    
    boolean expiresAfterAccess() {
        boolean b = true;
        int n;
        if (this.expireAfterAccessNanos <= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n != 0) {
            b = false;
        }
        return b;
    }
    
    boolean expiresAfterWrite() {
        boolean b = true;
        int n;
        if (this.expireAfterWriteNanos <= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n != 0) {
            b = false;
        }
        return b;
    }
    
    @Override
    public V get(@Nullable final Object o) {
        if (o != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).get(o, hash);
        }
        return null;
    }
    
    ReferenceEntry<K, V> getEntry(@Nullable final Object o) {
        if (o != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).getEntry(o, hash);
        }
        return null;
    }
    
    V getLiveValue(final ReferenceEntry<K, V> referenceEntry) {
        if (referenceEntry.getKey() == null) {
            return null;
        }
        final V value = referenceEntry.getValueReference().get();
        if (value == null) {
            return null;
        }
        if (this.expires() && this.isExpired(referenceEntry)) {
            return null;
        }
        return value;
    }
    
    int hash(final Object o) {
        return rehash(this.keyEquivalence.hash(o));
    }
    
    @Override
    public boolean isEmpty() {
        final Segment<K, V>[] segments = this.segments;
        int i = 0;
        long n = 0L;
        while (i < segments.length) {
            if (segments[i].count != 0) {
                return false;
            }
            n += segments[i].modCount;
            ++i;
        }
        if (n != 0L) {
            for (int j = 0; j < segments.length; ++j) {
                if (segments[j].count != 0) {
                    return false;
                }
                n -= segments[j].modCount;
            }
            if (n != 0L) {
                return false;
            }
        }
        return true;
    }
    
    boolean isExpired(final ReferenceEntry<K, V> referenceEntry) {
        return this.isExpired(referenceEntry, this.ticker.read());
    }
    
    boolean isExpired(final ReferenceEntry<K, V> referenceEntry, final long n) {
        boolean b = true;
        int n2;
        if (n - referenceEntry.getExpirationTime() <= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 != 0) {
            b = false;
        }
        return b;
    }
    
    @VisibleForTesting
    boolean isLive(final ReferenceEntry<K, V> referenceEntry) {
        return this.segmentFor(referenceEntry.getHash()).getLiveValue(referenceEntry) != null;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keySet;
        if ((keySet = this.keySet) == null) {
            keySet = new KeySet();
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @VisibleForTesting
    ReferenceEntry<K, V> newEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
        return this.segmentFor(n).newEntry(k, n, referenceEntry);
    }
    
    final Segment<K, V>[] newSegmentArray(final int n) {
        return new Segment[n];
    }
    
    @VisibleForTesting
    ValueReference<K, V> newValueReference(final ReferenceEntry<K, V> referenceEntry, final V v) {
        return this.valueStrength.referenceValue(this.segmentFor(referenceEntry.getHash()), referenceEntry, v);
    }
    
    void processPendingNotifications() {
        while (true) {
            final MapMaker.RemovalNotification removalNotification = this.removalNotificationQueue.poll();
            if (removalNotification == null) {
                break;
            }
            try {
                this.removalListener.onRemoval(removalNotification);
            }
            catch (final Exception thrown) {
                MapMakerInternalMap.logger.log(Level.WARNING, "Exception thrown by removal listener", thrown);
            }
        }
    }
    
    @Override
    public V put(final K k, final V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        final int hash = this.hash(k);
        return this.segmentFor(hash).put(k, hash, v, false);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public V putIfAbsent(final K k, final V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        final int hash = this.hash(k);
        return this.segmentFor(hash).put(k, hash, v, true);
    }
    
    void reclaimKey(final ReferenceEntry<K, V> referenceEntry) {
        final int hash = referenceEntry.getHash();
        this.segmentFor(hash).reclaimKey(referenceEntry, hash);
    }
    
    void reclaimValue(final ValueReference<K, V> valueReference) {
        final ReferenceEntry<K, V> entry = valueReference.getEntry();
        final int hash = entry.getHash();
        this.segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
    }
    
    @Override
    public V remove(@Nullable final Object o) {
        if (o != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).remove(o, hash);
        }
        return null;
    }
    
    @Override
    public boolean remove(@Nullable final Object o, @Nullable final Object o2) {
        if (o != null && o2 != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).remove(o, hash, o2);
        }
        return false;
    }
    
    @Override
    public V replace(final K k, final V v) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v);
        final int hash = this.hash(k);
        return this.segmentFor(hash).replace(k, hash, v);
    }
    
    @Override
    public boolean replace(final K k, @Nullable final V v, final V v2) {
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(v2);
        if (v != null) {
            final int hash = this.hash(k);
            return this.segmentFor(hash).replace(k, hash, v, v2);
        }
        return false;
    }
    
    Segment<K, V> segmentFor(final int n) {
        return this.segments[n >>> this.segmentShift & this.segmentMask];
    }
    
    @Override
    public int size() {
        final Segment<K, V>[] segments = this.segments;
        long n = 0L;
        for (int i = 0; i < segments.length; ++i) {
            n += segments[i].count;
        }
        return Ints.saturatedCast(n);
    }
    
    boolean usesKeyReferences() {
        return this.keyStrength != Strength.STRONG;
    }
    
    boolean usesValueReferences() {
        return this.valueStrength != Strength.STRONG;
    }
    
    @Override
    public Collection<V> values() {
        Collection<V> values;
        if ((values = this.values) == null) {
            values = new Values();
            this.values = values;
        }
        return values;
    }
    
    Object writeReplace() {
        return new SerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, (MapMaker.RemovalListener<? super Object, ? super Object>)this.removalListener, (ConcurrentMap<Object, Object>)this);
    }
    
    abstract static class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V>
    {
        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getHash() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public K getKey() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setExpirationTime(final long n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            throw new UnsupportedOperationException();
        }
    }
    
    interface ReferenceEntry<K, V>
    {
        long getExpirationTime();
        
        int getHash();
        
        K getKey();
        
        ReferenceEntry<K, V> getNext();
        
        ReferenceEntry<K, V> getNextEvictable();
        
        ReferenceEntry<K, V> getNextExpirable();
        
        ReferenceEntry<K, V> getPreviousEvictable();
        
        ReferenceEntry<K, V> getPreviousExpirable();
        
        ValueReference<K, V> getValueReference();
        
        void setExpirationTime(final long p0);
        
        void setNextEvictable(final ReferenceEntry<K, V> p0);
        
        void setNextExpirable(final ReferenceEntry<K, V> p0);
        
        void setPreviousEvictable(final ReferenceEntry<K, V> p0);
        
        void setPreviousExpirable(final ReferenceEntry<K, V> p0);
        
        void setValueReference(final ValueReference<K, V> p0);
    }
    
    abstract static class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V> implements Serializable
    {
        private static final long serialVersionUID = 3L;
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
        
        AbstractSerializationProxy(final Strength keyStrength, final Strength valueStrength, final Equivalence<Object> keyEquivalence, final Equivalence<Object> valueEquivalence, final long expireAfterWriteNanos, final long expireAfterAccessNanos, final int maximumSize, final int concurrencyLevel, final MapMaker.RemovalListener<? super K, ? super V> removalListener, final ConcurrentMap<K, V> delegate) {
            this.keyStrength = keyStrength;
            this.valueStrength = valueStrength;
            this.keyEquivalence = keyEquivalence;
            this.valueEquivalence = valueEquivalence;
            this.expireAfterWriteNanos = expireAfterWriteNanos;
            this.expireAfterAccessNanos = expireAfterAccessNanos;
            this.maximumSize = maximumSize;
            this.concurrencyLevel = concurrencyLevel;
            this.removalListener = removalListener;
            this.delegate = delegate;
        }
        
        @Override
        protected ConcurrentMap<K, V> delegate() {
            return this.delegate;
        }
        
        void readEntries(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            while (true) {
                final Object object = objectInputStream.readObject();
                if (object == null) {
                    break;
                }
                this.delegate.put((K)object, (V)objectInputStream.readObject());
            }
        }
        
        MapMaker readMapMaker(final ObjectInputStream objectInputStream) throws IOException {
            final int n = 1;
            final MapMaker concurrencyLevel = new MapMaker().initialCapacity(objectInputStream.readInt()).setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).concurrencyLevel(this.concurrencyLevel);
            concurrencyLevel.removalListener(this.removalListener);
            int n2;
            if (this.expireAfterWriteNanos <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                concurrencyLevel.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
            }
            int n3;
            if (this.expireAfterAccessNanos <= 0L) {
                n3 = n;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                concurrencyLevel.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
            }
            if (this.maximumSize != -1) {
                concurrencyLevel.maximumSize(this.maximumSize);
            }
            return concurrencyLevel;
        }
        
        void writeMapTo(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeInt(this.delegate.size());
            for (final Map.Entry<Object, V> entry : this.delegate.entrySet()) {
                objectOutputStream.writeObject(entry.getKey());
                objectOutputStream.writeObject(entry.getValue());
            }
            objectOutputStream.writeObject(null);
        }
    }
    
    static final class CleanupMapTask implements Runnable
    {
        final WeakReference<MapMakerInternalMap<?, ?>> mapReference;
        
        public CleanupMapTask(final MapMakerInternalMap<?, ?> referent) {
            this.mapReference = new WeakReference<MapMakerInternalMap<?, ?>>(referent);
        }
        
        @Override
        public void run() {
            final MapMakerInternalMap mapMakerInternalMap = this.mapReference.get();
            if (mapMakerInternalMap != null) {
                final Segment<K, V>[] segments = mapMakerInternalMap.segments;
                for (int length = segments.length, i = 0; i < length; ++i) {
                    segments[i].runCleanup();
                }
                return;
            }
            throw new CancellationException();
        }
    }
    
    enum EntryFactory
    {
        static final int EVICTABLE_MASK = 2;
        static final int EXPIRABLE_MASK = 1;
        
        STRONG {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        STRONG_EVICTABLE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongEvictableEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        STRONG_EXPIRABLE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyExpirableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongExpirableEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        STRONG_EXPIRABLE_EVICTABLE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyExpirableEntry(referenceEntry, copyEntry);
                this.copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongExpirableEvictableEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        WEAK {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        }, 
        WEAK_EVICTABLE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakEvictableEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        }, 
        WEAK_EXPIRABLE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyExpirableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakExpirableEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        }, 
        WEAK_EXPIRABLE_EVICTABLE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyExpirableEntry(referenceEntry, copyEntry);
                this.copyEvictableEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakExpirableEvictableEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        };
        
        static final EntryFactory[][] factories;
        
        static {
            factories = new EntryFactory[][] { { EntryFactory.STRONG, EntryFactory.STRONG_EXPIRABLE, EntryFactory.STRONG_EVICTABLE, EntryFactory.STRONG_EXPIRABLE_EVICTABLE }, new EntryFactory[0], { EntryFactory.WEAK, EntryFactory.WEAK_EXPIRABLE, EntryFactory.WEAK_EVICTABLE, EntryFactory.WEAK_EXPIRABLE_EVICTABLE } };
        }
        
        static EntryFactory getFactory(final Strength strength, final boolean b, final boolean b2) {
            int n = 0;
            boolean b3;
            if (!b) {
                b3 = false;
            }
            else {
                b3 = true;
            }
            if (b2) {
                n = 2;
            }
            return EntryFactory.factories[strength.ordinal()][n | (b3 ? 1 : 0)];
        }
        
         <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            return this.newEntry(segment, referenceEntry.getKey(), referenceEntry.getHash(), referenceEntry2);
        }
        
         <K, V> void copyEvictableEntry(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            MapMakerInternalMap.connectEvictables((ReferenceEntry<K, V>)referenceEntry.getPreviousEvictable(), referenceEntry2);
            MapMakerInternalMap.connectEvictables(referenceEntry2, (ReferenceEntry<K, V>)referenceEntry.getNextEvictable());
            MapMakerInternalMap.nullifyEvictable((ReferenceEntry)referenceEntry);
        }
        
         <K, V> void copyExpirableEntry(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            referenceEntry2.setExpirationTime(referenceEntry.getExpirationTime());
            MapMakerInternalMap.connectExpirables((ReferenceEntry<K, V>)referenceEntry.getPreviousExpirable(), referenceEntry2);
            MapMakerInternalMap.connectExpirables((ReferenceEntry<Object, Object>)referenceEntry2, (ReferenceEntry<Object, Object>)referenceEntry.getNextExpirable());
            MapMakerInternalMap.nullifyExpirable((ReferenceEntry)referenceEntry);
        }
        
        abstract <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> p0, final K p1, final int p2, @Nullable final ReferenceEntry<K, V> p3);
    }
    
    final class EntryIterator extends HashIterator<Entry<K, V>>
    {
        @Override
        public Entry<K, V> next() {
            return ((HashIterator)this).nextEntry();
        }
    }
    
    final class EntrySet extends SafeToArraySet<Entry<K, V>>
    {
        @Override
        public void clear() {
            MapMakerInternalMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            boolean b = false;
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            final Object key = entry.getKey();
            if (key != null) {
                final V value = MapMakerInternalMap.this.get(key);
                if (value != null && MapMakerInternalMap.this.valueEquivalence.equivalent(entry.getValue(), value)) {
                    b = true;
                }
                return b;
            }
            return false;
        }
        
        @Override
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }
        
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean remove(Object key) {
            boolean b = false;
            if (key instanceof Entry) {
                final Entry entry = (Entry)key;
                key = entry.getKey();
                if (key != null && MapMakerInternalMap.this.remove(key, entry.getValue())) {
                    b = true;
                }
                return b;
            }
            return false;
        }
        
        @Override
        public int size() {
            return MapMakerInternalMap.this.size();
        }
    }
    
    static final class EvictionQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>>
    {
        final ReferenceEntry<K, V> head;
        
        EvictionQueue() {
            this.head = new AbstractReferenceEntry<K, V>() {
                ReferenceEntry<K, V> nextEvictable = this;
                ReferenceEntry<K, V> previousEvictable = this;
                
                @Override
                public ReferenceEntry<K, V> getNextEvictable() {
                    return this.nextEvictable;
                }
                
                @Override
                public ReferenceEntry<K, V> getPreviousEvictable() {
                    return this.previousEvictable;
                }
                
                @Override
                public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
                    this.nextEvictable = nextEvictable;
                }
                
                @Override
                public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
                    this.previousEvictable = previousEvictable;
                }
            };
        }
        
        @Override
        public void clear() {
            ReferenceEntry<K, V> nextEvictable2;
            for (ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable(); nextEvictable != this.head; nextEvictable = nextEvictable2) {
                nextEvictable2 = nextEvictable.getNextEvictable();
                MapMakerInternalMap.nullifyEvictable(nextEvictable);
            }
            this.head.setNextEvictable(this.head);
            this.head.setPreviousEvictable(this.head);
        }
        
        @Override
        public boolean contains(final Object o) {
            return ((ReferenceEntry)o).getNextEvictable() != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean isEmpty() {
            return this.head.getNextEvictable() == this.head;
        }
        
        @Override
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
                @Override
                protected ReferenceEntry<K, V> computeNext(final ReferenceEntry<K, V> referenceEntry) {
                    ReferenceEntry<K, V> nextEvictable = referenceEntry.getNextEvictable();
                    if (nextEvictable == EvictionQueue.this.head) {
                        nextEvictable = null;
                    }
                    return nextEvictable;
                }
            };
        }
        
        @Override
        public boolean offer(final ReferenceEntry<K, V> referenceEntry) {
            MapMakerInternalMap.connectEvictables(referenceEntry.getPreviousEvictable(), referenceEntry.getNextEvictable());
            MapMakerInternalMap.connectEvictables(this.head.getPreviousEvictable(), referenceEntry);
            MapMakerInternalMap.connectEvictables(referenceEntry, this.head);
            return true;
        }
        
        @Override
        public ReferenceEntry<K, V> peek() {
            ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable();
            if (nextEvictable == this.head) {
                nextEvictable = null;
            }
            return nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> poll() {
            final ReferenceEntry<K, V> nextEvictable = this.head.getNextEvictable();
            if (nextEvictable != this.head) {
                this.remove(nextEvictable);
                return nextEvictable;
            }
            return null;
        }
        
        @Override
        public boolean remove(final Object o) {
            final ReferenceEntry referenceEntry = (ReferenceEntry)o;
            final ReferenceEntry previousEvictable = referenceEntry.getPreviousEvictable();
            final ReferenceEntry nextEvictable = referenceEntry.getNextEvictable();
            MapMakerInternalMap.connectEvictables(previousEvictable, (ReferenceEntry<Object, Object>)nextEvictable);
            MapMakerInternalMap.nullifyEvictable((ReferenceEntry<Object, Object>)referenceEntry);
            return nextEvictable != NullEntry.INSTANCE;
        }
        
        @Override
        public int size() {
            int n = 0;
            for (ReferenceEntry<K, V> referenceEntry = this.head.getNextEvictable(); referenceEntry != this.head; referenceEntry = referenceEntry.getNextEvictable()) {
                ++n;
            }
            return n;
        }
    }
    
    static final class ExpirationQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>>
    {
        final ReferenceEntry<K, V> head;
        
        ExpirationQueue() {
            this.head = new AbstractReferenceEntry<K, V>() {
                ReferenceEntry<K, V> nextExpirable = this;
                ReferenceEntry<K, V> previousExpirable = this;
                
                @Override
                public long getExpirationTime() {
                    return Long.MAX_VALUE;
                }
                
                @Override
                public ReferenceEntry<K, V> getNextExpirable() {
                    return this.nextExpirable;
                }
                
                @Override
                public ReferenceEntry<K, V> getPreviousExpirable() {
                    return this.previousExpirable;
                }
                
                @Override
                public void setExpirationTime(final long n) {
                }
                
                @Override
                public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
                    this.nextExpirable = nextExpirable;
                }
                
                @Override
                public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
                    this.previousExpirable = previousExpirable;
                }
            };
        }
        
        @Override
        public void clear() {
            ReferenceEntry<K, V> nextExpirable2;
            for (ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable(); nextExpirable != this.head; nextExpirable = nextExpirable2) {
                nextExpirable2 = nextExpirable.getNextExpirable();
                MapMakerInternalMap.nullifyExpirable(nextExpirable);
            }
            this.head.setNextExpirable(this.head);
            this.head.setPreviousExpirable(this.head);
        }
        
        @Override
        public boolean contains(final Object o) {
            return ((ReferenceEntry)o).getNextExpirable() != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean isEmpty() {
            return this.head.getNextExpirable() == this.head;
        }
        
        @Override
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
                @Override
                protected ReferenceEntry<K, V> computeNext(final ReferenceEntry<K, V> referenceEntry) {
                    ReferenceEntry<K, V> nextExpirable = referenceEntry.getNextExpirable();
                    if (nextExpirable == ExpirationQueue.this.head) {
                        nextExpirable = null;
                    }
                    return nextExpirable;
                }
            };
        }
        
        @Override
        public boolean offer(final ReferenceEntry<K, V> referenceEntry) {
            MapMakerInternalMap.connectExpirables(referenceEntry.getPreviousExpirable(), referenceEntry.getNextExpirable());
            MapMakerInternalMap.connectExpirables(this.head.getPreviousExpirable(), referenceEntry);
            MapMakerInternalMap.connectExpirables(referenceEntry, this.head);
            return true;
        }
        
        @Override
        public ReferenceEntry<K, V> peek() {
            ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable();
            if (nextExpirable == this.head) {
                nextExpirable = null;
            }
            return nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> poll() {
            final ReferenceEntry<K, V> nextExpirable = this.head.getNextExpirable();
            if (nextExpirable != this.head) {
                this.remove(nextExpirable);
                return nextExpirable;
            }
            return null;
        }
        
        @Override
        public boolean remove(final Object o) {
            final ReferenceEntry referenceEntry = (ReferenceEntry)o;
            final ReferenceEntry previousExpirable = referenceEntry.getPreviousExpirable();
            final ReferenceEntry nextExpirable = referenceEntry.getNextExpirable();
            MapMakerInternalMap.connectExpirables(previousExpirable, (ReferenceEntry<Object, Object>)nextExpirable);
            MapMakerInternalMap.nullifyExpirable((ReferenceEntry<Object, Object>)referenceEntry);
            return nextExpirable != NullEntry.INSTANCE;
        }
        
        @Override
        public int size() {
            int n = 0;
            for (ReferenceEntry<K, V> referenceEntry = this.head.getNextExpirable(); referenceEntry != this.head; referenceEntry = referenceEntry.getNextExpirable()) {
                ++n;
            }
            return n;
        }
    }
    
    abstract class HashIterator<E> implements Iterator<E>
    {
        Segment<K, V> currentSegment;
        AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
        WriteThroughEntry lastReturned;
        ReferenceEntry<K, V> nextEntry;
        WriteThroughEntry nextExternal;
        int nextSegmentIndex;
        int nextTableIndex;
        
        HashIterator() {
            this.nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
            this.nextTableIndex = -1;
            this.advance();
        }
        
        final void advance() {
            this.nextExternal = null;
            if (this.nextInChain()) {
                return;
            }
            if (!this.nextInTable()) {
                while (this.nextSegmentIndex >= 0) {
                    this.currentSegment = MapMakerInternalMap.this.segments[this.nextSegmentIndex--];
                    if (this.currentSegment.count != 0) {
                        this.currentTable = this.currentSegment.table;
                        this.nextTableIndex = this.currentTable.length() - 1;
                        if (this.nextInTable()) {
                            return;
                        }
                        continue;
                    }
                }
            }
        }
        
        boolean advanceTo(final ReferenceEntry<K, V> referenceEntry) {
            try {
                final K key = referenceEntry.getKey();
                final Object liveValue = MapMakerInternalMap.this.getLiveValue((ReferenceEntry<K, Object>)referenceEntry);
                if (liveValue == null) {
                    return false;
                }
                this.nextExternal = new WriteThroughEntry(key, (V)liveValue);
                return true;
            }
            finally {
                this.currentSegment.postReadCleanup();
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextExternal != null;
        }
        
        @Override
        public abstract E next();
        
        WriteThroughEntry nextEntry() {
            if (this.nextExternal != null) {
                this.lastReturned = this.nextExternal;
                this.advance();
                return this.lastReturned;
            }
            throw new NoSuchElementException();
        }
        
        boolean nextInChain() {
            if (this.nextEntry != null) {
                ReferenceEntry<K, V> nextEntry = this.nextEntry.getNext();
                while (true) {
                    this.nextEntry = nextEntry;
                    if (this.nextEntry == null) {
                        return false;
                    }
                    if (this.advanceTo(this.nextEntry)) {
                        break;
                    }
                    nextEntry = this.nextEntry.getNext();
                }
                return true;
            }
            return false;
        }
        
        boolean nextInTable() {
            while (this.nextTableIndex >= 0) {
                if ((this.nextEntry = this.currentTable.get(this.nextTableIndex--)) != null) {
                    if (!this.advanceTo(this.nextEntry) && !this.nextInChain()) {
                        continue;
                    }
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void remove() {
            CollectPreconditions.checkRemove(this.lastReturned != null);
            MapMakerInternalMap.this.remove(this.lastReturned.getKey());
            this.lastReturned = null;
        }
    }
    
    final class KeyIterator extends HashIterator<K>
    {
        @Override
        public K next() {
            return ((HashIterator)this).nextEntry().getKey();
        }
    }
    
    final class KeySet extends SafeToArraySet<K>
    {
        @Override
        public void clear() {
            MapMakerInternalMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return MapMakerInternalMap.this.containsKey(o);
        }
        
        @Override
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            return MapMakerInternalMap.this.remove(o) != null;
        }
        
        @Override
        public int size() {
            return MapMakerInternalMap.this.size();
        }
    }
    
    private enum NullEntry implements ReferenceEntry<Object, Object>
    {
        INSTANCE;
        
        @Override
        public long getExpirationTime() {
            return 0L;
        }
        
        @Override
        public int getHash() {
            return 0;
        }
        
        @Override
        public Object getKey() {
            return null;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNext() {
            return null;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNextEvictable() {
            return this;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNextExpirable() {
            return this;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getPreviousEvictable() {
            return this;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getPreviousExpirable() {
            return this;
        }
        
        @Override
        public ValueReference<Object, Object> getValueReference() {
            return null;
        }
        
        @Override
        public void setExpirationTime(final long n) {
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setValueReference(final ValueReference<Object, Object> valueReference) {
        }
    }
    
    private abstract static class SafeToArraySet<E> extends AbstractSet<E>
    {
        @Override
        public Object[] toArray() {
            return toArrayList((Collection<Object>)this).toArray();
        }
        
        @Override
        public <E> E[] toArray(final E[] a) {
            return toArrayList((Collection<Object>)this).toArray(a);
        }
    }
    
    static class Segment<K, V> extends ReentrantLock
    {
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
        final AtomicInteger readCount;
        final Queue<ReferenceEntry<K, V>> recencyQueue;
        volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
        int threshold;
        final ReferenceQueue<V> valueReferenceQueue;
        
        Segment(final MapMakerInternalMap<K, V> map, final int n, final int maxSegmentSize) {
            final ReferenceQueue<V> referenceQueue = null;
            this.readCount = new AtomicInteger();
            this.map = map;
            this.maxSegmentSize = maxSegmentSize;
            this.initTable(this.newEntryArray(n));
            ReferenceQueue<K> keyReferenceQueue;
            if (!map.usesKeyReferences()) {
                keyReferenceQueue = null;
            }
            else {
                keyReferenceQueue = new ReferenceQueue<K>();
            }
            this.keyReferenceQueue = keyReferenceQueue;
            ReferenceQueue<V> valueReferenceQueue;
            if (!map.usesValueReferences()) {
                valueReferenceQueue = referenceQueue;
            }
            else {
                valueReferenceQueue = new ReferenceQueue<V>();
            }
            this.valueReferenceQueue = valueReferenceQueue;
            Queue<ReferenceEntry<K, V>> discardingQueue;
            if (!map.evictsBySize() && !map.expiresAfterAccess()) {
                discardingQueue = MapMakerInternalMap.discardingQueue();
            }
            else {
                discardingQueue = new ConcurrentLinkedQueue<ReferenceEntry<K, V>>();
            }
            this.recencyQueue = discardingQueue;
            Queue<Object> discardingQueue2;
            if (!map.evictsBySize()) {
                discardingQueue2 = (Queue<Object>)MapMakerInternalMap.discardingQueue();
            }
            else {
                discardingQueue2 = (Queue<Object>)new EvictionQueue();
            }
            this.evictionQueue = (Queue<ReferenceEntry<K, V>>)discardingQueue2;
            Queue<Object> discardingQueue3;
            if (!map.expires()) {
                discardingQueue3 = (Queue<Object>)MapMakerInternalMap.discardingQueue();
            }
            else {
                discardingQueue3 = (Queue<Object>)new ExpirationQueue();
            }
            this.expirationQueue = (Queue<ReferenceEntry<K, V>>)discardingQueue3;
        }
        
        void clear() {
            final int n = 0;
            if (this.count != 0) {
                while (true) {
                    this.lock();
                    while (true) {
                        AtomicReferenceArray<ReferenceEntry<K, V>> table;
                        int i;
                        try {
                            table = this.table;
                            if (this.map.removalNotificationQueue == MapMakerInternalMap.DISCARDING_QUEUE) {
                                i = n;
                            }
                            else {
                                int j = 0;
                                while (true) {
                                    i = n;
                                    if (j >= table.length()) {
                                        break;
                                    }
                                    for (ReferenceEntry<?, ?> next = table.get(j); next != null; next = next.getNext()) {
                                        if (!next.getValueReference().isComputingReference()) {
                                            this.enqueueNotification((ReferenceEntry<K, V>)next, MapMaker.RemovalCause.EXPLICIT);
                                        }
                                    }
                                    ++j;
                                }
                            }
                            if (i >= table.length()) {
                                this.clearReferenceQueues();
                                this.evictionQueue.clear();
                                this.expirationQueue.clear();
                                this.readCount.set(0);
                                ++this.modCount;
                                this.count = 0;
                                break;
                            }
                        }
                        finally {
                            this.unlock();
                            this.postWriteCleanup();
                        }
                        table.set(i, null);
                        ++i;
                        continue;
                    }
                }
            }
        }
        
        void clearKeyReferenceQueue() {
            while (this.keyReferenceQueue.poll() != null) {}
        }
        
        void clearReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                this.clearKeyReferenceQueue();
            }
            if (this.map.usesValueReferences()) {
                this.clearValueReferenceQueue();
            }
        }
        
        boolean clearValue(final K k, final int n, final ValueReference<K, V> valueReference) {
            this.lock();
            try {
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n2 = n & table.length() - 1;
                Object next;
                final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n2));
                while (next != null) {
                    final Object key = ((ReferenceEntry<Object, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (((ReferenceEntry<K, V>)next).getValueReference() != valueReference) {
                            return false;
                        }
                        table.set(n2, this.removeFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next));
                        return true;
                    }
                    else {
                        next = ((ReferenceEntry<K, V>)next).getNext();
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        void clearValueReferenceQueue() {
            while (this.valueReferenceQueue.poll() != null) {}
        }
        
        boolean containsKey(Object value, final int n) {
            boolean b = false;
            try {
                if (this.count == 0) {
                    return false;
                }
                final ReferenceEntry<K, V> liveEntry = this.getLiveEntry(value, n);
                if (liveEntry != null) {
                    value = liveEntry.getValueReference().get();
                    if (value != null) {
                        b = true;
                    }
                    return b;
                }
                return false;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        @VisibleForTesting
        boolean containsValue(final Object o) {
            try {
                if (this.count != 0) {
                    final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                    for (int length = table.length(), i = 0; i < length; ++i) {
                        for (ReferenceEntry<?, ?> next = table.get(i); next != null; next = next.getNext()) {
                            final Object liveValue = this.getLiveValue((ReferenceEntry<K, Object>)next);
                            if (liveValue != null && this.map.valueEquivalence.equivalent(o, liveValue)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        @GuardedBy("this")
        ReferenceEntry<K, V> copyEntry(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            if (referenceEntry.getKey() == null) {
                return null;
            }
            final ValueReference<K, V> valueReference = referenceEntry.getValueReference();
            final V value = valueReference.get();
            if (value == null && !valueReference.isComputingReference()) {
                return null;
            }
            final ReferenceEntry<K, V> copyEntry = this.map.entryFactory.copyEntry(this, referenceEntry, referenceEntry2);
            copyEntry.setValueReference((ValueReference<K, V>)valueReference.copyFor((ReferenceQueue<V>)this.valueReferenceQueue, value, (ReferenceEntry<K, V>)copyEntry));
            return copyEntry;
        }
        
        @GuardedBy("this")
        void drainKeyReferenceQueue() {
            int n = 0;
            do {
                final Reference<? extends K> poll = this.keyReferenceQueue.poll();
                if (poll == null) {
                    break;
                }
                this.map.reclaimKey((ReferenceEntry<K, V>)poll);
            } while (++n != 16);
        }
        
        @GuardedBy("this")
        void drainRecencyQueue() {
            while (true) {
                final ReferenceEntry referenceEntry = this.recencyQueue.poll();
                if (referenceEntry == null) {
                    break;
                }
                if (this.evictionQueue.contains(referenceEntry)) {
                    this.evictionQueue.add(referenceEntry);
                }
                if (!this.map.expiresAfterAccess() || !this.expirationQueue.contains(referenceEntry)) {
                    continue;
                }
                this.expirationQueue.add(referenceEntry);
            }
        }
        
        @GuardedBy("this")
        void drainReferenceQueues() {
            if (this.map.usesKeyReferences()) {
                this.drainKeyReferenceQueue();
            }
            if (this.map.usesValueReferences()) {
                this.drainValueReferenceQueue();
            }
        }
        
        @GuardedBy("this")
        void drainValueReferenceQueue() {
            int n = 0;
            do {
                final Reference<? extends V> poll = this.valueReferenceQueue.poll();
                if (poll == null) {
                    break;
                }
                this.map.reclaimValue((ValueReference<K, V>)poll);
            } while (++n != 16);
        }
        
        void enqueueNotification(final ReferenceEntry<K, V> referenceEntry, final MapMaker.RemovalCause removalCause) {
            this.enqueueNotification(referenceEntry.getKey(), referenceEntry.getHash(), referenceEntry.getValueReference().get(), removalCause);
        }
        
        void enqueueNotification(@Nullable final K k, final int n, @Nullable final V v, final MapMaker.RemovalCause removalCause) {
            if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
                this.map.removalNotificationQueue.offer((MapMaker.RemovalNotification<K, V>)new MapMaker.RemovalNotification(k, v, removalCause));
            }
        }
        
        @GuardedBy("this")
        boolean evictEntries() {
            if (!this.map.evictsBySize() || this.count < this.maxSegmentSize) {
                return false;
            }
            this.drainRecencyQueue();
            final ReferenceEntry referenceEntry = this.evictionQueue.remove();
            if (this.removeEntry(referenceEntry, referenceEntry.getHash(), MapMaker.RemovalCause.SIZE)) {
                return true;
            }
            throw new AssertionError();
        }
        
        @GuardedBy("this")
        void expand() {
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            final int length = table.length();
            if (length < 1073741824) {
                int count = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> entryArray = this.newEntryArray(length << 1);
                this.threshold = entryArray.length() * 3 / 4;
                final int n = entryArray.length() - 1;
                for (int i = 0; i < length; ++i) {
                    final ReferenceEntry<K, V> newValue = table.get(i);
                    if (newValue != null) {
                        ReferenceEntry<K, V> referenceEntry = newValue.getNext();
                        int n2 = newValue.getHash() & n;
                        if (referenceEntry != null) {
                            ReferenceEntry<K, V> newValue2 = newValue;
                            while (referenceEntry != null) {
                                final int n3 = referenceEntry.getHash() & n;
                                if (n3 != n2) {
                                    n2 = n3;
                                    newValue2 = referenceEntry;
                                }
                                referenceEntry = referenceEntry.getNext();
                            }
                            entryArray.set(n2, newValue2);
                            ReferenceEntry<K, V> next = newValue;
                            int n4 = count;
                            while (true) {
                                count = n4;
                                if (next == newValue2) {
                                    break;
                                }
                                final int n5 = next.getHash() & n;
                                final ReferenceEntry<K, V> copyEntry = this.copyEntry(next, entryArray.get(n5));
                                int n6;
                                if (copyEntry == null) {
                                    this.removeCollectedEntry(next);
                                    n6 = n4 - 1;
                                }
                                else {
                                    entryArray.set(n5, (ReferenceEntry)copyEntry);
                                    n6 = n4;
                                }
                                next = next.getNext();
                                n4 = n6;
                            }
                        }
                        else {
                            entryArray.set(n2, newValue);
                        }
                    }
                }
                this.table = entryArray;
                this.count = count;
            }
        }
        
        @GuardedBy("this")
        void expireEntries() {
            this.drainRecencyQueue();
            if (!this.expirationQueue.isEmpty()) {
                final long read = this.map.ticker.read();
                ReferenceEntry referenceEntry;
                do {
                    referenceEntry = this.expirationQueue.peek();
                    if (referenceEntry != null && this.map.isExpired(referenceEntry, read)) {
                        continue;
                    }
                    return;
                } while (this.removeEntry(referenceEntry, referenceEntry.getHash(), MapMaker.RemovalCause.EXPIRED));
                throw new AssertionError();
            }
        }
        
        V get(final Object o, final int n) {
            try {
                final ReferenceEntry<K, V> liveEntry = this.getLiveEntry(o, n);
                if (liveEntry != null) {
                    final V value = liveEntry.getValueReference().get();
                    if (value == null) {
                        this.tryDrainReferenceQueues();
                    }
                    else {
                        this.recordRead(liveEntry);
                    }
                    return value;
                }
                return null;
            }
            finally {
                this.postReadCleanup();
            }
        }
        
        ReferenceEntry<K, V> getEntry(final Object o, final int n) {
            if (this.count != 0) {
                for (Object o2 = this.getFirst(n); o2 != null; o2 = ((ReferenceEntry)o2).getNext()) {
                    if (((ReferenceEntry)o2).getHash() == n) {
                        final Object key = ((ReferenceEntry)o2).getKey();
                        if (key != null) {
                            if (this.map.keyEquivalence.equivalent(o, key)) {
                                return (ReferenceEntry<K, V>)o2;
                            }
                        }
                        else {
                            this.tryDrainReferenceQueues();
                        }
                    }
                }
            }
            return null;
        }
        
        ReferenceEntry<K, V> getFirst(final int n) {
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            return (ReferenceEntry<K, V>)(ReferenceEntry)table.get(table.length() - 1 & n);
        }
        
        ReferenceEntry<K, V> getLiveEntry(final Object o, final int n) {
            final ReferenceEntry<K, V> entry = this.getEntry(o, n);
            if (entry == null) {
                return null;
            }
            if (this.map.expires() && this.map.isExpired(entry)) {
                this.tryExpireEntries();
                return null;
            }
            return entry;
        }
        
        V getLiveValue(final ReferenceEntry<K, V> referenceEntry) {
            if (referenceEntry.getKey() == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            final V value = referenceEntry.getValueReference().get();
            if (value == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            if (this.map.expires() && this.map.isExpired(referenceEntry)) {
                this.tryExpireEntries();
                return null;
            }
            return value;
        }
        
        void initTable(final AtomicReferenceArray<ReferenceEntry<K, V>> table) {
            this.threshold = table.length() * 3 / 4;
            if (this.threshold == this.maxSegmentSize) {
                ++this.threshold;
            }
            this.table = table;
        }
        
        boolean isCollected(final ValueReference<K, V> valueReference) {
            boolean b = false;
            if (!valueReference.isComputingReference()) {
                if (valueReference.get() == null) {
                    b = true;
                }
                return b;
            }
            return false;
        }
        
        @GuardedBy("this")
        ReferenceEntry<K, V> newEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            return this.map.entryFactory.newEntry(this, k, n, referenceEntry);
        }
        
        AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(final int length) {
            return new AtomicReferenceArray<ReferenceEntry<K, V>>(length);
        }
        
        void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 0x3F) == 0x0) {
                this.runCleanup();
            }
        }
        
        void postWriteCleanup() {
            this.runUnlockedCleanup();
        }
        
        @GuardedBy("this")
        void preWriteCleanup() {
            this.runLockedCleanup();
        }
        
        V put(final K k, int count, final V v, final boolean b) {
            this.lock();
            try {
                this.preWriteCleanup();
                int count2 = this.count + 1;
                if (count2 > this.threshold) {
                    this.expand();
                    count2 = this.count + 1;
                }
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n));
                while (next != null) {
                    final Object key = ((ReferenceEntry<Object, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        final V value = valueReference.get();
                        if (value == null) {
                            ++this.modCount;
                            this.setValue((ReferenceEntry<K, V>)next, v);
                            if (valueReference.isComputingReference()) {
                                if (this.evictEntries()) {
                                    count2 = this.count + 1;
                                }
                            }
                            else {
                                this.enqueueNotification(k, count, value, MapMaker.RemovalCause.COLLECTED);
                                count2 = this.count;
                            }
                            this.count = count2;
                            return null;
                        }
                        if (!b) {
                            ++this.modCount;
                            this.enqueueNotification(k, count, value, MapMaker.RemovalCause.REPLACED);
                            this.setValue((ReferenceEntry<K, V>)next, v);
                            return value;
                        }
                        this.recordLockedRead((ReferenceEntry<K, V>)next);
                        return value;
                    }
                    else {
                        next = ((ReferenceEntry<K, V>)next).getNext();
                    }
                }
                ++this.modCount;
                final ReferenceEntry<K, V> entry = this.newEntry(k, count, (ReferenceEntry<K, V>)referenceEntry);
                this.setValue(entry, v);
                table.set(n, (ReferenceEntry)entry);
                if (this.evictEntries()) {
                    count = this.count;
                    count2 = count + 1;
                }
                this.count = count2;
                return null;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean reclaimKey(final ReferenceEntry<K, V> referenceEntry, int count) {
            this.lock();
            try {
                final int count2 = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                ReferenceEntry<K, V> next;
                for (ReferenceEntry<K, V> referenceEntry2 = next = table.get(n); next != null; next = next.getNext()) {
                    if (next == referenceEntry) {
                        ++this.modCount;
                        this.enqueueNotification(next.getKey(), count, next.getValueReference().get(), MapMaker.RemovalCause.COLLECTED);
                        final ReferenceEntry<K, V> removeFromChain = this.removeFromChain(referenceEntry2, next);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeFromChain);
                        this.count = count - 1;
                        return true;
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean reclaimValue(final K k, int count, final ValueReference<K, V> valueReference) {
            this.lock();
            try {
                final int count2 = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n));
                while (next != null) {
                    final Object key = ((ReferenceEntry<Object, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (((ReferenceEntry<K, V>)next).getValueReference() != valueReference) {
                            this.unlock();
                            if (!this.isHeldByCurrentThread()) {
                                this.postWriteCleanup();
                            }
                            return false;
                        }
                        ++this.modCount;
                        this.enqueueNotification(k, count, valueReference.get(), MapMaker.RemovalCause.COLLECTED);
                        final ReferenceEntry<K, V> removeFromChain = this.removeFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeFromChain);
                        this.count = count - 1;
                        this.unlock();
                        if (!this.isHeldByCurrentThread()) {
                            this.postWriteCleanup();
                        }
                        return true;
                    }
                    else {
                        next = ((ReferenceEntry<K, V>)next).getNext();
                    }
                }
                this.unlock();
                if (!this.isHeldByCurrentThread()) {
                    this.postWriteCleanup();
                }
                return false;
            }
            finally {
                this.unlock();
                while (true) {
                    if (this.isHeldByCurrentThread()) {
                        break Label_0242;
                    }
                    this.postWriteCleanup();
                    break Label_0242;
                    continue;
                }
            }
        }
        
        void recordExpirationTime(final ReferenceEntry<K, V> referenceEntry, final long n) {
            referenceEntry.setExpirationTime(this.map.ticker.read() + n);
        }
        
        @GuardedBy("this")
        void recordLockedRead(final ReferenceEntry<K, V> referenceEntry) {
            this.evictionQueue.add(referenceEntry);
            if (this.map.expiresAfterAccess()) {
                this.recordExpirationTime(referenceEntry, this.map.expireAfterAccessNanos);
                this.expirationQueue.add(referenceEntry);
            }
        }
        
        void recordRead(final ReferenceEntry<K, V> referenceEntry) {
            if (this.map.expiresAfterAccess()) {
                this.recordExpirationTime(referenceEntry, this.map.expireAfterAccessNanos);
            }
            this.recencyQueue.add(referenceEntry);
        }
        
        @GuardedBy("this")
        void recordWrite(final ReferenceEntry<K, V> referenceEntry) {
            this.drainRecencyQueue();
            this.evictionQueue.add(referenceEntry);
            if (this.map.expires()) {
                long n;
                if (!this.map.expiresAfterAccess()) {
                    n = this.map.expireAfterWriteNanos;
                }
                else {
                    n = this.map.expireAfterAccessNanos;
                }
                this.recordExpirationTime(referenceEntry, n);
                this.expirationQueue.add(referenceEntry);
            }
        }
        
        V remove(final Object o, int count) {
            this.lock();
            try {
                this.preWriteCleanup();
                final int count2 = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                for (ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n)); next != null; next = ((ReferenceEntry<K, V>)next).getNext()) {
                    final K key = ((ReferenceEntry<K, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(o, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        final V value = valueReference.get();
                        MapMaker.RemovalCause removalCause;
                        if (value == null) {
                            if (!this.isCollected(valueReference)) {
                                return null;
                            }
                            removalCause = MapMaker.RemovalCause.COLLECTED;
                        }
                        else {
                            removalCause = MapMaker.RemovalCause.EXPLICIT;
                        }
                        ++this.modCount;
                        this.enqueueNotification(key, count, value, removalCause);
                        final ReferenceEntry<K, V> removeFromChain = this.removeFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeFromChain);
                        this.count = count - 1;
                        return value;
                    }
                }
                return null;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean remove(final Object o, int count, final Object o2) {
            this.lock();
            try {
                this.preWriteCleanup();
                final int count2 = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                for (ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n)); next != null; next = ((ReferenceEntry<K, V>)next).getNext()) {
                    final K key = ((ReferenceEntry<K, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(o, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        final V value = valueReference.get();
                        MapMaker.RemovalCause removalCause;
                        if (!this.map.valueEquivalence.equivalent(o2, value)) {
                            if (!this.isCollected(valueReference)) {
                                return false;
                            }
                            removalCause = MapMaker.RemovalCause.COLLECTED;
                        }
                        else {
                            removalCause = MapMaker.RemovalCause.EXPLICIT;
                        }
                        ++this.modCount;
                        this.enqueueNotification(key, count, value, removalCause);
                        final ReferenceEntry<K, V> removeFromChain = this.removeFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeFromChain);
                        this.count = count - 1;
                        return removalCause == MapMaker.RemovalCause.EXPLICIT;
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        void removeCollectedEntry(final ReferenceEntry<K, V> referenceEntry) {
            this.enqueueNotification(referenceEntry, MapMaker.RemovalCause.COLLECTED);
            this.evictionQueue.remove(referenceEntry);
            this.expirationQueue.remove(referenceEntry);
        }
        
        @GuardedBy("this")
        boolean removeEntry(final ReferenceEntry<K, V> referenceEntry, int count, final MapMaker.RemovalCause removalCause) {
            final int count2 = this.count;
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            final int n = count & table.length() - 1;
            ReferenceEntry<K, V> next;
            for (ReferenceEntry<K, V> referenceEntry2 = next = table.get(n); next != null; next = next.getNext()) {
                if (next == referenceEntry) {
                    ++this.modCount;
                    this.enqueueNotification(next.getKey(), count, next.getValueReference().get(), removalCause);
                    final ReferenceEntry<K, V> removeFromChain = this.removeFromChain(referenceEntry2, next);
                    count = this.count;
                    table.set(n, (ReferenceEntry)removeFromChain);
                    this.count = count - 1;
                    return true;
                }
            }
            return false;
        }
        
        @GuardedBy("this")
        ReferenceEntry<K, V> removeFromChain(ReferenceEntry<K, V> next, final ReferenceEntry<K, V> referenceEntry) {
            this.evictionQueue.remove(referenceEntry);
            this.expirationQueue.remove(referenceEntry);
            int count = this.count;
            Object next2 = referenceEntry.getNext();
            while (next != referenceEntry) {
                final ReferenceEntry<K, V> copyEntry = this.copyEntry(next, (ReferenceEntry<K, V>)next2);
                if (copyEntry == null) {
                    this.removeCollectedEntry(next);
                    --count;
                }
                else {
                    next2 = copyEntry;
                }
                next = next.getNext();
            }
            this.count = count;
            return (ReferenceEntry<K, V>)next2;
        }
        
        V replace(final K k, int count, final V v) {
            this.lock();
            try {
                this.preWriteCleanup();
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n));
                while (next != null) {
                    final K key = ((ReferenceEntry<K, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        final V value = valueReference.get();
                        if (value != null) {
                            ++this.modCount;
                            this.enqueueNotification(k, count, value, MapMaker.RemovalCause.REPLACED);
                            this.setValue((ReferenceEntry<K, V>)next, v);
                            return value;
                        }
                        if (this.isCollected(valueReference)) {
                            final int count2 = this.count;
                            ++this.modCount;
                            this.enqueueNotification(key, count, value, MapMaker.RemovalCause.COLLECTED);
                            final ReferenceEntry<K, V> removeFromChain = this.removeFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next);
                            count = this.count;
                            table.set(n, (ReferenceEntry)removeFromChain);
                            this.count = count - 1;
                        }
                        return null;
                    }
                    else {
                        next = ((ReferenceEntry<K, V>)next).getNext();
                    }
                }
                return null;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        boolean replace(final K k, int count, final V v, final V v2) {
            this.lock();
            Object next = null;
            V value = null;
            Label_0258: {
                try {
                    this.preWriteCleanup();
                    final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                    final int n = count & table.length() - 1;
                    final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n));
                    while (next != null) {
                        final K key = ((ReferenceEntry<K, V>)next).getKey();
                        if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                            final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                            value = valueReference.get();
                            if (value == null) {
                                if (this.isCollected(valueReference)) {
                                    final int count2 = this.count;
                                    ++this.modCount;
                                    this.enqueueNotification(key, count, value, MapMaker.RemovalCause.COLLECTED);
                                    final ReferenceEntry<K, V> removeFromChain = this.removeFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next);
                                    count = this.count;
                                    table.set(n, (ReferenceEntry)removeFromChain);
                                    this.count = count - 1;
                                }
                                return false;
                            }
                            if (!this.map.valueEquivalence.equivalent(v, value)) {
                                this.recordLockedRead((ReferenceEntry<K, V>)next);
                                return false;
                            }
                            break Label_0258;
                        }
                        else {
                            next = ((ReferenceEntry<K, V>)next).getNext();
                        }
                    }
                    return false;
                }
                finally {
                    this.unlock();
                    this.postWriteCleanup();
                }
            }
            ++this.modCount;
            final K i;
            this.enqueueNotification(i, count, value, MapMaker.RemovalCause.REPLACED);
            this.setValue((ReferenceEntry<K, V>)next, v2);
            this.unlock();
            this.postWriteCleanup();
            return true;
        }
        
        void runCleanup() {
            this.runLockedCleanup();
            this.runUnlockedCleanup();
        }
        
        void runLockedCleanup() {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                    this.expireEntries();
                    this.readCount.set(0);
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        void runUnlockedCleanup() {
            if (!this.isHeldByCurrentThread()) {
                this.map.processPendingNotifications();
            }
        }
        
        @GuardedBy("this")
        void setValue(final ReferenceEntry<K, V> referenceEntry, final V v) {
            referenceEntry.setValueReference(this.map.valueStrength.referenceValue(this, referenceEntry, v));
            this.recordWrite(referenceEntry);
        }
        
        void tryDrainReferenceQueues() {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        void tryExpireEntries() {
            if (this.tryLock()) {
                try {
                    this.expireEntries();
                }
                finally {
                    this.unlock();
                }
            }
        }
    }
    
    private static final class SerializationProxy<K, V> extends AbstractSerializationProxy<K, V>
    {
        private static final long serialVersionUID = 3L;
        
        SerializationProxy(final Strength strength, final Strength strength2, final Equivalence<Object> equivalence, final Equivalence<Object> equivalence2, final long n, final long n2, final int n3, final int n4, final MapMaker.RemovalListener<? super K, ? super V> removalListener, final ConcurrentMap<K, V> concurrentMap) {
            super(strength, strength2, equivalence, equivalence2, n, n2, n3, n4, removalListener, concurrentMap);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.delegate = (ConcurrentMap<K, V>)this.readMapMaker(objectInputStream).makeMap();
            this.readEntries(objectInputStream);
        }
        
        private Object readResolve() {
            return this.delegate;
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            this.writeMapTo(objectOutputStream);
        }
    }
    
    static class SoftEntry<K, V> extends SoftReference<K> implements ReferenceEntry<K, V>
    {
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        SoftEntry(final ReferenceQueue<K> q, final K referent, final int hash, @Nullable final ReferenceEntry<K, V> next) {
            super(referent, q);
            this.valueReference = MapMakerInternalMap.unset();
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public K getKey() {
            return this.get();
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public void setExpirationTime(final long n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference.clear(this.valueReference = valueReference);
        }
    }
    
    static final class SoftEvictableEntry<K, V> extends SoftEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> previousEvictable;
        
        SoftEvictableEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
            this.nextEvictable = nextEvictable;
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
            this.previousEvictable = previousEvictable;
        }
    }
    
    static final class SoftExpirableEntry<K, V> extends SoftEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;
        
        SoftExpirableEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public long getExpirationTime() {
            return this.time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }
        
        @Override
        public void setExpirationTime(final long time) {
            this.time = time;
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
            this.nextExpirable = nextExpirable;
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
            this.previousExpirable = previousExpirable;
        }
    }
    
    static final class SoftExpirableEvictableEntry<K, V> extends SoftEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousEvictable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;
        
        SoftExpirableEvictableEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public long getExpirationTime() {
            return this.time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }
        
        @Override
        public void setExpirationTime(final long time) {
            this.time = time;
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
            this.nextEvictable = nextEvictable;
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
            this.nextExpirable = nextExpirable;
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
            this.previousEvictable = previousEvictable;
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
            this.previousExpirable = previousExpirable;
        }
    }
    
    static final class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        SoftValueReference(final ReferenceQueue<V> q, final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, q);
            this.entry = entry;
        }
        
        @Override
        public void clear(final ValueReference<K, V> valueReference) {
            this.clear();
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return new SoftValueReference((ReferenceQueue<Object>)referenceQueue, v, (ReferenceEntry<Object, Object>)referenceEntry);
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }
        
        @Override
        public boolean isComputingReference() {
            return false;
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    enum Strength
    {
        SOFT {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final V v) {
                return new SoftValueReference<K, V>(segment.valueReferenceQueue, v, referenceEntry);
            }
        }, 
        STRONG {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final V v) {
                return new StrongValueReference<K, V>(v);
            }
        }, 
        WEAK {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final V v) {
                return new WeakValueReference<K, V>(segment.valueReferenceQueue, v, referenceEntry);
            }
        };
        
        abstract Equivalence<Object> defaultEquivalence();
        
        abstract <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> p0, final ReferenceEntry<K, V> p1, final V p2);
    }
    
    static class StrongEntry<K, V> implements ReferenceEntry<K, V>
    {
        final int hash;
        final K key;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        StrongEntry(final K key, final int hash, @Nullable final ReferenceEntry<K, V> next) {
            this.valueReference = MapMakerInternalMap.unset();
            this.key = key;
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public void setExpirationTime(final long n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference.clear(this.valueReference = valueReference);
        }
    }
    
    static final class StrongEvictableEntry<K, V> extends StrongEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> previousEvictable;
        
        StrongEvictableEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(k, n, referenceEntry);
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
            this.nextEvictable = nextEvictable;
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
            this.previousEvictable = previousEvictable;
        }
    }
    
    static final class StrongExpirableEntry<K, V> extends StrongEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;
        
        StrongExpirableEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(k, n, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public long getExpirationTime() {
            return this.time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }
        
        @Override
        public void setExpirationTime(final long time) {
            this.time = time;
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
            this.nextExpirable = nextExpirable;
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
            this.previousExpirable = previousExpirable;
        }
    }
    
    static final class StrongExpirableEvictableEntry<K, V> extends StrongEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousEvictable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;
        
        StrongExpirableEvictableEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(k, n, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public long getExpirationTime() {
            return this.time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }
        
        @Override
        public void setExpirationTime(final long time) {
            this.time = time;
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
            this.nextEvictable = nextEvictable;
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
            this.nextExpirable = nextExpirable;
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
            this.previousEvictable = previousEvictable;
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
            this.previousExpirable = previousExpirable;
        }
    }
    
    static final class StrongValueReference<K, V> implements ValueReference<K, V>
    {
        final V referent;
        
        StrongValueReference(final V referent) {
            this.referent = referent;
        }
        
        @Override
        public void clear(final ValueReference<K, V> valueReference) {
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return this;
        }
        
        @Override
        public V get() {
            return this.referent;
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        @Override
        public boolean isComputingReference() {
            return false;
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    final class ValueIterator extends HashIterator<V>
    {
        @Override
        public V next() {
            return ((HashIterator)this).nextEntry().getValue();
        }
    }
    
    interface ValueReference<K, V>
    {
        void clear(@Nullable final ValueReference<K, V> p0);
        
        ValueReference<K, V> copyFor(final ReferenceQueue<V> p0, @Nullable final V p1, final ReferenceEntry<K, V> p2);
        
        V get();
        
        ReferenceEntry<K, V> getEntry();
        
        boolean isComputingReference();
        
        V waitForValue() throws ExecutionException;
    }
    
    final class Values extends AbstractCollection<V>
    {
        @Override
        public void clear() {
            MapMakerInternalMap.this.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return MapMakerInternalMap.this.containsValue(o);
        }
        
        @Override
        public boolean isEmpty() {
            return MapMakerInternalMap.this.isEmpty();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public int size() {
            return MapMakerInternalMap.this.size();
        }
        
        @Override
        public Object[] toArray() {
            return toArrayList((Collection<Object>)this).toArray();
        }
        
        @Override
        public <E> E[] toArray(final E[] a) {
            return toArrayList((Collection<Object>)this).toArray(a);
        }
    }
    
    static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V>
    {
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        WeakEntry(final ReferenceQueue<K> q, final K referent, final int hash, @Nullable final ReferenceEntry<K, V> next) {
            super(referent, q);
            this.valueReference = MapMakerInternalMap.unset();
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public long getExpirationTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public K getKey() {
            return this.get();
        }
        
        @Override
        public ReferenceEntry<K, V> getNext() {
            return this.next;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public void setExpirationTime(final long n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference.clear(this.valueReference = valueReference);
        }
    }
    
    static final class WeakEvictableEntry<K, V> extends WeakEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> previousEvictable;
        
        WeakEvictableEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
            this.nextEvictable = nextEvictable;
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
            this.previousEvictable = previousEvictable;
        }
    }
    
    static final class WeakExpirableEntry<K, V> extends WeakEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;
        
        WeakExpirableEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public long getExpirationTime() {
            return this.time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }
        
        @Override
        public void setExpirationTime(final long time) {
            this.time = time;
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
            this.nextExpirable = nextExpirable;
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
            this.previousExpirable = previousExpirable;
        }
    }
    
    static final class WeakExpirableEvictableEntry<K, V> extends WeakEntry<K, V> implements ReferenceEntry<K, V>
    {
        ReferenceEntry<K, V> nextEvictable;
        ReferenceEntry<K, V> nextExpirable;
        ReferenceEntry<K, V> previousEvictable;
        ReferenceEntry<K, V> previousExpirable;
        volatile long time;
        
        WeakExpirableEvictableEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.time = Long.MAX_VALUE;
            this.nextExpirable = MapMakerInternalMap.nullEntry();
            this.previousExpirable = MapMakerInternalMap.nullEntry();
            this.nextEvictable = MapMakerInternalMap.nullEntry();
            this.previousEvictable = MapMakerInternalMap.nullEntry();
        }
        
        @Override
        public long getExpirationTime() {
            return this.time;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextEvictable() {
            return this.nextEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextExpirable() {
            return this.nextExpirable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousEvictable() {
            return this.previousEvictable;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousExpirable() {
            return this.previousExpirable;
        }
        
        @Override
        public void setExpirationTime(final long time) {
            this.time = time;
        }
        
        @Override
        public void setNextEvictable(final ReferenceEntry<K, V> nextEvictable) {
            this.nextEvictable = nextEvictable;
        }
        
        @Override
        public void setNextExpirable(final ReferenceEntry<K, V> nextExpirable) {
            this.nextExpirable = nextExpirable;
        }
        
        @Override
        public void setPreviousEvictable(final ReferenceEntry<K, V> previousEvictable) {
            this.previousEvictable = previousEvictable;
        }
        
        @Override
        public void setPreviousExpirable(final ReferenceEntry<K, V> previousExpirable) {
            this.previousExpirable = previousExpirable;
        }
    }
    
    static final class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        WeakValueReference(final ReferenceQueue<V> q, final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, q);
            this.entry = entry;
        }
        
        @Override
        public void clear(final ValueReference<K, V> valueReference) {
            this.clear();
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return new WeakValueReference((ReferenceQueue<Object>)referenceQueue, v, (ReferenceEntry<Object, Object>)referenceEntry);
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return this.entry;
        }
        
        @Override
        public boolean isComputingReference() {
            return false;
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    final class WriteThroughEntry extends AbstractMapEntry<K, V>
    {
        final K key;
        V value;
        
        WriteThroughEntry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public boolean equals(@Nullable final Object o) {
            boolean b = false;
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry entry = (Entry)o;
            if (this.key.equals(entry.getKey()) && this.value.equals(entry.getValue())) {
                b = true;
            }
            return b;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.value.hashCode();
        }
        
        @Override
        public V setValue(final V value) {
            final Object put = MapMakerInternalMap.this.put(this.key, value);
            this.value = value;
            return (V)put;
        }
    }
}
