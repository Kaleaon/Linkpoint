package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo

class VoiceChannelClosed : VoicePluginMessage {
    val channelInfo: VoiceChannelInfo

    constructor(bundle: Bundle) {
        channelInfo = VoiceChannelInfo(bundle.getBundle("channelInfo")!!)
    }

    constructor(channelInfo: VoiceChannelInfo) {
        this.channelInfo = channelInfo
    }

    override fun toBundle(): Bundle {
        return Bundle().apply {
            putBundle("channelInfo", channelInfo.toBundle())
        }
    }
}