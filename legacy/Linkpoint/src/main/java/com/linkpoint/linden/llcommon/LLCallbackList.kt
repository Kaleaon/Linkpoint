package com.linkpoint.linden.llcommon

/**
 * Ordered list of callbacks, preserving insertion order.
 * Equivalent to LLCallbackList.  Each callback is a distinct () -> Unit lambda;
 * identity-based deduplication is intentionally not enforced for lambdas —
 * callers are responsible for not adding the same logical callback twice,
 * matching C++ raw-function-pointer behaviour.
 */
class CallbackList {
    private val mCallbackList: MutableList<() -> Unit> = mutableListOf()

    fun addFunction(fn: () -> Unit): Boolean {
        mCallbackList.add(fn)
        return true
    }

    fun removeFunction(fn: () -> Unit): Boolean = mCallbackList.remove(fn)

    fun containsFunction(fn: () -> Unit): Boolean = fn in mCallbackList

    fun callFunctions() {
        val snapshot = mCallbackList.toList()
        for (fn in snapshot) fn()
    }

    fun deleteAllFunctions() {
        mCallbackList.clear()
    }
}

val gIdleCallbacks = CallbackList()

fun doOnIdleOneTime(callable: () -> Unit) {
    var wrapper: (() -> Unit)? = null
    wrapper = {
        gIdleCallbacks.removeFunction(wrapper!!)
        callable()
    }
    gIdleCallbacks.addFunction(wrapper)
}

fun doOnIdleRepeating(callable: () -> Boolean) {
    var wrapper: (() -> Unit)? = null
    wrapper = {
        if (callable()) {
            gIdleCallbacks.removeFunction(wrapper!!)
        }
    }
    gIdleCallbacks.addFunction(wrapper)
}
