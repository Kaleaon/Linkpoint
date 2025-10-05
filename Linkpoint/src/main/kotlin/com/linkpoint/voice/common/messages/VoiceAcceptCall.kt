package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import javax.annotation.Nullable

class VoiceAcceptCall : VoicePluginMessage {
    val String sessionHandle
    val VoiceChannelInfo voiceChannelInfo

    public VoiceAcceptCall(Bundle bundle) {
        this.sessionHandle = bundle.getString("sessionHandle")
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
    }

    public VoiceAcceptCall(String string2, VoiceChannelInfo voiceChannelInfo) {
        this.sessionHandle = string2
        this.voiceChannelInfo = voiceChannelInfo
    }

    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putString("sessionHandle", this.sessionHandle)
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        return bundle
    }
}

