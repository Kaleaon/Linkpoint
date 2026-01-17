package de.greenrobot.dao.identityscope;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
/* loaded from: classes.dex */
public class IdentityScopeObject<K, T> implements IdentityScope<K, T> {
    private final HashMap<K, Reference<T>> map = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void clear() {
        this.lock.lock();
        try {
            this.map.clear();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public boolean detach(K k, T t) {
        this.lock.lock();
        try {
            if (get(k) == t && t != null) {
                remove((IdentityScopeObject<K, T>) k);
                return true;
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public T get(K k) {
        this.lock.lock();
        try {
            Reference<T> reference = this.map.get(k);
            if (reference == null) {
                return null;
            }
            return reference.get();
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public T getNoLock(K k) {
        Reference<T> reference = this.map.get(k);
        if (reference == null) {
            return null;
        }
        return reference.get();
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void lock() {
        this.lock.lock();
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void put(K k, T t) {
        this.lock.lock();
        try {
            this.map.put(k, new WeakReference(t));
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void putNoLock(K k, T t) {
        this.map.put(k, new WeakReference(t));
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void remove(Iterable<K> iterable) {
        this.lock.lock();
        try {
            for (K k : iterable) {
                this.map.remove(k);
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void remove(K k) {
        this.lock.lock();
        try {
            this.map.remove(k);
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void reserveRoom(int i) {
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void unlock() {
        this.lock.unlock();
    }
}
