// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import com.google.vr.cardboard.AndroidNCompat;
import android.app.Activity;

public final class AndroidCompat
{
    private AndroidCompat() {
    }
    
    public static void setSustainedPerformanceMode(final Activity activity, final boolean b) {
        AndroidNCompat.setSustainedPerformanceMode(activity, b);
    }
    
    public static boolean setVrModeEnabled(final Activity activity, final boolean b) {
        return AndroidNCompat.setVrModeEnabled(activity, b, 1);
    }
    
    public static boolean trySetVrModeEnabled(final Activity activity, final boolean b) {
        return AndroidNCompat.setVrModeEnabled(activity, b, 0);
    }
}
