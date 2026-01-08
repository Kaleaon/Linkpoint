package com.linkpoint.utils.reqset

import java.lang.ref.WeakReference
import androidx.annotation.ThreadSafe

@ThreadSafe
internal class WeakRequestSet<T> {
    private val lock = Any()
    private val requests: MutableMap<T, MutableSet<WeakReference<Any>>> = HashMap()

    fun addRequest(t: T, obj: Any): Boolean {
        synchronized(lock) {
            val set = requests[t]
            if (set == null) {
                val hashSet = HashSet<WeakReference<Any>>()
                hashSet.add(WeakReference(obj))
                requests[t] = hashSet
                return true
            } else {
                val iterator = set.iterator()
                var found = false
                while (iterator.hasNext()) {
                    val weakReference = iterator.next()
                    if (weakReference.get() == null) {
                        iterator.remove()
                    } else if (weakReference.get() === obj) {
                        found = true
                    }
                }
                if (!found) {
                    set.add(WeakReference(obj))
                    return true
                }
                return false
            }
        }
    }

    fun completeRequest(t: T) {
        val remove: Set<WeakReference<Any>>
        synchronized(lock) {
            remove = requests.remove(t) ?: return
        }
        for (weakReference in remove) {
            val obj = weakReference.get()
            if (obj != null && obj is RequestCompleteListener<*>) {
                @Suppress("UNCHECKED_CAST")
                (obj as RequestCompleteListener<T>).onRequestComplete(t)
            }
        }
    }

    fun getRequest(): T? {
        synchronized(lock) {
            val iterator = requests.entries.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val valueIterator = next.value.iterator()
                while (valueIterator.hasNext()) {
                    if (valueIterator.next().get() == null) {
                        valueIterator.remove()
                    }
                }
                if (next.value.isNotEmpty()) {
                    return next.key
                }
                iterator.remove()
            }
            return null
        }
    }
}