// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.Queue;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import com.google.common.collect.ForwardingQueue;

public abstract class ForwardingBlockingQueue<E> extends ForwardingQueue<E> implements BlockingQueue<E>
{
    protected ForwardingBlockingQueue() {
    }
    
    @Override
    protected abstract BlockingQueue<E> delegate();
    
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
    public E poll(final long n, final TimeUnit timeUnit) throws InterruptedException {
        return this.delegate().poll(n, timeUnit);
    }
    
    @Override
    public void put(final E e) throws InterruptedException {
        this.delegate().put(e);
    }
    
    @Override
    public int remainingCapacity() {
        return this.delegate().remainingCapacity();
    }
    
    @Override
    public E take() throws InterruptedException {
        return this.delegate().take();
    }
}
