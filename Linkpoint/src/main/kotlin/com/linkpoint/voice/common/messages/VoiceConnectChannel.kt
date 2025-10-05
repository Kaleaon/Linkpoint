package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceConnectChannel : VoicePluginMessage {
    val String channelCredentials
    val VoiceChannelInfo voiceChannelInfo

    public VoiceConnectChannel(Bundle bundle) {
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
        this.channelCredentials = bundle.getString("channelCredentials")
    }

    public VoiceConnectChannel(VoiceChannelInfo voiceChannelInfo, String string2) {
        this.voiceChannelInfo = voiceChannelInfo
        this.channelCredentials = string2
    }

    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        bundle.putString("channelCredentials", this.channelCredentials)
        return bundle
    }
}

