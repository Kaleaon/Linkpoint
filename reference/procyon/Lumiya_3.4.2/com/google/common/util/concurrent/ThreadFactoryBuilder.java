// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.base.Preconditions;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class ThreadFactoryBuilder
{
    private ThreadFactory backingThreadFactory;
    private Boolean daemon;
    private String nameFormat;
    private Integer priority;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
    
    public ThreadFactoryBuilder() {
        this.nameFormat = null;
        this.daemon = null;
        this.priority = null;
        this.uncaughtExceptionHandler = null;
        this.backingThreadFactory = null;
    }
    
    private static ThreadFactory build(final ThreadFactoryBuilder threadFactoryBuilder) {
        AtomicLong atomicLong = null;
        final String nameFormat = threadFactoryBuilder.nameFormat;
        final Boolean daemon = threadFactoryBuilder.daemon;
        final Integer priority = threadFactoryBuilder.priority;
        final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = threadFactoryBuilder.uncaughtExceptionHandler;
        ThreadFactory threadFactory;
        if (threadFactoryBuilder.backingThreadFactory == null) {
            threadFactory = Executors.defaultThreadFactory();
        }
        else {
            threadFactory = threadFactoryBuilder.backingThreadFactory;
        }
        if (nameFormat != null) {
            atomicLong = new AtomicLong(0L);
        }
        return new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                final Thread thread = threadFactory.newThread(runnable);
                if (nameFormat != null) {
                    thread.setName(format(nameFormat, new Object[] { atomicLong.getAndIncrement() }));
                }
                if (daemon != null) {
                    thread.setDaemon(daemon);
                }
                if (priority != null) {
                    thread.setPriority(priority);
                }
                if (uncaughtExceptionHandler != null) {
                    thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
                }
                return thread;
            }
        };
    }
    
    private static String format(final String format, final Object... args) {
        return String.format(Locale.ROOT, format, args);
    }
    
    public ThreadFactory build() {
        return build(this);
    }
    
    public ThreadFactoryBuilder setDaemon(final boolean b) {
        this.daemon = b;
        return this;
    }
    
    public ThreadFactoryBuilder setNameFormat(final String nameFormat) {
        format(nameFormat, 0);
        this.nameFormat = nameFormat;
        return this;
    }
    
    public ThreadFactoryBuilder setPriority(final int i) {
        Preconditions.checkArgument(i >= 1, "Thread priority (%s) must be >= %s", i, 1);
        Preconditions.checkArgument(i <= 10, "Thread priority (%s) must be <= %s", i, 10);
        this.priority = i;
        return this;
    }
    
    public ThreadFactoryBuilder setThreadFactory(final ThreadFactory threadFactory) {
        this.backingThreadFactory = Preconditions.checkNotNull(threadFactory);
        return this;
    }
    
    public ThreadFactoryBuilder setUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = Preconditions.checkNotNull(uncaughtExceptionHandler);
        return this;
    }
}
