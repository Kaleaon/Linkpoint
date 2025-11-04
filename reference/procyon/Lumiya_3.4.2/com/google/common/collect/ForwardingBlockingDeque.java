// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.collect;

import java.util.concurrent.TimeUnit;
import java.util.Queue;
import java.util.Deque;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;

public abstract class ForwardingBlockingDeque<E> extends ForwardingDeque<E> implements BlockingDeque<E>
{
    protected ForwardingBlockingDeque() {
    }
    
    @Override
    protected abstract BlockingDeque<E> delegate();
    
    @Override
    public int drainTo(final Collection<? super E> collection) {
        return this.delegate().drainTo(collection);
    }
    
    @Override
    public int drainTo(final Collection<? super E> collection, final int n) {
        return this.delegate().drainTo(collection, n);
    }
    
    @Override
    public boolean offer(final E e, final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().offer(e, n, timeUnit);
    }
    
    @Override
    public boolean offerFirst(final E e, final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().offerFirst(e, n, timeUnit);
    }
    
    @Override
    public boolean offerLast(final E e, final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().offerLast(e, n, timeUnit);
    }
    
    @Override
    public E poll(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().poll(n, timeUnit);
    }
    
    @Override
    public E pollFirst(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().pollFirst(n, timeUnit);
    }
    
    @Override
    public E pollLast(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().pollLast(n, timeUnit);
    }
    
    @Override
    public void put(final E e) throws InterruptedException {
        this.delegate().put(e);
    }
    
    @Override
    public void putFirst(final E e) throws InterruptedException {
        this.delegate().putFirst(e);
    }
    
    @Override
    public void putLast(final E e) throws InterruptedException {
        this.delegate().putLast(e);
    }
    
    @Override
    public int remainingCapacity() {
        return this.delegate().remainingCapacity();
    }
    
    @Override
    public E take() throws InterruptedException {
        return this.delegate().take();
    }
    
    @Override
    public E takeFirst() throws InterruptedException {
        return this.delegate().takeFirst();
    }
    
    @Override
    public E takeLast() throws InterruptedException {
        return this.delegate().takeLast();
    }
}
