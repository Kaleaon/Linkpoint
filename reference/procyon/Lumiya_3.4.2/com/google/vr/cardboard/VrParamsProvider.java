// 
// Decompiled by Procyon v0.6.0
// 

package com.google.vr.cardboard;

import com.google.vrtoolkit.cardboard.proto.nano.Preferences;
import com.google.common.logging.nano.Vr;
import com.google.vr.vrcore.nano.SdkConfiguration;
import com.google.vrtoolkit.cardboard.proto.nano.Phone;
import com.google.vrtoolkit.cardboard.proto.nano.CardboardDevice;

public interface VrParamsProvider
{
    void close();
    
    CardboardDevice.DeviceParams readDeviceParams();
    
    Phone.PhoneParams readPhoneParams();
    
    Vr.VREvent.SdkConfigurationParams readSdkConfigurationParams(final SdkConfiguration.SdkConfigurationRequest p0);
    
    Preferences.UserPrefs readUserPrefs();
    
    boolean updateUserPrefs(final Preferences.UserPrefs p0);
    
    boolean writeDeviceParams(final CardboardDevice.DeviceParams p0);
}
