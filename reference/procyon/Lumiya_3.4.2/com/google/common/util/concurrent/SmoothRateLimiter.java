// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.math.LongMath;
import java.util.concurrent.TimeUnit;

abstract class SmoothRateLimiter extends RateLimiter
{
    double maxPermits;
    private long nextFreeTicketMicros;
    double stableIntervalMicros;
    double storedPermits;
    
    private SmoothRateLimiter(final SleepingStopwatch sleepingStopwatch) {
        super(sleepingStopwatch);
        this.nextFreeTicketMicros = 0L;
    }
    
    abstract double coolDownIntervalMicros();
    
    @Override
    final double doGetRate() {
        return TimeUnit.SECONDS.toMicros(1L) / this.stableIntervalMicros;
    }
    
    abstract void doSetRate(final double p0, final double p1);
    
    @Override
    final void doSetRate(final double n, final long n2) {
        this.resync(n2);
        this.doSetRate(n, this.stableIntervalMicros = TimeUnit.SECONDS.toMicros(1L) / n);
    }
    
    @Override
    final long queryEarliestAvailable(final long n) {
        return this.nextFreeTicketMicros;
    }
    
    @Override
    final long reserveEarliestAvailable(final int n, long nextFreeTicketMicros) {
        this.resync(nextFreeTicketMicros);
        nextFreeTicketMicros = this.nextFreeTicketMicros;
        final double min = Math.min(n, this.storedPermits);
        final double n2 = n;
        final long storedPermitsToWaitTime = this.storedPermitsToWaitTime(this.storedPermits, min);
        final long n3 = (long)((n2 - min) * this.stableIntervalMicros);
        while (true) {
            try {
                this.nextFreeTicketMicros = LongMath.checkedAdd(this.nextFreeTicketMicros, n3 + storedPermitsToWaitTime);
                this.storedPermits -= min;
                return nextFreeTicketMicros;
            }
            catch (final ArithmeticException ex) {
                this.nextFreeTicketMicros = Long.MAX_VALUE;
                continue;
            }
            break;
        }
    }
    
    void resync(final long nextFreeTicketMicros) {
        int n;
        if (nextFreeTicketMicros <= this.nextFreeTicketMicros) {
            n = 1;
        }
        else {
            n = 0;
        }
        if (n == 0) {
            this.storedPermits = Math.min(this.maxPermits, this.storedPermits + (nextFreeTicketMicros - this.nextFreeTicketMicros) / this.coolDownIntervalMicros());
            this.nextFreeTicketMicros = nextFreeTicketMicros;
        }
    }
    
    abstract long storedPermitsToWaitTime(final double p0, final double p1);
    
    static final class SmoothBursty extends SmoothRateLimiter
    {
        final double maxBurstSeconds;
        
        SmoothBursty(final SleepingStopwatch sleepingStopwatch, final double maxBurstSeconds) {
            super(sleepingStopwatch, null);
            this.maxBurstSeconds = maxBurstSeconds;
        }
        
        @Override
        double coolDownIntervalMicros() {
            return this.stableIntervalMicros;
        }
        
        @Override
        void doSetRate(double storedPermits, double n) {
            n = 0.0;
            final double maxPermits = this.maxPermits;
            this.maxPermits = this.maxBurstSeconds * storedPermits;
            if (maxPermits == Double.POSITIVE_INFINITY) {
                this.storedPermits = this.maxPermits;
            }
            else {
                if (maxPermits == 0.0) {
                    storedPermits = n;
                }
                else {
                    storedPermits = this.storedPermits * this.maxPermits / maxPermits;
                }
                this.storedPermits = storedPermits;
            }
        }
        
        @Override
        long storedPermitsToWaitTime(final double n, final double n2) {
            return 0L;
        }
    }
    
    static final class SmoothWarmingUp extends SmoothRateLimiter
    {
        private double coldFactor;
        private double slope;
        private double thresholdPermits;
        private final long warmupPeriodMicros;
        
        SmoothWarmingUp(final SleepingStopwatch sleepingStopwatch, final long duration, final TimeUnit timeUnit, final double coldFactor) {
            super(sleepingStopwatch, null);
            this.warmupPeriodMicros = timeUnit.toMicros(duration);
            this.coldFactor = coldFactor;
        }
        
        private double permitsToTime(final double n) {
            return this.stableIntervalMicros + this.slope * n;
        }
        
        @Override
        double coolDownIntervalMicros() {
            return this.warmupPeriodMicros / this.maxPermits;
        }
        
        @Override
        void doSetRate(double maxPermits, final double n) {
            final double maxPermits2 = this.maxPermits;
            maxPermits = this.coldFactor * n;
            this.thresholdPermits = this.warmupPeriodMicros * 0.5 / n;
            this.maxPermits = this.thresholdPermits + this.warmupPeriodMicros * 2.0 / (n + maxPermits);
            this.slope = (maxPermits - n) / (this.maxPermits - this.thresholdPermits);
            if (maxPermits2 == Double.POSITIVE_INFINITY) {
                this.storedPermits = 0.0;
            }
            else {
                if (maxPermits2 == 0.0) {
                    maxPermits = this.maxPermits;
                }
                else {
                    maxPermits = this.storedPermits * this.maxPermits / maxPermits2;
                }
                this.storedPermits = maxPermits;
            }
        }
        
        @Override
        long storedPermitsToWaitTime(double min, final double b) {
            final double a = min - this.thresholdPermits;
            long n = 0L;
            min = b;
            if (a > 0.0) {
                min = Math.min(a, b);
                n = (long)((this.permitsToTime(a) + this.permitsToTime(a - min)) * min / 2.0);
                min = b - min;
            }
            return (long)(n + this.stableIntervalMicros * min);
        }
    }
}
