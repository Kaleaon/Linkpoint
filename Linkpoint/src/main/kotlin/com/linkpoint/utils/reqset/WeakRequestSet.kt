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
     public fun addRequest(T t, obj: Object): Boolean {
        val z2: Boolean = true
        synchronized (this.lock) {
            val set: Set = this.requests.get(t)
            if (set == null) {
                val hashSet: HashSet = HashSet()
                hashSet.add(WeakReference(obj))
                this.requests.put(t, hashSet)
            } else {
                val it: Iterator = set.iterator()
                val z3: Boolean = false
                while (it.hasNext()) {
                    val weakReference: WeakReference = (WeakReference) it.next()
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
            val obj: Object = weakReference.get()
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
                val it2: Iterator = ((Set) next.getValue()).iterator()
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
