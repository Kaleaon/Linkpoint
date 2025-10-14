package com.lumiyaviewer.lumiya.utils.reqset

import java.lang.ref.WeakReference
import java.util.TreeMap
import java.util.concurrent.locks.ReentrantLock
import javax.annotation.concurrent.ThreadSafe
import kotlin.concurrent.withLock

@ThreadSafe
class WeakPriorityRequestSet<T> {
    private val listeners: MutableSet<WeakReference<RequestListener>> = HashSet()
    private val lock = ReentrantLock()
    private val notEmpty = lock.newCondition()
    private val priorityBins: MutableMap<Int, WeakRequestSet<T>> = TreeMap()

    private fun invokeListeners() {
        val linkedList = mutableListOf<RequestListener>()
        lock.withLock {
            val iterator = listeners.iterator()
            while (iterator.hasNext()) {
                val requestListener = iterator.next().get()
                if (requestListener == null) {
                    iterator.remove()
                } else {
                    linkedList.add(requestListener)
                }
            }
        }
        for (requestListener in linkedList) {
            requestListener.onNewRequest()
        }
    }

    fun addListener(requestListener: RequestListener) {
        lock.withLock {
            val iterator = listeners.iterator()
            var found = false
            while (iterator.hasNext()) {
                val listener = iterator.next().get()
                if (listener == null) {
                    iterator.remove()
                } else if (listener === requestListener) {
                    found = true
                }
            }
            if (!found) {
                listeners.add(WeakReference(requestListener))
            }
        }
    }

    fun addRequest(priority: Int, t: T, obj: Any) {
        lock.withLock {
            var weakRequestSet = priorityBins[priority]
            if (weakRequestSet == null) {
                weakRequestSet = WeakRequestSet()
                priorityBins[priority] = weakRequestSet
            }
            val addRequest = weakRequestSet.addRequest(t, obj)
            if (addRequest) {
                notEmpty.signalAll()
            }
            if (addRequest) {
                invokeListeners()
            }
        }
    }

    fun completeRequest(t: T) {
        lock.withLock {
            for (weakRequestSet in priorityBins.values) {
                weakRequestSet.completeRequest(t)
            }
        }
    }

    fun getRequest(): T? {
        lock.withLock {
            for (weakRequestSet in priorityBins.values) {
                val request = weakRequestSet.getRequest()
                if (request != null) {
                    return request
                }
            }
            return null
        }
    }

    fun waitForRequest(): T? {
        lock.withLock {
            while (true) {
                val request = getRequest()
                if (request != null) {
                    return request
                }
                notEmpty.await()
            }
        }
    }
}