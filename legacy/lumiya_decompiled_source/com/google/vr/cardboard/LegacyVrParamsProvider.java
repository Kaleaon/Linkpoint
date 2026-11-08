package com.google.vr.cardboard;

import com.google.common.logging.nano.Vr;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.Preferences;
/* loaded from: classes.dex */
public final class LegacyVrParamsProvider implements VrParamsProvider {
    private static final String TAG = LegacyVrParamsProvider.class.getSimpleName();

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final void close() {
    }

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final CardboardDevice.DeviceParams readDeviceParams() {
        return ConfigUtils.readDeviceParamsFromExternalStorage();
    }

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final Phone.PhoneParams readPhoneParams() {
        Phone.PhoneParams readPhoneParamsFromExternalStorage = ConfigUtils.readPhoneParamsFromExternalStorage();
        return readPhoneParamsFromExternalStorage != null ? readPhoneParamsFromExternalStorage : PhoneParams.getPpiOverride();
    }

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest) {
        return null;
    }

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final Preferences.UserPrefs readUserPrefs() {
        return null;
    }

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final boolean updateUserPrefs(Preferences.UserPrefs userPrefs) {
        return false;
    }

    @Override // com.google.vr.cardboard.VrParamsProvider
    public final boolean writeDeviceParams(CardboardDevice.DeviceParams deviceParams) {
        return deviceParams != null ? ConfigUtils.writeDeviceParamsToExternalStorage(deviceParams) : ConfigUtils.removeDeviceParamsFromExternalStorage();
    }
}
