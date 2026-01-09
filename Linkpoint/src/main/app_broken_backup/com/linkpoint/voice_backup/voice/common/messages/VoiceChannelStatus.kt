package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import com.linkpoint.voice.common.model.VoiceChatInfo
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceChannelStatus(
    val channelInfo: VoiceChannelInfo,
    val chatInfo: VoiceChatInfo,
    val errorMessage: String?
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        VoiceChannelInfo(bundle.getBundle("channelInfo")!!),
        VoiceChatInfo.create(bundle.getBundle("chatInfo")!!),
        bundle.getString("errorMessage")
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putBundle("channelInfo", channelInfo.toBundle())
        bundle.putBundle("chatInfo", chatInfo.toBundle())
        bundle.putString("errorMessage", errorMessage)
        return bundle
    }
}