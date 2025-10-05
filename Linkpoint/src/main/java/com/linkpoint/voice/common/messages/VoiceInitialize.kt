package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage

class VoiceInitialize : VoicePluginMessage {
    val appVersionCode: Int

    constructor(appVersionCode: Int) {
        this.appVersionCode = appVersionCode
    }

    constructor(bundle: Bundle) {
        appVersionCode = bundle.getInt("appVersionCode")
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putInt("appVersionCode", appVersionCode)
        }
    }
}