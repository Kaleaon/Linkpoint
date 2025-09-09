package com.google.vr.cardboard;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Handler uiHandler = new Handler(Looper.getMainLooper());

    public static void assertOnUiThread() {
    }

    public static Handler getUiThreadHandler() {
        return uiHandler;
    }

    public static void postOnUiThread(Runnable runnable) {
        uiHandler.post(runnable);
    }

    public static void runOnUiThread(Runnable runnable) {
        if (!runningOnUiThread()) {
            uiHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public static boolean runningOnUiThread() {
        return uiHandler.getLooper() == Looper.myLooper();
    }

    public static void throwIfNotOnUiThread() {
        if (!runningOnUiThread()) {
            throw new IllegalStateException("Call was not made on the UI thread.");
        }
    }
}
