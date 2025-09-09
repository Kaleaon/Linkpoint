package com.google.vr.ndk.base;

import android.app.Activity;
import android.content.Context;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;

public class DaydreamUtilsWrapper {
    public int getActivityDaydreamCompatibility(Activity activity) {
        return DaydreamUtils.getComponentDaydreamCompatibility(activity, activity.getComponentName());
    }

    public boolean isDaydreamActivity(Activity activity) {
        return getActivityDaydreamCompatibility(activity) != 0;
    }

    public boolean isDaydreamPhone(Context context) {
        return DaydreamUtils.isDaydreamPhone(context);
    }

    public boolean isDaydreamRequiredActivity(Activity activity) {
        return getActivityDaydreamCompatibility(activity) == 2;
    }

    public boolean isDaydreamViewer(CardboardDevice.DeviceParams deviceParams) {
        return DaydreamUtils.isDaydreamViewer(deviceParams);
    }
}
