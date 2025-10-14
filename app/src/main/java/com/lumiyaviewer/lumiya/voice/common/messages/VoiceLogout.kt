package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage

class VoiceLogout : VoicePluginMessage {
    override fun toBundle(): Bundle {
        return Bundle()
    }
}