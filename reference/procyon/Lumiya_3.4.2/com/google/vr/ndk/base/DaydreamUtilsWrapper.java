// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.ndk.base;

import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.content.Context;
import android.app.Activity;

public class DaydreamUtilsWrapper
{
    public int getActivityDaydreamCompatibility(final Activity activity) {
        return DaydreamUtils.getComponentDaydreamCompatibility((Context)activity, activity.getComponentName());
    }
    
    public boolean isDaydreamActivity(final Activity activity) {
        return this.getActivityDaydreamCompatibility(activity) != 0;
    }
    
    public boolean isDaydreamPhone(final Context context) {
        return DaydreamUtils.isDaydreamPhone(context);
    }
    
    public boolean isDaydreamRequiredActivity(final Activity activity) {
        return this.getActivityDaydreamCompatibility(activity) == 2;
    }
    
    public boolean isDaydreamViewer(final CardboardDevice.DeviceParams deviceParams) {
        return DaydreamUtils.isDaydreamViewer(deviceParams);
    }
}
