// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import java.util.Iterator;
import java.util.Collection;
import javax.annotation.Nullable;
import java.util.Set;
import com.google.common.annotations.GwtCompatible;
import java.util.AbstractCollection;

@GwtCompatible
abstract class AbstractMultiset<E> extends AbstractCollection<E> implements Multiset<E>
{
    private transient Set<E> elementSet;
    private transient Set<Entry<E>> entrySet;
    
    @Override
    public int add(@Nullable final E e, final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean add(@Nullable final E e) {
        this.add(e, 1);
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return Multisets.addAllImpl((Multiset<Object>)this, collection);
    }
    
    @Override
    public void clear() {
        Iterators.clear(this.entryIterator());
    }
    
    @Override
    public boolean contains(@Nullable final Object o) {
        boolean b = false;
        if (this.count(o) > 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int count(@Nullable final Object o) {
        for (final Entry<Object> entry : this.entrySet()) {
            if (Objects.equal(entry.getElement(), o)) {
                return entry.getCount();
            }
        }
        return 0;
    }
    
    Set<E> createElementSet() {
        return new ElementSet();
    }
    
    Set<Entry<E>> createEntrySet() {
        return (Set<Entry<E>>)new EntrySet();
    }
    
    abstract int distinctElements();
    
    @Override
    public Set<E> elementSet() {
        Set<E> elementSet = this.elementSet;
        if (elementSet == null) {
            elementSet = this.createElementSet();
            this.elementSet = elementSet;
        }
        return elementSet;
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
    public boolean equals(@Nullable final Object o) {
        return Multisets.equalsImpl(this, o);
    }
    
    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }
    
    @Override
    public boolean isEmpty() {
        return this.entrySet().isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl((Multiset<E>)this);
    }
    
    @Override
    public int remove(@Nullable final Object o, final int n) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(@Nullable final Object o) {
        boolean b = false;
        if (this.remove(o, 1) > 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        return Multisets.removeAllImpl(this, collection);
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        return Multisets.retainAllImpl(this, collection);
    }
    
    @Override
    public int setCount(@Nullable final E e, final int n) {
        return Multisets.setCountImpl(this, e, n);
    }
    
    @Override
    public boolean setCount(@Nullable final E e, final int n, final int n2) {
        return Multisets.setCountImpl(this, e, n, n2);
    }
    
    @Override
    public int size() {
        return Multisets.sizeImpl(this);
    }
    
    @Override
    public String toString() {
        return this.entrySet().toString();
    }
    
    class ElementSet extends Multisets.ElementSet<E>
    {
        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }
    }
    
    class EntrySet extends Multisets.EntrySet<E>
    {
        @Override
        public Iterator<Entry<E>> iterator() {
            return AbstractMultiset.this.entryIterator();
        }
        
        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }
        
        @Override
        public int size() {
            return AbstractMultiset.this.distinctElements();
        }
    }
}
