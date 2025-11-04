// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.CountDownLatch;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public final class Uninterruptibles
{
    private Uninterruptibles() {
    }
    
    @GwtIncompatible("concurrency")
    public static void awaitUninterruptibly(final CountDownLatch countDownLatch) {
        int n = 0;
        while (true) {
            try {
                countDownLatch.await();
                if (n != 0) {
                    Thread.currentThread().interrupt();
                }
            }
            catch (final InterruptedException ex) {
                n = 1;
                continue;
            }
            finally {
                while (true) {
                    if (n == 0) {
                        break Label_0031;
                    }
                    Thread.currentThread().interrupt();
                    break Label_0031;
                    continue;
                }
            }
            break;
        }
    }
    
    @GwtIncompatible("concurrency")
    public static boolean awaitUninterruptibly(final CountDownLatch countDownLatch, long nanoTime, final TimeUnit timeUnit) {
        final boolean b = false;
        int n = 0;
        int n2 = b ? 1 : 0;
        try {
            final long nanos = timeUnit.toNanos(nanoTime);
            n2 = (b ? 1 : 0);
            final long nanoTime2 = System.nanoTime();
            nanoTime = nanos;
            while (true) {
                n2 = n;
                try {
                    final boolean await = countDownLatch.await(nanoTime, TimeUnit.NANOSECONDS);
                    if (n != 0) {
                        Thread.currentThread().interrupt();
                    }
                    return await;
                }
                catch (final InterruptedException ex) {
                    n2 = 1;
                    n = 1;
                    nanoTime = System.nanoTime();
                    nanoTime = nanoTime2 + nanos - nanoTime;
                }
            }
        }
        finally {
            while (true) {
                if (n2 == 0) {
                    break Label_0088;
                }
                Thread.currentThread().interrupt();
                break Label_0088;
                continue;
            }
        }
    }
    
    public static <V> V getUninterruptibly(final Future<V> future) throws ExecutionException {
        int n = 0;
        while (true) {
            try {
                final V value = future.get();
                if (n != 0) {
                    Thread.currentThread().interrupt();
                }
                return value;
            }
            catch (final InterruptedException ex) {
                n = 1;
                continue;
            }
            finally {
                while (true) {
                    if (n == 0) {
                        break Label_0035;
                    }
                    Thread.currentThread().interrupt();
                    break Label_0035;
                    continue;
                }
            }
            break;
        }
    }
    
    @GwtIncompatible("TODO")
    public static <V> V getUninterruptibly(final Future<V> future, long nanoTime, final TimeUnit timeUnit) throws ExecutionException, TimeoutException {
        final boolean b = false;
        int n = 0;
        int n2 = b ? 1 : 0;
        try {
            final long nanos = timeUnit.toNanos(nanoTime);
            n2 = (b ? 1 : 0);
            final long nanoTime2 = System.nanoTime();
            nanoTime = nanos;
            while (true) {
                n2 = n;
                try {
                    final V value = future.get(nanoTime, TimeUnit.NANOSECONDS);
                    if (n != 0) {
                        Thread.currentThread().interrupt();
                    }
                    return value;
                }
                catch (final InterruptedException ex) {
                    n2 = 1;
                    n = 1;
                    nanoTime = System.nanoTime();
                    nanoTime = nanoTime2 + nanos - nanoTime;
                }
            }
        }
        finally {
            while (true) {
                if (n2 == 0) {
                    break Label_0088;
                }
                Thread.currentThread().interrupt();
                break Label_0088;
                continue;
            }
        }
    }
    
    @GwtIncompatible("concurrency")
    public static void joinUninterruptibly(final Thread thread) {
        int n = 0;
        while (true) {
            try {
                thread.join();
                if (n != 0) {
                    Thread.currentThread().interrupt();
                }
            }
            catch (final InterruptedException ex) {
                n = 1;
                continue;
            }
            finally {
                while (true) {
                    if (n == 0) {
                        break Label_0031;
                    }
                    Thread.currentThread().interrupt();
                    break Label_0031;
                    continue;
                }
            }
            break;
        }
    }
    
    @GwtIncompatible("concurrency")
    public static void joinUninterruptibly(final Thread thread, long nanoTime, final TimeUnit timeUnit) {
        final boolean b = false;
        int n = 0;
        Preconditions.checkNotNull(thread);
        int n2 = b ? 1 : 0;
        try {
            final long nanos = timeUnit.toNanos(nanoTime);
            n2 = (b ? 1 : 0);
            final long nanoTime2 = System.nanoTime();
            nanoTime = nanos;
            while (true) {
                n2 = n;
                try {
                    TimeUnit.NANOSECONDS.timedJoin(thread, nanoTime);
                    if (n != 0) {
                        Thread.currentThread().interrupt();
                    }
                }
                catch (final InterruptedException ex) {
                    n2 = 1;
                    n = 1;
                    nanoTime = System.nanoTime();
                    nanoTime = nanoTime2 + nanos - nanoTime;
                }
            }
        }
        finally {
            while (true) {
                if (n2 == 0) {
                    break Label_0089;
                }
                Thread.currentThread().interrupt();
                break Label_0089;
                continue;
            }
        }
    }
    
    @GwtIncompatible("concurrency")
    public static <E> void putUninterruptibly(final BlockingQueue<E> blockingQueue, final E e) {
        int n = 0;
        while (true) {
            try {
                blockingQueue.put(e);
                if (n != 0) {
                    Thread.currentThread().interrupt();
                }
            }
            catch (final InterruptedException ex) {
                n = 1;
                continue;
            }
            finally {
                while (true) {
                    if (n == 0) {
                        break Label_0034;
                    }
                    Thread.currentThread().interrupt();
                    break Label_0034;
                    continue;
                }
            }
            break;
        }
    }
    
    @GwtIncompatible("concurrency")
    public static void sleepUninterruptibly(long nanoTime, final TimeUnit timeUnit) {
        final boolean b = false;
        int n = 0;
        int n2 = b ? 1 : 0;
        try {
            final long nanos = timeUnit.toNanos(nanoTime);
            n2 = (b ? 1 : 0);
            final long nanoTime2 = System.nanoTime();
            nanoTime = nanos;
            while (true) {
                n2 = n;
                try {
                    TimeUnit.NANOSECONDS.sleep(nanoTime);
                    if (n != 0) {
                        Thread.currentThread().interrupt();
                    }
                }
                catch (final InterruptedException ex) {
                    n2 = 1;
                    n = 1;
                    nanoTime = System.nanoTime();
                    nanoTime = nanoTime2 + nanos - nanoTime;
                }
            }
        }
        finally {
            while (true) {
                if (n2 == 0) {
                    break Label_0080;
                }
                Thread.currentThread().interrupt();
                break Label_0080;
                continue;
            }
        }
    }
    
    @GwtIncompatible("concurrency")
    public static <E> E takeUninterruptibly(final BlockingQueue<E> blockingQueue) {
        int n = 0;
        while (true) {
            try {
                final E take = blockingQueue.take();
                if (n != 0) {
                    Thread.currentThread().interrupt();
                }
                return take;
            }
            catch (final InterruptedException ex) {
                n = 1;
                continue;
            }
            finally {
                while (true) {
                    if (n == 0) {
                        break Label_0035;
                    }
                    Thread.currentThread().interrupt();
                    break Label_0035;
                    continue;
                }
            }
            break;
        }
    }
    
    @GwtIncompatible("concurrency")
    public static boolean tryAcquireUninterruptibly(final Semaphore semaphore, final int permits, long nanoTime, final TimeUnit timeUnit) {
        final boolean b = false;
        int n = 0;
        int n2 = b ? 1 : 0;
        try {
            final long nanos = timeUnit.toNanos(nanoTime);
            n2 = (b ? 1 : 0);
            final long nanoTime2 = System.nanoTime();
            nanoTime = nanos;
            while (true) {
                n2 = n;
                try {
                    final boolean tryAcquire = semaphore.tryAcquire(permits, nanoTime, TimeUnit.NANOSECONDS);
                    if (n != 0) {
                        Thread.currentThread().interrupt();
                    }
                    return tryAcquire;
                }
                catch (final InterruptedException ex) {
                    n2 = 1;
                    n = 1;
                    nanoTime = System.nanoTime();
                    nanoTime = nanoTime2 + nanos - nanoTime;
                }
            }
        }
        finally {
            while (true) {
                if (n2 == 0) {
                    break Label_0091;
                }
                Thread.currentThread().interrupt();
                break Label_0091;
                continue;
            }
        }
    }
    
    @GwtIncompatible("concurrency")
    public static boolean tryAcquireUninterruptibly(final Semaphore semaphore, final long n, final TimeUnit timeUnit) {
        return tryAcquireUninterruptibly(semaphore, 1, n, timeUnit);
    }
}
