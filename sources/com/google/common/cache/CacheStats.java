package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import javax.annotation.Nullable;

@GwtCompatible
public final class CacheStats {
    private final long evictionCount;
    private final long hitCount;
    private final long loadExceptionCount;
    private final long loadSuccessCount;
    private final long missCount;
    private final long totalLoadTime;

    public CacheStats(long j, long j2, long j3, long j4, long j5, long j6) {
        Preconditions.checkArgument(!((j > 0 ? 1 : (j == 0 ? 0 : -1)) < 0));
        Preconditions.checkArgument(!((j2 > 0 ? 1 : (j2 == 0 ? 0 : -1)) < 0));
        Preconditions.checkArgument(!((j3 > 0 ? 1 : (j3 == 0 ? 0 : -1)) < 0));
        Preconditions.checkArgument(!((j4 > 0 ? 1 : (j4 == 0 ? 0 : -1)) < 0));
        Preconditions.checkArgument(!((j5 > 0 ? 1 : (j5 == 0 ? 0 : -1)) < 0));
        Preconditions.checkArgument(!((j6 > 0 ? 1 : (j6 == 0 ? 0 : -1)) < 0));
        this.hitCount = j;
        this.missCount = j2;
        this.loadSuccessCount = j3;
        this.loadExceptionCount = j4;
        this.totalLoadTime = j5;
        this.evictionCount = j6;
    }

    public double averageLoadPenalty() {
        long j = this.loadSuccessCount + this.loadExceptionCount;
        if (j == 0) {
            return 0.0d;
        }
        return ((double) this.totalLoadTime) / ((double) j);
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CacheStats)) {
            return false;
        }
        CacheStats cacheStats = (CacheStats) obj;
        return this.hitCount == cacheStats.hitCount && this.missCount == cacheStats.missCount && this.loadSuccessCount == cacheStats.loadSuccessCount && this.loadExceptionCount == cacheStats.loadExceptionCount && this.totalLoadTime == cacheStats.totalLoadTime && this.evictionCount == cacheStats.evictionCount;
    }

    public long evictionCount() {
        return this.evictionCount;
    }

    public int hashCode() {
        return Objects.hashCode(Long.valueOf(this.hitCount), Long.valueOf(this.missCount), Long.valueOf(this.loadSuccessCount), Long.valueOf(this.loadExceptionCount), Long.valueOf(this.totalLoadTime), Long.valueOf(this.evictionCount));
    }

    public long hitCount() {
        return this.hitCount;
    }

    public double hitRate() {
        long requestCount = requestCount();
        if (requestCount == 0) {
            return 1.0d;
        }
        return ((double) this.hitCount) / ((double) requestCount);
    }

    public long loadCount() {
        return this.loadSuccessCount + this.loadExceptionCount;
    }

    public long loadExceptionCount() {
        return this.loadExceptionCount;
    }

    public double loadExceptionRate() {
        long j = this.loadSuccessCount + this.loadExceptionCount;
        if (j == 0) {
            return 0.0d;
        }
        return ((double) this.loadExceptionCount) / ((double) j);
    }

    public long loadSuccessCount() {
        return this.loadSuccessCount;
    }

    public CacheStats minus(CacheStats cacheStats) {
        return new CacheStats(Math.max(0, this.hitCount - cacheStats.hitCount), Math.max(0, this.missCount - cacheStats.missCount), Math.max(0, this.loadSuccessCount - cacheStats.loadSuccessCount), Math.max(0, this.loadExceptionCount - cacheStats.loadExceptionCount), Math.max(0, this.totalLoadTime - cacheStats.totalLoadTime), Math.max(0, this.evictionCount - cacheStats.evictionCount));
    }

    public long missCount() {
        return this.missCount;
    }

    public double missRate() {
        long requestCount = requestCount();
        if (requestCount == 0) {
            return 0.0d;
        }
        return ((double) this.missCount) / ((double) requestCount);
    }

    public CacheStats plus(CacheStats cacheStats) {
        return new CacheStats(this.hitCount + cacheStats.hitCount, this.missCount + cacheStats.missCount, this.loadSuccessCount + cacheStats.loadSuccessCount, this.loadExceptionCount + cacheStats.loadExceptionCount, this.totalLoadTime + cacheStats.totalLoadTime, this.evictionCount + cacheStats.evictionCount);
    }

    public long requestCount() {
        return this.hitCount + this.missCount;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object) this).add("hitCount", this.hitCount).add("missCount", this.missCount).add("loadSuccessCount", this.loadSuccessCount).add("loadExceptionCount", this.loadExceptionCount).add("totalLoadTime", this.totalLoadTime).add("evictionCount", this.evictionCount).toString();
    }

    public long totalLoadTime() {
        return this.totalLoadTime;
    }
}
