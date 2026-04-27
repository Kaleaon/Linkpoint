package com.google.vr.cardboard;

import com.google.common.logging.nano.Vr;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.Preferences;

public interface VrParamsProvider {
    void close();

    CardboardDevice.DeviceParams readDeviceParams();

    Phone.PhoneParams readPhoneParams();

    Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(SdkConfiguration.SdkConfigurationRequest sdkConfigurationRequest);

    Preferences.UserPrefs readUserPrefs();

    boolean updateUserPrefs(Preferences.UserPrefs userPrefs);

    boolean writeDeviceParams(CardboardDevice.DeviceParams deviceParams);
}
