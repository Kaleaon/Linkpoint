// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Queue;
import java.util.Deque;
import java.util.concurrent.SynchronousQueue;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayDeque;
import java.util.concurrent.ArrayBlockingQueue;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;

public final class Queues
{
    private Queues() {
    }
    
    @Beta
    public static <E> int drain(final BlockingQueue<E> blockingQueue, final Collection<? super E> collection, final int n, long nanos, final TimeUnit timeUnit) throws InterruptedException {
        Preconditions.checkNotNull(collection);
        final long nanoTime = System.nanoTime();
        nanos = timeUnit.toNanos(nanos);
        int i;
        int n2;
        for (i = 0; i < n; i = n2 + 1) {
            n2 = i + blockingQueue.drainTo(collection, n - i);
            if ((i = n2) < n) {
                final E poll = blockingQueue.poll(nanos + nanoTime - System.nanoTime(), TimeUnit.NANOSECONDS);
                i = n2;
                if (poll == null) {
                    break;
                }
                collection.add(poll);
            }
        }
        return i;
    }
    
    @Beta
    public static <E> int drainUninterruptibly(final BlockingQueue<E> blockingQueue, final Collection<? super E> collection, final int n, long nanos, final TimeUnit timeUnit) {
        int n2 = 0;
        Preconditions.checkNotNull(collection);
        final long nanoTime = System.nanoTime();
        nanos = timeUnit.toNanos(nanos);
        int n3 = 0;
    Label_0033_Outer:
        while (true) {
        Label_0033:
            while (true) {
                Label_0141: {
                    Label_0041: {
                        if (n3 < n) {
                            break Label_0041;
                        }
                        final int n4 = n2;
                        if (n4 == 0) {
                            break;
                        }
                        break Label_0141;
                    }
                    int n4 = n2;
                    try {
                        final int n5 = n3 += blockingQueue.drainTo((Collection<? super Object>)collection, n - n3);
                        if (n5 < n) {
                            while (true) {
                                n4 = n2;
                                try {
                                    final Object poll = blockingQueue.poll(nanoTime + nanos - System.nanoTime(), TimeUnit.NANOSECONDS);
                                    n4 = n2;
                                    n3 = n5;
                                    if (poll != null) {
                                        n4 = n2;
                                        collection.add(poll);
                                        n3 = n5 + 1;
                                        continue Label_0033_Outer;
                                    }
                                    continue Label_0033;
                                }
                                catch (final InterruptedException ex) {
                                    n2 = 1;
                                    continue Label_0033_Outer;
                                }
                                break;
                            }
                            Thread.currentThread().interrupt();
                            break;
                        }
                        continue Label_0033_Outer;
                    }
                    finally {
                        while (true) {
                            if (n4 == 0) {
                                break Label_0156;
                            }
                            Thread.currentThread().interrupt();
                            break Label_0156;
                            continue;
                        }
                    }
                }
                break;
            }
        }
        return n3;
    }
    
    public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(final int capacity) {
        return new ArrayBlockingQueue<E>(capacity);
    }
    
    public static <E> ArrayDeque<E> newArrayDeque() {
        return new ArrayDeque<E>();
    }
    
    public static <E> ArrayDeque<E> newArrayDeque(final Iterable<? extends E> iterable) {
        if (!(iterable instanceof Collection)) {
            final ArrayDeque arrayDeque = new ArrayDeque();
            Iterables.addAll((Collection<Object>)arrayDeque, iterable);
            return arrayDeque;
        }
        return new ArrayDeque<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
    }
    
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue() {
        return new ConcurrentLinkedQueue<E>();
    }
    
    public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(final Iterable<? extends E> iterable) {
        if (!(iterable instanceof Collection)) {
            final ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
            Iterables.addAll((Collection<Object>)concurrentLinkedQueue, iterable);
            return concurrentLinkedQueue;
        }
        return new ConcurrentLinkedQueue<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
    }
    
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque() {
        return new LinkedBlockingDeque<E>();
    }
    
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(final int capacity) {
        return new LinkedBlockingDeque<E>(capacity);
    }
    
    public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(final Iterable<? extends E> iterable) {
        if (!(iterable instanceof Collection)) {
            final LinkedBlockingDeque linkedBlockingDeque = new LinkedBlockingDeque();
            Iterables.addAll((Collection<Object>)linkedBlockingDeque, iterable);
            return linkedBlockingDeque;
        }
        return new LinkedBlockingDeque<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
    }
    
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue() {
        return new LinkedBlockingQueue<E>();
    }
    
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(final int capacity) {
        return new LinkedBlockingQueue<E>(capacity);
    }
    
    public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(final Iterable<? extends E> iterable) {
        if (!(iterable instanceof Collection)) {
            final LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue();
            Iterables.addAll((Collection<Object>)linkedBlockingQueue, iterable);
            return linkedBlockingQueue;
        }
        return new LinkedBlockingQueue<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
    }
    
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue() {
        return new PriorityBlockingQueue<E>();
    }
    
    public static <E extends Comparable> PriorityBlockingQueue<E> newPriorityBlockingQueue(final Iterable<? extends E> iterable) {
        if (!(iterable instanceof Collection)) {
            final PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue();
            Iterables.addAll((Collection<Object>)priorityBlockingQueue, iterable);
            return priorityBlockingQueue;
        }
        return new PriorityBlockingQueue<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
    }
    
    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue() {
        return new PriorityQueue<E>();
    }
    
    public static <E extends Comparable> PriorityQueue<E> newPriorityQueue(final Iterable<? extends E> iterable) {
        if (!(iterable instanceof Collection)) {
            final PriorityQueue priorityQueue = new PriorityQueue();
            Iterables.addAll((Collection<Object>)priorityQueue, iterable);
            return priorityQueue;
        }
        return new PriorityQueue<E>((Collection<? extends E>)Collections2.cast((Iterable<? extends E>)iterable));
    }
    
    public static <E> SynchronousQueue<E> newSynchronousQueue() {
        return new SynchronousQueue<E>();
    }
    
    public static <E> Deque<E> synchronizedDeque(final Deque<E> deque) {
        return Synchronized.deque(deque, null);
    }
    
    public static <E> Queue<E> synchronizedQueue(final Queue<E> queue) {
        return Synchronized.queue(queue, null);
    }
}
