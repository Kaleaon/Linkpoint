package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import com.linkpoint.voice.common.model.VoiceChatInfo
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceChannelStatus : VoicePluginMessage {
    val VoiceChannelInfo channelInfo
    val VoiceChatInfo chatInfo
    val String errorMessage

    public VoiceChannelStatus(Bundle bundle) {
        this.channelInfo = VoiceChannelInfo(bundle.getBundle("channelInfo"))
        this.chatInfo = VoiceChatInfo.create(bundle.getBundle("chatInfo"))
        this.errorMessage = bundle.getString("errorMessage")
    }

    public VoiceChannelStatus(VoiceChannelInfo voiceChannelInfo, VoiceChatInfo voiceChatInfo, String string2) {
        this.channelInfo = voiceChannelInfo
        this.chatInfo = voiceChatInfo
        this.errorMessage = string2
    }

    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("channelInfo", this.channelInfo.toBundle())
        bundle.putBundle("chatInfo", this.chatInfo.toBundle())
        bundle.putString("errorMessage", this.errorMessage)
        return bundle
    }
}

