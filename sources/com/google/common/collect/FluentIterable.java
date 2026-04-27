package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public abstract class FluentIterable<E> implements Iterable<E> {
    private final Iterable<E> iterable;

    private static class FromIterableFunction<E> implements Function<Iterable<E>, FluentIterable<E>> {
        private FromIterableFunction() {
        }

        public FluentIterable<E> apply(Iterable<E> iterable) {
            return FluentIterable.from(iterable);
        }
    }

    protected FluentIterable() {
        this.iterable = this;
    }

    FluentIterable(Iterable<E> iterable2) {
        this.iterable = (Iterable) Preconditions.checkNotNull(iterable2);
    }

    @CheckReturnValue
    @Deprecated
    public static <E> FluentIterable<E> from(FluentIterable<E> fluentIterable) {
        return (FluentIterable) Preconditions.checkNotNull(fluentIterable);
    }

    @CheckReturnValue
    public static <E> FluentIterable<E> from(final Iterable<E> iterable2) {
        return !(iterable2 instanceof FluentIterable) ? new FluentIterable<E>(iterable2) {
            public Iterator<E> iterator() {
                return iterable2.iterator();
            }
        } : (FluentIterable) iterable2;
    }

    @CheckReturnValue
    @Beta
    public static <E> FluentIterable<E> of(E[] eArr) {
        return from(Lists.newArrayList(eArr));
    }

    @CheckReturnValue
    public final boolean allMatch(Predicate<? super E> predicate) {
        return Iterables.all(this.iterable, predicate);
    }

    @CheckReturnValue
    public final boolean anyMatch(Predicate<? super E> predicate) {
        return Iterables.any(this.iterable, predicate);
    }

    @CheckReturnValue
    @Beta
    public final FluentIterable<E> append(Iterable<? extends E> iterable2) {
        return from(Iterables.concat(this.iterable, iterable2));
    }

    @CheckReturnValue
    @Beta
    public final FluentIterable<E> append(E... eArr) {
        return from(Iterables.concat(this.iterable, Arrays.asList(eArr)));
    }

    @CheckReturnValue
    public final boolean contains(@Nullable Object obj) {
        return Iterables.contains(this.iterable, obj);
    }

    public final <C extends Collection<? super E>> C copyInto(C c) {
        Preconditions.checkNotNull(c);
        if (!(this.iterable instanceof Collection)) {
            for (E add : this.iterable) {
                c.add(add);
            }
        } else {
            c.addAll(Collections2.cast(this.iterable));
        }
        return c;
    }

    @CheckReturnValue
    public final FluentIterable<E> cycle() {
        return from(Iterables.cycle(this.iterable));
    }

    @CheckReturnValue
    public final FluentIterable<E> filter(Predicate<? super E> predicate) {
        return from(Iterables.filter(this.iterable, predicate));
    }

    @CheckReturnValue
    @GwtIncompatible("Class.isInstance")
    public final <T> FluentIterable<T> filter(Class<T> cls) {
        return from(Iterables.filter((Iterable<?>) this.iterable, cls));
    }

    @CheckReturnValue
    public final Optional<E> first() {
        Iterator<E> it = this.iterable.iterator();
        return !it.hasNext() ? Optional.absent() : Optional.of(it.next());
    }

    @CheckReturnValue
    public final Optional<E> firstMatch(Predicate<? super E> predicate) {
        return Iterables.tryFind(this.iterable, predicate);
    }

    @CheckReturnValue
    public final E get(int i) {
        return Iterables.get(this.iterable, i);
    }

    @CheckReturnValue
    public final <K> ImmutableListMultimap<K, E> index(Function<? super E, K> function) {
        return Multimaps.index(this.iterable, function);
    }

    @CheckReturnValue
    public final boolean isEmpty() {
        return !this.iterable.iterator().hasNext();
    }

    @CheckReturnValue
    @Beta
    public final String join(Joiner joiner) {
        return joiner.join((Iterable<?>) this);
    }

    @CheckReturnValue
    public final Optional<E> last() {
        E next;
        if (!(this.iterable instanceof List)) {
            Iterator<E> it = this.iterable.iterator();
            if (!it.hasNext()) {
                return Optional.absent();
            }
            if (this.iterable instanceof SortedSet) {
                return Optional.of(((SortedSet) this.iterable).last());
            }
            do {
                next = it.next();
            } while (it.hasNext());
            return Optional.of(next);
        }
        List list = (List) this.iterable;
        return !list.isEmpty() ? Optional.of(list.get(list.size() - 1)) : Optional.absent();
    }

    @CheckReturnValue
    public final FluentIterable<E> limit(int i) {
        return from(Iterables.limit(this.iterable, i));
    }

    @CheckReturnValue
    public final int size() {
        return Iterables.size(this.iterable);
    }

    @CheckReturnValue
    public final FluentIterable<E> skip(int i) {
        return from(Iterables.skip(this.iterable, i));
    }

    @CheckReturnValue
    @GwtIncompatible("Array.newArray(Class, int)")
    public final E[] toArray(Class<E> cls) {
        return Iterables.toArray(this.iterable, cls);
    }

    @CheckReturnValue
    public final ImmutableList<E> toList() {
        return ImmutableList.copyOf(this.iterable);
    }

    @CheckReturnValue
    public final <V> ImmutableMap<E, V> toMap(Function<? super E, V> function) {
        return Maps.toMap(this.iterable, function);
    }

    @CheckReturnValue
    public final ImmutableMultiset<E> toMultiset() {
        return ImmutableMultiset.copyOf(this.iterable);
    }

    @CheckReturnValue
    public final ImmutableSet<E> toSet() {
        return ImmutableSet.copyOf(this.iterable);
    }

    @CheckReturnValue
    public final ImmutableList<E> toSortedList(Comparator<? super E> comparator) {
        return Ordering.from(comparator).immutableSortedCopy(this.iterable);
    }

    @CheckReturnValue
    public final ImmutableSortedSet<E> toSortedSet(Comparator<? super E> comparator) {
        return ImmutableSortedSet.copyOf(comparator, this.iterable);
    }

    @CheckReturnValue
    public String toString() {
        return Iterables.toString(this.iterable);
    }

    @CheckReturnValue
    public final <T> FluentIterable<T> transform(Function<? super E, T> function) {
        return from(Iterables.transform(this.iterable, function));
    }

    @CheckReturnValue
    public <T> FluentIterable<T> transformAndConcat(Function<? super E, ? extends Iterable<? extends T>> function) {
        return from(Iterables.concat(transform(function)));
    }

    @CheckReturnValue
    public final <K> ImmutableMap<K, E> uniqueIndex(Function<? super E, K> function) {
        return Maps.uniqueIndex(this.iterable, function);
    }
}
