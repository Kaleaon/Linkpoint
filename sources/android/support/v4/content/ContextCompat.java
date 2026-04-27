package android.support.v4.content;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import java.io.File;

public class ContextCompat {
    private static final String TAG = "ContextCompat";
    private static final Object sLock = new Object();
    private static TypedValue sTempValue;

    protected ContextCompat() {
    }

    private static File buildPath(File file, String... strArr) {
        File file2 = file;
        for (String str : strArr) {
            if (file2 == null) {
                file2 = new File(str);
            } else if (str != null) {
                file2 = new File(file2, str);
            }
        }
        return file2;
    }

    public static int checkSelfPermission(@NonNull Context context, @NonNull String str) {
        if (str != null) {
            return context.checkPermission(str, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");
    }

    public static Context createDeviceProtectedStorageContext(Context context) {
        if (Build.VERSION.SDK_INT < 24) {
            return null;
        }
        return context.createDeviceProtectedStorageContext();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x000a, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static synchronized java.io.File createFilesDir(java.io.File r3) {
        /*
            java.lang.Class<android.support.v4.content.ContextCompat> r1 = android.support.v4.content.ContextCompat.class
            monitor-enter(r1)
            boolean r0 = r3.exists()     // Catch:{ all -> 0x003a }
            if (r0 == 0) goto L_0x000b
        L_0x0009:
            monitor-exit(r1)
            return r3
        L_0x000b:
            boolean r0 = r3.mkdirs()     // Catch:{ all -> 0x003a }
            if (r0 != 0) goto L_0x0009
            boolean r0 = r3.exists()     // Catch:{ all -> 0x003a }
            if (r0 != 0) goto L_0x0038
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x003a }
            r0.<init>()     // Catch:{ all -> 0x003a }
            java.lang.String r2 = "Unable to create files subdir "
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ all -> 0x003a }
            java.lang.String r2 = r3.getPath()     // Catch:{ all -> 0x003a }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ all -> 0x003a }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x003a }
            java.lang.String r2 = "ContextCompat"
            android.util.Log.w(r2, r0)     // Catch:{ all -> 0x003a }
            r0 = 0
            monitor-exit(r1)
            return r0
        L_0x0038:
            monitor-exit(r1)
            return r3
        L_0x003a:
            r0 = move-exception
            monitor-exit(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.content.ContextCompat.createFilesDir(java.io.File):java.io.File");
    }

    public static File getCodeCacheDir(Context context) {
        return Build.VERSION.SDK_INT < 21 ? createFilesDir(new File(context.getApplicationInfo().dataDir, "code_cache")) : context.getCodeCacheDir();
    }

    @ColorInt
    public static final int getColor(Context context, @ColorRes int i) {
        return Build.VERSION.SDK_INT < 23 ? context.getResources().getColor(i) : context.getColor(i);
    }

    public static final ColorStateList getColorStateList(Context context, @ColorRes int i) {
        return Build.VERSION.SDK_INT < 23 ? context.getResources().getColorStateList(i) : context.getColorStateList(i);
    }

    public static File getDataDir(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            return context.getDataDir();
        }
        String str = context.getApplicationInfo().dataDir;
        if (str == null) {
            return null;
        }
        return new File(str);
    }

    public static final Drawable getDrawable(Context context, @DrawableRes int i) {
        int i2;
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getDrawable(i);
        }
        if (Build.VERSION.SDK_INT >= 16) {
            return context.getResources().getDrawable(i);
        }
        synchronized (sLock) {
            if (sTempValue == null) {
                sTempValue = new TypedValue();
            }
            context.getResources().getValue(i, sTempValue, true);
            i2 = sTempValue.resourceId;
        }
        return context.getResources().getDrawable(i2);
    }

    public static File[] getExternalCacheDirs(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return context.getExternalCacheDirs();
        }
        return new File[]{context.getExternalCacheDir()};
    }

    public static File[] getExternalFilesDirs(Context context, String str) {
        if (Build.VERSION.SDK_INT >= 19) {
            return context.getExternalFilesDirs(str);
        }
        return new File[]{context.getExternalFilesDir(str)};
    }

    public static final File getNoBackupFilesDir(Context context) {
        return Build.VERSION.SDK_INT < 21 ? createFilesDir(new File(context.getApplicationInfo().dataDir, "no_backup")) : context.getNoBackupFilesDir();
    }

    public static File[] getObbDirs(Context context) {
        if (Build.VERSION.SDK_INT >= 19) {
            return context.getObbDirs();
        }
        return new File[]{context.getObbDir()};
    }

    public static boolean isDeviceProtectedStorage(Context context) {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        return context.isDeviceProtectedStorage();
    }

    public static boolean startActivities(Context context, Intent[] intentArr) {
        return startActivities(context, intentArr, (Bundle) null);
    }

    public static boolean startActivities(Context context, Intent[] intentArr, Bundle bundle) {
        if (Build.VERSION.SDK_INT < 16) {
            context.startActivities(intentArr);
            return true;
        }
        context.startActivities(intentArr, bundle);
        return true;
    }

    public static void startActivity(Context context, Intent intent, @Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT < 16) {
            context.startActivity(intent);
        } else {
            context.startActivity(intent, bundle);
        }
    }

    public static void startForegroundService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT < 26) {
            context.startService(intent);
        } else {
            context.startForegroundService(intent);
        }
    }
}
