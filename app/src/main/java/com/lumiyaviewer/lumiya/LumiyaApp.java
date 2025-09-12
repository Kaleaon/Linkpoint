package com.lumiyaviewer.lumiya;

import android.app.AlarmManager;
import android.app.Application;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.lumiyaviewer.lumiya.fixes.ResourceConflictResolver;

/**
 * Main Application class for Lumiya Viewer.
 * 
 * Handles global application state, resource conflict resolution, and system-wide initialization.
 * Updated to use AndroidX libraries and modern Android development practices.
 * Extends MultiDexApplication to support large applications with 64K+ methods.
 */
public class LumiyaApp extends MultiDexApplication {
    private static final String TAG = "LumiyaApp";
    private static DisplayMetrics displayMetrics = new DisplayMetrics();
    private static Context mContext;
    private static SharedPreferences prefs;

    public static String getAppVersion() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Could not get app version", e);
            return "";
        }
    }

    public static AssetManager getAssetManager() {
        return mContext != null ? mContext.getAssets() : null;
    }

    public static Context getContext() {
        return mContext;
    }

    public static SharedPreferences getDefaultSharedPreferences() {
        if (prefs == null) {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return prefs;
    }

    public static boolean isSplitScreenNeeded(Context context) {
        String string = getDefaultSharedPreferences().getString("split_screens", "auto");
        if (string.equals("never")) {
            return false;
        }
        if (string.equals("always")) {
            return true;
        }
        Display defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (string.equals("landscape")) {
            return defaultDisplay.getWidth() > defaultDisplay.getHeight();
        } else {
            defaultDisplay.getMetrics(displayMetrics);
            float f = ((float) displayMetrics.heightPixels) / displayMetrics.ydpi;
            float f2 = ((float) displayMetrics.widthPixels) / displayMetrics.xdpi;
            double diagonalInches = Math.sqrt((double) ((f * f) + (f2 * f2)));
            if (diagonalInches <= 6.5d || f2 < 5.0f) {
                return false;
            }
            Debug.Printf("LumiyaApp: Display width in dp: %f, xInches %.1f, diag %.1f", 
                ((float) defaultDisplay.getWidth()) / displayMetrics.density, f2, diagonalInches);
            return ((float) defaultDisplay.getWidth()) / displayMetrics.density >= 1000.0f;
        }
    }

    public static void restartApp() {
        try {
            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, 
                new Intent(getContext(), LauncherActivity.class), 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
            System.exit(0);
        } catch (Exception e) {
            Log.e(TAG, "Failed to restart app", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.i(TAG, "Lumiya Application starting up");
        
        // Set global context
        mContext = this;
        
        // CRITICAL: Initialize resource conflict resolver before any other initialization
        // This fixes build-breaking resource conflicts between AndroidX and support libraries
        ResourceConflictResolver.initialize(this);
        
        // Initialize global options after resource conflicts are resolved
        GlobalOptions.getInstance().initialize();
        
        Log.i(TAG, "Lumiya Application initialization complete");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
