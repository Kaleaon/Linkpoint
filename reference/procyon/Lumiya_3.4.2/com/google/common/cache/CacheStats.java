// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public final class CacheStats
{
    private final long evictionCount;
    private final long hitCount;
    private final long loadExceptionCount;
    private final long loadSuccessCount;
    private final long missCount;
    private final long totalLoadTime;
    
    public CacheStats(final long hitCount, final long missCount, final long loadSuccessCount, final long loadExceptionCount, final long totalLoadTime, final long evictionCount) {
        boolean b;
        if (hitCount < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b);
        boolean b2;
        if (missCount < 0L) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        Preconditions.checkArgument(!b2);
        boolean b3;
        if (loadSuccessCount < 0L) {
            b3 = true;
        }
        else {
            b3 = false;
        }
        Preconditions.checkArgument(!b3);
        boolean b4;
        if (loadExceptionCount < 0L) {
            b4 = true;
        }
        else {
            b4 = false;
        }
        Preconditions.checkArgument(!b4);
        boolean b5;
        if (totalLoadTime < 0L) {
            b5 = true;
        }
        else {
            b5 = false;
        }
        Preconditions.checkArgument(!b5);
        boolean b6;
        if (evictionCount < 0L) {
            b6 = true;
        }
        else {
            b6 = false;
        }
        Preconditions.checkArgument(!b6);
        this.hitCount = hitCount;
        this.missCount = missCount;
        this.loadSuccessCount = loadSuccessCount;
        this.loadExceptionCount = loadExceptionCount;
        this.totalLoadTime = totalLoadTime;
        this.evictionCount = evictionCount;
    }
    
    public double averageLoadPenalty() {
        final long n = this.loadSuccessCount + this.loadExceptionCount;
        double n2;
        if (n == 0L) {
            n2 = 0.0;
        }
        else {
            n2 = this.totalLoadTime / (double)n;
        }
        return n2;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        final boolean b = false;
        if (!(o instanceof CacheStats)) {
            return false;
        }
        final CacheStats cacheStats = (CacheStats)o;
        boolean b2 = b;
        if (this.hitCount == cacheStats.hitCount) {
            b2 = b;
            if (this.missCount == cacheStats.missCount) {
                b2 = b;
                if (this.loadSuccessCount == cacheStats.loadSuccessCount) {
                    b2 = b;
                    if (this.loadExceptionCount == cacheStats.loadExceptionCount) {
                        b2 = b;
                        if (this.totalLoadTime == cacheStats.totalLoadTime) {
                            b2 = b;
                            if (this.evictionCount == cacheStats.evictionCount) {
                                b2 = true;
                            }
                        }
                    }
                }
            }
        }
        return b2;
    }
    
    public long evictionCount() {
        return this.evictionCount;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.hitCount, this.missCount, this.loadSuccessCount, this.loadExceptionCount, this.totalLoadTime, this.evictionCount);
    }
    
    public long hitCount() {
        return this.hitCount;
    }
    
    public double hitRate() {
        final long requestCount = this.requestCount();
        double n;
        if (requestCount == 0L) {
            n = 1.0;
        }
        else {
            n = this.hitCount / (double)requestCount;
        }
        return n;
    }
    
    public long loadCount() {
        return this.loadSuccessCount + this.loadExceptionCount;
    }
    
    public long loadExceptionCount() {
        return this.loadExceptionCount;
    }
    
    public double loadExceptionRate() {
        final long n = this.loadSuccessCount + this.loadExceptionCount;
        double n2;
        if (n == 0L) {
            n2 = 0.0;
        }
        else {
            n2 = this.loadExceptionCount / (double)n;
        }
        return n2;
    }
    
    public long loadSuccessCount() {
        return this.loadSuccessCount;
    }
    
    public CacheStats minus(final CacheStats cacheStats) {
        return new CacheStats(Math.max(0L, this.hitCount - cacheStats.hitCount), Math.max(0L, this.missCount - cacheStats.missCount), Math.max(0L, this.loadSuccessCount - cacheStats.loadSuccessCount), Math.max(0L, this.loadExceptionCount - cacheStats.loadExceptionCount), Math.max(0L, this.totalLoadTime - cacheStats.totalLoadTime), Math.max(0L, this.evictionCount - cacheStats.evictionCount));
    }
    
    public long missCount() {
        return this.missCount;
    }
    
    public double missRate() {
        final long requestCount = this.requestCount();
        double n;
        if (requestCount == 0L) {
            n = 0.0;
        }
        else {
            n = this.missCount / (double)requestCount;
        }
        return n;
    }
    
    public CacheStats plus(final CacheStats cacheStats) {
        return new CacheStats(this.hitCount + cacheStats.hitCount, this.missCount + cacheStats.missCount, this.loadSuccessCount + cacheStats.loadSuccessCount, this.loadExceptionCount + cacheStats.loadExceptionCount, this.totalLoadTime + cacheStats.totalLoadTime, this.evictionCount + cacheStats.evictionCount);
    }
    
    public long requestCount() {
        return this.hitCount + this.missCount;
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("hitCount", this.hitCount).add("missCount", this.missCount).add("loadSuccessCount", this.loadSuccessCount).add("loadExceptionCount", this.loadExceptionCount).add("totalLoadTime", this.totalLoadTime).add("evictionCount", this.evictionCount).toString();
    }
    
    public long totalLoadTime() {
        return this.totalLoadTime;
    }
}
