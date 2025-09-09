package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue<E> extends ForwardingCollection<E> implements Queue<E> {
    protected ForwardingQueue() {
    }

    /* access modifiers changed from: protected */
    public abstract Queue<E> delegate();

    public E element() {
        return delegate().element();
    }

    public boolean offer(E e) {
        return delegate().offer(e);
    }

    public E peek() {
        return delegate().peek();
    }

    public E poll() {
        return delegate().poll();
    }

    public E remove() {
        return delegate().remove();
    }

    /* access modifiers changed from: protected */
    public boolean standardOffer(E e) {
        try {
            return add(e);
        } catch (IllegalStateException e2) {
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public E standardPeek() {
        try {
            return element();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public E standardPoll() {
        try {
            return remove();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
