// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import com.google.protobuf.nano.MessageNano;
import java.util.Iterator;
import android.util.Log;
import android.os.Build;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import java.util.Arrays;
import java.util.List;

public class PhoneParams
{
    private static final boolean DEBUG = false;
    private static final List<PpiOverride> PPI_OVERRIDES;
    private static final String TAG;
    
    static {
        TAG = PhoneParams.class.getSimpleName();
        PPI_OVERRIDES = Arrays.asList(new PpiOverride("Micromax", null, "4560MMX", null, 217, 217), new PpiOverride("HTC", "endeavoru", "HTC One X", null, 312, 312), new PpiOverride("samsung", null, "SM-G920P", null, 575, 575), new PpiOverride("samsung", null, "SM-G930", null, 581, 580), new PpiOverride("samsung", null, "SM-G9300", null, 581, 580), new PpiOverride("samsung", null, "SM-G930A", null, 581, 580), new PpiOverride("samsung", null, "SM-G930F", null, 581, 580), new PpiOverride("samsung", null, "SM-G930P", null, 581, 580), new PpiOverride("samsung", null, "SM-G930R4", null, 581, 580), new PpiOverride("samsung", null, "SM-G930T", null, 581, 580), new PpiOverride("samsung", null, "SM-G930V", null, 581, 580), new PpiOverride("samsung", null, "SM-G930W8", null, 581, 580), new PpiOverride("samsung", null, "SM-N915FY", null, 541, 541), new PpiOverride("samsung", null, "SM-N915A", null, 541, 541), new PpiOverride("samsung", null, "SM-N915T", null, 541, 541), new PpiOverride("samsung", null, "SM-N915K", null, 541, 541), new PpiOverride("samsung", null, "SM-N915T", null, 541, 541), new PpiOverride("samsung", null, "SM-N915G", null, 541, 541), new PpiOverride("samsung", null, "SM-N915D", null, 541, 541), new PpiOverride("BLU", "BLU", "Studio 5.0 HD LTE", "qcom", 294, 294), new PpiOverride("OnePlus", "A0001", "A0001", "bacon", 401, 401), new PpiOverride("THL", "THL", "thl 5000", "mt6592", 441, 441));
    }
    
    private PhoneParams() {
    }
    
    public static Phone.PhoneParams getPpiOverride() {
        final Phone.PhoneParams phoneParams = new Phone.PhoneParams();
        if (!getPpiOverride(PhoneParams.PPI_OVERRIDES, Build.MANUFACTURER, Build.DEVICE, Build.MODEL, Build.HARDWARE, phoneParams)) {
            return null;
        }
        return phoneParams;
    }
    
    static boolean getPpiOverride(final List<PpiOverride> list, final String s, final String s2, final String s3, final String s4, final Phone.PhoneParams phoneParams) {
        for (final PpiOverride ppiOverride : list) {
            if (ppiOverride.isMatching(s, s2, s3, s4)) {
                Log.d(PhoneParams.TAG, String.format("Found override: {MANUFACTURER=%s, DEVICE=%s, MODEL=%s, HARDWARE=%s} : x_ppi=%d, y_ppi=%d", ppiOverride.manufacturer, ppiOverride.device, ppiOverride.model, ppiOverride.hardware, ppiOverride.xPpi, ppiOverride.yPpi));
                phoneParams.setXPpi((float)ppiOverride.xPpi);
                phoneParams.setYPpi((float)ppiOverride.yPpi);
                return true;
            }
        }
        return false;
    }
    
    public static void registerOverrides() {
        registerOverridesInternal(PhoneParams.PPI_OVERRIDES, Build.MANUFACTURER, Build.DEVICE, Build.MODEL, Build.HARDWARE);
    }
    
    static void registerOverridesInternal(final List<PpiOverride> list, final String s, final String s2, final String s3, final String s4) {
        final Phone.PhoneParams phoneParamsFromExternalStorage = ConfigUtils.readPhoneParamsFromExternalStorage();
        Cloneable clone;
        if (phoneParamsFromExternalStorage != null) {
            clone = phoneParamsFromExternalStorage.clone();
        }
        else {
            clone = new Phone.PhoneParams();
        }
        if (getPpiOverride(list, s, s2, s3, s4, (Phone.PhoneParams)clone) && !MessageNano.messageNanoEquals(phoneParamsFromExternalStorage, (MessageNano)clone)) {
            Log.i(PhoneParams.TAG, "Applying phone param override.");
            ConfigUtils.writePhoneParamsToExternalStorage((Phone.PhoneParams)clone);
        }
    }
    
    static class PpiOverride
    {
        String device;
        String hardware;
        String manufacturer;
        String model;
        int xPpi;
        int yPpi;
        
        PpiOverride(final String manufacturer, final String device, final String model, final String hardware, final int xPpi, final int yPpi) {
            this.manufacturer = manufacturer;
            this.device = device;
            this.model = model;
            this.hardware = hardware;
            this.xPpi = xPpi;
            this.yPpi = yPpi;
        }
        
        boolean isMatching(final String anObject, final String anObject2, final String anObject3, final String anObject4) {
            if (this.manufacturer == null || this.manufacturer.equals(anObject)) {
                if (this.device == null || this.device.equals(anObject2)) {
                    if (this.model == null || this.model.equals(anObject3)) {
                        if (this.hardware == null || this.hardware.equals(anObject4)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}
