package com.google.common.collect;

import com.google.common.collect.Multiset;
import javax.annotation.Nullable;

final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E> {
    private final transient ImmutableSortedMultiset<E> forward;

    DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> immutableSortedMultiset) {
        this.forward = immutableSortedMultiset;
    }

    public int count(@Nullable Object obj) {
        return this.forward.count(obj);
    }

    public ImmutableSortedMultiset<E> descendingMultiset() {
        return this.forward;
    }

    public ImmutableSortedSet<E> elementSet() {
        return this.forward.elementSet().descendingSet();
    }

    public Multiset.Entry<E> firstEntry() {
        return this.forward.lastEntry();
    }

    /* access modifiers changed from: package-private */
    public Multiset.Entry<E> getEntry(int i) {
        return (Multiset.Entry) this.forward.entrySet().asList().reverse().get(i);
    }

    public ImmutableSortedMultiset<E> headMultiset(E e, BoundType boundType) {
        return this.forward.tailMultiset(e, boundType).descendingMultiset();
    }

    /* access modifiers changed from: package-private */
    public boolean isPartialView() {
        return this.forward.isPartialView();
    }

    public Multiset.Entry<E> lastEntry() {
        return this.forward.firstEntry();
    }

    public int size() {
        return this.forward.size();
    }

    public ImmutableSortedMultiset<E> tailMultiset(E e, BoundType boundType) {
        return this.forward.headMultiset(e, boundType).descendingMultiset();
    }
}
