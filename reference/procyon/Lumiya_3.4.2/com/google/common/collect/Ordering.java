// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.concurrent.atomic.AtomicInteger;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import java.util.List;
import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;

@GwtCompatible
public abstract class Ordering<T> implements Comparator<T>
{
    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;
    
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
    public static <T> Ordering<T> compound(final Iterable<? extends Comparator<? super T>> iterable) {
        return new CompoundOrdering<T>(iterable);
    }
    
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(final T t, final T... array) {
        return explicit((List<T>)Lists.asList(t, (T[])array));
    }
    
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> explicit(final List<T> list) {
        return new ExplicitOrdering<T>(list);
    }
    
    @Deprecated
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(final Ordering<T> ordering) {
        return Preconditions.checkNotNull(ordering);
    }
    
    @GwtCompatible(serializable = true)
    public static <T> Ordering<T> from(final Comparator<T> comparator) {
        Ordering ordering;
        if (!(comparator instanceof Ordering)) {
            ordering = new ComparatorOrdering(comparator);
        }
        else {
            ordering = (Ordering)comparator;
        }
        return ordering;
    }
    
    @GwtCompatible(serializable = true)
    public static <C extends Comparable> Ordering<C> natural() {
        return (Ordering<C>)NaturalOrdering.INSTANCE;
    }
    
    private <E extends T> int partition(final E[] array, int i, final int n, int n2) {
        final T t = array[n2];
        array[n2] = array[n];
        array[n] = (E)t;
        n2 = i;
        while (i < n) {
            if (this.compare(array[i], t) < 0) {
                ObjectArrays.swap(array, n2, i);
                ++n2;
            }
            ++i;
        }
        ObjectArrays.swap(array, n, n2);
        return n2;
    }
    
    @GwtCompatible(serializable = true)
    public static Ordering<Object> usingToString() {
        return UsingToStringOrdering.INSTANCE;
    }
    
    public int binarySearch(final List<? extends T> list, @Nullable final T key) {
        return Collections.binarySearch(list, key, this);
    }
    
    @Override
    public abstract int compare(@Nullable final T p0, @Nullable final T p1);
    
    @GwtCompatible(serializable = true)
    public <U extends T> Ordering<U> compound(final Comparator<? super U> comparator) {
        return new CompoundOrdering<U>(this, Preconditions.checkNotNull(comparator));
    }
    
    public <E extends T> List<E> greatestOf(final Iterable<E> iterable, final int n) {
        return (List<E>)this.reverse().leastOf((Iterable<Object>)iterable, n);
    }
    
    public <E extends T> List<E> greatestOf(final Iterator<E> iterator, final int n) {
        return (List<E>)this.reverse().leastOf((Iterator<Object>)iterator, n);
    }
    
    public <E extends T> ImmutableList<E> immutableSortedCopy(final Iterable<E> iterable) {
        final Object[] a = Iterables.toArray(iterable);
        for (int length = a.length, i = 0; i < length; ++i) {
            Preconditions.checkNotNull(a[i]);
        }
        Arrays.sort(a, (Comparator<? super Object>)this);
        return (ImmutableList<E>)ImmutableList.asImmutableList(a);
    }
    
    public boolean isOrdered(final Iterable<? extends T> iterable) {
        final Iterator<? extends T> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            T next = (T)iterator.next();
            while (iterator.hasNext()) {
                final T next2 = (T)iterator.next();
                if (this.compare(next, next2) > 0) {
                    return false;
                }
                next = next2;
            }
        }
        return true;
    }
    
    public boolean isStrictlyOrdered(final Iterable<? extends T> iterable) {
        final Iterator<? extends T> iterator = iterable.iterator();
        if (iterator.hasNext()) {
            T next = (T)iterator.next();
            while (iterator.hasNext()) {
                final T next2 = (T)iterator.next();
                if (this.compare(next, next2) >= 0) {
                    return false;
                }
                next = next2;
            }
        }
        return true;
    }
    
    public <E extends T> List<E> leastOf(final Iterable<E> iterable, final int n) {
        boolean b = false;
        if (iterable instanceof Collection) {
            final Collection collection = (Collection)iterable;
            if (collection.size() > n * 2L) {
                b = true;
            }
            if (!b) {
                Object[] arraysCopy = collection.toArray();
                Arrays.sort(arraysCopy, (Comparator<? super Object>)this);
                if (arraysCopy.length > n) {
                    arraysCopy = ObjectArrays.arraysCopyOf(arraysCopy, n);
                }
                return (List<E>)Collections.unmodifiableList((List<?>)Arrays.asList((T[])arraysCopy));
            }
        }
        return this.leastOf((Iterator<E>)iterable.iterator(), n);
    }
    
    public <E extends T> List<E> leastOf(final Iterator<E> iterator, final int n) {
        Preconditions.checkNotNull(iterator);
        CollectPreconditions.checkNonnegative(n, "k");
        if (n == 0 || !iterator.hasNext()) {
            return (List<E>)ImmutableList.of();
        }
        if (n < 1073741823) {
            final int n2 = n * 2;
            final Object[] a = new Object[n2];
            T t = iterator.next();
            a[0] = t;
            while (true) {
                int i;
                for (i = 1; i < n; ++i) {
                    int n3 = i;
                    T t2 = t;
                    if (!iterator.hasNext()) {
                        while (iterator.hasNext()) {
                            final T next = iterator.next();
                            if (this.compare(next, t2) < 0) {
                                final int n4 = n3 + 1;
                                a[n3] = next;
                                if (n4 != n2) {
                                    n3 = n4;
                                }
                                else {
                                    int n5 = n2 - 1;
                                    int j = 0;
                                    int k = 0;
                                    while (k < n5) {
                                        final int partition = this.partition(a, k, n5, k + n5 + 1 >>> 1);
                                        if (partition <= n) {
                                            if (partition >= n) {
                                                break;
                                            }
                                            k = Math.max(partition, k + 1);
                                            j = partition;
                                        }
                                        else {
                                            n5 = partition - 1;
                                        }
                                    }
                                    Object max = a[j];
                                    ++j;
                                    while (j < n) {
                                        max = this.max(max, a[j]);
                                        ++j;
                                    }
                                    n3 = n;
                                    t2 = (T)max;
                                }
                            }
                        }
                        Arrays.sort(a, 0, n3, (Comparator<? super Object>)this);
                        return (List<E>)Collections.unmodifiableList((List<?>)Arrays.asList((T[])ObjectArrays.arraysCopyOf(a, Math.min(n3, n))));
                    }
                    final T next2 = iterator.next();
                    a[i] = next2;
                    t = this.max(t, next2);
                }
                T t2 = t;
                int n3 = i;
                continue;
            }
        }
        final ArrayList<Object> arrayList = Lists.newArrayList((Iterator<?>)iterator);
        Collections.sort(arrayList, (Comparator<? super Object>)this);
        if (arrayList.size() > n) {
            arrayList.subList(n, arrayList.size()).clear();
        }
        arrayList.trimToSize();
        return Collections.unmodifiableList((List<? extends E>)arrayList);
    }
    
    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<Iterable<S>> lexicographical() {
        return (Ordering<Iterable<S>>)new LexicographicalOrdering((Comparator<? super Object>)this);
    }
    
    public <E extends T> E max(final Iterable<E> iterable) {
        return this.max(iterable.iterator());
    }
    
    public <E extends T> E max(@Nullable E e, @Nullable final E e2) {
        if (this.compare(e, e2) < 0) {
            e = e2;
        }
        return e;
    }
    
    public <E extends T> E max(@Nullable final E e, @Nullable final E e2, @Nullable final E e3, final E... array) {
        T t = this.max(this.max(e, e2), e3);
        for (int length = array.length, i = 0; i < length; ++i) {
            t = this.max(t, array[i]);
        }
        return (E)t;
    }
    
    public <E extends T> E max(final Iterator<E> iterator) {
        T t = iterator.next();
        while (iterator.hasNext()) {
            t = this.max(t, iterator.next());
        }
        return (E)t;
    }
    
    public <E extends T> E min(final Iterable<E> iterable) {
        return this.min(iterable.iterator());
    }
    
    public <E extends T> E min(@Nullable final E e, @Nullable E e2) {
        if (this.compare(e, e2) <= 0) {
            e2 = e;
        }
        return e2;
    }
    
    public <E extends T> E min(@Nullable final E e, @Nullable final E e2, @Nullable final E e3, final E... array) {
        T t = this.min(this.min(e, e2), e3);
        for (int length = array.length, i = 0; i < length; ++i) {
            t = this.min(t, array[i]);
        }
        return (E)t;
    }
    
    public <E extends T> E min(final Iterator<E> iterator) {
        T t = iterator.next();
        while (iterator.hasNext()) {
            t = this.min(t, iterator.next());
        }
        return (E)t;
    }
    
    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsFirst() {
        return new NullsFirstOrdering<S>(this);
    }
    
    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> nullsLast() {
        return new NullsLastOrdering<S>(this);
    }
    
     <T2 extends T> Ordering<Map.Entry<T2, ?>> onKeys() {
        return this.onResultOf((Function<Map.Entry<T2, ?>, ? extends T>)Maps.keyFunction());
    }
    
    @GwtCompatible(serializable = true)
    public <F> Ordering<F> onResultOf(final Function<F, ? extends T> function) {
        return (Ordering<F>)new ByFunctionOrdering((Function<Object, ?>)function, (Ordering<Object>)this);
    }
    
    @GwtCompatible(serializable = true)
    public <S extends T> Ordering<S> reverse() {
        return new ReverseOrdering<S>(this);
    }
    
    public <E extends T> List<E> sortedCopy(final Iterable<E> iterable) {
        final Object[] array = Iterables.toArray(iterable);
        Arrays.sort(array, (Comparator<? super Object>)this);
        return (List<E>)Lists.newArrayList((Iterable<?>)Arrays.asList(array));
    }
    
    @VisibleForTesting
    static class ArbitraryOrdering extends Ordering<Object>
    {
        private Map<Object, Integer> uids;
        
        ArbitraryOrdering() {
            this.uids = (Map<Object, Integer>)Platform.tryWeakKeys(new MapMaker()).makeComputingMap((Function<? super Object, ?>)new Function<Object, Integer>() {
                final AtomicInteger counter = new AtomicInteger(0);
                
                @Override
                public Integer apply(final Object o) {
                    return this.counter.getAndIncrement();
                }
            });
        }
        
        @Override
        public int compare(final Object o, final Object o2) {
            int n = 1;
            if (o == o2) {
                return 0;
            }
            if (o == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            final int identityHashCode = this.identityHashCode(o);
            final int identityHashCode2 = this.identityHashCode(o2);
            if (identityHashCode != identityHashCode2) {
                if (identityHashCode < identityHashCode2) {
                    n = -1;
                }
                return n;
            }
            final int compareTo = this.uids.get(o).compareTo(this.uids.get(o2));
            if (compareTo != 0) {
                return compareTo;
            }
            throw new AssertionError();
        }
        
        int identityHashCode(final Object o) {
            return System.identityHashCode(o);
        }
        
        @Override
        public String toString() {
            return "Ordering.arbitrary()";
        }
    }
    
    private static class ArbitraryOrderingHolder
    {
        static final Ordering<Object> ARBITRARY_ORDERING;
        
        static {
            ARBITRARY_ORDERING = new ArbitraryOrdering();
        }
    }
    
    @VisibleForTesting
    static class IncomparableValueException extends ClassCastException
    {
        private static final long serialVersionUID = 0L;
        final Object value;
        
        IncomparableValueException(final Object o) {
            super("Cannot compare value: " + o);
            this.value = o;
        }
    }
}
