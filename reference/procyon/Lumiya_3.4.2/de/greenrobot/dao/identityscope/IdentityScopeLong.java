// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.identityscope;

import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import de.greenrobot.dao.internal.LongHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class IdentityScopeLong<T> implements IdentityScope<Long, T>
{
    private final ReentrantLock lock;
    private final LongHashMap<Reference<T>> map;
    
    public IdentityScopeLong() {
        this.map = new LongHashMap<Reference<T>>();
        this.lock = new ReentrantLock();
    }
    
    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.map.clear();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean detach(final Long n, final T t) {
        this.lock.lock();
        try {
            if (this.get(n) == t && t != null) {
                this.remove(n);
                return true;
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T get(final Long n) {
        return this.get2(n);
    }
    
    public T get2(final long n) {
        this.lock.lock();
        try {
            final Reference reference = this.map.get(n);
            this.lock.unlock();
            if (reference == null) {
                return null;
            }
        }
        finally {
            this.lock.unlock();
        }
        final Reference<T> reference2;
        return reference2.get();
    }
    
    public T get2NoLock(final long n) {
        final Reference reference = this.map.get(n);
        if (reference == null) {
            return null;
        }
        return (T)reference.get();
    }
    
    @Override
    public T getNoLock(final Long n) {
        return this.get2NoLock(n);
    }
    
    @Override
    public void lock() {
        this.lock.lock();
    }
    
    @Override
    public void put(final Long n, final T t) {
        this.put2(n, t);
    }
    
    public void put2(final long n, final T referent) {
        this.lock.lock();
        try {
            this.map.put(n, new WeakReference<T>(referent));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void put2NoLock(final long n, final T referent) {
        this.map.put(n, new WeakReference<T>(referent));
    }
    
    @Override
    public void putNoLock(final Long n, final T t) {
        this.put2NoLock(n, t);
    }
    
    @Override
    public void remove(final Iterable<Long> iterable) {
        this.lock.lock();
        try {
            final Iterator<Long> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.map.remove(iterator.next());
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void remove(final Long n) {
        this.lock.lock();
        try {
            this.map.remove(n);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void reserveRoom(final int n) {
        this.map.reserveRoom(n);
    }
    
    @Override
    public void unlock() {
        this.lock.unlock();
    }
}
