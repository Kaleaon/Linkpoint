// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.os.Looper;
import android.os.Handler;

public class ThreadUtils
{
    private static final Handler uiHandler;
    
    static {
        uiHandler = new Handler(Looper.getMainLooper());
    }
    
    public static void assertOnUiThread() {
    }
    
    public static Handler getUiThreadHandler() {
        return ThreadUtils.uiHandler;
    }
    
    public static void postOnUiThread(final Runnable runnable) {
        ThreadUtils.uiHandler.post(runnable);
    }
    
    public static void runOnUiThread(final Runnable runnable) {
        if (!runningOnUiThread()) {
            ThreadUtils.uiHandler.post(runnable);
            return;
        }
        runnable.run();
    }
    
    public static boolean runningOnUiThread() {
        return ThreadUtils.uiHandler.getLooper() == Looper.myLooper();
    }
    
    public static void throwIfNotOnUiThread() {
        if (runningOnUiThread()) {
            return;
        }
        throw new IllegalStateException("Call was not made on the UI thread.");
    }
}
