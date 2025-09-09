package com.lumiyaviewer.lumiya.utils;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class InternPool<T> {
    private final WeakHashMap<T, WeakReference<T>> pool = new WeakHashMap<>();

    public final synchronized T intern(T t) {
        T t2;
        WeakReference weakReference = this.pool.get(t);
        if (weakReference != null && (t2 = weakReference.get()) != null) {
            return t2;
        }
        this.pool.put(t, new WeakReference(t));
        return t;
    }
}
