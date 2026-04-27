// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import android.view.Display;
import com.google.vrtoolkit.cardboard.proto.nano.Preferences;
import com.google.vr.ndk.base.SdkConfigurationReader;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import android.content.res.Resources;
import android.util.Log;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.protobuf.nano.MessageNano;
import android.view.WindowManager;
import android.content.Context;
import android.util.DisplayMetrics;

@UsedByNative
public class VrParamsProviderJni
{
    private static final String TAG = "VrParamsProviderJni";
    private static volatile DisplayMetrics displayMetricsOverride;
    
    static {
        VrParamsProviderJni.displayMetricsOverride = null;
    }
    
    private static DisplayMetrics getDisplayMetrics(final Context context) {
        final DisplayMetrics displayMetricsOverride = VrParamsProviderJni.displayMetricsOverride;
        if (displayMetricsOverride == null) {
            return DisplayUtils.getDisplayMetricsLandscape(((WindowManager)context.getSystemService("window")).getDefaultDisplay());
        }
        return displayMetricsOverride;
    }
    
    private static native void nativeUpdateNativePhoneParamsPointer(final long p0, final int p1, final int p2, final float p3, final float p4);
    
    @UsedByNative
    private static byte[] readDeviceParams(final Context context) {
        final VrParamsProvider create = VrParamsProviderFactory.create(context);
        final CardboardDevice.DeviceParams deviceParams = create.readDeviceParams();
        create.close();
        if (deviceParams != null) {
            return MessageNano.toByteArray(deviceParams);
        }
        return null;
    }
    
    @UsedByNative
    private static void readPhoneParams(final Context context, final long n) {
        if (context != null) {
            final DisplayMetrics displayMetrics = getDisplayMetrics(context);
            final VrParamsProvider create = VrParamsProviderFactory.create(context);
            final Phone.PhoneParams phoneParams = create.readPhoneParams();
            create.close();
            if (phoneParams != null) {
                if (phoneParams.hasXPpi()) {
                    displayMetrics.xdpi = phoneParams.getXPpi();
                }
                if (phoneParams.hasYPpi()) {
                    displayMetrics.ydpi = phoneParams.getYPpi();
                }
            }
            updateNativePhoneParamsPointer(n, displayMetrics);
            return;
        }
        Log.w("VrParamsProviderJni", "Missing context for phone params lookup. Results may be invalid.");
        updateNativePhoneParamsPointer(n, Resources.getSystem().getDisplayMetrics());
    }
    
    @UsedByNative
    private static byte[] readSdkConfigurationParams(final Context context) {
        return MessageNano.toByteArray(SdkConfigurationReader.getParams(context));
    }
    
    @UsedByNative
    private static byte[] readUserPrefs(final Context context) {
        final VrParamsProvider create = VrParamsProviderFactory.create(context);
        final Preferences.UserPrefs userPrefs = create.readUserPrefs();
        create.close();
        if (userPrefs != null) {
            return MessageNano.toByteArray(userPrefs);
        }
        return null;
    }
    
    public static void setDisplayOverride(final Display display) {
        final DisplayMetrics displayMetrics = null;
        DisplayMetrics displayMetricsLandscape;
        if (display == null) {
            displayMetricsLandscape = displayMetrics;
        }
        else {
            displayMetricsLandscape = DisplayUtils.getDisplayMetricsLandscape(display);
        }
        VrParamsProviderJni.displayMetricsOverride = displayMetricsLandscape;
    }
    
    private static void updateNativePhoneParamsPointer(final long n, final DisplayMetrics displayMetrics) {
        nativeUpdateNativePhoneParamsPointer(n, displayMetrics.widthPixels, displayMetrics.heightPixels, displayMetrics.xdpi, displayMetrics.ydpi);
    }
    
    @UsedByNative
    private static boolean writeDeviceParams(final Context context, final byte[] array) {
        final CardboardDevice.DeviceParams deviceParams = null;
        final VrParamsProvider create = VrParamsProviderFactory.create(context);
        Label_0031: {
            if (array != null) {
                break Label_0031;
            }
            CardboardDevice.DeviceParams deviceParams2 = deviceParams;
            try {
                return create.writeDeviceParams(deviceParams2);
                deviceParams2 = MessageNano.mergeFrom(new CardboardDevice.DeviceParams(), array);
                return create.writeDeviceParams(deviceParams2);
            }
            catch (final InvalidProtocolBufferNanoException obj) {
                final String value = String.valueOf(obj);
                Log.w("VrParamsProviderJni", new StringBuilder(String.valueOf(value).length() + 31).append("Error parsing protocol buffer: ").append(value).toString());
                return false;
            }
            finally {
                create.close();
            }
        }
    }
}
