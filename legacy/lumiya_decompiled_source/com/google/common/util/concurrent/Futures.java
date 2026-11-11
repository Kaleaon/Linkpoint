package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.AbstractFuture;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
@Beta
@GwtCompatible(emulated = true)
/* loaded from: classes.dex */
public final class Futures extends GwtFuturesCatchingSpecialization {
    private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>() { // from class: com.google.common.util.concurrent.Futures.4
        @Override // com.google.common.util.concurrent.AsyncFunction
        public ListenableFuture<Object> apply(ListenableFuture<Object> listenableFuture) {
            return listenableFuture;
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.google.common.util.concurrent.Futures$2  reason: invalid class name */
    /* loaded from: classes.dex */
    public static class AnonymousClass2 implements Executor {
        volatile boolean thrownFromDelegate = true;
        final /* synthetic */ Executor val$delegate;
        final /* synthetic */ AbstractFuture val$future;

        AnonymousClass2(Executor executor, AbstractFuture abstractFuture) {
            this.val$delegate = executor;
            this.val$future = abstractFuture;
        }

        @Override // java.util.concurrent.Executor
        public void execute(final Runnable runnable) {
            try {
                this.val$delegate.execute(new Runnable() { // from class: com.google.common.util.concurrent.Futures.2.1
                    @Override // java.lang.Runnable
                    public void run() {
                        AnonymousClass2.this.thrownFromDelegate = false;
                        runnable.run();
                    }
                });
            } catch (RejectedExecutionException e) {
                if (this.thrownFromDelegate) {
                    this.val$future.setException(e);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private static abstract class AbstractCatchingFuture<V, X extends Throwable, F> extends AbstractFuture.TrustedFuture<V> implements Runnable {
        @Nullable
        Class<X> exceptionType;
        @Nullable
        F fallback;
        @Nullable
        ListenableFuture<? extends V> inputFuture;

        AbstractCatchingFuture(ListenableFuture<? extends V> listenableFuture, Class<X> cls, F f) {
            this.inputFuture = (ListenableFuture) Preconditions.checkNotNull(listenableFuture);
            this.exceptionType = (Class) Preconditions.checkNotNull(cls);
            this.fallback = (F) Preconditions.checkNotNull(f);
        }

        abstract void doFallback(F f, X x) throws Exception;

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.util.concurrent.AbstractFuture
        public final void done() {
            maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
            this.exceptionType = null;
            this.fallback = null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:22:0x003a A[Catch: Throwable -> 0x0042, TryCatch #0 {Throwable -> 0x0042, blocks: (B:20:0x0034, B:22:0x003a, B:24:0x003e), top: B:30:0x0034 }] */
        /* JADX WARN: Removed duplicated region for block: B:24:0x003e A[Catch: Throwable -> 0x0042, TRY_LEAVE, TryCatch #0 {Throwable -> 0x0042, blocks: (B:20:0x0034, B:22:0x003a, B:24:0x003e), top: B:30:0x0034 }] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final void run() {
            /*
                r8 = this;
                r1 = 1
                r0 = 0
                r7 = 0
                com.google.common.util.concurrent.ListenableFuture<? extends V> r4 = r8.inputFuture
                java.lang.Class<X extends java.lang.Throwable> r5 = r8.exceptionType
                F r6 = r8.fallback
                if (r4 == 0) goto L28
                r3 = r0
            Lc:
                if (r5 == 0) goto L2a
                r2 = r0
            Lf:
                r2 = r2 | r3
                if (r6 == 0) goto L2c
            L12:
                r0 = r0 | r2
                boolean r1 = r8.isCancelled()
                r0 = r0 | r1
                if (r0 != 0) goto L2e
                r8.inputFuture = r7
                r8.exceptionType = r7
                r8.fallback = r7
                java.lang.Object r0 = com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly(r4)     // Catch: java.util.concurrent.ExecutionException -> L2f java.lang.Throwable -> L47
                r8.set(r0)     // Catch: java.util.concurrent.ExecutionException -> L2f java.lang.Throwable -> L47
                return
            L28:
                r3 = r1
                goto Lc
            L2a:
                r2 = r1
                goto Lf
            L2c:
                r0 = r1
                goto L12
            L2e:
                return
            L2f:
                r0 = move-exception
                java.lang.Throwable r0 = r0.getCause()
            L34:
                boolean r1 = com.google.common.util.concurrent.Platform.isInstanceOfThrowableClass(r0, r5)     // Catch: java.lang.Throwable -> L42
                if (r1 != 0) goto L3e
                r8.setException(r0)     // Catch: java.lang.Throwable -> L42
            L3d:
                return
            L3e:
                r8.doFallback(r6, r0)     // Catch: java.lang.Throwable -> L42
                goto L3d
            L42:
                r0 = move-exception
                r8.setException(r0)
                goto L3d
            L47:
                r0 = move-exception
                goto L34
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Futures.AbstractCatchingFuture.run():void");
        }
    }

    /* loaded from: classes.dex */
    private static abstract class AbstractChainingFuture<I, O, F> extends AbstractFuture.TrustedFuture<O> implements Runnable {
        @Nullable
        F function;
        @Nullable
        ListenableFuture<? extends I> inputFuture;

        AbstractChainingFuture(ListenableFuture<? extends I> listenableFuture, F f) {
            this.inputFuture = (ListenableFuture) Preconditions.checkNotNull(listenableFuture);
            this.function = (F) Preconditions.checkNotNull(f);
        }

        abstract void doTransform(F f, I i) throws Exception;

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.util.concurrent.AbstractFuture
        public final void done() {
            maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
            this.function = null;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.lang.Runnable
        public final void run() {
            try {
                ListenableFuture<? extends I> listenableFuture = this.inputFuture;
                F f = this.function;
                if (!(f == null) && !((listenableFuture == null) | isCancelled())) {
                    this.inputFuture = null;
                    this.function = null;
                    try {
                        doTransform(f, Uninterruptibles.getUninterruptibly(listenableFuture));
                    } catch (CancellationException e) {
                        cancel(false);
                    } catch (ExecutionException e2) {
                        setException(e2.getCause());
                    }
                }
            } catch (UndeclaredThrowableException e3) {
                setException(e3.getCause());
            } catch (Throwable th) {
                setException(th);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class AsyncCatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>> {
        AsyncCatchingFuture(ListenableFuture<? extends V> listenableFuture, Class<X> cls, AsyncFunction<? super X, ? extends V> asyncFunction) {
            super(listenableFuture, cls, asyncFunction);
        }

        void doFallback(AsyncFunction<? super X, ? extends V> asyncFunction, X x) throws Exception {
            ListenableFuture<? extends V> apply = asyncFunction.apply(x);
            Preconditions.checkNotNull(apply, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            setFuture(apply);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.common.util.concurrent.Futures.AbstractCatchingFuture
        /* bridge */ /* synthetic */ void doFallback(Object obj, Throwable th) throws Exception {
            doFallback((AsyncFunction<? super AsyncFunction<? super X, ? extends V>, ? extends V>) obj, (AsyncFunction<? super X, ? extends V>) th);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class AsyncChainingFuture<I, O> extends AbstractChainingFuture<I, O, AsyncFunction<? super I, ? extends O>> {
        AsyncChainingFuture(ListenableFuture<? extends I> listenableFuture, AsyncFunction<? super I, ? extends O> asyncFunction) {
            super(listenableFuture, asyncFunction);
        }

        /* JADX WARN: Multi-variable type inference failed */
        void doTransform(AsyncFunction<? super I, ? extends O> asyncFunction, I i) throws Exception {
            ListenableFuture listenableFuture = (ListenableFuture<? extends O>) asyncFunction.apply(i);
            Preconditions.checkNotNull(listenableFuture, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            setFuture(listenableFuture);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.common.util.concurrent.Futures.AbstractChainingFuture
        /* bridge */ /* synthetic */ void doTransform(Object obj, Object obj2) throws Exception {
            doTransform((AsyncFunction<? super AsyncFunction<? super I, ? extends O>, ? extends O>) obj, (AsyncFunction<? super I, ? extends O>) obj2);
        }
    }

    /* loaded from: classes.dex */
    static final class CatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>> {
        CatchingFuture(ListenableFuture<? extends V> listenableFuture, Class<X> cls, Function<? super X, ? extends V> function) {
            super(listenableFuture, cls, function);
        }

        void doFallback(Function<? super X, ? extends V> function, X x) throws Exception {
            set(function.apply(x));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.common.util.concurrent.Futures.AbstractCatchingFuture
        /* bridge */ /* synthetic */ void doFallback(Object obj, Throwable th) throws Exception {
            doFallback((Function<? super Function<? super X, ? extends V>, ? extends V>) obj, (Function<? super X, ? extends V>) th);
        }
    }

    /* loaded from: classes.dex */
    private static final class ChainingFuture<I, O> extends AbstractChainingFuture<I, O, Function<? super I, ? extends O>> {
        ChainingFuture(ListenableFuture<? extends I> listenableFuture, Function<? super I, ? extends O> function) {
            super(listenableFuture, function);
        }

        /* JADX WARN: Multi-variable type inference failed */
        void doTransform(Function<? super I, ? extends O> function, I i) {
            set(function.apply(i));
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.google.common.util.concurrent.Futures.AbstractChainingFuture
        /* bridge */ /* synthetic */ void doTransform(Object obj, Object obj2) throws Exception {
            doTransform((Function<? super Function<? super I, ? extends O>, ? extends O>) obj, (Function<? super I, ? extends O>) obj2);
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static class ImmediateCancelledFuture<V> extends ImmediateFuture<V> {
        private final CancellationException thrown;

        ImmediateCancelledFuture() {
            super();
            this.thrown = new CancellationException("Immediate cancelled future.");
        }

        @Override // com.google.common.util.concurrent.Futures.ImmediateFuture, java.util.concurrent.Future
        public V get() {
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
        }

        @Override // com.google.common.util.concurrent.Futures.ImmediateFuture, java.util.concurrent.Future
        public boolean isCancelled() {
            return true;
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static class ImmediateFailedCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
        private final X thrown;

        ImmediateFailedCheckedFuture(X x) {
            super();
            this.thrown = x;
        }

        @Override // com.google.common.util.concurrent.CheckedFuture
        public V checkedGet() throws Exception {
            throw this.thrown;
        }

        @Override // com.google.common.util.concurrent.CheckedFuture
        public V checkedGet(long j, TimeUnit timeUnit) throws Exception {
            Preconditions.checkNotNull(timeUnit);
            throw this.thrown;
        }

        @Override // com.google.common.util.concurrent.Futures.ImmediateFuture, java.util.concurrent.Future
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }

    /* loaded from: classes.dex */
    private static class ImmediateFailedFuture<V> extends ImmediateFuture<V> {
        private final Throwable thrown;

        ImmediateFailedFuture(Throwable th) {
            super();
            this.thrown = th;
        }

        @Override // com.google.common.util.concurrent.Futures.ImmediateFuture, java.util.concurrent.Future
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }

    /* loaded from: classes.dex */
    private static abstract class ImmediateFuture<V> implements ListenableFuture<V> {
        private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());

        private ImmediateFuture() {
        }

        @Override // com.google.common.util.concurrent.ListenableFuture
        public void addListener(Runnable runnable, Executor executor) {
            Preconditions.checkNotNull(runnable, "Runnable was null.");
            Preconditions.checkNotNull(executor, "Executor was null.");
            try {
                executor.execute(runnable);
            } catch (RuntimeException e) {
                log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, (Throwable) e);
            }
        }

        @Override // java.util.concurrent.Future
        public boolean cancel(boolean z) {
            return false;
        }

        @Override // java.util.concurrent.Future
        public abstract V get() throws ExecutionException;

        @Override // java.util.concurrent.Future
        public V get(long j, TimeUnit timeUnit) throws ExecutionException {
            Preconditions.checkNotNull(timeUnit);
            return get();
        }

        @Override // java.util.concurrent.Future
        public boolean isCancelled() {
            return false;
        }

        @Override // java.util.concurrent.Future
        public boolean isDone() {
            return true;
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
        @Nullable
        private final V value;

        ImmediateSuccessfulCheckedFuture(@Nullable V v) {
            super();
            this.value = v;
        }

        @Override // com.google.common.util.concurrent.CheckedFuture
        public V checkedGet() {
            return this.value;
        }

        @Override // com.google.common.util.concurrent.CheckedFuture
        public V checkedGet(long j, TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            return this.value;
        }

        @Override // com.google.common.util.concurrent.Futures.ImmediateFuture, java.util.concurrent.Future
        public V get() {
            return this.value;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
        static final ImmediateSuccessfulFuture<Object> NULL = new ImmediateSuccessfulFuture<>(null);
        @Nullable
        private final V value;

        ImmediateSuccessfulFuture(@Nullable V v) {
            super();
            this.value = v;
        }

        @Override // com.google.common.util.concurrent.Futures.ImmediateFuture, java.util.concurrent.Future
        public V get() {
            return this.value;
        }
    }

    /* loaded from: classes.dex */
    private static final class ListFuture<V> extends CollectionFuture<V, List<V>> {

        /* loaded from: classes.dex */
        private final class ListFutureRunningState extends CollectionFuture<V, List<V>>.CollectionFutureRunningState {
            ListFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> immutableCollection, boolean z) {
                super(immutableCollection, z);
            }

            @Override // com.google.common.util.concurrent.CollectionFuture.CollectionFutureRunningState
            public List<V> combine(List<Optional<V>> list) {
                ArrayList newArrayList = Lists.newArrayList();
                Iterator<Optional<V>> it = list.iterator();
                while (it.hasNext()) {
                    Optional<V> next = it.next();
                    newArrayList.add(next == null ? null : next.orNull());
                }
                return Collections.unmodifiableList(newArrayList);
            }
        }

        ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> immutableCollection, boolean z) {
            init(new ListFutureRunningState(immutableCollection, z));
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X> {
        final Function<? super Exception, X> mapper;

        MappingCheckedFuture(ListenableFuture<V> listenableFuture, Function<? super Exception, X> function) {
            super(listenableFuture);
            this.mapper = (Function) Preconditions.checkNotNull(function);
        }

        @Override // com.google.common.util.concurrent.AbstractCheckedFuture
        protected X mapException(Exception exc) {
            return this.mapper.apply(exc);
        }
    }

    @GwtIncompatible("TODO")
    /* loaded from: classes.dex */
    private static final class NonCancellationPropagatingFuture<V> extends AbstractFuture.TrustedFuture<V> {
        NonCancellationPropagatingFuture(final ListenableFuture<V> listenableFuture) {
            listenableFuture.addListener(new Runnable() { // from class: com.google.common.util.concurrent.Futures.NonCancellationPropagatingFuture.1
                @Override // java.lang.Runnable
                public void run() {
                    NonCancellationPropagatingFuture.this.setFuture(listenableFuture);
                }
            }, MoreExecutors.directExecutor());
        }
    }

    /* loaded from: classes.dex */
    private static final class TimeoutFuture<V> extends AbstractFuture.TrustedFuture<V> {
        @Nullable
        ListenableFuture<V> delegateRef;
        @Nullable
        Future<?> timer;

        /* loaded from: classes.dex */
        private static final class Fire<V> implements Runnable {
            @Nullable
            TimeoutFuture<V> timeoutFutureRef;

            Fire(TimeoutFuture<V> timeoutFuture) {
                this.timeoutFutureRef = timeoutFuture;
            }

            @Override // java.lang.Runnable
            public void run() {
                ListenableFuture<V> listenableFuture;
                TimeoutFuture<V> timeoutFuture = this.timeoutFutureRef;
                if (timeoutFuture == null || (listenableFuture = timeoutFuture.delegateRef) == null) {
                    return;
                }
                this.timeoutFutureRef = null;
                if (listenableFuture.isDone()) {
                    timeoutFuture.setFuture(listenableFuture);
                    return;
                }
                try {
                    timeoutFuture.setException(new TimeoutException("Future timed out: " + listenableFuture));
                } finally {
                    listenableFuture.cancel(true);
                }
            }
        }

        TimeoutFuture(ListenableFuture<V> listenableFuture) {
            this.delegateRef = (ListenableFuture) Preconditions.checkNotNull(listenableFuture);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.util.concurrent.AbstractFuture
        public void done() {
            maybePropagateCancellation(this.delegateRef);
            Future<?> future = this.timer;
            if (future != null) {
                future.cancel(false);
            }
            this.delegateRef = null;
            this.timer = null;
        }
    }

    private Futures() {
    }

    public static <V> void addCallback(ListenableFuture<V> listenableFuture, FutureCallback<? super V> futureCallback) {
        addCallback(listenableFuture, futureCallback, MoreExecutors.directExecutor());
    }

    public static <V> void addCallback(final ListenableFuture<V> listenableFuture, final FutureCallback<? super V> futureCallback, Executor executor) {
        Preconditions.checkNotNull(futureCallback);
        listenableFuture.addListener(new Runnable() { // from class: com.google.common.util.concurrent.Futures.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    futureCallback.onSuccess(Uninterruptibles.getUninterruptibly(ListenableFuture.this));
                } catch (Error e) {
                    futureCallback.onFailure(e);
                } catch (RuntimeException e2) {
                    futureCallback.onFailure(e2);
                } catch (ExecutionException e3) {
                    futureCallback.onFailure(e3.getCause());
                }
            }
        }, executor);
    }

    @Beta
    @CheckReturnValue
    public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return new ListFuture(ImmutableList.copyOf(iterable), true);
    }

    @SafeVarargs
    @Beta
    @CheckReturnValue
    public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... listenableFutureArr) {
        return new ListFuture(ImmutableList.copyOf(listenableFutureArr), true);
    }

    @Deprecated
    static <V> AsyncFunction<Throwable, V> asAsyncFunction(final FutureFallback<V> futureFallback) {
        Preconditions.checkNotNull(futureFallback);
        return new AsyncFunction<Throwable, V>() { // from class: com.google.common.util.concurrent.Futures.1
            @Override // com.google.common.util.concurrent.AsyncFunction
            public ListenableFuture<V> apply(Throwable th) throws Exception {
                return (ListenableFuture) Preconditions.checkNotNull(FutureFallback.this.create(th), "FutureFallback.create returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            }
        };
    }

    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    @CheckReturnValue
    public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> listenableFuture, Class<X> cls, Function<? super X, ? extends V> function) {
        CatchingFuture catchingFuture = new CatchingFuture(listenableFuture, cls, function);
        listenableFuture.addListener(catchingFuture, MoreExecutors.directExecutor());
        return catchingFuture;
    }

    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    @CheckReturnValue
    public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> listenableFuture, Class<X> cls, Function<? super X, ? extends V> function, Executor executor) {
        CatchingFuture catchingFuture = new CatchingFuture(listenableFuture, cls, function);
        listenableFuture.addListener(catchingFuture, rejectionPropagatingExecutor(executor, catchingFuture));
        return catchingFuture;
    }

    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> listenableFuture, Class<X> cls, AsyncFunction<? super X, ? extends V> asyncFunction) {
        AsyncCatchingFuture asyncCatchingFuture = new AsyncCatchingFuture(listenableFuture, cls, asyncFunction);
        listenableFuture.addListener(asyncCatchingFuture, MoreExecutors.directExecutor());
        return asyncCatchingFuture;
    }

    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(ListenableFuture<? extends V> listenableFuture, Class<X> cls, AsyncFunction<? super X, ? extends V> asyncFunction, Executor executor) {
        AsyncCatchingFuture asyncCatchingFuture = new AsyncCatchingFuture(listenableFuture, cls, asyncFunction);
        listenableFuture.addListener(asyncCatchingFuture, rejectionPropagatingExecutor(executor, asyncCatchingFuture));
        return asyncCatchingFuture;
    }

    @CheckReturnValue
    public static <V> ListenableFuture<V> dereference(ListenableFuture<? extends ListenableFuture<? extends V>> listenableFuture) {
        return transformAsync(listenableFuture, DEREFERENCER);
    }

    @GwtIncompatible("reflection")
    @Deprecated
    public static <V, X extends Exception> V get(Future<V> future, long j, TimeUnit timeUnit, Class<X> cls) throws Exception {
        return (V) getChecked(future, cls, j, timeUnit);
    }

    @GwtIncompatible("reflection")
    @Deprecated
    public static <V, X extends Exception> V get(Future<V> future, Class<X> cls) throws Exception {
        return (V) getChecked(future, cls);
    }

    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> cls) throws Exception {
        return (V) FuturesGetChecked.getChecked(future, cls);
    }

    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> cls, long j, TimeUnit timeUnit) throws Exception {
        return (V) FuturesGetChecked.getChecked(future, cls, j, timeUnit);
    }

    @GwtIncompatible("TODO")
    public static <V> V getUnchecked(Future<V> future) {
        Preconditions.checkNotNull(future);
        try {
            return (V) Uninterruptibles.getUninterruptibly(future);
        } catch (ExecutionException e) {
            wrapAndThrowUnchecked(e.getCause());
            throw new AssertionError();
        }
    }

    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <V> ListenableFuture<V> immediateCancelledFuture() {
        return new ImmediateCancelledFuture();
    }

    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V v) {
        return new ImmediateSuccessfulCheckedFuture(v);
    }

    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(X x) {
        Preconditions.checkNotNull(x);
        return new ImmediateFailedCheckedFuture(x);
    }

    @CheckReturnValue
    public static <V> ListenableFuture<V> immediateFailedFuture(Throwable th) {
        Preconditions.checkNotNull(th);
        return new ImmediateFailedFuture(th);
    }

    @CheckReturnValue
    public static <V> ListenableFuture<V> immediateFuture(@Nullable V v) {
        return v != null ? new ImmediateSuccessfulFuture(v) : ImmediateSuccessfulFuture.NULL;
    }

    @Beta
    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> iterable) {
        final ConcurrentLinkedQueue newConcurrentLinkedQueue = Queues.newConcurrentLinkedQueue();
        ImmutableList.Builder builder = ImmutableList.builder();
        SerializingExecutor serializingExecutor = new SerializingExecutor(MoreExecutors.directExecutor());
        for (final ListenableFuture<? extends T> listenableFuture : iterable) {
            SettableFuture create = SettableFuture.create();
            newConcurrentLinkedQueue.add(create);
            listenableFuture.addListener(new Runnable() { // from class: com.google.common.util.concurrent.Futures.5
                @Override // java.lang.Runnable
                public void run() {
                    ((SettableFuture) newConcurrentLinkedQueue.remove()).setFuture(listenableFuture);
                }
            }, serializingExecutor);
            builder.add((ImmutableList.Builder) create);
        }
        return builder.build();
    }

    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <I, O> Future<O> lazyTransform(final Future<I> future, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(future);
        Preconditions.checkNotNull(function);
        return new Future<O>() { // from class: com.google.common.util.concurrent.Futures.3
            private O applyTransformation(I i) throws ExecutionException {
                try {
                    return (O) function.apply(i);
                } catch (Throwable th) {
                    throw new ExecutionException(th);
                }
            }

            @Override // java.util.concurrent.Future
            public boolean cancel(boolean z) {
                return future.cancel(z);
            }

            @Override // java.util.concurrent.Future
            public O get() throws InterruptedException, ExecutionException {
                return applyTransformation(future.get());
            }

            @Override // java.util.concurrent.Future
            public O get(long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return applyTransformation(future.get(j, timeUnit));
            }

            @Override // java.util.concurrent.Future
            public boolean isCancelled() {
                return future.isCancelled();
            }

            @Override // java.util.concurrent.Future
            public boolean isDone() {
                return future.isDone();
            }
        };
    }

    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> listenableFuture, Function<? super Exception, X> function) {
        return new MappingCheckedFuture((ListenableFuture) Preconditions.checkNotNull(listenableFuture), function);
    }

    @GwtIncompatible("TODO")
    @CheckReturnValue
    public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> listenableFuture) {
        return new NonCancellationPropagatingFuture(listenableFuture);
    }

    private static Executor rejectionPropagatingExecutor(Executor executor, AbstractFuture<?> abstractFuture) {
        Preconditions.checkNotNull(executor);
        return executor != MoreExecutors.directExecutor() ? new AnonymousClass2(executor, abstractFuture) : executor;
    }

    @Beta
    @CheckReturnValue
    public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return new ListFuture(ImmutableList.copyOf(iterable), false);
    }

    @SafeVarargs
    @Beta
    @CheckReturnValue
    public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... listenableFutureArr) {
        return new ListFuture(ImmutableList.copyOf(listenableFutureArr), false);
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenableFuture, Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        ChainingFuture chainingFuture = new ChainingFuture(listenableFuture, function);
        listenableFuture.addListener(chainingFuture, MoreExecutors.directExecutor());
        return chainingFuture;
    }

    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenableFuture, Function<? super I, ? extends O> function, Executor executor) {
        Preconditions.checkNotNull(function);
        ChainingFuture chainingFuture = new ChainingFuture(listenableFuture, function);
        listenableFuture.addListener(chainingFuture, rejectionPropagatingExecutor(executor, chainingFuture));
        return chainingFuture;
    }

    @Deprecated
    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenableFuture, AsyncFunction<? super I, ? extends O> asyncFunction) {
        return transformAsync(listenableFuture, asyncFunction);
    }

    @Deprecated
    public static <I, O> ListenableFuture<O> transform(ListenableFuture<I> listenableFuture, AsyncFunction<? super I, ? extends O> asyncFunction, Executor executor) {
        return transformAsync(listenableFuture, asyncFunction, executor);
    }

    public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> listenableFuture, AsyncFunction<? super I, ? extends O> asyncFunction) {
        AsyncChainingFuture asyncChainingFuture = new AsyncChainingFuture(listenableFuture, asyncFunction);
        listenableFuture.addListener(asyncChainingFuture, MoreExecutors.directExecutor());
        return asyncChainingFuture;
    }

    public static <I, O> ListenableFuture<O> transformAsync(ListenableFuture<I> listenableFuture, AsyncFunction<? super I, ? extends O> asyncFunction, Executor executor) {
        Preconditions.checkNotNull(executor);
        AsyncChainingFuture asyncChainingFuture = new AsyncChainingFuture(listenableFuture, asyncFunction);
        listenableFuture.addListener(asyncChainingFuture, rejectionPropagatingExecutor(executor, asyncChainingFuture));
        return asyncChainingFuture;
    }

    @CheckReturnValue
    @Deprecated
    public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> listenableFuture, FutureFallback<? extends V> futureFallback) {
        return withFallback(listenableFuture, futureFallback, MoreExecutors.directExecutor());
    }

    @CheckReturnValue
    @Deprecated
    public static <V> ListenableFuture<V> withFallback(ListenableFuture<? extends V> listenableFuture, FutureFallback<? extends V> futureFallback, Executor executor) {
        return catchingAsync(listenableFuture, Throwable.class, asAsyncFunction(futureFallback), executor);
    }

    @GwtIncompatible("java.util.concurrent.ScheduledExecutorService")
    @CheckReturnValue
    public static <V> ListenableFuture<V> withTimeout(ListenableFuture<V> listenableFuture, long j, TimeUnit timeUnit, ScheduledExecutorService scheduledExecutorService) {
        TimeoutFuture timeoutFuture = new TimeoutFuture(listenableFuture);
        TimeoutFuture.Fire fire = new TimeoutFuture.Fire(timeoutFuture);
        timeoutFuture.timer = scheduledExecutorService.schedule(fire, j, timeUnit);
        listenableFuture.addListener(fire, MoreExecutors.directExecutor());
        return timeoutFuture;
    }

    @GwtIncompatible("TODO")
    private static void wrapAndThrowUnchecked(Throwable th) {
        if (!(th instanceof Error)) {
            throw new UncheckedExecutionException(th);
        }
        throw new ExecutionError((Error) th);
    }
}
