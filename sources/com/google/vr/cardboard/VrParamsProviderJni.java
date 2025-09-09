package com.google.vr.cardboard;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.vr.ndk.base.SdkConfigurationReader;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.Preferences;

@UsedByNative
public class VrParamsProviderJni {
    private static final String TAG = "VrParamsProviderJni";
    private static volatile DisplayMetrics displayMetricsOverride = null;

    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = displayMetricsOverride;
        return displayMetrics == null ? DisplayUtils.getDisplayMetricsLandscape(((WindowManager) context.getSystemService("window")).getDefaultDisplay()) : displayMetrics;
    }

    private static native void nativeUpdateNativePhoneParamsPointer(long j, int i, int i2, float f, float f2);

    @UsedByNative
    private static byte[] readDeviceParams(Context context) {
        VrParamsProvider create = VrParamsProviderFactory.create(context);
        CardboardDevice.DeviceParams readDeviceParams = create.readDeviceParams();
        create.close();
        if (readDeviceParams != null) {
            return MessageNano.toByteArray(readDeviceParams);
        }
        return null;
    }

    @UsedByNative
    private static void readPhoneParams(Context context, long j) {
        if (context != null) {
            DisplayMetrics displayMetrics = getDisplayMetrics(context);
            VrParamsProvider create = VrParamsProviderFactory.create(context);
            Phone.PhoneParams readPhoneParams = create.readPhoneParams();
            create.close();
            if (readPhoneParams != null) {
                if (readPhoneParams.hasXPpi()) {
                    displayMetrics.xdpi = readPhoneParams.getXPpi();
                }
                if (readPhoneParams.hasYPpi()) {
                    displayMetrics.ydpi = readPhoneParams.getYPpi();
                }
            }
            updateNativePhoneParamsPointer(j, displayMetrics);
            return;
        }
        Log.w(TAG, "Missing context for phone params lookup. Results may be invalid.");
        updateNativePhoneParamsPointer(j, Resources.getSystem().getDisplayMetrics());
    }

    @UsedByNative
    private static byte[] readSdkConfigurationParams(Context context) {
        return MessageNano.toByteArray(SdkConfigurationReader.getParams(context));
    }

    @UsedByNative
    private static byte[] readUserPrefs(Context context) {
        VrParamsProvider create = VrParamsProviderFactory.create(context);
        Preferences.UserPrefs readUserPrefs = create.readUserPrefs();
        create.close();
        if (readUserPrefs != null) {
            return MessageNano.toByteArray(readUserPrefs);
        }
        return null;
    }

    public static void setDisplayOverride(Display display) {
        DisplayMetrics displayMetrics = null;
        if (display != null) {
            displayMetrics = DisplayUtils.getDisplayMetricsLandscape(display);
        }
        displayMetricsOverride = displayMetrics;
    }

    private static void updateNativePhoneParamsPointer(long j, DisplayMetrics displayMetrics) {
        nativeUpdateNativePhoneParamsPointer(j, displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.xdpi, displayMetrics.ydpi);
    }

    /* JADX INFO: finally extract failed */
    @UsedByNative
    private static boolean writeDeviceParams(Context context, byte[] bArr) {
        CardboardDevice.DeviceParams deviceParams = null;
        VrParamsProvider create = VrParamsProviderFactory.create(context);
        if (bArr != null) {
            deviceParams = (CardboardDevice.DeviceParams) MessageNano.mergeFrom(new CardboardDevice.DeviceParams(), bArr);
        }
        try {
            boolean writeDeviceParams = create.writeDeviceParams(deviceParams);
            create.close();
            return writeDeviceParams;
        } catch (InvalidProtocolBufferNanoException e) {
            String valueOf = String.valueOf(e);
            Log.w(TAG, new StringBuilder(String.valueOf(valueOf).length() + 31).append("Error parsing protocol buffer: ").append(valueOf).toString());
            create.close();
            return false;
        } catch (Throwable th) {
            create.close();
            throw th;
        }
    }
}
