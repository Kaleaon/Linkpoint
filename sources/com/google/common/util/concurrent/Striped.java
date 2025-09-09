package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Beta
public abstract class Striped<L> {
    private static final int ALL_SET = -1;
    private static final int LARGE_LAZY_CUTOFF = 1024;
    private static final Supplier<ReadWriteLock> READ_WRITE_LOCK_SUPPLIER = new Supplier<ReadWriteLock>() {
        public ReadWriteLock get() {
            return new ReentrantReadWriteLock();
        }
    };

    private static class CompactStriped<L> extends PowerOfTwoStriped<L> {
        private final Object[] array;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        private CompactStriped(int i, Supplier<L> supplier) {
            super(i);
            Preconditions.checkArgument(i <= 1073741824, "Stripes must be <= 2^30)");
            this.array = new Object[(this.mask + 1)];
            for (int i2 = 0; i2 < this.array.length; i2++) {
                this.array[i2] = supplier.get();
            }
        }

        public L getAt(int i) {
            return this.array[i];
        }

        public int size() {
            return this.array.length;
        }
    }

    @VisibleForTesting
    static class LargeLazyStriped<L> extends PowerOfTwoStriped<L> {
        final ConcurrentMap<Integer, L> locks;
        final int size;
        final Supplier<L> supplier;

        LargeLazyStriped(int i, Supplier<L> supplier2) {
            super(i);
            this.size = this.mask != -1 ? this.mask + 1 : Integer.MAX_VALUE;
            this.supplier = supplier2;
            this.locks = new MapMaker().weakValues().makeMap();
        }

        public L getAt(int i) {
            if (this.size != Integer.MAX_VALUE) {
                Preconditions.checkElementIndex(i, size());
            }
            L l = this.locks.get(Integer.valueOf(i));
            if (l != null) {
                return l;
            }
            L l2 = this.supplier.get();
            return MoreObjects.firstNonNull(this.locks.putIfAbsent(Integer.valueOf(i), l2), l2);
        }

        public int size() {
            return this.size;
        }
    }

    private static class PaddedLock extends ReentrantLock {
        long unused1;
        long unused2;
        long unused3;

        PaddedLock() {
            super(false);
        }
    }

    private static class PaddedSemaphore extends Semaphore {
        long unused1;
        long unused2;
        long unused3;

        PaddedSemaphore(int i) {
            super(i, false);
        }
    }

    private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
        final int mask;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        PowerOfTwoStriped(int i) {
            super();
            boolean z = false;
            Preconditions.checkArgument(i > 0 ? true : z, "Stripes must be positive");
            this.mask = i <= 1073741824 ? Striped.ceilToPowerOfTwo(i) - 1 : -1;
        }

        public final L get(Object obj) {
            return getAt(indexFor(obj));
        }

        /* access modifiers changed from: package-private */
        public final int indexFor(Object obj) {
            return Striped.smear(obj.hashCode()) & this.mask;
        }
    }

    @VisibleForTesting
    static class SmallLazyStriped<L> extends PowerOfTwoStriped<L> {
        final AtomicReferenceArray<ArrayReference<? extends L>> locks;
        final ReferenceQueue<L> queue = new ReferenceQueue<>();
        final int size;
        final Supplier<L> supplier;

        private static final class ArrayReference<L> extends WeakReference<L> {
            final int index;

            ArrayReference(L l, int i, ReferenceQueue<L> referenceQueue) {
                super(l, referenceQueue);
                this.index = i;
            }
        }

        SmallLazyStriped(int i, Supplier<L> supplier2) {
            super(i);
            this.size = this.mask != -1 ? this.mask + 1 : Integer.MAX_VALUE;
            this.locks = new AtomicReferenceArray<>(this.size);
            this.supplier = supplier2;
        }

        private void drainQueue() {
            while (true) {
                Reference<? extends L> poll = this.queue.poll();
                if (poll != null) {
                    ArrayReference arrayReference = (ArrayReference) poll;
                    this.locks.compareAndSet(arrayReference.index, arrayReference, (Object) null);
                } else {
                    return;
                }
            }
        }

        public L getAt(int i) {
            L l;
            if (this.size != Integer.MAX_VALUE) {
                Preconditions.checkElementIndex(i, size());
            }
            ArrayReference arrayReference = this.locks.get(i);
            L l2 = arrayReference != null ? arrayReference.get() : null;
            if (l2 != null) {
                return l2;
            }
            L l3 = this.supplier.get();
            ArrayReference arrayReference2 = new ArrayReference(l3, i, this.queue);
            while (!this.locks.compareAndSet(i, arrayReference, arrayReference2)) {
                arrayReference = this.locks.get(i);
                if (arrayReference != null) {
                    l = arrayReference.get();
                    continue;
                } else {
                    l = null;
                    continue;
                }
                if (l != null) {
                    return l;
                }
            }
            drainQueue();
            return l3;
        }

        public int size() {
            return this.size;
        }
    }

    private Striped() {
    }

    /* access modifiers changed from: private */
    public static int ceilToPowerOfTwo(int i) {
        return 1 << IntMath.log2(i, RoundingMode.CEILING);
    }

    private static <L> Striped<L> lazy(int i, Supplier<L> supplier) {
        return i >= 1024 ? new LargeLazyStriped(i, supplier) : new SmallLazyStriped(i, supplier);
    }

    public static Striped<Lock> lazyWeakLock(int i) {
        return lazy(i, new Supplier<Lock>() {
            public Lock get() {
                return new ReentrantLock(false);
            }
        });
    }

    public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int i) {
        return lazy(i, READ_WRITE_LOCK_SUPPLIER);
    }

    public static Striped<Semaphore> lazyWeakSemaphore(int i, final int i2) {
        return lazy(i, new Supplier<Semaphore>() {
            public Semaphore get() {
                return new Semaphore(i2, false);
            }
        });
    }

    public static Striped<Lock> lock(int i) {
        return new CompactStriped(i, new Supplier<Lock>() {
            public Lock get() {
                return new PaddedLock();
            }
        });
    }

    public static Striped<ReadWriteLock> readWriteLock(int i) {
        return new CompactStriped(i, READ_WRITE_LOCK_SUPPLIER);
    }

    public static Striped<Semaphore> semaphore(int i, final int i2) {
        return new CompactStriped(i, new Supplier<Semaphore>() {
            public Semaphore get() {
                return new PaddedSemaphore(i2);
            }
        });
    }

    /* access modifiers changed from: private */
    public static int smear(int i) {
        int i2 = ((i >>> 20) ^ (i >>> 12)) ^ i;
        return (i2 >>> 4) ^ ((i2 >>> 7) ^ i2);
    }

    public Iterable<L> bulkGet(Iterable<?> iterable) {
        Object[] array = Iterables.toArray(iterable, Object.class);
        if (array.length == 0) {
            return ImmutableList.of();
        }
        int[] iArr = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            iArr[i] = indexFor(array[i]);
        }
        Arrays.sort(iArr);
        int i2 = iArr[0];
        array[0] = getAt(i2);
        int i3 = i2;
        for (int i4 = 1; i4 < array.length; i4++) {
            int i5 = iArr[i4];
            if (i5 != i3) {
                array[i4] = getAt(i5);
                i3 = i5;
            } else {
                array[i4] = array[i4 - 1];
            }
        }
        return Collections.unmodifiableList(Arrays.asList(array));
    }

    public abstract L get(Object obj);

    public abstract L getAt(int i);

    /* access modifiers changed from: package-private */
    public abstract int indexFor(Object obj);

    public abstract int size();
}
