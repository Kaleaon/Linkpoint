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
import android.util.Log;
import android.util.TypedValue;
import java.io.File;
/* loaded from: classes.dex */
public class ContextCompat {
    private static final String TAG = "ContextCompat";
    private static final Object sLock = new Object();
    private static TypedValue sTempValue;

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

    private static synchronized File createFilesDir(File file) {
        synchronized (ContextCompat.class) {
            if (file.exists() || file.mkdirs()) {
                return file;
            }
            if (file.exists()) {
                return file;
            }
            Log.w(TAG, "Unable to create files subdir " + file.getPath());
            return null;
        }
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
        if (Build.VERSION.SDK_INT < 24) {
            String str = context.getApplicationInfo().dataDir;
            if (str == null) {
                return null;
            }
            return new File(str);
        }
        return context.getDataDir();
    }

    public static final Drawable getDrawable(Context context, @DrawableRes int i) {
        int i2;
        if (Build.VERSION.SDK_INT < 21) {
            if (Build.VERSION.SDK_INT < 16) {
                synchronized (sLock) {
                    if (sTempValue == null) {
                        sTempValue = new TypedValue();
                    }
                    context.getResources().getValue(i, sTempValue, true);
                    i2 = sTempValue.resourceId;
                }
                return context.getResources().getDrawable(i2);
            }
            return context.getResources().getDrawable(i);
        }
        return context.getDrawable(i);
    }

    public static File[] getExternalCacheDirs(Context context) {
        return Build.VERSION.SDK_INT < 19 ? new File[]{context.getExternalCacheDir()} : context.getExternalCacheDirs();
    }

    public static File[] getExternalFilesDirs(Context context, String str) {
        return Build.VERSION.SDK_INT < 19 ? new File[]{context.getExternalFilesDir(str)} : context.getExternalFilesDirs(str);
    }

    public static final File getNoBackupFilesDir(Context context) {
        return Build.VERSION.SDK_INT < 21 ? createFilesDir(new File(context.getApplicationInfo().dataDir, "no_backup")) : context.getNoBackupFilesDir();
    }

    public static File[] getObbDirs(Context context) {
        return Build.VERSION.SDK_INT < 19 ? new File[]{context.getObbDir()} : context.getObbDirs();
    }

    public static boolean isDeviceProtectedStorage(Context context) {
        if (Build.VERSION.SDK_INT < 24) {
            return false;
        }
        return context.isDeviceProtectedStorage();
    }

    public static boolean startActivities(Context context, Intent[] intentArr) {
        return startActivities(context, intentArr, null);
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
