package com.lumiyaviewer.lumiya;

import android.app.AlarmManager;
import android.app.Application;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class LumiyaApp extends Application {
    private static DisplayMetrics displayMetrics = new DisplayMetrics();
    private static Context mContext;
    private static SharedPreferences prefs;

    public static String getAppVersion() {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static AssetManager getAssetManager() {
        if (mContext != null) {
            return mContext.getAssets();
        }
        return null;
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
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        if (string.equals("landscape")) {
            return defaultDisplay.getWidth() > defaultDisplay.getHeight();
        }
        defaultDisplay.getMetrics(displayMetrics);
        float f = ((float) displayMetrics.heightPixels) / displayMetrics.ydpi;
        float f2 = ((float) displayMetrics.widthPixels) / displayMetrics.xdpi;
        double sqrt = Math.sqrt((double) ((f * f) + (f2 * f2)));
        if (sqrt <= 6.5d || f2 < 5.0f) {
            return false;
        }
        float width = ((float) defaultDisplay.getWidth()) / displayMetrics.density;
        Debug.Printf("LumiyaApp: Display width in dp: %f, xInches %.1f, diag %.1f", Float.valueOf(width), Float.valueOf(f2), Double.valueOf(sqrt));
        return width >= 1000.0f;
    }

    public static void restartApp() {
        ((AlarmManager) getContext().getSystemService(NotificationCompat.CATEGORY_ALARM)).set(1, System.currentTimeMillis() + 1000, PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), LauncherActivity.class), 268435456));
        System.exit(0);
    }

    public void onCreate() {
        super.onCreate();
        mContext = this;
        GlobalOptions.getInstance().initialize();
    }
}
