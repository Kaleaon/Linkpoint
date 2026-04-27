// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.List;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible(emulated = true, serializable = true)
final class SingletonImmutableList<E> extends ImmutableList<E>
{
    final transient E element;
    
    SingletonImmutableList(final E e) {
        this.element = Preconditions.checkNotNull(e);
    }
    
    @Override
    public E get(final int n) {
        Preconditions.checkElementIndex(n, 1);
        return this.element;
    }
    
    @Override
    boolean isPartialView() {
        return false;
    }
    
    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public ImmutableList<E> subList(final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n2, 1);
        ImmutableList<Object> of;
        if (n != n2) {
            of = (ImmutableList<Object>)this;
        }
        else {
            of = ImmutableList.of();
        }
        return (ImmutableList<E>)of;
    }
    
    @Override
    public String toString() {
        final String string = this.element.toString();
        return new StringBuilder(string.length() + 2).append('[').append(string).append(']').toString();
    }
}
