package com.lumiyaviewer.lumiya;

import android.util.Log;
import java.nio.ByteBuffer;

public class Debug {
    private static final String LOG_TAG = "Lumiya";

    public static void AlwaysPrintf(String str, Object... objArr) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String className = stackTraceElement.getClassName();
        Log.d(LOG_TAG, "[" + className.substring(className.lastIndexOf(46) + 1) + "::" + stackTraceElement.getMethodName() + "] " + String.format(str, objArr));
    }

    public static void DumpBuffer(String str, ByteBuffer byteBuffer) {
    }

    public static void DumpBuffer(String str, byte[] bArr) {
    }

    public static void DumpBuffer(String str, byte[] bArr, int i) {
    }

    public static void Log(String str) {
    }

    public static void Printf(String str, Object... objArr) {
    }

    public static void Warning(Throwable th) {
    }

    public static boolean isDebugBuild() {
        return false;
    }
}
