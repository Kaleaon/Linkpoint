/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 */
package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceChannelStatus
implements VoicePluginMessage {
    @Nonnull
    public final VoiceChannelInfo channelInfo;
    @Nonnull
    public final VoiceChatInfo chatInfo;
    @Nullable
    public final String errorMessage;

    public VoiceChannelStatus(Bundle bundle) {
        this.channelInfo = new VoiceChannelInfo(bundle.getBundle("channelInfo"));
        this.chatInfo = VoiceChatInfo.create(bundle.getBundle("chatInfo"));
        this.errorMessage = bundle.getString("errorMessage");
    }

    public VoiceChannelStatus(@Nonnull VoiceChannelInfo voiceChannelInfo, @Nonnull VoiceChatInfo voiceChatInfo, @Nullable String string2) {
        this.channelInfo = voiceChannelInfo;
        this.chatInfo = voiceChatInfo;
        this.errorMessage = string2;
    }

    @Override
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("channelInfo", this.channelInfo.toBundle());
        bundle.putBundle("chatInfo", this.chatInfo.toBundle());
        bundle.putString("errorMessage", this.errorMessage);
        return bundle;
    }
}

