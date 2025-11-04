// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import com.google.common.base.Objects;
import javax.annotation.Nullable;
import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;
import java.util.Collection;

@GwtCompatible
public abstract class ForwardingCollection<E> extends ForwardingObject implements Collection<E>
{
    protected ForwardingCollection() {
    }
    
    @Override
    public boolean add(final E e) {
        return this.delegate().add(e);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) {
        return this.delegate().addAll(collection);
    }
    
    @Override
    public void clear() {
        this.delegate().clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.delegate().contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.delegate().containsAll(collection);
    }
    
    @Override
    protected abstract Collection<E> delegate();
    
    @Override
    public boolean isEmpty() {
        return this.delegate().isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.delegate().iterator();
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.delegate().remove(o);
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        return this.delegate().removeAll(collection);
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) {
        return this.delegate().retainAll(collection);
    }
    
    @Override
    public int size() {
        return this.delegate().size();
    }
    
    protected boolean standardAddAll(final Collection<? extends E> collection) {
        return Iterators.addAll((Collection<Object>)this, collection.iterator());
    }
    
    protected void standardClear() {
        Iterators.clear(this.iterator());
    }
    
    protected boolean standardContains(@Nullable final Object o) {
        return Iterators.contains(this.iterator(), o);
    }
    
    protected boolean standardContainsAll(final Collection<?> collection) {
        return Collections2.containsAllImpl(this, collection);
    }
    
    protected boolean standardIsEmpty() {
        boolean b = false;
        if (!this.iterator().hasNext()) {
            b = true;
        }
        return b;
    }
    
    protected boolean standardRemove(@Nullable final Object o) {
        final Iterator<E> iterator = this.iterator();
        while (iterator.hasNext()) {
            if (Objects.equal(iterator.next(), o)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    protected boolean standardRemoveAll(final Collection<?> collection) {
        return Iterators.removeAll(this.iterator(), collection);
    }
    
    protected boolean standardRetainAll(final Collection<?> collection) {
        return Iterators.retainAll(this.iterator(), collection);
    }
    
    protected Object[] standardToArray() {
        return this.toArray(new Object[this.size()]);
    }
    
    protected <T> T[] standardToArray(final T[] array) {
        return ObjectArrays.toArrayImpl(this, array);
    }
    
    protected String standardToString() {
        return Collections2.toStringImpl(this);
    }
    
    @Override
    public Object[] toArray() {
        return this.delegate().toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.delegate().toArray(array);
    }
}
