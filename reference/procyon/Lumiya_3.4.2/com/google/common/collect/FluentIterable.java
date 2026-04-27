// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.annotations.GwtIncompatible;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Arrays;
import com.google.common.base.Predicate;
import com.google.common.annotations.Beta;
import java.util.Iterator;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
public abstract class FluentIterable<E> implements Iterable<E>
{
    private final Iterable<E> iterable;
    
    protected FluentIterable() {
        this.iterable = this;
    }
    
    FluentIterable(final Iterable<E> iterable) {
        this.iterable = Preconditions.checkNotNull(iterable);
    }
    
    @Deprecated
    @CheckReturnValue
    public static <E> FluentIterable<E> from(final FluentIterable<E> fluentIterable) {
        return Preconditions.checkNotNull(fluentIterable);
    }
    
    @CheckReturnValue
    public static <E> FluentIterable<E> from(final Iterable<E> iterable) {
        FluentIterable fluentIterable;
        if (!(iterable instanceof FluentIterable)) {
            fluentIterable = new FluentIterable<E>(iterable) {
                @Override
                public Iterator<E> iterator() {
                    return iterable.iterator();
                }
            };
        }
        else {
            fluentIterable = (FluentIterable)iterable;
        }
        return fluentIterable;
    }
    
    @CheckReturnValue
    @Beta
    public static <E> FluentIterable<E> of(final E[] array) {
        return from(Lists.newArrayList(array));
    }
    
    @CheckReturnValue
    public final boolean allMatch(final Predicate<? super E> predicate) {
        return Iterables.all(this.iterable, predicate);
    }
    
    @CheckReturnValue
    public final boolean anyMatch(final Predicate<? super E> predicate) {
        return Iterables.any(this.iterable, predicate);
    }
    
    @CheckReturnValue
    @Beta
    public final FluentIterable<E> append(final Iterable<? extends E> iterable) {
        return from(Iterables.concat((Iterable<? extends E>)this.iterable, iterable));
    }
    
    @CheckReturnValue
    @Beta
    public final FluentIterable<E> append(final E... a) {
        return from(Iterables.concat((Iterable<? extends E>)this.iterable, (Iterable<? extends E>)Arrays.asList(a)));
    }
    
    @CheckReturnValue
    public final boolean contains(@Nullable final Object o) {
        return Iterables.contains(this.iterable, o);
    }
    
    public final <C extends Collection<? super E>> C copyInto(final C c) {
        Preconditions.checkNotNull(c);
        if (!(this.iterable instanceof Collection)) {
            final Iterator<E> iterator = this.iterable.iterator();
            while (iterator.hasNext()) {
                c.add(iterator.next());
            }
        }
        else {
            c.addAll(Collections2.cast((Iterable<? extends E>)this.iterable));
        }
        return c;
    }
    
    @CheckReturnValue
    public final FluentIterable<E> cycle() {
        return from((Iterable<E>)Iterables.cycle((Iterable<E>)this.iterable));
    }
    
    @CheckReturnValue
    public final FluentIterable<E> filter(final Predicate<? super E> predicate) {
        return from((Iterable<E>)Iterables.filter((Iterable<E>)this.iterable, (Predicate<? super E>)predicate));
    }
    
    @CheckReturnValue
    @GwtIncompatible("Class.isInstance")
    public final <T> FluentIterable<T> filter(final Class<T> clazz) {
        return from((Iterable<T>)Iterables.filter(this.iterable, (Class<E>)clazz));
    }
    
    @CheckReturnValue
    public final Optional<E> first() {
        final Iterator<E> iterator = this.iterable.iterator();
        Optional<Object> optional;
        if (!iterator.hasNext()) {
            optional = Optional.absent();
        }
        else {
            optional = Optional.of(iterator.next());
        }
        return (Optional<E>)optional;
    }
    
    @CheckReturnValue
    public final Optional<E> firstMatch(final Predicate<? super E> predicate) {
        return Iterables.tryFind(this.iterable, predicate);
    }
    
    @CheckReturnValue
    public final E get(final int n) {
        return Iterables.get(this.iterable, n);
    }
    
    @CheckReturnValue
    public final <K> ImmutableListMultimap<K, E> index(final Function<? super E, K> function) {
        return Multimaps.index(this.iterable, function);
    }
    
    @CheckReturnValue
    public final boolean isEmpty() {
        boolean b = false;
        if (!this.iterable.iterator().hasNext()) {
            b = true;
        }
        return b;
    }
    
    @CheckReturnValue
    @Beta
    public final String join(final Joiner joiner) {
        return joiner.join(this);
    }
    
    @CheckReturnValue
    public final Optional<E> last() {
        if (!(this.iterable instanceof List)) {
            final Iterator<E> iterator = this.iterable.iterator();
            if (!iterator.hasNext()) {
                return Optional.absent();
            }
            if (!(this.iterable instanceof SortedSet)) {
                E next;
                do {
                    next = iterator.next();
                } while (iterator.hasNext());
                return Optional.of(next);
            }
            return Optional.of(((SortedSet)this.iterable).last());
        }
        else {
            final List list = (List)this.iterable;
            if (!list.isEmpty()) {
                return Optional.of(list.get(list.size() - 1));
            }
            return Optional.absent();
        }
    }
    
    @CheckReturnValue
    public final FluentIterable<E> limit(final int n) {
        return from((Iterable<E>)Iterables.limit((Iterable<E>)this.iterable, n));
    }
    
    @CheckReturnValue
    public final int size() {
        return Iterables.size(this.iterable);
    }
    
    @CheckReturnValue
    public final FluentIterable<E> skip(final int n) {
        return from((Iterable<E>)Iterables.skip((Iterable<E>)this.iterable, n));
    }
    
    @CheckReturnValue
    @GwtIncompatible("Array.newArray(Class, int)")
    public final E[] toArray(final Class<E> clazz) {
        return Iterables.toArray((Iterable<? extends E>)this.iterable, clazz);
    }
    
    @CheckReturnValue
    public final ImmutableList<E> toList() {
        return ImmutableList.copyOf((Iterable<? extends E>)this.iterable);
    }
    
    @CheckReturnValue
    public final <V> ImmutableMap<E, V> toMap(final Function<? super E, V> function) {
        return Maps.toMap(this.iterable, function);
    }
    
    @CheckReturnValue
    public final ImmutableMultiset<E> toMultiset() {
        return ImmutableMultiset.copyOf((Iterable<? extends E>)this.iterable);
    }
    
    @CheckReturnValue
    public final ImmutableSet<E> toSet() {
        return ImmutableSet.copyOf((Iterable<? extends E>)this.iterable);
    }
    
    @CheckReturnValue
    public final ImmutableList<E> toSortedList(final Comparator<? super E> comparator) {
        return Ordering.from(comparator).immutableSortedCopy(this.iterable);
    }
    
    @CheckReturnValue
    public final ImmutableSortedSet<E> toSortedSet(final Comparator<? super E> comparator) {
        return ImmutableSortedSet.copyOf(comparator, (Iterable<? extends E>)this.iterable);
    }
    
    @CheckReturnValue
    @Override
    public String toString() {
        return Iterables.toString(this.iterable);
    }
    
    @CheckReturnValue
    public final <T> FluentIterable<T> transform(final Function<? super E, T> function) {
        return from(Iterables.transform(this.iterable, (Function<? super E, ? extends T>)function));
    }
    
    @CheckReturnValue
    public <T> FluentIterable<T> transformAndConcat(final Function<? super E, ? extends Iterable<? extends T>> function) {
        return from(Iterables.concat((Iterable<? extends Iterable<? extends T>>)this.transform((Function<? super E, Object>)function)));
    }
    
    @CheckReturnValue
    public final <K> ImmutableMap<K, E> uniqueIndex(final Function<? super E, K> function) {
        return Maps.uniqueIndex(this.iterable, function);
    }
    
    private static class FromIterableFunction<E> implements Function<Iterable<E>, FluentIterable<E>>
    {
        @Override
        public FluentIterable<E> apply(final Iterable<E> iterable) {
            return FluentIterable.from(iterable);
        }
    }
}
