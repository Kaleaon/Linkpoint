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
import javax.annotation.Nullable

class VoiceConnectChannel
: VoicePluginMessage {
    @Nullable
    String channelCredentials
    @Nonnull
    VoiceChannelInfo voiceChannelInfo

    VoiceConnectChannel(Bundle bundle) {
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
        this.channelCredentials = bundle.getString("channelCredentials")
    }

    VoiceConnectChannel(@Nonnull VoiceChannelInfo voiceChannelInfo, @Nullable String string2) {
        this.voiceChannelInfo = voiceChannelInfo
        this.channelCredentials = string2
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        bundle.putString("channelCredentials", this.channelCredentials)
        return bundle
    }
}

