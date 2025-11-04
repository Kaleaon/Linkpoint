// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import sun.misc.Unsafe;
import com.google.common.annotations.Beta;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import java.util.logging.Level;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Logger;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public abstract class AbstractFuture<V> implements ListenableFuture<V>
{
    private static final AtomicHelper ATOMIC_HELPER;
    private static final boolean GENERATE_CANCELLATION_CAUSES;
    private static final Object NULL;
    private static final long SPIN_THRESHOLD_NANOS = 1000L;
    private static final Logger log;
    private volatile Listener listeners;
    private volatile Object value;
    private volatile Waiter waiters;
    
    static {
        GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean(System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));
        log = Logger.getLogger(AbstractFuture.class.getName());
        try {
            final AtomicHelper atomic_HELPER = new UnsafeAtomicHelper();
            ATOMIC_HELPER = atomic_HELPER;
            NULL = new Object();
        }
        catch (final Throwable thrown) {
            try {
                final AtomicHelper atomic_HELPER = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, "next"), (AtomicReferenceFieldUpdater<AbstractFuture, Waiter>)AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, "waiters"), (AtomicReferenceFieldUpdater<AbstractFuture, Listener>)AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, "listeners"), (AtomicReferenceFieldUpdater<AbstractFuture, Object>)AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
            }
            catch (final Throwable thrown2) {
                AbstractFuture.log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", thrown);
                AbstractFuture.log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrown2);
                final AtomicHelper atomic_HELPER = new SynchronizedHelper();
            }
        }
    }
    
    protected AbstractFuture() {
    }
    
    static final CancellationException cancellationExceptionWithCause(@Nullable final String message, @Nullable final Throwable cause) {
        final CancellationException ex = new CancellationException(message);
        ex.initCause(cause);
        return ex;
    }
    
    private Listener clearListeners() {
        Listener listeners;
        do {
            listeners = this.listeners;
        } while (!AbstractFuture.ATOMIC_HELPER.casListeners(this, listeners, Listener.TOMBSTONE));
        return listeners;
    }
    
    private Waiter clearWaiters() {
        Waiter waiters;
        do {
            waiters = this.waiters;
        } while (!AbstractFuture.ATOMIC_HELPER.casWaiters(this, waiters, Waiter.TOMBSTONE));
        return waiters;
    }
    
    private void complete() {
        Listener next = null;
        for (Waiter waiter = this.clearWaiters(); waiter != null; waiter = waiter.next) {
            waiter.unpark();
        }
        Listener next2;
        for (Listener clearListeners = this.clearListeners(); clearListeners != null; clearListeners = next2) {
            next2 = clearListeners.next;
            clearListeners.next = next;
            next = clearListeners;
        }
        while (next != null) {
            executeListener(next.task, next.executor);
            next = next.next;
        }
        this.done();
    }
    
    private boolean completeWithFuture(final ListenableFuture<? extends V> listenableFuture, final Object o) {
        while (true) {
            if (!(listenableFuture instanceof TrustedFuture)) {
            Label_0091:
                while (true) {
                    try {
                        Object o2 = Uninterruptibles.getUninterruptibly((Future<Object>)listenableFuture);
                        if (o2 == null) {
                            o2 = AbstractFuture.NULL;
                        }
                        if (!AbstractFuture.ATOMIC_HELPER.casValue(this, o, o2)) {
                            return false;
                        }
                        break Label_0091;
                        o2 = ((AbstractFuture)listenableFuture).value;
                        continue;
                    }
                    catch (final ExecutionException ex) {
                        final Object o2 = new Failure(ex.getCause());
                    }
                    catch (final CancellationException ex2) {
                        final Object o2 = new Cancellation(false, ex2);
                        goto Label_0061;
                    }
                    catch (final Throwable t) {
                        final Object o2 = new Failure(t);
                        continue;
                    }
                    break;
                }
                this.complete();
                return true;
            }
            continue;
        }
    }
    
    private static void executeListener(final Runnable obj, final Executor obj2) {
        try {
            obj2.execute(obj);
        }
        catch (final RuntimeException thrown) {
            AbstractFuture.log.log(Level.SEVERE, "RuntimeException while executing runnable " + obj + " with executor " + obj2, thrown);
        }
    }
    
    private V getDoneValue(final Object o) throws ExecutionException {
        if (o instanceof Cancellation) {
            throw cancellationExceptionWithCause("Task was cancelled.", ((Cancellation)o).cause);
        }
        if (o instanceof Failure) {
            throw new ExecutionException(((Failure)o).exception);
        }
        if (o != AbstractFuture.NULL) {
            return (V)o;
        }
        return null;
    }
    
    private Throwable newCancellationCause() {
        return new CancellationException("Future.cancel() was called.");
    }
    
    private void removeWaiter(Waiter waiters) {
        waiters.thread = null;
    Label_0005:
        while (true) {
            waiters = this.waiters;
            if (waiters != Waiter.TOMBSTONE) {
                Waiter waiter = null;
                while (waiters != null) {
                    final Waiter next = waiters.next;
                    if (waiters.thread == null) {
                        if (waiter == null) {
                            if (!AbstractFuture.ATOMIC_HELPER.casWaiters(this, waiters, next)) {
                                continue Label_0005;
                            }
                        }
                        else {
                            waiter.next = next;
                            if (waiter.thread == null) {
                                continue Label_0005;
                            }
                        }
                    }
                    else {
                        waiter = waiters;
                    }
                    waiters = next;
                }
            }
        }
    }
    
    @Override
    public void addListener(final Runnable runnable, final Executor executor) {
        Preconditions.checkNotNull(runnable, (Object)"Runnable was null.");
        Preconditions.checkNotNull(executor, (Object)"Executor was null.");
        Listener next = this.listeners;
        Label_0028: {
            if (next != Listener.TOMBSTONE) {
                final Listener listener = new Listener(runnable, executor);
                while (true) {
                    listener.next = next;
                    if (AbstractFuture.ATOMIC_HELPER.casListeners(this, next, listener)) {
                        break;
                    }
                    next = this.listeners;
                    if (next != Listener.TOMBSTONE) {
                        continue;
                    }
                    break Label_0028;
                }
                return;
            }
        }
        executeListener(runnable, executor);
    }
    
    @Override
    public boolean cancel(final boolean b) {
        final Object value = this.value;
        if (value == null | value instanceof SetFuture) {
            Throwable cancellationCause;
            if (!AbstractFuture.GENERATE_CANCELLATION_CAUSES) {
                cancellationCause = null;
            }
            else {
                cancellationCause = this.newCancellationCause();
            }
            final Cancellation cancellation = new Cancellation(b, cancellationCause);
            Object value2 = value;
            while (!AbstractFuture.ATOMIC_HELPER.casValue(this, value2, cancellation)) {
                value2 = this.value;
                if (value2 instanceof SetFuture) {
                    continue;
                }
                return false;
            }
            if (b) {
                this.interruptTask();
            }
            this.complete();
            if (value2 instanceof SetFuture) {
                ((SetFuture)value2).future.cancel(b);
            }
            return true;
        }
        return false;
    }
    
    void done() {
    }
    
    @Override
    public V get() throws InterruptedException, ExecutionException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final Object value = this.value;
        boolean b;
        if (value == null) {
            b = false;
        }
        else {
            b = true;
        }
        boolean b2;
        if (value instanceof SetFuture) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (!(b & b2)) {
            Waiter next = this.waiters;
            if (next != Waiter.TOMBSTONE) {
                final Waiter waiter = new Waiter();
                while (true) {
                    waiter.setNext(next);
                    if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, next, waiter)) {
                        break;
                    }
                    next = this.waiters;
                    if (next != Waiter.TOMBSTONE) {
                        continue;
                    }
                    return this.getDoneValue(this.value);
                }
                while (true) {
                    LockSupport.park(this);
                    if (Thread.interrupted()) {
                        this.removeWaiter(waiter);
                        throw new InterruptedException();
                    }
                    final Object value2 = this.value;
                    boolean b3;
                    if (value2 == null) {
                        b3 = false;
                    }
                    else {
                        b3 = true;
                    }
                    boolean b4;
                    if (value2 instanceof SetFuture) {
                        b4 = false;
                    }
                    else {
                        b4 = true;
                    }
                    if (!(b3 & b4)) {
                        continue;
                    }
                    return this.getDoneValue(value2);
                }
            }
            return this.getDoneValue(this.value);
        }
        return this.getDoneValue(value);
    }
    
    @Override
    public V get(long nanos, final TimeUnit timeUnit) throws InterruptedException, TimeoutException, ExecutionException {
        nanos = timeUnit.toNanos(nanos);
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        final Object value = this.value;
        boolean b;
        if (value == null) {
            b = false;
        }
        else {
            b = true;
        }
        boolean b2;
        if (value instanceof SetFuture) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (b & b2) {
            return this.getDoneValue(value);
        }
        int n;
        if (nanos <= 0L) {
            n = 1;
        }
        else {
            n = 0;
        }
        long n2;
        if (n == 0) {
            n2 = System.nanoTime() + nanos;
        }
        else {
            n2 = 0L;
        }
        int n3;
        if (nanos < 1000L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        Label_0144: {
            if (n3 == 0) {
                Waiter next = this.waiters;
                if (next != Waiter.TOMBSTONE) {
                    final Waiter waiter = new Waiter();
                    while (true) {
                        waiter.setNext(next);
                        if (AbstractFuture.ATOMIC_HELPER.casWaiters(this, next, waiter)) {
                            break;
                        }
                        next = this.waiters;
                        if (next != Waiter.TOMBSTONE) {
                            continue;
                        }
                        return this.getDoneValue(this.value);
                    }
                    long nanos2 = nanos;
                    while (true) {
                        LockSupport.parkNanos(this, nanos2);
                        if (Thread.interrupted()) {
                            this.removeWaiter(waiter);
                            throw new InterruptedException();
                        }
                        final Object value2 = this.value;
                        boolean b3;
                        if (value2 == null) {
                            b3 = false;
                        }
                        else {
                            b3 = true;
                        }
                        boolean b4;
                        if (value2 instanceof SetFuture) {
                            b4 = false;
                        }
                        else {
                            b4 = true;
                        }
                        if (b3 & b4) {
                            return this.getDoneValue(value2);
                        }
                        nanos = n2 - System.nanoTime();
                        int n4;
                        if (nanos >= 1000L) {
                            n4 = 1;
                        }
                        else {
                            n4 = 0;
                        }
                        nanos2 = nanos;
                        if (n4 == 0) {
                            this.removeWaiter(waiter);
                            break Label_0144;
                        }
                    }
                }
                return this.getDoneValue(this.value);
            }
        }
        while (true) {
            int n5;
            if (nanos <= 0L) {
                n5 = 1;
            }
            else {
                n5 = 0;
            }
            if (n5 != 0) {
                throw new TimeoutException();
            }
            final Object value3 = this.value;
            boolean b5;
            if (value3 == null) {
                b5 = false;
            }
            else {
                b5 = true;
            }
            boolean b6;
            if (value3 instanceof SetFuture) {
                b6 = false;
            }
            else {
                b6 = true;
            }
            if (b5 & b6) {
                return this.getDoneValue(value3);
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            nanos = n2 - System.nanoTime();
        }
    }
    
    protected void interruptTask() {
    }
    
    @Override
    public boolean isCancelled() {
        return this.value instanceof Cancellation;
    }
    
    @Override
    public boolean isDone() {
        boolean b = false;
        final Object value = this.value;
        boolean b2;
        if (value == null) {
            b2 = false;
        }
        else {
            b2 = true;
        }
        if (!(value instanceof SetFuture)) {
            b = true;
        }
        return b2 & b;
    }
    
    final void maybePropagateCancellation(@Nullable final Future<?> future) {
        boolean b = false;
        if (future != null) {
            b = true;
        }
        if (b & this.isCancelled()) {
            future.cancel(this.wasInterrupted());
        }
    }
    
    protected boolean set(@Nullable V null) {
        if (null == null) {
            null = (V)AbstractFuture.NULL;
        }
        if (!AbstractFuture.ATOMIC_HELPER.casValue(this, null, null)) {
            return false;
        }
        this.complete();
        return true;
    }
    
    protected boolean setException(final Throwable t) {
        if (!AbstractFuture.ATOMIC_HELPER.casValue(this, null, new Failure(Preconditions.checkNotNull(t)))) {
            return false;
        }
        this.complete();
        return true;
    }
    
    @Beta
    protected boolean setFuture(final ListenableFuture<? extends V> listenableFuture) {
        Preconditions.checkNotNull(listenableFuture);
        Object o = this.value;
        Label_0112: {
            if (o == null) {
                if (listenableFuture.isDone()) {
                    return this.completeWithFuture(listenableFuture, null);
                }
                final SetFuture setFuture = new SetFuture(listenableFuture);
                if (AbstractFuture.ATOMIC_HELPER.casValue(this, null, setFuture)) {
                    try {
                        listenableFuture.addListener(setFuture, MoreExecutors.directExecutor());
                        return true;
                    }
                    catch (final Throwable t) {
                        try {
                            final Failure fallback_INSTANCE = new Failure(t);
                            AbstractFuture.ATOMIC_HELPER.casValue(this, setFuture, fallback_INSTANCE);
                        }
                        catch (final Throwable t2) {
                            final Failure fallback_INSTANCE = Failure.FALLBACK_INSTANCE;
                        }
                    }
                    break Label_0112;
                }
                o = this.value;
            }
            if (o instanceof Cancellation) {
                break Label_0112;
            }
            return false;
        }
        listenableFuture.cancel(((Cancellation)o).wasInterrupted);
        return false;
    }
    
    final Throwable trustedGetException() {
        return ((Failure)this.value).exception;
    }
    
    protected final boolean wasInterrupted() {
        final Object value = this.value;
        return value instanceof Cancellation && ((Cancellation)value).wasInterrupted;
    }
    
    private abstract static class AtomicHelper
    {
        abstract boolean casListeners(final AbstractFuture<?> p0, final Listener p1, final Listener p2);
        
        abstract boolean casValue(final AbstractFuture<?> p0, final Object p1, final Object p2);
        
        abstract boolean casWaiters(final AbstractFuture<?> p0, final Waiter p1, final Waiter p2);
        
        abstract void putNext(final Waiter p0, final Waiter p1);
        
        abstract void putThread(final Waiter p0, final Thread p1);
    }
    
    private static final class Cancellation
    {
        @Nullable
        final Throwable cause;
        final boolean wasInterrupted;
        
        Cancellation(final boolean wasInterrupted, @Nullable final Throwable cause) {
            this.wasInterrupted = wasInterrupted;
            this.cause = cause;
        }
    }
    
    private static final class Failure
    {
        static final Failure FALLBACK_INSTANCE;
        final Throwable exception;
        
        static {
            FALLBACK_INSTANCE = new Failure(new Throwable("Failure occurred while trying to finish a future.") {
                @Override
                public Throwable fillInStackTrace() {
                    monitorenter(this);
                    monitorexit(this);
                    return this;
                }
            });
        }
        
        Failure(final Throwable t) {
            this.exception = Preconditions.checkNotNull(t);
        }
    }
    
    private static final class Listener
    {
        static final Listener TOMBSTONE;
        final Executor executor;
        @Nullable
        Listener next;
        final Runnable task;
        
        static {
            TOMBSTONE = new Listener(null, null);
        }
        
        Listener(final Runnable task, final Executor executor) {
            this.task = task;
            this.executor = executor;
        }
    }
    
    private static final class SafeAtomicHelper extends AtomicHelper
    {
        final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater;
        final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater;
        final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater;
        
        SafeAtomicHelper(final AtomicReferenceFieldUpdater<Waiter, Thread> waiterThreadUpdater, final AtomicReferenceFieldUpdater<Waiter, Waiter> waiterNextUpdater, final AtomicReferenceFieldUpdater<AbstractFuture, Waiter> waitersUpdater, final AtomicReferenceFieldUpdater<AbstractFuture, Listener> listenersUpdater, final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
            this.waiterThreadUpdater = waiterThreadUpdater;
            this.waiterNextUpdater = waiterNextUpdater;
            this.waitersUpdater = waitersUpdater;
            this.listenersUpdater = listenersUpdater;
            this.valueUpdater = valueUpdater;
        }
        
        @Override
        boolean casListeners(final AbstractFuture<?> abstractFuture, final Listener listener, final Listener listener2) {
            return this.listenersUpdater.compareAndSet(abstractFuture, listener, listener2);
        }
        
        @Override
        boolean casValue(final AbstractFuture<?> abstractFuture, final Object o, final Object o2) {
            return this.valueUpdater.compareAndSet(abstractFuture, o, o2);
        }
        
        @Override
        boolean casWaiters(final AbstractFuture<?> abstractFuture, final Waiter waiter, final Waiter waiter2) {
            return this.waitersUpdater.compareAndSet(abstractFuture, waiter, waiter2);
        }
        
        @Override
        void putNext(final Waiter waiter, final Waiter waiter2) {
            this.waiterNextUpdater.lazySet(waiter, waiter2);
        }
        
        @Override
        void putThread(final Waiter waiter, final Thread thread) {
            this.waiterThreadUpdater.lazySet(waiter, thread);
        }
    }
    
    private final class SetFuture implements Runnable
    {
        final ListenableFuture<? extends V> future;
        
        SetFuture(final ListenableFuture<? extends V> future) {
            this.future = future;
        }
        
        @Override
        public void run() {
            if (AbstractFuture.this.value == this) {
                AbstractFuture.this.completeWithFuture(this.future, this);
            }
        }
    }
    
    private static final class SynchronizedHelper extends AtomicHelper
    {
        @Override
        boolean casListeners(final AbstractFuture<?> abstractFuture, final Listener listener, final Listener listener2) {
            synchronized (abstractFuture) {
                if (((AbstractFuture<Object>)abstractFuture).listeners != listener) {
                    return false;
                }
                ((AbstractFuture<Object>)abstractFuture).listeners = listener2;
                return true;
            }
        }
        
        @Override
        boolean casValue(final AbstractFuture<?> abstractFuture, final Object o, final Object o2) {
            synchronized (abstractFuture) {
                if (((AbstractFuture<Object>)abstractFuture).value != o) {
                    return false;
                }
                ((AbstractFuture<Object>)abstractFuture).value = o2;
                return true;
            }
        }
        
        @Override
        boolean casWaiters(final AbstractFuture<?> abstractFuture, final Waiter waiter, final Waiter waiter2) {
            synchronized (abstractFuture) {
                if (((AbstractFuture<Object>)abstractFuture).waiters != waiter) {
                    return false;
                }
                ((AbstractFuture<Object>)abstractFuture).waiters = waiter2;
                return true;
            }
        }
        
        @Override
        void putNext(final Waiter waiter, final Waiter next) {
            waiter.next = next;
        }
        
        @Override
        void putThread(final Waiter waiter, final Thread thread) {
            waiter.thread = thread;
        }
    }
    
    abstract static class TrustedFuture<V> extends AbstractFuture<V>
    {
        @Override
        public final void addListener(final Runnable runnable, final Executor executor) {
            super.addListener(runnable, executor);
        }
        
        @Override
        public final V get() throws InterruptedException, ExecutionException {
            return super.get();
        }
        
        @Override
        public final V get(final long n, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
            return super.get(n, timeUnit);
        }
        
        @Override
        public final boolean isCancelled() {
            return super.isCancelled();
        }
        
        @Override
        public final boolean isDone() {
            return super.isDone();
        }
    }
    
    private static final class UnsafeAtomicHelper extends AtomicHelper
    {
        static final long LISTENERS_OFFSET;
        static final Unsafe UNSAFE;
        static final long VALUE_OFFSET;
        static final long WAITERS_OFFSET;
        static final long WAITER_NEXT_OFFSET;
        static final long WAITER_THREAD_OFFSET;
        
        static {
            // 
            // This method could not be decompiled.
            // 
            // Original Bytecode:
            // 
            //     3: astore_0       
            //     4: aload_0        
            //     5: ldc             Lcom/google/common/util/concurrent/AbstractFuture;.class
            //     7: ldc             "waiters"
            //     9: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
            //    12: invokevirtual   sun/misc/Unsafe.objectFieldOffset:(Ljava/lang/reflect/Field;)J
            //    15: putstatic       com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper.WAITERS_OFFSET:J
            //    18: aload_0        
            //    19: ldc             Lcom/google/common/util/concurrent/AbstractFuture;.class
            //    21: ldc             "listeners"
            //    23: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
            //    26: invokevirtual   sun/misc/Unsafe.objectFieldOffset:(Ljava/lang/reflect/Field;)J
            //    29: putstatic       com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper.LISTENERS_OFFSET:J
            //    32: aload_0        
            //    33: ldc             Lcom/google/common/util/concurrent/AbstractFuture;.class
            //    35: ldc             "value"
            //    37: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
            //    40: invokevirtual   sun/misc/Unsafe.objectFieldOffset:(Ljava/lang/reflect/Field;)J
            //    43: putstatic       com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper.VALUE_OFFSET:J
            //    46: aload_0        
            //    47: ldc             Lcom/google/common/util/concurrent/AbstractFuture$Waiter;.class
            //    49: ldc             "thread"
            //    51: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
            //    54: invokevirtual   sun/misc/Unsafe.objectFieldOffset:(Ljava/lang/reflect/Field;)J
            //    57: putstatic       com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper.WAITER_THREAD_OFFSET:J
            //    60: aload_0        
            //    61: ldc             Lcom/google/common/util/concurrent/AbstractFuture$Waiter;.class
            //    63: ldc             "next"
            //    65: invokevirtual   java/lang/Class.getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;
            //    68: invokevirtual   sun/misc/Unsafe.objectFieldOffset:(Ljava/lang/reflect/Field;)J
            //    71: putstatic       com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper.WAITER_NEXT_OFFSET:J
            //    74: aload_0        
            //    75: putstatic       com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper.UNSAFE:Lsun/misc/Unsafe;
            //    78: return         
            //    79: astore_0       
            //    80: new             Lcom/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper$1;
            //    83: astore_0       
            //    84: aload_0        
            //    85: invokespecial   com/google/common/util/concurrent/AbstractFuture$UnsafeAtomicHelper$1.<init>:()V
            //    88: aload_0        
            //    89: invokestatic    java/security/AccessController.doPrivileged:(Ljava/security/PrivilegedExceptionAction;)Ljava/lang/Object;
            //    92: checkcast       Lsun/misc/Unsafe;
            //    95: astore_0       
            //    96: goto            4
            //    99: astore_0       
            //   100: new             Ljava/lang/RuntimeException;
            //   103: dup            
            //   104: ldc             "Could not initialize intrinsics"
            //   106: aload_0        
            //   107: invokevirtual   java/security/PrivilegedActionException.getCause:()Ljava/lang/Throwable;
            //   110: invokespecial   java/lang/RuntimeException.<init>:(Ljava/lang/String;Ljava/lang/Throwable;)V
            //   113: athrow         
            //   114: astore_0       
            //   115: aload_0        
            //   116: invokestatic    com/google/common/base/Throwables.propagate:(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
            //   119: athrow         
            //    Exceptions:
            //  Try           Handler
            //  Start  End    Start  End    Type                                     
            //  -----  -----  -----  -----  -----------------------------------------
            //  0      4      79     114    Ljava/lang/SecurityException;
            //  4      78     114    120    Ljava/lang/Exception;
            //  80     96     99     114    Ljava/security/PrivilegedActionException;
            // 
            // The error that occurred was:
            // 
            // java.lang.IllegalStateException: Expression is linked from several locations: Label_0004:
            //     at com.strobel.decompiler.ast.Error.expressionLinkedFromMultipleLocations(Error.java:27)
            //     at com.strobel.decompiler.ast.AstOptimizer.mergeDisparateObjectInitializations(AstOptimizer.java:2604)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:235)
            //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:206)
            //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:662)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
            //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
            //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
            //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
            //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
            //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
            // 
            throw new IllegalStateException("An error occurred while decompiling this method.");
        }
        
        @Override
        boolean casListeners(final AbstractFuture<?> o, final Listener expected, final Listener x) {
            return UnsafeAtomicHelper.UNSAFE.compareAndSwapObject(o, UnsafeAtomicHelper.LISTENERS_OFFSET, expected, x);
        }
        
        @Override
        boolean casValue(final AbstractFuture<?> o, final Object expected, final Object x) {
            return UnsafeAtomicHelper.UNSAFE.compareAndSwapObject(o, UnsafeAtomicHelper.VALUE_OFFSET, expected, x);
        }
        
        @Override
        boolean casWaiters(final AbstractFuture<?> o, final Waiter expected, final Waiter x) {
            return UnsafeAtomicHelper.UNSAFE.compareAndSwapObject(o, UnsafeAtomicHelper.WAITERS_OFFSET, expected, x);
        }
        
        @Override
        void putNext(final Waiter o, final Waiter x) {
            UnsafeAtomicHelper.UNSAFE.putObject(o, UnsafeAtomicHelper.WAITER_NEXT_OFFSET, x);
        }
        
        @Override
        void putThread(final Waiter o, final Thread x) {
            UnsafeAtomicHelper.UNSAFE.putObject(o, UnsafeAtomicHelper.WAITER_THREAD_OFFSET, x);
        }
    }
    
    private static final class Waiter
    {
        static final Waiter TOMBSTONE;
        @Nullable
        volatile Waiter next;
        @Nullable
        volatile Thread thread;
        
        static {
            TOMBSTONE = new Waiter(false);
        }
        
        Waiter() {
            AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
        }
        
        Waiter(final boolean b) {
        }
        
        void setNext(final Waiter waiter) {
            AbstractFuture.ATOMIC_HELPER.putNext(this, waiter);
        }
        
        void unpark() {
            final Thread thread = this.thread;
            if (thread != null) {
                this.thread = null;
                LockSupport.unpark(thread);
            }
        }
    }
}
