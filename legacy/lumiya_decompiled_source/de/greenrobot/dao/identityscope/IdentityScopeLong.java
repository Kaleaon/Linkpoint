package de.greenrobot.dao.identityscope;

import de.greenrobot.dao.internal.LongHashMap;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;
/* loaded from: classes.dex */
public class IdentityScopeLong<T> implements IdentityScope<Long, T> {
    private final LongHashMap<Reference<T>> map = new LongHashMap<>();
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

    /* renamed from: detach  reason: avoid collision after fix types in other method */
    public boolean detach2(Long l, T t) {
        this.lock.lock();
        try {
            if (get(l) == t && t != null) {
                remove(l);
                return true;
            }
            return false;
        } finally {
            this.lock.unlock();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public /* bridge */ /* synthetic */ boolean detach(Long l, Object obj) {
        return detach2(l, (Long) obj);
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public T get(Long l) {
        return get2(l.longValue());
    }

    public T get2(long j) {
        this.lock.lock();
        try {
            Reference<T> reference = this.map.get(j);
            if (reference == null) {
                return null;
            }
            return reference.get();
        } finally {
            this.lock.unlock();
        }
    }

    public T get2NoLock(long j) {
        Reference<T> reference = this.map.get(j);
        if (reference == null) {
            return null;
        }
        return reference.get();
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public T getNoLock(Long l) {
        return get2NoLock(l.longValue());
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void lock() {
        this.lock.lock();
    }

    /* renamed from: put  reason: avoid collision after fix types in other method */
    public void put3(Long l, T t) {
        put2(l.longValue(), t);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public /* bridge */ /* synthetic */ void put(Long l, Object obj) {
        put3(l, (Long) obj);
    }

    public void put2(long j, T t) {
        this.lock.lock();
        try {
            this.map.put(j, new WeakReference(t));
        } finally {
            this.lock.unlock();
        }
    }

    public void put2NoLock(long j, T t) {
        this.map.put(j, new WeakReference(t));
    }

    /* renamed from: putNoLock  reason: avoid collision after fix types in other method */
    public void putNoLock2(Long l, T t) {
        put2NoLock(l.longValue(), t);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public /* bridge */ /* synthetic */ void putNoLock(Long l, Object obj) {
        putNoLock2(l, (Long) obj);
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void remove(Iterable<Long> iterable) {
        this.lock.lock();
        try {
            for (Long l : iterable) {
                this.map.remove(l.longValue());
            }
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void remove(Long l) {
        this.lock.lock();
        try {
            this.map.remove(l.longValue());
        } finally {
            this.lock.unlock();
        }
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void reserveRoom(int i) {
        this.map.reserveRoom(i);
    }

    @Override // de.greenrobot.dao.identityscope.IdentityScope
    public void unlock() {
        this.lock.unlock();
    }
}
