// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.licensing;

import android.os.Handler;
import android.content.Context;

public class LicenseChecker
{
    public static final String APP_STORE_NAME = "Google Play";
    public static final String APP_STORE_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya";
    public static final String CLOUD_PLUGIN_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud";
    public static final int MSG_LICENSING_ALLOW = 2131755033;
    public static final int MSG_LICENSING_APP_ERROR = 2131755034;
    public static final int MSG_LICENSING_DONT_ALLOW = 2131755035;
    public static final String VOICE_PLUGIN_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice";
    
    public LicenseChecker(final Context context, final Handler handler, final Object o) {
        handler.obtainMessage(2131755033, o).sendToTarget();
    }
}
