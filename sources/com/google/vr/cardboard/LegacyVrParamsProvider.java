package com.google.vr.cardboard;

import com.google.common.logging.nano.Vr;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.Preferences;

public final class LegacyVrParamsProvider implements VrParamsProvider {
    private static final String TAG = LegacyVrParamsProvider.class.getSimpleName();

    public final void close() {
    }

    public final CardboardDevice.DeviceParams readDeviceParams() {
        return ConfigUtils.readDeviceParamsFromExternalStorage();
    }

    public final Phone.PhoneParams readPhoneParams() {
        Phone.PhoneParams readPhoneParamsFromExternalStorage = ConfigUtils.readPhoneParamsFromExternalStorage();
        return readPhoneParamsFromExternalStorage != null ? readPhoneParamsFromExternalStorage : PhoneParams.getPpiOverride();
    }

    public final Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest) {
        return null;
    }

    public final Preferences.UserPrefs readUserPrefs() {
        return null;
    }

    public final boolean updateUserPrefs(Preferences.UserPrefs userPrefs) {
        return false;
    }

    public final boolean writeDeviceParams(CardboardDevice.DeviceParams deviceParams) {
        return deviceParams != null ? ConfigUtils.writeDeviceParamsToExternalStorage(deviceParams) : ConfigUtils.removeDeviceParamsFromExternalStorage();
    }
}
