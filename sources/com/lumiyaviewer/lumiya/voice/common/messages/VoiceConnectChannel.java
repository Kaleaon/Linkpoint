package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VoiceConnectChannel implements VoicePluginMessage {
    @Nullable
    public final String channelCredentials;
    @Nonnull
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceConnectChannel(Bundle bundle) {
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        this.channelCredentials = bundle.getString("channelCredentials");
    }

    public VoiceConnectChannel(@Nonnull VoiceChannelInfo voiceChannelInfo2, @Nullable String str) {
        this.voiceChannelInfo = voiceChannelInfo2;
        this.channelCredentials = str;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        bundle.putString("channelCredentials", this.channelCredentials);
        return bundle;
    }
}
