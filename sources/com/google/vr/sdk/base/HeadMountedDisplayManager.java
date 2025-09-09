package com.google.vr.sdk.base;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.google.vr.cardboard.VrParamsProvider;
import com.google.vr.cardboard.VrParamsProviderFactory;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;

public class HeadMountedDisplayManager {
    private static final String TAG = "HeadMountedDisplayManager";
    private final Context context;
    private final HeadMountedDisplay hmd = new HeadMountedDisplay(createScreenParams(), createGvrViewerParams());
    private final VrParamsProvider paramsProvider;

    public HeadMountedDisplayManager(Context context2) {
        this.context = context2;
        this.paramsProvider = VrParamsProviderFactory.create(context2);
    }

    private GvrViewerParams createGvrViewerParams() {
        return new GvrViewerParams(this.paramsProvider.readDeviceParams());
    }

    private ScreenParams createScreenParams() {
        Display display = getDisplay();
        ScreenParams fromProto = ScreenParams.fromProto(display, this.paramsProvider.readPhoneParams());
        return fromProto == null ? new ScreenParams(display) : fromProto;
    }

    private Display getDisplay() {
        return ((WindowManager) this.context.getSystemService("window")).getDefaultDisplay();
    }

    public HeadMountedDisplay getHeadMountedDisplay() {
        return this.hmd;
    }

    public void onPause() {
    }

    public void onResume() {
        ScreenParams screenParams = null;
        CardboardDevice.DeviceParams readDeviceParams = this.paramsProvider.readDeviceParams();
        GvrViewerParams gvrViewerParams = readDeviceParams == null ? null : new GvrViewerParams(readDeviceParams);
        if (gvrViewerParams != null && !gvrViewerParams.equals(this.hmd.getGvrViewerParams())) {
            this.hmd.setGvrViewerParams(gvrViewerParams);
            Log.i(TAG, "Successfully read updated device params from external storage");
        }
        Phone.PhoneParams readPhoneParams = this.paramsProvider.readPhoneParams();
        if (readPhoneParams != null) {
            screenParams = ScreenParams.fromProto(getDisplay(), readPhoneParams);
        }
        if (screenParams != null && !screenParams.equals(this.hmd.getScreenParams())) {
            this.hmd.setScreenParams(screenParams);
            Log.i(TAG, "Successfully read updated screen params from external storage");
        }
    }

    public boolean updateGvrViewerParams(GvrViewerParams gvrViewerParams) {
        if (gvrViewerParams == null || gvrViewerParams.equals(this.hmd.getGvrViewerParams())) {
            return false;
        }
        this.hmd.setGvrViewerParams(gvrViewerParams);
        this.paramsProvider.writeDeviceParams(gvrViewerParams.toProtobuf());
        return true;
    }

    public boolean updateScreenParams(ScreenParams screenParams) {
        if (screenParams == null || screenParams.equals(this.hmd.getScreenParams())) {
            return false;
        }
        this.hmd.setScreenParams(screenParams);
        return true;
    }
}
