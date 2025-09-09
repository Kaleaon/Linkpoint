package com.google.vr.ndk.base;

import android.app.Activity;
import com.google.vr.cardboard.AndroidNCompat;

public final class AndroidCompat {
    private AndroidCompat() {
    }

    public static void setSustainedPerformanceMode(Activity activity, boolean z) {
        AndroidNCompat.setSustainedPerformanceMode(activity, z);
    }

    public static boolean setVrModeEnabled(Activity activity, boolean z) {
        return AndroidNCompat.setVrModeEnabled(activity, z);
    }
}
