package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.lang.Comparable;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
final class EmptyContiguousSet<C extends Comparable> extends ContiguousSet<C> {

    @GwtIncompatible("serialization")
    private static final class SerializedForm<C extends Comparable> implements Serializable {
        private static final long serialVersionUID = 0;
        private final DiscreteDomain<C> domain;

        private SerializedForm(DiscreteDomain<C> discreteDomain) {
            this.domain = discreteDomain;
        }

        private Object readResolve() {
            return new EmptyContiguousSet(this.domain);
        }
    }

    EmptyContiguousSet(DiscreteDomain<C> discreteDomain) {
        super(discreteDomain);
    }

    public ImmutableList<C> asList() {
        return ImmutableList.of();
    }

    public boolean contains(Object obj) {
        return false;
    }

    /* access modifiers changed from: package-private */
    @GwtIncompatible("NavigableSet")
    public ImmutableSortedSet<C> createDescendingSet() {
        return ImmutableSortedSet.emptySet(Ordering.natural().reverse());
    }

    @GwtIncompatible("NavigableSet")
    public UnmodifiableIterator<C> descendingIterator() {
        return Iterators.emptyIterator();
    }

    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Set)) {
            return false;
        }
        return ((Set) obj).isEmpty();
    }

    public C first() {
        throw new NoSuchElementException();
    }

    public int hashCode() {
        return 0;
    }

    /* access modifiers changed from: package-private */
    public ContiguousSet<C> headSetImpl(C c, boolean z) {
        return this;
    }

    /* access modifiers changed from: package-private */
    @GwtIncompatible("not used by GWT emulation")
    public int indexOf(Object obj) {
        return -1;
    }

    public ContiguousSet<C> intersection(ContiguousSet<C> contiguousSet) {
        return this;
    }

    public boolean isEmpty() {
        return true;
    }

    /* access modifiers changed from: package-private */
    @GwtIncompatible("not used in GWT")
    public boolean isHashCodeFast() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isPartialView() {
        return false;
    }

    public UnmodifiableIterator<C> iterator() {
        return Iterators.emptyIterator();
    }

    public C last() {
        throw new NoSuchElementException();
    }

    public Range<C> range() {
        throw new NoSuchElementException();
    }

    public Range<C> range(BoundType boundType, BoundType boundType2) {
        throw new NoSuchElementException();
    }

    public int size() {
        return 0;
    }

    /* access modifiers changed from: package-private */
    public ContiguousSet<C> subSetImpl(C c, boolean z, C c2, boolean z2) {
        return this;
    }

    /* access modifiers changed from: package-private */
    public ContiguousSet<C> tailSetImpl(C c, boolean z) {
        return this;
    }

    public String toString() {
        return "[]";
    }

    /* access modifiers changed from: package-private */
    @GwtIncompatible("serialization")
    public Object writeReplace() {
        return new SerializedForm(this.domain);
    }
}
