package com.google.vr.cardboard;

import android.net.Uri;

public class VrSettingsProviderContract {
    public static final String DAYDREAM_SETUP_COMPLETED = "daydream_setup";
    public static final String DEVICE_PARAMS_SETTING = "device_params";
    public static final String PHONE_PARAMS_SETTING = "phone_params";
    public static final String PROVIDER_INTENT_ACTION = "android.content.action.VR_SETTINGS_PROVIDER";
    public static final String SDK_CONFIGURATION_PARAMS_SETTING = "sdk_configuration_params";
    public static final String SETTING_VALUE_KEY = "value";
    public static final String USER_PREFS_SETTING = "user_prefs";
    public static final String VRCORE_AUTHORITY = "com.google.vr.vrcore.settings";

    public static Uri createUri(String str, String str2) {
        String valueOf = String.valueOf("content://");
        return Uri.parse(new StringBuilder(String.valueOf(valueOf).length() + 1 + String.valueOf(str).length() + String.valueOf(str2).length()).append(valueOf).append(str).append("/").append(str2).toString());
    }
}
