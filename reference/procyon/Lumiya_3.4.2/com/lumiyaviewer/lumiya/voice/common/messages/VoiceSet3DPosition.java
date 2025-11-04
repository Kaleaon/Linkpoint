// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.model.Voice3DPosition;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceSet3DPosition implements VoicePluginMessage
{
    @Nonnull
    public final Voice3DPosition listenerPosition;
    @Nonnull
    public final Voice3DPosition speakerPosition;
    @Nonnull
    public final VoiceChannelInfo voiceChannelInfo;
    
    public VoiceSet3DPosition(final Bundle bundle) {
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        this.speakerPosition = new Voice3DPosition(bundle.getBundle("speakerPosition"));
        this.listenerPosition = new Voice3DPosition(bundle.getBundle("listenerPosition"));
    }
    
    public VoiceSet3DPosition(@Nonnull final VoiceChannelInfo voiceChannelInfo, @Nonnull final Voice3DPosition speakerPosition, @Nonnull final Voice3DPosition listenerPosition) {
        this.voiceChannelInfo = voiceChannelInfo;
        this.speakerPosition = speakerPosition;
        this.listenerPosition = listenerPosition;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        bundle.putBundle("speakerPosition", this.speakerPosition.toBundle());
        bundle.putBundle("listenerPosition", this.listenerPosition.toBundle());
        return bundle;
    }
}
