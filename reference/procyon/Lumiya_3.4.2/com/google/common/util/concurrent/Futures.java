// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import com.google.common.collect.Lists;
import com.google.common.base.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.common.collect.Queues;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import javax.annotation.CheckReturnValue;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtCompatible(emulated = true)
public final class Futures extends GwtFuturesCatchingSpecialization
{
    private static final AsyncFunction<ListenableFuture<Object>, Object> DEREFERENCER;
    
    static {
        DEREFERENCER = new AsyncFunction<ListenableFuture<Object>, Object>() {
            @Override
            public ListenableFuture<Object> apply(final ListenableFuture<Object> listenableFuture) {
                return listenableFuture;
            }
        };
    }
    
    private Futures() {
    }
    
    public static <V> void addCallback(final ListenableFuture<V> listenableFuture, final FutureCallback<? super V> futureCallback) {
        addCallback(listenableFuture, futureCallback, MoreExecutors.directExecutor());
    }
    
    public static <V> void addCallback(final ListenableFuture<V> listenableFuture, final FutureCallback<? super V> futureCallback, final Executor executor) {
        Preconditions.checkNotNull(futureCallback);
        listenableFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    futureCallback.onSuccess(Uninterruptibles.getUninterruptibly((Future<Object>)listenableFuture));
                }
                catch (final ExecutionException ex) {
                    futureCallback.onFailure(ex.getCause());
                }
                catch (final RuntimeException ex2) {
                    futureCallback.onFailure(ex2);
                }
                catch (final Error error) {
                    futureCallback.onFailure(error);
                }
            }
        }, executor);
    }
    
    @CheckReturnValue
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(final Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return (ListenableFuture<List<V>>)new ListFuture((ImmutableCollection<? extends ListenableFuture<?>>)ImmutableList.copyOf((Iterable<?>)iterable), true);
    }
    
    @SafeVarargs
    @CheckReturnValue
    @Beta
    public static <V> ListenableFuture<List<V>> allAsList(final ListenableFuture<? extends V>... array) {
        return (ListenableFuture<List<V>>)new ListFuture(ImmutableList.copyOf(array), true);
    }
    
    @Deprecated
    static <V> AsyncFunction<Throwable, V> asAsyncFunction(final FutureFallback<V> futureFallback) {
        Preconditions.checkNotNull(futureFallback);
        return new AsyncFunction<Throwable, V>() {
            @Override
            public ListenableFuture<V> apply(final Throwable t) throws Exception {
                return Preconditions.checkNotNull(futureFallback.create(t), (Object)"FutureFallback.create returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            }
        };
    }
    
    @CheckReturnValue
    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catching(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final Function<? super X, ? extends V> function) {
        final CatchingFuture catchingFuture = new CatchingFuture(listenableFuture, (Class<Throwable>)clazz, (Function<? super Throwable, ?>)function);
        listenableFuture.addListener(catchingFuture, MoreExecutors.directExecutor());
        return catchingFuture;
    }
    
    @CheckReturnValue
    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catching(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final Function<? super X, ? extends V> function, final Executor executor) {
        final CatchingFuture catchingFuture = new CatchingFuture(listenableFuture, (Class<Throwable>)clazz, (Function<? super Throwable, ?>)function);
        listenableFuture.addListener(catchingFuture, rejectionPropagatingExecutor(executor, catchingFuture));
        return catchingFuture;
    }
    
    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final AsyncFunction<? super X, ? extends V> asyncFunction) {
        final AsyncCatchingFuture asyncCatchingFuture = new AsyncCatchingFuture(listenableFuture, (Class<Throwable>)clazz, (AsyncFunction<? super Throwable, ?>)asyncFunction);
        listenableFuture.addListener(asyncCatchingFuture, MoreExecutors.directExecutor());
        return asyncCatchingFuture;
    }
    
    @GwtIncompatible("AVAILABLE but requires exceptionType to be Throwable.class")
    public static <V, X extends Throwable> ListenableFuture<V> catchingAsync(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final AsyncFunction<? super X, ? extends V> asyncFunction, final Executor executor) {
        final AsyncCatchingFuture asyncCatchingFuture = new AsyncCatchingFuture(listenableFuture, (Class<Throwable>)clazz, (AsyncFunction<? super Throwable, ?>)asyncFunction);
        listenableFuture.addListener(asyncCatchingFuture, rejectionPropagatingExecutor(executor, asyncCatchingFuture));
        return asyncCatchingFuture;
    }
    
    @CheckReturnValue
    public static <V> ListenableFuture<V> dereference(final ListenableFuture<? extends ListenableFuture<? extends V>> listenableFuture) {
        return transformAsync((ListenableFuture<Object>)listenableFuture, (AsyncFunction<? super Object, ? extends V>)Futures.DEREFERENCER);
    }
    
    @Deprecated
    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V get(final Future<V> future, final long n, final TimeUnit timeUnit, final Class<X> clazz) throws X, Exception {
        return getChecked(future, clazz, n, timeUnit);
    }
    
    @Deprecated
    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V get(final Future<V> future, final Class<X> clazz) throws X, Exception {
        return (V)getChecked((Future<Object>)future, (Class<Exception>)clazz);
    }
    
    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V getChecked(final Future<V> future, final Class<X> clazz) throws X, Exception {
        return FuturesGetChecked.getChecked(future, clazz);
    }
    
    @GwtIncompatible("reflection")
    public static <V, X extends Exception> V getChecked(final Future<V> future, final Class<X> clazz, final long n, final TimeUnit timeUnit) throws X, Exception {
        return FuturesGetChecked.getChecked(future, clazz, n, timeUnit);
    }
    
    @GwtIncompatible("TODO")
    public static <V> V getUnchecked(final Future<V> future) {
        Preconditions.checkNotNull(future);
        try {
            return Uninterruptibles.getUninterruptibly(future);
        }
        catch (final ExecutionException ex) {
            wrapAndThrowUnchecked(ex.getCause());
            throw new AssertionError();
        }
    }
    
    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V> ListenableFuture<V> immediateCancelledFuture() {
        return new ImmediateCancelledFuture<V>();
    }
    
    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V, X extends Exception> CheckedFuture<V, X> immediateCheckedFuture(@Nullable final V v) {
        return new ImmediateSuccessfulCheckedFuture<V, X>(v);
    }
    
    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V, X extends Exception> CheckedFuture<V, X> immediateFailedCheckedFuture(final X x) {
        Preconditions.checkNotNull(x);
        return new ImmediateFailedCheckedFuture<V, X>(x);
    }
    
    @CheckReturnValue
    public static <V> ListenableFuture<V> immediateFailedFuture(final Throwable t) {
        Preconditions.checkNotNull(t);
        return new ImmediateFailedFuture<V>(t);
    }
    
    @CheckReturnValue
    public static <V> ListenableFuture<V> immediateFuture(@Nullable final V v) {
        if (v != null) {
            return new ImmediateSuccessfulFuture<V>(v);
        }
        return (ListenableFuture<V>)ImmediateSuccessfulFuture.NULL;
    }
    
    @CheckReturnValue
    @Beta
    @GwtIncompatible("TODO")
    public static <T> ImmutableList<ListenableFuture<T>> inCompletionOrder(final Iterable<? extends ListenableFuture<? extends T>> iterable) {
        final ConcurrentLinkedQueue<SettableFuture<Object>> concurrentLinkedQueue = Queues.newConcurrentLinkedQueue();
        final ImmutableList.Builder<SettableFuture> builder = ImmutableList.builder();
        final SerializingExecutor serializingExecutor = new SerializingExecutor(MoreExecutors.directExecutor());
        for (final ListenableFuture listenableFuture : iterable) {
            final SettableFuture<Object> create = SettableFuture.create();
            concurrentLinkedQueue.add(create);
            listenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    ((SettableFuture)concurrentLinkedQueue.remove()).setFuture(listenableFuture);
                }
            }, serializingExecutor);
            builder.add(create);
        }
        return (ImmutableList<ListenableFuture<T>>)builder.build();
    }
    
    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <I, O> Future<O> lazyTransform(final Future<I> future, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(future);
        Preconditions.checkNotNull(function);
        return new Future<O>() {
            private O applyTransformation(final I n) throws ExecutionException {
                try {
                    return function.apply(n);
                }
                catch (final Throwable cause) {
                    throw new ExecutionException(cause);
                }
            }
            
            @Override
            public boolean cancel(final boolean b) {
                return future.cancel(b);
            }
            
            @Override
            public O get() throws InterruptedException, ExecutionException {
                return this.applyTransformation(future.get());
            }
            
            @Override
            public O get(final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return this.applyTransformation(future.get(n, timeUnit));
            }
            
            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }
            
            @Override
            public boolean isDone() {
                return future.isDone();
            }
        };
    }
    
    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V, X extends Exception> CheckedFuture<V, X> makeChecked(final ListenableFuture<V> listenableFuture, final Function<? super Exception, X> function) {
        return new MappingCheckedFuture<V, X>(Preconditions.checkNotNull(listenableFuture), function);
    }
    
    @CheckReturnValue
    @GwtIncompatible("TODO")
    public static <V> ListenableFuture<V> nonCancellationPropagating(final ListenableFuture<V> listenableFuture) {
        return new NonCancellationPropagatingFuture<V>(listenableFuture);
    }
    
    private static Executor rejectionPropagatingExecutor(final Executor executor, final AbstractFuture<?> abstractFuture) {
        Preconditions.checkNotNull(executor);
        if (executor != MoreExecutors.directExecutor()) {
            return new Executor() {
                volatile boolean thrownFromDelegate = true;
                
                @Override
                public void execute(final Runnable runnable) {
                    try {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                Executor.this.thrownFromDelegate = false;
                                runnable.run();
                            }
                        });
                    }
                    catch (final RejectedExecutionException exception) {
                        if (this.thrownFromDelegate) {
                            abstractFuture.setException(exception);
                        }
                    }
                }
            };
        }
        return executor;
    }
    
    @CheckReturnValue
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(final Iterable<? extends ListenableFuture<? extends V>> iterable) {
        return (ListenableFuture<List<V>>)new ListFuture((ImmutableCollection<? extends ListenableFuture<?>>)ImmutableList.copyOf((Iterable<?>)iterable), false);
    }
    
    @SafeVarargs
    @CheckReturnValue
    @Beta
    public static <V> ListenableFuture<List<V>> successfulAsList(final ListenableFuture<? extends V>... array) {
        return (ListenableFuture<List<V>>)new ListFuture(ImmutableList.copyOf(array), false);
    }
    
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> listenableFuture, final Function<? super I, ? extends O> function) {
        Preconditions.checkNotNull(function);
        final ChainingFuture chainingFuture = new ChainingFuture(listenableFuture, (Function<? super Object, ?>)function);
        listenableFuture.addListener(chainingFuture, MoreExecutors.directExecutor());
        return chainingFuture;
    }
    
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> listenableFuture, final Function<? super I, ? extends O> function, final Executor executor) {
        Preconditions.checkNotNull(function);
        final ChainingFuture chainingFuture = new ChainingFuture(listenableFuture, (Function<? super Object, ?>)function);
        listenableFuture.addListener(chainingFuture, rejectionPropagatingExecutor(executor, chainingFuture));
        return chainingFuture;
    }
    
    @Deprecated
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> listenableFuture, final AsyncFunction<? super I, ? extends O> asyncFunction) {
        return (ListenableFuture<O>)transformAsync((ListenableFuture<Object>)listenableFuture, (AsyncFunction<? super Object, ?>)asyncFunction);
    }
    
    @Deprecated
    public static <I, O> ListenableFuture<O> transform(final ListenableFuture<I> listenableFuture, final AsyncFunction<? super I, ? extends O> asyncFunction, final Executor executor) {
        return (ListenableFuture<O>)transformAsync((ListenableFuture<Object>)listenableFuture, (AsyncFunction<? super Object, ?>)asyncFunction, executor);
    }
    
    public static <I, O> ListenableFuture<O> transformAsync(final ListenableFuture<I> listenableFuture, final AsyncFunction<? super I, ? extends O> asyncFunction) {
        final AsyncChainingFuture asyncChainingFuture = new AsyncChainingFuture(listenableFuture, (AsyncFunction<? super Object, ?>)asyncFunction);
        listenableFuture.addListener(asyncChainingFuture, MoreExecutors.directExecutor());
        return asyncChainingFuture;
    }
    
    public static <I, O> ListenableFuture<O> transformAsync(final ListenableFuture<I> listenableFuture, final AsyncFunction<? super I, ? extends O> asyncFunction, final Executor executor) {
        Preconditions.checkNotNull(executor);
        final AsyncChainingFuture asyncChainingFuture = new AsyncChainingFuture(listenableFuture, (AsyncFunction<? super Object, ?>)asyncFunction);
        listenableFuture.addListener(asyncChainingFuture, rejectionPropagatingExecutor(executor, asyncChainingFuture));
        return asyncChainingFuture;
    }
    
    @Deprecated
    @CheckReturnValue
    public static <V> ListenableFuture<V> withFallback(final ListenableFuture<? extends V> listenableFuture, final FutureFallback<? extends V> futureFallback) {
        return withFallback(listenableFuture, futureFallback, MoreExecutors.directExecutor());
    }
    
    @Deprecated
    @CheckReturnValue
    public static <V> ListenableFuture<V> withFallback(final ListenableFuture<? extends V> listenableFuture, final FutureFallback<? extends V> futureFallback, final Executor executor) {
        return catchingAsync(listenableFuture, Throwable.class, (AsyncFunction<? super Throwable, ? extends V>)asAsyncFunction((FutureFallback<? extends V>)futureFallback), executor);
    }
    
    @CheckReturnValue
    @GwtIncompatible("java.util.concurrent.ScheduledExecutorService")
    public static <V> ListenableFuture<V> withTimeout(final ListenableFuture<V> listenableFuture, final long n, final TimeUnit timeUnit, final ScheduledExecutorService scheduledExecutorService) {
        final TimeoutFuture timeoutFuture = new TimeoutFuture((ListenableFuture<Object>)listenableFuture);
        final Fire fire = new Fire(timeoutFuture);
        timeoutFuture.timer = scheduledExecutorService.schedule(fire, n, timeUnit);
        listenableFuture.addListener(fire, MoreExecutors.directExecutor());
        return timeoutFuture;
    }
    
    @GwtIncompatible("TODO")
    private static void wrapAndThrowUnchecked(final Throwable t) {
        if (!(t instanceof Error)) {
            throw new UncheckedExecutionException(t);
        }
        throw new ExecutionError((Error)t);
    }
    
    private abstract static class AbstractCatchingFuture<V, X extends Throwable, F> extends TrustedFuture<V> implements Runnable
    {
        @Nullable
        Class<X> exceptionType;
        @Nullable
        F fallback;
        @Nullable
        ListenableFuture<? extends V> inputFuture;
        
        AbstractCatchingFuture(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final F n) {
            this.inputFuture = Preconditions.checkNotNull(listenableFuture);
            this.exceptionType = Preconditions.checkNotNull(clazz);
            this.fallback = Preconditions.checkNotNull(n);
        }
        
        abstract void doFallback(final F p0, final X p1) throws Exception;
        
        @Override
        final void done() {
            this.maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
            this.exceptionType = null;
            this.fallback = null;
        }
        
        @Override
        public final void run() {
            int n = 0;
            final ListenableFuture<? extends V> inputFuture = this.inputFuture;
            final Class<X> exceptionType = this.exceptionType;
            final F fallback = this.fallback;
            Throwable cause = null;
            Label_0077: {
                if (inputFuture == null) {
                    break Label_0077;
                }
                int n2 = 0;
            Label_0032_Outer:
                while (true) {
                    Label_0083: {
                        if (exceptionType == null) {
                            break Label_0083;
                        }
                        int n3 = 0;
                    Label_0037_Outer:
                        while (true) {
                            Label_0089: {
                                if (fallback == null) {
                                    break Label_0089;
                                }
                                while (true) {
                                    if ((n | (n3 | n2) | (this.isCancelled() ? 1 : 0)) != 0x0) {
                                        return;
                                    }
                                    this.inputFuture = null;
                                    this.exceptionType = null;
                                    this.fallback = null;
                                    try {
                                        this.set(Uninterruptibles.getUninterruptibly((Future<V>)inputFuture));
                                        return;
                                        n2 = 1;
                                        continue Label_0032_Outer;
                                        n3 = 1;
                                        continue Label_0037_Outer;
                                        n = 1;
                                        continue;
                                    }
                                    catch (final ExecutionException ex) {
                                        cause = ex.getCause();
                                    }
                                    catch (final Throwable cause) {}
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
            }
            try {
                if (!Platform.isInstanceOfThrowableClass(cause, exceptionType)) {
                    this.setException(cause);
                }
                else {
                    this.doFallback(fallback, (X)cause);
                }
            }
            catch (final Throwable t) {}
        }
    }
    
    private abstract static class AbstractChainingFuture<I, O, F> extends TrustedFuture<O> implements Runnable
    {
        @Nullable
        F function;
        @Nullable
        ListenableFuture<? extends I> inputFuture;
        
        AbstractChainingFuture(final ListenableFuture<? extends I> listenableFuture, final F n) {
            this.inputFuture = Preconditions.checkNotNull(listenableFuture);
            this.function = Preconditions.checkNotNull(n);
        }
        
        abstract void doTransform(final F p0, final I p1) throws Exception;
        
        @Override
        final void done() {
            this.maybePropagateCancellation(this.inputFuture);
            this.inputFuture = null;
            this.function = null;
        }
        
        @Override
        public final void run() {
            int n = 0;
            try {
                final ListenableFuture<? extends I> inputFuture = this.inputFuture;
                final F function = this.function;
                final boolean cancelled = this.isCancelled();
                Label_0061: {
                    if (inputFuture == null) {
                        break Label_0061;
                    }
                    int n2 = 0;
                Label_0029_Outer:
                    while (true) {
                        Label_0067: {
                            if (function == null) {
                                break Label_0067;
                            }
                            while (true) {
                                if ((n | (n2 | (cancelled ? 1 : 0))) != 0x0) {
                                    return;
                                }
                                this.inputFuture = null;
                                this.function = null;
                                try {
                                    this.doTransform(function, Uninterruptibles.getUninterruptibly((Future<I>)inputFuture));
                                    return;
                                    n2 = 1;
                                    continue Label_0029_Outer;
                                    n = 1;
                                    continue;
                                }
                                catch (final CancellationException ex) {
                                    this.cancel(false);
                                }
                                catch (final ExecutionException ex2) {
                                    this.setException(ex2.getCause());
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            catch (final UndeclaredThrowableException ex3) {
                this.setException(ex3.getCause());
            }
            catch (final Throwable exception) {
                this.setException(exception);
            }
        }
    }
    
    static final class AsyncCatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, AsyncFunction<? super X, ? extends V>>
    {
        AsyncCatchingFuture(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final AsyncFunction<? super X, ? extends V> asyncFunction) {
            super(listenableFuture, clazz, asyncFunction);
        }
        
        void doFallback(final AsyncFunction<? super X, ? extends V> asyncFunction, final X x) throws Exception {
            final ListenableFuture<? extends V> apply = asyncFunction.apply(x);
            Preconditions.checkNotNull(apply, (Object)"AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            this.setFuture((ListenableFuture<? extends V>)apply);
        }
    }
    
    private static final class AsyncChainingFuture<I, O> extends AbstractChainingFuture<I, O, AsyncFunction<? super I, ? extends O>>
    {
        AsyncChainingFuture(final ListenableFuture<? extends I> listenableFuture, final AsyncFunction<? super I, ? extends O> asyncFunction) {
            super(listenableFuture, asyncFunction);
        }
        
        void doTransform(final AsyncFunction<? super I, ? extends O> asyncFunction, final I n) throws Exception {
            final ListenableFuture<? extends O> apply = asyncFunction.apply(n);
            Preconditions.checkNotNull(apply, (Object)"AsyncFunction.apply returned null instead of a Future. Did you mean to return immediateFuture(null)?");
            this.setFuture((ListenableFuture<? extends O>)apply);
        }
    }
    
    static final class CatchingFuture<V, X extends Throwable> extends AbstractCatchingFuture<V, X, Function<? super X, ? extends V>>
    {
        CatchingFuture(final ListenableFuture<? extends V> listenableFuture, final Class<X> clazz, final Function<? super X, ? extends V> function) {
            super(listenableFuture, clazz, function);
        }
        
        void doFallback(final Function<? super X, ? extends V> function, final X x) throws Exception {
            this.set((V)function.apply(x));
        }
    }
    
    private static final class ChainingFuture<I, O> extends AbstractChainingFuture<I, O, Function<? super I, ? extends O>>
    {
        ChainingFuture(final ListenableFuture<? extends I> listenableFuture, final Function<? super I, ? extends O> function) {
            super(listenableFuture, function);
        }
        
        void doTransform(final Function<? super I, ? extends O> function, final I n) {
            this.set((O)function.apply(n));
        }
    }
    
    @GwtIncompatible("TODO")
    private static class ImmediateCancelledFuture<V> extends ImmediateFuture<V>
    {
        private final CancellationException thrown;
        
        ImmediateCancelledFuture() {
            this.thrown = new CancellationException("Immediate cancelled future.");
        }
        
        @Override
        public V get() {
            throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
        }
        
        @Override
        public boolean isCancelled() {
            return true;
        }
    }
    
    private abstract static class ImmediateFuture<V> implements ListenableFuture<V>
    {
        private static final Logger log;
        
        static {
            log = Logger.getLogger(ImmediateFuture.class.getName());
        }
        
        @Override
        public void addListener(final Runnable obj, final Executor obj2) {
            Preconditions.checkNotNull(obj, (Object)"Runnable was null.");
            Preconditions.checkNotNull(obj2, (Object)"Executor was null.");
            try {
                obj2.execute(obj);
            }
            catch (final RuntimeException thrown) {
                ImmediateFuture.log.log(Level.SEVERE, "RuntimeException while executing runnable " + obj + " with executor " + obj2, thrown);
            }
        }
        
        @Override
        public boolean cancel(final boolean b) {
            return false;
        }
        
        @Override
        public abstract V get() throws ExecutionException;
        
        @Override
        public V get(final long n, final TimeUnit timeUnit) throws ExecutionException {
            Preconditions.checkNotNull(timeUnit);
            return this.get();
        }
        
        @Override
        public boolean isCancelled() {
            return false;
        }
        
        @Override
        public boolean isDone() {
            return true;
        }
    }
    
    @GwtIncompatible("TODO")
    private static class ImmediateFailedCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X>
    {
        private final X thrown;
        
        ImmediateFailedCheckedFuture(final X thrown) {
            this.thrown = thrown;
        }
        
        @Override
        public V checkedGet() throws X, Exception {
            throw this.thrown;
        }
        
        @Override
        public V checkedGet(final long n, final TimeUnit timeUnit) throws X, Exception {
            Preconditions.checkNotNull(timeUnit);
            throw this.thrown;
        }
        
        @Override
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }
    
    private static class ImmediateFailedFuture<V> extends ImmediateFuture<V>
    {
        private final Throwable thrown;
        
        ImmediateFailedFuture(final Throwable thrown) {
            this.thrown = thrown;
        }
        
        @Override
        public V get() throws ExecutionException {
            throw new ExecutionException(this.thrown);
        }
    }
    
    @GwtIncompatible("TODO")
    private static class ImmediateSuccessfulCheckedFuture<V, X extends Exception> extends ImmediateFuture<V> implements CheckedFuture<V, X>
    {
        @Nullable
        private final V value;
        
        ImmediateSuccessfulCheckedFuture(@Nullable final V value) {
            this.value = value;
        }
        
        @Override
        public V checkedGet() {
            return this.value;
        }
        
        @Override
        public V checkedGet(final long n, final TimeUnit timeUnit) {
            Preconditions.checkNotNull(timeUnit);
            return this.value;
        }
        
        @Override
        public V get() {
            return this.value;
        }
    }
    
    private static class ImmediateSuccessfulFuture<V> extends ImmediateFuture<V>
    {
        static final ImmediateSuccessfulFuture<Object> NULL;
        @Nullable
        private final V value;
        
        static {
            NULL = new ImmediateSuccessfulFuture<Object>((Object)null);
        }
        
        ImmediateSuccessfulFuture(@Nullable final V value) {
            this.value = value;
        }
        
        @Override
        public V get() {
            return this.value;
        }
    }
    
    private static final class ListFuture<V> extends CollectionFuture<V, List<V>>
    {
        ListFuture(final ImmutableCollection<? extends ListenableFuture<? extends V>> collection, final boolean b) {
            this.init((RunningState)new ListFutureRunningState(collection, b));
        }
        
        private final class ListFutureRunningState extends CollectionFutureRunningState
        {
            ListFutureRunningState(final ImmutableCollection<? extends ListenableFuture<? extends V>> collection, final boolean b) {
                super((ImmutableCollection<? extends ListenableFuture<? extends V>>)collection, b);
            }
            
            public List<V> combine(final List<Optional<V>> list) {
                final ArrayList<Object> arrayList = Lists.newArrayList();
                for (final Optional optional : list) {
                    Object orNull;
                    if (optional == null) {
                        orNull = null;
                    }
                    else {
                        orNull = optional.orNull();
                    }
                    arrayList.add(orNull);
                }
                return Collections.unmodifiableList((List<? extends V>)arrayList);
            }
        }
    }
    
    @GwtIncompatible("TODO")
    private static class MappingCheckedFuture<V, X extends Exception> extends AbstractCheckedFuture<V, X>
    {
        final Function<? super Exception, X> mapper;
        
        MappingCheckedFuture(final ListenableFuture<V> listenableFuture, final Function<? super Exception, X> function) {
            super(listenableFuture);
            this.mapper = Preconditions.checkNotNull(function);
        }
        
        @Override
        protected X mapException(final Exception ex) {
            return this.mapper.apply(ex);
        }
    }
    
    @GwtIncompatible("TODO")
    private static final class NonCancellationPropagatingFuture<V> extends TrustedFuture<V>
    {
        NonCancellationPropagatingFuture(final ListenableFuture<V> listenableFuture) {
            listenableFuture.addListener(new Runnable() {
                @Override
                public void run() {
                    NonCancellationPropagatingFuture.this.setFuture(listenableFuture);
                }
            }, MoreExecutors.directExecutor());
        }
    }
    
    private static final class TimeoutFuture<V> extends TrustedFuture<V>
    {
        @Nullable
        ListenableFuture<V> delegateRef;
        @Nullable
        Future<?> timer;
        
        TimeoutFuture(final ListenableFuture<V> listenableFuture) {
            this.delegateRef = Preconditions.checkNotNull(listenableFuture);
        }
        
        @Override
        void done() {
            this.maybePropagateCancellation(this.delegateRef);
            final Future<?> timer = this.timer;
            if (timer != null) {
                timer.cancel(false);
            }
            this.delegateRef = null;
            this.timer = null;
        }
        
        private static final class Fire<V> implements Runnable
        {
            @Nullable
            TimeoutFuture<V> timeoutFutureRef;
            
            Fire(final TimeoutFuture<V> timeoutFutureRef) {
                this.timeoutFutureRef = timeoutFutureRef;
            }
            
            @Override
            public void run() {
                final TimeoutFuture<V> timeoutFutureRef = this.timeoutFutureRef;
                if (timeoutFutureRef == null) {
                    return;
                }
                final ListenableFuture<Object> delegateRef = (ListenableFuture<Object>)timeoutFutureRef.delegateRef;
                if (delegateRef == null) {
                    return;
                }
                this.timeoutFutureRef = null;
                Label_0081: {
                    if (delegateRef.isDone()) {
                        break Label_0081;
                    }
                    try {
                        timeoutFutureRef.setException(new TimeoutException("Future timed out: " + delegateRef));
                        return;
                        timeoutFutureRef.setFuture((ListenableFuture<?>)delegateRef);
                    }
                    finally {
                        delegateRef.cancel(true);
                    }
                }
            }
        }
    }
}
