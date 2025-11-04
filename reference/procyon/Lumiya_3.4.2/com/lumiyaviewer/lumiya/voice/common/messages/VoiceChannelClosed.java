// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceChannelClosed implements VoicePluginMessage
{
    @Nonnull
    public final VoiceChannelInfo channelInfo;
    
    public VoiceChannelClosed(final Bundle bundle) {
        this.channelInfo = new VoiceChannelInfo(bundle.getBundle("channelInfo"));
    }
    
    public VoiceChannelClosed(@Nonnull final VoiceChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBundle("channelInfo", this.channelInfo.toBundle());
        return bundle;
    }
}
