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
import javax.annotation.Nullable

class VoiceAcceptCall
: VoicePluginMessage {
    @Nullable
    String sessionHandle
    VoiceChannelInfo voiceChannelInfo

    VoiceAcceptCall(Bundle bundle) {
        this.sessionHandle = bundle.getString("sessionHandle")
        this.voiceChannelInfo = VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"))
    }

    VoiceAcceptCall(@Nullable String string2, VoiceChannelInfo voiceChannelInfo) {
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

