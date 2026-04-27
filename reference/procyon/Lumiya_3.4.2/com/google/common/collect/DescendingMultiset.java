// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Set;
import java.util.NavigableSet;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
abstract class DescendingMultiset<E> extends ForwardingMultiset<E> implements SortedMultiset<E>
{
    private transient Comparator<? super E> comparator;
    private transient NavigableSet<E> elementSet;
    private transient Set<Entry<E>> entrySet;
    
    @Override
    public Comparator<? super E> comparator() {
        final Comparator<? super E> comparator = this.comparator;
        if (comparator != null) {
            return comparator;
        }
        return this.comparator = Ordering.from(this.forwardMultiset().comparator()).reverse();
    }
    
    Set<Entry<E>> createEntrySet() {
        return (Set<Entry<E>>)new EntrySetImpl();
    }
    
    @Override
    protected Multiset<E> delegate() {
        return this.forwardMultiset();
    }
    
    @Override
    public SortedMultiset<E> descendingMultiset() {
        return this.forwardMultiset();
    }
    
    @Override
    public NavigableSet<E> elementSet() {
        final NavigableSet<E> elementSet = this.elementSet;
        if (elementSet != null) {
            return elementSet;
        }
        return this.elementSet = new SortedMultisets.NavigableElementSet<E>(this);
    }
    
    abstract Iterator<Entry<E>> entryIterator();
    
    @Override
    public Set<Entry<E>> entrySet() {
        Set<Entry<E>> entrySet = this.entrySet;
        if (entrySet == null) {
            entrySet = this.createEntrySet();
            this.entrySet = entrySet;
        }
        return entrySet;
    }
    
    @Override
    public Entry<E> firstEntry() {
        return this.forwardMultiset().lastEntry();
    }
    
    abstract SortedMultiset<E> forwardMultiset();
    
    @Override
    public SortedMultiset<E> headMultiset(final E e, final BoundType boundType) {
        return this.forwardMultiset().tailMultiset(e, boundType).descendingMultiset();
    }
    
    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl((Multiset<E>)this);
    }
    
    @Override
    public Entry<E> lastEntry() {
        return this.forwardMultiset().firstEntry();
    }
    
    @Override
    public Entry<E> pollFirstEntry() {
        return this.forwardMultiset().pollLastEntry();
    }
    
    @Override
    public Entry<E> pollLastEntry() {
        return this.forwardMultiset().pollFirstEntry();
    }
    
    @Override
    public SortedMultiset<E> subMultiset(final E e, final BoundType boundType, final E e2, final BoundType boundType2) {
        return this.forwardMultiset().subMultiset(e2, boundType2, e, boundType).descendingMultiset();
    }
    
    @Override
    public SortedMultiset<E> tailMultiset(final E e, final BoundType boundType) {
        return this.forwardMultiset().headMultiset(e, boundType).descendingMultiset();
    }
    
    @Override
    public Object[] toArray() {
        return this.standardToArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.standardToArray(array);
    }
    
    @Override
    public String toString() {
        return this.entrySet().toString();
    }
    
    class EntrySetImpl extends EntrySet<E>
    {
        @Override
        public Iterator<Entry<E>> iterator() {
            return DescendingMultiset.this.entryIterator();
        }
        
        @Override
        Multiset<E> multiset() {
            return DescendingMultiset.this;
        }
        
        @Override
        public int size() {
            return DescendingMultiset.this.forwardMultiset().entrySet().size();
        }
    }
}
