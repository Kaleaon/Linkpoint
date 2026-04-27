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

@GwtCompatible(emulated = true, serializable = true)
final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E> {
    private final transient ImmutableList<E> elements;

    RegularImmutableSortedSet(ImmutableList<E> immutableList, Comparator<? super E> comparator) {
        super(comparator);
        this.elements = immutableList;
    }

    private int unsafeBinarySearch(Object obj) throws ClassCastException {
        return Collections.binarySearch(this.elements, obj, unsafeComparator());
    }

    public E ceiling(E e) {
        int tailIndex = tailIndex(e, true);
        if (tailIndex != size()) {
            return this.elements.get(tailIndex);
        }
        return null;
    }

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

    public boolean containsAll(Collection<?> collection) {
        if (collection instanceof Multiset) {
            collection = ((Multiset) collection).elementSet();
        }
        if (!SortedIterables.hasSameComparator(comparator(), collection) || collection.size() <= 1) {
            return super.containsAll(collection);
        }
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
            } catch (NullPointerException e) {
                return false;
            } catch (ClassCastException e2) {
                return false;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public int copyIntoArray(Object[] objArr, int i) {
        return this.elements.copyIntoArray(objArr, i);
    }

    /* access modifiers changed from: package-private */
    public ImmutableList<E> createAsList() {
        return size() > 1 ? new ImmutableSortedAsList(this, this.elements) : this.elements;
    }

    /* access modifiers changed from: package-private */
    public ImmutableSortedSet<E> createDescendingSet() {
        Ordering reverse = Ordering.from(this.comparator).reverse();
        return !isEmpty() ? new RegularImmutableSortedSet(this.elements.reverse(), reverse) : emptySet(reverse);
    }

    @GwtIncompatible("NavigableSet")
    public UnmodifiableIterator<E> descendingIterator() {
        return this.elements.reverse().iterator();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x003a A[Catch:{ ClassCastException -> 0x004c, NoSuchElementException -> 0x004e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(@javax.annotation.Nullable java.lang.Object r7) {
        /*
            r6 = this;
            r5 = 1
            r4 = 0
            if (r7 == r6) goto L_0x0027
            boolean r0 = r7 instanceof java.util.Set
            if (r0 == 0) goto L_0x0028
            java.util.Set r7 = (java.util.Set) r7
            int r0 = r6.size()
            int r1 = r7.size()
            if (r0 != r1) goto L_0x0029
            boolean r0 = r6.isEmpty()
            if (r0 != 0) goto L_0x002a
            java.util.Comparator r0 = r6.comparator
            boolean r0 = com.google.common.collect.SortedIterables.hasSameComparator(r0, r7)
            if (r0 != 0) goto L_0x002b
            boolean r0 = r6.containsAll(r7)
            return r0
        L_0x0027:
            return r5
        L_0x0028:
            return r4
        L_0x0029:
            return r4
        L_0x002a:
            return r5
        L_0x002b:
            java.util.Iterator r0 = r7.iterator()
            com.google.common.collect.UnmodifiableIterator r1 = r6.iterator()     // Catch:{ ClassCastException -> 0x004c, NoSuchElementException -> 0x004e }
        L_0x0033:
            boolean r2 = r1.hasNext()     // Catch:{ ClassCastException -> 0x004c, NoSuchElementException -> 0x004e }
            if (r2 != 0) goto L_0x003a
            return r5
        L_0x003a:
            java.lang.Object r2 = r1.next()     // Catch:{ ClassCastException -> 0x004c, NoSuchElementException -> 0x004e }
            java.lang.Object r3 = r0.next()     // Catch:{ ClassCastException -> 0x004c, NoSuchElementException -> 0x004e }
            if (r3 != 0) goto L_0x0045
        L_0x0044:
            return r4
        L_0x0045:
            int r2 = r6.unsafeCompare(r2, r3)     // Catch:{ ClassCastException -> 0x004c, NoSuchElementException -> 0x004e }
            if (r2 != 0) goto L_0x0044
            goto L_0x0033
        L_0x004c:
            r0 = move-exception
            return r4
        L_0x004e:
            r0 = move-exception
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.RegularImmutableSortedSet.equals(java.lang.Object):boolean");
    }

    public E first() {
        if (!isEmpty()) {
            return this.elements.get(0);
        }
        throw new NoSuchElementException();
    }

    public E floor(E e) {
        int headIndex = headIndex(e, true) - 1;
        if (headIndex != -1) {
            return this.elements.get(headIndex);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public RegularImmutableSortedSet<E> getSubSet(int i, int i2) {
        return (i == 0 && i2 == size()) ? this : i >= i2 ? emptySet(this.comparator) : new RegularImmutableSortedSet<>(this.elements.subList(i, i2), this.comparator);
    }

    /* access modifiers changed from: package-private */
    public int headIndex(E e, boolean z) {
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(e), comparator(), !z ? SortedLists.KeyPresentBehavior.FIRST_PRESENT : SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    /* access modifiers changed from: package-private */
    public ImmutableSortedSet<E> headSetImpl(E e, boolean z) {
        return getSubSet(0, headIndex(e, z));
    }

    public E higher(E e) {
        int tailIndex = tailIndex(e, false);
        if (tailIndex != size()) {
            return this.elements.get(tailIndex);
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int indexOf(@Nullable Object obj) {
        if (obj == null) {
            return -1;
        }
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

    /* access modifiers changed from: package-private */
    public boolean isPartialView() {
        return this.elements.isPartialView();
    }

    public UnmodifiableIterator<E> iterator() {
        return this.elements.iterator();
    }

    public E last() {
        if (!isEmpty()) {
            return this.elements.get(size() - 1);
        }
        throw new NoSuchElementException();
    }

    public E lower(E e) {
        int headIndex = headIndex(e, false) - 1;
        if (headIndex != -1) {
            return this.elements.get(headIndex);
        }
        return null;
    }

    public int size() {
        return this.elements.size();
    }

    /* access modifiers changed from: package-private */
    public ImmutableSortedSet<E> subSetImpl(E e, boolean z, E e2, boolean z2) {
        return tailSetImpl(e, z).headSetImpl(e2, z2);
    }

    /* access modifiers changed from: package-private */
    public int tailIndex(E e, boolean z) {
        return SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(e), comparator(), !z ? SortedLists.KeyPresentBehavior.FIRST_AFTER : SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }

    /* access modifiers changed from: package-private */
    public ImmutableSortedSet<E> tailSetImpl(E e, boolean z) {
        return getSubSet(tailIndex(e, z), size());
    }

    /* access modifiers changed from: package-private */
    public Comparator<Object> unsafeComparator() {
        return this.comparator;
    }
}
