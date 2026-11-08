package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.j2objc.annotations.Weak;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;
@Beta
/* loaded from: classes.dex */
public final class Monitor {
    @GuardedBy("lock")
    private Guard activeGuards;
    private final boolean fair;
    private final ReentrantLock lock;

    @Beta
    /* loaded from: classes.dex */
    public static abstract class Guard {
        final Condition condition;
        @Weak
        final Monitor monitor;
        @GuardedBy("monitor.lock")
        Guard next;
        @GuardedBy("monitor.lock")
        int waiterCount = 0;

        /* JADX INFO: Access modifiers changed from: protected */
        public Guard(Monitor monitor) {
            this.monitor = (Monitor) Preconditions.checkNotNull(monitor, "monitor");
            this.condition = monitor.lock.newCondition();
        }

        public abstract boolean isSatisfied();
    }

    public Monitor() {
        this(false);
    }

    public Monitor(boolean z) {
        this.activeGuards = null;
        this.fair = z;
        this.lock = new ReentrantLock(z);
    }

    @GuardedBy("lock")
    private void await(Guard guard, boolean z) throws InterruptedException {
        if (z) {
            signalNextWaiter();
        }
        beginWaitingFor(guard);
        do {
            try {
                guard.condition.await();
            } finally {
                endWaitingFor(guard);
            }
        } while (!guard.isSatisfied());
    }

    @GuardedBy("lock")
    private boolean awaitNanos(Guard guard, long j, boolean z) throws InterruptedException {
        boolean z2 = true;
        do {
            if (!(j > 0)) {
                return false;
            }
            if (z2) {
                if (z) {
                    try {
                        signalNextWaiter();
                    } finally {
                        if (!z2) {
                            endWaitingFor(guard);
                        }
                    }
                }
                beginWaitingFor(guard);
                z2 = false;
            }
            j = guard.condition.awaitNanos(j);
        } while (!guard.isSatisfied());
        if (!z2) {
            endWaitingFor(guard);
        }
        return true;
    }

    @GuardedBy("lock")
    private void awaitUninterruptibly(Guard guard, boolean z) {
        if (z) {
            signalNextWaiter();
        }
        beginWaitingFor(guard);
        do {
            try {
                guard.condition.awaitUninterruptibly();
            } finally {
                endWaitingFor(guard);
            }
        } while (!guard.isSatisfied());
    }

    @GuardedBy("lock")
    private void beginWaitingFor(Guard guard) {
        int i = guard.waiterCount;
        guard.waiterCount = i + 1;
        if (i != 0) {
            return;
        }
        guard.next = this.activeGuards;
        this.activeGuards = guard;
    }

    @GuardedBy("lock")
    private void endWaitingFor(Guard guard) {
        int i = guard.waiterCount - 1;
        guard.waiterCount = i;
        if (i != 0) {
            return;
        }
        Guard guard2 = this.activeGuards;
        Guard guard3 = null;
        while (guard2 != guard) {
            Guard guard4 = guard2;
            guard2 = guard2.next;
            guard3 = guard4;
        }
        if (guard3 != null) {
            guard3.next = guard2.next;
        } else {
            this.activeGuards = guard2.next;
        }
        guard2.next = null;
    }

    private static long initNanoTime(long j) {
        if (j > 0) {
            long nanoTime = System.nanoTime();
            if (nanoTime == 0) {
                return 1L;
            }
            return nanoTime;
        }
        return 0L;
    }

    @GuardedBy("lock")
    private boolean isSatisfied(Guard guard) {
        try {
            return guard.isSatisfied();
        } catch (Throwable th) {
            signalAllWaiters();
            throw Throwables.propagate(th);
        }
    }

    private static long remainingNanos(long j, long j2) {
        if (j2 > 0) {
            return j2 - (System.nanoTime() - j);
        }
        return 0L;
    }

    @GuardedBy("lock")
    private void signalAllWaiters() {
        for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
            guard.condition.signalAll();
        }
    }

    @GuardedBy("lock")
    private void signalNextWaiter() {
        for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
            if (isSatisfied(guard)) {
                guard.condition.signal();
                return;
            }
        }
    }

    private static long toSafeNanos(long j, TimeUnit timeUnit) {
        long nanos = timeUnit.toNanos(j);
        if (nanos > 0) {
            if (nanos <= 6917529027641081853L) {
                return nanos;
            }
            return 6917529027641081853L;
        }
        return 0L;
    }

    public void enter() {
        this.lock.lock();
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean enter(long r12, java.util.concurrent.TimeUnit r14) {
        /*
            r11 = this;
            r5 = 1
            long r2 = toSafeNanos(r12, r14)
            java.util.concurrent.locks.ReentrantLock r6 = r11.lock
            boolean r0 = r11.fair
            if (r0 == 0) goto L1e
        Lb:
            boolean r0 = java.lang.Thread.interrupted()
            long r8 = java.lang.System.nanoTime()     // Catch: java.lang.Throwable -> L34
            r4 = r0
            r0 = r2
        L15:
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch: java.lang.InterruptedException -> L2d java.lang.Throwable -> L42
            boolean r0 = r6.tryLock(r0, r7)     // Catch: java.lang.InterruptedException -> L2d java.lang.Throwable -> L42
            if (r4 != 0) goto L25
        L1d:
            return r0
        L1e:
            boolean r0 = r6.tryLock()
            if (r0 == 0) goto Lb
            return r5
        L25:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L1d
        L2d:
            r0 = move-exception
            long r0 = remainingNanos(r8, r2)     // Catch: java.lang.Throwable -> L44
            r4 = r5
            goto L15
        L34:
            r1 = move-exception
            r4 = r0
            r0 = r1
        L37:
            if (r4 != 0) goto L3a
        L39:
            throw r0
        L3a:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L39
        L42:
            r0 = move-exception
            goto L37
        L44:
            r0 = move-exception
            r4 = r5
            goto L37
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Monitor.enter(long, java.util.concurrent.TimeUnit):boolean");
    }

    public boolean enterIf(Guard guard) {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            reentrantLock.lock();
            try {
                boolean isSatisfied = guard.isSatisfied();
                if (!isSatisfied) {
                }
                return isSatisfied;
            } finally {
                reentrantLock.unlock();
            }
        }
        throw new IllegalMonitorStateException();
    }

    public boolean enterIf(Guard guard, long j, TimeUnit timeUnit) {
        if (guard.monitor == this) {
            if (enter(j, timeUnit)) {
                try {
                    boolean isSatisfied = guard.isSatisfied();
                    if (!isSatisfied) {
                    }
                    return isSatisfied;
                } finally {
                    this.lock.unlock();
                }
            }
            return false;
        }
        throw new IllegalMonitorStateException();
    }

    public boolean enterIfInterruptibly(Guard guard) throws InterruptedException {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            reentrantLock.lockInterruptibly();
            try {
                boolean isSatisfied = guard.isSatisfied();
                if (!isSatisfied) {
                }
                return isSatisfied;
            } finally {
                reentrantLock.unlock();
            }
        }
        throw new IllegalMonitorStateException();
    }

    public boolean enterIfInterruptibly(Guard guard, long j, TimeUnit timeUnit) throws InterruptedException {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            if (reentrantLock.tryLock(j, timeUnit)) {
                try {
                    boolean isSatisfied = guard.isSatisfied();
                    if (!isSatisfied) {
                    }
                    return isSatisfied;
                } finally {
                    reentrantLock.unlock();
                }
            }
            return false;
        }
        throw new IllegalMonitorStateException();
    }

    public void enterInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    public boolean enterInterruptibly(long j, TimeUnit timeUnit) throws InterruptedException {
        return this.lock.tryLock(j, timeUnit);
    }

    public void enterWhen(Guard guard) throws InterruptedException {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock reentrantLock = this.lock;
        boolean isHeldByCurrentThread = reentrantLock.isHeldByCurrentThread();
        reentrantLock.lockInterruptibly();
        try {
            if (guard.isSatisfied()) {
                return;
            }
            await(guard, isHeldByCurrentThread);
        } catch (Throwable th) {
            leave();
            throw th;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0051  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0067 A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean enterWhen(com.google.common.util.concurrent.Monitor.Guard r17, long r18, java.util.concurrent.TimeUnit r20) throws java.lang.InterruptedException {
        /*
            r16 = this;
            r6 = 0
            r9 = 1
            r8 = 0
            long r10 = toSafeNanos(r18, r20)
            r0 = r17
            com.google.common.util.concurrent.Monitor r4 = r0.monitor
            r0 = r16
            if (r4 != r0) goto L36
            r0 = r16
            java.util.concurrent.locks.ReentrantLock r12 = r0.lock
            boolean r13 = r12.isHeldByCurrentThread()
            r0 = r16
            boolean r4 = r0.fair
            if (r4 == 0) goto L3c
        L1e:
            long r4 = initNanoTime(r10)
            r0 = r18
            r2 = r20
            boolean r14 = r12.tryLock(r0, r2)
            if (r14 == 0) goto L50
        L2c:
            boolean r14 = r17.isSatisfied()     // Catch: java.lang.Throwable -> L6b
            if (r14 == 0) goto L51
        L32:
            r4 = r9
        L33:
            if (r4 == 0) goto L67
        L35:
            return r4
        L36:
            java.lang.IllegalMonitorStateException r4 = new java.lang.IllegalMonitorStateException
            r4.<init>()
            throw r4
        L3c:
            boolean r4 = java.lang.Thread.interrupted()
            if (r4 != 0) goto L4a
            boolean r4 = r12.tryLock()
            if (r4 == 0) goto L1e
            r4 = r6
            goto L2c
        L4a:
            java.lang.InterruptedException r4 = new java.lang.InterruptedException
            r4.<init>()
            throw r4
        L50:
            return r8
        L51:
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 != 0) goto L62
            r4 = r10
        L56:
            r0 = r16
            r1 = r17
            boolean r4 = r0.awaitNanos(r1, r4, r13)     // Catch: java.lang.Throwable -> L6b
            if (r4 != 0) goto L32
            r4 = r8
            goto L33
        L62:
            long r4 = remainingNanos(r4, r10)     // Catch: java.lang.Throwable -> L6b
            goto L56
        L67:
            r12.unlock()
            goto L35
        L6b:
            r4 = move-exception
            if (r13 == 0) goto L72
        L6e:
            r12.unlock()
            throw r4
        L72:
            r16.signalNextWaiter()     // Catch: java.lang.Throwable -> L76
            goto L6e
        L76:
            r4 = move-exception
            r12.unlock()
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Monitor.enterWhen(com.google.common.util.concurrent.Monitor$Guard, long, java.util.concurrent.TimeUnit):boolean");
    }

    public void enterWhenUninterruptibly(Guard guard) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock reentrantLock = this.lock;
        boolean isHeldByCurrentThread = reentrantLock.isHeldByCurrentThread();
        reentrantLock.lock();
        try {
            if (guard.isSatisfied()) {
                return;
            }
            awaitUninterruptibly(guard, isHeldByCurrentThread);
        } catch (Throwable th) {
            leave();
            throw th;
        }
    }

    public boolean enterWhenUninterruptibly(Guard guard, long j, TimeUnit timeUnit) {
        boolean z;
        Throwable th;
        boolean z2;
        long remainingNanos;
        long safeNanos = toSafeNanos(j, timeUnit);
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        }
        ReentrantLock reentrantLock = this.lock;
        long j2 = 0;
        boolean isHeldByCurrentThread = reentrantLock.isHeldByCurrentThread();
        boolean interrupted = Thread.interrupted();
        try {
            if (this.fair || !reentrantLock.tryLock()) {
                long initNanoTime = initNanoTime(safeNanos);
                z = interrupted;
                long j3 = safeNanos;
                while (true) {
                    try {
                        try {
                            break;
                        } catch (InterruptedException e) {
                            z = true;
                            j3 = remainingNanos(initNanoTime, safeNanos);
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        if (z) {
                            Thread.currentThread().interrupt();
                        }
                        throw th;
                    }
                }
                if (!reentrantLock.tryLock(j3, TimeUnit.NANOSECONDS)) {
                    if (z) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                    return false;
                }
                interrupted = z;
                j2 = initNanoTime;
            }
            boolean z3 = interrupted;
            long j4 = j2;
            boolean z4 = isHeldByCurrentThread;
            z = z3;
            while (true) {
                try {
                    if (!guard.isSatisfied()) {
                        if (j4 == 0) {
                            initNanoTime(safeNanos);
                            remainingNanos = safeNanos;
                        } else {
                            remainingNanos = remainingNanos(j4, safeNanos);
                        }
                        z2 = awaitNanos(guard, remainingNanos, z4);
                        break;
                    }
                    z2 = true;
                    break;
                } catch (InterruptedException e2) {
                    z = true;
                    z4 = false;
                } catch (Throwable th3) {
                    reentrantLock.unlock();
                    throw th3;
                }
            }
            if (!z2) {
                reentrantLock.unlock();
            }
            if (z) {
                Thread.currentThread().interrupt();
            }
            return z2;
        } catch (Throwable th4) {
            z = interrupted;
            th = th4;
        }
    }

    public int getOccupiedDepth() {
        return this.lock.getHoldCount();
    }

    public int getQueueLength() {
        return this.lock.getQueueLength();
    }

    public int getWaitQueueLength(Guard guard) {
        if (guard.monitor == this) {
            this.lock.lock();
            try {
                return guard.waiterCount;
            } finally {
                this.lock.unlock();
            }
        }
        throw new IllegalMonitorStateException();
    }

    public boolean hasQueuedThread(Thread thread) {
        return this.lock.hasQueuedThread(thread);
    }

    public boolean hasQueuedThreads() {
        return this.lock.hasQueuedThreads();
    }

    public boolean hasWaiters(Guard guard) {
        return getWaitQueueLength(guard) > 0;
    }

    public boolean isFair() {
        return this.fair;
    }

    public boolean isOccupied() {
        return this.lock.isLocked();
    }

    public boolean isOccupiedByCurrentThread() {
        return this.lock.isHeldByCurrentThread();
    }

    public void leave() {
        ReentrantLock reentrantLock = this.lock;
        try {
            if (reentrantLock.getHoldCount() == 1) {
                signalNextWaiter();
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public boolean tryEnter() {
        return this.lock.tryLock();
    }

    public boolean tryEnterIf(Guard guard) {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            if (reentrantLock.tryLock()) {
                try {
                    boolean isSatisfied = guard.isSatisfied();
                    if (!isSatisfied) {
                    }
                    return isSatisfied;
                } finally {
                    reentrantLock.unlock();
                }
            }
            return false;
        }
        throw new IllegalMonitorStateException();
    }

    public void waitFor(Guard guard) throws InterruptedException {
        if (!(guard.monitor == this) || !this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        if (guard.isSatisfied()) {
            return;
        }
        await(guard, true);
    }

    public boolean waitFor(Guard guard, long j, TimeUnit timeUnit) throws InterruptedException {
        long safeNanos = toSafeNanos(j, timeUnit);
        if ((guard.monitor == this) && this.lock.isHeldByCurrentThread()) {
            if (guard.isSatisfied()) {
                return true;
            }
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            return awaitNanos(guard, safeNanos, true);
        }
        throw new IllegalMonitorStateException();
    }

    public void waitForUninterruptibly(Guard guard) {
        if (!(guard.monitor == this) || !this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        }
        if (guard.isSatisfied()) {
            return;
        }
        awaitUninterruptibly(guard, true);
    }

    /* JADX WARN: Removed duplicated region for block: B:29:0x0058  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean waitForUninterruptibly(com.google.common.util.concurrent.Monitor.Guard r11, long r12, java.util.concurrent.TimeUnit r14) {
        /*
            r10 = this;
            r2 = 1
            r1 = 0
            long r6 = toSafeNanos(r12, r14)
            com.google.common.util.concurrent.Monitor r0 = r11.monitor
            if (r0 == r10) goto L2b
            r0 = r1
        Lb:
            java.util.concurrent.locks.ReentrantLock r3 = r10.lock
            boolean r3 = r3.isHeldByCurrentThread()
            r0 = r0 & r3
            if (r0 == 0) goto L2d
            boolean r0 = r11.isSatisfied()
            if (r0 != 0) goto L33
            long r8 = initNanoTime(r6)
            boolean r0 = java.lang.Thread.interrupted()
            r4 = r6
            r3 = r2
        L24:
            boolean r1 = r10.awaitNanos(r11, r4, r3)     // Catch: java.lang.InterruptedException -> L3c java.lang.Throwable -> L52
            if (r0 != 0) goto L34
        L2a:
            return r1
        L2b:
            r0 = r2
            goto Lb
        L2d:
            java.lang.IllegalMonitorStateException r0 = new java.lang.IllegalMonitorStateException
            r0.<init>()
            throw r0
        L33:
            return r2
        L34:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            goto L2a
        L3c:
            r0 = move-exception
            boolean r0 = r11.isSatisfied()     // Catch: java.lang.Throwable -> L60
            if (r0 != 0) goto L4a
            long r4 = remainingNanos(r8, r6)     // Catch: java.lang.Throwable -> L60
            r0 = r2
            r3 = r1
            goto L24
        L4a:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            return r2
        L52:
            r1 = move-exception
            r2 = r0
            r0 = r1
        L55:
            if (r2 != 0) goto L58
        L57:
            throw r0
        L58:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L57
        L60:
            r0 = move-exception
            goto L55
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Monitor.waitForUninterruptibly(com.google.common.util.concurrent.Monitor$Guard, long, java.util.concurrent.TimeUnit):boolean");
    }
}
