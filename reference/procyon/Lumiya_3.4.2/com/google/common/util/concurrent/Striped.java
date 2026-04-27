// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;
import com.google.common.base.MoreObjects;
import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import com.google.common.collect.Iterables;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;
import com.google.common.base.Supplier;
import com.google.common.annotations.Beta;

@Beta
public abstract class Striped<L>
{
    private static final int ALL_SET = -1;
    private static final int LARGE_LAZY_CUTOFF = 1024;
    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER;
    
    static {
        READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
            @Override
            public ReadWriteLock get() {
                return new ReentrantReadWriteLock();
            }
        };
    }
    
    private Striped() {
    }
    
    private static int ceilToPowerOfTwo(final int n) {
        return 1 << IntMath.log2(n, RoundingMode.CEILING);
    }
    
    private static <L> Striped<L> lazy(final int n, final Supplier<L> supplier) {
        Object o;
        if (n >= 1024) {
            o = new LargeLazyStriped(n, (Supplier<Object>)supplier);
        }
        else {
            o = new SmallLazyStriped(n, (Supplier<Object>)supplier);
        }
        return (Striped<L>)o;
    }
    
    public static Striped<Lock> lazyWeakLock(final int n) {
        return lazy(n, (Supplier<Lock>)new Supplier<Lock>() {
            @Override
            public Lock get() {
                return new ReentrantLock(false);
            }
        });
    }
    
    public static Striped<ReadWriteLock> lazyWeakReadWriteLock(final int n) {
        return lazy(n, Striped.READ_WRITE_LOCK_SUPPLIER);
    }
    
    public static Striped<Semaphore> lazyWeakSemaphore(final int n, final int n2) {
        return lazy(n, (Supplier<Semaphore>)new Supplier<Semaphore>() {
            @Override
            public Semaphore get() {
                return new Semaphore(n2, false);
            }
        });
    }
    
    public static Striped<Lock> lock(final int n) {
        return new CompactStriped<Lock>(n, (Supplier)new Supplier<Lock>() {
            @Override
            public Lock get() {
                return new PaddedLock();
            }
        });
    }
    
    public static Striped<ReadWriteLock> readWriteLock(final int n) {
        return new CompactStriped<ReadWriteLock>(n, (Supplier)Striped.READ_WRITE_LOCK_SUPPLIER);
    }
    
    public static Striped<Semaphore> semaphore(final int n, final int n2) {
        return new CompactStriped<Semaphore>(n, (Supplier)new Supplier<Semaphore>() {
            @Override
            public Semaphore get() {
                return new PaddedSemaphore(n2);
            }
        });
    }
    
    private static int smear(int n) {
        n ^= (n >>> 20 ^ n >>> 12);
        return n >>> 4 ^ (n >>> 7 ^ n);
    }
    
    public Iterable<L> bulkGet(final Iterable<?> iterable) {
        final Object[] array = Iterables.toArray(iterable, Object.class);
        if (array.length != 0) {
            final int[] a = new int[array.length];
            for (int i = 0; i < array.length; ++i) {
                a[i] = this.indexFor(array[i]);
            }
            Arrays.sort(a);
            int n = a[0];
            array[0] = this.getAt(n);
            for (int j = 1; j < array.length; ++j) {
                final int n2 = a[j];
                if (n2 != n) {
                    array[j] = this.getAt(n2);
                    n = n2;
                }
                else {
                    array[j] = array[j - 1];
                }
            }
            return (Iterable<L>)Collections.unmodifiableList((List<?>)Arrays.asList((T[])array));
        }
        return (Iterable<L>)ImmutableList.of();
    }
    
    public abstract L get(final Object p0);
    
    public abstract L getAt(final int p0);
    
    abstract int indexFor(final Object p0);
    
    public abstract int size();
    
    private static class CompactStriped<L> extends PowerOfTwoStriped<L>
    {
        private final Object[] array;
        
        private CompactStriped(int i, final Supplier<L> supplier) {
            final int n = 0;
            super(i);
            Preconditions.checkArgument(i <= 1073741824, (Object)"Stripes must be <= 2^30)");
            this.array = new Object[this.mask + 1];
            for (i = n; i < this.array.length; ++i) {
                this.array[i] = supplier.get();
            }
        }
        
        @Override
        public L getAt(final int n) {
            return (L)this.array[n];
        }
        
        @Override
        public int size() {
            return this.array.length;
        }
    }
    
    private abstract static class PowerOfTwoStriped<L> extends Striped<L>
    {
        final int mask;
        
        PowerOfTwoStriped(int mask) {
            boolean b = false;
            super(null);
            if (mask > 0) {
                b = true;
            }
            Preconditions.checkArgument(b, (Object)"Stripes must be positive");
            if (mask <= 1073741824) {
                mask = ceilToPowerOfTwo(mask) - 1;
            }
            else {
                mask = -1;
            }
            this.mask = mask;
        }
        
        @Override
        public final L get(final Object o) {
            return this.getAt(this.indexFor(o));
        }
        
        @Override
        final int indexFor(final Object o) {
            return smear(o.hashCode()) & this.mask;
        }
    }
    
    @VisibleForTesting
    static class LargeLazyStriped<L> extends PowerOfTwoStriped<L>
    {
        final ConcurrentMap<Integer, L> locks;
        final int size;
        final Supplier<L> supplier;
        
        LargeLazyStriped(int size, final Supplier<L> supplier) {
            super(size);
            if (this.mask != -1) {
                size = this.mask + 1;
            }
            else {
                size = Integer.MAX_VALUE;
            }
            this.size = size;
            this.supplier = supplier;
            this.locks = new MapMaker().weakValues().makeMap();
        }
        
        @Override
        public L getAt(final int n) {
            if (this.size != Integer.MAX_VALUE) {
                Preconditions.checkElementIndex(n, this.size());
            }
            final L value = this.locks.get(n);
            if (value == null) {
                final L value2 = this.supplier.get();
                return MoreObjects.firstNonNull(this.locks.putIfAbsent(n, value2), value2);
            }
            return value;
        }
        
        @Override
        public int size() {
            return this.size;
        }
    }
    
    private static class PaddedLock extends ReentrantLock
    {
        long unused1;
        long unused2;
        long unused3;
        
        PaddedLock() {
            super(false);
        }
    }
    
    private static class PaddedSemaphore extends Semaphore
    {
        long unused1;
        long unused2;
        long unused3;
        
        PaddedSemaphore(final int permits) {
            super(permits, false);
        }
    }
    
    @VisibleForTesting
    static class SmallLazyStriped<L> extends PowerOfTwoStriped<L>
    {
        final AtomicReferenceArray<ArrayReference<? extends L>> locks;
        final ReferenceQueue<L> queue;
        final int size;
        final Supplier<L> supplier;
        
        SmallLazyStriped(int size, final Supplier<L> supplier) {
            super(size);
            this.queue = new ReferenceQueue<L>();
            if (this.mask != -1) {
                size = this.mask + 1;
            }
            else {
                size = Integer.MAX_VALUE;
            }
            this.size = size;
            this.locks = new AtomicReferenceArray<ArrayReference<? extends L>>(this.size);
            this.supplier = supplier;
        }
        
        private void drainQueue() {
            while (true) {
                final Reference<? extends L> poll = this.queue.poll();
                if (poll == null) {
                    break;
                }
                final ArrayReference expectedValue = (ArrayReference)poll;
                this.locks.compareAndSet(expectedValue.index, expectedValue, null);
            }
        }
        
        @Override
        public L getAt(final int i) {
            if (this.size != Integer.MAX_VALUE) {
                Preconditions.checkElementIndex(i, this.size());
            }
            ArrayReference expectedValue = this.locks.get(i);
            Object value;
            if (expectedValue != null) {
                value = expectedValue.get();
            }
            else {
                value = null;
            }
            if (value == null) {
                final L value2 = this.supplier.get();
                while (!this.locks.compareAndSet(i, expectedValue, (ArrayReference)new ArrayReference<L>(value2, i, (ReferenceQueue<Object>)this.queue))) {
                    expectedValue = this.locks.get(i);
                    Object value3;
                    if (expectedValue != null) {
                        value3 = expectedValue.get();
                    }
                    else {
                        value3 = null;
                    }
                    if (value3 != null) {
                        return (L)value3;
                    }
                }
                this.drainQueue();
                return value2;
            }
            return (L)value;
        }
        
        @Override
        public int size() {
            return this.size;
        }
        
        private static final class ArrayReference<L> extends WeakReference<L>
        {
            final int index;
            
            ArrayReference(final L referent, final int index, final ReferenceQueue<L> q) {
                super(referent, q);
                this.index = index;
            }
        }
    }
}
