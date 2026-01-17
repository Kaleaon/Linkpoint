package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AbstractFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible
/* loaded from: classes.dex */
public class TrustedListenableFutureTask<V> extends AbstractFuture.TrustedFuture<V> implements RunnableFuture<V> {
    private TrustedListenableFutureTask<V>.TrustedFutureInterruptibleTask task;

    /* loaded from: classes.dex */
    private final class TrustedFutureInterruptibleTask extends InterruptibleTask {
        private final Callable<V> callable;

        TrustedFutureInterruptibleTask(Callable<V> callable) {
            this.callable = (Callable) Preconditions.checkNotNull(callable);
        }

        @Override // com.google.common.util.concurrent.InterruptibleTask
        void runInterruptibly() {
            if (TrustedListenableFutureTask.this.isDone()) {
                return;
            }
            try {
                TrustedListenableFutureTask.this.set(this.callable.call());
            } catch (Throwable th) {
                TrustedListenableFutureTask.this.setException(th);
            }
        }

        @Override // com.google.common.util.concurrent.InterruptibleTask
        boolean wasInterrupted() {
            return TrustedListenableFutureTask.this.wasInterrupted();
        }
    }

    TrustedListenableFutureTask(Callable<V> callable) {
        this.task = new TrustedFutureInterruptibleTask(callable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <V> TrustedListenableFutureTask<V> create(Runnable runnable, @Nullable V v) {
        return new TrustedListenableFutureTask<>(Executors.callable(runnable, v));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <V> TrustedListenableFutureTask<V> create(Callable<V> callable) {
        return new TrustedListenableFutureTask<>(callable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.util.concurrent.AbstractFuture
    public final void done() {
        super.done();
        this.task = null;
    }

    @Override // com.google.common.util.concurrent.AbstractFuture
    @GwtIncompatible("Interruption not supported")
    protected final void interruptTask() {
        TrustedListenableFutureTask<V>.TrustedFutureInterruptibleTask trustedFutureInterruptibleTask = this.task;
        if (trustedFutureInterruptibleTask == null) {
            return;
        }
        trustedFutureInterruptibleTask.interruptTask();
    }

    @Override // java.util.concurrent.RunnableFuture, java.lang.Runnable
    public void run() {
        TrustedListenableFutureTask<V>.TrustedFutureInterruptibleTask trustedFutureInterruptibleTask = this.task;
        if (trustedFutureInterruptibleTask == null) {
            return;
        }
        trustedFutureInterruptibleTask.run();
    }
}
