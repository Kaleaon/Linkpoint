// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Optional;
import java.util.RandomAccess;
import com.google.common.annotations.Beta;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.Set;
import com.google.common.annotations.GwtIncompatible;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.util.Queue;
import com.google.common.base.Predicate;
import com.google.common.base.Preconditions;
import java.util.Collection;
import com.google.common.base.Function;
import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public final class Iterables
{
    private Iterables() {
    }
    
    public static <T> boolean addAll(final Collection<T> collection, final Iterable<? extends T> iterable) {
        if (!(iterable instanceof Collection)) {
            return Iterators.addAll(collection, Preconditions.checkNotNull(iterable).iterator());
        }
        return collection.addAll(Collections2.cast(iterable));
    }
    
    public static <T> boolean all(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return Iterators.all(iterable.iterator(), predicate);
    }
    
    public static <T> boolean any(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return Iterators.any(iterable.iterator(), predicate);
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends Iterable<? extends T>> iterable) {
        Preconditions.checkNotNull(iterable);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.concat((Iterator<? extends Iterator<? extends T>>)iterators((Iterable<? extends Iterable<?>>)iterable));
            }
        };
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2) {
        return concat((Iterable<? extends Iterable<? extends T>>)ImmutableList.of(iterable, iterable2));
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2, final Iterable<? extends T> iterable3) {
        return concat((Iterable<? extends Iterable<? extends T>>)ImmutableList.of(iterable, iterable2, iterable3));
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends T> iterable, final Iterable<? extends T> iterable2, final Iterable<? extends T> iterable3, final Iterable<? extends T> iterable4) {
        return concat((Iterable<? extends Iterable<? extends T>>)ImmutableList.of(iterable, iterable2, iterable3, iterable4));
    }
    
    public static <T> Iterable<T> concat(final Iterable<? extends T>... array) {
        return concat((Iterable<? extends Iterable<? extends T>>)ImmutableList.copyOf(array));
    }
    
    public static <T> Iterable<T> consumingIterable(final Iterable<T> iterable) {
        if (!(iterable instanceof Queue)) {
            Preconditions.checkNotNull(iterable);
            return new FluentIterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return Iterators.consumingIterator(iterable.iterator());
                }
                
                @Override
                public String toString() {
                    return "Iterables.consumingIterable(...)";
                }
            };
        }
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new ConsumingQueueIterator<T>((Queue<T>)iterable);
            }
            
            @Override
            public String toString() {
                return "Iterables.consumingIterable(...)";
            }
        };
    }
    
    public static boolean contains(final Iterable<?> iterable, @Nullable final Object o) {
        if (!(iterable instanceof Collection)) {
            return Iterators.contains(iterable.iterator(), o);
        }
        return Collections2.safeContains((Collection<?>)iterable, o);
    }
    
    public static <T> Iterable<T> cycle(final Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.cycle(iterable);
            }
            
            @Override
            public String toString() {
                return iterable.toString() + " (cycled)";
            }
        };
    }
    
    public static <T> Iterable<T> cycle(final T... array) {
        return cycle(Lists.newArrayList(array));
    }
    
    @CheckReturnValue
    public static boolean elementsEqual(final Iterable<?> iterable, final Iterable<?> iterable2) {
        return (!(iterable instanceof Collection) || !(iterable2 instanceof Collection) || ((Collection)iterable).size() == ((Collection)iterable2).size()) && Iterators.elementsEqual(iterable.iterator(), iterable2.iterator());
    }
    
    @CheckReturnValue
    public static <T> Iterable<T> filter(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(predicate);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return (Iterator<T>)Iterators.filter(iterable.iterator(), predicate);
            }
        };
    }
    
    @CheckReturnValue
    @GwtIncompatible("Class.isInstance")
    public static <T> Iterable<T> filter(final Iterable<?> iterable, final Class<T> clazz) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(clazz);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return (Iterator<T>)Iterators.filter(iterable.iterator(), (Class<Object>)clazz);
            }
        };
    }
    
    public static <T> T find(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return Iterators.find(iterable.iterator(), predicate);
    }
    
    @Nullable
    public static <T> T find(final Iterable<? extends T> iterable, final Predicate<? super T> predicate, @Nullable final T t) {
        return Iterators.find(iterable.iterator(), predicate, t);
    }
    
    public static int frequency(final Iterable<?> iterable, @Nullable final Object o) {
        int n = 0;
        if (iterable instanceof Multiset) {
            return ((Multiset)iterable).count(o);
        }
        if (!(iterable instanceof Set)) {
            return Iterators.frequency(iterable.iterator(), o);
        }
        if (((Set)iterable).contains(o)) {
            n = 1;
        }
        return n;
    }
    
    public static <T> T get(final Iterable<T> iterable, final int n) {
        Preconditions.checkNotNull(iterable);
        T t;
        if (!(iterable instanceof List)) {
            t = Iterators.get(iterable.iterator(), n);
        }
        else {
            t = ((List<T>)iterable).get(n);
        }
        return t;
    }
    
    @Nullable
    public static <T> T get(final Iterable<? extends T> iterable, final int n, @Nullable T value) {
        Preconditions.checkNotNull(iterable);
        Iterators.checkNonnegative(n);
        if (!(iterable instanceof List)) {
            final Iterator<Object> iterator = (Iterator<Object>)iterable.iterator();
            Iterators.advance(iterator, n);
            return Iterators.getNext((Iterator<? extends T>)iterator, value);
        }
        final List<Object> cast = (List<Object>)Lists.cast((Iterable<T>)iterable);
        if (n < cast.size()) {
            value = (T)cast.get(n);
        }
        return value;
    }
    
    @Nullable
    public static <T> T getFirst(final Iterable<? extends T> iterable, @Nullable final T t) {
        return Iterators.getNext(iterable.iterator(), t);
    }
    
    public static <T> T getLast(final Iterable<T> iterable) {
        if (!(iterable instanceof List)) {
            return Iterators.getLast((Iterator<T>)iterable.iterator());
        }
        final List list = (List)iterable;
        if (!list.isEmpty()) {
            return getLastInNonemptyList((List<T>)list);
        }
        throw new NoSuchElementException();
    }
    
    @Nullable
    public static <T> T getLast(final Iterable<? extends T> iterable, @Nullable final T t) {
        if (iterable instanceof Collection) {
            if (Collections2.cast((Iterable<Object>)iterable).isEmpty()) {
                return t;
            }
            if (iterable instanceof List) {
                return getLastInNonemptyList((List<T>)Lists.cast((Iterable<T>)iterable));
            }
        }
        return Iterators.getLast((Iterator<? extends T>)iterable.iterator(), t);
    }
    
    private static <T> T getLastInNonemptyList(final List<T> list) {
        return list.get(list.size() - 1);
    }
    
    public static <T> T getOnlyElement(final Iterable<T> iterable) {
        return Iterators.getOnlyElement(iterable.iterator());
    }
    
    @Nullable
    public static <T> T getOnlyElement(final Iterable<? extends T> iterable, @Nullable final T t) {
        return Iterators.getOnlyElement(iterable.iterator(), t);
    }
    
    public static <T> int indexOf(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return Iterators.indexOf(iterable.iterator(), predicate);
    }
    
    public static boolean isEmpty(final Iterable<?> iterable) {
        boolean b = false;
        if (!(iterable instanceof Collection)) {
            if (!iterable.iterator().hasNext()) {
                b = true;
            }
            return b;
        }
        return ((Collection)iterable).isEmpty();
    }
    
    private static <T> Iterator<Iterator<? extends T>> iterators(final Iterable<? extends Iterable<? extends T>> iterable) {
        return new TransformedIterator<Iterable<? extends T>, Iterator<? extends T>>(iterable.iterator()) {
            @Override
            Iterator<? extends T> transform(final Iterable<? extends T> iterable) {
                return iterable.iterator();
            }
        };
    }
    
    public static <T> Iterable<T> limit(final Iterable<T> iterable, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(iterable);
        if (n >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b, (Object)"limit is negative");
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.limit(iterable.iterator(), n);
            }
        };
    }
    
    @Beta
    public static <T> Iterable<T> mergeSorted(final Iterable<? extends Iterable<? extends T>> iterable, final Comparator<? super T> comparator) {
        Preconditions.checkNotNull(iterable, (Object)"iterables");
        Preconditions.checkNotNull(comparator, (Object)"comparator");
        return new UnmodifiableIterable<T>((Iterable)new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return (Iterator<T>)Iterators.mergeSorted(Iterables.transform((Iterable<Object>)iterable, (Function<? super Object, ? extends Iterator<?>>)toIterator()), (Comparator<? super Object>)comparator);
            }
        });
    }
    
    public static <T> Iterable<List<T>> paddedPartition(final Iterable<T> iterable, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(iterable);
        if (n > 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        return new FluentIterable<List<T>>() {
            @Override
            public Iterator<List<T>> iterator() {
                return (Iterator<List<T>>)Iterators.paddedPartition(iterable.iterator(), n);
            }
        };
    }
    
    public static <T> Iterable<List<T>> partition(final Iterable<T> iterable, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(iterable);
        if (n > 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        return new FluentIterable<List<T>>() {
            @Override
            public Iterator<List<T>> iterator() {
                return (Iterator<List<T>>)Iterators.partition(iterable.iterator(), n);
            }
        };
    }
    
    public static boolean removeAll(final Iterable<?> iterable, final Collection<?> collection) {
        boolean b;
        if (!(iterable instanceof Collection)) {
            b = Iterators.removeAll(iterable.iterator(), collection);
        }
        else {
            b = ((Collection)iterable).removeAll(Preconditions.checkNotNull(collection));
        }
        return b;
    }
    
    @Nullable
    static <T> T removeFirstMatching(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(predicate);
        final Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            final T next = iterator.next();
            if (predicate.apply(next)) {
                iterator.remove();
                return next;
            }
        }
        return null;
    }
    
    public static <T> boolean removeIf(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        if (iterable instanceof RandomAccess && iterable instanceof List) {
            return removeIfFromRandomAccessList((List<Object>)iterable, Preconditions.checkNotNull(predicate));
        }
        return Iterators.removeIf((Iterator<T>)iterable.iterator(), predicate);
    }
    
    private static <T> boolean removeIfFromRandomAccessList(final List<T> list, final Predicate<? super T> predicate) {
        boolean b = false;
        int n = 0;
        int i;
        for (i = 0; i < list.size(); ++i) {
            final Object value = list.get(i);
            if (!predicate.apply((T)value)) {
                Label_0077: {
                    if (i > n) {
                        try {
                            list.set(n, (T)value);
                            break Label_0077;
                        }
                        catch (final UnsupportedOperationException ex) {
                            slowRemoveIfForRemainingElements(list, predicate, n, i);
                            return true;
                        }
                        b = true;
                        return b;
                    }
                }
                ++n;
            }
        }
        list.subList(n, list.size()).clear();
        if (i == n) {
            return b;
        }
        return true;
    }
    
    public static boolean retainAll(final Iterable<?> iterable, final Collection<?> collection) {
        boolean b;
        if (!(iterable instanceof Collection)) {
            b = Iterators.retainAll(iterable.iterator(), collection);
        }
        else {
            b = ((Collection)iterable).retainAll(Preconditions.checkNotNull(collection));
        }
        return b;
    }
    
    public static int size(final Iterable<?> iterable) {
        int n;
        if (!(iterable instanceof Collection)) {
            n = Iterators.size(iterable.iterator());
        }
        else {
            n = ((Collection)iterable).size();
        }
        return n;
    }
    
    public static <T> Iterable<T> skip(final Iterable<T> iterable, final int n) {
        boolean b = false;
        Preconditions.checkNotNull(iterable);
        if (n >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b, (Object)"number to skip cannot be negative");
        if (!(iterable instanceof List)) {
            return new FluentIterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    final Iterator iterator = iterable.iterator();
                    Iterators.advance(iterator, n);
                    return new Iterator<T>() {
                        boolean atStart = true;
                        
                        @Override
                        public boolean hasNext() {
                            return iterator.hasNext();
                        }
                        
                        @Override
                        public T next() {
                            final T next = iterator.next();
                            this.atStart = false;
                            return next;
                        }
                        
                        @Override
                        public void remove() {
                            boolean b = false;
                            if (!this.atStart) {
                                b = true;
                            }
                            CollectPreconditions.checkRemove(b);
                            iterator.remove();
                        }
                    };
                }
            };
        }
        return new FluentIterable<T>() {
            final /* synthetic */ List val$list = (List)iterable;
            
            @Override
            public Iterator<T> iterator() {
                return this.val$list.subList(Math.min(this.val$list.size(), n), this.val$list.size()).iterator();
            }
        };
    }
    
    private static <T> void slowRemoveIfForRemainingElements(final List<T> list, final Predicate<? super T> predicate, final int n, int i) {
        int size = list.size();
        while (true) {
            final int n2 = size - 1;
            if (n2 <= i) {
                break;
            }
            size = n2;
            if (!predicate.apply((T)list.get(n2))) {
                continue;
            }
            list.remove(n2);
            size = n2;
        }
        --i;
        while (i >= n) {
            list.remove(i);
            --i;
        }
    }
    
    static Object[] toArray(final Iterable<?> iterable) {
        return toCollection(iterable).toArray();
    }
    
    @GwtIncompatible("Array.newInstance(Class, int)")
    public static <T> T[] toArray(final Iterable<? extends T> iterable, final Class<T> clazz) {
        final Collection<Object> collection = toCollection((Iterable<Object>)iterable);
        return collection.toArray(ObjectArrays.newArray(clazz, collection.size()));
    }
    
    static <T> T[] toArray(final Iterable<? extends T> iterable, final T[] array) {
        return toCollection((Iterable<Object>)iterable).toArray(array);
    }
    
    private static <E> Collection<E> toCollection(final Iterable<E> iterable) {
        Collection<Object> arrayList;
        if (!(iterable instanceof Collection)) {
            arrayList = Lists.newArrayList((Iterator<?>)iterable.iterator());
        }
        else {
            arrayList = (Collection)iterable;
        }
        return (Collection<E>)arrayList;
    }
    
    private static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator() {
        return new Function<Iterable<? extends T>, Iterator<? extends T>>() {
            @Override
            public Iterator<? extends T> apply(final Iterable<? extends T> iterable) {
                return iterable.iterator();
            }
        };
    }
    
    public static String toString(final Iterable<?> iterable) {
        return Iterators.toString(iterable.iterator());
    }
    
    @CheckReturnValue
    public static <F, T> Iterable<T> transform(final Iterable<F> iterable, final Function<? super F, ? extends T> function) {
        Preconditions.checkNotNull(iterable);
        Preconditions.checkNotNull(function);
        return new FluentIterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return Iterators.transform(iterable.iterator(), (Function<? super Object, ? extends T>)function);
            }
        };
    }
    
    public static <T> Optional<T> tryFind(final Iterable<T> iterable, final Predicate<? super T> predicate) {
        return Iterators.tryFind(iterable.iterator(), predicate);
    }
    
    @Deprecated
    public static <E> Iterable<E> unmodifiableIterable(final ImmutableCollection<E> collection) {
        return Preconditions.checkNotNull(collection);
    }
    
    public static <T> Iterable<T> unmodifiableIterable(final Iterable<T> iterable) {
        Preconditions.checkNotNull(iterable);
        if (!(iterable instanceof UnmodifiableIterable) && !(iterable instanceof ImmutableCollection)) {
            return new UnmodifiableIterable<T>((Iterable)iterable);
        }
        return iterable;
    }
    
    private static final class UnmodifiableIterable<T> extends FluentIterable<T>
    {
        private final Iterable<T> iterable;
        
        private UnmodifiableIterable(final Iterable<T> iterable) {
            this.iterable = iterable;
        }
        
        @Override
        public Iterator<T> iterator() {
            return Iterators.unmodifiableIterator(this.iterable.iterator());
        }
        
        @Override
        public String toString() {
            return this.iterable.toString();
        }
    }
}
