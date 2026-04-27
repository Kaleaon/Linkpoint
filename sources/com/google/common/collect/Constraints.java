package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;

@GwtCompatible
final class Constraints {

    static class ConstrainedCollection<E> extends ForwardingCollection<E> {
        private final Constraint<? super E> constraint;
        private final Collection<E> delegate;

        public ConstrainedCollection(Collection<E> collection, Constraint<? super E> constraint2) {
            this.delegate = (Collection) Preconditions.checkNotNull(collection);
            this.constraint = (Constraint) Preconditions.checkNotNull(constraint2);
        }

        public boolean add(E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }

        public boolean addAll(Collection<? extends E> collection) {
            return this.delegate.addAll(Constraints.checkElements(collection, this.constraint));
        }

        /* access modifiers changed from: protected */
        public Collection<E> delegate() {
            return this.delegate;
        }
    }

    @GwtCompatible
    private static class ConstrainedList<E> extends ForwardingList<E> {
        final Constraint<? super E> constraint;
        final List<E> delegate;

        ConstrainedList(List<E> list, Constraint<? super E> constraint2) {
            this.delegate = (List) Preconditions.checkNotNull(list);
            this.constraint = (Constraint) Preconditions.checkNotNull(constraint2);
        }

        public void add(int i, E e) {
            this.constraint.checkElement(e);
            this.delegate.add(i, e);
        }

        public boolean add(E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }

        public boolean addAll(int i, Collection<? extends E> collection) {
            return this.delegate.addAll(i, Constraints.checkElements(collection, this.constraint));
        }

        public boolean addAll(Collection<? extends E> collection) {
            return this.delegate.addAll(Constraints.checkElements(collection, this.constraint));
        }

        /* access modifiers changed from: protected */
        public List<E> delegate() {
            return this.delegate;
        }

        public ListIterator<E> listIterator() {
            return Constraints.constrainedListIterator(this.delegate.listIterator(), this.constraint);
        }

        public ListIterator<E> listIterator(int i) {
            return Constraints.constrainedListIterator(this.delegate.listIterator(i), this.constraint);
        }

        public E set(int i, E e) {
            this.constraint.checkElement(e);
            return this.delegate.set(i, e);
        }

        public List<E> subList(int i, int i2) {
            return Constraints.constrainedList(this.delegate.subList(i, i2), this.constraint);
        }
    }

    static class ConstrainedListIterator<E> extends ForwardingListIterator<E> {
        private final Constraint<? super E> constraint;
        private final ListIterator<E> delegate;

        public ConstrainedListIterator(ListIterator<E> listIterator, Constraint<? super E> constraint2) {
            this.delegate = listIterator;
            this.constraint = constraint2;
        }

        public void add(E e) {
            this.constraint.checkElement(e);
            this.delegate.add(e);
        }

        /* access modifiers changed from: protected */
        public ListIterator<E> delegate() {
            return this.delegate;
        }

        public void set(E e) {
            this.constraint.checkElement(e);
            this.delegate.set(e);
        }
    }

    static class ConstrainedRandomAccessList<E> extends ConstrainedList<E> implements RandomAccess {
        ConstrainedRandomAccessList(List<E> list, Constraint<? super E> constraint) {
            super(list, constraint);
        }
    }

    static class ConstrainedSet<E> extends ForwardingSet<E> {
        private final Constraint<? super E> constraint;
        private final Set<E> delegate;

        public ConstrainedSet(Set<E> set, Constraint<? super E> constraint2) {
            this.delegate = (Set) Preconditions.checkNotNull(set);
            this.constraint = (Constraint) Preconditions.checkNotNull(constraint2);
        }

        public boolean add(E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }

        public boolean addAll(Collection<? extends E> collection) {
            return this.delegate.addAll(Constraints.checkElements(collection, this.constraint));
        }

        /* access modifiers changed from: protected */
        public Set<E> delegate() {
            return this.delegate;
        }
    }

    private static class ConstrainedSortedSet<E> extends ForwardingSortedSet<E> {
        final Constraint<? super E> constraint;
        final SortedSet<E> delegate;

        ConstrainedSortedSet(SortedSet<E> sortedSet, Constraint<? super E> constraint2) {
            this.delegate = (SortedSet) Preconditions.checkNotNull(sortedSet);
            this.constraint = (Constraint) Preconditions.checkNotNull(constraint2);
        }

        public boolean add(E e) {
            this.constraint.checkElement(e);
            return this.delegate.add(e);
        }

        public boolean addAll(Collection<? extends E> collection) {
            return this.delegate.addAll(Constraints.checkElements(collection, this.constraint));
        }

        /* access modifiers changed from: protected */
        public SortedSet<E> delegate() {
            return this.delegate;
        }

        public SortedSet<E> headSet(E e) {
            return Constraints.constrainedSortedSet(this.delegate.headSet(e), this.constraint);
        }

        public SortedSet<E> subSet(E e, E e2) {
            return Constraints.constrainedSortedSet(this.delegate.subSet(e, e2), this.constraint);
        }

        public SortedSet<E> tailSet(E e) {
            return Constraints.constrainedSortedSet(this.delegate.tailSet(e), this.constraint);
        }
    }

    private Constraints() {
    }

    /* access modifiers changed from: private */
    public static <E> Collection<E> checkElements(Collection<E> collection, Constraint<? super E> constraint) {
        ArrayList<E> newArrayList = Lists.newArrayList(collection);
        for (E checkElement : newArrayList) {
            constraint.checkElement(checkElement);
        }
        return newArrayList;
    }

    public static <E> Collection<E> constrainedCollection(Collection<E> collection, Constraint<? super E> constraint) {
        return new ConstrainedCollection(collection, constraint);
    }

    public static <E> List<E> constrainedList(List<E> list, Constraint<? super E> constraint) {
        return !(list instanceof RandomAccess) ? new ConstrainedList(list, constraint) : new ConstrainedRandomAccessList(list, constraint);
    }

    /* access modifiers changed from: private */
    public static <E> ListIterator<E> constrainedListIterator(ListIterator<E> listIterator, Constraint<? super E> constraint) {
        return new ConstrainedListIterator(listIterator, constraint);
    }

    public static <E> Set<E> constrainedSet(Set<E> set, Constraint<? super E> constraint) {
        return new ConstrainedSet(set, constraint);
    }

    public static <E> SortedSet<E> constrainedSortedSet(SortedSet<E> sortedSet, Constraint<? super E> constraint) {
        return new ConstrainedSortedSet(sortedSet, constraint);
    }

    static <E> Collection<E> constrainedTypePreservingCollection(Collection<E> collection, Constraint<E> constraint) {
        return !(collection instanceof SortedSet) ? !(collection instanceof Set) ? !(collection instanceof List) ? constrainedCollection(collection, constraint) : constrainedList((List) collection, constraint) : constrainedSet((Set) collection, constraint) : constrainedSortedSet((SortedSet) collection, constraint);
    }
}
