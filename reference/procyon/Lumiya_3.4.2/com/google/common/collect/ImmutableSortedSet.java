// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import javax.annotation.Nullable;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.SortedSet;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Arrays;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;
import java.util.NavigableSet;

@GwtCompatible(emulated = true, serializable = true)
public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E> implements NavigableSet<E>, SortedIterable<E>
{
    private static final RegularImmutableSortedSet<Comparable> NATURAL_EMPTY_SET;
    private static final Comparator<Comparable> NATURAL_ORDER;
    final transient Comparator<? super E> comparator;
    @GwtIncompatible("NavigableSet")
    transient ImmutableSortedSet<E> descendingSet;
    
    static {
        NATURAL_ORDER = Ordering.natural();
        NATURAL_EMPTY_SET = new RegularImmutableSortedSet<Comparable>((ImmutableList<Comparable>)ImmutableList.of(), ImmutableSortedSet.NATURAL_ORDER);
    }
    
    ImmutableSortedSet(final Comparator<? super E> comparator) {
        this.comparator = comparator;
    }
    
    static <E> ImmutableSortedSet<E> construct(final Comparator<? super E> c, final int n, final E... array) {
        int fromIndex = 1;
        if (n != 0) {
            ObjectArrays.checkElementsNotNull(array, n);
            Arrays.sort(array, 0, n, c);
            for (int i = 1; i < n; ++i) {
                final E e = array[i];
                if (c.compare(e, array[fromIndex - 1]) != 0) {
                    array[fromIndex] = e;
                    ++fromIndex;
                }
            }
            Arrays.fill(array, fromIndex, n, null);
            return new RegularImmutableSortedSet<E>(ImmutableList.asImmutableList(array, fromIndex), (Comparator<? super Object>)c);
        }
        return (ImmutableSortedSet<E>)emptySet((Comparator<? super Object>)c);
    }
    
    public static <E> ImmutableSortedSet<E> copyOf(final Iterable<? extends E> iterable) {
        return copyOf((Comparator<? super E>)Ordering.natural(), iterable);
    }
    
    public static <E> ImmutableSortedSet<E> copyOf(final Collection<? extends E> collection) {
        return copyOf((Comparator<? super E>)Ordering.natural(), collection);
    }
    
    public static <E> ImmutableSortedSet<E> copyOf(final Comparator<? super E> comparator, final Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(comparator);
        if (SortedIterables.hasSameComparator(comparator, iterable) && iterable instanceof ImmutableSortedSet) {
            final ImmutableSortedSet set = (ImmutableSortedSet)iterable;
            if (!set.isPartialView()) {
                return set;
            }
        }
        final Object[] array = Iterables.toArray(iterable);
        return construct(comparator, ((E[])array).length, (E[])array);
    }
    
    public static <E> ImmutableSortedSet<E> copyOf(final Comparator<? super E> comparator, final Collection<? extends E> collection) {
        return copyOf(comparator, (Iterable<? extends E>)collection);
    }
    
    public static <E> ImmutableSortedSet<E> copyOf(final Comparator<? super E> comparator, final Iterator<? extends E> iterator) {
        return new Builder<E>(comparator).addAll(iterator).build();
    }
    
    public static <E> ImmutableSortedSet<E> copyOf(final Iterator<? extends E> iterator) {
        return copyOf((Comparator<? super E>)Ordering.natural(), iterator);
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(final E[] array) {
        return construct(Ordering.natural(), array.length, (E[])array.clone());
    }
    
    public static <E> ImmutableSortedSet<E> copyOfSorted(final SortedSet<E> set) {
        final Comparator<? super E> comparator = SortedIterables.comparator(set);
        final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)set);
        if (!copy.isEmpty()) {
            return new RegularImmutableSortedSet<E>(copy, (Comparator<? super Object>)comparator);
        }
        return (ImmutableSortedSet<E>)emptySet((Comparator<? super Object>)comparator);
    }
    
    static <E> RegularImmutableSortedSet<E> emptySet(final Comparator<? super E> comparator) {
        if (!ImmutableSortedSet.NATURAL_ORDER.equals(comparator)) {
            return new RegularImmutableSortedSet<E>(ImmutableList.of(), comparator);
        }
        return (RegularImmutableSortedSet<E>)ImmutableSortedSet.NATURAL_EMPTY_SET;
    }
    
    public static <E extends Comparable<?>> Builder<E> naturalOrder() {
        return new Builder<E>(Ordering.natural());
    }
    
    public static <E> ImmutableSortedSet<E> of() {
        return (ImmutableSortedSet<E>)ImmutableSortedSet.NATURAL_EMPTY_SET;
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(final E e) {
        return new RegularImmutableSortedSet<E>(ImmutableList.of(e), Ordering.natural());
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(final E e, final E e2) {
        return construct(Ordering.natural(), 2, (E[])new Comparable[] { e, e2 });
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(final E e, final E e2, final E e3) {
        return construct(Ordering.natural(), 3, (E[])new Comparable[] { e, e2, e3 });
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(final E e, final E e2, final E e3, final E e4) {
        return construct(Ordering.natural(), 4, (E[])new Comparable[] { e, e2, e3, e4 });
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(final E e, final E e2, final E e3, final E e4, final E e5) {
        return construct(Ordering.natural(), 5, (E[])new Comparable[] { e, e2, e3, e4, e5 });
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E... array) {
        final Comparable[] array2 = new Comparable[array.length + 6];
        array2[0] = e;
        array2[1] = e2;
        array2[2] = e3;
        array2[3] = e4;
        array2[4] = e5;
        array2[5] = e6;
        System.arraycopy(array, 0, array2, 6, array.length);
        return construct(Ordering.natural(), ((E[])array2).length, (E[])array2);
    }
    
    public static <E> Builder<E> orderedBy(final Comparator<E> comparator) {
        return new Builder<E>(comparator);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }
    
    public static <E extends Comparable<?>> Builder<E> reverseOrder() {
        return new Builder<E>(Ordering.natural().reverse());
    }
    
    static int unsafeCompare(final Comparator<?> comparator, final Object o, final Object o2) {
        return comparator.compare(o, o2);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public E ceiling(final E e) {
        return Iterables.getFirst((Iterable<? extends E>)this.tailSet(e, true), (E)null);
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }
    
    @GwtIncompatible("NavigableSet")
    ImmutableSortedSet<E> createDescendingSet() {
        return new DescendingImmutableSortedSet<E>(this);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public abstract UnmodifiableIterator<E> descendingIterator();
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ImmutableSortedSet<E> descendingSet() {
        ImmutableSortedSet<E> descendingSet = this.descendingSet;
        if (descendingSet == null) {
            descendingSet = this.createDescendingSet();
            this.descendingSet = descendingSet;
            descendingSet.descendingSet = this;
        }
        return descendingSet;
    }
    
    @Override
    public E first() {
        return this.iterator().next();
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public E floor(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.headSet(e, true).descendingIterator(), (E)null);
    }
    
    @Override
    public ImmutableSortedSet<E> headSet(final E e) {
        return this.headSet(e, false);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ImmutableSortedSet<E> headSet(final E e, final boolean b) {
        return this.headSetImpl(Preconditions.checkNotNull(e), b);
    }
    
    abstract ImmutableSortedSet<E> headSetImpl(final E p0, final boolean p1);
    
    @GwtIncompatible("NavigableSet")
    @Override
    public E higher(final E e) {
        return Iterables.getFirst((Iterable<? extends E>)this.tailSet(e, false), (E)null);
    }
    
    abstract int indexOf(@Nullable final Object p0);
    
    @Override
    public abstract UnmodifiableIterator<E> iterator();
    
    @Override
    public E last() {
        return this.descendingIterator().next();
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public E lower(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.headSet(e, false).descendingIterator(), (E)null);
    }
    
    @Deprecated
    @GwtIncompatible("NavigableSet")
    @Override
    public final E pollFirst() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @GwtIncompatible("NavigableSet")
    @Override
    public final E pollLast() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableSortedSet<E> subSet(final E e, final E e2) {
        return this.subSet(e, true, e2, false);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ImmutableSortedSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
        boolean b3 = false;
        Preconditions.checkNotNull(e);
        Preconditions.checkNotNull(e2);
        if (this.comparator.compare((Object)e, (Object)e2) <= 0) {
            b3 = true;
        }
        Preconditions.checkArgument(b3);
        return this.subSetImpl(e, b, e2, b2);
    }
    
    abstract ImmutableSortedSet<E> subSetImpl(final E p0, final boolean p1, final E p2, final boolean p3);
    
    @Override
    public ImmutableSortedSet<E> tailSet(final E e) {
        return this.tailSet(e, true);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ImmutableSortedSet<E> tailSet(final E e, final boolean b) {
        return this.tailSetImpl(Preconditions.checkNotNull(e), b);
    }
    
    abstract ImmutableSortedSet<E> tailSetImpl(final E p0, final boolean p1);
    
    int unsafeCompare(final Object o, final Object o2) {
        return unsafeCompare(this.comparator, o, o2);
    }
    
    @Override
    Object writeReplace() {
        return new SerializedForm((Comparator<? super Object>)this.comparator, this.toArray());
    }
    
    public static final class Builder<E> extends ImmutableSet.Builder<E>
    {
        private final Comparator<? super E> comparator;
        
        public Builder(final Comparator<? super E> comparator) {
            this.comparator = Preconditions.checkNotNull(comparator);
        }
        
        public Builder<E> add(final E e) {
            super.add(e);
            return this;
        }
        
        public Builder<E> add(final E... array) {
            super.add(array);
            return this;
        }
        
        public Builder<E> addAll(final Iterable<? extends E> iterable) {
            super.addAll(iterable);
            return this;
        }
        
        public Builder<E> addAll(final Iterator<? extends E> iterator) {
            super.addAll(iterator);
            return this;
        }
        
        public ImmutableSortedSet<E> build() {
            final ImmutableSortedSet<Object> construct = ImmutableSortedSet.construct((Comparator<? super Object>)this.comparator, this.size, (Object[])this.contents);
            this.size = construct.size();
            return (ImmutableSortedSet<E>)construct;
        }
    }
    
    private static class SerializedForm<E> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final Comparator<? super E> comparator;
        final Object[] elements;
        
        public SerializedForm(final Comparator<? super E> comparator, final Object[] elements) {
            this.comparator = comparator;
            this.elements = elements;
        }
        
        Object readResolve() {
            return new Builder<Object>((Comparator<? super Object>)this.comparator).add((Object[])this.elements).build();
        }
    }
}
