// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.annotations.Beta;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Set;
import java.util.Collection;
import java.util.NavigableSet;

public abstract class ForwardingNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E>
{
    protected ForwardingNavigableSet() {
    }
    
    @Override
    public E ceiling(final E e) {
        return this.delegate().ceiling(e);
    }
    
    @Override
    protected abstract NavigableSet<E> delegate();
    
    @Override
    public Iterator<E> descendingIterator() {
        return this.delegate().descendingIterator();
    }
    
    @Override
    public NavigableSet<E> descendingSet() {
        return this.delegate().descendingSet();
    }
    
    @Override
    public E floor(final E e) {
        return this.delegate().floor(e);
    }
    
    @Override
    public NavigableSet<E> headSet(final E e, final boolean b) {
        return this.delegate().headSet(e, b);
    }
    
    @Override
    public E higher(final E e) {
        return this.delegate().higher(e);
    }
    
    @Override
    public E lower(final E e) {
        return this.delegate().lower(e);
    }
    
    @Override
    public E pollFirst() {
        return this.delegate().pollFirst();
    }
    
    @Override
    public E pollLast() {
        return this.delegate().pollLast();
    }
    
    protected E standardCeiling(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.tailSet(e, true).iterator(), (E)null);
    }
    
    protected E standardFirst() {
        return this.iterator().next();
    }
    
    protected E standardFloor(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.headSet(e, true).descendingIterator(), (E)null);
    }
    
    protected SortedSet<E> standardHeadSet(final E e) {
        return this.headSet(e, false);
    }
    
    protected E standardHigher(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.tailSet(e, false).iterator(), (E)null);
    }
    
    protected E standardLast() {
        return this.descendingIterator().next();
    }
    
    protected E standardLower(final E e) {
        return Iterators.getNext((Iterator<? extends E>)this.headSet(e, false).descendingIterator(), (E)null);
    }
    
    protected E standardPollFirst() {
        return Iterators.pollNext(this.iterator());
    }
    
    protected E standardPollLast() {
        return Iterators.pollNext(this.descendingIterator());
    }
    
    @Beta
    protected NavigableSet<E> standardSubSet(final E e, final boolean b, final E e2, final boolean b2) {
        return this.tailSet(e, b).headSet(e2, b2);
    }
    
    @Override
    protected SortedSet<E> standardSubSet(final E e, final E e2) {
        return this.subSet(e, true, e2, false);
    }
    
    protected SortedSet<E> standardTailSet(final E e) {
        return this.tailSet(e, true);
    }
    
    @Override
    public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
        return this.delegate().subSet(e, b, e2, b2);
    }
    
    @Override
    public NavigableSet<E> tailSet(final E e, final boolean b) {
        return this.delegate().tailSet(e, b);
    }
    
    @Beta
    protected class StandardDescendingSet extends DescendingSet<E>
    {
        public StandardDescendingSet() {
            super(ForwardingNavigableSet.this);
        }
    }
}
