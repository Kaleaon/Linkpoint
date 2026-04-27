// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.os.Build$VERSION;
import android.content.Context;
import java.util.Iterator;
import java.util.List;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public class DaydreamUtils
{
    public static final int DAYDREAM_NOT_SUPPORTED = 0;
    public static final int DAYDREAM_OPTIONAL = 1;
    public static final int DAYDREAM_REQUIRED = 2;
    static final String INTENT_CATEGORY_CARDBOARD = "com.google.intent.category.CARDBOARD";
    static final String INTENT_CATEGORY_DAYDREAM = "com.google.intent.category.DAYDREAM";
    private static boolean sDaydreamPhoneOverrideForTesting;
    
    protected DaydreamUtils() {
    }
    
    private static boolean canResolveIntent(final PackageManager packageManager, final ComponentName componentName, final Intent intent) {
        final List queryIntentActivities = packageManager.queryIntentActivities(intent, 128);
        if (queryIntentActivities != null) {
            for (final ResolveInfo resolveInfo : queryIntentActivities) {
                if (resolveInfo != null && resolveInfo.activityInfo != null && resolveInfo.activityInfo.name != null && resolveInfo.activityInfo.name.equals(componentName.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static int getComponentDaydreamCompatibility(final Context context, final ComponentName componentName) {
        return getComponentDaydreamCompatibility(context, context.getPackageManager(), componentName);
    }
    
    static int getComponentDaydreamCompatibility(final Context context, final PackageManager packageManager, final ComponentName componentName) {
        final Intent intent = new Intent();
        intent.setPackage(componentName.getPackageName());
        intent.addCategory("com.google.intent.category.DAYDREAM");
        if (!canResolveIntent(packageManager, componentName, intent)) {
            return 0;
        }
        final Intent intent2 = new Intent();
        intent2.setPackage(componentName.getPackageName());
        intent2.addCategory("com.google.intent.category.CARDBOARD");
        if (!canResolveIntent(packageManager, componentName, intent2)) {
            return 2;
        }
        return 1;
    }
    
    public static boolean isDaydreamPhone(final Context context) {
        return DaydreamUtils.sDaydreamPhoneOverrideForTesting || (Build$VERSION.SDK_INT >= 24 && context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance"));
    }
    
    public static boolean isDaydreamViewer(final CardboardDevice.DeviceParams deviceParams) {
        return deviceParams != null && deviceParams.daydreamInternal != null;
    }
    
    static void setIsDaydreamPhoneForTesting(final boolean sDaydreamPhoneOverrideForTesting) {
        DaydreamUtils.sDaydreamPhoneOverrideForTesting = sDaydreamPhoneOverrideForTesting;
    }
}
