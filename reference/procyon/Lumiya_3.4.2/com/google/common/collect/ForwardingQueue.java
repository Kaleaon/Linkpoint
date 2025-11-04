// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.NoSuchElementException;
import java.util.Collection;
import com.google.common.annotations.GwtCompatible;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue<E> extends ForwardingCollection<E> implements Queue<E>
{
    protected ForwardingQueue() {
    }
    
    @Override
    protected abstract Queue<E> delegate();
    
    @Override
    public E element() {
        return this.delegate().element();
    }
    
    @Override
    public boolean offer(final E e) {
        return this.delegate().offer(e);
    }
    
    @Override
    public E peek() {
        return this.delegate().peek();
    }
    
    @Override
    public E poll() {
        return this.delegate().poll();
    }
    
    @Override
    public E remove() {
        return this.delegate().remove();
    }
    
    protected boolean standardOffer(final E e) {
        try {
            return this.add(e);
        }
        catch (final IllegalStateException ex) {
            return false;
        }
    }
    
    protected E standardPeek() {
        try {
            return this.element();
        }
        catch (final NoSuchElementException ex) {
            return null;
        }
    }
    
    protected E standardPoll() {
        try {
            return this.remove();
        }
        catch (final NoSuchElementException ex) {
            return null;
        }
    }
}
