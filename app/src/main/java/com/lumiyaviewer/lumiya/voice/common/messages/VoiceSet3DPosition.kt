/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull

class VoiceSet3DPosition
: VoicePluginMessage {
    @Nonnull
    Voice3DPosition listenerPosition
    @Nonnull
    Voice3DPosition speakerPosition
    @Nonnull
    VoiceChannelInfo voiceChannelInfo

    VoiceSet3DPosition(Bundle bundle) {
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
        this.speakerPosition = Voice3DPosition(bundle.getBundle("speakerPosition"))
        this.listenerPosition = Voice3DPosition(bundle.getBundle("listenerPosition"))
    }

    VoiceSet3DPosition(@Nonnull VoiceChannelInfo voiceChannelInfo, @Nonnull Voice3DPosition voice3DPosition, @Nonnull Voice3DPosition voice3DPosition2) {
        this.voiceChannelInfo = voiceChannelInfo
        this.speakerPosition = voice3DPosition
        this.listenerPosition = voice3DPosition2
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        bundle.putBundle("speakerPosition", this.speakerPosition.toBundle())
        bundle.putBundle("listenerPosition", this.listenerPosition.toBundle())
        return bundle
    }
}

