// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.SortedSet;
import java.util.Collection;
import java.util.Set;
import java.util.NavigableSet;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class UnmodifiableSortedMultiset<E> extends UnmodifiableMultiset<E> implements SortedMultiset<E>
{
    private static final long serialVersionUID = 0L;
    private transient UnmodifiableSortedMultiset<E> descendingMultiset;
    
    UnmodifiableSortedMultiset(final SortedMultiset<E> sortedMultiset) {
        super(sortedMultiset);
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.delegate().comparator();
    }
    
    NavigableSet<E> createElementSet() {
        return Sets.unmodifiableNavigableSet(this.delegate().elementSet());
    }
    
    @Override
    protected SortedMultiset<E> delegate() {
        return (SortedMultiset)super.delegate();
    }
    
    @Override
    public SortedMultiset<E> descendingMultiset() {
        final UnmodifiableSortedMultiset<E> descendingMultiset = this.descendingMultiset;
        if (descendingMultiset != null) {
            return descendingMultiset;
        }
        final UnmodifiableSortedMultiset descendingMultiset2 = new UnmodifiableSortedMultiset(this.delegate().descendingMultiset());
        descendingMultiset2.descendingMultiset = this;
        return this.descendingMultiset = descendingMultiset2;
    }
    
    @Override
    public NavigableSet<E> elementSet() {
        return (NavigableSet)super.elementSet();
    }
    
    @Override
    public Entry<E> firstEntry() {
        return this.delegate().firstEntry();
    }
    
    @Override
    public SortedMultiset<E> headMultiset(final E e, final BoundType boundType) {
        return Multisets.unmodifiableSortedMultiset(this.delegate().headMultiset(e, boundType));
    }
    
    @Override
    public Entry<E> lastEntry() {
        return this.delegate().lastEntry();
    }
    
    @Override
    public Entry<E> pollFirstEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Entry<E> pollLastEntry() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public SortedMultiset<E> subMultiset(final E e, final BoundType boundType, final E e2, final BoundType boundType2) {
        return Multisets.unmodifiableSortedMultiset(this.delegate().subMultiset(e, boundType, e2, boundType2));
    }
    
    @Override
    public SortedMultiset<E> tailMultiset(final E e, final BoundType boundType) {
        return Multisets.unmodifiableSortedMultiset(this.delegate().tailMultiset(e, boundType));
    }
}
