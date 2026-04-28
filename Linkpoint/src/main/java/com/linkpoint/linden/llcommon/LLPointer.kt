package com.linkpoint.linden.llcommon

import java.lang.ref.WeakReference

class LLPointer<T : Any>(target: T? = null) {
    private var strong: T? = target
    private var weak: WeakReference<T>? = null

    fun set(value: T?) { strong = value; weak = null }
    fun setWeak(value: T?) { strong = null; weak = value?.let { WeakReference(it) } }

    fun get(): T? = strong ?: weak?.get()
    fun isNull(): Boolean = get() == null
    fun notNull(): Boolean = get() != null

    operator fun invoke(): T? = get()

    fun release() { strong = null; weak = null }
}

fun <T : Any> llPointerOf(value: T): LLPointer<T> = LLPointer(value)
fun <T : Any> llWeakPointerOf(value: T): LLPointer<T> = LLPointer<T>().also { it.setWeak(value) }
