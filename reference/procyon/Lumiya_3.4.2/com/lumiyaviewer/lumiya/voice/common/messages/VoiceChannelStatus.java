// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChannelInfo;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceChannelStatus implements VoicePluginMessage
{
    @Nonnull
    public final VoiceChannelInfo channelInfo;
    @Nonnull
    public final VoiceChatInfo chatInfo;
    @Nullable
    public final String errorMessage;
    
    public VoiceChannelStatus(final Bundle bundle) {
        this.channelInfo = new VoiceChannelInfo(bundle.getBundle("channelInfo"));
        this.chatInfo = VoiceChatInfo.create(bundle.getBundle("chatInfo"));
        this.errorMessage = bundle.getString("errorMessage");
    }
    
    public VoiceChannelStatus(@Nonnull final VoiceChannelInfo channelInfo, @Nonnull final VoiceChatInfo chatInfo, @Nullable final String errorMessage) {
        this.channelInfo = channelInfo;
        this.chatInfo = chatInfo;
        this.errorMessage = errorMessage;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBundle("channelInfo", this.channelInfo.toBundle());
        bundle.putBundle("chatInfo", this.chatInfo.toBundle());
        bundle.putString("errorMessage", this.errorMessage);
        return bundle;
    }
}
