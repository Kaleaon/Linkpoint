// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.sdk.base;

import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import android.util.Log;
import android.view.WindowManager;
import android.view.Display;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vr.cardboard.VrParamsProvider;
import android.content.Context;

public class HeadMountedDisplayManager
{
    private static final String TAG = "HeadMountedDisplayManager";
    private final Context context;
    private final HeadMountedDisplay hmd;
    private final VrParamsProvider paramsProvider;
    
    public HeadMountedDisplayManager(final Context context) {
        this.context = context;
        this.paramsProvider = VrParamsProviderFactory.create(context);
        this.hmd = new HeadMountedDisplay(this.createScreenParams(), this.createGvrViewerParams());
    }
    
    private GvrViewerParams createGvrViewerParams() {
        return new GvrViewerParams(this.paramsProvider.readDeviceParams());
    }
    
    private ScreenParams createScreenParams() {
        final Display display = this.getDisplay();
        ScreenParams fromProto;
        if ((fromProto = ScreenParams.fromProto(display, this.paramsProvider.readPhoneParams())) == null) {
            fromProto = new ScreenParams(display);
        }
        return fromProto;
    }
    
    private Display getDisplay() {
        return ((WindowManager)this.context.getSystemService("window")).getDefaultDisplay();
    }
    
    public HeadMountedDisplay getHeadMountedDisplay() {
        return this.hmd;
    }
    
    public void onPause() {
    }
    
    public void onResume() {
        final ScreenParams screenParams = null;
        final CardboardDevice.DeviceParams deviceParams = this.paramsProvider.readDeviceParams();
        GvrViewerParams gvrViewerParams;
        if (deviceParams == null) {
            gvrViewerParams = null;
        }
        else {
            gvrViewerParams = new GvrViewerParams(deviceParams);
        }
        if (gvrViewerParams != null && !gvrViewerParams.equals(this.hmd.getGvrViewerParams())) {
            this.hmd.setGvrViewerParams(gvrViewerParams);
            Log.i("HeadMountedDisplayManager", "Successfully read updated device params from external storage");
        }
        final Phone.PhoneParams phoneParams = this.paramsProvider.readPhoneParams();
        ScreenParams fromProto;
        if (phoneParams == null) {
            fromProto = screenParams;
        }
        else {
            fromProto = ScreenParams.fromProto(this.getDisplay(), phoneParams);
        }
        if (fromProto != null && !fromProto.equals(this.hmd.getScreenParams())) {
            this.hmd.setScreenParams(fromProto);
            Log.i("HeadMountedDisplayManager", "Successfully read updated screen params from external storage");
        }
    }
    
    public boolean updateGvrViewerParams(final GvrViewerParams gvrViewerParams) {
        if (gvrViewerParams != null && !gvrViewerParams.equals(this.hmd.getGvrViewerParams())) {
            this.hmd.setGvrViewerParams(gvrViewerParams);
            this.paramsProvider.writeDeviceParams(gvrViewerParams.toProtobuf());
            return true;
        }
        return false;
    }
    
    public boolean updateScreenParams(final ScreenParams screenParams) {
        if (screenParams != null && !screenParams.equals(this.hmd.getScreenParams())) {
            this.hmd.setScreenParams(screenParams);
            return true;
        }
        return false;
    }
}
