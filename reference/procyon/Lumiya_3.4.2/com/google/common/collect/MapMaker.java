// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.concurrent.ExecutionException;
import com.google.common.base.Throwables;
import com.google.common.base.Ascii;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import java.util.concurrent.ConcurrentMap;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Ticker;
import com.google.common.base.Equivalence;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class MapMaker extends GenericMapMaker<Object, Object>
{
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    private static final int DEFAULT_EXPIRATION_NANOS = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    static final int UNSET_INT = -1;
    int concurrencyLevel;
    long expireAfterAccessNanos;
    long expireAfterWriteNanos;
    int initialCapacity;
    Equivalence<Object> keyEquivalence;
    MapMakerInternalMap.Strength keyStrength;
    int maximumSize;
    RemovalCause nullRemovalCause;
    Ticker ticker;
    boolean useCustomMap;
    MapMakerInternalMap.Strength valueStrength;
    
    public MapMaker() {
        this.initialCapacity = -1;
        this.concurrencyLevel = -1;
        this.maximumSize = -1;
        this.expireAfterWriteNanos = -1L;
        this.expireAfterAccessNanos = -1L;
    }
    
    private void checkExpiration(final long l, final TimeUnit timeUnit) {
        Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", this.expireAfterWriteNanos);
        Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", this.expireAfterAccessNanos);
        boolean b;
        if (l < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "duration cannot be negative: %s %s", l, timeUnit);
    }
    
    @Override
    public MapMaker concurrencyLevel(final int concurrencyLevel) {
        final boolean b = false;
        Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", this.concurrencyLevel);
        Preconditions.checkArgument(concurrencyLevel > 0 || b);
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }
    
    @Deprecated
    @GwtIncompatible("To be supported")
    @Override
    MapMaker expireAfterAccess(final long duration, final TimeUnit timeUnit) {
        this.checkExpiration(duration, timeUnit);
        this.expireAfterAccessNanos = timeUnit.toNanos(duration);
        if (duration == 0L && this.nullRemovalCause == null) {
            this.nullRemovalCause = RemovalCause.EXPIRED;
        }
        this.useCustomMap = true;
        return this;
    }
    
    @Deprecated
    @Override
    MapMaker expireAfterWrite(final long duration, final TimeUnit timeUnit) {
        this.checkExpiration(duration, timeUnit);
        this.expireAfterWriteNanos = timeUnit.toNanos(duration);
        if (duration == 0L && this.nullRemovalCause == null) {
            this.nullRemovalCause = RemovalCause.EXPIRED;
        }
        this.useCustomMap = true;
        return this;
    }
    
    int getConcurrencyLevel() {
        int concurrencyLevel;
        if (this.concurrencyLevel != -1) {
            concurrencyLevel = this.concurrencyLevel;
        }
        else {
            concurrencyLevel = 4;
        }
        return concurrencyLevel;
    }
    
    long getExpireAfterAccessNanos() {
        long expireAfterAccessNanos;
        if (this.expireAfterAccessNanos == -1L) {
            expireAfterAccessNanos = 0L;
        }
        else {
            expireAfterAccessNanos = this.expireAfterAccessNanos;
        }
        return expireAfterAccessNanos;
    }
    
    long getExpireAfterWriteNanos() {
        long expireAfterWriteNanos;
        if (this.expireAfterWriteNanos == -1L) {
            expireAfterWriteNanos = 0L;
        }
        else {
            expireAfterWriteNanos = this.expireAfterWriteNanos;
        }
        return expireAfterWriteNanos;
    }
    
    int getInitialCapacity() {
        int initialCapacity;
        if (this.initialCapacity != -1) {
            initialCapacity = this.initialCapacity;
        }
        else {
            initialCapacity = 16;
        }
        return initialCapacity;
    }
    
    Equivalence<Object> getKeyEquivalence() {
        return MoreObjects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
    }
    
    MapMakerInternalMap.Strength getKeyStrength() {
        return MoreObjects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
    }
    
    Ticker getTicker() {
        return MoreObjects.firstNonNull(this.ticker, Ticker.systemTicker());
    }
    
    MapMakerInternalMap.Strength getValueStrength() {
        return MoreObjects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
    }
    
    @Override
    public MapMaker initialCapacity(final int initialCapacity) {
        final boolean b = false;
        Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", this.initialCapacity);
        Preconditions.checkArgument(initialCapacity >= 0 || b);
        this.initialCapacity = initialCapacity;
        return this;
    }
    
    @GwtIncompatible("To be supported")
    @Override
    MapMaker keyEquivalence(final Equivalence<Object> equivalence) {
        Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", this.keyEquivalence);
        this.keyEquivalence = Preconditions.checkNotNull(equivalence);
        this.useCustomMap = true;
        return this;
    }
    
    @Deprecated
    @Override
     <K, V> ConcurrentMap<K, V> makeComputingMap(final Function<? super K, ? extends V> function) {
        Serializable s;
        if (this.nullRemovalCause != null) {
            s = new NullComputingConcurrentMap<Object, Object>(this, function);
        }
        else {
            s = new ComputingMapAdapter<Object, Object>(this, function);
        }
        return (ConcurrentMap<K, V>)s;
    }
    
    @GwtIncompatible("MapMakerInternalMap")
    @Override
     <K, V> MapMakerInternalMap<K, V> makeCustomMap() {
        return new MapMakerInternalMap<K, V>(this);
    }
    
    @Override
    public <K, V> ConcurrentMap<K, V> makeMap() {
        if (this.useCustomMap) {
            Serializable s;
            if (this.nullRemovalCause != null) {
                s = new NullConcurrentMap<Object, Object>(this);
            }
            else {
                s = new MapMakerInternalMap<Object, Object>(this);
            }
            return (ConcurrentMap<K, V>)s;
        }
        return new ConcurrentHashMap<K, V>(this.getInitialCapacity(), 0.75f, this.getConcurrencyLevel());
    }
    
    @Deprecated
    @Override
    MapMaker maximumSize(final int maximumSize) {
        final boolean b = false;
        Preconditions.checkState(this.maximumSize == -1, "maximum size was already set to %s", this.maximumSize);
        Preconditions.checkArgument(maximumSize >= 0 || b, (Object)"maximum size must not be negative");
        this.maximumSize = maximumSize;
        this.useCustomMap = true;
        if (this.maximumSize == 0) {
            this.nullRemovalCause = RemovalCause.SIZE;
        }
        return this;
    }
    
    @Deprecated
    @GwtIncompatible("To be supported")
     <K, V> GenericMapMaker<K, V> removalListener(final RemovalListener<K, V> removalListener) {
        Preconditions.checkState(this.removalListener == null);
        super.removalListener = Preconditions.checkNotNull(removalListener);
        this.useCustomMap = true;
        return (GenericMapMaker<K, V>)this;
    }
    
    MapMaker setKeyStrength(final MapMakerInternalMap.Strength strength) {
        final boolean b = false;
        Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", this.keyStrength);
        this.keyStrength = Preconditions.checkNotNull(strength);
        Preconditions.checkArgument(this.keyStrength != MapMakerInternalMap.Strength.SOFT || b, (Object)"Soft keys are not supported");
        if (strength != MapMakerInternalMap.Strength.STRONG) {
            this.useCustomMap = true;
        }
        return this;
    }
    
    MapMaker setValueStrength(final MapMakerInternalMap.Strength strength) {
        Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", this.valueStrength);
        this.valueStrength = Preconditions.checkNotNull(strength);
        if (strength != MapMakerInternalMap.Strength.STRONG) {
            this.useCustomMap = true;
        }
        return this;
    }
    
    @Deprecated
    @GwtIncompatible("java.lang.ref.SoftReference")
    @Override
    MapMaker softValues() {
        return this.setValueStrength(MapMakerInternalMap.Strength.SOFT);
    }
    
    @Override
    public String toString() {
        final MoreObjects.ToStringHelper stringHelper = MoreObjects.toStringHelper(this);
        if (this.initialCapacity != -1) {
            stringHelper.add("initialCapacity", this.initialCapacity);
        }
        if (this.concurrencyLevel != -1) {
            stringHelper.add("concurrencyLevel", this.concurrencyLevel);
        }
        if (this.maximumSize != -1) {
            stringHelper.add("maximumSize", this.maximumSize);
        }
        if (this.expireAfterWriteNanos != -1L) {
            stringHelper.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
        }
        if (this.expireAfterAccessNanos != -1L) {
            stringHelper.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
        }
        if (this.keyStrength != null) {
            stringHelper.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
        }
        if (this.valueStrength != null) {
            stringHelper.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
        }
        if (this.keyEquivalence != null) {
            stringHelper.addValue("keyEquivalence");
        }
        if (this.removalListener != null) {
            stringHelper.addValue("removalListener");
        }
        return stringHelper.toString();
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    @Override
    public MapMaker weakKeys() {
        return this.setKeyStrength(MapMakerInternalMap.Strength.WEAK);
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    @Override
    public MapMaker weakValues() {
        return this.setValueStrength(MapMakerInternalMap.Strength.WEAK);
    }
    
    static final class ComputingMapAdapter<K, V> extends ComputingConcurrentHashMap<K, V> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        
        ComputingMapAdapter(final MapMaker mapMaker, final Function<? super K, ? extends V> function) {
            super(mapMaker, function);
        }
        
        @Override
        public V get(final Object obj) {
            try {
                final V orCompute = this.getOrCompute((K)obj);
                if (orCompute != null) {
                    return orCompute;
                }
            }
            catch (final ExecutionException ex) {
                final Throwable cause = ex.getCause();
                Throwables.propagateIfInstanceOf(cause, ComputationException.class);
                throw new ComputationException(cause);
            }
            throw new NullPointerException(this.computingFunction + " returned null for key " + obj + ".");
        }
    }
    
    static final class NullComputingConcurrentMap<K, V> extends NullConcurrentMap<K, V>
    {
        private static final long serialVersionUID = 0L;
        final Function<? super K, ? extends V> computingFunction;
        
        NullComputingConcurrentMap(final MapMaker mapMaker, final Function<? super K, ? extends V> function) {
            super(mapMaker);
            this.computingFunction = Preconditions.checkNotNull(function);
        }
        
        private V compute(final K k) {
            Preconditions.checkNotNull(k);
            try {
                return (V)this.computingFunction.apply(k);
            }
            catch (final ComputationException ex) {
                throw ex;
            }
            catch (final Throwable t) {
                throw new ComputationException(t);
            }
        }
        
        @Override
        public V get(final Object o) {
            final V compute = this.compute(o);
            Preconditions.checkNotNull(compute, "%s returned null for key %s.", this.computingFunction, o);
            this.notifyRemoval((K)o, compute);
            return compute;
        }
    }
    
    static class NullConcurrentMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable
    {
        private static final long serialVersionUID = 0L;
        private final RemovalCause removalCause;
        private final RemovalListener<K, V> removalListener;
        
        NullConcurrentMap(final MapMaker mapMaker) {
            this.removalListener = mapMaker.getRemovalListener();
            this.removalCause = mapMaker.nullRemovalCause;
        }
        
        @Override
        public boolean containsKey(@Nullable final Object o) {
            return false;
        }
        
        @Override
        public boolean containsValue(@Nullable final Object o) {
            return false;
        }
        
        @Override
        public Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
        
        @Override
        public V get(@Nullable final Object o) {
            return null;
        }
        
        void notifyRemoval(final K k, final V v) {
            this.removalListener.onRemoval((RemovalNotification<K, V>)new RemovalNotification(k, v, this.removalCause));
        }
        
        @Override
        public V put(final K k, final V v) {
            Preconditions.checkNotNull(k);
            Preconditions.checkNotNull(v);
            this.notifyRemoval(k, v);
            return null;
        }
        
        @Override
        public V putIfAbsent(final K k, final V v) {
            return this.put(k, v);
        }
        
        @Override
        public V remove(@Nullable final Object o) {
            return null;
        }
        
        @Override
        public boolean remove(@Nullable final Object o, @Nullable final Object o2) {
            return false;
        }
        
        @Override
        public V replace(final K k, final V v) {
            Preconditions.checkNotNull(k);
            Preconditions.checkNotNull(v);
            return null;
        }
        
        @Override
        public boolean replace(final K k, @Nullable final V v, final V v2) {
            Preconditions.checkNotNull(k);
            Preconditions.checkNotNull(v2);
            return false;
        }
    }
    
    enum RemovalCause
    {
        COLLECTED {
            @Override
            boolean wasEvicted() {
                return true;
            }
        }, 
        EXPIRED {
            @Override
            boolean wasEvicted() {
                return true;
            }
        }, 
        EXPLICIT {
            @Override
            boolean wasEvicted() {
                return false;
            }
        }, 
        REPLACED {
            @Override
            boolean wasEvicted() {
                return false;
            }
        }, 
        SIZE {
            @Override
            boolean wasEvicted() {
                return true;
            }
        };
        
        abstract boolean wasEvicted();
    }
    
    interface RemovalListener<K, V>
    {
        void onRemoval(final RemovalNotification<K, V> p0);
    }
    
    static final class RemovalNotification<K, V> extends ImmutableEntry<K, V>
    {
        private static final long serialVersionUID = 0L;
        private final RemovalCause cause;
        
        RemovalNotification(@Nullable final K k, @Nullable final V v, final RemovalCause cause) {
            super(k, v);
            this.cause = cause;
        }
        
        public RemovalCause getCause() {
            return this.cause;
        }
        
        public boolean wasEvicted() {
            return this.cause.wasEvicted();
        }
    }
}
