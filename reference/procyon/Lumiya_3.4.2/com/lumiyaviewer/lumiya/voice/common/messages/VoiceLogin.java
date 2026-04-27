// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.voice.common.messages;

import android.os.Bundle;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceLoginInfo;
import com.lumiyaviewer.lumiya.voice.common.VoicePluginMessage;

public class VoiceLogin implements VoicePluginMessage
{
    @Nonnull
    public final VoiceLoginInfo voiceLoginInfo;
    
    public VoiceLogin(final Bundle bundle) {
        this.voiceLoginInfo = new VoiceLoginInfo(bundle.getBundle("voiceLoginInfo"));
    }
    
    public VoiceLogin(@Nonnull final VoiceLoginInfo voiceLoginInfo) {
        this.voiceLoginInfo = voiceLoginInfo;
    }
    
    @Override
    public Bundle toBundle() {
        final Bundle bundle = new Bundle();
        bundle.putBundle("voiceLoginInfo", this.voiceLoginInfo.toBundle());
        return bundle;
    }
}
