// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable = true)
final class ReverseOrdering<T> extends Ordering<T> implements Serializable
{
    private static final long serialVersionUID = 0L;
    final Ordering<? super T> forwardOrder;
    
    ReverseOrdering(final Ordering<? super T> ordering) {
        this.forwardOrder = Preconditions.checkNotNull(ordering);
    }
    
    @Override
    public int compare(final T t, final T t2) {
        return this.forwardOrder.compare((Object)t2, (Object)t);
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        return o == this || (o instanceof ReverseOrdering && this.forwardOrder.equals(((ReverseOrdering)o).forwardOrder));
    }
    
    @Override
    public int hashCode() {
        return -this.forwardOrder.hashCode();
    }
    
    @Override
    public <E extends T> E max(final Iterable<E> iterable) {
        return this.forwardOrder.min(iterable);
    }
    
    @Override
    public <E extends T> E max(final E e, final E e2) {
        return this.forwardOrder.min(e, e2);
    }
    
    @Override
    public <E extends T> E max(final E e, final E e2, final E e3, final E... array) {
        return this.forwardOrder.min(e, e2, e3, array);
    }
    
    @Override
    public <E extends T> E max(final Iterator<E> iterator) {
        return this.forwardOrder.min(iterator);
    }
    
    @Override
    public <E extends T> E min(final Iterable<E> iterable) {
        return this.forwardOrder.max(iterable);
    }
    
    @Override
    public <E extends T> E min(final E e, final E e2) {
        return this.forwardOrder.max(e, e2);
    }
    
    @Override
    public <E extends T> E min(final E e, final E e2, final E e3, final E... array) {
        return this.forwardOrder.max(e, e2, e3, array);
    }
    
    @Override
    public <E extends T> E min(final Iterator<E> iterator) {
        return this.forwardOrder.max(iterator);
    }
    
    @Override
    public <S extends T> Ordering<S> reverse() {
        return (Ordering<S>)this.forwardOrder;
    }
    
    @Override
    public String toString() {
        return this.forwardOrder + ".reverse()";
    }
}
