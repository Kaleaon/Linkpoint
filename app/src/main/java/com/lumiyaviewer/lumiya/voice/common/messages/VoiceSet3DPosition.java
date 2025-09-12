package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nonnull;

public class VoiceSet3DPosition implements VoicePluginMessage {
    @Nonnull
    public final Voice3DPosition listenerPosition;
    @Nonnull
    public final Voice3DPosition speakerPosition;
    @Nonnull
    public final VoiceChannelInfo voiceChannelInfo;

    public VoiceSet3DPosition(Bundle bundle) {
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        this.speakerPosition = new Voice3DPosition(bundle.getBundle("speakerPosition"));
        this.listenerPosition = new Voice3DPosition(bundle.getBundle("listenerPosition"));
    }

    public VoiceSet3DPosition(@Nonnull VoiceChannelInfo voiceChannelInfo2, @Nonnull Voice3DPosition voice3DPosition, @Nonnull Voice3DPosition voice3DPosition2) {
        this.voiceChannelInfo = voiceChannelInfo2;
        this.speakerPosition = voice3DPosition;
        this.listenerPosition = voice3DPosition2;
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        bundle.putBundle("speakerPosition", this.speakerPosition.toBundle());
        bundle.putBundle("listenerPosition", this.listenerPosition.toBundle());
        return bundle;
    }
}
