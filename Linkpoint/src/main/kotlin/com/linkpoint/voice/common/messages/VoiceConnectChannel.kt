package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceConnectChannel(
    val voiceChannelInfo: VoiceChannelInfo,
    val channelCredentials: String?
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        VoiceChannelInfo(bundle.getBundle("voiceChannelInfo")!!),
        bundle.getString("channelCredentials")
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle())
        bundle.putString("channelCredentials", channelCredentials)
        return bundle
    }
}