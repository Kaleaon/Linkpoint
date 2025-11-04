// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.base;

import java.util.NoSuchElementException;
import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
abstract class AbstractIterator<T> implements Iterator<T>
{
    private T next;
    private State state;
    
    protected AbstractIterator() {
        this.state = State.NOT_READY;
    }
    
    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state == State.DONE) {
            return false;
        }
        this.state = State.READY;
        return true;
    }
    
    protected abstract T computeNext();
    
    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }
    
    @Override
    public final boolean hasNext() {
        Preconditions.checkState(this.state != State.FAILED);
        switch (this.state) {
            default: {
                return this.tryToComputeNext();
            }
            case READY: {
                return true;
            }
            case DONE: {
                return false;
            }
        }
    }
    
    @Override
    public final T next() {
        if (this.hasNext()) {
            this.state = State.NOT_READY;
            final T next = this.next;
            this.next = null;
            return next;
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }
    
    private enum State
    {
        DONE, 
        FAILED, 
        NOT_READY, 
        READY;
    }
}
