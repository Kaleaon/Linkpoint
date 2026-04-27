/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.lumiyaviewer.lumiya.cloud;

import android.util.Log;

public class Debug {
    private static final String LOG_TAG = "LumiyaCloud";

    public static void AlwaysPrintf(String string2, Object ... objectArray) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String string3 = stackTraceElement.getClassName();
        string3 = string3.substring(string3.lastIndexOf(46) + 1);
        Log.d((String)LOG_TAG, (String)("[" + string3 + "::" + stackTraceElement.getMethodName() + "] " + String.format(string2, objectArray)));
    }

    public static void Log(String string2) {
    }

    public static void Printf(String string2, Object ... objectArray) {
    }

    public static void Warning(Throwable throwable) {
    }

    public static boolean isDebugBuild() {
        return false;
    }
}

