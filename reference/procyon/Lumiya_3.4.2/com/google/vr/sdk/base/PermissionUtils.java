// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import android.os.Process;
import android.content.Context;

public class PermissionUtils
{
    public static boolean hasCameraPermission(final Context context) {
        return hasPermission(context, "android.permission.CAMERA");
    }
    
    public static boolean hasPermission(final Context context, final String s) {
        boolean b = false;
        if (context != null && s != null) {
            if (context.checkPermission(s, Process.myPid(), Process.myUid()) == 0) {
                b = true;
            }
            return b;
        }
        return false;
    }
    
    public static boolean hasStoragePermission(final Context context) {
        boolean b = false;
        if (hasPermission(context, "android.permission.READ_EXTERNAL_STORAGE") && hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            b = true;
        }
        return b;
    }
}
