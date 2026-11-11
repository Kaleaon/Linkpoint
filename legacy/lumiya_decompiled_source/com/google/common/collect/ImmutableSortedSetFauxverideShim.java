package com.google.common.collect;

import com.google.common.collect.ImmutableSortedSet;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class ImmutableSortedSetFauxverideShim<E> extends ImmutableSet<E> {
    @Deprecated
    public static <E> ImmutableSortedSet.Builder<E> builder() {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> copyOf(E[] eArr) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e, E e2) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e, E e2, E e3) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e, E e2, E e3, E e4) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e, E e2, E e3, E e4, E e5) {
        throw new IllegalStateException();
    }

    @Deprecated
    public static <E> ImmutableSortedSet<E> of(E e, E e2, E e3, E e4, E e5, E e6, E... eArr) {
        throw new IllegalStateException();
    }
}
