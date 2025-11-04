// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.util.Locale;
import javax.annotation.CheckReturnValue;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Stopwatch
{
    private long elapsedNanos;
    private boolean isRunning;
    private long startTick;
    private final Ticker ticker;
    
    Stopwatch() {
        this.ticker = Ticker.systemTicker();
    }
    
    Stopwatch(final Ticker ticker) {
        this.ticker = Preconditions.checkNotNull(ticker, (Object)"ticker");
    }
    
    private static String abbreviate(final TimeUnit timeUnit) {
        switch (timeUnit) {
            default: {
                throw new AssertionError();
            }
            case NANOSECONDS: {
                return "ns";
            }
            case MICROSECONDS: {
                return "\u03bcs";
            }
            case MILLISECONDS: {
                return "ms";
            }
            case SECONDS: {
                return "s";
            }
            case MINUTES: {
                return "min";
            }
            case HOURS: {
                return "h";
            }
            case DAYS: {
                return "d";
            }
        }
    }
    
    private static TimeUnit chooseUnit(final long n) {
        final int n2 = 1;
        int n3;
        if (TimeUnit.DAYS.convert(n, TimeUnit.NANOSECONDS) <= 0L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 == 0) {
            return TimeUnit.DAYS;
        }
        int n4;
        if (TimeUnit.HOURS.convert(n, TimeUnit.NANOSECONDS) <= 0L) {
            n4 = 1;
        }
        else {
            n4 = 0;
        }
        if (n4 == 0) {
            return TimeUnit.HOURS;
        }
        int n5;
        if (TimeUnit.MINUTES.convert(n, TimeUnit.NANOSECONDS) <= 0L) {
            n5 = 1;
        }
        else {
            n5 = 0;
        }
        if (n5 == 0) {
            return TimeUnit.MINUTES;
        }
        int n6;
        if (TimeUnit.SECONDS.convert(n, TimeUnit.NANOSECONDS) <= 0L) {
            n6 = 1;
        }
        else {
            n6 = 0;
        }
        if (n6 == 0) {
            return TimeUnit.SECONDS;
        }
        int n7;
        if (TimeUnit.MILLISECONDS.convert(n, TimeUnit.NANOSECONDS) <= 0L) {
            n7 = 1;
        }
        else {
            n7 = 0;
        }
        if (n7 == 0) {
            return TimeUnit.MILLISECONDS;
        }
        int n8;
        if (TimeUnit.MICROSECONDS.convert(n, TimeUnit.NANOSECONDS) <= 0L) {
            n8 = n2;
        }
        else {
            n8 = 0;
        }
        if (n8 == 0) {
            return TimeUnit.MICROSECONDS;
        }
        return TimeUnit.NANOSECONDS;
    }
    
    @CheckReturnValue
    public static Stopwatch createStarted() {
        return new Stopwatch().start();
    }
    
    @CheckReturnValue
    public static Stopwatch createStarted(final Ticker ticker) {
        return new Stopwatch(ticker).start();
    }
    
    @CheckReturnValue
    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }
    
    @CheckReturnValue
    public static Stopwatch createUnstarted(final Ticker ticker) {
        return new Stopwatch(ticker);
    }
    
    private long elapsedNanos() {
        long elapsedNanos;
        if (!this.isRunning) {
            elapsedNanos = this.elapsedNanos;
        }
        else {
            elapsedNanos = this.ticker.read() - this.startTick + this.elapsedNanos;
        }
        return elapsedNanos;
    }
    
    @CheckReturnValue
    public long elapsed(final TimeUnit timeUnit) {
        return timeUnit.convert(this.elapsedNanos(), TimeUnit.NANOSECONDS);
    }
    
    @CheckReturnValue
    public boolean isRunning() {
        return this.isRunning;
    }
    
    public Stopwatch reset() {
        this.elapsedNanos = 0L;
        this.isRunning = false;
        return this;
    }
    
    public Stopwatch start() {
        boolean b = false;
        if (!this.isRunning) {
            b = true;
        }
        Preconditions.checkState(b, (Object)"This stopwatch is already running.");
        this.isRunning = true;
        this.startTick = this.ticker.read();
        return this;
    }
    
    public Stopwatch stop() {
        final long read = this.ticker.read();
        Preconditions.checkState(this.isRunning, (Object)"This stopwatch is already stopped.");
        this.isRunning = false;
        this.elapsedNanos += read - this.startTick;
        return this;
    }
    
    @GwtIncompatible("String.format()")
    @Override
    public String toString() {
        final long elapsedNanos = this.elapsedNanos();
        final TimeUnit chooseUnit = chooseUnit(elapsedNanos);
        return String.format(Locale.ROOT, "%.4g %s", elapsedNanos / (double)TimeUnit.NANOSECONDS.convert(1L, chooseUnit), abbreviate(chooseUnit));
    }
}
