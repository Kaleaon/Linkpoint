// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.base.Ascii;
import javax.annotation.CheckReturnValue;
import com.google.common.base.MoreObjects;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.GwtIncompatible;
import java.util.logging.Level;
import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.base.Equivalence;
import java.util.logging.Logger;
import com.google.common.base.Ticker;
import com.google.common.base.Supplier;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class CacheBuilder<K, V>
{
    static final Supplier<AbstractCache.StatsCounter> CACHE_STATS_COUNTER;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    private static final int DEFAULT_EXPIRATION_NANOS = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_REFRESH_NANOS = 0;
    static final CacheStats EMPTY_STATS;
    static final Supplier<? extends AbstractCache.StatsCounter> NULL_STATS_COUNTER;
    static final Ticker NULL_TICKER;
    static final int UNSET_INT = -1;
    private static final Logger logger;
    int concurrencyLevel;
    long expireAfterAccessNanos;
    long expireAfterWriteNanos;
    int initialCapacity;
    Equivalence<Object> keyEquivalence;
    LocalCache.Strength keyStrength;
    long maximumSize;
    long maximumWeight;
    long refreshNanos;
    RemovalListener<? super K, ? super V> removalListener;
    Supplier<? extends AbstractCache.StatsCounter> statsCounterSupplier;
    boolean strictParsing;
    Ticker ticker;
    Equivalence<Object> valueEquivalence;
    LocalCache.Strength valueStrength;
    Weigher<? super K, ? super V> weigher;
    
    static {
        NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter() {
            @Override
            public void recordEviction() {
            }
            
            @Override
            public void recordHits(final int n) {
            }
            
            @Override
            public void recordLoadException(final long n) {
            }
            
            @Override
            public void recordLoadSuccess(final long n) {
            }
            
            @Override
            public void recordMisses(final int n) {
            }
            
            @Override
            public CacheStats snapshot() {
                return CacheBuilder.EMPTY_STATS;
            }
        });
        EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L);
        CACHE_STATS_COUNTER = new Supplier<AbstractCache.StatsCounter>() {
            @Override
            public AbstractCache.StatsCounter get() {
                return new AbstractCache.SimpleStatsCounter();
            }
        };
        NULL_TICKER = new Ticker() {
            @Override
            public long read() {
                return 0L;
            }
        };
        logger = Logger.getLogger(CacheBuilder.class.getName());
    }
    
    CacheBuilder() {
        this.strictParsing = true;
        this.initialCapacity = -1;
        this.concurrencyLevel = -1;
        this.maximumSize = -1L;
        this.maximumWeight = -1L;
        this.expireAfterWriteNanos = -1L;
        this.expireAfterAccessNanos = -1L;
        this.refreshNanos = -1L;
        this.statsCounterSupplier = CacheBuilder.NULL_STATS_COUNTER;
    }
    
    private void checkNonLoadingCache() {
        Preconditions.checkState(this.refreshNanos == -1L, (Object)"refreshAfterWrite requires a LoadingCache");
    }
    
    private void checkWeightWithWeigher() {
        final boolean b = true;
        boolean b2 = true;
        if (this.weigher != null) {
            if (!this.strictParsing) {
                if (this.maximumWeight == -1L) {
                    CacheBuilder.logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
                }
            }
            else {
                Preconditions.checkState(this.maximumWeight != -1L && b, (Object)"weigher requires maximumWeight");
            }
        }
        else {
            if (this.maximumWeight != -1L) {
                b2 = false;
            }
            Preconditions.checkState(b2, (Object)"maximumWeight requires weigher");
        }
    }
    
    @GwtIncompatible("To be supported")
    public static CacheBuilder<Object, Object> from(final CacheBuilderSpec cacheBuilderSpec) {
        return cacheBuilderSpec.toCacheBuilder().lenientParsing();
    }
    
    @GwtIncompatible("To be supported")
    public static CacheBuilder<Object, Object> from(final String s) {
        return from(CacheBuilderSpec.parse(s));
    }
    
    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<Object, Object>();
    }
    
    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        this.checkWeightWithWeigher();
        this.checkNonLoadingCache();
        return new LocalCache.LocalManualCache<K1, V1>(this);
    }
    
    public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(final CacheLoader<? super K1, V1> cacheLoader) {
        this.checkWeightWithWeigher();
        return new LocalCache.LocalLoadingCache<K1, V1>(this, cacheLoader);
    }
    
    public CacheBuilder<K, V> concurrencyLevel(final int concurrencyLevel) {
        final boolean b = false;
        Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", this.concurrencyLevel);
        Preconditions.checkArgument(concurrencyLevel > 0 || b);
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }
    
    public CacheBuilder<K, V> expireAfterAccess(final long n, final TimeUnit timeUnit) {
        Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", this.expireAfterAccessNanos);
        boolean b;
        if (n < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "duration cannot be negative: %s %s", n, timeUnit);
        this.expireAfterAccessNanos = timeUnit.toNanos(n);
        return this;
    }
    
    public CacheBuilder<K, V> expireAfterWrite(final long n, final TimeUnit timeUnit) {
        Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", this.expireAfterWriteNanos);
        boolean b;
        if (n < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "duration cannot be negative: %s %s", n, timeUnit);
        this.expireAfterWriteNanos = timeUnit.toNanos(n);
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
    
    LocalCache.Strength getKeyStrength() {
        return MoreObjects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
    }
    
    long getMaximumWeight() {
        if (this.expireAfterWriteNanos == 0L || this.expireAfterAccessNanos == 0L) {
            return 0L;
        }
        long n;
        if (this.weigher != null) {
            n = this.maximumWeight;
        }
        else {
            n = this.maximumSize;
        }
        return n;
    }
    
    long getRefreshNanos() {
        long refreshNanos;
        if (this.refreshNanos == -1L) {
            refreshNanos = 0L;
        }
        else {
            refreshNanos = this.refreshNanos;
        }
        return refreshNanos;
    }
    
     <K1 extends K, V1 extends V> RemovalListener<K1, V1> getRemovalListener() {
        return MoreObjects.firstNonNull((RemovalListener<K1, V1>)this.removalListener, (RemovalListener<K1, V1>)NullListener.INSTANCE);
    }
    
    Supplier<? extends AbstractCache.StatsCounter> getStatsCounterSupplier() {
        return this.statsCounterSupplier;
    }
    
    Ticker getTicker(final boolean b) {
        if (this.ticker == null) {
            Ticker ticker;
            if (!b) {
                ticker = CacheBuilder.NULL_TICKER;
            }
            else {
                ticker = Ticker.systemTicker();
            }
            return ticker;
        }
        return this.ticker;
    }
    
    Equivalence<Object> getValueEquivalence() {
        return MoreObjects.firstNonNull(this.valueEquivalence, this.getValueStrength().defaultEquivalence());
    }
    
    LocalCache.Strength getValueStrength() {
        return MoreObjects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
    }
    
     <K1 extends K, V1 extends V> Weigher<K1, V1> getWeigher() {
        return MoreObjects.firstNonNull((Weigher<K1, V1>)this.weigher, (Weigher<K1, V1>)OneWeigher.INSTANCE);
    }
    
    public CacheBuilder<K, V> initialCapacity(final int initialCapacity) {
        final boolean b = false;
        Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", this.initialCapacity);
        Preconditions.checkArgument(initialCapacity >= 0 || b);
        this.initialCapacity = initialCapacity;
        return this;
    }
    
    boolean isRecordingStats() {
        return this.statsCounterSupplier == CacheBuilder.CACHE_STATS_COUNTER;
    }
    
    @GwtIncompatible("To be supported")
    CacheBuilder<K, V> keyEquivalence(final Equivalence<Object> equivalence) {
        Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", this.keyEquivalence);
        this.keyEquivalence = Preconditions.checkNotNull(equivalence);
        return this;
    }
    
    @GwtIncompatible("To be supported")
    CacheBuilder<K, V> lenientParsing() {
        this.strictParsing = false;
        return this;
    }
    
    public CacheBuilder<K, V> maximumSize(final long maximumSize) {
        final boolean b = true;
        Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", this.maximumSize);
        Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", this.maximumWeight);
        Preconditions.checkState(this.weigher == null, (Object)"maximum size can not be combined with weigher");
        boolean b2;
        if (maximumSize < 0L) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        Preconditions.checkArgument(!b2 && b, (Object)"maximum size must not be negative");
        this.maximumSize = maximumSize;
        return this;
    }
    
    @GwtIncompatible("To be supported")
    public CacheBuilder<K, V> maximumWeight(final long maximumWeight) {
        final boolean b = true;
        Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", this.maximumWeight);
        Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", this.maximumSize);
        this.maximumWeight = maximumWeight;
        boolean b2;
        if (maximumWeight < 0L) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        Preconditions.checkArgument(!b2 && b, (Object)"maximum weight must not be negative");
        return this;
    }
    
    public CacheBuilder<K, V> recordStats() {
        this.statsCounterSupplier = CacheBuilder.CACHE_STATS_COUNTER;
        return this;
    }
    
    @GwtIncompatible("To be supported (synchronously).")
    public CacheBuilder<K, V> refreshAfterWrite(final long n, final TimeUnit timeUnit) {
        Preconditions.checkNotNull(timeUnit);
        Preconditions.checkState(this.refreshNanos == -1L, "refresh was already set to %s ns", this.refreshNanos);
        boolean b;
        if (n <= 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "duration must be positive: %s %s", n, timeUnit);
        this.refreshNanos = timeUnit.toNanos(n);
        return this;
    }
    
    @CheckReturnValue
    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removalListener(final RemovalListener<? super K1, ? super V1> removalListener) {
        Preconditions.checkState(this.removalListener == null);
        this.removalListener = Preconditions.checkNotNull(removalListener);
        return (CacheBuilder<K1, V1>)this;
    }
    
    CacheBuilder<K, V> setKeyStrength(final LocalCache.Strength strength) {
        Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", this.keyStrength);
        this.keyStrength = Preconditions.checkNotNull(strength);
        return this;
    }
    
    CacheBuilder<K, V> setValueStrength(final LocalCache.Strength strength) {
        Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", this.valueStrength);
        this.valueStrength = Preconditions.checkNotNull(strength);
        return this;
    }
    
    @GwtIncompatible("java.lang.ref.SoftReference")
    public CacheBuilder<K, V> softValues() {
        return this.setValueStrength(LocalCache.Strength.SOFT);
    }
    
    public CacheBuilder<K, V> ticker(final Ticker ticker) {
        Preconditions.checkState(this.ticker == null);
        this.ticker = Preconditions.checkNotNull(ticker);
        return this;
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
        if (this.maximumSize != -1L) {
            stringHelper.add("maximumSize", this.maximumSize);
        }
        if (this.maximumWeight != -1L) {
            stringHelper.add("maximumWeight", this.maximumWeight);
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
        if (this.valueEquivalence != null) {
            stringHelper.addValue("valueEquivalence");
        }
        if (this.removalListener != null) {
            stringHelper.addValue("removalListener");
        }
        return stringHelper.toString();
    }
    
    @GwtIncompatible("To be supported")
    CacheBuilder<K, V> valueEquivalence(final Equivalence<Object> equivalence) {
        Preconditions.checkState(this.valueEquivalence == null, "value equivalence was already set to %s", this.valueEquivalence);
        this.valueEquivalence = Preconditions.checkNotNull(equivalence);
        return this;
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    public CacheBuilder<K, V> weakKeys() {
        return this.setKeyStrength(LocalCache.Strength.WEAK);
    }
    
    @GwtIncompatible("java.lang.ref.WeakReference")
    public CacheBuilder<K, V> weakValues() {
        return this.setValueStrength(LocalCache.Strength.WEAK);
    }
    
    @GwtIncompatible("To be supported")
    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> weigher(final Weigher<? super K1, ? super V1> weigher) {
        Preconditions.checkState(this.weigher == null);
        if (this.strictParsing) {
            Preconditions.checkState(this.maximumSize == -1L, "weigher can not be combined with maximum size", this.maximumSize);
        }
        this.weigher = Preconditions.checkNotNull(weigher);
        return (CacheBuilder<K1, V1>)this;
    }
    
    enum NullListener implements RemovalListener<Object, Object>
    {
        INSTANCE;
        
        @Override
        public void onRemoval(final RemovalNotification<Object, Object> removalNotification) {
        }
    }
    
    enum OneWeigher implements Weigher<Object, Object>
    {
        INSTANCE;
        
        @Override
        public int weigh(final Object o, final Object o2) {
            return 1;
        }
    }
}
