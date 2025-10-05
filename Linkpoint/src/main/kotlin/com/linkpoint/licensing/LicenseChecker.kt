package com.linkpoint.licensing

import android.content.Context
import android.os.Handler
import com.linkpoint.R

class LicenseChecker {
    const val APP_STORE_NAME: String = "Google Play"
    const val APP_STORE_URL: String = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya"
    const val CLOUD_PLUGIN_URL: String = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.cloud"
    const val MSG_LICENSING_ALLOW: Int = 2131755033
    const val MSG_LICENSING_APP_ERROR: Int = 2131755034
    const val MSG_LICENSING_DONT_ALLOW: Int = 2131755035
    const val VOICE_PLUGIN_URL: String = "https://play.google.com/store/apps/details?id=com.lumiyaviewer.lumiya.voice"

    public LicenseChecker(Context context, Handler handler, Object obj) {
        handler.obtainMessage(R.id.msg_licensing_allow, obj).sendToTarget()
    }
}
