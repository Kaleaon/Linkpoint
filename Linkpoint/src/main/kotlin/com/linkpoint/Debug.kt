package com.linkpoint

import android.util.Log
import java.nio.ByteBuffer

class Debug {
    private const val LOG_TAG: String = "Linkpoint"

    @JvmStatic
    fun AlwaysPrintf(str: String, vararg objArr: Any) {
        val stackTraceElement: StackTraceElement = Thread.currentThread().getStackTrace()[3]
        val className: String = stackTraceElement.getClassName()
        Log.d(LOG_TAG, "[" + className.substring(className.lastIndexOf(46) + 1) + "::" + stackTraceElement.getMethodName() + "] " + String.format(str, objArr))
    }

    @JvmStatic
    fun DumpBuffer(str: String, byteBuffer: ByteBuffer) {
    }

    @JvmStatic
    fun DumpBuffer(str: String, bArr: ByteArray) {
    }

    @JvmStatic
    fun DumpBuffer(str: String, bArr: ByteArray, i: Int) {
    }

    @JvmStatic
    fun Log(str: String) {
    }

    @JvmStatic
    fun Printf(str: String, vararg objArr: Any) {
        try {
            if (objArr == null || objArr.length == 0) {
                Log.d(LOG_TAG, str)
            } else {
                Log.d(LOG_TAG, String.format(str, objArr))
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, str + " (format error: " + e.getMessage() + ")")
        }
    }

    @JvmStatic
    fun Warning(th: Throwable) {
    }

    @JvmStatic
     fun isDebugBuild(): Boolean {
        return false
    }
}
