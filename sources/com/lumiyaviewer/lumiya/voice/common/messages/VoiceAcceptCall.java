package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nullable;

public class VoiceAcceptCall implements VoicePluginMessage {
    @Nullable
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceAcceptCall(Bundle bundle) {
        this.sessionHandle = bundle.getString("sessionHandle");
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
    }

    public VoiceAcceptCall(@Nullable String str, VoiceChannelInfo voiceChannelInfo2) {
        this.sessionHandle = str;
        this.voiceChannelInfo = voiceChannelInfo2;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", this.sessionHandle);
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        return bundle;
    }
}
