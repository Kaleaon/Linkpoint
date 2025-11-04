// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya;

import android.app.PendingIntent;
import android.content.Intent;
import android.app.LauncherActivity;
import android.app.AlarmManager;
import android.view.Display;
import android.view.WindowManager;
import android.preference.PreferenceManager;
import android.content.res.AssetManager;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.SharedPreferences;
import android.content.Context;
import android.util.DisplayMetrics;
import android.app.Application;

public class LumiyaApp extends Application
{
    private static DisplayMetrics displayMetrics;
    private static Context mContext;
    private static SharedPreferences prefs;
    
    static {
        LumiyaApp.displayMetrics = new DisplayMetrics();
    }
    
    public static String getAppVersion() {
        try {
            return LumiyaApp.mContext.getPackageManager().getPackageInfo(LumiyaApp.mContext.getPackageName(), 0).versionName;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            return "";
        }
    }
    
    public static AssetManager getAssetManager() {
        if (LumiyaApp.mContext != null) {
            return LumiyaApp.mContext.getAssets();
        }
        return null;
    }
    
    public static Context getContext() {
        return LumiyaApp.mContext;
    }
    
    public static SharedPreferences getDefaultSharedPreferences() {
        if (LumiyaApp.prefs == null) {
            LumiyaApp.prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return LumiyaApp.prefs;
    }
    
    public static boolean isSplitScreenNeeded(final Context context) {
        final String string = getDefaultSharedPreferences().getString("split_screens", "auto");
        if (string.equals("never")) {
            return false;
        }
        if (string.equals("always")) {
            return true;
        }
        final Display defaultDisplay = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
        if (string.equals("landscape")) {
            return defaultDisplay.getWidth() > defaultDisplay.getHeight();
        }
        defaultDisplay.getMetrics(LumiyaApp.displayMetrics);
        final float n = LumiyaApp.displayMetrics.heightPixels / LumiyaApp.displayMetrics.ydpi;
        final float f = LumiyaApp.displayMetrics.widthPixels / LumiyaApp.displayMetrics.xdpi;
        final double sqrt = Math.sqrt(n * n + f * f);
        if (sqrt > 6.5 && f >= 5.0f) {
            final float f2 = defaultDisplay.getWidth() / LumiyaApp.displayMetrics.density;
            Debug.Printf("LumiyaApp: Display width in dp: %f, xInches %.1f, diag %.1f", f2, f, sqrt);
            return f2 >= 1000.0f;
        }
        return false;
    }
    
    public static void restartApp() {
        ((AlarmManager)getContext().getSystemService("alarm")).set(1, System.currentTimeMillis() + 1000L, PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), (Class)LauncherActivity.class), 268435456));
        System.exit(0);
    }
    
    public void onCreate() {
        super.onCreate();
        LumiyaApp.mContext = (Context)this;
        GlobalOptions.getInstance().initialize();
    }
}
