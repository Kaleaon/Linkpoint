// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.common.annotations.GwtIncompatible;
import java.util.NavigableSet;
import java.util.Comparator;
import com.google.j2objc.annotations.Weak;
import java.util.SortedSet;
import java.util.NoSuchElementException;
import javax.annotation.Nullable;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class SortedMultisets
{
    private SortedMultisets() {
    }
    
    private static <E> E getElementOrNull(@Nullable final Multiset.Entry<E> entry) {
        E element = null;
        if (entry != null) {
            element = entry.getElement();
        }
        return element;
    }
    
    private static <E> E getElementOrThrow(final Multiset.Entry<E> entry) {
        if (entry != null) {
            return entry.getElement();
        }
        throw new NoSuchElementException();
    }
    
    static class ElementSet<E> extends Multisets.ElementSet<E> implements SortedSet<E>
    {
        @Weak
        private final SortedMultiset<E> multiset;
        
        ElementSet(final SortedMultiset<E> multiset) {
            this.multiset = multiset;
        }
        
        @Override
        public Comparator<? super E> comparator() {
            return this.multiset().comparator();
        }
        
        @Override
        public E first() {
            return (E)getElementOrThrow((Multiset.Entry<Object>)this.multiset().firstEntry());
        }
        
        @Override
        public SortedSet<E> headSet(final E e) {
            return this.multiset().headMultiset(e, BoundType.OPEN).elementSet();
        }
        
        @Override
        public E last() {
            return (E)getElementOrThrow((Multiset.Entry<Object>)this.multiset().lastEntry());
        }
        
        final SortedMultiset<E> multiset() {
            return this.multiset;
        }
        
        @Override
        public SortedSet<E> subSet(final E e, final E e2) {
            return this.multiset().subMultiset(e, BoundType.CLOSED, e2, BoundType.OPEN).elementSet();
        }
        
        @Override
        public SortedSet<E> tailSet(final E e) {
            return this.multiset().tailMultiset(e, BoundType.CLOSED).elementSet();
        }
    }
    
    @GwtIncompatible("Navigable")
    static class NavigableElementSet<E> extends ElementSet<E> implements NavigableSet<E>
    {
        NavigableElementSet(final SortedMultiset<E> sortedMultiset) {
            super(sortedMultiset);
        }
        
        @Override
        public E ceiling(final E e) {
            return (E)getElementOrNull((Multiset.Entry<Object>)this.multiset().tailMultiset(e, BoundType.CLOSED).firstEntry());
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            return this.descendingSet().iterator();
        }
        
        @Override
        public NavigableSet<E> descendingSet() {
            return new NavigableElementSet((SortedMultiset<Object>)this.multiset().descendingMultiset());
        }
        
        @Override
        public E floor(final E e) {
            return (E)getElementOrNull((Multiset.Entry<Object>)this.multiset().headMultiset(e, BoundType.CLOSED).lastEntry());
        }
        
        @Override
        public NavigableSet<E> headSet(final E e, final boolean b) {
            return new NavigableElementSet((SortedMultiset<Object>)this.multiset().headMultiset(e, BoundType.forBoolean(b)));
        }
        
        @Override
        public E higher(final E e) {
            return (E)getElementOrNull((Multiset.Entry<Object>)this.multiset().tailMultiset(e, BoundType.OPEN).firstEntry());
        }
        
        @Override
        public E lower(final E e) {
            return (E)getElementOrNull((Multiset.Entry<Object>)this.multiset().headMultiset(e, BoundType.OPEN).lastEntry());
        }
        
        @Override
        public E pollFirst() {
            return (E)getElementOrNull((Multiset.Entry<Object>)this.multiset().pollFirstEntry());
        }
        
        @Override
        public E pollLast() {
            return (E)getElementOrNull((Multiset.Entry<Object>)this.multiset().pollLastEntry());
        }
        
        @Override
        public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
            return new NavigableElementSet((SortedMultiset<Object>)this.multiset().subMultiset(e, BoundType.forBoolean(b), e2, BoundType.forBoolean(b2)));
        }
        
        @Override
        public NavigableSet<E> tailSet(final E e, final boolean b) {
            return new NavigableElementSet((SortedMultiset<Object>)this.multiset().tailMultiset(e, BoundType.forBoolean(b)));
        }
    }
}
