package com.google.vr.sdk.base;

import android.content.Context;
import android.os.Process;

public class PermissionUtils {
    public static boolean hasCameraPermission(Context context) {
        return hasPermission(context, "android.permission.CAMERA");
    }

    public static boolean hasPermission(Context context, String str) {
        return (context == null || str == null || context.checkPermission(str, Process.myPid(), Process.myUid()) != 0) ? false : true;
    }

    public static boolean hasStoragePermission(Context context) {
        return hasPermission(context, "android.permission.READ_EXTERNAL_STORAGE") && hasPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE");
    }
}
