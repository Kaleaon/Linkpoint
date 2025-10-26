package com.lumiyaviewer.lumiya.stub;

import android.content.Context;
import android.util.Log;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

/**
 * Minimal stub Application class to enable APK build.
 * 
 * This replaces the broken LumiyaApp.kt which has compilation errors from poor Java-to-Kotlin migration.
 * Once the 221,328+ Kotlin compilation errors are fixed, this can be replaced with the full implementation.
 */
public class StubLumiyaApp extends MultiDexApplication {
    private static final String TAG = "StubLumiyaApp";
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        Log.i(TAG, "Lumiya stub application started - limited functionality");
        Log.w(TAG, "This is a minimal build - full Second Life viewer features require fixing Kotlin migration errors");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static Context getContext() {
        return appContext;
    }
}
