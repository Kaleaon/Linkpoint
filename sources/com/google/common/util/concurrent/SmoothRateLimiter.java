package com.google.common.util.concurrent;

import com.google.common.math.LongMath;
import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.TimeUnit;

abstract class SmoothRateLimiter extends RateLimiter {
    double maxPermits;
    private long nextFreeTicketMicros;
    double stableIntervalMicros;
    double storedPermits;

    static final class SmoothBursty extends SmoothRateLimiter {
        final double maxBurstSeconds;

        SmoothBursty(RateLimiter.SleepingStopwatch sleepingStopwatch, double d) {
            super(sleepingStopwatch);
            this.maxBurstSeconds = d;
        }

        /* access modifiers changed from: package-private */
        public double coolDownIntervalMicros() {
            return this.stableIntervalMicros;
        }

        /* access modifiers changed from: package-private */
        public void doSetRate(double d, double d2) {
            double d3 = 0.0d;
            double d4 = this.maxPermits;
            this.maxPermits = this.maxBurstSeconds * d;
            if (d4 == Double.POSITIVE_INFINITY) {
                this.storedPermits = this.maxPermits;
                return;
            }
            if (d4 != 0.0d) {
                d3 = (this.storedPermits * this.maxPermits) / d4;
            }
            this.storedPermits = d3;
        }

        /* access modifiers changed from: package-private */
        public long storedPermitsToWaitTime(double d, double d2) {
            return 0;
        }
    }

    static final class SmoothWarmingUp extends SmoothRateLimiter {
        private double coldFactor;
        private double slope;
        private double thresholdPermits;
        private final long warmupPeriodMicros;

        SmoothWarmingUp(RateLimiter.SleepingStopwatch sleepingStopwatch, long j, TimeUnit timeUnit, double d) {
            super(sleepingStopwatch);
            this.warmupPeriodMicros = timeUnit.toMicros(j);
            this.coldFactor = d;
        }

        private double permitsToTime(double d) {
            return this.stableIntervalMicros + (this.slope * d);
        }

        /* access modifiers changed from: package-private */
        public double coolDownIntervalMicros() {
            return ((double) this.warmupPeriodMicros) / this.maxPermits;
        }

        /* access modifiers changed from: package-private */
        public void doSetRate(double d, double d2) {
            double d3 = this.maxPermits;
            double d4 = this.coldFactor * d2;
            this.thresholdPermits = (((double) this.warmupPeriodMicros) * 0.5d) / d2;
            this.maxPermits = this.thresholdPermits + ((((double) this.warmupPeriodMicros) * 2.0d) / (d2 + d4));
            this.slope = (d4 - d2) / (this.maxPermits - this.thresholdPermits);
            if (d3 == Double.POSITIVE_INFINITY) {
                this.storedPermits = 0.0d;
            } else {
                this.storedPermits = d3 == 0.0d ? this.maxPermits : (this.storedPermits * this.maxPermits) / d3;
            }
        }

        /* access modifiers changed from: package-private */
        public long storedPermitsToWaitTime(double d, double d2) {
            double d3 = d - this.thresholdPermits;
            long j = 0;
            if (d3 > 0.0d) {
                double min = Math.min(d3, d2);
                j = (long) (((permitsToTime(d3) + permitsToTime(d3 - min)) * min) / 2.0d);
                d2 -= min;
            }
            return (long) (((double) j) + (this.stableIntervalMicros * d2));
        }
    }

    private SmoothRateLimiter(RateLimiter.SleepingStopwatch sleepingStopwatch) {
        super(sleepingStopwatch);
        this.nextFreeTicketMicros = 0;
    }

    /* access modifiers changed from: package-private */
    public abstract double coolDownIntervalMicros();

    /* access modifiers changed from: package-private */
    public final double doGetRate() {
        return ((double) TimeUnit.SECONDS.toMicros(1)) / this.stableIntervalMicros;
    }

    /* access modifiers changed from: package-private */
    public abstract void doSetRate(double d, double d2);

    /* access modifiers changed from: package-private */
    public final void doSetRate(double d, long j) {
        resync(j);
        double micros = ((double) TimeUnit.SECONDS.toMicros(1)) / d;
        this.stableIntervalMicros = micros;
        doSetRate(d, micros);
    }

    /* access modifiers changed from: package-private */
    public final long queryEarliestAvailable(long j) {
        return this.nextFreeTicketMicros;
    }

    /* access modifiers changed from: package-private */
    public final long reserveEarliestAvailable(int i, long j) {
        resync(j);
        long j2 = this.nextFreeTicketMicros;
        double min = Math.min((double) i, this.storedPermits);
        try {
            this.nextFreeTicketMicros = LongMath.checkedAdd(this.nextFreeTicketMicros, ((long) ((((double) i) - min) * this.stableIntervalMicros)) + storedPermitsToWaitTime(this.storedPermits, min));
        } catch (ArithmeticException e) {
            this.nextFreeTicketMicros = Long.MAX_VALUE;
        }
        this.storedPermits -= min;
        return j2;
    }

    /* access modifiers changed from: package-private */
    public void resync(long j) {
        if (!(j <= this.nextFreeTicketMicros)) {
            this.storedPermits = Math.min(this.maxPermits, this.storedPermits + (((double) (j - this.nextFreeTicketMicros)) / coolDownIntervalMicros()));
            this.nextFreeTicketMicros = j;
        }
    }

    /* access modifiers changed from: package-private */
    public abstract long storedPermitsToWaitTime(double d, double d2);
}
