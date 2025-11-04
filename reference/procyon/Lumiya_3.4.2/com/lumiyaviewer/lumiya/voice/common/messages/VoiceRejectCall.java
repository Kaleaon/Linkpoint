// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceRejectCall implements VoicePluginMessage
{
    @Nonnull
    public final String sessionHandle;
    public final VoiceChannelInfo voiceChannelInfo;
    
    public VoiceRejectCall(final Bundle bundle) {
        this.sessionHandle = bundle.getString("sessionHandle");
        this.voiceChannelInfo = new VoiceChannelInfo(bundle.getBundle("voiceChannelInfo"));
    }
    
    public VoiceRejectCall(@Nonnull final String sessionHandle, final VoiceChannelInfo voiceChannelInfo) {
        this.sessionHandle = sessionHandle;
        this.voiceChannelInfo = voiceChannelInfo;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putString("sessionHandle", this.sessionHandle);
        bundle.putBundle("voiceChannelInfo", this.voiceChannelInfo.toBundle());
        return bundle;
    }
}
