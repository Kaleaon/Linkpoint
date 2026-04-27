// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.common.annotations.Beta;
import java.util.ListIterator;
import javax.annotation.Nullable;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;
import java.util.List;

@GwtCompatible
public abstract class ForwardingList<E> extends ForwardingCollection<E> implements List<E>
{
    protected ForwardingList() {
    }
    
    @Override
    public void add(final int n, final E e) {
        this.delegate().add(n, e);
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends E> collection) {
        return this.delegate().addAll(n, collection);
    }
    
    @Override
    protected abstract List<E> delegate();
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (o == this || this.delegate().equals(o)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public E get(final int n) {
        return this.delegate().get(n);
    }
    
    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }
    
    @Override
    public int indexOf(final Object o) {
        return this.delegate().indexOf(o);
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        return this.delegate().lastIndexOf(o);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.delegate().listIterator();
    }
    
    @Override
    public ListIterator<E> listIterator(final int n) {
        return this.delegate().listIterator(n);
    }
    
    @Override
    public E remove(final int n) {
        return this.delegate().remove(n);
    }
    
    @Override
    public E set(final int n, final E e) {
        return this.delegate().set(n, e);
    }
    
    protected boolean standardAdd(final E e) {
        this.add(this.size(), e);
        return true;
    }
    
    protected boolean standardAddAll(final int n, final Iterable<? extends E> iterable) {
        return Lists.addAllImpl((List<Object>)this, n, iterable);
    }
    
    @Beta
    protected boolean standardEquals(@Nullable final Object o) {
        return Lists.equalsImpl(this, o);
    }
    
    @Beta
    protected int standardHashCode() {
        return Lists.hashCodeImpl(this);
    }
    
    protected int standardIndexOf(@Nullable final Object o) {
        return Lists.indexOfImpl(this, o);
    }
    
    protected Iterator<E> standardIterator() {
        return this.listIterator();
    }
    
    protected int standardLastIndexOf(@Nullable final Object o) {
        return Lists.lastIndexOfImpl(this, o);
    }
    
    protected ListIterator<E> standardListIterator() {
        return this.listIterator(0);
    }
    
    @Beta
    protected ListIterator<E> standardListIterator(final int n) {
        return Lists.listIteratorImpl((List<E>)this, n);
    }
    
    @Beta
    protected List<E> standardSubList(final int n, final int n2) {
        return Lists.subListImpl((List<E>)this, n, n2);
    }
    
    @Override
    public List<E> subList(final int n, final int n2) {
        return this.delegate().subList(n, n2);
    }
}
