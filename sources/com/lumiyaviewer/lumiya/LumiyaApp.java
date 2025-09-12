// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.app.AlarmManager;
import android.app.Application;
import android.app.LauncherActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            Debug, GlobalOptions

public class LumiyaApp extends Application
{

    private static DisplayMetrics displayMetrics = new DisplayMetrics();
    private static Context mContext;
    private static SharedPreferences prefs;

    public LumiyaApp()
    {
    }

    public static String getAppVersion()
    {
        String s;
        try
        {
            s = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        }
        catch (android.content.pm.PackageManager.NameNotFoundException namenotfoundexception)
        {
            return "";
        }
        return s;
    }

    public static AssetManager getAssetManager()
    {
        if (mContext != null)
        {
            return mContext.getAssets();
        } else
        {
            return null;
        }
    }

    public static Context getContext()
    {
        return mContext;
    }

    public static SharedPreferences getDefaultSharedPreferences()
    {
        if (prefs == null)
        {
            prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        }
        return prefs;
    }

    public static boolean isSplitScreenNeeded(Context context)
    {
        String s = getDefaultSharedPreferences().getString("split_screens", "auto");
        if (s.equals("never"))
        {
            return false;
        }
        if (s.equals("always"))
        {
            return true;
        }
        context = ((WindowManager)context.getSystemService("window")).getDefaultDisplay();
        if (s.equals("landscape"))
        {
            return context.getWidth() > context.getHeight();
        }
        context.getMetrics(displayMetrics);
        float f1 = (float)displayMetrics.heightPixels / displayMetrics.ydpi;
        float f = (float)displayMetrics.widthPixels / displayMetrics.xdpi;
        double d = Math.sqrt(f1 * f1 + f * f);
        if (d > 6.5D && f >= 5F)
        {
            float f2 = (float)context.getWidth() / displayMetrics.density;
            Debug.Printf("LumiyaApp: Display width in dp: %f, xInches %.1f, diag %.1f", new Object[] {
                Float.valueOf(f2), Float.valueOf(f), Double.valueOf(d)
            });
            return f2 >= 1000F;
        } else
        {
            return false;
        }
    }

    public static void restartApp()
    {
        Object obj = new Intent(getContext(), android/app/LauncherActivity);
        obj = PendingIntent.getActivity(getContext(), 0, ((Intent) (obj)), 0x10000000);
        ((AlarmManager)getContext().getSystemService("alarm")).set(1, System.currentTimeMillis() + 1000L, ((PendingIntent) (obj)));
        System.exit(0);
    }

    public void onCreate()
    {
        super.onCreate();
        mContext = this;
        GlobalOptions.getInstance().initialize();
    }

}
