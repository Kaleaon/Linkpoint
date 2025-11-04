// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.PriorityQueue;
import java.util.Queue;
import com.google.common.base.Optional;
import com.google.common.base.Function;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import com.google.common.annotations.Beta;
import java.util.Comparator;
import com.google.common.annotations.GwtIncompatible;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import javax.annotation.Nullable;
import java.util.ListIterator;
import java.util.Enumeration;
import com.google.common.base.Predicate;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Iterators
{
    static final UnmodifiableListIterator<Object> EMPTY_LIST_ITERATOR;
    private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR;
    
    static {
        EMPTY_LIST_ITERATOR = new UnmodifiableListIterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public boolean hasPrevious() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new NoSuchElementException();
            }
            
            @Override
            public int nextIndex() {
                return 0;
            }
            
            @Override
            public Object previous() {
                throw new NoSuchElementException();
            }
            
            @Override
            public int previousIndex() {
                return -1;
            }
        };
        EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(false);
            }
        };
    }
    
    private Iterators() {
    }
    
    public static <T> boolean addAll(final Collection<T> collection, final Iterator<? extends T> iterator) {
        boolean b = false;
        Preconditions.checkNotNull(collection);
        Preconditions.checkNotNull(iterator);
        while (iterator.hasNext()) {
            b |= collection.add(iterator.next());
        }
        return b;
    }
    
    public static int advance(final Iterator<?> iterator, final int n) {
        int n2 = 0;
        Preconditions.checkNotNull(iterator);
        Preconditions.checkArgument(n >= 0, (Object)"numberToAdvance must be nonnegative");
        while (n2 < n && iterator.hasNext()) {
            iterator.next();
            ++n2;
        }
        return n2;
    }
    
    public static <T> boolean all(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        while (iterator.hasNext()) {
            if (!predicate.apply(iterator.next())) {
                return false;
            }
        }
        return true;
    }
    
    public static <T> boolean any(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        return indexOf(iterator, predicate) != -1;
    }
    
    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }
            
            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }
    
    static <T> ListIterator<T> cast(final Iterator<T> iterator) {
        return (ListIterator<T>)iterator;
    }
    
    static void checkNonnegative(final int i) {
        if (i >= 0) {
            return;
        }
        throw new IndexOutOfBoundsException("position (" + i + ") must not be negative");
    }
    
    static void clear(final Iterator<?> iterator) {
        Preconditions.checkNotNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
    
    public static <T> Iterator<T> concat(final Iterator<? extends Iterator<? extends T>> iterator) {
        Preconditions.checkNotNull(iterator);
        return new Iterator<T>() {
            Iterator<? extends T> current = Iterators.emptyIterator();
            Iterator<? extends T> removeFrom;
            
            @Override
            public boolean hasNext() {
                boolean hasNext;
                while (true) {
                    hasNext = Preconditions.checkNotNull(this.current).hasNext();
                    if (hasNext || !iterator.hasNext()) {
                        break;
                    }
                    this.current = iterator.next();
                }
                return hasNext;
            }
            
            @Override
            public T next() {
                if (this.hasNext()) {
                    this.removeFrom = this.current;
                    return (T)this.current.next();
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                CollectPreconditions.checkRemove(this.removeFrom != null);
                this.removeFrom.remove();
                this.removeFrom = null;
            }
        };
    }
    
    public static <T> Iterator<T> concat(final Iterator<? extends T> iterator, final Iterator<? extends T> iterator2) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkNotNull(iterator2);
        return concat((Iterator<? extends Iterator<? extends T>>)new ConsumingQueueIterator<Iterator<? extends T>>((Iterator<? extends T>[])new Iterator[] { iterator, iterator2 }));
    }
    
    public static <T> Iterator<T> concat(final Iterator<? extends T> iterator, final Iterator<? extends T> iterator2, final Iterator<? extends T> iterator3) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkNotNull(iterator2);
        Preconditions.checkNotNull(iterator3);
        return concat((Iterator<? extends Iterator<? extends T>>)new ConsumingQueueIterator<Iterator<? extends T>>((Iterator<? extends T>[])new Iterator[] { iterator, iterator2, iterator3 }));
    }
    
    public static <T> Iterator<T> concat(final Iterator<? extends T> iterator, final Iterator<? extends T> iterator2, final Iterator<? extends T> iterator3, final Iterator<? extends T> iterator4) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkNotNull(iterator2);
        Preconditions.checkNotNull(iterator3);
        Preconditions.checkNotNull(iterator4);
        return concat((Iterator<? extends Iterator<? extends T>>)new ConsumingQueueIterator<Iterator<? extends T>>((Iterator<? extends T>[])new Iterator[] { iterator, iterator2, iterator3, iterator4 }));
    }
    
    public static <T> Iterator<T> concat(final Iterator<? extends T>... array) {
        final Iterator[] array2 = Preconditions.checkNotNull(array);
        for (int length = array2.length, i = 0; i < length; ++i) {
            Preconditions.checkNotNull(array2[i]);
        }
        return concat((Iterator<? extends Iterator<? extends T>>)new ConsumingQueueIterator<Iterator<? extends T>>((Iterator<? extends T>[])array));
    }
    
    public static <T> Iterator<T> consumingIterator(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        return new UnmodifiableIterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            @Override
            public T next() {
                final T next = iterator.next();
                iterator.remove();
                return next;
            }
            
            @Override
            public String toString() {
                return "Iterators.consumingIterator(...)";
            }
        };
    }
    
    public static boolean contains(final Iterator<?> iterator, @Nullable final Object o) {
        return any(iterator, Predicates.equalTo(o));
    }
    
    public static <T> Iterator<T> cycle(final Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return new Iterator<T>() {
            Iterator<T> iterator = Iterators.emptyModifiableIterator();
            
            @Override
            public boolean hasNext() {
                boolean b = false;
                if (this.iterator.hasNext() || iterable.iterator().hasNext()) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public T next() {
                if (!this.iterator.hasNext()) {
                    this.iterator = iterable.iterator();
                    if (!this.iterator.hasNext()) {
                        throw new NoSuchElementException();
                    }
                }
                return this.iterator.next();
            }
            
            @Override
            public void remove() {
                this.iterator.remove();
            }
        };
    }
    
    public static <T> Iterator<T> cycle(final T... array) {
        return cycle(Lists.newArrayList(array));
    }
    
    public static boolean elementsEqual(final Iterator<?> iterator, final Iterator<?> iterator2) {
        boolean b = false;
        while (iterator.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            if (!Objects.equal(iterator.next(), iterator2.next())) {
                return false;
            }
        }
        if (!iterator2.hasNext()) {
            b = true;
        }
        return b;
    }
    
    @Deprecated
    public static <T> UnmodifiableIterator<T> emptyIterator() {
        return (UnmodifiableIterator<T>)emptyListIterator();
    }
    
    static <T> UnmodifiableListIterator<T> emptyListIterator() {
        return (UnmodifiableListIterator<T>)Iterators.EMPTY_LIST_ITERATOR;
    }
    
    static <T> Iterator<T> emptyModifiableIterator() {
        return (Iterator<T>)Iterators.EMPTY_MODIFIABLE_ITERATOR;
    }
    
    @CheckReturnValue
    public static <T> UnmodifiableIterator<T> filter(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(iterator);
        Preconditions.checkNotNull(predicate);
        return new AbstractIterator<T>() {
            @Override
            protected T computeNext() {
                while (iterator.hasNext()) {
                    final T next = iterator.next();
                    if (predicate.apply(next)) {
                        return next;
                    }
                }
                return this.endOfData();
            }
        };
    }
    
    @CheckReturnValue
    @GwtIncompatible("Class.isInstance")
    public static <T> UnmodifiableIterator<T> filter(final Iterator<?> iterator, final Class<T> clazz) {
        return filter(iterator, Predicates.instanceOf(clazz));
    }
    
    public static <T> T find(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        return filter(iterator, predicate).next();
    }
    
    @Nullable
    public static <T> T find(final Iterator<? extends T> iterator, final Predicate<? super T> predicate, @Nullable final T t) {
        return getNext((Iterator<? extends T>)filter((Iterator<Object>)iterator, (Predicate<? super Object>)predicate), t);
    }
    
    public static <T> UnmodifiableIterator<T> forArray(final T... array) {
        return forArray(array, 0, array.length, 0);
    }
    
    static <T> UnmodifiableListIterator<T> forArray(final T[] array, final int n, final int n2, final int n3) {
        boolean b = false;
        if (n2 >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        Preconditions.checkPositionIndexes(n, n + n2, array.length);
        Preconditions.checkPositionIndex(n3, n2);
        if (n2 != 0) {
            return new AbstractIndexedListIterator<T>(n2, n3) {
                @Override
                protected T get(final int n) {
                    return array[n + n];
                }
            };
        }
        return emptyListIterator();
    }
    
    public static <T> UnmodifiableIterator<T> forEnumeration(final Enumeration<T> enumeration) {
        Preconditions.checkNotNull(enumeration);
        return new UnmodifiableIterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }
            
            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }
    
    public static int frequency(final Iterator<?> iterator, @Nullable final Object o) {
        return size(filter(iterator, Predicates.equalTo(o)));
    }
    
    public static <T> T get(final Iterator<T> iterator, final int i) {
        checkNonnegative(i);
        final int advance = advance(iterator, i);
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new IndexOutOfBoundsException("position (" + i + ") must be less than the number of elements that remained (" + advance + ")");
    }
    
    @Nullable
    public static <T> T get(final Iterator<? extends T> iterator, final int n, @Nullable final T t) {
        checkNonnegative(n);
        advance(iterator, n);
        return getNext(iterator, t);
    }
    
    public static <T> T getLast(final Iterator<T> iterator) {
        T next;
        do {
            next = iterator.next();
        } while (iterator.hasNext());
        return next;
    }
    
    @Nullable
    public static <T> T getLast(final Iterator<? extends T> iterator, @Nullable T last) {
        if (iterator.hasNext()) {
            last = getLast((Iterator<T>)iterator);
        }
        return last;
    }
    
    @Nullable
    public static <T> T getNext(final Iterator<? extends T> iterator, @Nullable T next) {
        if (iterator.hasNext()) {
            next = iterator.next();
        }
        return next;
    }
    
    public static <T> T getOnlyElement(final Iterator<T> iterator) {
        int n = 0;
        final T next = iterator.next();
        if (iterator.hasNext()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("expected one element but was: <" + next);
            while (n < 4 && iterator.hasNext()) {
                sb.append(", " + iterator.next());
                ++n;
            }
            if (iterator.hasNext()) {
                sb.append(", ...");
            }
            sb.append('>');
            throw new IllegalArgumentException(sb.toString());
        }
        return next;
    }
    
    @Nullable
    public static <T> T getOnlyElement(final Iterator<? extends T> iterator, @Nullable T onlyElement) {
        if (iterator.hasNext()) {
            onlyElement = getOnlyElement((Iterator<T>)iterator);
        }
        return onlyElement;
    }
    
    public static <T> int indexOf(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        int n = 0;
        Preconditions.checkNotNull(predicate, (Object)"predicate");
        while (iterator.hasNext()) {
            if (predicate.apply(iterator.next())) {
                return n;
            }
            ++n;
        }
        return -1;
    }
    
    public static <T> Iterator<T> limit(final Iterator<T> iterator, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(iterator);
        if (n >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b, (Object)"limit is negative");
        return new Iterator<T>() {
            private int count;
            
            @Override
            public boolean hasNext() {
                boolean b = false;
                if (this.count < n && iterator.hasNext()) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public T next() {
                if (this.hasNext()) {
                    ++this.count;
                    return iterator.next();
                }
                throw new NoSuchElementException();
            }
            
            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }
    
    @Beta
    public static <T> UnmodifiableIterator<T> mergeSorted(final Iterable<? extends Iterator<? extends T>> iterable, final Comparator<? super T> comparator) {
        Preconditions.checkNotNull(iterable, (Object)"iterators");
        Preconditions.checkNotNull(comparator, (Object)"comparator");
        return new MergingIterator<T>(iterable, comparator);
    }
    
    public static <T> UnmodifiableIterator<List<T>> paddedPartition(final Iterator<T> iterator, final int n) {
        return partitionImpl(iterator, n, true);
    }
    
    public static <T> UnmodifiableIterator<List<T>> partition(final Iterator<T> iterator, final int n) {
        return partitionImpl(iterator, n, false);
    }
    
    private static <T> UnmodifiableIterator<List<T>> partitionImpl(final Iterator<T> iterator, final int n, final boolean b) {
        boolean b2 = false;
        Preconditions.checkNotNull(iterator);
        if (n > 0) {
            b2 = true;
        }
        Preconditions.checkArgument(b2);
        return new UnmodifiableIterator<List<T>>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            
            @Override
            public List<T> next() {
                if (this.hasNext()) {
                    final Object[] a = new Object[n];
                    int n;
                    for (n = 0; n < n && iterator.hasNext(); ++n) {
                        a[n] = iterator.next();
                    }
                    for (int i = n; i < n; ++i) {
                        a[i] = null;
                    }
                    List<Object> list = Collections.unmodifiableList((List<?>)Arrays.asList((T[])a));
                    if (!b && n != n) {
                        list = list.subList(0, n);
                    }
                    return (List<T>)list;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    @Deprecated
    public static <T> PeekingIterator<T> peekingIterator(final PeekingIterator<T> peekingIterator) {
        return Preconditions.checkNotNull(peekingIterator);
    }
    
    public static <T> PeekingIterator<T> peekingIterator(final Iterator<? extends T> iterator) {
        if (!(iterator instanceof PeekingImpl)) {
            return new PeekingImpl<T>(iterator);
        }
        return (PeekingImpl<T>)iterator;
    }
    
    @Nullable
    static <T> T pollNext(final Iterator<T> iterator) {
        if (!iterator.hasNext()) {
            return null;
        }
        final T next = iterator.next();
        iterator.remove();
        return next;
    }
    
    public static boolean removeAll(final Iterator<?> iterator, final Collection<?> collection) {
        return removeIf(iterator, Predicates.in(collection));
    }
    
    public static <T> boolean removeIf(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        boolean b = false;
        Preconditions.checkNotNull(predicate);
        while (iterator.hasNext()) {
            if (predicate.apply(iterator.next())) {
                iterator.remove();
                b = true;
            }
        }
        return b;
    }
    
    public static boolean retainAll(final Iterator<?> iterator, final Collection<?> collection) {
        return removeIf(iterator, Predicates.not(Predicates.in((Collection<? extends T>)collection)));
    }
    
    public static <T> UnmodifiableIterator<T> singletonIterator(@Nullable final T t) {
        return new UnmodifiableIterator<T>() {
            boolean done;
            
            @Override
            public boolean hasNext() {
                boolean b = false;
                if (!this.done) {
                    b = true;
                }
                return b;
            }
            
            @Override
            public T next() {
                if (!this.done) {
                    this.done = true;
                    return t;
                }
                throw new NoSuchElementException();
            }
        };
    }
    
    public static int size(final Iterator<?> iterator) {
        int n = 0;
        while (iterator.hasNext()) {
            iterator.next();
            ++n;
        }
        return n;
    }
    
    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] toArray(final Iterator<? extends T> iterator, final Class<T> clazz) {
        return Iterables.toArray((Iterable<? extends T>)Lists.newArrayList((Iterator<?>)iterator), clazz);
    }
    
    public static String toString(final Iterator<?> iterator) {
        return Collections2.STANDARD_JOINER.appendTo(new StringBuilder().append('['), iterator).append(']').toString();
    }
    
    public static <F, T> Iterator<T> transform(final Iterator<F> iterator, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(function);
        return new TransformedIterator<F, T>(iterator) {
            @Override
            T transform(final F n) {
                return function.apply(n);
            }
        };
    }
    
    public static <T> Optional<T> tryFind(final Iterator<T> iterator, final Predicate<? super T> predicate) {
        final UnmodifiableIterator<T> filter = filter(iterator, predicate);
        Optional<Object> optional;
        if (!filter.hasNext()) {
            optional = Optional.absent();
        }
        else {
            optional = Optional.of(filter.next());
        }
        return (Optional<T>)optional;
    }
    
    @Deprecated
    public static <T> UnmodifiableIterator<T> unmodifiableIterator(final UnmodifiableIterator<T> unmodifiableIterator) {
        return Preconditions.checkNotNull(unmodifiableIterator);
    }
    
    public static <T> UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
        Preconditions.checkNotNull(iterator);
        if (!(iterator instanceof UnmodifiableIterator)) {
            return new UnmodifiableIterator<T>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }
                
                @Override
                public T next() {
                    return iterator.next();
                }
            };
        }
        return (UnmodifiableIterator<T>)iterator;
    }
    
    private static class MergingIterator<T> extends UnmodifiableIterator<T>
    {
        final Queue<PeekingIterator<T>> queue;
        
        public MergingIterator(final Iterable<? extends Iterator<? extends T>> iterable, final Comparator<? super T> comparator) {
            this.queue = new PriorityQueue<PeekingIterator<T>>(2, new Comparator<PeekingIterator<T>>() {
                @Override
                public int compare(final PeekingIterator<T> peekingIterator, final PeekingIterator<T> peekingIterator2) {
                    return comparator.compare(peekingIterator.peek(), peekingIterator2.peek());
                }
            });
            for (final Iterator iterator2 : iterable) {
                if (iterator2.hasNext()) {
                    this.queue.add((PeekingIterator<T>)Iterators.peekingIterator((Iterator<?>)iterator2));
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (!this.queue.isEmpty()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public T next() {
            final PeekingIterator peekingIterator = this.queue.remove();
            final Object next = peekingIterator.next();
            if (peekingIterator.hasNext()) {
                this.queue.add(peekingIterator);
            }
            return (T)next;
        }
    }
    
    private static class PeekingImpl<E> implements PeekingIterator<E>
    {
        private boolean hasPeeked;
        private final Iterator<? extends E> iterator;
        private E peekedElement;
        
        public PeekingImpl(final Iterator<? extends E> iterator) {
            this.iterator = Preconditions.checkNotNull(iterator);
        }
        
        @Override
        public boolean hasNext() {
            boolean b = false;
            if (this.hasPeeked || this.iterator.hasNext()) {
                b = true;
            }
            return b;
        }
        
        @Override
        public E next() {
            if (this.hasPeeked) {
                final E peekedElement = this.peekedElement;
                this.hasPeeked = false;
                this.peekedElement = null;
                return peekedElement;
            }
            return (E)this.iterator.next();
        }
        
        @Override
        public E peek() {
            if (!this.hasPeeked) {
                this.peekedElement = (E)this.iterator.next();
                this.hasPeeked = true;
            }
            return this.peekedElement;
        }
        
        @Override
        public void remove() {
            boolean b = false;
            if (!this.hasPeeked) {
                b = true;
            }
            Preconditions.checkState(b, (Object)"Can't remove after you've peeked at next");
            this.iterator.remove();
        }
    }
}
