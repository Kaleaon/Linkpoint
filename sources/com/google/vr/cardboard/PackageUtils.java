package com.google.vr.cardboard;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageUtils {
    private static final String GOOGLE_PACKAGE_PREFIX = "com.google.";

    public static boolean isGooglePackage(String str) {
        return str != null && str.startsWith(GOOGLE_PACKAGE_PREFIX);
    }

    public static boolean isSystemPackage(Context context, String str) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            int i = applicationInfo == null ? 0 : applicationInfo.flags;
            return ((i & 1) == 0 && (i & 128) == 0) ? false : true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
