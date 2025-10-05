package com.linkpoint.licensing

import android.content.Context
import android.os.Handler
import com.linkpoint.R

class LicenseChecker {
    const val String APP_STORE_NAME = "Google Play"
    const val String APP_STORE_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya"
    const val String CLOUD_PLUGIN_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"
    const val Int MSG_LICENSING_ALLOW = 2131755033
    const val Int MSG_LICENSING_APP_ERROR = 2131755034
    const val Int MSG_LICENSING_DONT_ALLOW = 2131755035
    const val String VOICE_PLUGIN_URL = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"

    public LicenseChecker(Context context, Handler handler, Object obj) {
        handler.obtainMessage(R.id.msg_licensing_allow, obj).sendToTarget()
    }
}
