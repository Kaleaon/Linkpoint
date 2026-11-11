package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.SmoothRateLimiter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;
@Beta
@ThreadSafe
/* loaded from: classes.dex */
public abstract class RateLimiter {
    private volatile Object mutexDoNotUseDirectly;
    private final SleepingStopwatch stopwatch;

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static abstract class SleepingStopwatch {
        SleepingStopwatch() {
        }

        static final SleepingStopwatch createFromSystemTimer() {
            return new SleepingStopwatch() { // from class: com.google.common.util.concurrent.RateLimiter.SleepingStopwatch.1
                final Stopwatch stopwatch = Stopwatch.createStarted();

                @Override // com.google.common.util.concurrent.RateLimiter.SleepingStopwatch
                long readMicros() {
                    return this.stopwatch.elapsed(TimeUnit.MICROSECONDS);
                }

                @Override // com.google.common.util.concurrent.RateLimiter.SleepingStopwatch
                void sleepMicrosUninterruptibly(long j) {
                    if (j <= 0) {
                        return;
                    }
                    Uninterruptibles.sleepUninterruptibly(j, TimeUnit.MICROSECONDS);
                }
            };
        }

        abstract long readMicros();

        abstract void sleepMicrosUninterruptibly(long j);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RateLimiter(SleepingStopwatch sleepingStopwatch) {
        this.stopwatch = (SleepingStopwatch) Preconditions.checkNotNull(sleepingStopwatch);
    }

    private boolean canAcquire(long j, long j2) {
        return !(((queryEarliestAvailable(j) - j2) > j ? 1 : ((queryEarliestAvailable(j) - j2) == j ? 0 : -1)) > 0);
    }

    private static int checkPermits(int i) {
        Preconditions.checkArgument(i > 0, "Requested permits (%s) must be positive", Integer.valueOf(i));
        return i;
    }

    public static RateLimiter create(double d) {
        return create(SleepingStopwatch.createFromSystemTimer(), d);
    }

    public static RateLimiter create(double d, long j, TimeUnit timeUnit) {
        Preconditions.checkArgument(!((j > 0L ? 1 : (j == 0L ? 0 : -1)) < 0), "warmupPeriod must not be negative: %s", Long.valueOf(j));
        return create(SleepingStopwatch.createFromSystemTimer(), d, j, timeUnit, 3.0d);
    }

    @VisibleForTesting
    static RateLimiter create(SleepingStopwatch sleepingStopwatch, double d) {
        SmoothRateLimiter.SmoothBursty smoothBursty = new SmoothRateLimiter.SmoothBursty(sleepingStopwatch, 1.0d);
        smoothBursty.setRate(d);
        return smoothBursty;
    }

    @VisibleForTesting
    static RateLimiter create(SleepingStopwatch sleepingStopwatch, double d, long j, TimeUnit timeUnit, double d2) {
        SmoothRateLimiter.SmoothWarmingUp smoothWarmingUp = new SmoothRateLimiter.SmoothWarmingUp(sleepingStopwatch, j, timeUnit, d2);
        smoothWarmingUp.setRate(d);
        return smoothWarmingUp;
    }

    private Object mutex() {
        Object obj = this.mutexDoNotUseDirectly;
        if (obj == null) {
            synchronized (this) {
                obj = this.mutexDoNotUseDirectly;
                if (obj == null) {
                    obj = new Object();
                    this.mutexDoNotUseDirectly = obj;
                }
            }
        }
        return obj;
    }

    public double acquire() {
        return acquire(1);
    }

    public double acquire(int i) {
        long reserve = reserve(i);
        this.stopwatch.sleepMicrosUninterruptibly(reserve);
        return (reserve * 1.0d) / TimeUnit.SECONDS.toMicros(1L);
    }

    abstract double doGetRate();

    abstract void doSetRate(double d, long j);

    public final double getRate() {
        double doGetRate;
        synchronized (mutex()) {
            doGetRate = doGetRate();
        }
        return doGetRate;
    }

    abstract long queryEarliestAvailable(long j);

    final long reserve(int i) {
        long reserveAndGetWaitLength;
        checkPermits(i);
        synchronized (mutex()) {
            reserveAndGetWaitLength = reserveAndGetWaitLength(i, this.stopwatch.readMicros());
        }
        return reserveAndGetWaitLength;
    }

    final long reserveAndGetWaitLength(int i, long j) {
        return Math.max(reserveEarliestAvailable(i, j) - j, 0L);
    }

    abstract long reserveEarliestAvailable(int i, long j);

    public final void setRate(double d) {
        boolean z = false;
        if (d > 0.0d && !Double.isNaN(d)) {
            z = true;
        }
        Preconditions.checkArgument(z, "rate must be positive");
        synchronized (mutex()) {
            doSetRate(d, this.stopwatch.readMicros());
        }
    }

    public String toString() {
        return String.format(Locale.ROOT, "RateLimiter[stableRate=%3.1fqps]", Double.valueOf(getRate()));
    }

    public boolean tryAcquire() {
        return tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire(int i) {
        return tryAcquire(i, 0L, TimeUnit.MICROSECONDS);
    }

    public boolean tryAcquire(int i, long j, TimeUnit timeUnit) {
        long max = Math.max(timeUnit.toMicros(j), 0L);
        checkPermits(i);
        synchronized (mutex()) {
            long readMicros = this.stopwatch.readMicros();
            if (canAcquire(readMicros, max)) {
                this.stopwatch.sleepMicrosUninterruptibly(reserveAndGetWaitLength(i, readMicros));
                return true;
            }
            return false;
        }
    }

    public boolean tryAcquire(long j, TimeUnit timeUnit) {
        return tryAcquire(1, j, timeUnit);
    }
}
