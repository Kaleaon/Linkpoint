package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckReturnValue;

@GwtCompatible(emulated = true)
public final class Stopwatch {
    private long elapsedNanos;
    private boolean isRunning;
    private long startTick;
    private final Ticker ticker;

    /* renamed from: com.google.common.base.Stopwatch$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$util$concurrent$TimeUnit = new int[TimeUnit.values().length];

        static {
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.NANOSECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MICROSECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MILLISECONDS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$util$concurrent$TimeUnit[TimeUnit.DAYS.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    Stopwatch() {
        this.ticker = Ticker.systemTicker();
    }

    Stopwatch(Ticker ticker2) {
        this.ticker = (Ticker) Preconditions.checkNotNull(ticker2, "ticker");
    }

    private static String abbreviate(TimeUnit timeUnit) {
        switch (AnonymousClass1.$SwitchMap$java$util$concurrent$TimeUnit[timeUnit.ordinal()]) {
            case 1:
                return "ns";
            case 2:
                return "μs";
            case 3:
                return "ms";
            case 4:
                return "s";
            case 5:
                return "min";
            case 6:
                return "h";
            case 7:
                return "d";
            default:
                throw new AssertionError();
        }
    }

    private static TimeUnit chooseUnit(long j) {
        boolean z = true;
        if (!(TimeUnit.DAYS.convert(j, TimeUnit.NANOSECONDS) <= 0)) {
            return TimeUnit.DAYS;
        }
        if (!(TimeUnit.HOURS.convert(j, TimeUnit.NANOSECONDS) <= 0)) {
            return TimeUnit.HOURS;
        }
        if (!(TimeUnit.MINUTES.convert(j, TimeUnit.NANOSECONDS) <= 0)) {
            return TimeUnit.MINUTES;
        }
        if (!(TimeUnit.SECONDS.convert(j, TimeUnit.NANOSECONDS) <= 0)) {
            return TimeUnit.SECONDS;
        }
        if (!(TimeUnit.MILLISECONDS.convert(j, TimeUnit.NANOSECONDS) <= 0)) {
            return TimeUnit.MILLISECONDS;
        }
        if (TimeUnit.MICROSECONDS.convert(j, TimeUnit.NANOSECONDS) > 0) {
            z = false;
        }
        return !z ? TimeUnit.MICROSECONDS : TimeUnit.NANOSECONDS;
    }

    @CheckReturnValue
    public static Stopwatch createStarted() {
        return new Stopwatch().start();
    }

    @CheckReturnValue
    public static Stopwatch createStarted(Ticker ticker2) {
        return new Stopwatch(ticker2).start();
    }

    @CheckReturnValue
    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }

    @CheckReturnValue
    public static Stopwatch createUnstarted(Ticker ticker2) {
        return new Stopwatch(ticker2);
    }

    private long elapsedNanos() {
        return !this.isRunning ? this.elapsedNanos : (this.ticker.read() - this.startTick) + this.elapsedNanos;
    }

    @CheckReturnValue
    public long elapsed(TimeUnit timeUnit) {
        return timeUnit.convert(elapsedNanos(), TimeUnit.NANOSECONDS);
    }

    @CheckReturnValue
    public boolean isRunning() {
        return this.isRunning;
    }

    public Stopwatch reset() {
        this.elapsedNanos = 0;
        this.isRunning = false;
        return this;
    }

    public Stopwatch start() {
        boolean z = false;
        if (!this.isRunning) {
            z = true;
        }
        Preconditions.checkState(z, "This stopwatch is already running.");
        this.isRunning = true;
        this.startTick = this.ticker.read();
        return this;
    }

    public Stopwatch stop() {
        long read = this.ticker.read();
        Preconditions.checkState(this.isRunning, "This stopwatch is already stopped.");
        this.isRunning = false;
        this.elapsedNanos = (read - this.startTick) + this.elapsedNanos;
        return this;
    }

    @GwtIncompatible("String.format()")
    public String toString() {
        long elapsedNanos2 = elapsedNanos();
        TimeUnit chooseUnit = chooseUnit(elapsedNanos2);
        double convert = ((double) elapsedNanos2) / ((double) TimeUnit.NANOSECONDS.convert(1, chooseUnit));
        return String.format(Locale.ROOT, "%.4g %s", new Object[]{Double.valueOf(convert), abbreviate(chooseUnit)});
    }
}
