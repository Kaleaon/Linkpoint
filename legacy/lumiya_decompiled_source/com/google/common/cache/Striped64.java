package com.google.common.cache;

import com.google.vr.cardboard.VrSettingsProviderContract;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;
/* loaded from: classes.dex */
abstract class Striped64 extends Number {
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;
    volatile transient long base;
    volatile transient int busy;
    volatile transient Cell[] cells;
    static final ThreadLocal<int[]> threadHashCode = new ThreadLocal<>();
    static final Random rng = new Random();
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /* loaded from: classes.dex */
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
                UNSAFE = Striped64.access$000();
                valueOffset = UNSAFE.objectFieldOffset(Cell.class.getDeclaredField(VrSettingsProviderContract.SETTING_VALUE_KEY));
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        Cell(long j) {
            this.value = j;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final boolean cas(long j, long j2) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, j, j2);
        }
    }

    static {
        try {
            UNSAFE = getUnsafe();
            baseOffset = UNSAFE.objectFieldOffset(Striped64.class.getDeclaredField("base"));
            busyOffset = UNSAFE.objectFieldOffset(Striped64.class.getDeclaredField("busy"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    static /* synthetic */ Unsafe access$000() {
        return getUnsafe();
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException e) {
            try {
                return (Unsafe) AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() { // from class: com.google.common.cache.Striped64.1
                    @Override // java.security.PrivilegedExceptionAction
                    public Unsafe run() throws Exception {
                        Field[] declaredFields;
                        for (Field field : Unsafe.class.getDeclaredFields()) {
                            field.setAccessible(true);
                            Object obj = field.get(null);
                            if (Unsafe.class.isInstance(obj)) {
                                return (Unsafe) Unsafe.class.cast(obj);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean casBase(long j, long j2) {
        return UNSAFE.compareAndSwapLong(this, baseOffset, j, j2);
    }

    final boolean casBusy() {
        return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
    }

    abstract long fn(long j, long j2);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalReset(long j) {
        Cell[] cellArr = this.cells;
        this.base = j;
        if (cellArr == null) {
            return;
        }
        for (Cell cell : cellArr) {
            if (cell != null) {
                cell.value = j;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void retryUpdate(long j, int[] iArr, boolean z) {
        int nextInt;
        int length;
        int length2;
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
                if (cell != null) {
                    if (z) {
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
                                    Cell[] cellArr2 = new Cell[length << 1];
                                    for (int i = 0; i < length; i++) {
                                        cellArr2[i] = cellArr[i];
                                    }
                                    this.cells = cellArr2;
                                }
                                this.busy = 0;
                                z2 = false;
                            } finally {
                            }
                        }
                    } else {
                        z = true;
                    }
                    int i2 = nextInt ^ (nextInt << 13);
                    int i3 = i2 ^ (i2 >>> 17);
                    nextInt = i3 ^ (i3 << 5);
                    iArr[0] = nextInt;
                } else {
                    if (this.busy == 0) {
                        Cell cell2 = new Cell(j);
                        if (this.busy == 0 && casBusy()) {
                            boolean z3 = false;
                            try {
                                Cell[] cellArr3 = this.cells;
                                if (cellArr3 != null && (length2 = cellArr3.length) > 0) {
                                    int i4 = (length2 - 1) & nextInt;
                                    if (cellArr3[i4] == null) {
                                        cellArr3[i4] = cell2;
                                        z3 = true;
                                    }
                                }
                                if (z3) {
                                    return;
                                }
                            } finally {
                            }
                        }
                    }
                    z2 = false;
                    int i22 = nextInt ^ (nextInt << 13);
                    int i32 = i22 ^ (i22 >>> 17);
                    nextInt = i32 ^ (i32 << 5);
                    iArr[0] = nextInt;
                }
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
