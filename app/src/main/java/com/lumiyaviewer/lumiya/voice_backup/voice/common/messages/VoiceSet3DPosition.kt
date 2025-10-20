package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull

class VoiceSet3DPosition : VoicePluginMessage {
    val Voice3DPosition listenerPosition
    val Voice3DPosition speakerPosition
    val VoiceChannelInfo voiceChannelInfo

    public VoiceSet3DPosition(Bundle bundle) {
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
        this.speakerPosition = Voice3DPosition(bundle.getBundle("speakerPosition"))
        this.listenerPosition = Voice3DPosition(bundle.getBundle("listenerPosition"))
    }

    public VoiceSet3DPosition(VoiceChannelInfo voiceChannelInfo, Voice3DPosition voice3DPosition, Voice3DPosition voice3DPosition2) {
        this.voiceChannelInfo = voiceChannelInfo
        this.speakerPosition = voice3DPosition
        this.listenerPosition = voice3DPosition2
    }

    override Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        bundle.putBundle("speakerPosition", this.speakerPosition.toBundle())
        bundle.putBundle("listenerPosition", this.listenerPosition.toBundle())
        return bundle
    }
}

