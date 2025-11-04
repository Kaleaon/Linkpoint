// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import java.util.Iterator;
import com.google.common.annotations.Beta;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class ForwardingMultiset<E> extends ForwardingCollection<E> implements Multiset<E>
{
    protected ForwardingMultiset() {
    }
    
    @Override
    public int add(final E e, final int n) {
        return this.delegate().add(e, n);
    }
    
    @Override
    public int count(final Object o) {
        return this.delegate().count(o);
    }
    
    @Override
    protected abstract Multiset<E> delegate();
    
    @Override
    public Set<E> elementSet() {
        return this.delegate().elementSet();
    }
    
    @Override
    public Set<Entry<E>> entrySet() {
        return this.delegate().entrySet();
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (o == this || this.delegate().equals(o)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    @Override
    public int remove(final Object o, final int n) {
        return this.delegate().remove(o, n);
    }
    
    @Override
    public int setCount(final E e, final int n) {
        return this.delegate().setCount(e, n);
    }
    
    @Override
    public boolean setCount(final E e, final int n, final int n2) {
        return this.delegate().setCount(e, n, n2);
    }
    
    protected boolean standardAdd(final E e) {
        this.add(e, 1);
        return true;
    }
    
    @Beta
    @Override
    protected boolean standardAddAll(final Collection<? extends E> collection) {
        return Multisets.addAllImpl((Multiset<Object>)this, collection);
    }
    
    @Override
    protected void standardClear() {
        Iterators.clear(this.entrySet().iterator());
    }
    
    @Override
    protected boolean standardContains(@Nullable final Object o) {
        boolean b = false;
        if (this.count(o) > 0) {
            b = true;
        }
        return b;
    }
    
    @Beta
    protected int standardCount(@Nullable final Object o) {
        for (final Entry<Object> entry : this.entrySet()) {
            if (Objects.equal(entry.getElement(), o)) {
                return entry.getCount();
            }
        }
        return 0;
    }
    
    protected boolean standardEquals(@Nullable final Object o) {
        return Multisets.equalsImpl(this, o);
    }
    
    protected int standardHashCode() {
        return this.entrySet().hashCode();
    }
    
    protected Iterator<E> standardIterator() {
        return Multisets.iteratorImpl((Multiset<E>)this);
    }
    
    @Override
    protected boolean standardRemove(final Object o) {
        boolean b = false;
        if (this.remove(o, 1) > 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    protected boolean standardRemoveAll(final Collection<?> collection) {
        return Multisets.removeAllImpl(this, collection);
    }
    
    @Override
    protected boolean standardRetainAll(final Collection<?> collection) {
        return Multisets.retainAllImpl(this, collection);
    }
    
    protected int standardSetCount(final E e, final int n) {
        return Multisets.setCountImpl(this, e, n);
    }
    
    protected boolean standardSetCount(final E e, final int n, final int n2) {
        return Multisets.setCountImpl(this, e, n, n2);
    }
    
    protected int standardSize() {
        return Multisets.sizeImpl(this);
    }
    
    @Override
    protected String standardToString() {
        return this.entrySet().toString();
    }
    
    @Beta
    protected class StandardElementSet extends ElementSet<E>
    {
        public StandardElementSet() {
        }
        
        @Override
        Multiset<E> multiset() {
            return ForwardingMultiset.this;
        }
    }
}
