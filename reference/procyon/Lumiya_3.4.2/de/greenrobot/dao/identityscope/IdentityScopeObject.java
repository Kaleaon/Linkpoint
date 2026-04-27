// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.identityscope;

import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class IdentityScopeObject<K, T> implements IdentityScope<K, T>
{
    private final ReentrantLock lock;
    private final HashMap<K, Reference<T>> map;
    
    public IdentityScopeObject() {
        this.map = new HashMap<K, Reference<T>>();
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
    public boolean detach(final K k, final T t) {
        this.lock.lock();
        try {
            if (this.get(k) == t && t != null) {
                this.remove(k);
                return true;
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public T get(final K key) {
        this.lock.lock();
        try {
            final Reference reference = this.map.get(key);
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
    
    @Override
    public T getNoLock(final K key) {
        final Reference reference = this.map.get(key);
        if (reference == null) {
            return null;
        }
        return (T)reference.get();
    }
    
    @Override
    public void lock() {
        this.lock.lock();
    }
    
    @Override
    public void put(final K key, final T referent) {
        this.lock.lock();
        try {
            this.map.put(key, new WeakReference<T>(referent));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void putNoLock(final K key, final T referent) {
        this.map.put(key, new WeakReference<T>(referent));
    }
    
    @Override
    public void remove(final Iterable<K> iterable) {
        this.lock.lock();
        try {
            final Iterator<K> iterator = iterable.iterator();
            while (iterator.hasNext()) {
                this.map.remove(iterator.next());
            }
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void remove(final K key) {
        this.lock.lock();
        try {
            this.map.remove(key);
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public void reserveRoom(final int n) {
    }
    
    @Override
    public void unlock() {
        this.lock.unlock();
    }
}
