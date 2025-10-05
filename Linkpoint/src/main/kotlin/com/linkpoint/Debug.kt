package com.linkpoint

import android.util.Log
import java.nio.ByteBuffer

class Debug {
    private const val String LOG_TAG = "Linkpoint"

    @JvmStatic
    Unit AlwaysPrintf(String str, Object... objArr) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3]
        String className = stackTraceElement.getClassName()
        Log.d(LOG_TAG, "[" + className.substring(className.lastIndexOf(46) + 1) + "::" + stackTraceElement.getMethodName() + "] " + String.format(str, objArr))
    }

    @JvmStatic
    Unit DumpBuffer(String str, ByteBuffer byteBuffer) {
    }

    @JvmStatic
    Unit DumpBuffer(String str, Byte[] bArr) {
    }

    @JvmStatic
    Unit DumpBuffer(String str, Byte[] bArr, Int i) {
    }

    @JvmStatic
    Unit Log(String str) {
    }

    @JvmStatic
    Unit Printf(String str, Object... objArr) {
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
    Unit Warning(Throwable th) {
    }

    @JvmStatic
    Boolean isDebugBuild() {
        return false
    }
}
