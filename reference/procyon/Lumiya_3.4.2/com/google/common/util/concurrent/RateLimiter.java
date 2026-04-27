// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Stopwatch;
import java.util.Locale;
import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.TimeUnit;
import com.google.common.base.Preconditions;
import javax.annotation.concurrent.ThreadSafe;
import com.google.common.annotations.Beta;

@Beta
@ThreadSafe
public abstract class RateLimiter
{
    private volatile Object mutexDoNotUseDirectly;
    private final SleepingStopwatch stopwatch;
    
    RateLimiter(final SleepingStopwatch sleepingStopwatch) {
        this.stopwatch = Preconditions.checkNotNull(sleepingStopwatch);
    }
    
    private boolean canAcquire(final long n, final long n2) {
        boolean b = true;
        int n3;
        if (this.queryEarliestAvailable(n) - n2 > n) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 != 0) {
            b = false;
        }
        return b;
    }
    
    private static int checkPermits(final int i) {
        Preconditions.checkArgument(i > 0, "Requested permits (%s) must be positive", i);
        return i;
    }
    
    public static RateLimiter create(final double n) {
        return create(SleepingStopwatch.createFromSystemTimer(), n);
    }
    
    public static RateLimiter create(final double n, final long l, final TimeUnit timeUnit) {
        boolean b;
        if (l < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, "warmupPeriod must not be negative: %s", l);
        return create(SleepingStopwatch.createFromSystemTimer(), n, l, timeUnit, 3.0);
    }
    
    @VisibleForTesting
    static RateLimiter create(final SleepingStopwatch sleepingStopwatch, final double rate) {
        final SmoothRateLimiter.SmoothBursty smoothBursty = new SmoothRateLimiter.SmoothBursty(sleepingStopwatch, 1.0);
        smoothBursty.setRate(rate);
        return smoothBursty;
    }
    
    @VisibleForTesting
    static RateLimiter create(final SleepingStopwatch sleepingStopwatch, final double rate, final long n, final TimeUnit timeUnit, final double n2) {
        final SmoothRateLimiter.SmoothWarmingUp smoothWarmingUp = new SmoothRateLimiter.SmoothWarmingUp(sleepingStopwatch, n, timeUnit, n2);
        smoothWarmingUp.setRate(rate);
        return smoothWarmingUp;
    }
    
    private Object mutex() {
        final Object mutexDoNotUseDirectly = this.mutexDoNotUseDirectly;
        if (mutexDoNotUseDirectly == null) {
            while (true) {
                while (true) {
                    synchronized (this) {
                        if (this.mutexDoNotUseDirectly != null) {
                            break;
                        }
                    }
                    this.mutexDoNotUseDirectly = new Object();
                    continue;
                }
            }
        }
        return mutexDoNotUseDirectly;
    }
    
    public double acquire() {
        return this.acquire(1);
    }
    
    public double acquire(final int n) {
        final long reserve = this.reserve(n);
        this.stopwatch.sleepMicrosUninterruptibly(reserve);
        return reserve * 1.0 / TimeUnit.SECONDS.toMicros(1L);
    }
    
    abstract double doGetRate();
    
    abstract void doSetRate(final double p0, final long p1);
    
    public final double getRate() {
        synchronized (this.mutex()) {
            return this.doGetRate();
        }
    }
    
    abstract long queryEarliestAvailable(final long p0);
    
    final long reserve(final int n) {
        checkPermits(n);
        synchronized (this.mutex()) {
            return this.reserveAndGetWaitLength(n, this.stopwatch.readMicros());
        }
    }
    
    final long reserveAndGetWaitLength(final int n, final long n2) {
        return Math.max(this.reserveEarliestAvailable(n, n2) - n2, 0L);
    }
    
    abstract long reserveEarliestAvailable(final int p0, final long p1);
    
    public final void setRate(final double v) {
        boolean b = false;
        Label_0053: {
            if (v > 0.0) {
                if (!Double.isNaN(v)) {
                    break Label_0053;
                }
                b = b;
            }
            while (true) {
                Preconditions.checkArgument(b, (Object)"rate must be positive");
                synchronized (this.mutex()) {
                    this.doSetRate(v, this.stopwatch.readMicros());
                    return;
                    b = true;
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "RateLimiter[stableRate=%3.1fqps]", this.getRate());
    }
    
    public boolean tryAcquire() {
        return this.tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
    }
    
    public boolean tryAcquire(final int n) {
        return this.tryAcquire(n, 0L, TimeUnit.MICROSECONDS);
    }
    
    public boolean tryAcquire(final int n, long duration, final TimeUnit timeUnit) {
        final long max = Math.max(timeUnit.toMicros(duration), 0L);
        checkPermits(n);
        synchronized (this.mutex()) {
            duration = this.stopwatch.readMicros();
            if (this.canAcquire(duration, max)) {
                duration = this.reserveAndGetWaitLength(n, duration);
                monitorexit(this.mutex());
                this.stopwatch.sleepMicrosUninterruptibly(duration);
                return true;
            }
            return false;
        }
    }
    
    public boolean tryAcquire(final long n, final TimeUnit timeUnit) {
        return this.tryAcquire(1, n, timeUnit);
    }
    
    @VisibleForTesting
    abstract static class SleepingStopwatch
    {
        static final SleepingStopwatch createFromSystemTimer() {
            return new SleepingStopwatch() {
                final Stopwatch stopwatch = Stopwatch.createStarted();
                
                @Override
                long readMicros() {
                    return this.stopwatch.elapsed(TimeUnit.MICROSECONDS);
                }
                
                @Override
                void sleepMicrosUninterruptibly(final long n) {
                    int n2;
                    if (n <= 0L) {
                        n2 = 1;
                    }
                    else {
                        n2 = 0;
                    }
                    if (n2 == 0) {
                        Uninterruptibles.sleepUninterruptibly(n, TimeUnit.MICROSECONDS);
                    }
                }
            };
        }
        
        abstract long readMicros();
        
        abstract void sleepMicrosUninterruptibly(final long p0);
    }
}
