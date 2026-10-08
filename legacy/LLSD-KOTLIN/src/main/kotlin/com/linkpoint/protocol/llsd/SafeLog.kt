package com.linkpoint.protocol.llsd

internal object SafeLog {
    private val logClass: Class<*>? = runCatching { Class.forName("android.util.Log") }.getOrNull()
    private val methodW = runCatching { logClass?.getMethod("w", String::class.java, String::class.java, Throwable::class.java) }.getOrNull()
    private val methodE = runCatching { logClass?.getMethod("e", String::class.java, String::class.java, Throwable::class.java) }.getOrNull()
    private val methodD = runCatching { logClass?.getMethod("d", String::class.java, String::class.java) }.getOrNull()

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        val m = methodW
        if (m != null) {
            val res = runCatching { m.invoke(null, tag, message, throwable) }
            if (res.isSuccess) return
        }
        if (throwable != null) {
            System.err.println("[$tag] WARNING: $message: ${throwable.message}")
        } else {
            System.err.println("[$tag] WARNING: $message")
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val m = methodE
        if (m != null) {
            val res = runCatching { m.invoke(null, tag, message, throwable) }
            if (res.isSuccess) return
        }
        if (throwable != null) {
            System.err.println("[$tag] ERROR: $message: ${throwable.message}")
        } else {
            System.err.println("[$tag] ERROR: $message")
        }
    }

    fun d(tag: String, message: String) {
        val m = methodD
        if (m != null) {
            val res = runCatching { m.invoke(null, tag, message) }
            if (res.isSuccess) return
        }
        println("[$tag] DEBUG: $message")
    }
}
