package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage

class VoiceEnableMic : VoicePluginMessage {
    val enableMic: Boolean

    constructor(bundle: Bundle) {
        enableMic = bundle.getBoolean("enableMic")
    }

    constructor(enableMic: Boolean) {
        this.enableMic = enableMic
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putBoolean("enableMic", enableMic)
        }
    }
}