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

class VoiceRejectCall
: VoicePluginMessage {
    @Nonnull
    String sessionHandle
    VoiceChannelInfo voiceChannelInfo

    VoiceRejectCall(Bundle bundle) {
        this.sessionHandle = bundle.getString("sessionHandle")
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
    }

    VoiceRejectCall(@Nonnull String string2, VoiceChannelInfo voiceChannelInfo) {
        this.sessionHandle = string2
        this.voiceChannelInfo = voiceChannelInfo
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putString("sessionHandle", this.sessionHandle)
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle())
        return bundle
    }
}

