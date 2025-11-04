// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.cache;

import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import java.util.Random;
import sun.misc.Unsafe;

abstract class Striped64 extends Number
{
    static final int NCPU;
    private static final Unsafe UNSAFE;
    private static final long baseOffset;
    private static final long busyOffset;
    static final Random rng;
    static final ThreadLocal<int[]> threadHashCode;
    transient volatile long base;
    transient volatile int busy;
    transient volatile Cell[] cells;
    
    static {
        threadHashCode = new ThreadLocal<int[]>();
        rng = new Random();
        NCPU = Runtime.getRuntime().availableProcessors();
        try {
            UNSAFE = getUnsafe();
            baseOffset = Striped64.UNSAFE.objectFieldOffset(Striped64.class.getDeclaredField("base"));
            busyOffset = Striped64.UNSAFE.objectFieldOffset(Striped64.class.getDeclaredField("busy"));
        }
        catch (final Exception cause) {
            throw new Error(cause);
        }
    }
    
    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (final SecurityException ex) {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>)new PrivilegedExceptionAction<Unsafe>() {
                    @Override
                    public Unsafe run() throws Exception {
                        int i = 0;
                        for (Field[] declaredFields = Unsafe.class.getDeclaredFields(); i < declaredFields.length; ++i) {
                            final Field field = declaredFields[i];
                            field.setAccessible(true);
                            final Object value = field.get(null);
                            if (Unsafe.class.isInstance(value)) {
                                return Unsafe.class.cast(value);
                            }
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            }
            catch (final PrivilegedActionException ex2) {
                throw new RuntimeException("Could not initialize intrinsics", ex2.getCause());
            }
        }
    }
    
    final boolean casBase(final long expected, final long x) {
        return Striped64.UNSAFE.compareAndSwapLong(this, Striped64.baseOffset, expected, x);
    }
    
    final boolean casBusy() {
        return Striped64.UNSAFE.compareAndSwapInt(this, Striped64.busyOffset, 0, 1);
    }
    
    abstract long fn(final long p0, final long p1);
    
    final void internalReset(final long n) {
        final Cell[] cells = this.cells;
        this.base = n;
        if (cells != null) {
            for (final Cell cell : cells) {
                if (cell != null) {
                    cell.value = n;
                }
            }
        }
    }
    
    final void retryUpdate(final long n, int[] value, boolean b) {
        int nextInt;
        if (value != null) {
            nextInt = value[0];
        }
        else {
            final ThreadLocal<int[]> threadHashCode = Striped64.threadHashCode;
            value = new int[] { 0 };
            threadHashCode.set((int[])value);
            nextInt = Striped64.rng.nextInt();
            if (nextInt == 0) {
                nextInt = 1;
            }
            value[0] = nextInt;
        }
        int n2 = 0;
        int n3 = nextInt;
    Label_0170_Outer:
        while (true) {
            final Cell[] cells = this.cells;
            if (cells != null) {
                final int length = cells.length;
                if (length > 0) {
                    Object cells2 = cells[length - 1 & n3];
                    while (true) {
                        Label_0343: {
                            boolean b2;
                            if (cells2 != null) {
                                if (!b) {
                                    break Label_0343;
                                }
                                final long value2 = ((Cell)cells2).value;
                                if (((Cell)cells2).cas(value2, this.fn(value2, n))) {
                                    break;
                                }
                                if (length < Striped64.NCPU && this.cells == cells) {
                                    Label_0421: {
                                        if (n2 == 0) {
                                            break Label_0421;
                                        }
                                        nextInt = n2;
                                        b2 = b;
                                        if (this.busy != 0) {
                                            break Label_0170;
                                        }
                                        nextInt = n2;
                                        b2 = b;
                                        if (!this.casBusy()) {
                                            break Label_0170;
                                        }
                                        while (true) {
                                            while (true) {
                                                Label_0467: {
                                                    try {
                                                        cells2 = this.cells;
                                                        if (cells2 == cells) {
                                                            cells2 = new Cell[length << 1];
                                                            nextInt = 0;
                                                            if (nextInt < length) {
                                                                break Label_0467;
                                                            }
                                                            this.cells = (Cell[])cells2;
                                                        }
                                                        this.busy = 0;
                                                        n2 = 0;
                                                        continue Label_0170_Outer;
                                                        nextInt = 1;
                                                        b2 = b;
                                                        break;
                                                    }
                                                    finally {
                                                        this.busy = 0;
                                                    }
                                                }
                                                cells2[nextInt] = cells[nextInt];
                                                ++nextInt;
                                                continue;
                                            }
                                        }
                                    }
                                }
                                else {
                                    nextInt = 0;
                                    b2 = b;
                                }
                            }
                            else {
                                if (this.busy == 0) {
                                    final Cell cell = new Cell(n);
                                    if (this.busy == 0 && this.casBusy()) {
                                        final int n4 = 0;
                                        try {
                                            final Cell[] cells3 = this.cells;
                                            if (cells3 == null) {
                                                nextInt = n4;
                                            }
                                            else {
                                                final int length2 = cells3.length;
                                                nextInt = n4;
                                                if (length2 > 0) {
                                                    final int n5 = length2 - 1 & n3;
                                                    nextInt = n4;
                                                    if (cells3[n5] == null) {
                                                        cells3[n5] = cell;
                                                        nextInt = 1;
                                                    }
                                                }
                                            }
                                            this.busy = 0;
                                            if (nextInt == 0) {
                                                continue Label_0170_Outer;
                                            }
                                            break;
                                        }
                                        finally {
                                            this.busy = 0;
                                        }
                                        break Label_0343;
                                    }
                                }
                                nextInt = 0;
                                b2 = b;
                            }
                            final int n6 = n3 ^ n3 << 13;
                            final int n7 = n6 ^ n6 >>> 17;
                            n3 = (n7 ^ n7 << 5);
                            value[0] = n3;
                            n2 = nextInt;
                            b = b2;
                            continue Label_0170_Outer;
                        }
                        boolean b2 = true;
                        nextInt = n2;
                        continue;
                    }
                }
            }
            if (this.busy == 0 && this.cells == cells && this.casBusy()) {
                nextInt = 0;
                try {
                    if (this.cells == cells) {
                        final Cell[] cells4 = new Cell[2];
                        cells4[n3 & 0x1] = new Cell(n);
                        this.cells = cells4;
                        nextInt = 1;
                    }
                    this.busy = 0;
                    if (nextInt == 0) {
                        continue;
                    }
                    break;
                }
                finally {
                    this.busy = 0;
                }
            }
            else {
                final long base = this.base;
                if (this.casBase(base, this.fn(base, n))) {
                    break;
                }
                continue;
            }
        }
    }
    
    static final class Cell
    {
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
                UNSAFE = getUnsafe();
                valueOffset = Cell.UNSAFE.objectFieldOffset(Cell.class.getDeclaredField("value"));
            }
            catch (final Exception cause) {
                throw new Error(cause);
            }
        }
        
        Cell(final long value) {
            this.value = value;
        }
        
        final boolean cas(final long expected, final long x) {
            return Cell.UNSAFE.compareAndSwapLong(this, Cell.valueOffset, expected, x);
        }
    }
}
