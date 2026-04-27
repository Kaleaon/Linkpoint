// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract class AbstractIterator<T> extends UnmodifiableIterator<T>
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
            case DONE: {
                return false;
            }
            case READY: {
                return true;
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
    
    public final T peek() {
        if (this.hasNext()) {
            return this.next;
        }
        throw new NoSuchElementException();
    }
    
    private enum State
    {
        DONE, 
        FAILED, 
        NOT_READY, 
        READY;
    }
}
