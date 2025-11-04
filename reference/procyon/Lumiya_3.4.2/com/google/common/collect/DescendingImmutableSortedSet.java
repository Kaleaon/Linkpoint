// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NavigableSet;
import java.util.Iterator;
import com.google.common.annotations.GwtIncompatible;
import javax.annotation.Nullable;
import java.util.Comparator;

class DescendingImmutableSortedSet<E> extends ImmutableSortedSet<E>
{
    private final ImmutableSortedSet<E> forward;
    
    DescendingImmutableSortedSet(final ImmutableSortedSet<E> forward) {
        super(Ordering.from(forward.comparator()).reverse());
        this.forward = forward;
    }
    
    @Override
    public E ceiling(final E e) {
        return this.forward.floor(e);
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        return this.forward.contains(o);
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    ImmutableSortedSet<E> createDescendingSet() {
        throw new AssertionError((Object)"should never be called");
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<E> descendingIterator() {
        return this.forward.iterator();
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public ImmutableSortedSet<E> descendingSet() {
        return this.forward;
    }
    
    @Override
    public E floor(final E e) {
        return this.forward.ceiling(e);
    }
    
    @Override
    ImmutableSortedSet<E> headSetImpl(final E e, final boolean b) {
        return this.forward.tailSet(e, b).descendingSet();
    }
    
    @Override
    public E higher(final E e) {
        return this.forward.lower(e);
    }
    
    @Override
    int indexOf(@Nullable final Object o) {
        final int index = this.forward.indexOf(o);
        if (index != -1) {
            return this.size() - 1 - index;
        }
        return index;
    }
    
    @Override
    boolean isPartialView() {
        return this.forward.isPartialView();
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return this.forward.descendingIterator();
    }
    
    @Override
    public E lower(final E e) {
        return this.forward.higher(e);
    }
    
    @Override
    public int size() {
        return this.forward.size();
    }
    
    @Override
    ImmutableSortedSet<E> subSetImpl(final E e, final boolean b, final E e2, final boolean b2) {
        return this.forward.subSet(e2, b2, e, b).descendingSet();
    }
    
    @Override
    ImmutableSortedSet<E> tailSetImpl(final E e, final boolean b) {
        return this.forward.headSet(e, b).descendingSet();
    }
}
