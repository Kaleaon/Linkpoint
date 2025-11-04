// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class InternPool<T>
{
    private final WeakHashMap<T, WeakReference<T>> pool;
    
    public InternPool() {
        this.pool = new WeakHashMap<T, WeakReference<T>>();
    }
    
    public final T intern(final T referent) {
        synchronized (this) {
            final WeakReference weakReference = this.pool.get(referent);
            if (weakReference != null) {
                final Object value = weakReference.get();
                if (value != null) {
                    return (T)value;
                }
            }
            this.pool.put(referent, new WeakReference<T>(referent));
            return referent;
        }
    }
}
