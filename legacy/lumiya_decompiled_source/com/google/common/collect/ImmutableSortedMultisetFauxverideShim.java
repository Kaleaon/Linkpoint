package com.google.common.collect;

import com.google.common.collect.ImmutableSortedMultiset;
/* loaded from: classes.dex */
abstract class ImmutableSortedMultisetFauxverideShim<E> extends ImmutableMultiset<E> {
    @Deprecated
    public static <E> ImmutableSortedMultiset.Builder<E> builder() {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> copyOf(E[] eArr) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e, E e2) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e, E e2, E e3) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e, E e2, E e3, E e4) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e, E e2, E e3, E e4, E e5) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedMultiset<E> of(E e, E e2, E e3, E e4, E e5, E e6, E... eArr) {
        throw new IllegalStateException();
    }
}
