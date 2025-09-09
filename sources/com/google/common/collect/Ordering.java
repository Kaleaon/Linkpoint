package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Ordering<T> implements Comparator<T> {
    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;

    @VisibleForTesting
    static class ArbitraryOrdering extends Ordering<Object> {
        private Map<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeComputingMap(new Function<Object, Integer>() {
            final AtomicInteger counter = new AtomicInteger(0);

            public Integer apply(Object obj) {
                return Integer.valueOf(this.counter.getAndIncrement());
            }
        });

        ArbitraryOrdering() {
        }

        public int compare(Object obj, Object obj2) {
            if (obj == obj2) {
                return 0;
            }
            if (obj == null) {
                return -1;
            }
            if (obj2 == null) {
                return 1;
            }
            int identityHashCode = identityHashCode(obj);
            int identityHashCode2 = identityHashCode(obj2);
            if (identityHashCode != identityHashCode2) {
                return identityHashCode >= identityHashCode2 ? 1 : -1;
            }
            int compareTo = this.uids.get(obj).compareTo(this.uids.get(obj2));
            if (compareTo != 0) {
                return compareTo;
            }
            throw new AssertionError();
        }

        /* access modifiers changed from: package-private */
        public int identityHashCode(Object obj) {
            return System.identityHashCode(obj);
        }

        public String toString() {
            return "Ordering.arbitrary()";
        }
    }

    private static class ArbitraryOrderingHolder {
        static final Ordering<Object> ARBITRARY_ORDERING = new ArbitraryOrdering();

        private ArbitraryOrderingHolder() {
        }
    }

    @VisibleForTesting
    static class IncomparableValueException extends ClassCastException {
        private static final long serialVersionUID = 0;
        final Object value;

        IncomparableValueException(Object obj) {
            super("Cannot compare value: " + obj);
            this.value = obj;
        }
    }

    protected Ordering() {
    }

    @GwtCompatible(serializable = true)
    public static Ordering<Object> allEqual() {
        return AllEqualOrdering.INSTANCE;
    }

    public static Ordering<Object> arbitrary() {
        return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> iterable) {
        return new CompoundOrdering(iterable);
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(T t, T... tArr) {
        return explicit(Lists.asList(t, tArr));
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(List<T> list) {
        return new ExplicitOrdering(list);
    }

    @GwtCompatible(serializable = true)
    @Deprecated
    public static <T> Ordering<T> from(Ordering<T> ordering) {
        return (Ordering) Preconditions.checkNotNull(ordering);
    }

    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(Comparator<T> comparator) {
        return !(comparator instanceof Ordering) ? new ComparatorOrdering(comparator) : (Ordering) comparator;
    }

    @GwtCompatible(serializable = true)
    public static <C extends Comparable> Ordering<C> natural() {
        return NaturalOrdering.INSTANCE;
    }

    private <E extends T> int partition(E[] eArr, int i, int i2, int i3) {
        E e = eArr[i3];
        eArr[i3] = eArr[i2];
        eArr[i2] = e;
        int i4 = i;
        while (i < i2) {
            if (compare(eArr[i], e) < 0) {
                ObjectArrays.swap(eArr, i4, i);
                i4++;
            }
            i++;
        }
        ObjectArrays.swap(eArr, i2, i4);
        return i4;
    }

    @GwtCompatible(serializable = true)
    public static Ordering<Object> usingToString() {
        return UsingToStringOrdering.INSTANCE;
    }

    public int binarySearch(List<? extends T> list, @Nullable T t) {
        return Collections.binarySearch(list, t, this);
    }

    public abstract int compare(@Nullable T t, @Nullable T t2);

    @GwtCompatible(serializable = true)
    public <U extends T> Ordering<U> compound(Comparator<? super U> comparator) {
        return new CompoundOrdering(this, (Comparator) Preconditions.checkNotNull(comparator));
    }

    public <E extends T> List<E> greatestOf(Iterable<E> iterable, int i) {
        return reverse().leastOf(iterable, i);
    }

    public <E extends T> List<E> greatestOf(Iterator<E> it, int i) {
        return reverse().leastOf(it, i);
    }

    public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable) {
        Object[] array = Iterables.toArray(iterable);
        for (Object checkNotNull : array) {
            Preconditions.checkNotNull(checkNotNull);
        }
        Arrays.sort(array, this);
        return ImmutableList.asImmutableList(array);
    }

    public boolean isOrdered(Iterable<? extends T> iterable) {
        Iterator<? extends T> it = iterable.iterator();
        if (!it.hasNext()) {
            return true;
        }
        Object next = it.next();
        while (it.hasNext()) {
            Object next2 = it.next();
            if (compare(next, next2) > 0) {
                return false;
            }
            next = next2;
        }
        return true;
    }

    public boolean isStrictlyOrdered(Iterable<? extends T> iterable) {
        Iterator<? extends T> it = iterable.iterator();
        if (!it.hasNext()) {
            return true;
        }
        Object next = it.next();
        while (it.hasNext()) {
            Object next2 = it.next();
            if (compare(next, next2) >= 0) {
                return false;
            }
            next = next2;
        }
        return true;
    }

    public <E extends T> List<E> leastOf(Iterable<E> iterable, int i) {
        boolean z = false;
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;
            if (((long) collection.size()) > ((long) i) * 2) {
                z = true;
            }
            if (!z) {
                Object[] array = collection.toArray();
                Arrays.sort(array, this);
                if (array.length > i) {
                    array = ObjectArrays.arraysCopyOf(array, i);
                }
                return Collections.unmodifiableList(Arrays.asList(array));
            }
        }
        return leastOf(iterable.iterator(), i);
    }

    public <E extends T> List<E> leastOf(Iterator<E> it, int i) {
        E e;
        int i2;
        int i3;
        Preconditions.checkNotNull(it);
        CollectPreconditions.checkNonnegative(i, "k");
        if (i == 0 || !it.hasNext()) {
            return ImmutableList.of();
        }
        if (i < 1073741823) {
            int i4 = i * 2;
            E[] eArr = (Object[]) new Object[i4];
            E next = it.next();
            eArr[0] = next;
            int i5 = 1;
            while (i5 < i && it.hasNext()) {
                E next2 = it.next();
                eArr[i5] = next2;
                next = max(e, next2);
                i5++;
            }
            while (it.hasNext()) {
                E next3 = it.next();
                if (compare(next3, e) < 0) {
                    int i6 = i5 + 1;
                    eArr[i5] = next3;
                    if (i6 != i4) {
                        i5 = i6;
                    } else {
                        int i7 = i4 - 1;
                        int i8 = 0;
                        int i9 = 0;
                        while (i9 < i7) {
                            int partition = partition(eArr, i9, i7, ((i9 + i7) + 1) >>> 1);
                            if (partition <= i) {
                                if (partition >= i) {
                                    break;
                                }
                                i3 = i7;
                                i2 = Math.max(partition, i9 + 1);
                            } else {
                                i2 = i9;
                                i3 = partition - 1;
                                partition = i8;
                            }
                            i9 = i2;
                            i7 = i3;
                            i8 = partition;
                        }
                        e = eArr[i8];
                        for (int i10 = i8 + 1; i10 < i; i10++) {
                            e = max(e, eArr[i10]);
                        }
                        i5 = i;
                    }
                }
            }
            Arrays.sort(eArr, 0, i5, this);
            return Collections.unmodifiableList(Arrays.asList(ObjectArrays.arraysCopyOf(eArr, Math.min(i5, i))));
        }
        ArrayList<E> newArrayList = Lists.newArrayList(it);
        Collections.sort(newArrayList, this);
        if (newArrayList.size() > i) {
            newArrayList.subList(i, newArrayList.size()).clear();
        }
        newArrayList.trimToSize();
        return Collections.unmodifiableList(newArrayList);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<Iterable<S>> lexicographical() {
        return new LexicographicalOrdering(this);
    }

    public <E extends T> E max(Iterable<E> iterable) {
        return max(iterable.iterator());
    }

    public <E extends T> E max(@Nullable E e, @Nullable E e2) {
        return compare(e, e2) < 0 ? e2 : e;
    }

    public <E extends T> E max(@Nullable E e, @Nullable E e2, @Nullable E e3, E... eArr) {
        E max = max(max(e, e2), e3);
        for (E max2 : eArr) {
            max = max(max, max2);
        }
        return max;
    }

    public <E extends T> E max(Iterator<E> it) {
        E next = it.next();
        while (it.hasNext()) {
            next = max(next, it.next());
        }
        return next;
    }

    public <E extends T> E min(Iterable<E> iterable) {
        return min(iterable.iterator());
    }

    public <E extends T> E min(@Nullable E e, @Nullable E e2) {
        return compare(e, e2) > 0 ? e2 : e;
    }

    public <E extends T> E min(@Nullable E e, @Nullable E e2, @Nullable E e3, E... eArr) {
        E min = min(min(e, e2), e3);
        for (E min2 : eArr) {
            min = min(min, min2);
        }
        return min;
    }

    public <E extends T> E min(Iterator<E> it) {
        E next = it.next();
        while (it.hasNext()) {
            next = min(next, it.next());
        }
        return next;
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsFirst() {
        return new NullsFirstOrdering(this);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsLast() {
        return new NullsLastOrdering(this);
    }

    /* access modifiers changed from: package-private */
    public <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
        return onResultOf(Maps.keyFunction());
    }

    @GwtCompatible(serializable = true)
    public <F> Ordering<F> onResultOf(Function<F, ? extends T> function) {
        return new ByFunctionOrdering(function, this);
    }

    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering(this);
    }

    public <E extends T> List<E> sortedCopy(Iterable<E> iterable) {
        Object[] array = Iterables.toArray(iterable);
        Arrays.sort(array, this);
        return Lists.newArrayList(Arrays.asList(array));
    }
}
