package com.google.vr.sdk.base;

import android.app.Activity;
import com.google.vr.cardboard.AndroidNCompat;

public final class AndroidCompat {
    private AndroidCompat() {
    }

    public static void setSustainedPerformanceMode(Activity activity, boolean z) {
        AndroidNCompat.setSustainedPerformanceMode(activity, z);
    }

    public static boolean setVrModeEnabled(Activity activity, boolean z) {
        return AndroidNCompat.setVrModeEnabled(activity, z, 1);
    }

    public static boolean trySetVrModeEnabled(Activity activity, boolean z) {
        return AndroidNCompat.setVrModeEnabled(activity, z, 0);
    }
}
