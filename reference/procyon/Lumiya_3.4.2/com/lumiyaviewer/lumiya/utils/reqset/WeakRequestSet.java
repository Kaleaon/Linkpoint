// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.utils.reqset;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.HashSet;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class WeakRequestSet<T>
{
    private final Object lock;
    private final Map<T, Set<WeakReference<Object>>> requests;
    
    WeakRequestSet() {
        this.requests = new HashMap<T, Set<WeakReference<Object>>>();
        this.lock = new Object();
    }
    
    boolean addRequest(@Nonnull final T t, @Nonnull final Object o) {
        while (true) {
            boolean b = true;
            while (true) {
                Label_0180: {
                    synchronized (this.lock) {
                        Set<WeakReference<Object>> set = this.requests.get(t);
                        if (set == null) {
                            set = new HashSet<WeakReference<Object>>();
                            set.add(new WeakReference<Object>(o));
                            this.requests.put(t, set);
                        }
                        else {
                            final Iterator<WeakReference<Object>> iterator = set.iterator();
                            boolean b2 = false;
                            while (iterator.hasNext()) {
                                final WeakReference weakReference = iterator.next();
                                if (weakReference.get() == null) {
                                    iterator.remove();
                                }
                                else {
                                    if (weakReference.get() != o) {
                                        break Label_0180;
                                    }
                                    b2 = true;
                                }
                            }
                            if (b2) {
                                return false;
                            }
                            set.add(new WeakReference<Object>(o));
                        }
                        return b;
                    }
                    b = false;
                    return b;
                }
                continue;
            }
        }
    }
    
    void completeRequest(@Nonnull final T t) {
        Object o = this.lock;
        synchronized (o) {
            final Set set = this.requests.remove(t);
            monitorexit(o);
            final Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                o = ((WeakReference<Object>)iterator.next()).get();
                if (o != null && o instanceof RequestCompleteListener) {
                    ((RequestCompleteListener<T>)o).onRequestComplete(t);
                }
            }
        }
    }
    
    @Nullable
    T getRequest() {
        Object key;
        while (true) {
            Label_0135: {
                Map.Entry<K, Set> entry;
                while (true) {
                    final Iterator<Map.Entry<T, Set<WeakReference<Object>>>> iterator;
                    synchronized (this.lock) {
                        iterator = this.requests.entrySet().iterator();
                        if (!iterator.hasNext()) {
                            break Label_0135;
                        }
                        entry = iterator.next();
                        final Iterator iterator2 = entry.getValue().iterator();
                        while (iterator2.hasNext()) {
                            if (((WeakReference)iterator2.next()).get() == null) {
                                iterator2.remove();
                            }
                        }
                    }
                    if (entry.getValue().isEmpty()) {
                        iterator.remove();
                        continue;
                    }
                    break;
                }
                key = entry.getKey();
                break;
            }
            key = null;
            break;
        }
        final Object o;
        monitorexit(o);
        return (T)key;
    }
}
