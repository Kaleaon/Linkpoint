package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.vr.cardboard.VrSettingsProviderContract;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import sun.misc.Unsafe;

@GwtCompatible(emulated = true)
public abstract class AbstractFuture<V> implements ListenableFuture<V> {
    /* access modifiers changed from: private */
    public static final AtomicHelper ATOMIC_HELPER;
    private static final boolean GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
    private static final Object NULL = new Object();
    private static final long SPIN_THRESHOLD_NANOS = 1000;
    private static final Logger log = Logger.getLogger(AbstractFuture.class.getName());
    /* access modifiers changed from: private */
    public volatile Listener listeners;
    /* access modifiers changed from: private */
    public volatile Object value;
    /* access modifiers changed from: private */
    public volatile Waiter waiters;

    private static abstract class AtomicHelper {
        private AtomicHelper() {
        }

        /* access modifiers changed from: package-private */
        public abstract boolean casListeners(AbstractFuture<?> abstractFuture, Listener listener, Listener listener2);

        /* access modifiers changed from: package-private */
        public abstract boolean casValue(AbstractFuture<?> abstractFuture, Object obj, Object obj2);

        /* access modifiers changed from: package-private */
        public abstract boolean casWaiters(AbstractFuture<?> abstractFuture, Waiter waiter, Waiter waiter2);

        /* access modifiers changed from: package-private */
        public abstract void putNext(Waiter waiter, Waiter waiter2);

        /* access modifiers changed from: package-private */
        public abstract void putThread(Waiter waiter, Thread thread);
    }

    private static final class Cancellation {
        @Nullable
        final Throwable cause;
        final boolean wasInterrupted;

        Cancellation(boolean z, @Nullable Throwable th) {
            this.wasInterrupted = z;
            this.cause = th;
        }
    }

    private static final class Failure {
        static final Failure FALLBACK_INSTANCE = new Failure(new Throwable("Failure occurred while trying to finish a future.") {
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        });
        final Throwable exception;

        Failure(Throwable th) {
            this.exception = (Throwable) Preconditions.checkNotNull(th);
        }
    }

    private static final class Listener {
        static final Listener TOMBSTONE = new Listener((Runnable) null, (Executor) null);
        final Executor executor;
        @Nullable
        Listener next;
        final Runnable task;

        Listener(Runnable runnable, Executor executor2) {
            this.task = runnable;
            this.executor = executor2;
        }
    }

    private static final class SafeAtomicHelper extends AtomicHelper {
        final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater;

        SafeAtomicHelper(AtomicReferenceFieldUpdater<Waiter, Thread> atomicReferenceFieldUpdater, AtomicReferenceFieldUpdater<Waiter, Waiter> atomicReferenceFieldUpdater2, AtomicReferenceFieldUpdater<AbstractFuture, Waiter> atomicReferenceFieldUpdater3, AtomicReferenceFieldUpdater<AbstractFuture, Listener> atomicReferenceFieldUpdater4, AtomicReferenceFieldUpdater<AbstractFuture, Object> atomicReferenceFieldUpdater5) {
            super();
            this.waiterThreadUpdater = atomicReferenceFieldUpdater;
            this.waiterNextUpdater = atomicReferenceFieldUpdater2;
            this.waitersUpdater = atomicReferenceFieldUpdater3;
            this.listenersUpdater = atomicReferenceFieldUpdater4;
            this.valueUpdater = atomicReferenceFieldUpdater5;
        }

        /* access modifiers changed from: package-private */
        public boolean casListeners(AbstractFuture<?> abstractFuture, Listener listener, Listener listener2) {
            return this.listenersUpdater.compareAndSet(abstractFuture, listener, listener2);
        }

        /* access modifiers changed from: package-private */
        public boolean casValue(AbstractFuture<?> abstractFuture, Object obj, Object obj2) {
            return this.valueUpdater.compareAndSet(abstractFuture, obj, obj2);
        }

        /* access modifiers changed from: package-private */
        public boolean casWaiters(AbstractFuture<?> abstractFuture, Waiter waiter, Waiter waiter2) {
            return this.waitersUpdater.compareAndSet(abstractFuture, waiter, waiter2);
        }

        /* access modifiers changed from: package-private */
        public void putNext(Waiter waiter, Waiter waiter2) {
            this.waiterNextUpdater.lazySet(waiter, waiter2);
        }

        /* access modifiers changed from: package-private */
        public void putThread(Waiter waiter, Thread thread) {
            this.waiterThreadUpdater.lazySet(waiter, thread);
        }
    }

    private final class SetFuture implements Runnable {
        final ListenableFuture<? extends V> future;

        SetFuture(ListenableFuture<? extends V> listenableFuture) {
            this.future = listenableFuture;
        }

        public void run() {
            if (AbstractFuture.this.value == this) {
                boolean unused = AbstractFuture.this.completeWithFuture(this.future, this);
            }
        }
    }

    private static final class SynchronizedHelper extends AtomicHelper {
        private SynchronizedHelper() {
            super();
        }

        /* access modifiers changed from: package-private */
        public boolean casListeners(AbstractFuture<?> abstractFuture, Listener listener, Listener listener2) {
            synchronized (abstractFuture) {
                if (abstractFuture.listeners != listener) {
                    return false;
                }
                Listener unused = abstractFuture.listeners = listener2;
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean casValue(AbstractFuture<?> abstractFuture, Object obj, Object obj2) {
            synchronized (abstractFuture) {
                if (abstractFuture.value != obj) {
                    return false;
                }
                Object unused = abstractFuture.value = obj2;
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean casWaiters(AbstractFuture<?> abstractFuture, Waiter waiter, Waiter waiter2) {
            synchronized (abstractFuture) {
                if (abstractFuture.waiters != waiter) {
                    return false;
                }
                Waiter unused = abstractFuture.waiters = waiter2;
                return true;
            }
        }

        /* access modifiers changed from: package-private */
        public void putNext(Waiter waiter, Waiter waiter2) {
            waiter.next = waiter2;
        }

        /* access modifiers changed from: package-private */
        public void putThread(Waiter waiter, Thread thread) {
            waiter.thread = thread;
        }
    }

    static abstract class TrustedFuture<V> extends AbstractFuture<V> {
        TrustedFuture() {
        }

        public final void addListener(Runnable runnable, Executor executor) {
            AbstractFuture.super.addListener(runnable, executor);
        }

        public final V get() throws InterruptedException, ExecutionException {
            return AbstractFuture.super.get();
        }

        public final V get(long j, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return AbstractFuture.super.get(j, timeUnit);
        }

        public final boolean isCancelled() {
            return AbstractFuture.super.isCancelled();
        }

        public final boolean isDone() {
            return AbstractFuture.super.isDone();
        }
    }

    private static final class UnsafeAtomicHelper extends AtomicHelper {
        static final long LISTENERS_OFFSET;
        static final Unsafe UNSAFE;
        static final long VALUE_OFFSET;
        static final long WAITERS_OFFSET;
        static final long WAITER_NEXT_OFFSET;
        static final long WAITER_THREAD_OFFSET;

        static {
            Unsafe unsafe;
            try {
                unsafe = Unsafe.getUnsafe();
            } catch (SecurityException e) {
                try {
                    unsafe = (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                        public Unsafe run() throws Exception {
                            Class<Unsafe> cls = Unsafe.class;
                            for (Field field : cls.getDeclaredFields()) {
                                field.setAccessible(true);
                                Object obj = field.get((Object) null);
                                if (cls.isInstance(obj)) {
                                    return cls.cast(obj);
                                }
                            }
                            throw new NoSuchFieldError("the Unsafe");
                        }
                    });
                } catch (PrivilegedActionException e2) {
                    throw new RuntimeException("Could not initialize intrinsics", e2.getCause());
                }
            }
            Class<AbstractFuture> cls = AbstractFuture.class;
            try {
                WAITERS_OFFSET = unsafe.objectFieldOffset(cls.getDeclaredField("waiters"));
                LISTENERS_OFFSET = unsafe.objectFieldOffset(cls.getDeclaredField("listeners"));
                VALUE_OFFSET = unsafe.objectFieldOffset(cls.getDeclaredField(VrSettingsProviderContract.SETTING_VALUE_KEY));
                WAITER_THREAD_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("thread"));
                WAITER_NEXT_OFFSET = unsafe.objectFieldOffset(Waiter.class.getDeclaredField("next"));
                UNSAFE = unsafe;
            } catch (Exception e3) {
                throw Throwables.propagate(e3);
            }
        }

        private UnsafeAtomicHelper() {
            super();
        }

        /* access modifiers changed from: package-private */
        public boolean casListeners(AbstractFuture<?> abstractFuture, Listener listener, Listener listener2) {
            return UNSAFE.compareAndSwapObject(abstractFuture, LISTENERS_OFFSET, listener, listener2);
        }

        /* access modifiers changed from: package-private */
        public boolean casValue(AbstractFuture<?> abstractFuture, Object obj, Object obj2) {
            return UNSAFE.compareAndSwapObject(abstractFuture, VALUE_OFFSET, obj, obj2);
        }

        /* access modifiers changed from: package-private */
        public boolean casWaiters(AbstractFuture<?> abstractFuture, Waiter waiter, Waiter waiter2) {
            return UNSAFE.compareAndSwapObject(abstractFuture, WAITERS_OFFSET, waiter, waiter2);
        }

        /* access modifiers changed from: package-private */
        public void putNext(Waiter waiter, Waiter waiter2) {
            UNSAFE.putObject(waiter, WAITER_NEXT_OFFSET, waiter2);
        }

        /* access modifiers changed from: package-private */
        public void putThread(Waiter waiter, Thread thread) {
            UNSAFE.putObject(waiter, WAITER_THREAD_OFFSET, thread);
        }
    }

    private static final class Waiter {
        static final Waiter TOMBSTONE = new Waiter(false);
        @Nullable
        volatile Waiter next;
        @Nullable
        volatile Thread thread;

        Waiter() {
            AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
        }

        Waiter(boolean z) {
        }

        /* access modifiers changed from: package-private */
        public void setNext(Waiter waiter) {
            AbstractFuture.ATOMIC_HELPER.putNext(this, waiter);
        }

        /* access modifiers changed from: package-private */
        public void unpark() {
            Thread thread2 = this.thread;
            if (thread2 != null) {
                this.thread = null;
                LockSupport.unpark(thread2);
            }
        }
    }

    static {
        Throwable th;
        AtomicHelper synchronizedHelper;
        try {
            synchronizedHelper = new UnsafeAtomicHelper();
        } catch (Throwable th2) {
            log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", th);
            log.log(Level.SEVERE, "SafeAtomicHelper is broken!", th2);
            synchronizedHelper = new SynchronizedHelper();
        }
        ATOMIC_HELPER = synchronizedHelper;
        Class<LockSupport> cls = LockSupport.class;
    }

    protected AbstractFuture() {
    }

    static final CancellationException cancellationExceptionWithCause(@Nullable String str, @Nullable Throwable th) {
        CancellationException cancellationException = new CancellationException(str);
        cancellationException.initCause(th);
        return cancellationException;
    }

    private Listener clearListeners() {
        Listener listener;
        do {
            listener = this.listeners;
        } while (!ATOMIC_HELPER.casListeners(this, listener, Listener.TOMBSTONE));
        return listener;
    }

    private Waiter clearWaiters() {
        Waiter waiter;
        do {
            waiter = this.waiters;
        } while (!ATOMIC_HELPER.casWaiters(this, waiter, Waiter.TOMBSTONE));
        return waiter;
    }

    private void complete() {
        Listener listener = null;
        for (Waiter clearWaiters = clearWaiters(); clearWaiters != null; clearWaiters = clearWaiters.next) {
            clearWaiters.unpark();
        }
        Listener clearListeners = clearListeners();
        while (clearListeners != null) {
            Listener listener2 = clearListeners.next;
            clearListeners.next = listener;
            listener = clearListeners;
            clearListeners = listener2;
        }
        while (listener != null) {
            executeListener(listener.task, listener.executor);
            listener = listener.next;
        }
        done();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x0013 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean completeWithFuture(com.google.common.util.concurrent.ListenableFuture<? extends V> r4, java.lang.Object r5) {
        /*
            r3 = this;
            r2 = 0
            boolean r0 = r4 instanceof com.google.common.util.concurrent.AbstractFuture.TrustedFuture
            if (r0 != 0) goto L_0x0014
            java.lang.Object r0 = com.google.common.util.concurrent.Uninterruptibles.getUninterruptibly(r4)     // Catch:{ ExecutionException -> 0x001c, CancellationException -> 0x0028, Throwable -> 0x002f }
            if (r0 == 0) goto L_0x0019
        L_0x000b:
            com.google.common.util.concurrent.AbstractFuture$AtomicHelper r1 = ATOMIC_HELPER
            boolean r0 = r1.casValue(r3, r5, r0)
            if (r0 != 0) goto L_0x0037
            return r2
        L_0x0014:
            com.google.common.util.concurrent.AbstractFuture r4 = (com.google.common.util.concurrent.AbstractFuture) r4
            java.lang.Object r0 = r4.value
            goto L_0x000b
        L_0x0019:
            java.lang.Object r0 = NULL     // Catch:{ ExecutionException -> 0x001c, CancellationException -> 0x0028, Throwable -> 0x002f }
            goto L_0x000b
        L_0x001c:
            r0 = move-exception
            com.google.common.util.concurrent.AbstractFuture$Failure r1 = new com.google.common.util.concurrent.AbstractFuture$Failure
            java.lang.Throwable r0 = r0.getCause()
            r1.<init>(r0)
        L_0x0026:
            r0 = r1
            goto L_0x000b
        L_0x0028:
            r0 = move-exception
            com.google.common.util.concurrent.AbstractFuture$Cancellation r1 = new com.google.common.util.concurrent.AbstractFuture$Cancellation
            r1.<init>(r2, r0)
            goto L_0x0026
        L_0x002f:
            r0 = move-exception
            com.google.common.util.concurrent.AbstractFuture$Failure r1 = new com.google.common.util.concurrent.AbstractFuture$Failure
            r1.<init>(r0)
            r0 = r1
            goto L_0x000b
        L_0x0037:
            r3.complete()
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.AbstractFuture.completeWithFuture(com.google.common.util.concurrent.ListenableFuture, java.lang.Object):boolean");
    }

    private static void executeListener(Runnable runnable, Executor executor) {
        try {
            executor.execute(runnable);
        } catch (RuntimeException e) {
            log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
        }
    }

    private V getDoneValue(Object obj) throws ExecutionException {
        if (obj instanceof Cancellation) {
            throw cancellationExceptionWithCause("Task was cancelled.", ((Cancellation) obj).cause);
        } else if (obj instanceof Failure) {
            throw new ExecutionException(((Failure) obj).exception);
        } else if (obj != NULL) {
            return obj;
        } else {
            return null;
        }
    }

    private Throwable newCancellationCause() {
        return new CancellationException("Future.cancel() was called.");
    }

    private void removeWaiter(Waiter waiter) {
        waiter.thread = null;
        while (true) {
            Waiter waiter2 = this.waiters;
            if (waiter2 != Waiter.TOMBSTONE) {
                Waiter waiter3 = null;
                while (waiter2 != null) {
                    Waiter waiter4 = waiter2.next;
                    if (waiter2.thread != null) {
                        waiter3 = waiter2;
                    } else if (waiter3 != null) {
                        waiter3.next = waiter4;
                        if (waiter3.thread != null) {
                        }
                    } else if (ATOMIC_HELPER.casWaiters(this, waiter2, waiter4)) {
                    }
                    waiter2 = waiter4;
                }
                return;
            }
            return;
        }
    }

    public void addListener(Runnable runnable, Executor executor) {
        Preconditions.checkNotNull(runnable, "Runnable was null.");
        Preconditions.checkNotNull(executor, "Executor was null.");
        Listener listener = this.listeners;
        if (listener == Listener.TOMBSTONE) {
            executeListener(runnable, executor);
        } else {
            Listener listener2 = new Listener(runnable, executor);
            do {
                listener2.next = listener;
                if (!ATOMIC_HELPER.casListeners(this, listener, listener2)) {
                    listener = this.listeners;
                } else {
                    return;
                }
            } while (listener != Listener.TOMBSTONE);
        }
        executeListener(runnable, executor);
    }

    public boolean cancel(boolean z) {
        Object obj = this.value;
        if ((obj == null) || (obj instanceof SetFuture)) {
            Cancellation cancellation = new Cancellation(z, !GENERATE_CANCELLATION_CAUSES ? null : newCancellationCause());
            Object obj2 = obj;
            while (!ATOMIC_HELPER.casValue(this, obj2, cancellation)) {
                obj2 = this.value;
                if (!(obj2 instanceof SetFuture)) {
                }
            }
            if (z) {
                interruptTask();
            }
            complete();
            if (obj2 instanceof SetFuture) {
                ((SetFuture) obj2).future.cancel(z);
            }
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void done() {
    }

    public V get() throws InterruptedException, ExecutionException {
        Object obj;
        if (!Thread.interrupted()) {
            Object obj2 = this.value;
            if ((obj2 != null) && (!(obj2 instanceof SetFuture))) {
                return getDoneValue(obj2);
            }
            Waiter waiter = this.waiters;
            if (waiter != Waiter.TOMBSTONE) {
                Waiter waiter2 = new Waiter();
                do {
                    waiter2.setNext(waiter);
                    if (!ATOMIC_HELPER.casWaiters(this, waiter, waiter2)) {
                        waiter = this.waiters;
                    } else {
                        do {
                            LockSupport.park(this);
                            if (!Thread.interrupted()) {
                                obj = this.value;
                            } else {
                                removeWaiter(waiter2);
                                throw new InterruptedException();
                            }
                        } while (!((obj != null) & (!(obj instanceof SetFuture))));
                        return getDoneValue(obj);
                    }
                } while (waiter != Waiter.TOMBSTONE);
            }
            return getDoneValue(this.value);
        }
        throw new InterruptedException();
    }

    public V get(long j, TimeUnit timeUnit) throws InterruptedException, TimeoutException, ExecutionException {
        long j2;
        boolean z;
        long nanos = timeUnit.toNanos(j);
        if (!Thread.interrupted()) {
            Object obj = this.value;
            if ((obj != null) && (!(obj instanceof SetFuture))) {
                return getDoneValue(obj);
            }
            long nanoTime = !((nanos > 0 ? 1 : (nanos == 0 ? 0 : -1)) <= 0) ? System.nanoTime() + nanos : 0;
            if (!(nanos < SPIN_THRESHOLD_NANOS)) {
                Waiter waiter = this.waiters;
                if (waiter != Waiter.TOMBSTONE) {
                    Waiter waiter2 = new Waiter();
                    do {
                        waiter2.setNext(waiter);
                        if (!ATOMIC_HELPER.casWaiters(this, waiter, waiter2)) {
                            waiter = this.waiters;
                        } else {
                            j2 = nanos;
                            do {
                                LockSupport.parkNanos(this, j2);
                                if (!Thread.interrupted()) {
                                    Object obj2 = this.value;
                                    if ((obj2 != null) && (!(obj2 instanceof SetFuture))) {
                                        return getDoneValue(obj2);
                                    }
                                    j2 = nanoTime - System.nanoTime();
                                    if (j2 >= SPIN_THRESHOLD_NANOS) {
                                        z = true;
                                        continue;
                                    } else {
                                        z = false;
                                        continue;
                                    }
                                } else {
                                    removeWaiter(waiter2);
                                    throw new InterruptedException();
                                }
                            } while (z);
                            removeWaiter(waiter2);
                        }
                    } while (waiter != Waiter.TOMBSTONE);
                }
                return getDoneValue(this.value);
            }
            j2 = nanos;
            while (true) {
                if (!(j2 <= 0)) {
                    Object obj3 = this.value;
                    if ((obj3 != null) && (!(obj3 instanceof SetFuture))) {
                        return getDoneValue(obj3);
                    }
                    if (!Thread.interrupted()) {
                        j2 = nanoTime - System.nanoTime();
                    } else {
                        throw new InterruptedException();
                    }
                } else {
                    throw new TimeoutException();
                }
            }
        } else {
            throw new InterruptedException();
        }
    }

    /* access modifiers changed from: protected */
    public void interruptTask() {
    }

    public boolean isCancelled() {
        return this.value instanceof Cancellation;
    }

    public boolean isDone() {
        boolean z = false;
        Object obj = this.value;
        boolean z2 = obj != null;
        if (!(obj instanceof SetFuture)) {
            z = true;
        }
        return z2 & z;
    }

    /* access modifiers changed from: package-private */
    public final void maybePropagateCancellation(@Nullable Future<?> future) {
        boolean z = false;
        if (future != null) {
            z = true;
        }
        if (z && isCancelled()) {
            future.cancel(wasInterrupted());
        }
    }

    /* access modifiers changed from: protected */
    public boolean set(@Nullable V v) {
        if (v == null) {
            v = NULL;
        }
        if (!ATOMIC_HELPER.casValue(this, (Object) null, v)) {
            return false;
        }
        complete();
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean setException(Throwable th) {
        if (!ATOMIC_HELPER.casValue(this, (Object) null, new Failure((Throwable) Preconditions.checkNotNull(th)))) {
            return false;
        }
        complete();
        return true;
    }

    /* access modifiers changed from: protected */
    @Beta
    public boolean setFuture(ListenableFuture<? extends V> listenableFuture) {
        SetFuture setFuture;
        Failure failure;
        Preconditions.checkNotNull(listenableFuture);
        Object obj = this.value;
        if (obj == null) {
            if (listenableFuture.isDone()) {
                return completeWithFuture(listenableFuture, (Object) null);
            }
            setFuture = new SetFuture(listenableFuture);
            if (!ATOMIC_HELPER.casValue(this, (Object) null, setFuture)) {
                obj = this.value;
            } else {
                try {
                    listenableFuture.addListener(setFuture, MoreExecutors.directExecutor());
                    return true;
                } catch (Throwable th) {
                    failure = Failure.FALLBACK_INSTANCE;
                }
            }
        }
        if (obj instanceof Cancellation) {
            listenableFuture.cancel(((Cancellation) obj).wasInterrupted);
        }
        return false;
        ATOMIC_HELPER.casValue(this, setFuture, failure);
        return true;
    }

    /* access modifiers changed from: package-private */
    public final Throwable trustedGetException() {
        return ((Failure) this.value).exception;
    }

    /* access modifiers changed from: protected */
    public final boolean wasInterrupted() {
        Object obj = this.value;
        return (obj instanceof Cancellation) && ((Cancellation) obj).wasInterrupted;
    }
}
