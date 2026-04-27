// 
// Decompiled by Procyon v0.6.0
// 

package de.greenrobot.dao.identityscope;

public interface IdentityScope<K, T>
{
    void clear();
    
    boolean detach(final K p0, final T p1);
    
    T get(final K p0);
    
    T getNoLock(final K p0);
    
    void lock();
    
    void put(final K p0, final T p1);
    
    void putNoLock(final K p0, final T p1);
    
    void remove(final Iterable<K> p0);
    
    void remove(final K p0);
    
    void reserveRoom(final int p0);
    
    void unlock();
}
