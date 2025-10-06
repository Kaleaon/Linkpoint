package com.linkpoint.utils.reqset

import java.lang.ref.WeakReference
import java.util.HashMap
import java.util.HashSet
import java.util.Iterator
import java.util.Map
import java.util.Set
import javax.annotation.Nonnull
import javax.annotation.Nullable
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
class WeakRequestSet<T> {
    private val Object lock = Object()
    private val Map<T, Set<WeakReference<Object>>> requests = HashMap()

    WeakRequestSet() {
    }

    /* access modifiers changed from: package-private */
    public Boolean addRequest(T t, Object obj) {
        Boolean z2 = true
        synchronized (this.lock) {
            Set set = this.requests.get(t)
            if (set == null) {
                HashSet hashSet = HashSet()
                hashSet.add(WeakReference(obj))
                this.requests.put(t, hashSet)
            } else {
                Iterator it = set.iterator()
                Boolean z3 = false
                while (it.hasNext()) {
                    WeakReference weakReference = (WeakReference) it.next()
                    if (weakReference.get() == null) {
                        it.remove()
                        z = z3
                    } else {
                        z = weakReference.get() == obj ? true : z3
                    }
                    z3 = z
                }
                if (!z3) {
                    set.add(WeakReference(obj))
                } else {
                    z2 = false
                }
            }
        }
        return z2
    }

    /* access modifiers changed from: package-private */
    fun completeRequest(T t) {
        Set<WeakReference> remove
        synchronized (this.lock) {
            remove = this.requests.remove(t)
        }
        for (WeakReference weakReference : remove) {
            Object obj = weakReference.get()
            if (obj != null && (obj instanceof RequestCompleteListener)) {
                ((RequestCompleteListener) obj).onRequestComplete(t)
            }
        }
    }

    /* access modifiers changed from: package-private */
    public T getRequest() {
        T t
        synchronized (this.lock) {
            Iterator<Map.Entry<T, Set<WeakReference<Object>>>> it = this.requests.entrySet().iterator()
            while (true) {
                if (!it.hasNext()) {
                    t = null
                    break
                }
                Map.Entry next = it.next()
                Iterator it2 = ((Set) next.getValue()).iterator()
                while (it2.hasNext()) {
                    if (((WeakReference) it2.next()).get() == null) {
                        it2.remove()
                    }
                }
                if (!((Set) next.getValue()).isEmpty()) {
                    t = next.getKey()
                    break
                }
                it.remove()
            }
        }
        return t
    }
}
