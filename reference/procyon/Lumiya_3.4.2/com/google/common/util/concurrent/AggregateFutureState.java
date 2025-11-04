// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
abstract class AggregateFutureState
{
    private static final AtomicIntegerFieldUpdater<AggregateFutureState> REMAINING_COUNT_UPDATER;
    private static final AtomicReferenceFieldUpdater<AggregateFutureState, Set<Throwable>> SEEN_EXCEPTIONS_UDPATER;
    private volatile int remaining;
    private volatile Set<Throwable> seenExceptions;
    
    static {
        SEEN_EXCEPTIONS_UDPATER = AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions");
        REMAINING_COUNT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining");
    }
    
    AggregateFutureState(final int remaining) {
        this.seenExceptions = null;
        this.remaining = remaining;
    }
    
    abstract void addInitialException(final Set<Throwable> p0);
    
    final int decrementRemainingAndGet() {
        return AggregateFutureState.REMAINING_COUNT_UPDATER.decrementAndGet(this);
    }
    
    final Set<Throwable> getOrInitSeenExceptions() {
        Set<Throwable> set = this.seenExceptions;
        if (set == null) {
            final Set<Object> concurrentHashSet = (Set<Object>)Sets.newConcurrentHashSet();
            this.addInitialException((Set<Throwable>)concurrentHashSet);
            AggregateFutureState.SEEN_EXCEPTIONS_UDPATER.compareAndSet(this, null, (Set<Throwable>)concurrentHashSet);
            set = this.seenExceptions;
        }
        return set;
    }
}
