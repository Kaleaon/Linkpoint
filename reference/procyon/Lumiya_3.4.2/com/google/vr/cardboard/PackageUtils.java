// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager$NameNotFoundException;
import android.content.Context;

public class PackageUtils
{
    private static final String GOOGLE_PACKAGE_PREFIX = "com.google.";
    
    public static boolean isGooglePackage(final String s) {
        return s != null && s.startsWith("com.google.");
    }
    
    public static boolean isSystemPackage(final Context context, final String s) {
        try {
            final ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(s, 0);
            int flags;
            if (applicationInfo == null) {
                flags = 0;
            }
            else {
                flags = applicationInfo.flags;
            }
            return (flags & 0x1) != 0x0 || (flags & 0x80) != 0x0;
        }
        catch (final PackageManager$NameNotFoundException ex) {
            return false;
        }
    }
}
