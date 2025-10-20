/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages

import android.os.Bundle
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull

class VoiceTerminateCall
: VoicePluginMessage {
    @Nonnull
    VoiceChannelInfo channelInfo

    VoiceTerminateCall(Bundle bundle) {
        this.channelInfo = VoiceChannelInfo(bundle.getBundle("channelInfo"))
    }

    VoiceTerminateCall(@Nonnull VoiceChannelInfo voiceChannelInfo) {
        this.channelInfo = voiceChannelInfo
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("channelInfo", this.channelInfo.toBundle())
        return bundle
    }
}

