// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceConnectChannel implements VoicePluginMessage
{
    @Nullable
    public final String channelCredentials;
    @Nonnull
    public final VoiceChannelInfo voiceChannelInfo;
    
    public VoiceConnectChannel(final Bundle bundle) {
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
        this.channelCredentials = bundle.getString("channelCredentials");
    }
    
    public VoiceConnectChannel(@Nonnull final VoiceChannelInfo voiceChannelInfo, @Nullable final String channelCredentials) {
        this.voiceChannelInfo = voiceChannelInfo;
        this.channelCredentials = channelCredentials;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        bundle.putString("channelCredentials", this.channelCredentials);
        return bundle;
    }
}
