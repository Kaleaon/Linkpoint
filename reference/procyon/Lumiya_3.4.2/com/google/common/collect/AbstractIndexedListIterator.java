// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
abstract class AbstractIndexedListIterator<E> extends UnmodifiableListIterator<E>
{
    private int position;
    private final int size;
    
    protected AbstractIndexedListIterator(final int n) {
        this(n, 0);
    }
    
    protected AbstractIndexedListIterator(final int size, final int position) {
        Preconditions.checkPositionIndex(position, size);
        this.size = size;
        this.position = position;
    }
    
    protected abstract E get(final int p0);
    
    @Override
    public final boolean hasNext() {
        return this.position < this.size;
    }
    
    @Override
    public final boolean hasPrevious() {
        boolean b = false;
        if (this.position > 0) {
            b = true;
        }
        return b;
    }
    
    @Override
    public final E next() {
        if (this.hasNext()) {
            return this.get(this.position++);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public final int nextIndex() {
        return this.position;
    }
    
    @Override
    public final E previous() {
        if (this.hasPrevious()) {
            final int position = this.position - 1;
            this.position = position;
            return this.get(position);
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public final int previousIndex() {
        return this.position - 1;
    }
}
