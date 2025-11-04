// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.Iterator;
import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T> implements ListIterator<T>
{
    TransformedListIterator(final ListIterator<? extends F> listIterator) {
        super(listIterator);
    }
    
    private ListIterator<? extends F> backingIterator() {
        return Iterators.cast(this.backingIterator);
    }
    
    @Override
    public void add(final T t) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final boolean hasPrevious() {
        return this.backingIterator().hasPrevious();
    }
    
    @Override
    public final int nextIndex() {
        return this.backingIterator().nextIndex();
    }
    
    @Override
    public final T previous() {
        return this.transform((F)this.backingIterator().previous());
    }
    
    @Override
    public final int previousIndex() {
        return this.backingIterator().previousIndex();
    }
    
    @Override
    public void set(final T t) {
        throw new UnsupportedOperationException();
    }
}
