// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.Iterator;
import java.util.Collections;
import com.google.common.collect.Maps;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class AtomicLongMap<K>
{
    private transient Map<K, Long> asMap;
    private final ConcurrentHashMap<K, AtomicLong> map;
    
    private AtomicLongMap(final ConcurrentHashMap<K, AtomicLong> concurrentHashMap) {
        this.map = Preconditions.checkNotNull(concurrentHashMap);
    }
    
    public static <K> AtomicLongMap<K> create() {
        return new AtomicLongMap<K>(new ConcurrentHashMap<K, AtomicLong>());
    }
    
    public static <K> AtomicLongMap<K> create(final Map<? extends K, ? extends Long> map) {
        final AtomicLongMap<Object> create = create();
        create.putAll(map);
        return (AtomicLongMap<K>)create;
    }
    
    private Map<K, Long> createAsMap() {
        return Collections.unmodifiableMap((Map<? extends K, ? extends Long>)Maps.transformValues((Map<? extends K, AtomicLong>)this.map, (Function<? super AtomicLong, ? extends V>)new Function<AtomicLong, Long>() {
            @Override
            public Long apply(final AtomicLong atomicLong) {
                return atomicLong.get();
            }
        }));
    }
    
    public long addAndGet(final K key, final long n) {
    Label_0000:
        while (true) {
            AtomicLong oldValue = this.map.get(key);
            if (oldValue == null && (oldValue = this.map.putIfAbsent(key, new AtomicLong(n))) == null) {
                return n;
            }
            long value;
            long newValue;
            do {
                value = oldValue.get();
                if (value == 0L) {
                    if (this.map.replace(key, oldValue, new AtomicLong(n))) {
                        return n;
                    }
                    continue Label_0000;
                }
                else {
                    newValue = value + n;
                }
            } while (!oldValue.compareAndSet(value, newValue));
            return newValue;
        }
    }
    
    public Map<K, Long> asMap() {
        Map<K, Long> asMap = this.asMap;
        if (asMap == null) {
            asMap = this.createAsMap();
            this.asMap = asMap;
        }
        return asMap;
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public boolean containsKey(final Object key) {
        return this.map.containsKey(key);
    }
    
    public long decrementAndGet(final K k) {
        return this.addAndGet(k, -1L);
    }
    
    public long get(final K key) {
        final AtomicLong atomicLong = this.map.get(key);
        long value;
        if (atomicLong != null) {
            value = atomicLong.get();
        }
        else {
            value = 0L;
        }
        return value;
    }
    
    public long getAndAdd(final K key, final long n) {
    Label_0000:
        while (true) {
            AtomicLong oldValue = this.map.get(key);
            if (oldValue == null && (oldValue = this.map.putIfAbsent(key, new AtomicLong(n))) == null) {
                return 0L;
            }
            long value;
            do {
                value = oldValue.get();
                if (value == 0L) {
                    if (this.map.replace(key, oldValue, new AtomicLong(n))) {
                        return 0L;
                    }
                    continue Label_0000;
                }
            } while (!oldValue.compareAndSet(value, value + n));
            return value;
        }
    }
    
    public long getAndDecrement(final K k) {
        return this.getAndAdd(k, -1L);
    }
    
    public long getAndIncrement(final K k) {
        return this.getAndAdd(k, 1L);
    }
    
    public long incrementAndGet(final K k) {
        return this.addAndGet(k, 1L);
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public long put(final K key, final long initialValue) {
    Label_0000:
        while (true) {
            AtomicLong oldValue = this.map.get(key);
            if (oldValue == null && (oldValue = this.map.putIfAbsent(key, new AtomicLong(initialValue))) == null) {
                return 0L;
            }
            long value;
            do {
                value = oldValue.get();
                if (value == 0L) {
                    if (this.map.replace(key, oldValue, new AtomicLong(initialValue))) {
                        return 0L;
                    }
                    continue Label_0000;
                }
            } while (!oldValue.compareAndSet(value, initialValue));
            return value;
        }
    }
    
    public void putAll(final Map<? extends K, ? extends Long> map) {
        for (final Map.Entry<K, V> entry : map.entrySet()) {
            this.put(entry.getKey(), (long)entry.getValue());
        }
    }
    
    long putIfAbsent(final K key, final long n) {
        AtomicLong oldValue;
        do {
            oldValue = this.map.get(key);
            if (oldValue == null && (oldValue = this.map.putIfAbsent(key, new AtomicLong(n))) == null) {
                return 0L;
            }
            final long value = oldValue.get();
            if (value == 0L) {
                continue;
            }
            return value;
        } while (!this.map.replace(key, oldValue, new AtomicLong(n)));
        return 0L;
    }
    
    public long remove(final K k) {
        final AtomicLong value = this.map.get(k);
        if (value != null) {
            long value2;
            do {
                value2 = value.get();
            } while (value2 != 0L && !value.compareAndSet(value2, 0L));
            this.map.remove(k, value);
            return value2;
        }
        return 0L;
    }
    
    boolean remove(final K k, final long n) {
        final AtomicLong value = this.map.get(k);
        if (value == null) {
            return false;
        }
        final long value2 = value.get();
        if (value2 != n) {
            return false;
        }
        if (value2 != 0L && !value.compareAndSet(value2, 0L)) {
            return false;
        }
        this.map.remove(k, value);
        return true;
    }
    
    public void removeAllZeros() {
        final Iterator<Map.Entry<K, AtomicLong>> iterator = this.map.entrySet().iterator();
        while (iterator.hasNext()) {
            final AtomicLong atomicLong = ((Map.Entry<K, AtomicLong>)iterator.next()).getValue();
            if (atomicLong != null && atomicLong.get() == 0L) {
                iterator.remove();
            }
        }
    }
    
    boolean replace(final K key, final long expectedValue, final long newValue) {
        boolean compareAndSet = false;
        if (expectedValue == 0L) {
            return this.putIfAbsent(key, newValue) == 0L;
        }
        final AtomicLong atomicLong = this.map.get(key);
        if (atomicLong != null) {
            compareAndSet = atomicLong.compareAndSet(expectedValue, newValue);
        }
        return compareAndSet;
    }
    
    public int size() {
        return this.map.size();
    }
    
    public long sum() {
        final Iterator<AtomicLong> iterator = this.map.values().iterator();
        long n = 0L;
        while (iterator.hasNext()) {
            n += iterator.next().get();
        }
        return n;
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
}
