// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.io.Serializable;

public class AtomicDouble extends Number implements Serializable
{
    private static final long serialVersionUID = 0L;
    private static final AtomicLongFieldUpdater<AtomicDouble> updater;
    private transient volatile long value;
    
    static {
        updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");
    }
    
    public AtomicDouble() {
    }
    
    public AtomicDouble(final double n) {
        this.value = Double.doubleToRawLongBits(n);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.set(objectInputStream.readDouble());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeDouble(this.get());
    }
    
    public final double addAndGet(final double n) {
        long value;
        double n2;
        do {
            value = this.value;
            n2 = Double.longBitsToDouble(value) + n;
        } while (!AtomicDouble.updater.compareAndSet(this, value, Double.doubleToRawLongBits(n2)));
        return n2;
    }
    
    public final boolean compareAndSet(final double n, final double n2) {
        return AtomicDouble.updater.compareAndSet(this, Double.doubleToRawLongBits(n), Double.doubleToRawLongBits(n2));
    }
    
    @Override
    public double doubleValue() {
        return this.get();
    }
    
    @Override
    public float floatValue() {
        return (float)this.get();
    }
    
    public final double get() {
        return Double.longBitsToDouble(this.value);
    }
    
    public final double getAndAdd(final double n) {
        long value;
        double longBitsToDouble;
        do {
            value = this.value;
            longBitsToDouble = Double.longBitsToDouble(value);
        } while (!AtomicDouble.updater.compareAndSet(this, value, Double.doubleToRawLongBits(longBitsToDouble + n)));
        return longBitsToDouble;
    }
    
    public final double getAndSet(final double n) {
        return Double.longBitsToDouble(AtomicDouble.updater.getAndSet(this, Double.doubleToRawLongBits(n)));
    }
    
    @Override
    public int intValue() {
        return (int)this.get();
    }
    
    public final void lazySet(final double n) {
        this.set(n);
    }
    
    @Override
    public long longValue() {
        return (long)this.get();
    }
    
    public final void set(final double n) {
        this.value = Double.doubleToRawLongBits(n);
    }
    
    @Override
    public String toString() {
        return Double.toString(this.get());
    }
    
    public final boolean weakCompareAndSet(final double n, final double n2) {
        return AtomicDouble.updater.weakCompareAndSet(this, Double.doubleToRawLongBits(n), Double.doubleToRawLongBits(n2));
    }
}
