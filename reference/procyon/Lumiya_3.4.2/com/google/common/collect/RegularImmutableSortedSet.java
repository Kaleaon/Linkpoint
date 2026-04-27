// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;
import java.util.Set;
import com.google.common.annotations.GwtIncompatible;
import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E>
{
    private final transient ImmutableList<E> elements;
    
    RegularImmutableSortedSet(final ImmutableList<E> elements, final Comparator<? super E> comparator) {
        super(comparator);
        this.elements = elements;
    }
    
    private int unsafeBinarySearch(final Object key) throws ClassCastException {
        return Collections.binarySearch(this.elements, key, this.unsafeComparator());
    }
    
    @Override
    public E ceiling(final E e) {
        final int tailIndex = this.tailIndex(e, true);
        Object value;
        if (tailIndex != this.size()) {
            value = this.elements.get(tailIndex);
        }
        else {
            value = null;
        }
        return (E)value;
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        boolean b = false;
        if (o != null) {
            try {
                if (this.unsafeBinarySearch(o) >= 0) {
                    b = true;
                }
            }
            catch (final ClassCastException ex) {
                return false;
            }
        }
        return b;
    }
    
    @Override
    public boolean containsAll(Collection<?> elementSet) {
        if (elementSet instanceof Multiset) {
            elementSet = ((Multiset)elementSet).elementSet();
        }
        if (!SortedIterables.hasSameComparator(this.comparator(), elementSet) || elementSet.size() <= 1) {
            return super.containsAll(elementSet);
        }
        final PeekingIterator<Object> peekingIterator = Iterators.peekingIterator((Iterator<?>)this.iterator());
        final Object next = elementSet.iterator().next();
        try {
            while (peekingIterator.hasNext()) {
                final int unsafeCompare = this.unsafeCompare(peekingIterator.peek(), next);
                if (unsafeCompare >= 0) {
                    if (unsafeCompare == 0) {
                        goto Label_0123;
                    }
                    if (unsafeCompare > 0) {
                        return false;
                    }
                    continue;
                }
                else {
                    peekingIterator.next();
                }
            }
            return false;
        }
        catch (final NullPointerException ex) {
            return false;
        }
        catch (final ClassCastException ex2) {
            return false;
        }
    }
    
    @Override
    int copyIntoArray(final Object[] array, final int n) {
        return this.elements.copyIntoArray(array, n);
    }
    
    @Override
    ImmutableList<E> createAsList() {
        ImmutableList<E> elements;
        if (this.size() > 1) {
            elements = new ImmutableSortedAsList<E>(this, this.elements);
        }
        else {
            elements = this.elements;
        }
        return elements;
    }
    
    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        final Ordering<Object> reverse = Ordering.from(this.comparator).reverse();
        RegularImmutableSortedSet<Object> emptySet;
        if (!this.isEmpty()) {
            emptySet = new RegularImmutableSortedSet<Object>((ImmutableList<Object>)this.elements.reverse(), reverse);
        }
        else {
            emptySet = ImmutableSortedSet.emptySet(reverse);
        }
        return (ImmutableSortedSet<E>)emptySet;
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return this.elements.reverse().iterator();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Set set = (Set)o;
        if (this.size() != set.size()) {
            return false;
        }
        if (this.isEmpty()) {
            return true;
        }
        if (!SortedIterables.hasSameComparator(this.comparator, set)) {
            return this.containsAll(set);
        }
        final Iterator iterator = set.iterator();
        try {
            for (final Object next : this) {
                final Object next2 = iterator.next();
                if (next2 != null && this.unsafeCompare(next, next2) == 0) {
                    continue;
                }
                return false;
            }
            return true;
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NoSuchElementException ex2) {
            return false;
        }
    }
    
    @Override
    public E first() {
        if (!this.isEmpty()) {
            return this.elements.get(0);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public E floor(final E e) {
        final int n = this.headIndex(e, true) - 1;
        Object value;
        if (n != -1) {
            value = this.elements.get(n);
        }
        else {
            value = null;
        }
        return (E)value;
    }
    
    RegularImmutableSortedSet<E> getSubSet(final int n, final int n2) {
        if (n == 0 && n2 == this.size()) {
            return this;
        }
        if (n >= n2) {
            return ImmutableSortedSet.emptySet(this.comparator);
        }
        return new RegularImmutableSortedSet<E>(this.elements.subList(n, n2), this.comparator);
    }
    
    int headIndex(final E e, final boolean b) {
        final ImmutableList<E> elements = this.elements;
        final E checkNotNull = Preconditions.checkNotNull(e);
        final Comparator<? super E> comparator = this.comparator();
        SortedLists.KeyPresentBehavior keyPresentBehavior;
        if (!b) {
            keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_PRESENT;
        }
        else {
            keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_AFTER;
        }
        return SortedLists.binarySearch((List<? extends E>)elements, checkNotNull, comparator, keyPresentBehavior, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }
    
    @Override
    ImmutableSortedSet<E> headSetImpl(final E e, final boolean b) {
        return this.getSubSet(0, this.headIndex(e, b));
    }
    
    @Override
    public E higher(final E e) {
        final int tailIndex = this.tailIndex(e, false);
        Object value;
        if (tailIndex != this.size()) {
            value = this.elements.get(tailIndex);
        }
        else {
            value = null;
        }
        return (E)value;
    }
    
    @Override
    int indexOf(@Nullable final Object o) {
        final int n = -1;
        if (o == null) {
            return -1;
        }
        while (true) {
            int binarySearch;
            try {
                binarySearch = SortedLists.binarySearch(this.elements, o, this.unsafeComparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
                if (binarySearch < 0) {
                    binarySearch = n;
                    return binarySearch;
                }
                return binarySearch;
            }
            catch (final ClassCastException ex) {
                return -1;
            }
            return binarySearch;
        }
    }
    
    @Override
    boolean isPartialView() {
        return this.elements.isPartialView();
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.elements.iterator();
    }
    
    @Override
    public E last() {
        if (!this.isEmpty()) {
            return this.elements.get(this.size() - 1);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public E lower(final E e) {
        final int n = this.headIndex(e, false) - 1;
        Object value;
        if (n != -1) {
            value = this.elements.get(n);
        }
        else {
            value = null;
        }
        return (E)value;
    }
    
    @Override
    public int size() {
        return this.elements.size();
    }
    
    @Override
    ImmutableSortedSet<E> subSetImpl(final E e, final boolean b, final E e2, final boolean b2) {
        return this.tailSetImpl(e, b).headSetImpl(e2, b2);
    }
    
    int tailIndex(final E e, final boolean b) {
        final ImmutableList<E> elements = this.elements;
        final E checkNotNull = Preconditions.checkNotNull(e);
        final Comparator<? super E> comparator = this.comparator();
        SortedLists.KeyPresentBehavior keyPresentBehavior;
        if (!b) {
            keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_AFTER;
        }
        else {
            keyPresentBehavior = SortedLists.KeyPresentBehavior.FIRST_PRESENT;
        }
        return SortedLists.binarySearch((List<? extends E>)elements, checkNotNull, comparator, keyPresentBehavior, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
    }
    
    @Override
    ImmutableSortedSet<E> tailSetImpl(final E e, final boolean b) {
        return this.getSubSet(this.tailIndex(e, b), this.size());
    }
    
    Comparator<Object> unsafeComparator() {
        return (Comparator<Object>)this.comparator;
    }
}
