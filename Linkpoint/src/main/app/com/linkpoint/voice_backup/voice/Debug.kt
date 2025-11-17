/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 */
package com.linkpoint.voice

import android.util.Log

class Debug {
    private val LOG_TAG: String = "LumiyaVoice"

    Unit AlwaysPrintf(String string2, Any ... objectArray) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3]
        String string3 = stackTraceElement.getClassName()
        string3 = string3.substring(string3.lastIndexOf(46) + 1)
        Log.d((String)LOG_TAG, (String)("[" + string3 + "::" + stackTraceElement.getMethodName() + "] " + String.format(string2, objectArray)))
    }

    Unit Log(String string2) {
    }

    Unit Printf(String string2, Any ... objectArray) {
    }

    Unit Warning(Throwable throwable) {
    }

    Boolean isDebugBuild() {
        return false
    }
}

