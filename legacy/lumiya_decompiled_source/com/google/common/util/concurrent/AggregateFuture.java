package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.util.concurrent.AbstractFuture;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
/* loaded from: classes.dex */
public abstract class AggregateFuture<InputT, OutputT> extends AbstractFuture.TrustedFuture<OutputT> {
    private static final Logger logger = Logger.getLogger(AggregateFuture.class.getName());
    private AggregateFuture<InputT, OutputT>.RunningState runningState;

    /* loaded from: classes.dex */
    abstract class RunningState extends AggregateFutureState implements Runnable {
        private final boolean allMustSucceed;
        private final boolean collectsValues;
        private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;

        /* JADX INFO: Access modifiers changed from: package-private */
        public RunningState(ImmutableCollection<? extends ListenableFuture<? extends InputT>> immutableCollection, boolean z, boolean z2) {
            super(immutableCollection.size());
            this.futures = (ImmutableCollection) Preconditions.checkNotNull(immutableCollection);
            this.allMustSucceed = z;
            this.collectsValues = z2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void decrementCountAndMaybeComplete() {
            int decrementRemainingAndGet = decrementRemainingAndGet();
            Preconditions.checkState(decrementRemainingAndGet >= 0, "Less than 0 remaining futures");
            if (decrementRemainingAndGet != 0) {
                return;
            }
            processCompleted();
        }

        private void handleException(Throwable th) {
            boolean exception;
            boolean z;
            Preconditions.checkNotNull(th);
            if (this.allMustSucceed) {
                exception = AggregateFuture.this.setException(th);
                if (exception) {
                    releaseResourcesAfterFailure();
                    z = true;
                } else {
                    z = AggregateFuture.addCausalChain(getOrInitSeenExceptions(), th);
                }
            } else {
                z = true;
                exception = false;
            }
            if (!(z & this.allMustSucceed & (exception ? false : true)) && !(th instanceof Error)) {
                return;
            }
            AggregateFuture.logger.log(Level.SEVERE, !(th instanceof Error) ? "Got more than one input Future failure. Logging failures after the first" : "Input Future failed with Error", th);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Multi-variable type inference failed */
        public void handleOneInputDone(int i, Future<? extends InputT> future) {
            Preconditions.checkState(this.allMustSucceed || !AggregateFuture.this.isDone() || AggregateFuture.this.isCancelled(), "Future was done before all dependencies completed");
            try {
                Preconditions.checkState(future.isDone(), "Tried to set value from future which is not done");
                if (this.allMustSucceed) {
                    if (future.isCancelled()) {
                        AggregateFuture.super.cancel(false);
                    } else {
                        Object uninterruptibly = Uninterruptibles.getUninterruptibly(future);
                        if (this.collectsValues) {
                            collectOneValue(this.allMustSucceed, i, uninterruptibly);
                        }
                    }
                } else if (this.collectsValues && !future.isCancelled()) {
                    collectOneValue(this.allMustSucceed, i, Uninterruptibles.getUninterruptibly(future));
                }
            } catch (ExecutionException e) {
                handleException(e.getCause());
            } catch (Throwable th) {
                handleException(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void init() {
            if (this.futures.isEmpty()) {
                handleAllCompleted();
            } else if (!this.allMustSucceed) {
                Iterator it = this.futures.iterator();
                while (it.hasNext()) {
                    ((ListenableFuture) it.next()).addListener(this, MoreExecutors.directExecutor());
                }
            } else {
                Iterator it2 = this.futures.iterator();
                final int i = 0;
                while (it2.hasNext()) {
                    final ListenableFuture listenableFuture = (ListenableFuture) it2.next();
                    listenableFuture.addListener(new Runnable() { // from class: com.google.common.util.concurrent.AggregateFuture.RunningState.1
                        @Override // java.lang.Runnable
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
            if ((!this.allMustSucceed) & this.collectsValues) {
                Iterator it = this.futures.iterator();
                int i = 0;
                while (it.hasNext()) {
                    handleOneInputDone(i, (ListenableFuture) it.next());
                    i++;
                }
            }
            handleAllCompleted();
        }

        @Override // com.google.common.util.concurrent.AggregateFutureState
        final void addInitialException(Set<Throwable> set) {
            if (AggregateFuture.this.isCancelled()) {
                return;
            }
            AggregateFuture.addCausalChain(set, AggregateFuture.this.trustedGetException());
        }

        abstract void collectOneValue(boolean z, int i, @Nullable InputT inputt);

        abstract void handleAllCompleted();

        void interruptTask() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void releaseResourcesAfterFailure() {
            this.futures = null;
        }

        @Override // java.lang.Runnable
        public final void run() {
            decrementCountAndMaybeComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean addCausalChain(Set<Throwable> set, Throwable th) {
        while (th != null) {
            if (!set.add(th)) {
                return false;
            }
            th = th.getCause();
        }
        return true;
    }

    @Override // com.google.common.util.concurrent.AbstractFuture, java.util.concurrent.Future
    public final boolean cancel(boolean z) {
        AggregateFuture<InputT, OutputT>.RunningState runningState = this.runningState;
        ImmutableCollection immutableCollection = runningState != null ? ((RunningState) runningState).futures : null;
        boolean cancel = super.cancel(z);
        if ((immutableCollection != null) & cancel) {
            Iterator it = immutableCollection.iterator();
            while (it.hasNext()) {
                ((ListenableFuture) it.next()).cancel(z);
            }
        }
        return cancel;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.util.concurrent.AbstractFuture
    public final void done() {
        super.done();
        this.runningState = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void init(AggregateFuture<InputT, OutputT>.RunningState runningState) {
        this.runningState = runningState;
        runningState.init();
    }

    @Override // com.google.common.util.concurrent.AbstractFuture
    @GwtIncompatible("Interruption not supported")
    protected final void interruptTask() {
        AggregateFuture<InputT, OutputT>.RunningState runningState = this.runningState;
        if (runningState == null) {
            return;
        }
        runningState.interruptTask();
    }
}
