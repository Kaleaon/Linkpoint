// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.AbstractSequentialList;
import java.util.NoSuchElementException;
import com.google.common.math.IntMath;
import java.math.RoundingMode;
import java.io.Serializable;
import com.google.common.base.Function;
import javax.annotation.CheckReturnValue;
import java.util.AbstractList;
import java.util.LinkedList;
import com.google.common.annotations.GwtIncompatible;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.google.common.base.Objects;
import java.util.RandomAccess;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Ints;
import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Lists
{
    private Lists() {
    }
    
    static <E> boolean addAllImpl(final List<E> list, final int n, final Iterable<? extends E> iterable) {
        boolean b = false;
        final ListIterator<E> listIterator = list.listIterator(n);
        final Iterator<? extends E> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            listIterator.add((E)iterator.next());
            b = true;
        }
        return b;
    }
    
    public static <E> List<E> asList(@Nullable final E e, @Nullable final E e2, final E[] array) {
        return new TwoPlusArrayList<E>(e, e2, array);
    }
    
    public static <E> List<E> asList(@Nullable final E e, final E[] array) {
        return new OnePlusArrayList<E>(e, array);
    }
    
    public static <B> List<List<B>> cartesianProduct(final List<? extends List<? extends B>> list) {
        return CartesianList.create(list);
    }
    
    public static <B> List<List<B>> cartesianProduct(final List<? extends B>... a) {
        return cartesianProduct((List<? extends List<? extends B>>)Arrays.asList((List<? extends B>[])a));
    }
    
    static <T> List<T> cast(final Iterable<T> iterable) {
        return (List<T>)iterable;
    }
    
    @Beta
    public static ImmutableList<Character> charactersOf(final String s) {
        return new StringAsImmutableList(Preconditions.checkNotNull(s));
    }
    
    @Beta
    public static List<Character> charactersOf(final CharSequence charSequence) {
        return new CharSequenceAsList(Preconditions.checkNotNull(charSequence));
    }
    
    @VisibleForTesting
    static int computeArrayListCapacity(final int n) {
        CollectPreconditions.checkNonnegative(n, "arraySize");
        return Ints.saturatedCast(n + 5L + n / 10);
    }
    
    static boolean equalsImpl(final List<?> list, @Nullable final Object o) {
        if (o == Preconditions.checkNotNull(list)) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        final List list2 = (List)o;
        final int size = list.size();
        if (size != list2.size()) {
            return false;
        }
        if (list instanceof RandomAccess && list2 instanceof RandomAccess) {
            for (int i = 0; i < size; ++i) {
                if (!Objects.equal(list.get(i), list2.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return Iterators.elementsEqual(list.iterator(), list2.iterator());
    }
    
    static int hashCodeImpl(final List<?> list) {
        int n = 1;
        for (final Object next : list) {
            int hashCode;
            if (next != null) {
                hashCode = next.hashCode();
            }
            else {
                hashCode = 0;
            }
            n = ~(~(hashCode + n * 31));
        }
        return n;
    }
    
    static int indexOfImpl(final List<?> list, @Nullable final Object o) {
        if (!(list instanceof RandomAccess)) {
            final ListIterator<?> listIterator = list.listIterator();
            while (listIterator.hasNext()) {
                if (Objects.equal(o, listIterator.next())) {
                    return listIterator.previousIndex();
                }
            }
            return -1;
        }
        return indexOfRandomAccess(list, o);
    }
    
    private static int indexOfRandomAccess(final List<?> list, @Nullable final Object o) {
        int i = 0;
        final int n = 0;
        final int size = list.size();
        if (o != null) {
            for (int j = n; j < size; ++j) {
                if (o.equals(list.get(j))) {
                    return j;
                }
            }
        }
        else {
            while (i < size) {
                if (list.get(i) == null) {
                    return i;
                }
                ++i;
            }
        }
        return -1;
    }
    
    static int lastIndexOfImpl(final List<?> list, @Nullable final Object o) {
        if (!(list instanceof RandomAccess)) {
            final ListIterator<?> listIterator = list.listIterator(list.size());
            while (listIterator.hasPrevious()) {
                if (Objects.equal(o, listIterator.previous())) {
                    return listIterator.nextIndex();
                }
            }
            return -1;
        }
        return lastIndexOfRandomAccess(list, o);
    }
    
    private static int lastIndexOfRandomAccess(final List<?> list, @Nullable final Object o) {
        if (o != null) {
            int size = list.size();
            int n;
            do {
                n = size - 1;
                if (n < 0) {
                    return -1;
                }
                size = n;
            } while (!o.equals(list.get(n)));
            return n;
        }
        int size2 = list.size();
        int n2;
        do {
            n2 = size2 - 1;
            if (n2 < 0) {
                return -1;
            }
            size2 = n2;
        } while (list.get(n2) != null);
        return n2;
    }
    
    static <E> ListIterator<E> listIteratorImpl(final List<E> list, final int index) {
        return new AbstractListWrapper<E>(list).listIterator(index);
    }
    
    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<E>();
    }
    
    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList(final Iterable<? extends E> iterable) {
        Preconditions.checkNotNull(iterable);
        ArrayList<Object> arrayList;
        if (!(iterable instanceof Collection)) {
            arrayList = newArrayList((Iterator<?>)iterable.iterator());
        }
        else {
            arrayList = new ArrayList<Object>(Collections2.cast((Iterable<? extends E>)iterable));
        }
        return (ArrayList<E>)arrayList;
    }
    
    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList(final Iterator<? extends E> iterator) {
        final ArrayList<Object> arrayList = newArrayList();
        Iterators.addAll(arrayList, iterator);
        return (ArrayList<E>)arrayList;
    }
    
    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayList(final E... elements) {
        Preconditions.checkNotNull(elements);
        final ArrayList c = new ArrayList(computeArrayListCapacity(elements.length));
        Collections.addAll(c, elements);
        return c;
    }
    
    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayListWithCapacity(final int initialCapacity) {
        CollectPreconditions.checkNonnegative(initialCapacity, "initialArraySize");
        return new ArrayList<E>(initialCapacity);
    }
    
    @GwtCompatible(serializable = true)
    public static <E> ArrayList<E> newArrayListWithExpectedSize(final int n) {
        return new ArrayList<E>(computeArrayListCapacity(n));
    }
    
    @GwtIncompatible("CopyOnWriteArrayList")
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
        return new CopyOnWriteArrayList<E>();
    }
    
    @GwtIncompatible("CopyOnWriteArrayList")
    public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(final Iterable<? extends E> iterable) {
        Collection<Object> c;
        if (!(iterable instanceof Collection)) {
            c = newArrayList((Iterable<?>)iterable);
        }
        else {
            c = Collections2.cast((Iterable<Object>)iterable);
        }
        return new CopyOnWriteArrayList<E>((Collection<? extends E>)c);
    }
    
    @GwtCompatible(serializable = true)
    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<E>();
    }
    
    @GwtCompatible(serializable = true)
    public static <E> LinkedList<E> newLinkedList(final Iterable<? extends E> iterable) {
        final LinkedList<Object> linkedList = newLinkedList();
        Iterables.addAll(linkedList, iterable);
        return (LinkedList<E>)linkedList;
    }
    
    public static <T> List<List<T>> partition(final List<T> list, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(list);
        if (n > 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        AbstractList<List<T>> list2;
        if (!(list instanceof RandomAccess)) {
            list2 = new Partition<Object>(list, n);
        }
        else {
            list2 = new RandomAccessPartition<Object>(list, n);
        }
        return (List<List<T>>)list2;
    }
    
    @CheckReturnValue
    public static <T> List<T> reverse(final List<T> list) {
        if (list instanceof ImmutableList) {
            return ((ImmutableList)list).reverse();
        }
        if (list instanceof ReverseList) {
            return ((ReverseList)list).getForwardList();
        }
        if (!(list instanceof RandomAccess)) {
            return new ReverseList<T>(list);
        }
        return new RandomAccessReverseList<T>(list);
    }
    
    static <E> List<E> subListImpl(final List<E> list, final int n, final int n2) {
        AbstractListWrapper<E> abstractListWrapper;
        if (!(list instanceof RandomAccess)) {
            abstractListWrapper = new AbstractListWrapper<E>(list) {
                private static final long serialVersionUID = 0L;
                
                @Override
                public ListIterator<E> listIterator(final int n) {
                    return (ListIterator<E>)this.backingList.listIterator(n);
                }
            };
        }
        else {
            abstractListWrapper = new RandomAccessListWrapper<E>(list) {
                private static final long serialVersionUID = 0L;
                
                @Override
                public ListIterator<E> listIterator(final int n) {
                    return (ListIterator<E>)this.backingList.listIterator(n);
                }
            };
        }
        return (List<E>)abstractListWrapper.subList(n, n2);
    }
    
    @CheckReturnValue
    public static <F, T> List<T> transform(final List<F> list, final Function<? super F, ? extends T> function) {
        Serializable s;
        if (!(list instanceof RandomAccess)) {
            s = new TransformingSequentialList<Object, Object>(list, function);
        }
        else {
            s = new TransformingRandomAccessList<Object, Object>(list, function);
        }
        return (List<T>)s;
    }
    
    private static class AbstractListWrapper<E> extends AbstractList<E>
    {
        final List<E> backingList;
        
        AbstractListWrapper(final List<E> list) {
            this.backingList = Preconditions.checkNotNull(list);
        }
        
        @Override
        public void add(final int n, final E e) {
            this.backingList.add(n, e);
        }
        
        @Override
        public boolean addAll(final int n, final Collection<? extends E> collection) {
            return this.backingList.addAll(n, collection);
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.backingList.contains(o);
        }
        
        @Override
        public E get(final int n) {
            return this.backingList.get(n);
        }
        
        @Override
        public E remove(final int n) {
            return this.backingList.remove(n);
        }
        
        @Override
        public E set(final int n, final E e) {
            return this.backingList.set(n, e);
        }
        
        @Override
        public int size() {
            return this.backingList.size();
        }
    }
    
    private static final class CharSequenceAsList extends AbstractList<Character>
    {
        private final CharSequence sequence;
        
        CharSequenceAsList(final CharSequence sequence) {
            this.sequence = sequence;
        }
        
        @Override
        public Character get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            return this.sequence.charAt(n);
        }
        
        @Override
        public int size() {
            return this.sequence.length();
        }
    }
    
    private static class OnePlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess
    {
        private static final long serialVersionUID = 0L;
        final E first;
        final E[] rest;
        
        OnePlusArrayList(@Nullable final E first, final E[] array) {
            this.first = first;
            this.rest = Preconditions.checkNotNull(array);
        }
        
        @Override
        public E get(final int n) {
            Preconditions.checkElementIndex(n, this.size());
            E first;
            if (n != 0) {
                first = this.rest[n - 1];
            }
            else {
                first = this.first;
            }
            return first;
        }
        
        @Override
        public int size() {
            return this.rest.length + 1;
        }
    }
    
    private static class Partition<T> extends AbstractList<List<T>>
    {
        final List<T> list;
        final int size;
        
        Partition(final List<T> list, final int size) {
            this.list = list;
            this.size = size;
        }
        
        @Override
        public List<T> get(int min) {
            Preconditions.checkElementIndex(min, this.size());
            final int n = this.size * min;
            min = Math.min(this.size + n, this.list.size());
            return this.list.subList(n, min);
        }
        
        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }
        
        @Override
        public int size() {
            return IntMath.divide(this.list.size(), this.size, RoundingMode.CEILING);
        }
    }
    
    private static class RandomAccessListWrapper<E> extends AbstractListWrapper<E> implements RandomAccess
    {
        RandomAccessListWrapper(final List<E> list) {
            super(list);
        }
    }
    
    private static class RandomAccessPartition<T> extends Partition<T> implements RandomAccess
    {
        RandomAccessPartition(final List<T> list, final int n) {
            super(list, n);
        }
    }
    
    private static class RandomAccessReverseList<T> extends ReverseList<T> implements RandomAccess
    {
        RandomAccessReverseList(final List<T> list) {
            super(list);
        }
    }
    
    private static class ReverseList<T> extends AbstractList<T>
    {
        private final List<T> forwardList;
        
        ReverseList(final List<T> list) {
            this.forwardList = Preconditions.checkNotNull(list);
        }
        
        private int reverseIndex(final int n) {
            final int size = this.size();
            Preconditions.checkElementIndex(n, size);
            return size - 1 - n;
        }
        
        private int reversePosition(final int n) {
            final int size = this.size();
            Preconditions.checkPositionIndex(n, size);
            return size - n;
        }
        
        @Override
        public void add(final int n, @Nullable final T t) {
            this.forwardList.add(this.reversePosition(n), t);
        }
        
        @Override
        public void clear() {
            this.forwardList.clear();
        }
        
        @Override
        public T get(final int n) {
            return this.forwardList.get(this.reverseIndex(n));
        }
        
        List<T> getForwardList() {
            return this.forwardList;
        }
        
        @Override
        public Iterator<T> iterator() {
            return this.listIterator();
        }
        
        @Override
        public ListIterator<T> listIterator(int reversePosition) {
            reversePosition = this.reversePosition(reversePosition);
            return new ListIterator<T>() {
                boolean canRemoveOrSet;
                final /* synthetic */ ListIterator val$forwardIterator = ReverseList.this.forwardList.listIterator(reversePosition);
                
                @Override
                public void add(final T t) {
                    this.val$forwardIterator.add(t);
                    this.val$forwardIterator.previous();
                    this.canRemoveOrSet = false;
                }
                
                @Override
                public boolean hasNext() {
                    return this.val$forwardIterator.hasPrevious();
                }
                
                @Override
                public boolean hasPrevious() {
                    return this.val$forwardIterator.hasNext();
                }
                
                @Override
                public T next() {
                    if (this.hasNext()) {
                        this.canRemoveOrSet = true;
                        return this.val$forwardIterator.previous();
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public int nextIndex() {
                    return ReverseList.this.reversePosition(this.val$forwardIterator.nextIndex());
                }
                
                @Override
                public T previous() {
                    if (this.hasPrevious()) {
                        this.canRemoveOrSet = true;
                        return this.val$forwardIterator.next();
                    }
                    throw new NoSuchElementException();
                }
                
                @Override
                public int previousIndex() {
                    return this.nextIndex() - 1;
                }
                
                @Override
                public void remove() {
                    CollectPreconditions.checkRemove(this.canRemoveOrSet);
                    this.val$forwardIterator.remove();
                    this.canRemoveOrSet = false;
                }
                
                @Override
                public void set(final T t) {
                    Preconditions.checkState(this.canRemoveOrSet);
                    this.val$forwardIterator.set(t);
                }
            };
        }
        
        @Override
        public T remove(final int n) {
            return this.forwardList.remove(this.reverseIndex(n));
        }
        
        @Override
        protected void removeRange(final int n, final int n2) {
            this.subList(n, n2).clear();
        }
        
        @Override
        public T set(final int n, @Nullable final T t) {
            return this.forwardList.set(this.reverseIndex(n), t);
        }
        
        @Override
        public int size() {
            return this.forwardList.size();
        }
        
        @Override
        public List<T> subList(final int n, final int n2) {
            Preconditions.checkPositionIndexes(n, n2, this.size());
            return Lists.reverse(this.forwardList.subList(this.reversePosition(n2), this.reversePosition(n)));
        }
    }
    
    private static final class StringAsImmutableList extends ImmutableList<Character>
    {
        private final String string;
        
        StringAsImmutableList(final String string) {
            this.string = string;
        }
        
        @Override
        public Character get(final int index) {
            Preconditions.checkElementIndex(index, this.size());
            return this.string.charAt(index);
        }
        
        @Override
        public int indexOf(@Nullable final Object o) {
            int index;
            if (!(o instanceof Character)) {
                index = -1;
            }
            else {
                index = this.string.indexOf((char)o);
            }
            return index;
        }
        
        @Override
        boolean isPartialView() {
            return false;
        }
        
        @Override
        public int lastIndexOf(@Nullable final Object o) {
            int lastIndex;
            if (!(o instanceof Character)) {
                lastIndex = -1;
            }
            else {
                lastIndex = this.string.lastIndexOf((char)o);
            }
            return lastIndex;
        }
        
        @Override
        public int size() {
            return this.string.length();
        }
        
        @Override
        public ImmutableList<Character> subList(final int beginIndex, final int endIndex) {
            Preconditions.checkPositionIndexes(beginIndex, endIndex, this.size());
            return Lists.charactersOf(this.string.substring(beginIndex, endIndex));
        }
    }
    
    private static class TransformingRandomAccessList<F, T> extends AbstractList<T> implements RandomAccess, Serializable
    {
        private static final long serialVersionUID = 0L;
        final List<F> fromList;
        final Function<? super F, ? extends T> function;
        
        TransformingRandomAccessList(final List<F> list, final Function<? super F, ? extends T> function) {
            this.fromList = Preconditions.checkNotNull(list);
            this.function = Preconditions.checkNotNull(function);
        }
        
        @Override
        public void clear() {
            this.fromList.clear();
        }
        
        @Override
        public T get(final int n) {
            return (T)this.function.apply(this.fromList.get(n));
        }
        
        @Override
        public boolean isEmpty() {
            return this.fromList.isEmpty();
        }
        
        @Override
        public Iterator<T> iterator() {
            return this.listIterator();
        }
        
        @Override
        public ListIterator<T> listIterator(final int n) {
            return new TransformedListIterator<F, T>(this.fromList.listIterator(n)) {
                @Override
                T transform(final F n) {
                    return (T)TransformingRandomAccessList.this.function.apply(n);
                }
            };
        }
        
        @Override
        public T remove(final int n) {
            return (T)this.function.apply(this.fromList.remove(n));
        }
        
        @Override
        public int size() {
            return this.fromList.size();
        }
    }
    
    private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final List<F> fromList;
        final Function<? super F, ? extends T> function;
        
        TransformingSequentialList(final List<F> list, final Function<? super F, ? extends T> function) {
            this.fromList = Preconditions.checkNotNull(list);
            this.function = Preconditions.checkNotNull(function);
        }
        
        @Override
        public void clear() {
            this.fromList.clear();
        }
        
        @Override
        public ListIterator<T> listIterator(final int n) {
            return new TransformedListIterator<F, T>(this.fromList.listIterator(n)) {
                @Override
                T transform(final F n) {
                    return (T)TransformingSequentialList.this.function.apply(n);
                }
            };
        }
        
        @Override
        public int size() {
            return this.fromList.size();
        }
    }
    
    private static class TwoPlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess
    {
        private static final long serialVersionUID = 0L;
        final E first;
        final E[] rest;
        final E second;
        
        TwoPlusArrayList(@Nullable final E first, @Nullable final E second, final E[] array) {
            this.first = first;
            this.second = second;
            this.rest = Preconditions.checkNotNull(array);
        }
        
        @Override
        public E get(final int n) {
            switch (n) {
                default: {
                    Preconditions.checkElementIndex(n, this.size());
                    return this.rest[n - 2];
                }
                case 0: {
                    return this.first;
                }
                case 1: {
                    return this.second;
                }
            }
        }
        
        @Override
        public int size() {
            return this.rest.length + 2;
        }
    }
}
