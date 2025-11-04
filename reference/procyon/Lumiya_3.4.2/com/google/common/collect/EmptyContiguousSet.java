// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true)
final class EmptyContiguousSet<C extends Comparable> extends ContiguousSet<C>
{
    EmptyContiguousSet(final DiscreteDomain<C> discreteDomain) {
        super(discreteDomain);
    }
    
    @Override
    public ImmutableList<C> asList() {
        return ImmutableList.of();
    }
    
    @Override
    public boolean contains(final Object o) {
        return false;
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    ImmutableSortedSet<C> createDescendingSet() {
        return (ImmutableSortedSet<C>)ImmutableSortedSet.emptySet(Ordering.natural().reverse());
    }
    
    @GwtIncompatible("NavigableSet")
    @Override
    public UnmodifiableIterator<C> descendingIterator() {
        return Iterators.emptyIterator();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o instanceof Set && ((Set)o).isEmpty();
    }
    
    @Override
    public C first() {
        throw new NoSuchElementException();
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    @Override
    ContiguousSet<C> headSetImpl(final C c, final boolean b) {
        return this;
    }
    
    @GwtIncompatible("not used by GWT emulation")
    @Override
    int indexOf(final Object o) {
        return -1;
    }
    
    @Override
    public ContiguousSet<C> intersection(final ContiguousSet<C> set) {
        return this;
    }
    
    @Override
    public boolean isEmpty() {
        return true;
    }
    
    @GwtIncompatible("not used in GWT")
    @Override
    boolean isHashCodeFast() {
        return true;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public UnmodifiableIterator<C> iterator() {
        return Iterators.emptyIterator();
    }
    
    @Override
    public C last() {
        throw new NoSuchElementException();
    }
    
    @Override
    public Range<C> range() {
        throw new NoSuchElementException();
    }
    
    @Override
    public Range<C> range(final BoundType boundType, final BoundType boundType2) {
        throw new NoSuchElementException();
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    ContiguousSet<C> subSetImpl(final C c, final boolean b, final C c2, final boolean b2) {
        return this;
    }
    
    @Override
    ContiguousSet<C> tailSetImpl(final C c, final boolean b) {
        return this;
    }
    
    @Override
    public String toString() {
        return "[]";
    }
    
    @GwtIncompatible("serialization")
    @Override
    Object writeReplace() {
        return new SerializedForm((DiscreteDomain)this.domain);
    }
    
    @GwtIncompatible("serialization")
    private static final class SerializedForm<C extends Comparable> implements Serializable
    {
        private static final long serialVersionUID = 0L;
        private final DiscreteDomain<C> domain;
        
        private SerializedForm(final DiscreteDomain<C> domain) {
            this.domain = domain;
        }
        
        private Object readResolve() {
            return new EmptyContiguousSet((DiscreteDomain<Comparable>)this.domain);
        }
    }
}
