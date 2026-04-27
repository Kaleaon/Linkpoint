// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicLongArray;
import java.io.Serializable;

public class AtomicDoubleArray implements Serializable
{
    private static final long serialVersionUID = 0L;
    private transient AtomicLongArray longs;
    
    public AtomicDoubleArray(final int length) {
        this.longs = new AtomicLongArray(length);
    }
    
    public AtomicDoubleArray(final double[] array) {
        final int length = array.length;
        final long[] array2 = new long[length];
        for (int i = 0; i < length; ++i) {
            array2[i] = Double.doubleToRawLongBits(array[i]);
        }
        this.longs = new AtomicLongArray(array2);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final int int1 = objectInputStream.readInt();
        this.longs = new AtomicLongArray(int1);
        for (int i = 0; i < int1; ++i) {
            this.set(i, objectInputStream.readDouble());
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        final int length = this.length();
        objectOutputStream.writeInt(length);
        for (int i = 0; i < length; ++i) {
            objectOutputStream.writeDouble(this.get(i));
        }
    }
    
    public double addAndGet(final int n, final double n2) {
        long value;
        double n3;
        do {
            value = this.longs.get(n);
            n3 = Double.longBitsToDouble(value) + n2;
        } while (!this.longs.compareAndSet(n, value, Double.doubleToRawLongBits(n3)));
        return n3;
    }
    
    public final boolean compareAndSet(final int i, final double n, final double n2) {
        return this.longs.compareAndSet(i, Double.doubleToRawLongBits(n), Double.doubleToRawLongBits(n2));
    }
    
    public final double get(final int i) {
        return Double.longBitsToDouble(this.longs.get(i));
    }
    
    public final double getAndAdd(final int n, final double n2) {
        long value;
        double longBitsToDouble;
        do {
            value = this.longs.get(n);
            longBitsToDouble = Double.longBitsToDouble(value);
        } while (!this.longs.compareAndSet(n, value, Double.doubleToRawLongBits(longBitsToDouble + n2)));
        return longBitsToDouble;
    }
    
    public final double getAndSet(final int i, final double n) {
        return Double.longBitsToDouble(this.longs.getAndSet(i, Double.doubleToRawLongBits(n)));
    }
    
    public final void lazySet(final int n, final double n2) {
        this.set(n, n2);
    }
    
    public final int length() {
        return this.longs.length();
    }
    
    public final void set(final int i, final double n) {
        this.longs.set(i, Double.doubleToRawLongBits(n));
    }
    
    @Override
    public String toString() {
        final int n = this.length() - 1;
        if (n != -1) {
            final StringBuilder sb = new StringBuilder((n + 1) * 19);
            sb.append('[');
            int i = 0;
            while (true) {
                sb.append(Double.longBitsToDouble(this.longs.get(i)));
                if (i == n) {
                    break;
                }
                sb.append(',').append(' ');
                ++i;
            }
            return sb.append(']').toString();
        }
        return "[]";
    }
    
    public final boolean weakCompareAndSet(final int i, final double n, final double n2) {
        return this.longs.weakCompareAndSet(i, Double.doubleToRawLongBits(n), Double.doubleToRawLongBits(n2));
    }
}
