// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.Set;
import java.util.NavigableSet;
import java.util.Collections;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Comparator;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.Beta;

@Beta
@GwtIncompatible("hasn't been tested yet")
public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E> implements SortedMultiset<E>
{
    private static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET;
    private static final Comparator<Comparable> NATURAL_ORDER;
    transient ImmutableSortedMultiset<E> descendingMultiset;
    
    static {
        NATURAL_ORDER = Ordering.natural();
        NATURAL_EMPTY_MULTISET = new RegularImmutableSortedMultiset<Comparable>(ImmutableSortedMultiset.NATURAL_ORDER);
    }
    
    ImmutableSortedMultiset() {
    }
    
    public static <E> ImmutableSortedMultiset<E> copyOf(final Iterable<? extends E> iterable) {
        return copyOf((Comparator<? super E>)Ordering.natural(), iterable);
    }
    
    public static <E> ImmutableSortedMultiset<E> copyOf(final Comparator<? super E> comparator, final Iterable<? extends E> iterable) {
        if (iterable instanceof ImmutableSortedMultiset) {
            final ImmutableSortedMultiset immutableSortedMultiset = (ImmutableSortedMultiset)iterable;
            if (comparator.equals(immutableSortedMultiset.comparator())) {
                if (!immutableSortedMultiset.isPartialView()) {
                    return immutableSortedMultiset;
                }
                return copyOfSortedEntries(comparator, (Collection<Entry<E>>)immutableSortedMultiset.entrySet().asList());
            }
        }
        final ArrayList<Object> arrayList = Lists.newArrayList((Iterable<?>)iterable);
        final TreeMultiset<Object> create = TreeMultiset.create(Preconditions.checkNotNull(comparator));
        Iterables.addAll(create, arrayList);
        return copyOfSortedEntries(comparator, (Collection<Entry<E>>)create.entrySet());
    }
    
    public static <E> ImmutableSortedMultiset<E> copyOf(final Comparator<? super E> comparator, final Iterator<? extends E> iterator) {
        Preconditions.checkNotNull(comparator);
        return new Builder<E>(comparator).addAll(iterator).build();
    }
    
    public static <E> ImmutableSortedMultiset<E> copyOf(final Iterator<? extends E> iterator) {
        return copyOf((Comparator<? super E>)Ordering.natural(), iterator);
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(final E[] a) {
        return copyOf((Comparator<? super E>)Ordering.natural(), (Iterable<? extends E>)Arrays.asList(a));
    }
    
    public static <E> ImmutableSortedMultiset<E> copyOfSorted(final SortedMultiset<E> sortedMultiset) {
        return copyOfSortedEntries(sortedMultiset.comparator(), (Collection<Entry<E>>)Lists.newArrayList((Iterable<?>)sortedMultiset.entrySet()));
    }
    
    private static <E> ImmutableSortedMultiset<E> copyOfSortedEntries(final Comparator<? super E> comparator, final Collection<Entry<E>> collection) {
        if (!collection.isEmpty()) {
            final ImmutableList.Builder<Object> builder = new ImmutableList.Builder<Object>(collection.size());
            final long[] array = new long[collection.size() + 1];
            final Iterator iterator = collection.iterator();
            int n = 0;
            while (iterator.hasNext()) {
                final Multiset.Entry<Object> entry = (Multiset.Entry<Object>)iterator.next();
                builder.add(entry.getElement());
                array[n + 1] = array[n] + entry.getCount();
                ++n;
            }
            return new RegularImmutableSortedMultiset<E>(new RegularImmutableSortedSet<Object>((ImmutableList<Object>)builder.build(), (Comparator<? super Object>)comparator), array, 0, collection.size());
        }
        return emptyMultiset(comparator);
    }
    
    static <E> ImmutableSortedMultiset<E> emptyMultiset(final Comparator<? super E> comparator) {
        if (!ImmutableSortedMultiset.NATURAL_ORDER.equals(comparator)) {
            return new RegularImmutableSortedMultiset<E>(comparator);
        }
        return (ImmutableSortedMultiset<E>)ImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    }
    
    public static <E extends Comparable<?>> Builder<E> naturalOrder() {
        return new Builder<E>(Ordering.natural());
    }
    
    public static <E> ImmutableSortedMultiset<E> of() {
        return (ImmutableSortedMultiset<E>)ImmutableSortedMultiset.NATURAL_EMPTY_MULTISET;
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(final E e) {
        return new RegularImmutableSortedMultiset<E>((RegularImmutableSortedSet)ImmutableSortedSet.of(e), new long[] { 0L, 1L }, 0, 1);
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(final E e, final E e2) {
        return copyOf((Comparator<? super E>)Ordering.natural(), (Iterable<? extends E>)Arrays.asList(e, e2));
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(final E e, final E e2, final E e3) {
        return copyOf((Comparator<? super E>)Ordering.natural(), (Iterable<? extends E>)Arrays.asList(e, e2, e3));
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(final E e, final E e2, final E e3, final E e4) {
        return copyOf((Comparator<? super E>)Ordering.natural(), (Iterable<? extends E>)Arrays.asList(e, e2, e3, e4));
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(final E e, final E e2, final E e3, final E e4, final E e5) {
        return copyOf((Comparator<? super E>)Ordering.natural(), (Iterable<? extends E>)Arrays.asList(e, e2, e3, e4, e5));
    }
    
    public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(final E e, final E e2, final E e3, final E e4, final E e5, final E e6, final E... elements) {
        final ArrayList<Object> arrayListWithCapacity = Lists.newArrayListWithCapacity(elements.length + 6);
        Collections.addAll(arrayListWithCapacity, e, e2, e3, e4, e5, e6);
        Collections.addAll(arrayListWithCapacity, elements);
        return (ImmutableSortedMultiset<E>)copyOf((Comparator<? super Object>)Ordering.natural(), (Iterable<?>)arrayListWithCapacity);
    }
    
    public static <E> Builder<E> orderedBy(final Comparator<E> comparator) {
        return new Builder<E>(comparator);
    }
    
    public static <E extends Comparable<?>> Builder<E> reverseOrder() {
        return new Builder<E>(Ordering.natural().reverse());
    }
    
    @Override
    public final Comparator<? super E> comparator() {
        return this.elementSet().comparator();
    }
    
    @Override
    public ImmutableSortedMultiset<E> descendingMultiset() {
        final ImmutableSortedMultiset<E> descendingMultiset = this.descendingMultiset;
        if (descendingMultiset != null) {
            return descendingMultiset;
        }
        ImmutableSortedMultiset<Object> emptyMultiset;
        if (!this.isEmpty()) {
            emptyMultiset = (ImmutableSortedMultiset<Object>)new DescendingImmutableSortedMultiset<E>(this);
        }
        else {
            emptyMultiset = (ImmutableSortedMultiset<Object>)emptyMultiset((Comparator<? super E>)Ordering.from(this.comparator()).reverse());
        }
        return this.descendingMultiset = (ImmutableSortedMultiset<E>)emptyMultiset;
    }
    
    @Override
    public abstract ImmutableSortedSet<E> elementSet();
    
    @Override
    public abstract ImmutableSortedMultiset<E> headMultiset(final E p0, final BoundType p1);
    
    @Deprecated
    @Override
    public final Entry<E> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public final Entry<E> pollLastEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public ImmutableSortedMultiset<E> subMultiset(final E e, final BoundType boundType, final E e2, final BoundType boundType2) {
        Preconditions.checkArgument(this.comparator().compare((Object)e, (Object)e2) <= 0, "Expected lowerBound <= upperBound but %s > %s", e, e2);
        return this.tailMultiset(e, boundType).headMultiset(e2, boundType2);
    }
    
    @Override
    public abstract ImmutableSortedMultiset<E> tailMultiset(final E p0, final BoundType p1);
    
    @Override
    Object writeReplace() {
        return new SerializedForm((SortedMultiset<Object>)this);
    }
    
    public static class Builder<E> extends ImmutableMultiset.Builder<E>
    {
        public Builder(final Comparator<? super E> comparator) {
            super(TreeMultiset.create(Preconditions.checkNotNull(comparator)));
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
        
        public Builder<E> addCopies(final E e, final int n) {
            super.addCopies(e, n);
            return this;
        }
        
        public ImmutableSortedMultiset<E> build() {
            return ImmutableSortedMultiset.copyOfSorted((SortedMultiset<E>)(SortedMultiset)this.contents);
        }
        
        public Builder<E> setCount(final E e, final int n) {
            super.setCount(e, n);
            return this;
        }
    }
    
    private static final class SerializedForm<E> implements Serializable
    {
        Comparator<? super E> comparator;
        int[] counts;
        E[] elements;
        
        SerializedForm(final SortedMultiset<E> sortedMultiset) {
            int n = 0;
            this.comparator = sortedMultiset.comparator();
            final int size = sortedMultiset.entrySet().size();
            this.elements = (E[])new Object[size];
            this.counts = new int[size];
            for (final Multiset.Entry<E> entry : sortedMultiset.entrySet()) {
                this.elements[n] = entry.getElement();
                this.counts[n] = entry.getCount();
                ++n;
            }
        }
        
        Object readResolve() {
            final int length = this.elements.length;
            final Builder<E> builder = new Builder<E>(this.comparator);
            for (int i = 0; i < length; ++i) {
                builder.addCopies(this.elements[i], this.counts[i]);
            }
            return builder.build();
        }
    }
}
