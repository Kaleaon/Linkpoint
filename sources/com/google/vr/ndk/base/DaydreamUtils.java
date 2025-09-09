package com.google.vr.ndk.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import java.util.List;

public class DaydreamUtils {
    public static final int DAYDREAM_NOT_SUPPORTED = 0;
    public static final int DAYDREAM_OPTIONAL = 1;
    public static final int DAYDREAM_REQUIRED = 2;
    static final String INTENT_CATEGORY_CARDBOARD = "com.google.intent.category.CARDBOARD";
    static final String INTENT_CATEGORY_DAYDREAM = "com.google.intent.category.DAYDREAM";
    private static boolean sDaydreamPhoneOverrideForTesting;

    protected DaydreamUtils() {
    }

    private static boolean canResolveIntent(PackageManager packageManager, ComponentName componentName, Intent intent) {
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 128);
        if (queryIntentActivities != null) {
            for (ResolveInfo next : queryIntentActivities) {
                if (next != null && next.activityInfo != null && next.activityInfo.name != null && next.activityInfo.name.equals(componentName.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int getComponentDaydreamCompatibility(Context context, ComponentName componentName) {
        return getComponentDaydreamCompatibility(context, context.getPackageManager(), componentName);
    }

    static int getComponentDaydreamCompatibility(Context context, PackageManager packageManager, ComponentName componentName) {
        Intent intent = new Intent();
        intent.setPackage(componentName.getPackageName());
        intent.addCategory("com.google.intent.category.DAYDREAM");
        if (!canResolveIntent(packageManager, componentName, intent)) {
            return 0;
        }
        Intent intent2 = new Intent();
        intent2.setPackage(componentName.getPackageName());
        intent2.addCategory("com.google.intent.category.CARDBOARD");
        return !canResolveIntent(packageManager, componentName, intent2) ? 2 : 1;
    }

    public static boolean isDaydreamPhone(Context context) {
        if (sDaydreamPhoneOverrideForTesting) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return context.getPackageManager().hasSystemFeature("android.hardware.vr.high_performance");
        }
        return false;
    }

    public static boolean isDaydreamViewer(CardboardDevice.DeviceParams deviceParams) {
        return (deviceParams == null || deviceParams.daydreamInternal == null) ? false : true;
    }

    static void setIsDaydreamPhoneForTesting(boolean z) {
        sDaydreamPhoneOverrideForTesting = z;
    }
}
