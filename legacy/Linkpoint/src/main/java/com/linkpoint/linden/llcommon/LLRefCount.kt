package com.linkpoint.linden.llcommon

import java.util.concurrent.atomic.AtomicInteger

abstract class RefCounted(initialRef: Int = 0) {
    private val mRef = AtomicInteger(initialRef)

    fun ref() {
        mRef.incrementAndGet()
    }

    fun unref(): Int {
        val count = mRef.decrementAndGet()
        if (count == 0) {
            deleteThis()
        }
        return count
    }

    fun getNumRefs(): Int = mRef.get()

    protected open fun deleteThis() {}
}

abstract class ThreadSafeRefCounted {
    private val mRef = AtomicInteger(0)

    fun ref() {
        mRef.incrementAndGet()
    }

    fun unref() {
        if (mRef.decrementAndGet() == 0) {
            deleteThis()
        }
    }

    fun getNumRefs(): Int = mRef.get()

    protected open fun deleteThis() {}
}
