// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class AbstractCache<K, V> implements Cache<K, V>
{
    protected AbstractCache() {
    }
    
    @Override
    public ConcurrentMap<K, V> asMap() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void cleanUp() {
    }
    
    @Override
    public V get(final K k, final Callable<? extends V> callable) throws ExecutionException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableMap<K, V> getAllPresent(final Iterable<?> iterable) {
        final LinkedHashMap<Object, Object> linkedHashMap = Maps.newLinkedHashMap();
        for (final Object next : iterable) {
            if (!linkedHashMap.containsKey(next)) {
                final V ifPresent = this.getIfPresent(next);
                if (ifPresent == null) {
                    continue;
                }
                linkedHashMap.put(next, ifPresent);
            }
        }
        return ImmutableMap.copyOf((Map<? extends K, ? extends V>)linkedHashMap);
    }
    
    @Override
    public void invalidate(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void invalidateAll() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void invalidateAll(final Iterable<?> iterable) {
        final Iterator<?> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            this.invalidate(iterator.next());
        }
    }
    
    @Override
    public void put(final K k, final V v) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    @Override
    public long size() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public CacheStats stats() {
        throw new UnsupportedOperationException();
    }
    
    public static final class SimpleStatsCounter implements StatsCounter
    {
        private final LongAddable evictionCount;
        private final LongAddable hitCount;
        private final LongAddable loadExceptionCount;
        private final LongAddable loadSuccessCount;
        private final LongAddable missCount;
        private final LongAddable totalLoadTime;
        
        public SimpleStatsCounter() {
            this.hitCount = LongAddables.create();
            this.missCount = LongAddables.create();
            this.loadSuccessCount = LongAddables.create();
            this.loadExceptionCount = LongAddables.create();
            this.totalLoadTime = LongAddables.create();
            this.evictionCount = LongAddables.create();
        }
        
        public void incrementBy(final StatsCounter statsCounter) {
            final CacheStats snapshot = statsCounter.snapshot();
            this.hitCount.add(snapshot.hitCount());
            this.missCount.add(snapshot.missCount());
            this.loadSuccessCount.add(snapshot.loadSuccessCount());
            this.loadExceptionCount.add(snapshot.loadExceptionCount());
            this.totalLoadTime.add(snapshot.totalLoadTime());
            this.evictionCount.add(snapshot.evictionCount());
        }
        
        @Override
        public void recordEviction() {
            this.evictionCount.increment();
        }
        
        @Override
        public void recordHits(final int n) {
            this.hitCount.add(n);
        }
        
        @Override
        public void recordLoadException(final long n) {
            this.loadExceptionCount.increment();
            this.totalLoadTime.add(n);
        }
        
        @Override
        public void recordLoadSuccess(final long n) {
            this.loadSuccessCount.increment();
            this.totalLoadTime.add(n);
        }
        
        @Override
        public void recordMisses(final int n) {
            this.missCount.add(n);
        }
        
        @Override
        public CacheStats snapshot() {
            return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadExceptionCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum());
        }
    }
    
    public interface StatsCounter
    {
        void recordEviction();
        
        void recordHits(final int p0);
        
        void recordLoadException(final long p0);
        
        void recordLoadSuccess(final long p0);
        
        void recordMisses(final int p0);
        
        CacheStats snapshot();
    }
}
