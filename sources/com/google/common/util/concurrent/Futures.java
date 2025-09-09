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

@GwtCompatible(emulated = true)
@Beta
public final class Futures extends GwtFuturesCatchingSpecialization {
    private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>() {
        public ListenableFuture<Object> apply(ListenableFuture<Object> listenableFuture) {
            return listenableFuture;
        }
    };

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
            this.fallback = Preconditions.checkNotNull(f);
        }

        /* access modifiers changed from: package-private */
        public abstract void doFallback(F f, X x) throws Exception;

        /* access modifiers changed from: package-private */
        public final void done() {
            maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
            this.exceptionType = null;
            this.fallback = null;
        }

        /* JADX WARNING: Removed duplicated region for block: B:22:0x003a A[Catch:{ Throwable -> 0x0042 }] */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x003e A[Catch:{ Throwable -> 0x0042 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public final void run() {
            /*
                r8 = this;
                r1 = 1
                r0 = 0
                r7 = 0
                com.google.common.util.concurrent.ListenableFuture<? extends V> r4 = r8.inputFuture
                java.lang.Class<X> r5 = r8.exceptionType
                F r6 = r8.fallback
                if (r4 == 0) goto L_0x0028
                r3 = r0
            L_0x000c:
                if (r5 == 0) goto L_0x002a
                r2 = r0
            L_0x000f:
                r2 = r2 | r3
                if (r6 == 0) goto L_0x002c
            L_0x0012:
                r0 = r0 | r2
                boolean r1 = r8.isCancelled()
                r0 = r0 | r1
                if (r0 != 0) goto L_0x002e
                r8.inputFuture = r7
                r8.exceptionType = r7
                r8.fallback = r7
                java.lang.Object r0 = com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly(r4)     // Catch:{ ExecutionException -> 0x002f, Throwable -> 0x0047 }
                r8.set(r0)     // Catch:{ ExecutionException -> 0x002f, Throwable -> 0x0047 }
                return
            L_0x0028:
                r3 = r1
                goto L_0x000c
            L_0x002a:
                r2 = r1
                goto L_0x000f
            L_0x002c:
                r0 = r1
                goto L_0x0012
            L_0x002e:
                return
            L_0x002f:
                r0 = move-exception
                java.lang.Throwable r0 = r0.getCause()
            L_0x0034:
                boolean r1 = com.google.common.util.concurrent.Platform.isInstanceOfThrowableClass(r0, r5)     // Catch:{ Throwable -> 0x0042 }
                if (r1 != 0) goto L_0x003e
                r8.setException(r0)     // Catch:{ Throwable -> 0x0042 }
            L_0x003d:
                return
            L_0x003e:
                r8.doFallback(r6, r0)     // Catch:{ Throwable -> 0x0042 }
                goto L_0x003d
            L_0x0042:
                r0 = move-exception
                r8.setException(r0)
                goto L_0x003d
            L_0x0047:
                r0 = move-exception
                goto L_0x0034
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Futures.AbstractCatchingFuture.run():void");
        }
    }

    private static abstract class AbstractChainingFuture<I, O, F> extends AbstractFuture.TrustedFuture<O> implements Runnable {
        @Nullable
        F function;
        @Nullable
        ListenableFuture<? extends I> inputFuture;

        AbstractChainingFuture(ListenableFuture<? extends I> listenableFuture, F f) {
            this.inputFuture = (ListenableFuture) Preconditions.checkNotNull(listenableFuture);
            this.function = Preconditions.checkNotNull(f);
        }

        /* access modifiers changed from: package-private */
        public abstract void doTransform(F f, I i) throws Exception;

        /* access modifiers changed from: package-private */
        public final void done() {
            maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
            this.function = null;
        }

        public final void run() {
            boolean z = false;
            try {
                ListenableFuture<? extends I> listenableFuture = this.inputFuture;
                F f = this.function;
                boolean isCancelled = (listenableFuture == null) | isCancelled();
                if (f == null) {
                    z = true;
                }
                if (!z && !isCancelled) {
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

    static final class AsyncCatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>> {
        AsyncCatchingFuture(ListenableFuture<? extends V> listenableFuture, Class<X> cls, AsyncFunction<? super X, ? extends V> asyncFunction) {
            super(listenableFuture, cls, asyncFunction);
        }

        /* access modifiers changed from: package-private */
        public void doFallback(AsyncFunction<? super X, ? extends V> asyncFunction, X x) throws Exception {
            ListenableFuture<? extends V> apply = asyncFunction.apply(x);
            Preconditions.checkNotNull(apply, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            setFuture(apply);
        }
    }

    private static final class AsyncChainingFuture<I, O> extends AbstractChainingFuture<I, O, AsyncFunction<? super I, ? extends O>> {
        AsyncChainingFuture(ListenableFuture<? extends I> listenableFuture, AsyncFunction<? super I, ? extends O> asyncFunction) {
            super(listenableFuture, asyncFunction);
        }

        /* access modifiers changed from: package-private */
        public void doTransform(AsyncFunction<? super I, ? extends O> asyncFunction, I i) throws Exception {
            ListenableFuture<? extends O> apply = asyncFunction.apply(i);
            Preconditions.checkNotNull(apply, "AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            setFuture(apply);
        }
    }

    static final class CatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>> {
        CatchingFuture(ListenableFuture<? extends V> listenableFuture, Class<X> cls, Function<? super X, ? extends V> function) {
            super(listenableFuture, cls, function);
        }

        /* access modifiers changed from: package-private */
        public void doFallback(Function<? super X, ? extends V> function, X x) throws Exception {
            set(function.apply(x));
        }
    }

    private static final class ChainingFuture<I, O> extends AbstractChainingFuture<I, O, Function<? super I, ? extends O>> {
        ChainingFuture(ListenableFuture<? extends I> listenableFuture, Function<? super I, ? extends O> function) {
            super(listenableFuture, function);
        }

        /* access modifiers changed from: package-private */
        public void doTransform(Function<? super I, ? extends O> function, I i) {
            set(function.apply(i));
        }
    }

    @GwtIncompatible("TODO")
    private static class ImmediateCancelledFuture<V> extends ImmediateFuture<V> {
        private final CancellationException thrown = new CancellationException("Immediate cancelled future.");

        ImmediateCancelledFuture() {
            super();
        }

        public V get() {
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
        }

        public boolean isCancelled() {
            return true;
        }
    }

    @GwtIncompatible("TODO")
    private static class ImmediateFailedCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
        private final X thrown;

        ImmediateFailedCheckedFuture(X x) {
            super();
            this.thrown = x;
        }

        public V checkedGet() throws Exception {
            throw this.thrown;
        }

        public V checkedGet(long j, TimeUnit timeUnit) throws Exception {
            Preconditions.checkNotNull(timeUnit);
            throw this.thrown;
        }

        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }

    private static class ImmediateFailedFuture<V> extends ImmediateFuture<V> {
        private final Throwable thrown;

        ImmediateFailedFuture(Throwable th) {
            super();
            this.thrown = th;
        }

        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }

    private static abstract class ImmediateFuture<V> implements ListenableFuture<V> {
        private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());

        private ImmediateFuture() {
        }

        public void addListener(Runnable runnable, Executor executor) {
            Preconditions.checkNotNull(runnable, "Runnable was null.");
            Preconditions.checkNotNull(executor, "Executor was null.");
            try {
                executor.execute(runnable);
            } catch (RuntimeException e) {
                log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
            }
        }

        public boolean cancel(boolean z) {
            return false;
        }

        public abstract V get() throws ExecutionException;

        public V get(long j, TimeUnit timeUnit) throws ExecutionException {
            Preconditions.checkNotNull(timeUnit);
            return get();
        }

        public boolean isCancelled() {
            return false;
        }

        public boolean isDone() {
            return true;
        }
    }

    @GwtIncompatible("TODO")
    private static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X> {
        @Nullable
        private final V value;

        ImmediateSuccessfulCheckedFuture(@Nullable V v) {
            super();
            this.value = v;
        }

        public V checkedGet() {
            return this.value;
        }

        public V checkedGet(long j, TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            return this.value;
        }

        public V get() {
            return this.value;
        }
    }

    private static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V> {
        static final ImmediateSuccessfulFuture<Object> NULL = new ImmediateSuccessfulFuture<>((Object) null);
        @Nullable
        private final V value;

        ImmediateSuccessfulFuture(@Nullable V v) {
            super();
            this.value = v;
        }

        public V get() {
            return this.value;
        }
    }

    private static final class ListFuture<V> extends CollectionFuture<V, List<V>> {

        private final class ListFutureRunningState extends CollectionFuture<V, List<V>>.CollectionFutureRunningState {
            ListFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> immutableCollection, boolean z) {
                super(immutableCollection, z);
            }

            public List<V> combine(List<Optional<V>> list) {
                ArrayList newArrayList = Lists.newArrayList();
                Iterator<Optional<V>> it = list.iterator();
                while (it.hasNext()) {
                    Optional next = it.next();
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
    private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X> {
        final Function<? super Exception, X> mapper;

        MappingCheckedFuture(ListenableFuture<V> listenableFuture, Function<? super Exception, X> function) {
            super(listenableFuture);
            this.mapper = (Function) Preconditions.checkNotNull(function);
        }

        /* access modifiers changed from: protected */
        public X mapException(Exception exc) {
            return (Exception) this.mapper.apply(exc);
        }
    }

    @GwtIncompatible("TODO")
    private static final class NonCancellationPropagatingFuture<V> extends AbstractFuture.TrustedFuture<V> {
        NonCancellationPropagatingFuture(final ListenableFuture<V> listenableFuture) {
            listenableFuture.addListener(new Runnable() {
                public void run() {
                    NonCancellationPropagatingFuture.this.setFuture(listenableFuture);
                }
            }, MoreExecutors.directExecutor());
        }
    }

    private static final class TimeoutFuture<V> extends AbstractFuture.TrustedFuture<V> {
        @Nullable
        ListenableFuture<V> delegateRef;
        @Nullable
        Future<?> timer;

        private static final class Fire<V> implements Runnable {
            @Nullable
            TimeoutFuture<V> timeoutFutureRef;

            Fire(TimeoutFuture<V> timeoutFuture) {
                this.timeoutFutureRef = timeoutFuture;
            }

            public void run() {
                ListenableFuture<V> listenableFuture;
                TimeoutFuture<V> timeoutFuture = this.timeoutFutureRef;
                if (timeoutFuture != null && (listenableFuture = timeoutFuture.delegateRef) != null) {
                    this.timeoutFutureRef = null;
                    if (!listenableFuture.isDone()) {
                        try {
                            timeoutFuture.setException(new TimeoutException("Future timed out: " + listenableFuture));
                        } finally {
                            listenableFuture.cancel(true);
                        }
                    } else {
                        timeoutFuture.setFuture(listenableFuture);
                    }
                }
            }
        }

        TimeoutFuture(ListenableFuture<V> listenableFuture) {
            this.delegateRef = (ListenableFuture) Preconditions.checkNotNull(listenableFuture);
        }

        /* access modifiers changed from: package-private */
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
        listenableFuture.addListener(new Runnable() {
            public void run() {
                try {
                    futureCallback.onSuccess(Uninterruptibles.getUninterruptibly(listenableFuture));
                } catch (ExecutionException e) {
                    futureCallback.onFailure(e.getCause());
                } catch (RuntimeException e2) {
                    futureCallback.onFailure(e2);
                } catch (Error e3) {
                    futureCallback.onFailure(e3);
                }
            }
        }, executor);
    }

    @CheckReturnValue
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return new ListFuture(ImmutableList.copyOf(iterable), true);
    }

    @CheckReturnValue
    @SafeVarargs
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(ListenableFuture<? extends V>... listenableFutureArr) {
        return new ListFuture(ImmutableList.copyOf((E[]) listenableFutureArr), true);
    }

    @Deprecated
    static <V> AsyncFunction<Throwable, V> asAsyncFunction(final FutureFallback<V> futureFallback) {
        Preconditions.checkNotNull(futureFallback);
        return new AsyncFunction<Throwable, V>() {
            public ListenableFuture<V> apply(Throwable th) throws Exception {
                return (ListenableFuture) Preconditions.checkNotNull(futureFallback.create(th), "FutureFallback.create returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            }
        };
    }

    @CheckReturnValue
    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catching(ListenableFuture<? extends V> listenableFuture, Class<X> cls, Function<? super X, ? extends V> function) {
        CatchingFuture catchingFuture = new CatchingFuture(listenableFuture, cls, function);
        listenableFuture.addListener(catchingFuture, MoreExecutors.directExecutor());
        return catchingFuture;
    }

    @CheckReturnValue
    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
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
        return getChecked(future, cls, j, timeUnit);
    }

    @GwtIncompatible("reflection")
    @Deprecated
    public static <V, X extends Exception> V get(Future<V> future, Class<X> cls) throws Exception {
        return getChecked(future, cls);
    }

    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> cls) throws Exception {
        return FuturesGetChecked.getChecked(future, cls);
    }

    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V getChecked(Future<V> future, Class<X> cls, long j, TimeUnit timeUnit) throws Exception {
        return FuturesGetChecked.getChecked(future, cls, j, timeUnit);
    }

    @GwtIncompatible("TODO")
    public static <V> V getUnchecked(Future<V> future) {
        Preconditions.checkNotNull(future);
        try {
            return Uninterruptibles.getUninterruptibly(future);
        } catch (ExecutionException e) {
            wrapAndThrowUnchecked(e.getCause());
            throw new AssertionError();
        }
    }

    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V> ListenableFuture<V> immediateCancelledFuture() {
        return new ImmediateCancelledFuture();
    }

    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable V v) {
        return new ImmediateSuccessfulCheckedFuture(v);
    }

    @CheckReturnValue
    @GwtIncompatible("TODO")
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

    @CheckReturnValue
    @GwtIncompatible("TODO")
    @Beta
    public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(Iterable<? extends ListenableFuture<? extends T>> iterable) {
        final ConcurrentLinkedQueue newConcurrentLinkedQueue = Queues.newConcurrentLinkedQueue();
        ImmutableList.Builder builder = ImmutableList.builder();
        SerializingExecutor serializingExecutor = new SerializingExecutor(MoreExecutors.directExecutor());
        for (final ListenableFuture listenableFuture : iterable) {
            SettableFuture create = SettableFuture.create();
            newConcurrentLinkedQueue.add(create);
            listenableFuture.addListener(new Runnable() {
                public void run() {
                    ((SettableFuture) newConcurrentLinkedQueue.remove()).setFuture(listenableFuture);
                }
            }, serializingExecutor);
            builder.add((Object) create);
        }
        return builder.build();
    }

    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <I, O> Future<O> lazyTransform(final Future<I> future, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(future);
        Preconditions.checkNotNull(function);
        return new Future<O>() {
            private O applyTransformation(I i) throws ExecutionException {
                try {
                    return function.apply(i);
                } catch (Throwable th) {
                    throw new ExecutionException(th);
                }
            }

            public boolean cancel(boolean z) {
                return future.cancel(z);
            }

            public O get() throws InterruptedException, ExecutionException {
                return applyTransformation(future.get());
            }

            public O get(long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return applyTransformation(future.get(j, timeUnit));
            }

            public boolean isCancelled() {
                return future.isCancelled();
            }

            public boolean isDone() {
                return future.isDone();
            }
        };
    }

    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(ListenableFuture<V> listenableFuture, Function<? super Exception, X> function) {
        return new MappingCheckedFuture((ListenableFuture) Preconditions.checkNotNull(listenableFuture), function);
    }

    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V> ListenableFuture<V> nonCancellationPropagating(ListenableFuture<V> listenableFuture) {
        return new NonCancellationPropagatingFuture(listenableFuture);
    }

    private static Executor rejectionPropagatingExecutor(final Executor executor, final AbstractFuture<?> abstractFuture) {
        Preconditions.checkNotNull(executor);
        return executor != MoreExecutors.directExecutor() ? new Executor() {
            volatile boolean thrownFromDelegate = true;

            public void execute(final Runnable runnable) {
                try {
                    executor.execute(new Runnable() {
                        public void run() {
                            AnonymousClass2.this.thrownFromDelegate = false;
                            runnable.run();
                        }
                    });
                } catch (RejectedExecutionException e) {
                    if (this.thrownFromDelegate) {
                        abstractFuture.setException(e);
                    }
                }
            }
        } : executor;
    }

    @CheckReturnValue
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return new ListFuture(ImmutableList.copyOf(iterable), false);
    }

    @CheckReturnValue
    @SafeVarargs
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(ListenableFuture<? extends V>... listenableFutureArr) {
        return new ListFuture(ImmutableList.copyOf((E[]) listenableFutureArr), false);
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

    @CheckReturnValue
    @GwtIncompatible("java.util.concurrent.ScheduledExecutorService")
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
