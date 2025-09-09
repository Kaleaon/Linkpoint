package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.util.concurrent.AbstractFuture;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@GwtCompatible
abstract class AggregateFuture<InputT, OutputT> extends AbstractFuture.TrustedFuture<OutputT> {
    /* access modifiers changed from: private */
    public static final Logger logger = Logger.getLogger(AggregateFuture.class.getName());
    private AggregateFuture<InputT, OutputT>.RunningState runningState;

    abstract class RunningState extends AggregateFutureState implements Runnable {
        private final boolean allMustSucceed;
        private final boolean collectsValues;
        /* access modifiers changed from: private */
        public ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;

        RunningState(ImmutableCollection<? extends ListenableFuture<? extends InputT>> immutableCollection, boolean z, boolean z2) {
            super(immutableCollection.size());
            this.futures = (ImmutableCollection) Preconditions.checkNotNull(immutableCollection);
            this.allMustSucceed = z;
            this.collectsValues = z2;
        }

        /* access modifiers changed from: private */
        public void decrementCountAndMaybeComplete() {
            boolean z = false;
            int decrementRemainingAndGet = decrementRemainingAndGet();
            if (decrementRemainingAndGet >= 0) {
                z = true;
            }
            Preconditions.checkState(z, "Less than 0 remaining futures");
            if (decrementRemainingAndGet == 0) {
                processCompleted();
            }
        }

        private void handleException(Throwable th) {
            boolean exception;
            boolean z;
            boolean z2 = false;
            Preconditions.checkNotNull(th);
            if (!this.allMustSucceed) {
                z = true;
                exception = false;
            } else {
                exception = AggregateFuture.this.setException(th);
                if (!exception) {
                    z = AggregateFuture.addCausalChain(getOrInitSeenExceptions(), th);
                } else {
                    releaseResourcesAfterFailure();
                    z = true;
                }
            }
            boolean z3 = th instanceof Error;
            boolean z4 = this.allMustSucceed;
            if (!exception) {
                z2 = true;
            }
            if ((z & z4 & z2) || z3) {
                AggregateFuture.logger.log(Level.SEVERE, !(th instanceof Error) ? "Got more than one input Future failure. Logging failures after the first" : "Input Future failed with Error", th);
            }
        }

        /* access modifiers changed from: private */
        public void handleOneInputDone(int i, Future<? extends InputT> future) {
            boolean z = false;
            if (this.allMustSucceed || !AggregateFuture.this.isDone() || AggregateFuture.this.isCancelled()) {
                z = true;
            }
            Preconditions.checkState(z, "Future was done before all dependencies completed");
            try {
                Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
                if (!this.allMustSucceed) {
                    if (this.collectsValues) {
                        if (!future.isCancelled()) {
                            collectOneValue(this.allMustSucceed, i, Uninterruptibles.getUninterruptibly(future));
                        }
                    }
                } else if (!future.isCancelled()) {
                    Object uninterruptibly = Uninterruptibles.getUninterruptibly(future);
                    if (this.collectsValues) {
                        collectOneValue(this.allMustSucceed, i, uninterruptibly);
                    }
                } else {
                    boolean unused = AggregateFuture.super.cancel(false);
                }
            } catch (ExecutionException e) {
                handleException(e.getCause());
            } catch (Throwable th) {
                handleException(th);
            }
        }

        /* access modifiers changed from: private */
        public void init() {
            if (this.futures.isEmpty()) {
                handleAllCompleted();
            } else if (!this.allMustSucceed) {
                UnmodifiableIterator<? extends ListenableFuture<? extends InputT>> it = this.futures.iterator();
                while (it.hasNext()) {
                    ((ListenableFuture) it.next()).addListener(this, MoreExecutors.directExecutor());
                }
            } else {
                UnmodifiableIterator<? extends ListenableFuture<? extends InputT>> it2 = this.futures.iterator();
                final int i = 0;
                while (it2.hasNext()) {
                    final ListenableFuture listenableFuture = (ListenableFuture) it2.next();
                    listenableFuture.addListener(new Runnable() {
                        public void run() {
                            try {
                                RunningState.this.handleOneInputDone(i, listenableFuture);
                            } finally {
                                RunningState.this.decrementCountAndMaybeComplete();
                            }
                        }
                    }, MoreExecutors.directExecutor());
                    i++;
                }
            }
        }

        private void processCompleted() {
            if ((!this.allMustSucceed) && this.collectsValues) {
                UnmodifiableIterator<? extends ListenableFuture<? extends InputT>> it = this.futures.iterator();
                int i = 0;
                while (it.hasNext()) {
                    handleOneInputDone(i, (ListenableFuture) it.next());
                    i++;
                }
            }
            handleAllCompleted();
        }

        /* access modifiers changed from: package-private */
        public final void addInitialException(Set<Throwable> set) {
            if (!AggregateFuture.this.isCancelled()) {
                boolean unused = AggregateFuture.addCausalChain(set, AggregateFuture.this.trustedGetException());
            }
        }

        /* access modifiers changed from: package-private */
        public abstract void collectOneValue(boolean z, int i, @Nullable InputT inputt);

        /* access modifiers changed from: package-private */
        public abstract void handleAllCompleted();

        /* access modifiers changed from: package-private */
        public void interruptTask() {
        }

        /* access modifiers changed from: package-private */
        public void releaseResourcesAfterFailure() {
            this.futures = null;
        }

        public final void run() {
            decrementCountAndMaybeComplete();
        }
    }

    AggregateFuture() {
    }

    /* access modifiers changed from: private */
    public static boolean addCausalChain(Set<Throwable> set, Throwable th) {
        while (th != null) {
            if (!set.add(th)) {
                return false;
            }
            th = th.getCause();
        }
        return true;
    }

    public final boolean cancel(boolean z) {
        ImmutableCollection immutableCollection = null;
        boolean z2 = false;
        AggregateFuture<InputT, OutputT>.RunningState runningState2 = this.runningState;
        if (runningState2 != null) {
            immutableCollection = runningState2.futures;
        }
        boolean cancel = super.cancel(z);
        if (immutableCollection != null) {
            z2 = true;
        }
        if (z2 && cancel) {
            Iterator it = immutableCollection.iterator();
            while (it.hasNext()) {
                ((ListenableFuture) it.next()).cancel(z);
            }
        }
        return cancel;
    }

    /* access modifiers changed from: package-private */
    public final void done() {
        super.done();
        this.runningState = null;
    }

    /* access modifiers changed from: package-private */
    public final void init(AggregateFuture<InputT, OutputT>.RunningState runningState2) {
        this.runningState = runningState2;
        runningState2.init();
    }

    /* access modifiers changed from: protected */
    @GwtIncompatible("Interruption not supported")
    public final void interruptTask() {
        AggregateFuture<InputT, OutputT>.RunningState runningState2 = this.runningState;
        if (runningState2 != null) {
            runningState2.interruptTask();
        }
    }
}
