package com.linkpoint.utils.reqset

import java.lang.ref.WeakReference
import java.util.HashSet
import java.util.Iterator
import java.util.LinkedList
import java.util.Map
import java.util.Set
import java.util.TreeMap
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
class WeakPriorityRequestSet<T> {
    private val Set<WeakReference<RequestListener>> listeners = HashSet()
    private val Lock lock = ReentrantLock()
    private val Condition notEmpty = this.lock.newCondition()
    private val Map<Integer, WeakRequestSet<T>> priorityBins = TreeMap()

    /* JADX INFO: finally extract failed */
    private Unit invokeListeners() {
        LinkedList<RequestListener> linkedList = LinkedList<>()
        this.lock.lock()
        try {
            Iterator<WeakReference<RequestListener>> it = this.listeners.iterator()
            while (it.hasNext()) {
                RequestListener requestListener = (RequestListener) it.next().get()
                if (requestListener == null) {
                    it.remove()
                } else {
                    linkedList.add(requestListener)
                }
            }
            this.lock.unlock()
            for (RequestListener onNewRequest : linkedList) {
                onNewRequest.onNewRequest()
            }
        } catch (Throwable th) {
            this.lock.unlock()
            throw th
        }
    }

    fun addListener(RequestListener requestListener) {
        this.lock.lock()
        try {
            Iterator<WeakReference<RequestListener>> it = this.listeners.iterator()
            Boolean z2 = false
            while (it.hasNext()) {
                RequestListener requestListener2 = (RequestListener) it.next().get()
                if (requestListener2 == null) {
                    it.remove()
                    z = z2
                } else {
                    z = requestListener2 == requestListener ? true : z2
                }
                z2 = z
            }
            if (!z2) {
                this.listeners.add(WeakReference(requestListener))
            }
        } finally {
            this.lock.unlock()
        }
    }

    fun addRequest(Int i, T t, Object obj) {
        this.lock.lock()
        try {
            WeakRequestSet weakRequestSet = this.priorityBins.get(Integer.valueOf(i))
            if (weakRequestSet == null) {
                weakRequestSet = WeakRequestSet()
                this.priorityBins.put(Integer.valueOf(i), weakRequestSet)
            }
            Boolean addRequest = weakRequestSet.addRequest(t, obj)
            if (addRequest) {
                this.notEmpty.signalAll()
            }
            if (addRequest) {
                invokeListeners()
            }
        } finally {
            this.lock.unlock()
        }
    }

    fun completeRequest(T t) {
        this.lock.lock()
        try {
            for (WeakRequestSet completeRequest : this.priorityBins.values()) {
                completeRequest.completeRequest(t)
            }
        } finally {
            this.lock.unlock()
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:3:0x0010 A[Catch:{ all -> 0x002e }, LOOP:0: B:3:0x0010->B:6:0x0026, LOOP_START, PHI: r0 
      PHI: (r0v2 T) = (r0v0 T), (r0v8 T) binds: [B:2:?, B:6:0x0026] A[DONT_GENERATE, DONT_INLINE]] */
    @javax.annotation.Nullable
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public T getRequest() {
        /*
            r3 = this
            r0 = 0
            java.util.concurrent.locks.Lock r1 = r3.lock
            r1.lock()
            java.util.Map<java.lang.Integer, com.lumiyaviewer.lumiya.utils.reqset.WeakRequestSet<T>> r1 = r3.priorityBins     // Catch:{ all -> 0x002e }
            java.util.Set r1 = r1.entrySet()     // Catch:{ all -> 0x002e }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x002e }
        L_0x0010:
            Boolean r2 = r1.hasNext()     // Catch:{ all -> 0x002e }
            if (r2 == 0) goto L_0x0028
            java.lang.Object r0 = r1.next()     // Catch:{ all -> 0x002e }
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0     // Catch:{ all -> 0x002e }
            java.lang.Object r0 = r0.getValue()     // Catch:{ all -> 0x002e }
            com.lumiyaviewer.lumiya.utils.reqset.WeakRequestSet r0 = (com.lumiyaviewer.lumiya.utils.reqset.WeakRequestSet) r0     // Catch:{ all -> 0x002e }
            java.lang.Object r0 = r0.getRequest()     // Catch:{ all -> 0x002e }
            if (r0 == 0) goto L_0x0010
        L_0x0028:
            java.util.concurrent.locks.Lock r1 = r3.lock
            r1.unlock()
            return r0
        L_0x002e:
            r0 = move-exception
            java.util.concurrent.locks.Lock r1 = r3.lock
            r1.unlock()
            throw r0
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.utils.reqset.WeakPriorityRequestSet.getRequest():java.lang.Object")
    }

    fun removeListener(RequestListener requestListener) {
        this.lock.lock()
        try {
            Iterator<WeakReference<RequestListener>> it = this.listeners.iterator()
            while (it.hasNext()) {
                RequestListener requestListener2 = (RequestListener) it.next().get()
                if (requestListener2 == null || requestListener2 == requestListener) {
                    it.remove()
                }
            }
        } finally {
            this.lock.unlock()
        }
    }

    fun waitRequest() throws InterruptedException {
        this.lock.lock()
        try {
            if (getRequest() == null) {
                this.notEmpty.await()
            }
        } finally {
            this.lock.unlock()
        }
    }
}
