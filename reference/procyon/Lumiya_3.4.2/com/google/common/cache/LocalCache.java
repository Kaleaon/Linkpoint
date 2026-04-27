// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import java.util.AbstractCollection;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import com.google.common.util.concurrent.MoreExecutors;
import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.NoSuchElementException;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.j2objc.annotations.Weak;
import java.util.AbstractSet;
import com.google.common.primitives.Ints;
import java.util.logging.Level;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Stopwatch;
import java.util.LinkedHashMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicReferenceArray;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.AbstractQueue;
import java.lang.ref.ReferenceQueue;
import java.util.Collection;
import com.google.common.base.Ticker;
import com.google.common.base.Equivalence;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.logging.Logger;
import java.util.Queue;
import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

@GwtCompatible(emulated = true)
class LocalCache<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>
{
    static final int CONTAINS_VALUE_RETRIES = 3;
    static final Queue<?> DISCARDING_QUEUE;
    static final int DRAIN_MAX = 16;
    static final int DRAIN_THRESHOLD = 63;
    static final int MAXIMUM_CAPACITY = 1073741824;
    static final int MAX_SEGMENTS = 65536;
    static final ValueReference<Object, Object> UNSET;
    static final Logger logger;
    final int concurrencyLevel;
    @Nullable
    final CacheLoader<? super K, V> defaultLoader;
    final EntryFactory entryFactory;
    Set<Entry<K, V>> entrySet;
    final long expireAfterAccessNanos;
    final long expireAfterWriteNanos;
    final AbstractCache.StatsCounter globalStatsCounter;
    final Equivalence<Object> keyEquivalence;
    Set<K> keySet;
    final Strength keyStrength;
    final long maxWeight;
    final long refreshNanos;
    final RemovalListener<K, V> removalListener;
    final Queue<RemovalNotification<K, V>> removalNotificationQueue;
    final int segmentMask;
    final int segmentShift;
    final Segment<K, V>[] segments;
    final Ticker ticker;
    final Equivalence<Object> valueEquivalence;
    final Strength valueStrength;
    Collection<V> values;
    final Weigher<K, V> weigher;
    
    static {
        logger = Logger.getLogger(LocalCache.class.getName());
        UNSET = (ValueReference)new ValueReference<Object, Object>() {
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
            public int getWeight() {
                return 0;
            }
            
            @Override
            public boolean isActive() {
                return false;
            }
            
            @Override
            public boolean isLoading() {
                return false;
            }
            
            @Override
            public void notifyNewValue(final Object o) {
            }
            
            @Override
            public Object waitForValue() {
                return null;
            }
        };
        DISCARDING_QUEUE = new AbstractQueue<Object>() {
            @Override
            public Iterator<Object> iterator() {
                return ImmutableSet.of().iterator();
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
    
    LocalCache(final CacheBuilder<? super K, ? super V> cacheBuilder, @Nullable final CacheLoader<? super K, V> defaultLoader) {
        final int n = 0;
        final int n2 = 0;
        this.concurrencyLevel = Math.min(cacheBuilder.getConcurrencyLevel(), 65536);
        this.keyStrength = cacheBuilder.getKeyStrength();
        this.valueStrength = cacheBuilder.getValueStrength();
        this.keyEquivalence = cacheBuilder.getKeyEquivalence();
        this.valueEquivalence = cacheBuilder.getValueEquivalence();
        this.maxWeight = cacheBuilder.getMaximumWeight();
        this.weigher = cacheBuilder.getWeigher();
        this.expireAfterAccessNanos = cacheBuilder.getExpireAfterAccessNanos();
        this.expireAfterWriteNanos = cacheBuilder.getExpireAfterWriteNanos();
        this.refreshNanos = cacheBuilder.getRefreshNanos();
        this.removalListener = cacheBuilder.getRemovalListener();
        Queue<Object> discardingQueue;
        if (this.removalListener != CacheBuilder.NullListener.INSTANCE) {
            discardingQueue = (Queue<Object>)new ConcurrentLinkedQueue<RemovalNotification<K, V>>();
        }
        else {
            discardingQueue = (Queue<Object>)discardingQueue();
        }
        this.removalNotificationQueue = (Queue<RemovalNotification<K, V>>)discardingQueue;
        this.ticker = cacheBuilder.getTicker(this.recordsTime());
        this.entryFactory = EntryFactory.getFactory(this.keyStrength, this.usesAccessEntries(), this.usesWriteEntries());
        this.globalStatsCounter = (AbstractCache.StatsCounter)cacheBuilder.getStatsCounterSupplier().get();
        this.defaultLoader = defaultLoader;
        final int min = Math.min(cacheBuilder.getInitialCapacity(), 1073741824);
        int min2;
        if (!this.evictsBySize()) {
            min2 = min;
        }
        else {
            min2 = min;
            if (!this.customWeigher()) {
                min2 = Math.min(min, (int)this.maxWeight);
            }
        }
        int i = 1;
        int n3 = 0;
        while (i < this.concurrencyLevel) {
            if (this.evictsBySize()) {
                int n4;
                if (i * 20 > this.maxWeight) {
                    n4 = 1;
                }
                else {
                    n4 = 0;
                }
                if (n4 != 0) {
                    break;
                }
            }
            ++n3;
            i <<= 1;
        }
        this.segmentShift = 32 - n3;
        this.segmentMask = i - 1;
        this.segments = this.newSegmentArray(i);
        int n5 = min2 / i;
        if (n5 * i < min2) {
            ++n5;
        }
        int j;
        for (j = 1; j < n5; j <<= 1) {}
        if (!this.evictsBySize()) {
            for (int k = n2; k < this.segments.length; ++k) {
                this.segments[k] = this.createSegment(j, -1L, (AbstractCache.StatsCounter)cacheBuilder.getStatsCounterSupplier().get());
            }
        }
        else {
            final long n6 = this.maxWeight / i;
            final long maxWeight = this.maxWeight;
            final long n7 = i;
            long n8 = n6 + 1L;
            for (int l = n; l < this.segments.length; ++l) {
                if (l == maxWeight % n7) {
                    --n8;
                }
                this.segments[l] = this.createSegment(j, n8, (AbstractCache.StatsCounter)cacheBuilder.getStatsCounterSupplier().get());
            }
        }
    }
    
    static <K, V> void connectAccessOrder(final ReferenceEntry<K, V> previousInAccessQueue, final ReferenceEntry<K, V> nextInAccessQueue) {
        previousInAccessQueue.setNextInAccessQueue(nextInAccessQueue);
        nextInAccessQueue.setPreviousInAccessQueue(previousInAccessQueue);
    }
    
    static <K, V> void connectWriteOrder(final ReferenceEntry<K, V> previousInWriteQueue, final ReferenceEntry<K, V> nextInWriteQueue) {
        previousInWriteQueue.setNextInWriteQueue(nextInWriteQueue);
        nextInWriteQueue.setPreviousInWriteQueue(previousInWriteQueue);
    }
    
    static <E> Queue<E> discardingQueue() {
        return (Queue<E>)LocalCache.DISCARDING_QUEUE;
    }
    
    static <K, V> ReferenceEntry<K, V> nullEntry() {
        return (ReferenceEntry<K, V>)NullEntry.INSTANCE;
    }
    
    static <K, V> void nullifyAccessOrder(final ReferenceEntry<K, V> referenceEntry) {
        final ReferenceEntry<Object, Object> nullEntry = (ReferenceEntry<Object, Object>)nullEntry();
        referenceEntry.setNextInAccessQueue((ReferenceEntry<K, V>)nullEntry);
        referenceEntry.setPreviousInAccessQueue((ReferenceEntry<K, V>)nullEntry);
    }
    
    static <K, V> void nullifyWriteOrder(final ReferenceEntry<K, V> referenceEntry) {
        final ReferenceEntry<Object, Object> nullEntry = (ReferenceEntry<Object, Object>)nullEntry();
        referenceEntry.setNextInWriteQueue((ReferenceEntry<K, V>)nullEntry);
        referenceEntry.setPreviousInWriteQueue((ReferenceEntry<K, V>)nullEntry);
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
        return (ValueReference<K, V>)LocalCache.UNSET;
    }
    
    public void cleanUp() {
        final Segment<K, V>[] segments = this.segments;
        for (int length = segments.length, i = 0; i < length; ++i) {
            segments[i].cleanUp();
        }
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
            final long read = this.ticker.read();
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
                            final V liveValue = segment.getLiveValue(next, read);
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
    
    Segment<K, V> createSegment(final int n, final long n2, final AbstractCache.StatsCounter statsCounter) {
        return new Segment<K, V>(this, n, n2, statsCounter);
    }
    
    boolean customWeigher() {
        return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
    }
    
    @GwtIncompatible("Not supported.")
    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet;
        if ((entrySet = this.entrySet) == null) {
            entrySet = new EntrySet(this);
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    boolean evictsBySize() {
        boolean b = true;
        int n;
        if (this.maxWeight < 0L) {
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
    
    @Nullable
    @Override
    public V get(@Nullable final Object o) {
        if (o != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).get(o, hash);
        }
        return null;
    }
    
    V get(final K k, final CacheLoader<? super K, V> cacheLoader) throws ExecutionException {
        final int hash = this.hash(Preconditions.checkNotNull(k));
        return this.segmentFor(hash).get(k, hash, cacheLoader);
    }
    
    ImmutableMap<K, V> getAll(final Iterable<? extends K> iterable) throws ExecutionException {
        int n = 0;
        final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
        Object o = Sets.newLinkedHashSet();
        Object o2 = iterable.iterator();
        int n2 = 0;
    Label_0022:
        while (true) {
            Label_0081: {
                if (((Iterator)o2).hasNext()) {
                    break Label_0081;
                }
                while (true) {
                    try {
                        Label_0142: {
                            if (!((Set)o).isEmpty()) {
                                break Label_0142;
                            }
                            int n3 = n;
                        Label_0049:
                            while (true) {
                                n = n3;
                                final Throwable t;
                                try {
                                    final ImmutableMap<Object, Object> copy = ImmutableMap.copyOf((Map<?, ?>)linkedHashMap);
                                    this.globalStatsCounter.recordHits(n2);
                                    this.globalStatsCounter.recordMisses(n3);
                                    return (ImmutableMap<K, V>)copy;
                                    try {
                                        final Map<K, V> loadAll = this.loadAll((Set<? extends K>)o, this.defaultLoader);
                                        for (final Object next : o) {
                                            o2 = loadAll.get(next);
                                            if (o2 == null) {
                                                throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + t);
                                            }
                                            linkedHashMap.put(next, o2);
                                        }
                                        n3 = n;
                                    }
                                    catch (final CacheLoader.UnsupportedLoadingOperationException ex) {
                                        o = ((Set)o).iterator();
                                        int n4 = n;
                                        while (true) {
                                            n3 = n4;
                                            n = n4;
                                            if (!((Iterator)o).hasNext()) {
                                                continue Label_0049;
                                            }
                                            n = n4;
                                            final Object next2 = ((Iterator)o).next();
                                            n = --n4;
                                            linkedHashMap.put(next2, this.get((K)next2, (CacheLoader<? super K, Iterator<K>>)this.defaultLoader));
                                        }
                                    }
                                    final Object next3 = ((Iterator)o2).next();
                                    final V value = this.get(next3);
                                    iftrue(Label_0022:)(linkedHashMap.containsKey(next3));
                                    linkedHashMap.put(next3, value);
                                    iftrue(Label_0127:)(value == null);
                                    ++n2;
                                    break;
                                    Label_0127: {
                                        ++n;
                                    }
                                    ((Set<K>)o).add((K)next3);
                                    break;
                                }
                                finally {}
                                this.globalStatsCounter.recordHits(n2);
                                this.globalStatsCounter.recordMisses(n);
                                throw t;
                                o2 = new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + t);
                                throw o2;
                            }
                        }
                    }
                    finally {
                        continue;
                    }
                    break;
                }
            }
        }
    }
    
    ImmutableMap<K, V> getAllPresent(final Iterable<?> iterable) {
        int n = 0;
        final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
        final Iterator<?> iterator = iterable.iterator();
        int n2 = 0;
        while (iterator.hasNext()) {
            final Object next = iterator.next();
            final V value = this.get(next);
            if (value != null) {
                linkedHashMap.put(next, value);
                ++n2;
            }
            else {
                ++n;
            }
        }
        this.globalStatsCounter.recordHits(n2);
        this.globalStatsCounter.recordMisses(n);
        return ImmutableMap.copyOf((Map<? extends K, ? extends V>)linkedHashMap);
    }
    
    ReferenceEntry<K, V> getEntry(@Nullable final Object o) {
        if (o != null) {
            final int hash = this.hash(o);
            return this.segmentFor(hash).getEntry(o, hash);
        }
        return null;
    }
    
    @Nullable
    public V getIfPresent(Object value) {
        final int hash = this.hash(Preconditions.checkNotNull(value));
        value = this.segmentFor(hash).get(value, hash);
        if (value != null) {
            this.globalStatsCounter.recordHits(1);
        }
        else {
            this.globalStatsCounter.recordMisses(1);
        }
        return (V)value;
    }
    
    @Nullable
    V getLiveValue(final ReferenceEntry<K, V> referenceEntry, final long n) {
        if (referenceEntry.getKey() == null) {
            return null;
        }
        final V value = referenceEntry.getValueReference().get();
        if (value == null) {
            return null;
        }
        if (!this.isExpired(referenceEntry, n)) {
            return value;
        }
        return null;
    }
    
    V getOrLoad(final K k) throws ExecutionException {
        return this.get(k, this.defaultLoader);
    }
    
    int hash(@Nullable final Object o) {
        return rehash(this.keyEquivalence.hash(o));
    }
    
    void invalidateAll(final Iterable<?> iterable) {
        final Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            this.remove(iterator.next());
        }
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
    
    boolean isExpired(final ReferenceEntry<K, V> referenceEntry, final long n) {
        Preconditions.checkNotNull(referenceEntry);
        if (this.expiresAfterAccess()) {
            int n2;
            if (n - referenceEntry.getAccessTime() < this.expireAfterAccessNanos) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                return true;
            }
        }
        if (this.expiresAfterWrite()) {
            int n3;
            if (n - referenceEntry.getWriteTime() < this.expireAfterWriteNanos) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                return true;
            }
        }
        return false;
    }
    
    @VisibleForTesting
    boolean isLive(final ReferenceEntry<K, V> referenceEntry, final long n) {
        return this.segmentFor(referenceEntry.getHash()).getLiveValue(referenceEntry, n) != null;
    }
    
    @Override
    public Set<K> keySet() {
        Set<K> keySet;
        if ((keySet = this.keySet) == null) {
            keySet = new KeySet(this);
            this.keySet = keySet;
        }
        return keySet;
    }
    
    @Nullable
    Map<K, V> loadAll(final Set<? extends K> set, CacheLoader<? super K, V> obj) throws ExecutionException {
        while (true) {
            final int n = 1;
            int n2 = 0;
            Preconditions.checkNotNull(obj);
            Preconditions.checkNotNull(set);
            final Stopwatch started = Stopwatch.createStarted();
            Label_0282: {
                Label_0180: {
                    while (true) {
                        Iterator iterator = null;
                        Label_0225: {
                            try {
                                final Map loadAll = ((CacheLoader)obj).loadAll(set);
                                if (loadAll == null) {
                                    break Label_0180;
                                }
                                started.stop();
                                iterator = loadAll.entrySet().iterator();
                                if (iterator.hasNext()) {
                                    break Label_0225;
                                }
                                if (n2 == 0) {
                                    this.globalStatsCounter.recordLoadSuccess(started.elapsed(TimeUnit.NANOSECONDS));
                                    return loadAll;
                                }
                                break Label_0282;
                            }
                            catch (final CacheLoader.UnsupportedLoadingOperationException ex) {
                                try {
                                    throw ex;
                                }
                                finally {
                                    n2 = n;
                                }
                            }
                            catch (final InterruptedException obj) {
                                Thread.currentThread().interrupt();
                                throw new ExecutionException(obj);
                            }
                            catch (final RuntimeException obj) {
                                throw new UncheckedExecutionException(obj);
                            }
                            catch (final Exception cause) {
                                obj = new ExecutionException(cause);
                                throw obj;
                            }
                            catch (final Error error) {
                                obj = new ExecutionError(error);
                                throw obj;
                            }
                            this.globalStatsCounter.recordLoadException(started.elapsed(TimeUnit.NANOSECONDS));
                            goto Label_0097;
                        }
                        final Map.Entry<Object, V> entry = (Map.Entry<Object, V>)iterator.next();
                        final Object key = entry.getKey();
                        final V value = entry.getValue();
                        if (key != null && value != null) {
                            this.put((K)key, (V)value);
                            continue;
                        }
                        n2 = 1;
                        continue;
                    }
                }
                this.globalStatsCounter.recordLoadException(started.elapsed(TimeUnit.NANOSECONDS));
                throw new CacheLoader.InvalidCacheLoadException(obj + " returned null map from loadAll");
            }
            this.globalStatsCounter.recordLoadException(started.elapsed(TimeUnit.NANOSECONDS));
            throw new CacheLoader.InvalidCacheLoadException(obj + " returned null keys or values from loadAll");
        }
    }
    
    long longSize() {
        final Segment<K, V>[] segments = this.segments;
        long n = 0L;
        for (int i = 0; i < segments.length; ++i) {
            n += Math.max(0, segments[i].count);
        }
        return n;
    }
    
    @VisibleForTesting
    ReferenceEntry<K, V> newEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
        final Segment<K, V> segment = this.segmentFor(n);
        segment.lock();
        try {
            return segment.newEntry(k, n, referenceEntry);
        }
        finally {
            segment.unlock();
        }
    }
    
    final Segment<K, V>[] newSegmentArray(final int n) {
        return new Segment[n];
    }
    
    @VisibleForTesting
    ValueReference<K, V> newValueReference(final ReferenceEntry<K, V> referenceEntry, final V v, final int n) {
        return this.valueStrength.referenceValue(this.segmentFor(referenceEntry.getHash()), referenceEntry, (V)Preconditions.checkNotNull((V)v), n);
    }
    
    void processPendingNotifications() {
        while (true) {
            final RemovalNotification removalNotification = this.removalNotificationQueue.poll();
            if (removalNotification == null) {
                break;
            }
            try {
                this.removalListener.onRemoval(removalNotification);
            }
            catch (final Throwable thrown) {
                LocalCache.logger.log(Level.WARNING, "Exception thrown by removal listener", thrown);
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
    
    boolean recordsAccess() {
        return this.expiresAfterAccess();
    }
    
    boolean recordsTime() {
        boolean b = false;
        if (this.recordsWrite() || this.recordsAccess()) {
            b = true;
        }
        return b;
    }
    
    boolean recordsWrite() {
        boolean b = false;
        if (this.expiresAfterWrite() || this.refreshes()) {
            b = true;
        }
        return b;
    }
    
    void refresh(final K k) {
        final int hash = this.hash(Preconditions.checkNotNull(k));
        this.segmentFor(hash).refresh(k, hash, this.defaultLoader, false);
    }
    
    boolean refreshes() {
        boolean b = true;
        int n;
        if (this.refreshNanos <= 0L) {
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
        return Ints.saturatedCast(this.longSize());
    }
    
    boolean usesAccessEntries() {
        boolean b = false;
        if (this.usesAccessQueue() || this.recordsAccess()) {
            b = true;
        }
        return b;
    }
    
    boolean usesAccessQueue() {
        boolean b = false;
        if (this.expiresAfterAccess() || this.evictsBySize()) {
            b = true;
        }
        return b;
    }
    
    boolean usesKeyReferences() {
        return this.keyStrength != Strength.STRONG;
    }
    
    boolean usesValueReferences() {
        return this.valueStrength != Strength.STRONG;
    }
    
    boolean usesWriteEntries() {
        boolean b = false;
        if (this.usesWriteQueue() || this.recordsWrite()) {
            b = true;
        }
        return b;
    }
    
    boolean usesWriteQueue() {
        return this.expiresAfterWrite();
    }
    
    @Override
    public Collection<V> values() {
        Collection<V> values;
        if ((values = this.values) == null) {
            values = new Values(this);
            this.values = values;
        }
        return values;
    }
    
    abstract class AbstractCacheSet<T> extends AbstractSet<T>
    {
        @Weak
        final ConcurrentMap<?, ?> map;
        
        AbstractCacheSet(final ConcurrentMap<?, ?> map) {
            this.map = map;
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public int size() {
            return this.map.size();
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
    
    abstract static class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V>
    {
        @Override
        public long getAccessTime() {
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
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public long getWriteTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setAccessTime(final long n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setWriteTime(final long n) {
            throw new UnsupportedOperationException();
        }
    }
    
    interface ReferenceEntry<K, V>
    {
        long getAccessTime();
        
        int getHash();
        
        @Nullable
        K getKey();
        
        @Nullable
        ReferenceEntry<K, V> getNext();
        
        ReferenceEntry<K, V> getNextInAccessQueue();
        
        ReferenceEntry<K, V> getNextInWriteQueue();
        
        ReferenceEntry<K, V> getPreviousInAccessQueue();
        
        ReferenceEntry<K, V> getPreviousInWriteQueue();
        
        ValueReference<K, V> getValueReference();
        
        long getWriteTime();
        
        void setAccessTime(final long p0);
        
        void setNextInAccessQueue(final ReferenceEntry<K, V> p0);
        
        void setNextInWriteQueue(final ReferenceEntry<K, V> p0);
        
        void setPreviousInAccessQueue(final ReferenceEntry<K, V> p0);
        
        void setPreviousInWriteQueue(final ReferenceEntry<K, V> p0);
        
        void setValueReference(final ValueReference<K, V> p0);
        
        void setWriteTime(final long p0);
    }
    
    static final class AccessQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>>
    {
        final ReferenceEntry<K, V> head;
        
        AccessQueue() {
            this.head = new AbstractReferenceEntry<K, V>() {
                ReferenceEntry<K, V> nextAccess = this;
                ReferenceEntry<K, V> previousAccess = this;
                
                @Override
                public long getAccessTime() {
                    return Long.MAX_VALUE;
                }
                
                @Override
                public ReferenceEntry<K, V> getNextInAccessQueue() {
                    return this.nextAccess;
                }
                
                @Override
                public ReferenceEntry<K, V> getPreviousInAccessQueue() {
                    return this.previousAccess;
                }
                
                @Override
                public void setAccessTime(final long n) {
                }
                
                @Override
                public void setNextInAccessQueue(final ReferenceEntry<K, V> nextAccess) {
                    this.nextAccess = nextAccess;
                }
                
                @Override
                public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previousAccess) {
                    this.previousAccess = previousAccess;
                }
            };
        }
        
        @Override
        public void clear() {
            ReferenceEntry<K, V> nextInAccessQueue2;
            for (ReferenceEntry<K, V> nextInAccessQueue = this.head.getNextInAccessQueue(); nextInAccessQueue != this.head; nextInAccessQueue = nextInAccessQueue2) {
                nextInAccessQueue2 = nextInAccessQueue.getNextInAccessQueue();
                LocalCache.nullifyAccessOrder(nextInAccessQueue);
            }
            this.head.setNextInAccessQueue(this.head);
            this.head.setPreviousInAccessQueue(this.head);
        }
        
        @Override
        public boolean contains(final Object o) {
            return ((ReferenceEntry)o).getNextInAccessQueue() != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean isEmpty() {
            return this.head.getNextInAccessQueue() == this.head;
        }
        
        @Override
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
                @Override
                protected ReferenceEntry<K, V> computeNext(final ReferenceEntry<K, V> referenceEntry) {
                    ReferenceEntry<K, V> nextInAccessQueue = referenceEntry.getNextInAccessQueue();
                    if (nextInAccessQueue == AccessQueue.this.head) {
                        nextInAccessQueue = null;
                    }
                    return nextInAccessQueue;
                }
            };
        }
        
        @Override
        public boolean offer(final ReferenceEntry<K, V> referenceEntry) {
            LocalCache.connectAccessOrder(referenceEntry.getPreviousInAccessQueue(), referenceEntry.getNextInAccessQueue());
            LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), referenceEntry);
            LocalCache.connectAccessOrder(referenceEntry, this.head);
            return true;
        }
        
        @Override
        public ReferenceEntry<K, V> peek() {
            ReferenceEntry<K, V> nextInAccessQueue = this.head.getNextInAccessQueue();
            if (nextInAccessQueue == this.head) {
                nextInAccessQueue = null;
            }
            return nextInAccessQueue;
        }
        
        @Override
        public ReferenceEntry<K, V> poll() {
            final ReferenceEntry<K, V> nextInAccessQueue = this.head.getNextInAccessQueue();
            if (nextInAccessQueue != this.head) {
                this.remove(nextInAccessQueue);
                return nextInAccessQueue;
            }
            return null;
        }
        
        @Override
        public boolean remove(final Object o) {
            final ReferenceEntry referenceEntry = (ReferenceEntry)o;
            final ReferenceEntry previousInAccessQueue = referenceEntry.getPreviousInAccessQueue();
            final ReferenceEntry nextInAccessQueue = referenceEntry.getNextInAccessQueue();
            LocalCache.connectAccessOrder(previousInAccessQueue, (ReferenceEntry<Object, Object>)nextInAccessQueue);
            LocalCache.nullifyAccessOrder((ReferenceEntry<Object, Object>)referenceEntry);
            return nextInAccessQueue != NullEntry.INSTANCE;
        }
        
        @Override
        public int size() {
            int n = 0;
            for (ReferenceEntry<K, V> referenceEntry = this.head.getNextInAccessQueue(); referenceEntry != this.head; referenceEntry = referenceEntry.getNextInAccessQueue()) {
                ++n;
            }
            return n;
        }
    }
    
    enum EntryFactory
    {
        static final int ACCESS_MASK = 1;
        
        STRONG {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        STRONG_ACCESS {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyAccessEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongAccessEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        STRONG_ACCESS_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyAccessEntry(referenceEntry, copyEntry);
                this.copyWriteEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongAccessWriteEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        STRONG_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyWriteEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new StrongWriteEntry<K, V>(k, n, referenceEntry);
            }
        }, 
        WEAK {
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        }, 
        WEAK_ACCESS {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyAccessEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakAccessEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        }, 
        WEAK_ACCESS_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyAccessEntry(referenceEntry, copyEntry);
                this.copyWriteEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakAccessWriteEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        };
        
        static final int WEAK_MASK = 4;
        
        WEAK_WRITE {
            @Override
             <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
                final ReferenceEntry<K, V> copyEntry = super.copyEntry(segment, referenceEntry, referenceEntry2);
                this.copyWriteEntry(referenceEntry, copyEntry);
                return copyEntry;
            }
            
            @Override
             <K, V> ReferenceEntry<K, V> newEntry(final Segment<K, V> segment, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
                return new WeakWriteEntry<K, V>(segment.keyReferenceQueue, k, n, referenceEntry);
            }
        };
        
        static final int WRITE_MASK = 2;
        static final EntryFactory[] factories;
        
        static {
            factories = new EntryFactory[] { EntryFactory.STRONG, EntryFactory.STRONG_ACCESS, EntryFactory.STRONG_WRITE, EntryFactory.STRONG_ACCESS_WRITE, EntryFactory.WEAK, EntryFactory.WEAK_ACCESS, EntryFactory.WEAK_WRITE, EntryFactory.WEAK_ACCESS_WRITE };
        }
        
        static EntryFactory getFactory(final Strength strength, final boolean b, final boolean b2) {
            int n = false ? 1 : 0;
            int n2;
            if (strength != Strength.WEAK) {
                n2 = (false ? 1 : 0);
            }
            else {
                n2 = 4;
            }
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
            return EntryFactory.factories[n | ((b3 ? 1 : 0) | n2)];
        }
        
         <K, V> void copyAccessEntry(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            referenceEntry2.setAccessTime(referenceEntry.getAccessTime());
            LocalCache.connectAccessOrder((ReferenceEntry<K, V>)referenceEntry.getPreviousInAccessQueue(), referenceEntry2);
            LocalCache.connectAccessOrder((ReferenceEntry<Object, Object>)referenceEntry2, (ReferenceEntry<Object, Object>)referenceEntry.getNextInAccessQueue());
            LocalCache.nullifyAccessOrder((ReferenceEntry)referenceEntry);
        }
        
         <K, V> ReferenceEntry<K, V> copyEntry(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            return this.newEntry(segment, referenceEntry.getKey(), referenceEntry.getHash(), referenceEntry2);
        }
        
         <K, V> void copyWriteEntry(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2) {
            referenceEntry2.setWriteTime(referenceEntry.getWriteTime());
            LocalCache.connectWriteOrder((ReferenceEntry<K, V>)referenceEntry.getPreviousInWriteQueue(), referenceEntry2);
            LocalCache.connectWriteOrder((ReferenceEntry<Object, Object>)referenceEntry2, (ReferenceEntry<Object, Object>)referenceEntry.getNextInWriteQueue());
            LocalCache.nullifyWriteOrder((ReferenceEntry)referenceEntry);
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
    
    final class EntrySet extends AbstractCacheSet<Entry<K, V>>
    {
        EntrySet(final ConcurrentMap<?, ?> concurrentMap) {
            super(concurrentMap);
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
                final V value = LocalCache.this.get(key);
                if (value != null && LocalCache.this.valueEquivalence.equivalent(entry.getValue(), value)) {
                    b = true;
                }
                return b;
            }
            return false;
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
                if (key != null && LocalCache.this.remove(key, entry.getValue())) {
                    b = true;
                }
                return b;
            }
            return false;
        }
    }
    
    abstract class HashIterator<T> implements Iterator<T>
    {
        Segment<K, V> currentSegment;
        AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
        WriteThroughEntry lastReturned;
        ReferenceEntry<K, V> nextEntry;
        WriteThroughEntry nextExternal;
        int nextSegmentIndex;
        int nextTableIndex;
        
        HashIterator() {
            this.nextSegmentIndex = LocalCache.this.segments.length - 1;
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
                    this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
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
                final long read = LocalCache.this.ticker.read();
                final K key = referenceEntry.getKey();
                final Object liveValue = LocalCache.this.getLiveValue((ReferenceEntry<K, Object>)referenceEntry, read);
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
        public abstract T next();
        
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
            Preconditions.checkState(this.lastReturned != null);
            LocalCache.this.remove(this.lastReturned.getKey());
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
    
    final class KeySet extends AbstractCacheSet<K>
    {
        KeySet(final ConcurrentMap<?, ?> concurrentMap) {
            super(concurrentMap);
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map.containsKey(o);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public boolean remove(final Object o) {
            return this.map.remove(o) != null;
        }
    }
    
    static final class LoadingSerializationProxy<K, V> extends ManualSerializationProxy<K, V> implements LoadingCache<K, V>, Serializable
    {
        private static final long serialVersionUID = 1L;
        transient LoadingCache<K, V> autoDelegate;
        
        LoadingSerializationProxy(final LocalCache<K, V> localCache) {
            super(localCache);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.autoDelegate = this.recreateCacheBuilder().build(this.loader);
        }
        
        private Object readResolve() {
            return this.autoDelegate;
        }
        
        @Override
        public final V apply(final K k) {
            return this.autoDelegate.apply(k);
        }
        
        @Override
        public V get(final K k) throws ExecutionException {
            return this.autoDelegate.get(k);
        }
        
        @Override
        public ImmutableMap<K, V> getAll(final Iterable<? extends K> iterable) throws ExecutionException {
            return this.autoDelegate.getAll(iterable);
        }
        
        @Override
        public V getUnchecked(final K k) {
            return this.autoDelegate.getUnchecked(k);
        }
        
        @Override
        public void refresh(final K k) {
            this.autoDelegate.refresh(k);
        }
    }
    
    static class LoadingValueReference<K, V> implements ValueReference<K, V>
    {
        final SettableFuture<V> futureValue;
        volatile ValueReference<K, V> oldValue;
        final Stopwatch stopwatch;
        
        public LoadingValueReference() {
            this(LocalCache.unset());
        }
        
        public LoadingValueReference(final ValueReference<K, V> oldValue) {
            this.futureValue = SettableFuture.create();
            this.stopwatch = Stopwatch.createUnstarted();
            this.oldValue = oldValue;
        }
        
        private ListenableFuture<V> fullyFailedFuture(final Throwable t) {
            return Futures.immediateFailedFuture(t);
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, @Nullable final V v, final ReferenceEntry<K, V> referenceEntry) {
            return this;
        }
        
        public long elapsedNanos() {
            return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
        }
        
        @Override
        public V get() {
            return this.oldValue.get();
        }
        
        @Override
        public ReferenceEntry<K, V> getEntry() {
            return null;
        }
        
        public ValueReference<K, V> getOldValue() {
            return this.oldValue;
        }
        
        @Override
        public int getWeight() {
            return this.oldValue.getWeight();
        }
        
        @Override
        public boolean isActive() {
            return this.oldValue.isActive();
        }
        
        @Override
        public boolean isLoading() {
            return true;
        }
        
        public ListenableFuture<V> loadFuture(final K k, final CacheLoader<? super K, V> cacheLoader) {
            try {
                this.stopwatch.start();
                final V value = this.oldValue.get();
                if (value == null) {
                    final V load = cacheLoader.load(k);
                    ListenableFuture<V> listenableFuture;
                    if (!this.set(load)) {
                        listenableFuture = Futures.immediateFuture(load);
                    }
                    else {
                        listenableFuture = this.futureValue;
                    }
                    return listenableFuture;
                }
                final ListenableFuture<V> reload = cacheLoader.reload(k, value);
                if (reload != null) {
                    return (ListenableFuture<V>)Futures.transform(reload, (Function<? super V, ?>)new Function<V, V>() {
                        @Override
                        public V apply(final V v) {
                            LoadingValueReference.this.set(v);
                            return v;
                        }
                    });
                }
                return Futures.immediateFuture((V)null);
            }
            catch (final Throwable exception) {
                ListenableFuture<V> listenableFuture2;
                if (!this.setException(exception)) {
                    listenableFuture2 = this.fullyFailedFuture(exception);
                }
                else {
                    listenableFuture2 = this.futureValue;
                }
                if (exception instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                return listenableFuture2;
            }
        }
        
        @Override
        public void notifyNewValue(@Nullable final V v) {
            if (v == null) {
                this.oldValue = LocalCache.unset();
            }
            else {
                this.set(v);
            }
        }
        
        public boolean set(@Nullable final V v) {
            return this.futureValue.set(v);
        }
        
        public boolean setException(final Throwable exception) {
            return this.futureValue.setException(exception);
        }
        
        @Override
        public V waitForValue() throws ExecutionException {
            return Uninterruptibles.getUninterruptibly(this.futureValue);
        }
    }
    
    static class LocalLoadingCache<K, V> extends LocalManualCache<K, V> implements LoadingCache<K, V>
    {
        private static final long serialVersionUID = 1L;
        
        LocalLoadingCache(final CacheBuilder<? super K, ? super V> cacheBuilder, final CacheLoader<? super K, V> cacheLoader) {
            super((LocalCache)new LocalCache(cacheBuilder, Preconditions.checkNotNull(cacheLoader)));
        }
        
        @Override
        public final V apply(final K k) {
            return this.getUnchecked(k);
        }
        
        @Override
        public V get(final K k) throws ExecutionException {
            return this.localCache.getOrLoad(k);
        }
        
        @Override
        public ImmutableMap<K, V> getAll(final Iterable<? extends K> iterable) throws ExecutionException {
            return this.localCache.getAll(iterable);
        }
        
        @Override
        public V getUnchecked(final K k) {
            try {
                return this.get(k);
            }
            catch (final ExecutionException ex) {
                throw new UncheckedExecutionException(ex.getCause());
            }
        }
        
        @Override
        public void refresh(final K k) {
            this.localCache.refresh(k);
        }
        
        @Override
        Object writeReplace() {
            return new LoadingSerializationProxy((LocalCache<Object, Object>)this.localCache);
        }
    }
    
    static class LocalManualCache<K, V> implements Cache<K, V>, Serializable
    {
        private static final long serialVersionUID = 1L;
        final LocalCache<K, V> localCache;
        
        LocalManualCache(final CacheBuilder<? super K, ? super V> cacheBuilder) {
            this((LocalCache)new LocalCache(cacheBuilder, null));
        }
        
        private LocalManualCache(final LocalCache<K, V> localCache) {
            this.localCache = localCache;
        }
        
        @Override
        public ConcurrentMap<K, V> asMap() {
            return this.localCache;
        }
        
        @Override
        public void cleanUp() {
            this.localCache.cleanUp();
        }
        
        @Override
        public V get(final K k, final Callable<? extends V> callable) throws ExecutionException {
            Preconditions.checkNotNull(callable);
            return this.localCache.get(k, new CacheLoader<Object, V>() {
                @Override
                public V load(final Object o) throws Exception {
                    return callable.call();
                }
            });
        }
        
        @Override
        public ImmutableMap<K, V> getAllPresent(final Iterable<?> iterable) {
            return this.localCache.getAllPresent(iterable);
        }
        
        @Nullable
        @Override
        public V getIfPresent(final Object o) {
            return this.localCache.getIfPresent(o);
        }
        
        @Override
        public void invalidate(final Object o) {
            Preconditions.checkNotNull(o);
            this.localCache.remove(o);
        }
        
        @Override
        public void invalidateAll() {
            this.localCache.clear();
        }
        
        @Override
        public void invalidateAll(final Iterable<?> iterable) {
            this.localCache.invalidateAll(iterable);
        }
        
        @Override
        public void put(final K k, final V v) {
            this.localCache.put(k, v);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends V> map) {
            this.localCache.putAll(map);
        }
        
        @Override
        public long size() {
            return this.localCache.longSize();
        }
        
        @Override
        public CacheStats stats() {
            final AbstractCache.SimpleStatsCounter simpleStatsCounter = new AbstractCache.SimpleStatsCounter();
            simpleStatsCounter.incrementBy(this.localCache.globalStatsCounter);
            final Segment<K, V>[] segments = this.localCache.segments;
            for (int length = segments.length, i = 0; i < length; ++i) {
                simpleStatsCounter.incrementBy(segments[i].statsCounter);
            }
            return simpleStatsCounter.snapshot();
        }
        
        Object writeReplace() {
            return new ManualSerializationProxy((LocalCache<Object, Object>)this.localCache);
        }
    }
    
    static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V> implements Serializable
    {
        private static final long serialVersionUID = 1L;
        final int concurrencyLevel;
        transient Cache<K, V> delegate;
        final long expireAfterAccessNanos;
        final long expireAfterWriteNanos;
        final Equivalence<Object> keyEquivalence;
        final Strength keyStrength;
        final CacheLoader<? super K, V> loader;
        final long maxWeight;
        final RemovalListener<? super K, ? super V> removalListener;
        final Ticker ticker;
        final Equivalence<Object> valueEquivalence;
        final Strength valueStrength;
        final Weigher<K, V> weigher;
        
        private ManualSerializationProxy(final Strength keyStrength, final Strength valueStrength, final Equivalence<Object> keyEquivalence, final Equivalence<Object> valueEquivalence, final long expireAfterWriteNanos, final long expireAfterAccessNanos, final long maxWeight, final Weigher<K, V> weigher, final int concurrencyLevel, final RemovalListener<? super K, ? super V> removalListener, Ticker ticker, final CacheLoader<? super K, V> loader) {
            this.keyStrength = keyStrength;
            this.valueStrength = valueStrength;
            this.keyEquivalence = keyEquivalence;
            this.valueEquivalence = valueEquivalence;
            this.expireAfterWriteNanos = expireAfterWriteNanos;
            this.expireAfterAccessNanos = expireAfterAccessNanos;
            this.maxWeight = maxWeight;
            this.weigher = weigher;
            this.concurrencyLevel = concurrencyLevel;
            this.removalListener = removalListener;
            if (ticker == Ticker.systemTicker() || ticker == CacheBuilder.NULL_TICKER) {
                ticker = null;
            }
            this.ticker = ticker;
            this.loader = loader;
        }
        
        ManualSerializationProxy(final LocalCache<K, V> localCache) {
            this(localCache.keyStrength, localCache.valueStrength, localCache.keyEquivalence, localCache.valueEquivalence, localCache.expireAfterWriteNanos, localCache.expireAfterAccessNanos, localCache.maxWeight, localCache.weigher, localCache.concurrencyLevel, localCache.removalListener, localCache.ticker, localCache.defaultLoader);
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.delegate = this.recreateCacheBuilder().build();
        }
        
        private Object readResolve() {
            return this.delegate;
        }
        
        @Override
        protected Cache<K, V> delegate() {
            return this.delegate;
        }
        
        CacheBuilder<K, V> recreateCacheBuilder() {
            final int n = 1;
            final CacheBuilder<Object, Object> removalListener = (CacheBuilder<Object, Object>)CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
            removalListener.strictParsing = false;
            int n2;
            if (this.expireAfterWriteNanos <= 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 == 0) {
                removalListener.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
            }
            int n3;
            if (this.expireAfterAccessNanos <= 0L) {
                n3 = n;
            }
            else {
                n3 = 0;
            }
            if (n3 == 0) {
                removalListener.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
            }
            if (this.weigher == CacheBuilder.OneWeigher.INSTANCE) {
                if (this.maxWeight != -1L) {
                    removalListener.maximumSize(this.maxWeight);
                }
            }
            else {
                removalListener.weigher((Weigher<? super Object, ? super Object>)this.weigher);
                if (this.maxWeight != -1L) {
                    removalListener.maximumWeight(this.maxWeight);
                }
            }
            if (this.ticker != null) {
                removalListener.ticker(this.ticker);
            }
            return (CacheBuilder<K, V>)removalListener;
        }
    }
    
    private enum NullEntry implements ReferenceEntry<Object, Object>
    {
        INSTANCE;
        
        @Override
        public long getAccessTime() {
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
        public ReferenceEntry<Object, Object> getNextInAccessQueue() {
            return this;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getNextInWriteQueue() {
            return this;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
            return this;
        }
        
        @Override
        public ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
            return this;
        }
        
        @Override
        public ValueReference<Object, Object> getValueReference() {
            return null;
        }
        
        @Override
        public long getWriteTime() {
            return 0L;
        }
        
        @Override
        public void setAccessTime(final long n) {
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<Object, Object> referenceEntry) {
        }
        
        @Override
        public void setValueReference(final ValueReference<Object, Object> valueReference) {
        }
        
        @Override
        public void setWriteTime(final long n) {
        }
    }
    
    static class Segment<K, V> extends ReentrantLock
    {
        @GuardedBy("this")
        final Queue<ReferenceEntry<K, V>> accessQueue;
        volatile int count;
        final ReferenceQueue<K> keyReferenceQueue;
        @Weak
        final LocalCache<K, V> map;
        final long maxSegmentWeight;
        int modCount;
        final AtomicInteger readCount;
        final Queue<ReferenceEntry<K, V>> recencyQueue;
        final AbstractCache.StatsCounter statsCounter;
        volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
        int threshold;
        @GuardedBy("this")
        long totalWeight;
        final ReferenceQueue<V> valueReferenceQueue;
        @GuardedBy("this")
        final Queue<ReferenceEntry<K, V>> writeQueue;
        
        Segment(final LocalCache<K, V> map, final int n, final long maxSegmentWeight, final AbstractCache.StatsCounter statsCounter) {
            final ReferenceQueue<V> referenceQueue = null;
            this.readCount = new AtomicInteger();
            this.map = map;
            this.maxSegmentWeight = maxSegmentWeight;
            this.statsCounter = Preconditions.checkNotNull(statsCounter);
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
            Queue<Object> discardingQueue;
            if (!map.usesAccessQueue()) {
                discardingQueue = (Queue<Object>)LocalCache.discardingQueue();
            }
            else {
                discardingQueue = (Queue<Object>)new ConcurrentLinkedQueue<ReferenceEntry<K, V>>();
            }
            this.recencyQueue = (Queue<ReferenceEntry<K, V>>)discardingQueue;
            Queue<Object> discardingQueue2;
            if (!map.usesWriteQueue()) {
                discardingQueue2 = (Queue<Object>)LocalCache.discardingQueue();
            }
            else {
                discardingQueue2 = (Queue<Object>)new WriteQueue();
            }
            this.writeQueue = (Queue<ReferenceEntry<K, V>>)discardingQueue2;
            Queue<Object> discardingQueue3;
            if (!map.usesAccessQueue()) {
                discardingQueue3 = (Queue<Object>)LocalCache.discardingQueue();
            }
            else {
                discardingQueue3 = (Queue<Object>)new AccessQueue();
            }
            this.accessQueue = (Queue<ReferenceEntry<K, V>>)discardingQueue3;
        }
        
        void cleanUp() {
            this.runLockedCleanup(this.map.ticker.read());
            this.runUnlockedCleanup();
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
                            ReferenceEntry<?, ?> next;
                            for (table = this.table, i = 0; i < table.length(); ++i) {
                                for (next = table.get(i); next != null; next = next.getNext()) {
                                    if (next.getValueReference().isActive()) {
                                        this.enqueueNotification((ReferenceEntry<K, V>)next, RemovalCause.EXPLICIT);
                                    }
                                }
                            }
                            i = n;
                            if (i >= table.length()) {
                                this.clearReferenceQueues();
                                this.writeQueue.clear();
                                this.accessQueue.clear();
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
        
        void clearValueReferenceQueue() {
            while (this.valueReferenceQueue.poll() != null) {}
        }
        
        boolean containsKey(Object value, final int n) {
            boolean b = false;
            try {
                if (this.count == 0) {
                    return false;
                }
                final ReferenceEntry<K, V> liveEntry = this.getLiveEntry(value, n, this.map.ticker.read());
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
                    final long read = this.map.ticker.read();
                    final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                    for (int length = table.length(), i = 0; i < length; ++i) {
                        for (ReferenceEntry<?, ?> next = table.get(i); next != null; next = next.getNext()) {
                            final Object liveValue = this.getLiveValue((ReferenceEntry<K, Object>)next, read);
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
            if (value == null && valueReference.isActive()) {
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
                if (!this.accessQueue.contains(referenceEntry)) {
                    continue;
                }
                this.accessQueue.add(referenceEntry);
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
        
        @GuardedBy("this")
        void enqueueNotification(final ReferenceEntry<K, V> referenceEntry, final RemovalCause removalCause) {
            this.enqueueNotification(referenceEntry.getKey(), referenceEntry.getHash(), referenceEntry.getValueReference(), removalCause);
        }
        
        @GuardedBy("this")
        void enqueueNotification(@Nullable final K k, final int n, final ValueReference<K, V> valueReference, final RemovalCause removalCause) {
            this.totalWeight -= valueReference.getWeight();
            if (removalCause.wasEvicted()) {
                this.statsCounter.recordEviction();
            }
            if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
                this.map.removalNotificationQueue.offer(RemovalNotification.create(k, valueReference.get(), removalCause));
            }
        }
        
        @GuardedBy("this")
        void evictEntries(final ReferenceEntry<K, V> referenceEntry) {
            if (!this.map.evictsBySize()) {
                return;
            }
            this.drainRecencyQueue();
            boolean b;
            if (referenceEntry.getValueReference().getWeight() <= this.maxSegmentWeight) {
                b = true;
            }
            else {
                b = false;
            }
            if (!b && !this.removeEntry(referenceEntry, referenceEntry.getHash(), RemovalCause.SIZE)) {
                throw new AssertionError();
            }
            while (true) {
                int n;
                if (this.totalWeight <= this.maxSegmentWeight) {
                    n = 1;
                }
                else {
                    n = 0;
                }
                if (n != 0) {
                    return;
                }
                final ReferenceEntry<K, V> nextEvictable = this.getNextEvictable();
                if (!this.removeEntry(nextEvictable, nextEvictable.getHash(), RemovalCause.SIZE)) {
                    throw new AssertionError();
                }
            }
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
        void expireEntries(final long n) {
            this.drainRecencyQueue();
            ReferenceEntry referenceEntry;
            do {
                referenceEntry = this.writeQueue.peek();
                if (referenceEntry != null && this.map.isExpired(referenceEntry, n)) {
                    continue;
                }
                ReferenceEntry referenceEntry2;
                do {
                    referenceEntry2 = this.accessQueue.peek();
                    if (referenceEntry2 != null && this.map.isExpired(referenceEntry2, n)) {
                        continue;
                    }
                    return;
                } while (this.removeEntry(referenceEntry2, referenceEntry2.getHash(), RemovalCause.EXPIRED));
                throw new AssertionError();
            } while (this.removeEntry(referenceEntry, referenceEntry.getHash(), RemovalCause.EXPIRED));
            throw new AssertionError();
        }
        
        @Nullable
        V get(Object o, final int n) {
            long read = 0L;
            ReferenceEntry<K, V> liveEntry = null;
            Label_0079: {
                Label_0073: {
                    try {
                        if (this.count != 0) {
                            read = this.map.ticker.read();
                            liveEntry = this.getLiveEntry(o, n, read);
                            if (liveEntry == null) {
                                break Label_0073;
                            }
                            o = liveEntry.getValueReference().get();
                            if (o != null) {
                                break Label_0079;
                            }
                            this.tryDrainReferenceQueues();
                        }
                        return null;
                    }
                    finally {
                        this.postReadCleanup();
                    }
                }
                this.postReadCleanup();
                return null;
            }
            this.recordRead(liveEntry, read);
            final V v;
            o = this.scheduleRefresh((ReferenceEntry<K, Object>)liveEntry, liveEntry.getKey(), n, v, read, (CacheLoader<? super K, Object>)this.map.defaultLoader);
            this.postReadCleanup();
            return (V)o;
        }
        
        V get(final K k, final int n, final CacheLoader<? super K, V> cacheLoader) throws ExecutionException {
            Preconditions.checkNotNull(k);
            Preconditions.checkNotNull(cacheLoader);
            final Error error;
            try {
                if (this.count != 0) {
                    final ReferenceEntry<K, Object> entry = this.getEntry(k, n);
                    if (entry != null) {
                        final long read = this.map.ticker.read();
                        final Object liveValue = this.getLiveValue(entry, read);
                        if (liveValue != null) {
                            this.recordRead((ReferenceEntry<K, V>)entry, read);
                            this.statsCounter.recordHits(1);
                            return (V)this.scheduleRefresh(entry, k, n, liveValue, read, (CacheLoader<? super K, Object>)cacheLoader);
                        }
                        final ValueReference<K, Object> valueReference = entry.getValueReference();
                        if (valueReference.isLoading()) {
                            return (V)this.waitForLoadingValue(entry, k, valueReference);
                        }
                    }
                }
                return (V)this.lockedGetOrLoad(k, n, (CacheLoader<? super K, Object>)cacheLoader);
            }
            catch (final ExecutionException ex) {
                final Throwable cause = ex.getCause();
                if (!(cause instanceof Error)) {
                    if (!(cause instanceof RuntimeException)) {
                        throw ex;
                    }
                    throw new UncheckedExecutionException(error);
                }
            }
            finally {
                this.postReadCleanup();
            }
            throw new ExecutionError(error);
        }
        
        V getAndRecordStats(final K obj, final int n, final LoadingValueReference<K, V> loadingValueReference, final ListenableFuture<V> listenableFuture) throws ExecutionException {
            while (true) {
                V v = null;
                while (true) {
                    Label_0152: {
                        V uninterruptibly;
                        try {
                            uninterruptibly = Uninterruptibles.getUninterruptibly(listenableFuture);
                            if (uninterruptibly != null) {
                                v = uninterruptibly;
                                this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
                                v = uninterruptibly;
                                this.storeLoadedValue(obj, n, loadingValueReference, uninterruptibly);
                                return uninterruptibly;
                            }
                            v = uninterruptibly;
                            v = uninterruptibly;
                            v = uninterruptibly;
                            final StringBuilder sb = new StringBuilder();
                            v = uninterruptibly;
                            final CacheLoader.InvalidCacheLoadException ex = new CacheLoader.InvalidCacheLoadException(sb.append("CacheLoader returned null for key ").append(obj).append(".").toString());
                            v = uninterruptibly;
                            throw ex;
                        }
                        finally {
                            if (v == null) {
                                break Label_0152;
                            }
                        }
                        this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
                        this.removeLoadingValue(obj, n, loadingValueReference);
                        return uninterruptibly;
                    }
                    this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
                    this.removeLoadingValue(obj, n, loadingValueReference);
                    continue;
                }
            }
        }
        
        @Nullable
        ReferenceEntry<K, V> getEntry(final Object o, final int n) {
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
            return null;
        }
        
        ReferenceEntry<K, V> getFirst(final int n) {
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            return (ReferenceEntry<K, V>)(ReferenceEntry)table.get(table.length() - 1 & n);
        }
        
        @Nullable
        ReferenceEntry<K, V> getLiveEntry(final Object o, final int n, final long n2) {
            final ReferenceEntry<K, V> entry = this.getEntry(o, n);
            if (entry == null) {
                return null;
            }
            if (!this.map.isExpired(entry, n2)) {
                return entry;
            }
            this.tryExpireEntries(n2);
            return null;
        }
        
        V getLiveValue(final ReferenceEntry<K, V> referenceEntry, final long n) {
            if (referenceEntry.getKey() == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            final V value = referenceEntry.getValueReference().get();
            if (value == null) {
                this.tryDrainReferenceQueues();
                return null;
            }
            if (!this.map.isExpired(referenceEntry, n)) {
                return value;
            }
            this.tryExpireEntries(n);
            return null;
        }
        
        @GuardedBy("this")
        ReferenceEntry<K, V> getNextEvictable() {
            for (final ReferenceEntry<K, V> referenceEntry : this.accessQueue) {
                if (referenceEntry.getValueReference().getWeight() > 0) {
                    return referenceEntry;
                }
            }
            throw new AssertionError();
        }
        
        void initTable(final AtomicReferenceArray<ReferenceEntry<K, V>> table) {
            this.threshold = table.length() * 3 / 4;
            if (!this.map.customWeigher() && this.threshold == this.maxSegmentWeight) {
                ++this.threshold;
            }
            this.table = table;
        }
        
        @Nullable
        LoadingValueReference<K, V> insertLoadingValueReference(final K k, int n, final boolean b) {
            this.lock();
            try {
                final long read = this.map.ticker.read();
                this.preWriteCleanup(read);
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n2 = n & table.length() - 1;
                Object next;
                ReferenceEntry<Object, ?> referenceEntry;
                for (referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n2)); next != null; next = ((ReferenceEntry<Object, V>)next).getNext()) {
                    final Object key = ((ReferenceEntry<Object, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final ValueReference<Object, V> valueReference = ((ReferenceEntry<Object, V>)next).getValueReference();
                        if (!valueReference.isLoading()) {
                            if (b) {
                                if (read - ((ReferenceEntry)next).getWriteTime() >= this.map.refreshNanos) {
                                    n = 1;
                                }
                                else {
                                    n = 0;
                                }
                                if (n == 0) {
                                    return null;
                                }
                            }
                            ++this.modCount;
                            final LoadingValueReference valueReference2 = new LoadingValueReference<Object, Object>((ValueReference<Object, V>)valueReference);
                            ((ReferenceEntry<Object, V>)next).setValueReference((ValueReference<Object, V>)valueReference2);
                            return (LoadingValueReference<K, V>)valueReference2;
                        }
                        return null;
                    }
                }
                ++this.modCount;
                final LoadingValueReference<K, V> valueReference3 = new LoadingValueReference<K, V>();
                final ReferenceEntry<K, V> entry = this.newEntry(k, n, (ReferenceEntry<K, V>)referenceEntry);
                entry.setValueReference(valueReference3);
                table.set(n2, entry);
                return valueReference3;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        ListenableFuture<V> loadAsync(final K k, final int n, final LoadingValueReference<K, V> loadingValueReference, final CacheLoader<? super K, V> cacheLoader) {
            final ListenableFuture<V> loadFuture = loadingValueReference.loadFuture(k, cacheLoader);
            loadFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        Segment.this.getAndRecordStats(k, n, loadingValueReference, loadFuture);
                    }
                    catch (final Throwable t) {
                        LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
                        loadingValueReference.setException(t);
                    }
                }
            }, MoreExecutors.directExecutor());
            return loadFuture;
        }
        
        V loadSync(final K k, final int n, final LoadingValueReference<K, V> loadingValueReference, final CacheLoader<? super K, V> cacheLoader) throws ExecutionException {
            return this.getAndRecordStats(k, n, loadingValueReference, loadingValueReference.loadFuture(k, cacheLoader));
        }
        
        V lockedGetOrLoad(final K k, final int n, final CacheLoader<? super K, V> cacheLoader) throws ExecutionException {
            Object newValue = null;
            K key = null;
        Label_0080_Outer:
            while (true) {
                Object valueReference = null;
                boolean b = true;
                this.lock();
                while (true) {
                    AtomicReferenceArray<ReferenceEntry<K, V>> table = null;
                    int n2 = 0;
                    ReferenceEntry<Object, ?> referenceEntry = null;
                Label_0311:
                    while (true) {
                        Label_0296: {
                            try {
                                final long read = this.map.ticker.read();
                                this.preWriteCleanup(read);
                                final int count = this.count;
                                table = this.table;
                                n2 = (n & table.length() - 1);
                                referenceEntry = (ReferenceEntry<Object, ?>)(newValue = table.get(n2));
                                while (newValue != null) {
                                    key = ((ReferenceEntry<K, V>)newValue).getKey();
                                    if (((ReferenceEntry)newValue).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                                        valueReference = ((ReferenceEntry<K, V>)newValue).getValueReference();
                                        if (((ValueReference)valueReference).isLoading()) {
                                            b = false;
                                            break;
                                        }
                                        final V value = ((ValueReference<Object, V>)valueReference).get();
                                        if (value == null) {
                                            this.enqueueNotification(key, n, (ValueReference<K, V>)valueReference, RemovalCause.COLLECTED);
                                            this.writeQueue.remove(newValue);
                                            this.accessQueue.remove(newValue);
                                            this.count = count - 1;
                                            break;
                                        }
                                        if (!this.map.isExpired((ReferenceEntry<K, V>)newValue, read)) {
                                            this.recordLockedRead((ReferenceEntry<K, V>)newValue, read);
                                            this.statsCounter.recordHits(1);
                                            return value;
                                        }
                                        break Label_0296;
                                    }
                                    else {
                                        newValue = ((ReferenceEntry<K, V>)newValue).getNext();
                                    }
                                }
                                if (b) {
                                    break Label_0311;
                                }
                                key = null;
                                this.unlock();
                                this.postWriteCleanup();
                                if (!b) {
                                    return this.waitForLoadingValue((ReferenceEntry<Object, V>)newValue, k, (ValueReference<Object, V>)valueReference);
                                }
                                break;
                            }
                            finally {
                                this.unlock();
                                this.postWriteCleanup();
                            }
                        }
                        this.enqueueNotification(key, n, (ValueReference<K, V>)valueReference, RemovalCause.EXPIRED);
                        continue Label_0080_Outer;
                    }
                    key = (K)new LoadingValueReference<Object, V>();
                    if (newValue != null) {
                        ((ReferenceEntry<K, V>)newValue).setValueReference((ValueReference<K, V>)key);
                        continue;
                    }
                    newValue = this.newEntry((Object)k, n, (ReferenceEntry<Object, V>)referenceEntry);
                    ((ReferenceEntry<K, V>)newValue).setValueReference((ValueReference<K, V>)key);
                    table.set(n2, (ReferenceEntry<K, V>)newValue);
                    continue;
                }
            }
            try {
                synchronized (newValue) {
                    return (V)this.loadSync(k, n, (LoadingValueReference<Object, Object>)key, (CacheLoader<? super Object, Object>)cacheLoader);
                }
            }
            finally {
                this.statsCounter.recordMisses(1);
            }
        }
        
        @GuardedBy("this")
        ReferenceEntry<K, V> newEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            return this.map.entryFactory.newEntry(this, (K)Preconditions.checkNotNull((K)k), n, referenceEntry);
        }
        
        AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(final int length) {
            return new AtomicReferenceArray<ReferenceEntry<K, V>>(length);
        }
        
        void postReadCleanup() {
            if ((this.readCount.incrementAndGet() & 0x3F) == 0x0) {
                this.cleanUp();
            }
        }
        
        void postWriteCleanup() {
            this.runUnlockedCleanup();
        }
        
        @GuardedBy("this")
        void preWriteCleanup(final long n) {
            this.runLockedCleanup(n);
        }
        
        @Nullable
        V put(final K k, int count, final V v, final boolean b) {
            this.lock();
            try {
                final long read = this.map.ticker.read();
                this.preWriteCleanup(read);
                if (this.count + 1 > this.threshold) {
                    this.expand();
                    final int count2 = this.count;
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
                            if (!valueReference.isActive()) {
                                this.setValue((ReferenceEntry<K, V>)next, k, v, read);
                                count = this.count + 1;
                            }
                            else {
                                this.enqueueNotification(k, count, valueReference, RemovalCause.COLLECTED);
                                this.setValue((ReferenceEntry<K, V>)next, k, v, read);
                                count = this.count;
                            }
                            this.count = count;
                            this.evictEntries((ReferenceEntry<K, V>)next);
                            return null;
                        }
                        if (!b) {
                            ++this.modCount;
                            this.enqueueNotification(k, count, valueReference, RemovalCause.REPLACED);
                            this.setValue((ReferenceEntry<K, V>)next, k, v, read);
                            this.evictEntries((ReferenceEntry<K, V>)next);
                            return value;
                        }
                        this.recordLockedRead((ReferenceEntry<K, V>)next, read);
                        return value;
                    }
                    else {
                        next = ((ReferenceEntry<K, V>)next).getNext();
                    }
                }
                ++this.modCount;
                final ReferenceEntry<K, V> entry = this.newEntry(k, count, (ReferenceEntry<K, V>)referenceEntry);
                this.setValue(entry, k, v, read);
                table.set(n, (ReferenceEntry)entry);
                ++this.count;
                this.evictEntries(entry);
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
                        final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain(referenceEntry2, next, next.getKey(), count, next.getValueReference(), RemovalCause.COLLECTED);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeValueFromChain);
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
                    final K key = ((ReferenceEntry<K, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (((ReferenceEntry<K, V>)next).getValueReference() != valueReference) {
                            this.unlock();
                            if (!this.isHeldByCurrentThread()) {
                                this.postWriteCleanup();
                            }
                            return false;
                        }
                        ++this.modCount;
                        final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next, key, count, valueReference, RemovalCause.COLLECTED);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeValueFromChain);
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
                        break Label_0232;
                    }
                    this.postWriteCleanup();
                    break Label_0232;
                    continue;
                }
            }
        }
        
        @GuardedBy("this")
        void recordLockedRead(final ReferenceEntry<K, V> referenceEntry, final long accessTime) {
            if (this.map.recordsAccess()) {
                referenceEntry.setAccessTime(accessTime);
            }
            this.accessQueue.add(referenceEntry);
        }
        
        void recordRead(final ReferenceEntry<K, V> referenceEntry, final long accessTime) {
            if (this.map.recordsAccess()) {
                referenceEntry.setAccessTime(accessTime);
            }
            this.recencyQueue.add(referenceEntry);
        }
        
        @GuardedBy("this")
        void recordWrite(final ReferenceEntry<K, V> referenceEntry, final int n, final long n2) {
            this.drainRecencyQueue();
            this.totalWeight += n;
            if (this.map.recordsAccess()) {
                referenceEntry.setAccessTime(n2);
            }
            if (this.map.recordsWrite()) {
                referenceEntry.setWriteTime(n2);
            }
            this.accessQueue.add(referenceEntry);
            this.writeQueue.add(referenceEntry);
        }
        
        @Nullable
        V refresh(final K k, final int n, final CacheLoader<? super K, V> cacheLoader, final boolean b) {
            final LoadingValueReference<K, V> insertLoadingValueReference = this.insertLoadingValueReference(k, n, b);
            if (insertLoadingValueReference != null) {
                final ListenableFuture<V> loadAsync = this.loadAsync(k, n, insertLoadingValueReference, cacheLoader);
                if (loadAsync.isDone()) {
                    try {
                        return Uninterruptibles.getUninterruptibly(loadAsync);
                    }
                    catch (final Throwable t) {}
                }
                return null;
            }
            return null;
        }
        
        @Nullable
        V remove(final Object o, int count) {
            this.lock();
            try {
                this.preWriteCleanup(this.map.ticker.read());
                final int count2 = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                for (ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n)); next != null; next = ((ReferenceEntry<K, V>)next).getNext()) {
                    final K key = ((ReferenceEntry<K, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(o, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        final V value = valueReference.get();
                        RemovalCause removalCause;
                        if (value == null) {
                            if (!valueReference.isActive()) {
                                return null;
                            }
                            removalCause = RemovalCause.COLLECTED;
                        }
                        else {
                            removalCause = RemovalCause.EXPLICIT;
                        }
                        ++this.modCount;
                        final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next, key, count, valueReference, removalCause);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeValueFromChain);
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
        
        boolean remove(Object value, int count, final Object o) {
            this.lock();
            try {
                this.preWriteCleanup(this.map.ticker.read());
                final int count2 = this.count;
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n = count & table.length() - 1;
                Object next;
                for (ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n)); next != null; next = ((ReferenceEntry<K, V>)next).getNext()) {
                    final K key = ((ReferenceEntry<K, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(value, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        value = valueReference.get();
                        RemovalCause removalCause;
                        if (!this.map.valueEquivalence.equivalent(o, value)) {
                            if (value != null || !valueReference.isActive()) {
                                return false;
                            }
                            removalCause = RemovalCause.COLLECTED;
                        }
                        else {
                            removalCause = RemovalCause.EXPLICIT;
                        }
                        ++this.modCount;
                        final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next, key, count, valueReference, removalCause);
                        count = this.count;
                        table.set(n, (ReferenceEntry)removeValueFromChain);
                        this.count = count - 1;
                        return removalCause == RemovalCause.EXPLICIT;
                    }
                }
                return false;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
        }
        
        @GuardedBy("this")
        void removeCollectedEntry(final ReferenceEntry<K, V> referenceEntry) {
            this.enqueueNotification(referenceEntry, RemovalCause.COLLECTED);
            this.writeQueue.remove(referenceEntry);
            this.accessQueue.remove(referenceEntry);
        }
        
        @GuardedBy("this")
        boolean removeEntry(final ReferenceEntry<K, V> referenceEntry, int count, final RemovalCause removalCause) {
            final int count2 = this.count;
            final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
            final int n = count & table.length() - 1;
            ReferenceEntry<K, V> next;
            for (ReferenceEntry<K, V> referenceEntry2 = next = table.get(n); next != null; next = next.getNext()) {
                if (next == referenceEntry) {
                    ++this.modCount;
                    final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain(referenceEntry2, next, next.getKey(), count, next.getValueReference(), removalCause);
                    count = this.count;
                    table.set(n, (ReferenceEntry)removeValueFromChain);
                    this.count = count - 1;
                    return true;
                }
            }
            return false;
        }
        
        @Nullable
        @GuardedBy("this")
        ReferenceEntry<K, V> removeEntryFromChain(ReferenceEntry<K, V> next, final ReferenceEntry<K, V> referenceEntry) {
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
        
        boolean removeLoadingValue(final K k, final int n, final LoadingValueReference<K, V> loadingValueReference) {
            this.lock();
            try {
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n2 = n & table.length() - 1;
                Object next;
                final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n2));
                while (next != null) {
                    final Object key = ((ReferenceEntry<Object, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        if (((ReferenceEntry<K, V>)next).getValueReference() != loadingValueReference) {
                            return false;
                        }
                        if (!loadingValueReference.isActive()) {
                            table.set(n2, this.removeEntryFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next));
                        }
                        else {
                            ((ReferenceEntry<K, V>)next).setValueReference(loadingValueReference.getOldValue());
                        }
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
        
        @Nullable
        @GuardedBy("this")
        ReferenceEntry<K, V> removeValueFromChain(final ReferenceEntry<K, V> referenceEntry, final ReferenceEntry<K, V> referenceEntry2, @Nullable final K k, final int n, final ValueReference<K, V> valueReference, final RemovalCause removalCause) {
            this.enqueueNotification(k, n, valueReference, removalCause);
            this.writeQueue.remove(referenceEntry2);
            this.accessQueue.remove(referenceEntry2);
            if (!valueReference.isLoading()) {
                return this.removeEntryFromChain(referenceEntry, referenceEntry2);
            }
            valueReference.notifyNewValue(null);
            return referenceEntry;
        }
        
        @Nullable
        V replace(final K k, int count, final V v) {
            this.lock();
            try {
                final long read = this.map.ticker.read();
                this.preWriteCleanup(read);
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
                            this.enqueueNotification(k, count, valueReference, RemovalCause.REPLACED);
                            this.setValue((ReferenceEntry<K, V>)next, k, v, read);
                            this.evictEntries((ReferenceEntry<K, V>)next);
                            return value;
                        }
                        if (valueReference.isActive()) {
                            final int count2 = this.count;
                            ++this.modCount;
                            final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next, key, count, valueReference, RemovalCause.COLLECTED);
                            count = this.count;
                            table.set(n, (ReferenceEntry)removeValueFromChain);
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
            long read = 0L;
            Object next = null;
            ValueReference<K, V> valueReference = null;
            Label_0271: {
                try {
                    read = this.map.ticker.read();
                    this.preWriteCleanup(read);
                    final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                    final int n = count & table.length() - 1;
                    final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n));
                    while (next != null) {
                        final K key = ((ReferenceEntry<K, V>)next).getKey();
                        if (((ReferenceEntry)next).getHash() == count && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                            valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                            final V value = valueReference.get();
                            if (value == null) {
                                if (valueReference.isActive()) {
                                    final int count2 = this.count;
                                    ++this.modCount;
                                    final ReferenceEntry<K, V> removeValueFromChain = this.removeValueFromChain((ReferenceEntry<K, V>)referenceEntry, (ReferenceEntry<K, V>)next, key, count, valueReference, RemovalCause.COLLECTED);
                                    count = this.count;
                                    table.set(n, (ReferenceEntry)removeValueFromChain);
                                    this.count = count - 1;
                                }
                                return false;
                            }
                            if (!this.map.valueEquivalence.equivalent(v, value)) {
                                this.recordLockedRead((ReferenceEntry<K, V>)next, read);
                                return false;
                            }
                            break Label_0271;
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
            this.enqueueNotification(i, count, valueReference, RemovalCause.REPLACED);
            this.setValue((ReferenceEntry<K, V>)next, i, v2, read);
            this.evictEntries((ReferenceEntry<K, V>)next);
            this.unlock();
            this.postWriteCleanup();
            return true;
        }
        
        void runLockedCleanup(final long n) {
            if (this.tryLock()) {
                try {
                    this.drainReferenceQueues();
                    this.expireEntries(n);
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
        
        V scheduleRefresh(final ReferenceEntry<K, V> referenceEntry, final K k, final int n, final V v, final long n2, final CacheLoader<? super K, V> cacheLoader) {
            boolean b = false;
            if (this.map.refreshes()) {
                if (n2 - referenceEntry.getWriteTime() <= this.map.refreshNanos) {
                    b = true;
                }
                if (!b && !referenceEntry.getValueReference().isLoading()) {
                    final Object refresh = this.refresh(k, n, (CacheLoader<? super K, Object>)cacheLoader, true);
                    if (refresh != null) {
                        return (V)refresh;
                    }
                }
            }
            return v;
        }
        
        @GuardedBy("this")
        void setValue(final ReferenceEntry<K, V> referenceEntry, final K k, final V v, final long n) {
            boolean b = false;
            final ValueReference<K, V> valueReference = referenceEntry.getValueReference();
            final int weigh = this.map.weigher.weigh(k, v);
            if (weigh >= 0) {
                b = true;
            }
            Preconditions.checkState(b, (Object)"Weights must be non-negative");
            referenceEntry.setValueReference(this.map.valueStrength.referenceValue(this, referenceEntry, v, weigh));
            this.recordWrite(referenceEntry, weigh, n);
            valueReference.notifyNewValue(v);
        }
        
        boolean storeLoadedValue(final K k, final int n, final LoadingValueReference<K, V> loadingValueReference, final V v) {
            this.lock();
            try {
                final long read = this.map.ticker.read();
                this.preWriteCleanup(read);
                int n2 = this.count + 1;
                if (n2 > this.threshold) {
                    this.expand();
                    n2 = this.count + 1;
                }
                final AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
                final int n3 = n & table.length() - 1;
                Object next;
                final ReferenceEntry<Object, ?> referenceEntry = (ReferenceEntry<Object, ?>)(next = table.get(n3));
                while (next != null) {
                    final Object key = ((ReferenceEntry<Object, V>)next).getKey();
                    if (((ReferenceEntry)next).getHash() == n && key != null && this.map.keyEquivalence.equivalent(k, key)) {
                        final ValueReference<K, V> valueReference = ((ReferenceEntry<K, V>)next).getValueReference();
                        final V value = valueReference.get();
                        if (loadingValueReference != valueReference && (value != null || valueReference == LocalCache.UNSET)) {
                            this.enqueueNotification(k, n, new WeightedStrongValueReference<K, V>(v, 0), RemovalCause.REPLACED);
                            return false;
                        }
                        ++this.modCount;
                        if (loadingValueReference.isActive()) {
                            RemovalCause removalCause;
                            if (value != null) {
                                removalCause = RemovalCause.REPLACED;
                            }
                            else {
                                removalCause = RemovalCause.COLLECTED;
                            }
                            this.enqueueNotification(k, n, loadingValueReference, removalCause);
                            --n2;
                        }
                        this.setValue((ReferenceEntry<K, V>)next, k, v, read);
                        this.count = n2;
                        this.evictEntries((ReferenceEntry<K, V>)next);
                        return true;
                    }
                    else {
                        next = ((ReferenceEntry<K, V>)next).getNext();
                    }
                }
                ++this.modCount;
                final ReferenceEntry<K, V> entry = this.newEntry(k, n, (ReferenceEntry<K, V>)referenceEntry);
                this.setValue(entry, k, v, read);
                table.set(n3, (ReferenceEntry)entry);
                this.count = n2;
                this.evictEntries(entry);
                return true;
            }
            finally {
                this.unlock();
                this.postWriteCleanup();
            }
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
        
        void tryExpireEntries(final long n) {
            if (this.tryLock()) {
                try {
                    this.expireEntries(n);
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        V waitForLoadingValue(final ReferenceEntry<K, V> referenceEntry, final K obj, ValueReference<K, V> waitForValue) throws ExecutionException {
            Label_0073: {
                if (!((ValueReference)waitForValue).isLoading()) {
                    break Label_0073;
                }
                Label_0081: {
                    if (!Thread.holdsLock(referenceEntry)) {
                        break Label_0081;
                    }
                    boolean b = false;
                    while (true) {
                        Preconditions.checkState(b, "Recursive load of: %s", obj);
                        try {
                            waitForValue = ((ValueReference)waitForValue).waitForValue();
                            if (waitForValue != null) {
                                this.recordRead(referenceEntry, this.map.ticker.read());
                                return (V)waitForValue;
                            }
                            waitForValue = new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + obj + ".");
                            throw waitForValue;
                            throw new AssertionError();
                            b = true;
                        }
                        finally {
                            this.statsCounter.recordMisses(1);
                        }
                    }
                }
            }
        }
    }
    
    static class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        SoftValueReference(final ReferenceQueue<V> q, final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, q);
            this.entry = entry;
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
        public int getWeight() {
            return 1;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public boolean isLoading() {
            return false;
        }
        
        @Override
        public void notifyNewValue(final V v) {
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
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final V v, final int n) {
                SoftReference<V> softReference;
                if (n != 1) {
                    softReference = new WeightedSoftValueReference<Object, Object>((ReferenceQueue<Object>)segment.valueReferenceQueue, v, (ReferenceEntry<Object, Object>)referenceEntry, n);
                }
                else {
                    softReference = new SoftValueReference<Object, Object>((ReferenceQueue<V>)segment.valueReferenceQueue, (V)v, (ReferenceEntry<?, V>)referenceEntry);
                }
                return (ValueReference<K, V>)softReference;
            }
        }, 
        STRONG {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.equals();
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final V v, final int n) {
                Object o;
                if (n != 1) {
                    o = new WeightedStrongValueReference(v, n);
                }
                else {
                    o = new StrongValueReference(v);
                }
                return (ValueReference<K, V>)o;
            }
        }, 
        WEAK {
            @Override
            Equivalence<Object> defaultEquivalence() {
                return Equivalence.identity();
            }
            
            @Override
             <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> segment, final ReferenceEntry<K, V> referenceEntry, final V v, final int n) {
                WeakReference<V> weakReference;
                if (n != 1) {
                    weakReference = new WeightedWeakValueReference<Object, Object>((ReferenceQueue<Object>)segment.valueReferenceQueue, v, (ReferenceEntry<Object, Object>)referenceEntry, n);
                }
                else {
                    weakReference = new WeakValueReference<Object, Object>((ReferenceQueue<V>)segment.valueReferenceQueue, (V)v, (ReferenceEntry<?, V>)referenceEntry);
                }
                return (ValueReference<K, V>)weakReference;
            }
        };
        
        abstract Equivalence<Object> defaultEquivalence();
        
        abstract <K, V> ValueReference<K, V> referenceValue(final Segment<K, V> p0, final ReferenceEntry<K, V> p1, final V p2, final int p3);
    }
    
    static final class StrongAccessEntry<K, V> extends StrongEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> previousAccess;
        
        StrongAccessEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(k, n, referenceEntry);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = LocalCache.nullEntry();
            this.previousAccess = LocalCache.nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public void setAccessTime(final long accessTime) {
            this.accessTime = accessTime;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> nextAccess) {
            this.nextAccess = nextAccess;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previousAccess) {
            this.previousAccess = previousAccess;
        }
    }
    
    static final class StrongAccessWriteEntry<K, V> extends StrongEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousAccess;
        ReferenceEntry<K, V> previousWrite;
        volatile long writeTime;
        
        StrongAccessWriteEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(k, n, referenceEntry);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = LocalCache.nullEntry();
            this.previousAccess = LocalCache.nullEntry();
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = LocalCache.nullEntry();
            this.previousWrite = LocalCache.nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setAccessTime(final long accessTime) {
            this.accessTime = accessTime;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> nextAccess) {
            this.nextAccess = nextAccess;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> nextWrite) {
            this.nextWrite = nextWrite;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previousAccess) {
            this.previousAccess = previousAccess;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previousWrite) {
            this.previousWrite = previousWrite;
        }
        
        @Override
        public void setWriteTime(final long writeTime) {
            this.writeTime = writeTime;
        }
    }
    
    static class StrongEntry<K, V> extends AbstractReferenceEntry<K, V>
    {
        final int hash;
        final K key;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        StrongEntry(final K key, final int hash, @Nullable final ReferenceEntry<K, V> next) {
            this.valueReference = LocalCache.unset();
            this.key = key;
            this.hash = hash;
            this.next = next;
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
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
    }
    
    static class StrongValueReference<K, V> implements ValueReference<K, V>
    {
        final V referent;
        
        StrongValueReference(final V referent) {
            this.referent = referent;
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
        public int getWeight() {
            return 1;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public boolean isLoading() {
            return false;
        }
        
        @Override
        public void notifyNewValue(final V v) {
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    static final class StrongWriteEntry<K, V> extends StrongEntry<K, V>
    {
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousWrite;
        volatile long writeTime;
        
        StrongWriteEntry(final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(k, n, referenceEntry);
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = LocalCache.nullEntry();
            this.previousWrite = LocalCache.nullEntry();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> nextWrite) {
            this.nextWrite = nextWrite;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previousWrite) {
            this.previousWrite = previousWrite;
        }
        
        @Override
        public void setWriteTime(final long writeTime) {
            this.writeTime = writeTime;
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
        ValueReference<K, V> copyFor(final ReferenceQueue<V> p0, @Nullable final V p1, final ReferenceEntry<K, V> p2);
        
        @Nullable
        V get();
        
        @Nullable
        ReferenceEntry<K, V> getEntry();
        
        int getWeight();
        
        boolean isActive();
        
        boolean isLoading();
        
        void notifyNewValue(@Nullable final V p0);
        
        V waitForValue() throws ExecutionException;
    }
    
    final class Values extends AbstractCollection<V>
    {
        private final ConcurrentMap<?, ?> map;
        
        Values(final ConcurrentMap<?, ?> map) {
            this.map = map;
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map.containsValue(o);
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public int size() {
            return this.map.size();
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
    
    static final class WeakAccessEntry<K, V> extends WeakEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> previousAccess;
        
        WeakAccessEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = LocalCache.nullEntry();
            this.previousAccess = LocalCache.nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public void setAccessTime(final long accessTime) {
            this.accessTime = accessTime;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> nextAccess) {
            this.nextAccess = nextAccess;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previousAccess) {
            this.previousAccess = previousAccess;
        }
    }
    
    static final class WeakAccessWriteEntry<K, V> extends WeakEntry<K, V>
    {
        volatile long accessTime;
        ReferenceEntry<K, V> nextAccess;
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousAccess;
        ReferenceEntry<K, V> previousWrite;
        volatile long writeTime;
        
        WeakAccessWriteEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.accessTime = Long.MAX_VALUE;
            this.nextAccess = LocalCache.nullEntry();
            this.previousAccess = LocalCache.nullEntry();
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = LocalCache.nullEntry();
            this.previousWrite = LocalCache.nullEntry();
        }
        
        @Override
        public long getAccessTime() {
            return this.accessTime;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            return this.nextAccess;
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            return this.previousAccess;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setAccessTime(final long accessTime) {
            this.accessTime = accessTime;
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> nextAccess) {
            this.nextAccess = nextAccess;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> nextWrite) {
            this.nextWrite = nextWrite;
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> previousAccess) {
            this.previousAccess = previousAccess;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previousWrite) {
            this.previousWrite = previousWrite;
        }
        
        @Override
        public void setWriteTime(final long writeTime) {
            this.writeTime = writeTime;
        }
    }
    
    static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V>
    {
        final int hash;
        final ReferenceEntry<K, V> next;
        volatile ValueReference<K, V> valueReference;
        
        WeakEntry(final ReferenceQueue<K> q, final K referent, final int hash, @Nullable final ReferenceEntry<K, V> next) {
            super(referent, q);
            this.valueReference = LocalCache.unset();
            this.hash = hash;
            this.next = next;
        }
        
        @Override
        public long getAccessTime() {
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
        public ReferenceEntry<K, V> getNextInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public ValueReference<K, V> getValueReference() {
            return this.valueReference;
        }
        
        @Override
        public long getWriteTime() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setAccessTime(final long n) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInAccessQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInAccessQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> referenceEntry) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public void setValueReference(final ValueReference<K, V> valueReference) {
            this.valueReference = valueReference;
        }
        
        @Override
        public void setWriteTime(final long n) {
            throw new UnsupportedOperationException();
        }
    }
    
    static class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V>
    {
        final ReferenceEntry<K, V> entry;
        
        WeakValueReference(final ReferenceQueue<V> q, final V referent, final ReferenceEntry<K, V> entry) {
            super(referent, q);
            this.entry = entry;
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
        public int getWeight() {
            return 1;
        }
        
        @Override
        public boolean isActive() {
            return true;
        }
        
        @Override
        public boolean isLoading() {
            return false;
        }
        
        @Override
        public void notifyNewValue(final V v) {
        }
        
        @Override
        public V waitForValue() {
            return this.get();
        }
    }
    
    static final class WeakWriteEntry<K, V> extends WeakEntry<K, V>
    {
        ReferenceEntry<K, V> nextWrite;
        ReferenceEntry<K, V> previousWrite;
        volatile long writeTime;
        
        WeakWriteEntry(final ReferenceQueue<K> referenceQueue, final K k, final int n, @Nullable final ReferenceEntry<K, V> referenceEntry) {
            super(referenceQueue, k, n, referenceEntry);
            this.writeTime = Long.MAX_VALUE;
            this.nextWrite = LocalCache.nullEntry();
            this.previousWrite = LocalCache.nullEntry();
        }
        
        @Override
        public ReferenceEntry<K, V> getNextInWriteQueue() {
            return this.nextWrite;
        }
        
        @Override
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
            return this.previousWrite;
        }
        
        @Override
        public long getWriteTime() {
            return this.writeTime;
        }
        
        @Override
        public void setNextInWriteQueue(final ReferenceEntry<K, V> nextWrite) {
            this.nextWrite = nextWrite;
        }
        
        @Override
        public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previousWrite) {
            this.previousWrite = previousWrite;
        }
        
        @Override
        public void setWriteTime(final long writeTime) {
            this.writeTime = writeTime;
        }
    }
    
    static final class WeightedSoftValueReference<K, V> extends SoftValueReference<K, V>
    {
        final int weight;
        
        WeightedSoftValueReference(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry, final int weight) {
            super(referenceQueue, v, referenceEntry);
            this.weight = weight;
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return new WeightedSoftValueReference((ReferenceQueue<Object>)referenceQueue, v, (ReferenceEntry<Object, Object>)referenceEntry, this.weight);
        }
        
        @Override
        public int getWeight() {
            return this.weight;
        }
    }
    
    static final class WeightedStrongValueReference<K, V> extends StrongValueReference<K, V>
    {
        final int weight;
        
        WeightedStrongValueReference(final V v, final int weight) {
            super(v);
            this.weight = weight;
        }
        
        @Override
        public int getWeight() {
            return this.weight;
        }
    }
    
    static final class WeightedWeakValueReference<K, V> extends WeakValueReference<K, V>
    {
        final int weight;
        
        WeightedWeakValueReference(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry, final int weight) {
            super(referenceQueue, v, referenceEntry);
            this.weight = weight;
        }
        
        @Override
        public ValueReference<K, V> copyFor(final ReferenceQueue<V> referenceQueue, final V v, final ReferenceEntry<K, V> referenceEntry) {
            return new WeightedWeakValueReference((ReferenceQueue<Object>)referenceQueue, v, (ReferenceEntry<Object, Object>)referenceEntry, this.weight);
        }
        
        @Override
        public int getWeight() {
            return this.weight;
        }
    }
    
    static final class WriteQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>>
    {
        final ReferenceEntry<K, V> head;
        
        WriteQueue() {
            this.head = new AbstractReferenceEntry<K, V>() {
                ReferenceEntry<K, V> nextWrite = this;
                ReferenceEntry<K, V> previousWrite = this;
                
                @Override
                public ReferenceEntry<K, V> getNextInWriteQueue() {
                    return this.nextWrite;
                }
                
                @Override
                public ReferenceEntry<K, V> getPreviousInWriteQueue() {
                    return this.previousWrite;
                }
                
                @Override
                public long getWriteTime() {
                    return Long.MAX_VALUE;
                }
                
                @Override
                public void setNextInWriteQueue(final ReferenceEntry<K, V> nextWrite) {
                    this.nextWrite = nextWrite;
                }
                
                @Override
                public void setPreviousInWriteQueue(final ReferenceEntry<K, V> previousWrite) {
                    this.previousWrite = previousWrite;
                }
                
                @Override
                public void setWriteTime(final long n) {
                }
            };
        }
        
        @Override
        public void clear() {
            ReferenceEntry<K, V> nextInWriteQueue2;
            for (ReferenceEntry<K, V> nextInWriteQueue = this.head.getNextInWriteQueue(); nextInWriteQueue != this.head; nextInWriteQueue = nextInWriteQueue2) {
                nextInWriteQueue2 = nextInWriteQueue.getNextInWriteQueue();
                LocalCache.nullifyWriteOrder(nextInWriteQueue);
            }
            this.head.setNextInWriteQueue(this.head);
            this.head.setPreviousInWriteQueue(this.head);
        }
        
        @Override
        public boolean contains(final Object o) {
            return ((ReferenceEntry)o).getNextInWriteQueue() != NullEntry.INSTANCE;
        }
        
        @Override
        public boolean isEmpty() {
            return this.head.getNextInWriteQueue() == this.head;
        }
        
        @Override
        public Iterator<ReferenceEntry<K, V>> iterator() {
            return new AbstractSequentialIterator<ReferenceEntry<K, V>>(this.peek()) {
                @Override
                protected ReferenceEntry<K, V> computeNext(final ReferenceEntry<K, V> referenceEntry) {
                    ReferenceEntry<K, V> nextInWriteQueue = referenceEntry.getNextInWriteQueue();
                    if (nextInWriteQueue == WriteQueue.this.head) {
                        nextInWriteQueue = null;
                    }
                    return nextInWriteQueue;
                }
            };
        }
        
        @Override
        public boolean offer(final ReferenceEntry<K, V> referenceEntry) {
            LocalCache.connectWriteOrder(referenceEntry.getPreviousInWriteQueue(), referenceEntry.getNextInWriteQueue());
            LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), referenceEntry);
            LocalCache.connectWriteOrder(referenceEntry, this.head);
            return true;
        }
        
        @Override
        public ReferenceEntry<K, V> peek() {
            ReferenceEntry<K, V> nextInWriteQueue = this.head.getNextInWriteQueue();
            if (nextInWriteQueue == this.head) {
                nextInWriteQueue = null;
            }
            return nextInWriteQueue;
        }
        
        @Override
        public ReferenceEntry<K, V> poll() {
            final ReferenceEntry<K, V> nextInWriteQueue = this.head.getNextInWriteQueue();
            if (nextInWriteQueue != this.head) {
                this.remove(nextInWriteQueue);
                return nextInWriteQueue;
            }
            return null;
        }
        
        @Override
        public boolean remove(final Object o) {
            final ReferenceEntry referenceEntry = (ReferenceEntry)o;
            final ReferenceEntry previousInWriteQueue = referenceEntry.getPreviousInWriteQueue();
            final ReferenceEntry nextInWriteQueue = referenceEntry.getNextInWriteQueue();
            LocalCache.connectWriteOrder(previousInWriteQueue, (ReferenceEntry<Object, Object>)nextInWriteQueue);
            LocalCache.nullifyWriteOrder((ReferenceEntry<Object, Object>)referenceEntry);
            return nextInWriteQueue != NullEntry.INSTANCE;
        }
        
        @Override
        public int size() {
            int n = 0;
            for (ReferenceEntry<K, V> referenceEntry = this.head.getNextInWriteQueue(); referenceEntry != this.head; referenceEntry = referenceEntry.getNextInWriteQueue()) {
                ++n;
            }
            return n;
        }
    }
    
    final class WriteThroughEntry implements Entry<K, V>
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
        public V setValue(final V v) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
