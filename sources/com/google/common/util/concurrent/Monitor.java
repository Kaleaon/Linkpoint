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
public final class Monitor {
    @GuardedBy("lock")
    private Guard activeGuards;
    private final boolean fair;
    /* access modifiers changed from: private */
    public final ReentrantLock lock;

    @Beta
    public static abstract class Guard {
        final Condition condition;
        @Weak
        final Monitor monitor;
        @GuardedBy("monitor.lock")
        Guard next;
        @GuardedBy("monitor.lock")
        int waiterCount = 0;

        protected Guard(Monitor monitor2) {
            this.monitor = (Monitor) Preconditions.checkNotNull(monitor2, "monitor");
            this.condition = monitor2.lock.newCondition();
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
                if (!z2) {
                    endWaitingFor(guard);
                }
                return false;
            }
            if (z2) {
                if (z) {
                    signalNextWaiter();
                }
                beginWaitingFor(guard);
                z2 = false;
            }
            try {
                j = guard.condition.awaitNanos(j);
            } catch (Throwable th) {
                if (!z2) {
                    endWaitingFor(guard);
                }
                throw th;
            }
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
        if (i == 0) {
            guard.next = this.activeGuards;
            this.activeGuards = guard;
        }
    }

    @GuardedBy("lock")
    private void endWaitingFor(Guard guard) {
        int i = guard.waiterCount - 1;
        guard.waiterCount = i;
        if (i == 0) {
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
    }

    private static long initNanoTime(long j) {
        if (!(j > 0)) {
            return 0;
        }
        long nanoTime = System.nanoTime();
        if (nanoTime == 0) {
            return 1;
        }
        return nanoTime;
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
        if (!(j2 > 0)) {
            return 0;
        }
        return j2 - (System.nanoTime() - j);
    }

    @GuardedBy("lock")
    private void signalAllWaiters() {
        for (Guard guard = this.activeGuards; guard != null; guard = guard.next) {
            guard.condition.signalAll();
        }
    }

    @GuardedBy("lock")
    private void signalNextWaiter() {
        Guard guard = this.activeGuards;
        while (guard != null) {
            if (!isSatisfied(guard)) {
                guard = guard.next;
            } else {
                guard.condition.signal();
                return;
            }
        }
    }

    private static long toSafeNanos(long j, TimeUnit timeUnit) {
        long nanos = timeUnit.toNanos(j);
        if (!(nanos > 0)) {
            return 0;
        }
        if (!(nanos <= 6917529027641081853L)) {
            return 6917529027641081853L;
        }
        return nanos;
    }

    public void enter() {
        this.lock.lock();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x003a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean enter(long r12, java.util.concurrent.TimeUnit r14) {
        /*
            r11 = this;
            r5 = 1
            long r2 = toSafeNanos(r12, r14)
            java.util.concurrent.locks.ReentrantLock r6 = r11.lock
            boolean r0 = r11.fair
            if (r0 == 0) goto L_0x001e
        L_0x000b:
            boolean r0 = java.lang.Thread.interrupted()
            long r8 = java.lang.System.nanoTime()     // Catch:{ all -> 0x0034 }
            r4 = r0
            r0 = r2
        L_0x0015:
            java.util.concurrent.TimeUnit r7 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch:{ InterruptedException -> 0x002d, all -> 0x0042 }
            boolean r0 = r6.tryLock(r0, r7)     // Catch:{ InterruptedException -> 0x002d, all -> 0x0042 }
            if (r4 != 0) goto L_0x0025
        L_0x001d:
            return r0
        L_0x001e:
            boolean r0 = r6.tryLock()
            if (r0 == 0) goto L_0x000b
            return r5
        L_0x0025:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L_0x001d
        L_0x002d:
            r0 = move-exception
            long r0 = remainingNanos(r8, r2)     // Catch:{ all -> 0x0044 }
            r4 = r5
            goto L_0x0015
        L_0x0034:
            r1 = move-exception
            r4 = r0
            r0 = r1
        L_0x0037:
            if (r4 != 0) goto L_0x003a
        L_0x0039:
            throw r0
        L_0x003a:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L_0x0039
        L_0x0042:
            r0 = move-exception
            goto L_0x0037
        L_0x0044:
            r0 = move-exception
            r4 = r5
            goto L_0x0037
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
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    public boolean enterIf(Guard guard, long j, TimeUnit timeUnit) {
        if (guard.monitor != this) {
            throw new IllegalMonitorStateException();
        } else if (!enter(j, timeUnit)) {
            return false;
        } else {
            try {
                boolean isSatisfied = guard.isSatisfied();
                if (!isSatisfied) {
                }
                return isSatisfied;
            } finally {
                this.lock.unlock();
            }
        }
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
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    public boolean enterIfInterruptibly(Guard guard, long j, TimeUnit timeUnit) throws InterruptedException {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            if (!reentrantLock.tryLock(j, timeUnit)) {
                return false;
            }
            try {
                boolean isSatisfied = guard.isSatisfied();
                if (!isSatisfied) {
                }
                return isSatisfied;
            } finally {
                reentrantLock.unlock();
            }
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    public void enterInterruptibly() throws InterruptedException {
        this.lock.lockInterruptibly();
    }

    public boolean enterInterruptibly(long j, TimeUnit timeUnit) throws InterruptedException {
        return this.lock.tryLock(j, timeUnit);
    }

    public void enterWhen(Guard guard) throws InterruptedException {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            boolean isHeldByCurrentThread = reentrantLock.isHeldByCurrentThread();
            reentrantLock.lockInterruptibly();
            try {
                if (!guard.isSatisfied()) {
                    await(guard, isHeldByCurrentThread);
                }
            } catch (Throwable th) {
                leave();
                throw th;
            }
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0067 A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
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
            if (r4 != r0) goto L_0x0036
            r0 = r16
            java.util.concurrent.locks.ReentrantLock r12 = r0.lock
            boolean r13 = r12.isHeldByCurrentThread()
            r0 = r16
            boolean r4 = r0.fair
            if (r4 == 0) goto L_0x003c
        L_0x001e:
            long r4 = initNanoTime(r10)
            r0 = r18
            r2 = r20
            boolean r14 = r12.tryLock(r0, r2)
            if (r14 == 0) goto L_0x0050
        L_0x002c:
            boolean r14 = r17.isSatisfied()     // Catch:{ all -> 0x006b }
            if (r14 == 0) goto L_0x0051
        L_0x0032:
            r4 = r9
        L_0x0033:
            if (r4 == 0) goto L_0x0067
        L_0x0035:
            return r4
        L_0x0036:
            java.lang.IllegalMonitorStateException r4 = new java.lang.IllegalMonitorStateException
            r4.<init>()
            throw r4
        L_0x003c:
            boolean r4 = java.lang.Thread.interrupted()
            if (r4 != 0) goto L_0x004a
            boolean r4 = r12.tryLock()
            if (r4 == 0) goto L_0x001e
            r4 = r6
            goto L_0x002c
        L_0x004a:
            java.lang.InterruptedException r4 = new java.lang.InterruptedException
            r4.<init>()
            throw r4
        L_0x0050:
            return r8
        L_0x0051:
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x0062
            r4 = r10
        L_0x0056:
            r0 = r16
            r1 = r17
            boolean r4 = r0.awaitNanos(r1, r4, r13)     // Catch:{ all -> 0x006b }
            if (r4 != 0) goto L_0x0032
            r4 = r8
            goto L_0x0033
        L_0x0062:
            long r4 = remainingNanos(r4, r10)     // Catch:{ all -> 0x006b }
            goto L_0x0056
        L_0x0067:
            r12.unlock()
            goto L_0x0035
        L_0x006b:
            r4 = move-exception
            if (r13 == 0) goto L_0x0072
        L_0x006e:
            r12.unlock()
            throw r4
        L_0x0072:
            r16.signalNextWaiter()     // Catch:{ all -> 0x0076 }
            goto L_0x006e
        L_0x0076:
            r4 = move-exception
            r12.unlock()
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Monitor.enterWhen(com.google.common.util.concurrent.Monitor$Guard, long, java.util.concurrent.TimeUnit):boolean");
    }

    public void enterWhenUninterruptibly(Guard guard) {
        if (guard.monitor == this) {
            ReentrantLock reentrantLock = this.lock;
            boolean isHeldByCurrentThread = reentrantLock.isHeldByCurrentThread();
            reentrantLock.lock();
            try {
                if (!guard.isSatisfied()) {
                    awaitUninterruptibly(guard, isHeldByCurrentThread);
                }
            } catch (Throwable th) {
                leave();
                throw th;
            }
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0034, code lost:
        if (r9.tryLock() != false) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004b, code lost:
        r0 = awaitNanos(r13, r6, r5);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean enterWhenUninterruptibly(com.google.common.util.concurrent.Monitor.Guard r13, long r14, java.util.concurrent.TimeUnit r16) {
        /*
            r12 = this;
            long r2 = toSafeNanos(r14, r16)
            com.google.common.util.concurrent.Monitor r0 = r13.monitor
            if (r0 != r12) goto L_0x002a
            java.util.concurrent.locks.ReentrantLock r9 = r12.lock
            r4 = 0
            boolean r8 = r9.isHeldByCurrentThread()
            boolean r0 = java.lang.Thread.interrupted()
            boolean r1 = r12.fair     // Catch:{ all -> 0x008e }
            if (r1 == 0) goto L_0x0030
        L_0x0018:
            long r6 = initNanoTime(r2)     // Catch:{ all -> 0x008e }
            r4 = r0
            r0 = r2
        L_0x001e:
            java.util.concurrent.TimeUnit r5 = java.util.concurrent.TimeUnit.NANOSECONDS     // Catch:{ InterruptedException -> 0x005f }
            boolean r0 = r9.tryLock(r0, r5)     // Catch:{ InterruptedException -> 0x005f }
            if (r0 != 0) goto L_0x0054
            if (r4 != 0) goto L_0x0057
        L_0x0028:
            r0 = 0
            return r0
        L_0x002a:
            java.lang.IllegalMonitorStateException r0 = new java.lang.IllegalMonitorStateException
            r0.<init>()
            throw r0
        L_0x0030:
            boolean r1 = r9.tryLock()     // Catch:{ all -> 0x008e }
            if (r1 == 0) goto L_0x0018
        L_0x0036:
            r10 = r0
            r0 = r4
            r5 = r8
            r4 = r10
        L_0x003a:
            boolean r6 = r13.isSatisfied()     // Catch:{ InterruptedException -> 0x007d, all -> 0x0081 }
            if (r6 != 0) goto L_0x0066
            r6 = 0
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 != 0) goto L_0x0068
            long r0 = initNanoTime(r2)     // Catch:{ InterruptedException -> 0x007d, all -> 0x0081 }
            r6 = r2
        L_0x004b:
            boolean r0 = r12.awaitNanos(r13, r6, r5)     // Catch:{ InterruptedException -> 0x007d, all -> 0x0081 }
        L_0x004f:
            if (r0 == 0) goto L_0x006d
        L_0x0051:
            if (r4 != 0) goto L_0x0075
        L_0x0053:
            return r0
        L_0x0054:
            r0 = r4
            r4 = r6
            goto L_0x0036
        L_0x0057:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            goto L_0x0028
        L_0x005f:
            r0 = move-exception
            r4 = 1
            long r0 = remainingNanos(r6, r2)     // Catch:{ all -> 0x0071 }
            goto L_0x001e
        L_0x0066:
            r0 = 1
            goto L_0x004f
        L_0x0068:
            long r6 = remainingNanos(r0, r2)     // Catch:{ InterruptedException -> 0x007d, all -> 0x0081 }
            goto L_0x004b
        L_0x006d:
            r9.unlock()     // Catch:{ all -> 0x0071 }
            goto L_0x0051
        L_0x0071:
            r0 = move-exception
        L_0x0072:
            if (r4 != 0) goto L_0x0086
        L_0x0074:
            throw r0
        L_0x0075:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L_0x0053
        L_0x007d:
            r4 = move-exception
            r4 = 1
            r5 = 0
            goto L_0x003a
        L_0x0081:
            r0 = move-exception
            r9.unlock()     // Catch:{ all -> 0x0071 }
            throw r0     // Catch:{ all -> 0x0071 }
        L_0x0086:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L_0x0074
        L_0x008e:
            r1 = move-exception
            r4 = r0
            r0 = r1
            goto L_0x0072
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Monitor.enterWhenUninterruptibly(com.google.common.util.concurrent.Monitor$Guard, long, java.util.concurrent.TimeUnit):boolean");
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
        } else {
            throw new IllegalMonitorStateException();
        }
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
            if (!reentrantLock.tryLock()) {
                return false;
            }
            try {
                boolean isSatisfied = guard.isSatisfied();
                if (!isSatisfied) {
                }
                return isSatisfied;
            } finally {
                reentrantLock.unlock();
            }
        } else {
            throw new IllegalMonitorStateException();
        }
    }

    public void waitFor(Guard guard) throws InterruptedException {
        boolean z = false;
        if (guard.monitor == this) {
            z = true;
        }
        if (!z || !this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        } else if (!guard.isSatisfied()) {
            await(guard, true);
        }
    }

    public boolean waitFor(Guard guard, long j, TimeUnit timeUnit) throws InterruptedException {
        boolean z = false;
        long safeNanos = toSafeNanos(j, timeUnit);
        if (guard.monitor == this) {
            z = true;
        }
        if (!z || !this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        } else if (guard.isSatisfied()) {
            return true;
        } else {
            if (!Thread.interrupted()) {
                return awaitNanos(guard, safeNanos, true);
            }
            throw new InterruptedException();
        }
    }

    public void waitForUninterruptibly(Guard guard) {
        boolean z = false;
        if (guard.monitor == this) {
            z = true;
        }
        if (!z || !this.lock.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException();
        } else if (!guard.isSatisfied()) {
            awaitUninterruptibly(guard, true);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:29:0x0058  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean waitForUninterruptibly(com.google.common.util.concurrent.Monitor.Guard r11, long r12, java.util.concurrent.TimeUnit r14) {
        /*
            r10 = this;
            r2 = 1
            r1 = 0
            long r6 = toSafeNanos(r12, r14)
            com.google.common.util.concurrent.Monitor r0 = r11.monitor
            if (r0 == r10) goto L_0x002b
            r0 = r1
        L_0x000b:
            java.util.concurrent.locks.ReentrantLock r3 = r10.lock
            boolean r3 = r3.isHeldByCurrentThread()
            r0 = r0 & r3
            if (r0 == 0) goto L_0x002d
            boolean r0 = r11.isSatisfied()
            if (r0 != 0) goto L_0x0033
            long r8 = initNanoTime(r6)
            boolean r0 = java.lang.Thread.interrupted()
            r4 = r6
            r3 = r2
        L_0x0024:
            boolean r1 = r10.awaitNanos(r11, r4, r3)     // Catch:{ InterruptedException -> 0x003c, all -> 0x0052 }
            if (r0 != 0) goto L_0x0034
        L_0x002a:
            return r1
        L_0x002b:
            r0 = r2
            goto L_0x000b
        L_0x002d:
            java.lang.IllegalMonitorStateException r0 = new java.lang.IllegalMonitorStateException
            r0.<init>()
            throw r0
        L_0x0033:
            return r2
        L_0x0034:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            goto L_0x002a
        L_0x003c:
            r0 = move-exception
            boolean r0 = r11.isSatisfied()     // Catch:{ all -> 0x0060 }
            if (r0 != 0) goto L_0x004a
            long r4 = remainingNanos(r8, r6)     // Catch:{ all -> 0x0060 }
            r0 = r2
            r3 = r1
            goto L_0x0024
        L_0x004a:
            java.lang.Thread r0 = java.lang.Thread.currentThread()
            r0.interrupt()
            return r2
        L_0x0052:
            r1 = move-exception
            r2 = r0
            r0 = r1
        L_0x0055:
            if (r2 != 0) goto L_0x0058
        L_0x0057:
            throw r0
        L_0x0058:
            java.lang.Thread r1 = java.lang.Thread.currentThread()
            r1.interrupt()
            goto L_0x0057
        L_0x0060:
            r0 = move-exception
            goto L_0x0055
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.util.concurrent.Monitor.waitForUninterruptibly(com.google.common.util.concurrent.Monitor$Guard, long, java.util.concurrent.TimeUnit):boolean");
    }
}
