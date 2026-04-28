package com.linkpoint.linden.llcommon

import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicLong

class LLHandle<T : Any> private constructor(
    private val ref: WeakReference<T>,
    val id: Long
) {
    fun get(): T? = ref.get()
    fun isValid(): Boolean = ref.get() != null

    companion object {
        private val nextId = AtomicLong(1L)
        fun <T : Any> of(target: T): LLHandle<T> =
            LLHandle(WeakReference(target), nextId.getAndIncrement())
        fun <T : Any> dead(): LLHandle<T> =
            LLHandle(WeakReference<T>(null as T?), 0L)
    }
}

abstract class LLHandleProvider<T : Any> {
    @Volatile private var handle: LLHandle<T>? = null

    @Suppress("UNCHECKED_CAST")
    fun getHandle(): LLHandle<T> {
        return handle ?: synchronized(this) {
            handle ?: LLHandle.of(this as T).also { handle = it }
        }
    }
}
