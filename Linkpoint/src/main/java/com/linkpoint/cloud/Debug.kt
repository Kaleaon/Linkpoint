package com.linkpoint.cloud

import android.util.Log

object Debug {
    private const val LOG_TAG = "LumiyaCloud"

    @JvmStatic
    fun AlwaysPrintf(format: String, vararg args: Any?) {
        val stackTrace = Thread.currentThread().stackTrace[3]
        val className = stackTrace.className.substringAfterLast('.')
        Log.d(LOG_TAG, "[${className}::${stackTrace.methodName}] ${String.format(format, *args)}")
    }

    @JvmStatic
    fun Log(message: String) {}

    @JvmStatic
    fun Printf(format: String, vararg args: Any?) {}

    @JvmStatic
    fun Warning(throwable: Throwable) {}

    @JvmStatic
    fun isDebugBuild(): Boolean = false
}