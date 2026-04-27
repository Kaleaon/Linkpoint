// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Arrays;
import java.util.ArrayList;
import com.google.common.math.IntMath;
import java.util.Collections;
import com.google.common.math.LongMath;
import java.util.Iterator;
import java.util.AbstractCollection;
import com.google.common.base.Function;
import javax.annotation.Nullable;
import com.google.common.annotations.Beta;
import java.util.Comparator;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collection;
import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.annotations.GwtCompatible;
import javax.annotation.CheckReturnValue;

@CheckReturnValue
@GwtCompatible
public final class Collections2
{
    static final Joiner STANDARD_JOINER;
    
    static {
        STANDARD_JOINER = Joiner.on(", ").useForNull("null");
    }
    
    private Collections2() {
    }
    
    static <T> Collection<T> cast(final Iterable<T> iterable) {
        return (Collection<T>)iterable;
    }
    
    static boolean containsAllImpl(final Collection<?> collection, final Collection<?> collection2) {
        return Iterables.all(collection2, Predicates.in(collection));
    }
    
    @CheckReturnValue
    public static <E> Collection<E> filter(final Collection<E> collection, final Predicate<? super E> predicate) {
        if (!(collection instanceof FilteredCollection)) {
            return new FilteredCollection<E>(Preconditions.checkNotNull((FilteredCollection)collection), Preconditions.checkNotNull(predicate));
        }
        return ((FilteredCollection)collection).createCombined(predicate);
    }
    
    private static boolean isPermutation(final List<?> list, final List<?> list2) {
        return list.size() == list2.size() && HashMultiset.create((Iterable<?>)list).equals(HashMultiset.create((Iterable<?>)list2));
    }
    
    private static boolean isPositiveInt(final long n) {
        boolean b = true;
        int n2;
        if (n < 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 != 0) {
            return false;
        }
        int n3;
        if (n > 2147483647L) {
            n3 = 1;
        }
        else {
            n3 = 0;
        }
        if (n3 != 0) {
            return false;
        }
        return b;
        b = false;
        return b;
    }
    
    static StringBuilder newStringBuilderForCollection(final int n) {
        CollectPreconditions.checkNonnegative(n, "size");
        return new StringBuilder((int)Math.min(n * 8L, 1073741824L));
    }
    
    @Beta
    public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(final Iterable<E> iterable) {
        return orderedPermutations(iterable, Ordering.natural());
    }
    
    @Beta
    public static <E> Collection<List<E>> orderedPermutations(final Iterable<E> iterable, final Comparator<? super E> comparator) {
        return (Collection<List<E>>)new OrderedPermutationCollection((Iterable<Object>)iterable, (Comparator<? super Object>)comparator);
    }
    
    @Beta
    public static <E> Collection<List<E>> permutations(final Collection<E> collection) {
        return (Collection<List<E>>)new PermutationCollection(ImmutableList.copyOf((Collection<?>)collection));
    }
    
    static boolean safeContains(final Collection<?> collection, @Nullable final Object o) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.contains(o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NullPointerException ex2) {
            return false;
        }
    }
    
    static boolean safeRemove(final Collection<?> collection, @Nullable final Object o) {
        Preconditions.checkNotNull(collection);
        try {
            return collection.remove(o);
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NullPointerException ex2) {
            return false;
        }
    }
    
    static String toStringImpl(final Collection<?> collection) {
        final StringBuilder append = newStringBuilderForCollection(collection.size()).append('[');
        Collections2.STANDARD_JOINER.appendTo(append, Iterables.transform((Iterable<Object>)collection, (Function<? super Object, ?>)new Function<Object, Object>() {
            @Override
            public Object apply(Object o) {
                if (o == collection) {
                    o = "(this Collection)";
                }
                return o;
            }
        }));
        return append.append(']').toString();
    }
    
    public static <F, T> Collection<T> transform(final Collection<F> collection, final Function<? super F, T> function) {
        return (Collection<T>)new TransformedCollection((Collection<Object>)collection, (Function<? super Object, ?>)function);
    }
    
    static class FilteredCollection<E> extends AbstractCollection<E>
    {
        final Predicate<? super E> predicate;
        final Collection<E> unfiltered;
        
        FilteredCollection(final Collection<E> unfiltered, final Predicate<? super E> predicate) {
            this.unfiltered = unfiltered;
            this.predicate = predicate;
        }
        
        @Override
        public boolean add(final E e) {
            Preconditions.checkArgument(this.predicate.apply(e));
            return this.unfiltered.add(e);
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> collection) {
            final Iterator<? extends E> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Preconditions.checkArgument(this.predicate.apply((Object)iterator.next()));
            }
            return this.unfiltered.addAll(collection);
        }
        
        @Override
        public void clear() {
            Iterables.removeIf(this.unfiltered, this.predicate);
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return Collections2.safeContains(this.unfiltered, o) && this.predicate.apply((Object)o);
        }
        
        @Override
        public boolean containsAll(final Collection<?> collection) {
            return Collections2.containsAllImpl(this, collection);
        }
        
        FilteredCollection<E> createCombined(final Predicate<? super E> predicate) {
            return new FilteredCollection<E>(this.unfiltered, Predicates.and(this.predicate, predicate));
        }
        
        @Override
        public boolean isEmpty() {
            boolean b = false;
            if (!Iterables.any(this.unfiltered, this.predicate)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public Iterator<E> iterator() {
            return Iterators.filter(this.unfiltered.iterator(), this.predicate);
        }
        
        @Override
        public boolean remove(final Object o) {
            boolean b = false;
            if (this.contains(o) && this.unfiltered.remove(o)) {
                b = true;
            }
            return b;
        }
        
        @Override
        public boolean removeAll(final Collection<?> collection) {
            return Iterables.removeIf(this.unfiltered, Predicates.and(this.predicate, Predicates.in((Collection<? extends E>)collection)));
        }
        
        @Override
        public boolean retainAll(final Collection<?> collection) {
            return Iterables.removeIf(this.unfiltered, Predicates.and(this.predicate, (Predicate<? super E>)Predicates.not(Predicates.in((Collection<? extends T>)collection))));
        }
        
        @Override
        public int size() {
            return Iterators.size(this.iterator());
        }
        
        @Override
        public Object[] toArray() {
            return Lists.newArrayList(this.iterator()).toArray();
        }
        
        @Override
        public <T> T[] toArray(final T[] a) {
            return Lists.newArrayList(this.iterator()).toArray(a);
        }
    }
    
    private static final class OrderedPermutationCollection<E> extends AbstractCollection<List<E>>
    {
        final Comparator<? super E> comparator;
        final ImmutableList<E> inputList;
        final int size;
        
        OrderedPermutationCollection(final Iterable<E> iterable, final Comparator<? super E> comparator) {
            this.inputList = Ordering.from(comparator).immutableSortedCopy(iterable);
            this.comparator = comparator;
            this.size = calculateSize(this.inputList, comparator);
        }
        
        private static <E> int calculateSize(final List<E> list, final Comparator<? super E> comparator) {
            int n = 1;
            long n2 = 1L;
            int i;
            for (i = 1; i < list.size(); ++i, ++n) {
                if (comparator.compare((E)list.get(i - 1), (E)list.get(i)) < 0) {
                    n2 *= LongMath.binomial(i, n);
                    if (!isPositiveInt(n2)) {
                        return Integer.MAX_VALUE;
                    }
                    n = 0;
                }
            }
            final long n3 = LongMath.binomial(i, n) * n2;
            if (isPositiveInt(n3)) {
                return (int)n3;
            }
            return Integer.MAX_VALUE;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return o instanceof List && isPermutation(this.inputList, (List)o);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public Iterator<List<E>> iterator() {
            return (Iterator<List<E>>)new OrderedPermutationIterator((List<Object>)this.inputList, (Comparator<? super Object>)this.comparator);
        }
        
        @Override
        public int size() {
            return this.size;
        }
        
        @Override
        public String toString() {
            return "orderedPermutationCollection(" + this.inputList + ")";
        }
    }
    
    private static final class OrderedPermutationIterator<E> extends AbstractIterator<List<E>>
    {
        final Comparator<? super E> comparator;
        List<E> nextPermutation;
        
        OrderedPermutationIterator(final List<E> list, final Comparator<? super E> comparator) {
            this.nextPermutation = (List<E>)Lists.newArrayList((Iterable<?>)list);
            this.comparator = comparator;
        }
        
        void calculateNextPermutation() {
            final int nextJ = this.findNextJ();
            if (nextJ != -1) {
                Collections.swap(this.nextPermutation, nextJ, this.findNextL(nextJ));
                Collections.reverse(this.nextPermutation.subList(nextJ + 1, this.nextPermutation.size()));
                return;
            }
            this.nextPermutation = null;
        }
        
        @Override
        protected List<E> computeNext() {
            if (this.nextPermutation != null) {
                final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)this.nextPermutation);
                this.calculateNextPermutation();
                return (List<E>)copy;
            }
            return this.endOfData();
        }
        
        int findNextJ() {
            for (int i = this.nextPermutation.size() - 2; i >= 0; --i) {
                if (this.comparator.compare((Object)this.nextPermutation.get(i), (Object)this.nextPermutation.get(i + 1)) < 0) {
                    return i;
                }
            }
            return -1;
        }
        
        int findNextL(final int n) {
            final E value = this.nextPermutation.get(n);
            int size = this.nextPermutation.size();
            int n2;
            do {
                n2 = size - 1;
                if (n2 <= n) {
                    throw new AssertionError((Object)"this statement should be unreachable");
                }
                size = n2;
            } while (this.comparator.compare((Object)value, (Object)this.nextPermutation.get(n2)) >= 0);
            return n2;
        }
    }
    
    private static final class PermutationCollection<E> extends AbstractCollection<List<E>>
    {
        final ImmutableList<E> inputList;
        
        PermutationCollection(final ImmutableList<E> inputList) {
            this.inputList = inputList;
        }
        
        @Override
        public boolean contains(@Nullable final Object o) {
            return o instanceof List && isPermutation(this.inputList, (List)o);
        }
        
        @Override
        public boolean isEmpty() {
            return false;
        }
        
        @Override
        public Iterator<List<E>> iterator() {
            return (Iterator<List<E>>)new PermutationIterator((List<Object>)this.inputList);
        }
        
        @Override
        public int size() {
            return IntMath.factorial(this.inputList.size());
        }
        
        @Override
        public String toString() {
            return "permutations(" + this.inputList + ")";
        }
    }
    
    private static class PermutationIterator<E> extends AbstractIterator<List<E>>
    {
        final int[] c;
        int j;
        final List<E> list;
        final int[] o;
        
        PermutationIterator(final List<E> c) {
            this.list = new ArrayList<E>((Collection<? extends E>)c);
            final int size = c.size();
            this.c = new int[size];
            this.o = new int[size];
            Arrays.fill(this.c, 0);
            Arrays.fill(this.o, 1);
            this.j = Integer.MAX_VALUE;
        }
        
        void calculateNextPermutation() {
            int n = 0;
            this.j = this.list.size() - 1;
            if (this.j != -1) {
                while (true) {
                    final int n2 = this.c[this.j] + this.o[this.j];
                    if (n2 >= 0) {
                        if (n2 != this.j + 1) {
                            Collections.swap(this.list, this.j - this.c[this.j] + n, n + (this.j - n2));
                            this.c[this.j] = n2;
                            break;
                        }
                        if (this.j == 0) {
                            break;
                        }
                        ++n;
                        this.switchDirection();
                    }
                    else {
                        this.switchDirection();
                    }
                }
            }
        }
        
        @Override
        protected List<E> computeNext() {
            if (this.j > 0) {
                final ImmutableList<Object> copy = ImmutableList.copyOf((Collection<?>)this.list);
                this.calculateNextPermutation();
                return (List<E>)copy;
            }
            return this.endOfData();
        }
        
        void switchDirection() {
            this.o[this.j] = -this.o[this.j];
            --this.j;
        }
    }
    
    static class TransformedCollection<F, T> extends AbstractCollection<T>
    {
        final Collection<F> fromCollection;
        final Function<? super F, ? extends T> function;
        
        TransformedCollection(final Collection<F> collection, final Function<? super F, ? extends T> function) {
            this.fromCollection = Preconditions.checkNotNull(collection);
            this.function = Preconditions.checkNotNull(function);
        }
        
        @Override
        public void clear() {
            this.fromCollection.clear();
        }
        
        @Override
        public boolean isEmpty() {
            return this.fromCollection.isEmpty();
        }
        
        @Override
        public Iterator<T> iterator() {
            return Iterators.transform(this.fromCollection.iterator(), this.function);
        }
        
        @Override
        public int size() {
            return this.fromCollection.size();
        }
    }
}
