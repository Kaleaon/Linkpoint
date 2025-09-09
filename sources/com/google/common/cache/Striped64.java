package com.google.common.cache;

import com.google.vr.cardboard.VrSettingsProviderContract;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

abstract class Striped64 extends Number {
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;
    static final Random rng = new Random();
    static final ThreadLocal<int[]> threadHashCode = new ThreadLocal<>();
    volatile transient long base;
    volatile transient int busy;
    volatile transient Cell[] cells;

    static final class Cell {
        private static final Unsafe UNSAFE;
        private static final long valueOffset;
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;
        volatile long value;

        static {
            try {
                UNSAFE = Striped64.getUnsafe();
                valueOffset = UNSAFE.objectFieldOffset(Cell.class.getDeclaredField(VrSettingsProviderContract.SETTING_VALUE_KEY));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        Cell(long j) {
            this.value = j;
        }

        /* access modifiers changed from: package-private */
        public final boolean cas(long j, long j2) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, j, j2);
        }
    }

    static {
        try {
            UNSAFE = getUnsafe();
            Class<Striped64> cls = Striped64.class;
            baseOffset = UNSAFE.objectFieldOffset(cls.getDeclaredField("base"));
            busyOffset = UNSAFE.objectFieldOffset(cls.getDeclaredField("busy"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    Striped64() {
    }

    /* access modifiers changed from: private */
    public static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException e) {
            try {
                return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
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
    }

    /* access modifiers changed from: package-private */
    public final boolean casBase(long j, long j2) {
        return UNSAFE.compareAndSwapLong(this, baseOffset, j, j2);
    }

    /* access modifiers changed from: package-private */
    public final boolean casBusy() {
        return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
    }

    /* access modifiers changed from: package-private */
    public abstract long fn(long j, long j2);

    /* access modifiers changed from: package-private */
    public final void internalReset(long j) {
        Cell[] cellArr = this.cells;
        this.base = j;
        if (cellArr != null) {
            for (Cell cell : cellArr) {
                if (cell != null) {
                    cell.value = j;
                }
            }
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: package-private */
    public final void retryUpdate(long j, int[] iArr, boolean z) {
        int nextInt;
        int length;
        if (iArr != null) {
            nextInt = iArr[0];
        } else {
            iArr = new int[1];
            threadHashCode.set(iArr);
            nextInt = rng.nextInt();
            if (nextInt == 0) {
                nextInt = 1;
            }
            iArr[0] = nextInt;
        }
        boolean z2 = false;
        while (true) {
            Cell[] cellArr = this.cells;
            if (cellArr != null && (length = cellArr.length) > 0) {
                Cell cell = cellArr[(length - 1) & nextInt];
                if (cell == null) {
                    if (this.busy == 0) {
                        Cell cell2 = new Cell(j);
                        if (this.busy == 0 && casBusy()) {
                            boolean z3 = false;
                            try {
                                Cell[] cellArr2 = this.cells;
                                if (cellArr2 != null) {
                                    int length2 = cellArr2.length;
                                    if (length2 > 0) {
                                        int i = (length2 - 1) & nextInt;
                                        if (cellArr2[i] == null) {
                                            cellArr2[i] = cell2;
                                            z3 = true;
                                        }
                                    }
                                }
                                if (z3) {
                                    return;
                                }
                            } finally {
                                this.busy = 0;
                            }
                        }
                    }
                    z2 = false;
                } else if (z) {
                    long j2 = cell.value;
                    if (cell.cas(j2, fn(j2, j))) {
                        return;
                    }
                    if (length >= NCPU || this.cells != cellArr) {
                        z2 = false;
                    } else if (!z2) {
                        z2 = true;
                    } else if (this.busy == 0 && casBusy()) {
                        try {
                            if (this.cells == cellArr) {
                                Cell[] cellArr3 = new Cell[(length << 1)];
                                for (int i2 = 0; i2 < length; i2++) {
                                    cellArr3[i2] = cellArr[i2];
                                }
                                this.cells = cellArr3;
                            }
                            this.busy = 0;
                            z2 = false;
                        } catch (Throwable th) {
                            this.busy = 0;
                            throw th;
                        }
                    }
                } else {
                    z = true;
                }
                int i3 = nextInt ^ (nextInt << 13);
                int i4 = i3 ^ (i3 >>> 17);
                nextInt = i4 ^ (i4 << 5);
                iArr[0] = nextInt;
            } else if (this.busy == 0 && this.cells == cellArr && casBusy()) {
                boolean z4 = false;
                try {
                    if (this.cells == cellArr) {
                        Cell[] cellArr4 = new Cell[2];
                        cellArr4[nextInt & 1] = new Cell(j);
                        this.cells = cellArr4;
                        z4 = true;
                    }
                    if (z4) {
                        return;
                    }
                } finally {
                    this.busy = 0;
                }
            } else {
                long j3 = this.base;
                if (casBase(j3, fn(j3, j))) {
                    return;
                }
            }
        }
    }
}
