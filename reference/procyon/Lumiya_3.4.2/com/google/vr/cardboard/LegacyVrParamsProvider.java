// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import com.google.vrtoolkit.cardboard.proto.nano.Preferences;
import com.google.common.logging.nano.Vr;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;

public final class LegacyVrParamsProvider implements VrParamsProvider
{
    private static final String TAG;
    
    static {
        TAG = LegacyVrParamsProvider.class.getSimpleName();
    }
    
    @Override
    public final void close() {
    }
    
    @Override
    public final CardboardDevice.DeviceParams readDeviceParams() {
        return ConfigUtils.readDeviceParamsFromExternalStorage();
    }
    
    @Override
    public final Phone.PhoneParams readPhoneParams() {
        Phone.PhoneParams phoneParams = ConfigUtils.readPhoneParamsFromExternalStorage();
        if (phoneParams == null) {
            phoneParams = PhoneParams.getPpiOverride();
        }
        return phoneParams;
    }
    
    @Override
    public final Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(final SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest) {
        return null;
    }
    
    @Override
    public final Preferences.UserPrefs readUserPrefs() {
        return null;
    }
    
    @Override
    public final boolean updateUserPrefs(final Preferences.UserPrefs userPrefs) {
        return false;
    }
    
    @Override
    public final boolean writeDeviceParams(final CardboardDevice.DeviceParams deviceParams) {
        if (deviceParams != null) {
            return ConfigUtils.writeDeviceParamsToExternalStorage(deviceParams);
        }
        return ConfigUtils.removeDeviceParamsFromExternalStorage();
    }
}
