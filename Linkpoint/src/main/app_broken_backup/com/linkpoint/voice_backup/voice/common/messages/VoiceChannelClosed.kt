/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.linkpoint.voice.common.messages

import android.os.Bundle
import com.linkpoint.voice.common.VoicePluginMessage
import com.linkpoint.voice.common.model.VoiceChannelInfo
import javax.annotation.Nonnull

class VoiceChannelClosed
: VoicePluginMessage {
    @Nonnull
    VoiceChannelInfo channelInfo

    VoiceChannelClosed(Bundle bundle) {
        this.channelInfo = VoiceChannelInfo(bundle.getBundle("channelInfo"))
    }

    VoiceChannelClosed(@Nonnull VoiceChannelInfo voiceChannelInfo) {
        this.channelInfo = voiceChannelInfo
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("channelInfo", this.channelInfo.toBundle())
        return bundle
    }
}

