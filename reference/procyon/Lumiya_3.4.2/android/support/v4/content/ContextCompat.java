// 
// Decompiled by Procyon v0.6.0
// 

package android.support.v4.content;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.util.Log;
import android.os.Build$VERSION;
import android.os.Process;
import android.support.annotation.NonNull;
import android.content.Context;
import java.io.File;
import android.util.TypedValue;

public class ContextCompat
{
    private static final String TAG = "ContextCompat";
    private static final Object sLock;
    private static TypedValue sTempValue;
    
    static {
        sLock = new Object();
    }
    
    protected ContextCompat() {
    }
    
    private static File buildPath(File parent, final String... array) {
        for (final String s : array) {
            if (parent != null) {
                if (s != null) {
                    parent = new File(parent, s);
                }
            }
            else {
                parent = new File(s);
            }
        }
        return parent;
    }
    
    public static int checkSelfPermission(@NonNull final Context context, @NonNull final String s) {
        if (s != null) {
            return context.checkPermission(s, Process.myPid(), Process.myUid());
        }
        throw new IllegalArgumentException("permission is null");
    }
    
    public static Context createDeviceProtectedStorageContext(final Context context) {
        if (Build$VERSION.SDK_INT < 24) {
            return null;
        }
        return context.createDeviceProtectedStorageContext();
    }
    
    private static File createFilesDir(final File file) {
        synchronized (ContextCompat.class) {
            if (file.exists() || file.mkdirs()) {
                return file;
            }
            if (!file.exists()) {
                Log.w("ContextCompat", "Unable to create files subdir " + file.getPath());
                return null;
            }
            return file;
        }
    }
    
    public static File getCodeCacheDir(final Context context) {
        if (Build$VERSION.SDK_INT < 21) {
            return createFilesDir(new File(context.getApplicationInfo().dataDir, "code_cache"));
        }
        return context.getCodeCacheDir();
    }
    
    @ColorInt
    public static final int getColor(final Context context, @ColorRes final int n) {
        if (Build$VERSION.SDK_INT < 23) {
            return context.getResources().getColor(n);
        }
        return context.getColor(n);
    }
    
    public static final ColorStateList getColorStateList(final Context context, @ColorRes final int n) {
        if (Build$VERSION.SDK_INT < 23) {
            return context.getResources().getColorStateList(n);
        }
        return context.getColorStateList(n);
    }
    
    public static File getDataDir(final Context context) {
        final File file = null;
        if (Build$VERSION.SDK_INT < 24) {
            final String dataDir = context.getApplicationInfo().dataDir;
            File file2;
            if (dataDir == null) {
                file2 = file;
            }
            else {
                file2 = new File(dataDir);
            }
            return file2;
        }
        return context.getDataDir();
    }
    
    public static final Drawable getDrawable(final Context context, @DrawableRes int resourceId) {
        Label_0058: {
            if (Build$VERSION.SDK_INT >= 21) {
                break Label_0058;
            }
            Label_0064: {
                if (Build$VERSION.SDK_INT >= 16) {
                    break Label_0064;
                }
                synchronized (ContextCompat.sLock) {
                    if (ContextCompat.sTempValue == null) {
                        ContextCompat.sTempValue = new TypedValue();
                    }
                    context.getResources().getValue(resourceId, ContextCompat.sTempValue, true);
                    resourceId = ContextCompat.sTempValue.resourceId;
                    monitorexit(ContextCompat.sLock);
                    return context.getResources().getDrawable(resourceId);
                    return context.getResources().getDrawable(resourceId);
                    return context.getDrawable(resourceId);
                }
            }
        }
    }
    
    public static File[] getExternalCacheDirs(final Context context) {
        if (Build$VERSION.SDK_INT < 19) {
            return new File[] { context.getExternalCacheDir() };
        }
        return context.getExternalCacheDirs();
    }
    
    public static File[] getExternalFilesDirs(final Context context, final String s) {
        if (Build$VERSION.SDK_INT < 19) {
            return new File[] { context.getExternalFilesDir(s) };
        }
        return context.getExternalFilesDirs(s);
    }
    
    public static final File getNoBackupFilesDir(final Context context) {
        if (Build$VERSION.SDK_INT < 21) {
            return createFilesDir(new File(context.getApplicationInfo().dataDir, "no_backup"));
        }
        return context.getNoBackupFilesDir();
    }
    
    public static File[] getObbDirs(final Context context) {
        if (Build$VERSION.SDK_INT < 19) {
            return new File[] { context.getObbDir() };
        }
        return context.getObbDirs();
    }
    
    public static boolean isDeviceProtectedStorage(final Context context) {
        return Build$VERSION.SDK_INT >= 24 && context.isDeviceProtectedStorage();
    }
    
    public static boolean startActivities(final Context context, final Intent[] array) {
        return startActivities(context, array, null);
    }
    
    public static boolean startActivities(final Context context, final Intent[] array, final Bundle bundle) {
        if (Build$VERSION.SDK_INT < 16) {
            context.startActivities(array);
        }
        else {
            context.startActivities(array, bundle);
        }
        return true;
    }
    
    public static void startActivity(final Context context, final Intent intent, @Nullable final Bundle bundle) {
        if (Build$VERSION.SDK_INT < 16) {
            context.startActivity(intent);
        }
        else {
            context.startActivity(intent, bundle);
        }
    }
    
    public static void startForegroundService(final Context context, final Intent intent) {
        if (Build$VERSION.SDK_INT < 26) {
            context.startService(intent);
        }
        else {
            context.startForegroundService(intent);
        }
    }
}
