package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.SortedLists;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
@GwtCompatible(emulated = true, serializable = true)
/* loaded from: classes.dex */
public final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E> {
    private final transient ImmutableList<E> elements;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegularImmutableSortedSet(ImmutableList<E> immutableList, Comparator<? super E> comparator) {
        super(comparator);
        this.elements = immutableList;
    }

    private int unsafeBinarySearch(Object obj) throws ClassCastException {
        return Collections.binarySearch(this.elements, obj, unsafeComparator());
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.NavigableSet
    public E ceiling(E e) {
        int tailIndex = tailIndex(e, true);
        if (tailIndex != size()) {
            return this.elements.get(tailIndex);
        }
        return null;
    }

    @Override // com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean contains(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            return unsafeBinarySearch(obj) >= 0;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean containsAll(Collection<?> collection) {
        if (collection instanceof Multiset) {
            collection = ((Multiset) collection).elementSet();
        }
        if (SortedIterables.hasSameComparator(comparator(), collection) && collection.size() > 1) {
            PeekingIterator peekingIterator = Iterators.peekingIterator(iterator());
            Iterator<?> it = collection.iterator();
            Object next = it.next();
            while (peekingIterator.hasNext()) {
                try {
                    int unsafeCompare = unsafeCompare(peekingIterator.peek(), next);
                    if (unsafeCompare < 0) {
                        peekingIterator.next();
                    } else if (unsafeCompare != 0) {
                        if (unsafeCompare > 0) {
                            return false;
                        }
                    } else if (!it.hasNext()) {
                        return true;
                    } else {
                        next = it.next();
                    }
                } catch (ClassCastException e) {
                    return false;
                } catch (NullPointerException e2) {
                    return false;
                }
            }
            return false;
        }
        return super.containsAll(collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableCollection
    public int copyIntoArray(Object[] objArr, int i) {
        return this.elements.copyIntoArray(objArr, i);
    }

    @Override // com.google.common.collect.ImmutableCollection
    ImmutableList<E> createAsList() {
        return size() > 1 ? new ImmutableSortedAsList(this, this.elements) : this.elements;
    }

    @Override // com.google.common.collect.ImmutableSortedSet
    ImmutableSortedSet<E> createDescendingSet() {
        Ordering reverse = Ordering.from(this.comparator).reverse();
        return !isEmpty() ? new RegularImmutableSortedSet(this.elements.reverse(), reverse) : emptySet(reverse);
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.NavigableSet
    @GwtIncompatible("NavigableSet")
    public UnmodifiableIterator<E> descendingIterator() {
        return this.elements.reverse().iterator();
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x003a A[Catch: ClassCastException -> 0x004c, NoSuchElementException -> 0x004e, TryCatch #2 {ClassCastException -> 0x004c, NoSuchElementException -> 0x004e, blocks: (B:19:0x002f, B:20:0x0033, B:23:0x003a, B:26:0x0045), top: B:33:0x002f }] */
    @Override // com.google.common.collect.ImmutableSet, java.util.Collection, java.util.Set
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean equals(@javax.annotation.Nullable java.lang.Object r7) {
        /*
            r6 = this;
            r5 = 1
            r4 = 0
            if (r7 == r6) goto L27
            boolean r0 = r7 instanceof java.util.Set
            if (r0 == 0) goto L28
            java.util.Set r7 = (java.util.Set) r7
            int r0 = r6.size()
            int r1 = r7.size()
            if (r0 != r1) goto L29
            boolean r0 = r6.isEmpty()
            if (r0 != 0) goto L2a
            java.util.Comparator<? super E> r0 = r6.comparator
            boolean r0 = com.google.common.collect.SortedIterables.hasSameComparator(r0, r7)
            if (r0 != 0) goto L2b
            boolean r0 = r6.containsAll(r7)
            return r0
        L27:
            return r5
        L28:
            return r4
        L29:
            return r4
        L2a:
            return r5
        L2b:
            java.util.Iterator r0 = r7.iterator()
            com.google.common.collect.UnmodifiableIterator r1 = r6.iterator()     // Catch: java.lang.ClassCastException -> L4c java.util.NoSuchElementException -> L4e
        L33:
            boolean r2 = r1.hasNext()     // Catch: java.lang.ClassCastException -> L4c java.util.NoSuchElementException -> L4e
            if (r2 != 0) goto L3a
            return r5
        L3a:
            java.lang.Object r2 = r1.next()     // Catch: java.lang.ClassCastException -> L4c java.util.NoSuchElementException -> L4e
            java.lang.Object r3 = r0.next()     // Catch: java.lang.ClassCastException -> L4c java.util.NoSuchElementException -> L4e
            if (r3 != 0) goto L45
        L44:
            return r4
        L45:
            int r2 = r6.unsafeCompare(r2, r3)     // Catch: java.lang.ClassCastException -> L4c java.util.NoSuchElementException -> L4e
            if (r2 != 0) goto L44
            goto L33
        L4c:
            r0 = move-exception
            return r4
        L4e:
            r0 = move-exception
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.RegularImmutableSortedSet.equals(java.lang.Object):boolean");
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.SortedSet
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.elements.get(0);
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.NavigableSet
    public E floor(E e) {
        int headIndex = headIndex(e, true) - 1;
        if (headIndex != -1) {
            return this.elements.get(headIndex);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RegularImmutableSortedSet<E> getSubSet(int i, int i2) {
        return (i == 0 && i2 == size()) ? this : i >= i2 ? emptySet(this.comparator) : new RegularImmutableSortedSet<>(this.elements.subList(i, i2), this.comparator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int headIndex(E e, boolean z) {
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(e), comparator(), !z ? SortedLists.KeyPresentBehavior.FIRST_PRESENT : SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableSortedSet
    public ImmutableSortedSet<E> headSetImpl(E e, boolean z) {
        return getSubSet(0, headIndex(e, z));
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.NavigableSet
    public E higher(E e) {
        int tailIndex = tailIndex(e, false);
        if (tailIndex != size()) {
            return this.elements.get(tailIndex);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableSortedSet
    public int indexOf(@Nullable Object obj) {
        if (obj != null) {
            try {
                int binarySearch = SortedLists.binarySearch(this.elements, obj, unsafeComparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
                if (binarySearch < 0) {
                    return -1;
                }
                return binarySearch;
            } catch (ClassCastException e) {
                return -1;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableCollection
    public boolean isPartialView() {
        return this.elements.isPartialView();
    }

    @Override // com.google.common.collect.ImmutableSortedSet, com.google.common.collect.ImmutableSet, com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set, java.util.NavigableSet, com.google.common.collect.SortedIterable
    public UnmodifiableIterator<E> iterator() {
        return this.elements.iterator();
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.SortedSet
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.elements.get(size() - 1);
    }

    @Override // com.google.common.collect.ImmutableSortedSet, java.util.NavigableSet
    public E lower(E e) {
        int headIndex = headIndex(e, false) - 1;
        if (headIndex != -1) {
            return this.elements.get(headIndex);
        }
        return null;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.elements.size();
    }

    @Override // com.google.common.collect.ImmutableSortedSet
    ImmutableSortedSet<E> subSetImpl(E e, boolean z, E e2, boolean z2) {
        return tailSetImpl(e, z).headSetImpl(e2, z2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int tailIndex(E e, boolean z) {
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(e), comparator(), !z ? SortedLists.KeyPresentBehavior.FIRST_AFTER : SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    @Override // com.google.common.collect.ImmutableSortedSet
    ImmutableSortedSet<E> tailSetImpl(E e, boolean z) {
        return getSubSet(tailIndex(e, z), size());
    }

    Comparator<Object> unsafeComparator() {
        return this.comparator;
    }
}
