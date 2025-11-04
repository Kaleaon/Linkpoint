// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import javax.annotation.Nullable;
import java.util.SortedSet;
import java.util.Iterator;
import java.util.Set;
import java.util.NavigableSet;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
abstract class AbstractSortedMultiset<E> extends AbstractMultiset<E> implements SortedMultiset<E>
{
    @GwtTransient
    final Comparator<? super E> comparator;
    private transient SortedMultiset<E> descendingMultiset;
    
    AbstractSortedMultiset() {
        this((Comparator)Ordering.natural());
    }
    
    AbstractSortedMultiset(final Comparator<? super E> comparator) {
        this.comparator = Preconditions.checkNotNull(comparator);
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.comparator;
    }
    
    SortedMultiset<E> createDescendingMultiset() {
        return new DescendingMultisetImpl();
    }
    
    @Override
    NavigableSet<E> createElementSet() {
        return new SortedMultisets.NavigableElementSet<E>(this);
    }
    
    abstract Iterator<Entry<E>> descendingEntryIterator();
    
    Iterator<E> descendingIterator() {
        return Multisets.iteratorImpl(this.descendingMultiset());
    }
    
    @Override
    public SortedMultiset<E> descendingMultiset() {
        SortedMultiset<E> descendingMultiset = this.descendingMultiset;
        if (descendingMultiset == null) {
            descendingMultiset = this.createDescendingMultiset();
            this.descendingMultiset = descendingMultiset;
        }
        return descendingMultiset;
    }
    
    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet)super.elementSet();
    }
    
    @Override
    public Entry<E> firstEntry() {
        final Iterator<Entry<E>> entryIterator = this.entryIterator();
        Entry<E> entry;
        if (!entryIterator.hasNext()) {
            entry = null;
        }
        else {
            entry = (Entry<E>)entryIterator.next();
        }
        return entry;
    }
    
    @Override
    public Entry<E> lastEntry() {
        final Iterator<Entry<E>> descendingEntryIterator = this.descendingEntryIterator();
        Entry<E> entry;
        if (!descendingEntryIterator.hasNext()) {
            entry = null;
        }
        else {
            entry = (Entry<E>)descendingEntryIterator.next();
        }
        return entry;
    }
    
    @Override
    public Entry<E> pollFirstEntry() {
        final Iterator<Entry<E>> entryIterator = this.entryIterator();
        if (!entryIterator.hasNext()) {
            return null;
        }
        final Multiset.Entry<E> entry = entryIterator.next();
        final Entry<Object> immutableEntry = Multisets.immutableEntry((Object)entry.getElement(), entry.getCount());
        entryIterator.remove();
        return (Entry<E>)immutableEntry;
    }
    
    @Override
    public Entry<E> pollLastEntry() {
        final Iterator<Entry<E>> descendingEntryIterator = this.descendingEntryIterator();
        if (!descendingEntryIterator.hasNext()) {
            return null;
        }
        final Multiset.Entry<E> entry = descendingEntryIterator.next();
        final Entry<Object> immutableEntry = Multisets.immutableEntry((Object)entry.getElement(), entry.getCount());
        descendingEntryIterator.remove();
        return (Entry<E>)immutableEntry;
    }
    
    @Override
    public SortedMultiset<E> subMultiset(@Nullable final E e, final BoundType boundType, @Nullable final E e2, final BoundType boundType2) {
        Preconditions.checkNotNull(boundType);
        Preconditions.checkNotNull(boundType2);
        return this.tailMultiset(e, boundType).headMultiset(e2, boundType2);
    }
    
    class DescendingMultisetImpl extends DescendingMultiset<E>
    {
        @Override
        Iterator<Entry<E>> entryIterator() {
            return AbstractSortedMultiset.this.descendingEntryIterator();
        }
        
        @Override
        SortedMultiset<E> forwardMultiset() {
            return AbstractSortedMultiset.this;
        }
        
        @Override
        public Iterator<E> iterator() {
            return AbstractSortedMultiset.this.descendingIterator();
        }
    }
}
