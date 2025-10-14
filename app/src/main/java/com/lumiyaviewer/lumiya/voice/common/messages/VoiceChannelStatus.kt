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
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo
import javax.annotation.Nonnull
import javax.annotation.Nullable

class VoiceChannelStatus
: VoicePluginMessage {
    @Nonnull
    VoiceChannelInfo channelInfo
    @Nonnull
    VoiceChatInfo chatInfo
    @Nullable
    String errorMessage

    VoiceChannelStatus(Bundle bundle) {
        this.channelInfo = VoiceChannelInfo(bundle.getBundle("channelInfo"))
        this.chatInfo = VoiceChatInfo.create(bundle.getBundle("chatInfo"))
        this.errorMessage = bundle.getString("errorMessage")
    }

    VoiceChannelStatus(@Nonnull VoiceChannelInfo voiceChannelInfo, @Nonnull VoiceChatInfo voiceChatInfo, @Nullable String string2) {
        this.channelInfo = voiceChannelInfo
        this.chatInfo = voiceChatInfo
        this.errorMessage = string2
    }

    @Override
    Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putBundle("channelInfo", this.channelInfo.toBundle())
        bundle.putBundle("chatInfo", this.chatInfo.toBundle())
        bundle.putString("errorMessage", this.errorMessage)
        return bundle
    }
}

