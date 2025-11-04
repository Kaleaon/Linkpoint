// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
public abstract class ForwardingListIterator<E> extends ForwardingIterator<E> implements ListIterator<E>
{
    protected ForwardingListIterator() {
    }
    
    @Override
    public void add(final E e) {
        this.delegate().add(e);
    }
    
    @Override
    protected abstract ListIterator<E> delegate();
    
    @Override
    public boolean hasPrevious() {
        return this.delegate().hasPrevious();
    }
    
    @Override
    public int nextIndex() {
        return this.delegate().nextIndex();
    }
    
    @Override
    public E previous() {
        return this.delegate().previous();
    }
    
    @Override
    public int previousIndex() {
        return this.delegate().previousIndex();
    }
    
    @Override
    public void set(final E e) {
        this.delegate().set(e);
    }
}
