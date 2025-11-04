// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.SortedSet;
import java.util.Set;
import java.util.NavigableSet;
import javax.annotation.Nullable;

final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
{
    private final transient ImmutableSortedMultiset<E> forward;
    
    DescendingImmutableSortedMultiset(final ImmutableSortedMultiset<E> forward) {
        this.forward = forward;
    }
    
    @Override
    public int count(@Nullable final Object o) {
        return this.forward.count(o);
    }
    
    @Override
    public ImmutableSortedMultiset<E> descendingMultiset() {
        return this.forward;
    }
    
    @Override
    public ImmutableSortedSet<E> elementSet() {
        return this.forward.elementSet().descendingSet();
    }
    
    @Override
    public Entry<E> firstEntry() {
        return this.forward.lastEntry();
    }
    
    @Override
    Entry<E> getEntry(final int n) {
        return (Entry<E>)this.forward.entrySet().asList().reverse().get(n);
    }
    
    @Override
    public ImmutableSortedMultiset<E> headMultiset(final E e, final BoundType boundType) {
        return this.forward.tailMultiset(e, boundType).descendingMultiset();
    }
    
    @Override
    boolean isPartialView() {
        return this.forward.isPartialView();
    }
    
    @Override
    public Entry<E> lastEntry() {
        return this.forward.firstEntry();
    }
    
    @Override
    public int size() {
        return this.forward.size();
    }
    
    @Override
    public ImmutableSortedMultiset<E> tailMultiset(final E e, final BoundType boundType) {
        return this.forward.headMultiset(e, boundType).descendingMultiset();
    }
}
