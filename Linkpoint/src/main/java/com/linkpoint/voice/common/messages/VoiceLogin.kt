package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceLoginInfo

class VoiceLogin : VoicePluginMessage {
    val voiceLoginInfo: VoiceLoginInfo

    constructor(bundle: Bundle) {
        voiceLoginInfo = VoiceLoginInfo(bundle.getBundle("voiceLoginInfo")!!)
    }

    constructor(voiceLoginInfo: VoiceLoginInfo) {
        this.voiceLoginInfo = voiceLoginInfo
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putBundle("voiceLoginInfo", voiceLoginInfo.toBundle())
        }
    }
}