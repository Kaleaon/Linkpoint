package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@GwtCompatible(emulated = true)
abstract class AggregateFutureState {
    private static final AtomicIntegerFieldUpdater<AggregateFutureState> REMAINING_COUNT_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining");
    private static final AtomicReferenceFieldUpdater<AggregateFutureState, Set<Throwable>> SEEN_EXCEPTIONS_UDPATER = AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions");
    private volatile int remaining;
    private volatile Set<Throwable> seenExceptions = null;

    AggregateFutureState(int i) {
        this.remaining = i;
    }

    /* access modifiers changed from: package-private */
    public abstract void addInitialException(Set<Throwable> set);

    /* access modifiers changed from: package-private */
    public final int decrementRemainingAndGet() {
        return REMAINING_COUNT_UPDATER.decrementAndGet(this);
    }

    /* access modifiers changed from: package-private */
    public final Set<Throwable> getOrInitSeenExceptions() {
        Set<Throwable> set = this.seenExceptions;
        if (set != null) {
            return set;
        }
        Set newConcurrentHashSet = Sets.newConcurrentHashSet();
        addInitialException(newConcurrentHashSet);
        SEEN_EXCEPTIONS_UDPATER.compareAndSet(this, (Object) null, newConcurrentHashSet);
        return this.seenExceptions;
    }
}
