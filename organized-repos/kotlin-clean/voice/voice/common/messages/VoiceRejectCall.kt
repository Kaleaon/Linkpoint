package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull

class VoiceRejectCall(
    val sessionHandle: String?,
    val voiceChannelInfo: VoiceChannelInfo
) : VoicePluginMessage {
    
    constructor(bundle: Bundle) : this(
        bundle.getString("sessionHandle"),
        VoiceChannelInfo(bundle.getBundle("voiceChannelInfo")!!)
    )

    override fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("sessionHandle", sessionHandle)
        bundle.putBundle("voiceChannelInfo", voiceChannelInfo.toBundle())
        return bundle
    }
}